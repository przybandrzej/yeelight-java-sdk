package pl.przybysz.yeelight_sdk;

import pl.przybysz.yeelight_sdk.exception.ColorModeException;
import pl.przybysz.yeelight_sdk.exception.DeviceMonoOnlyException;
import pl.przybysz.yeelight_sdk.exception.OutOfRangeException;
import pl.przybysz.yeelight_sdk.utils.Utils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Each device can hold up to 4 simultaneous connections. For each connection, there is a command message quota,
 * that is 60 commands per minute. There is also a total quota for all the LAN commands: 144
 * commands per minute (4 × 60 × 60%).
 */
public class DeviceControl {

  private static final int BRIGHT_MIN = 1;
  private static final int BRIGHT_MAX = 100;
  private static final int HUE_MIN = 0;
  private static final int HUE_MAX = 359;
  private static final int SATURATION_MIN = 1;
  private static final int SATURATION_MAX = 100;
  private static final int NAME_MAX_BYES = 64;
  private static final int SOCKET_TIMEOUT = 5000;
  private static final long LISTEN_FOR_NOTIFICATIONS_RATE = 100;

  private final Device device;
  private Socket socket;
  private BufferedReader socketReader;
  private BufferedWriter socketWriter;
  private YeelightEffect effect = YeelightEffect.SUDDEN;
  private int duration = 0;
  private Runnable onNotification = () -> {
    System.out.println("Callback");
  };
  private Map<Integer, CommandResult> resultMap = new ConcurrentHashMap<>();
  private Thread listeningThread;
  private AtomicBoolean listeningThreadRun = new AtomicBoolean(false);

  public DeviceControl(Device device) throws IOException {
    this.device = device;
    initConnection();
  }

  public Device getDevice() {
    return device;
  }

  /**
   * Setter for Yeelight device effect
   *
   * @param effect Effect to set (if null, 'sudden' is chosen)
   */
  public void effect(YeelightEffect effect) {
    this.effect = effect == null ? YeelightEffect.SUDDEN : effect;
  }

  /**
   * Setter for Yeelight device effect duration
   *
   * @param duration Duration to set (&gt;= 0)
   */
  public void duration(int duration) {
    this.duration = Math.max(0, duration);
  }

  public void onDeviceStateChange(Runnable r) {
    this.onNotification = r;
  }

  public void close() throws IOException, InterruptedException {
    //this.scheduledExecutorService.shutdown();
    this.listeningThreadRun.set(false);
    System.out.println("Closing thread");
    //this.listeningThread.join();
    //System.out.println("Closed");
    this.listeningThread.interrupt();
    this.listeningThread.join();
    this.socket.close();
    this.socketReader.close();
    this.socketWriter.close();
  }

  public void setBrightness(int brightness) throws OutOfRangeException {
    if(!inRange(brightness, BRIGHT_MIN, BRIGHT_MAX)) {
      throw new OutOfRangeException();
    }

    device.setBrightness(brightness);
  }

  public void setColorMode(ColorMode mode) throws DeviceMonoOnlyException {
    if(device.getModel() == Model.MONO) {
      throw new DeviceMonoOnlyException();
    }

    device.setColorMode(mode);
  }

  public boolean setPower(boolean power) throws IOException {
    if(device.isPower() == power) {
      return false;
    }

    Command command = new Command("set_power", power ? "on" : "off");
    sendCommand(command);
    boolean set = awaitAnswer(command);
    if(set) {
      device.setPower(power);
    }
    return set;
  }

  public void setColorTemp(int value) throws ColorModeException {
    if(device.getColorMode() != ColorMode.TEMPERATURE) {
      throw new ColorModeException();
    }

    device.setColorTemperature(value);
  }

  public void setRgb(int rgb) throws ColorModeException {
    if(device.getColorMode() != ColorMode.COLOR) {
      throw new ColorModeException();
    }

    device.setRgb(rgb);
  }

  public void setHue(int hue) throws OutOfRangeException, ColorModeException {
    if(device.getColorMode() != ColorMode.HSV) {
      throw new ColorModeException();
    }
    if(!inRange(hue, HUE_MIN, HUE_MAX)) {
      throw new OutOfRangeException();
    }

    device.setHue(hue);
  }

  public void setSaturation(int sat) throws OutOfRangeException, ColorModeException {
    if(device.getColorMode() != ColorMode.HSV) {
      throw new ColorModeException();
    }
    if(!inRange(sat, SATURATION_MIN, SATURATION_MAX)) {
      throw new OutOfRangeException();
    }

    device.setSaturation(sat);
  }

  public void setHsv(int hue, int saturation) throws ColorModeException, OutOfRangeException {
    if(device.getColorMode() != ColorMode.HSV) {
      throw new ColorModeException();
    }
    if(!inRange(saturation, SATURATION_MIN, SATURATION_MAX)) {
      throw new OutOfRangeException();
    }
    if(!inRange(hue, HUE_MIN, HUE_MAX)) {
      throw new OutOfRangeException();
    }

    device.setHue(hue);
    device.setSaturation(saturation);
  }

  public boolean setName(String name) throws OutOfRangeException, IOException {
    System.out.println("Calling set name " + name);
    String encodedName = new String(Base64.getEncoder().encode(name.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    System.out.println("Encoded : " + encodedName);
    if(encodedName.getBytes().length > NAME_MAX_BYES) {
      throw new OutOfRangeException();
    }
    Command command = new Command("set_name", encodedName);
    sendCommand(command);
    boolean ok = awaitAnswer(command);
    if(ok) {
      device.setName(name);
    }
    return ok;
  }

  private boolean awaitAnswer(Command command) {
    do {
      CommandResult commandResult = resultMap.get(command.getId());
      if(commandResult != null) {
        resultMap.remove(command.getId());
        return commandResult.getOk();
      }
    } while(true);
  }

  public CommandResult getProperties() {
    return new CommandResult();
  }

  public void sendCommand(Command command) throws IOException {
    String jsonCommand = command.toJson() + "\r\n";
    send(jsonCommand);
  }

  private boolean inRange(int val, int min, int max) {
    return !(val < min || val > max);
  }

  private void initConnection() throws IOException {
    String[] location = device.getLocation().split(":");
    InetSocketAddress inetSocketAddress = new InetSocketAddress(location[1].substring(2), Integer.parseInt(location[2]));
    this.socket = new Socket();
    this.socket.connect(inetSocketAddress, SOCKET_TIMEOUT);
    this.socket.setSoTimeout(SOCKET_TIMEOUT);
    this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    this.socketWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
    this.listeningThreadRun.set(true);
    this.listeningThread = new Thread(() -> {
      while(this.listeningThreadRun.get()) {
        try {
          this.listen();
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
    this.listeningThread.start();
  }

  private void listen() throws IOException {
    if(!this.listeningThreadRun.get() || Thread.currentThread().isInterrupted() || !this.socketReader.ready()) {
      return;
    }
    String data = socketReader.readLine();
    if(data == null) {
      System.out.println("Stream empty");
      return;
    }
    System.out.println(data);
    Map<String, Object> result = Utils.GSON.fromJson(data, Utils.MAP_TYPE_TOKEN);
    Object idObj = result.get("id");
    if(idObj == null) {
      processNotification(result);
      System.out.println("Notification");
      this.onNotification.run();
      return;
    }
    int id = (int) Double.parseDouble(idObj.toString());
    CommandResult commandResult = new CommandResult();
    Object err = result.get("error");
    if(err != null) {
      CommandResult.Error error = new CommandResult.Error();
      error.setCode(Integer.parseInt(((Map<String, Object>) err).get("code").toString()));
      error.setMessage(((Map<String, Object>) err).get("message").toString());
      commandResult.setError(error);
      commandResult.setOk(false);
      resultMap.put(id, commandResult);
      return;
    }
    List<Object> params = (List<Object>) result.get("result");
    if(params.get(0) instanceof String && params.get(0).equals("ok")) {
      commandResult.setOk(true);
      resultMap.put(id, commandResult);
      return;
    }
    Map<String, Integer> cronRes = (Map<String, Integer>) params.get(0);
    Integer type = cronRes.get("type");
    if(type != null) {
      CommandResult.CronSettings cron = new CommandResult.CronSettings();
      cron.setType(type);
      cron.setDelay(cronRes.get("delay"));
      cron.setMix(cronRes.get("delay"));
      commandResult.setCronSettings(cron);
      commandResult.setOk(true);
      resultMap.put(id, commandResult);
      return;
    }
    commandResult.setOk(true);
    commandResult.setParams(params.toArray());
    resultMap.put(id, commandResult);
  }

  /*private CommandResult readUntilResult(Command command) throws IOException {
    do {
      CommandResult commandResult = resultMap.get(command.getId());
      if(commandResult != null) {
        return commandResult;
      }
      this.listen();
    } while(true);
  }*/

  private void send(String data) throws IOException {
    System.out.println("sending : " + data);
    this.socketWriter.write(data);
    this.socketWriter.flush();
  }

  private void processNotification(Map<String, Object> result) {
    String method = result.get("method").toString();
    if(!method.equals("props")) {
      // currently only "props" is supported
      return;
    }
    System.out.println(result);
    Map<String, String> props = (Map<String, String>) result.get("params");
    setProps(props);
  }

  private void setProps(Map<String, String> props) {
    for(Map.Entry<String, String> property : props.entrySet()) {
      String prop = property.getKey();
      if(prop.equals("name")) {
        device.setName(property.getValue());
      } else if(prop.equals("power")) {
        device.setPower(property.getValue().equals("on"));
      } else if(prop.equals("bright")) {
        device.setBrightness(Integer.parseInt(property.getValue()));
      } else if(prop.equals("ct")) {
        device.setColorTemperature(Integer.parseInt(property.getValue()));
      } else if(prop.equals("rgb")) {
        device.setRgb(Integer.parseInt(property.getValue()));
      } else if(prop.equals("hue")) {
        device.setHue(Integer.parseInt(property.getValue()));
      } else if(prop.equals("sat")) {
        device.setSaturation(Integer.parseInt(property.getValue()));
      } else if(prop.equals("color_mode")) {
        device.setColorMode(ColorMode.valueOf(Integer.parseInt(property.getValue())));
      } else if(prop.equals("flowing")) {

      } else if(prop.equals("delayoff")) {

      } else if(prop.equals("flow_params")) {

      } else if(prop.equals("music_on")) {

      }
      // todo add rest of the props
    }
  }
}
