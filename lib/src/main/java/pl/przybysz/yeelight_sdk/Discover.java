package pl.przybysz.yeelight_sdk;

import pl.przybysz.yeelight_sdk.exception.BadResponseException;
import pl.przybysz.yeelight_sdk.exception.SocketClosedException;
import pl.przybysz.yeelight_sdk.exception.UnknownPacketException;

import java.io.IOException;
import java.net.*;

public class Discover {

  private static final int TIMEOUT = 5000;
  private static final int SEARCH_RESPONSE_LINES = 18;
  private static final String ADVERTISEMENT_ADDRESS = "239.255.255.250";
  private static final int ADVERTISEMENT_PORT = 1982;

  private static final String SEARCH_DEVICES_MESSAGE = "M-SEARCH * HTTP/1.1\r\n" +
      "HOST: 239.255.255.250:1982\r\n" +
      "MAN: \"ssdp:discover\"\r\n" +
      "ST: wifi_bulb";
  private static final String UDP_STATUS_OK_LINE = "HTTP/1.1 200 OK";

  private DatagramSocket udpSocket;
  private final String localAddr;
  private final int localPort;

  public Discover(String localAddr, int localPort) throws SocketException {
    this.localAddr = localAddr;
    this.localPort = localPort;
    this.udpSocket = new DatagramSocket(new InetSocketAddress(this.localAddr, this.localPort));
  }

  public void sendSearch() throws SocketClosedException, IOException {
    if(udpSocket.isClosed()) {
      throw new SocketClosedException();
    }
    byte[] buf = SEARCH_DEVICES_MESSAGE.getBytes();
    DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(ADVERTISEMENT_ADDRESS), ADVERTISEMENT_PORT);
    udpSocket.send(packet);
  }

  public Device receiveSearchPacket() throws SocketClosedException, IOException, UnknownPacketException, BadResponseException {
    return receiveSearchPacket(TIMEOUT);
  }

  public Device receiveSearchPacket(int timeout) throws SocketClosedException, IOException, UnknownPacketException, BadResponseException {
    if(udpSocket.isClosed()) {
      throw new SocketClosedException();
    }
    DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);
    udpSocket.setSoTimeout(timeout);
    udpSocket.receive(packet);
    String data = new String(
        packet.getData(), 0, packet.getLength());
    return mapSearchResponse(data);
  }

  public void initNewSearch() throws SocketException {
    udpSocket.close();
    udpSocket = new DatagramSocket(new InetSocketAddress(this.localAddr, this.localPort));
  }

  private Device mapSearchResponse(String response) throws UnknownPacketException, BadResponseException {
    String[] lines = response.split("\r\n");
    if(lines.length != SEARCH_RESPONSE_LINES) {
      throw new UnknownPacketException();
    }
    if(!lines[0].equals(UDP_STATUS_OK_LINE)) {
      throw new BadResponseException();
    }
    Device device = new Device();
    device.setId(getId(lines));
    device.setLocation(getLocation(lines));
    device.setBrightness(getBright(lines));
    device.setColorMode(getColorMode(lines));
    device.setColorTemperature(getCT(lines));
    device.setHue(getHue(lines));
    device.setModel(getModel(lines));
    device.setName(getName(lines));
    device.setPower(getPowerOn(lines));
    device.setRgb(getRgb(lines));
    device.setFirmwareVersion(getFwVer(lines));
    device.setSaturation(getSat(lines));
    device.setSupport(getSupport(lines));
    return device;
  }

  private String getId(String[] lines) {
    return lines[6].substring(4);
  }

  private String getLocation(String[] lines) {
    return lines[4].substring(10);
  }

  private Model getModel(String[] lines) {
    return Model.get(lines[7].substring(7));
  }

  private int getFwVer(String[] lines) {
    return Integer.parseInt(lines[8].substring(8));
  }

  private String[] getSupport(String[] lines) {
    return lines[9].substring(9).split(" ");
  }

  private boolean getPowerOn(String[] lines) {
    return lines[10].substring(7).equals("on");
  }

  private int getBright(String[] lines) {
    return Integer.parseInt(lines[11].substring(8));
  }

  private ColorMode getColorMode(String[] lines) {
    return ColorMode.valueOf(Integer.parseInt(lines[12].substring(12)));
  }

  private int getCT(String[] lines) {
    return Integer.parseInt(lines[13].substring(4));
  }

  private int getRgb(String[] lines) {
    return Integer.parseInt(lines[14].substring(5));
  }

  private int getHue(String[] lines) {
    return Integer.parseInt(lines[15].substring(5));
  }

  private int getSat(String[] lines) {
    return Integer.parseInt(lines[16].substring(5));
  }

  private String getName(String[] lines) {
    String line = lines[17];
    if(line.length() <= 6) {
      return "";
    }
    return lines[17].substring(6);
  }
}
