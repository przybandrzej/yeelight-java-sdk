package pl.przybysz.yeelight_sdk;

public class Device {

  /**
   * The ID of a Yeelight WiFi LED device, 3rd party device should use this value to
   * uniquely identified a Yeelight WiFi LED device.
   */
  private String id;

  /**
   *  The product model of a Yeelight smart device. Current it can be "mono",
   * "color", “stripe”, “ceiling”, “bslamp”. For "mono", it represents device that only supports
   * brightness adjustment. For "color", it represents device that support both color and color
   * temperature adjustment. “Stripe” stands for Yeelight smart LED stripe. “Ceiling” stands
   * for Yeelight Ceiling Light. More values may be added in future.
   */
  private Model model;

  /**
   * LED device's firmware version.
   */
  private int firmwareVersion;

  /**
   * All the supported control methods separated by white space. 3Rd party device
   * can use this field to dynamically render the control view to user if necessary. Any control
   * request that invokes method that is not included in this field will be rejected by smart LED.
   */
  private String[] support;

  /**
   * Current status of the device. "on" means the device is currently turned on, "off"
   * means it's turned off (not un-powered, just software-managed off).
   */
  private boolean power;

  /**
   * Current brightness, it's the percentage of maximum brightness. The range of
   * this value is 1 ~ 100.
   */
  private int brightness;

  /**
   * Current light mode. 1 means color mode, 2 means color temperature
   * mode, 3 means HSV mode.
   */
  private ColorMode colorMode;

  /**
   * Current color temperature value. The range of this value depends on product model,
   * refert to Yeelight product description. This field is only valid if COLOR_MODE is 2.
   */
  private int colorTemperature;

  /**
   * Current RGB value. The field is only valid if COLOR_MODE is 1. The value will be
   * explained in next section.
   */
  private int rgb;

  /**
   * Current hue value. The range of this value is 0 to 359. This field is only valid if
   * COLOR_MODE is 3.
   * NOTE: HUE and SAT should be used in combination. CT mode, RGB mode and HSV mode
   * are mutually exclusively.
   */
  private int hue;

  /**
   * Current saturation value. The range of this value is 0 to 100. The field is only valid if
   * COLOR_MODE is 3.
   * NOTE: HUE and SAT should be used in combination. CT mode, RGB mode and HSV mode
   * are mutually exclusively.
   */
  private int saturation;

  /**
   *  Name of the device. User can use “set_name” to store the name on the device.
   * The maximum length is 64 bytes. If none-ASCII character is used, it is suggested to
   * BASE64 the name first and then use “set_name” to store it on device.
   */
  private String name;

  /**
   * field contains the service access point of the smart LED deivce. The URI
   * scheme will always be "yeelight", host is the IP address of smart LED, port is control
   * service's TCP listen port.
   */
  private String location;

  public Device(String id, Model model, int firmwareVersion, String[] support, boolean power, int brightness, ColorMode colorMode, int colorTemperature, int rgb, int hue, int saturation, String name, String location) {
    this.id = id;
    this.model = model;
    this.firmwareVersion = firmwareVersion;
    this.support = support;
    this.power = power;
    this.brightness = brightness;
    this.colorMode = colorMode;
    this.colorTemperature = colorTemperature;
    this.rgb = rgb;
    this.hue = hue;
    this.saturation = saturation;
    this.name = name;
    this.location = location;
  }

  public Device() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Model getModel() {
    return model;
  }

  public void setModel(Model model) {
    this.model = model;
  }

  public int getFirmwareVersion() {
    return firmwareVersion;
  }

  public void setFirmwareVersion(int firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
  }

  public String[] getSupport() {
    return support;
  }

  public void setSupport(String[] support) {
    this.support = support;
  }

  public boolean isPower() {
    return power;
  }

  public void setPower(boolean power) {
    this.power = power;
  }

  public int getBrightness() {
    return brightness;
  }

  public void setBrightness(int brightness) {
    this.brightness = brightness;
  }

  public ColorMode getColorMode() {
    return colorMode;
  }

  public void setColorMode(ColorMode colorMode) {
    this.colorMode = colorMode;
  }

  public int getColorTemperature() {
    return colorTemperature;
  }

  public void setColorTemperature(int colorTemperature) {
    this.colorTemperature = colorTemperature;
  }

  public int getRgb() {
    return rgb;
  }

  public void setRgb(int rgb) {
    this.rgb = rgb;
  }

  public int getHue() {
    return hue;
  }

  public void setHue(int hue) {
    this.hue = hue;
  }

  public int getSaturation() {
    return saturation;
  }

  public void setSaturation(int saturation) {
    this.saturation = saturation;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
