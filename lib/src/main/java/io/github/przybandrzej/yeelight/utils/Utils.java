package io.github.przybandrzej.yeelight.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Yeelight utility class
 */
public class Utils {

  private Utils() {
  }

  /**
   * Clamp value in [min, max] interval
   *
   * @param value Value to clamp
   * @param min   Min value
   * @param max   Max value
   * @return Clamped value
   */
  public static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }

  /**
   * Clamp parameters 'r', 'g' and 'b' and then compute rgbValue
   *
   * @param r Red value
   * @param g Green value
   * @param b Blue value
   * @return RGB value
   */
  public static int clampAndComputeRGBValue(int r, int g, int b) {
    r = clamp(r, 0, 255);
    g = clamp(g, 0, 255);
    b = clamp(b, 0, 255);
    return r * 65536 + g * 256 + b;
  }

  public static String decodeName(String encoded) {
    return new String(Base64.getDecoder().decode(encoded.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
  }

  public static String encodeName(String name) {
    return new String(Base64.getEncoder().encode(name.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
  }

  public static String[] PROPERTIES = {"power", "bright", "ct", "rgb", "hue", "sat", "color_mode", "flowing",
      "delayoff", "flow_params", "music_on", "name", "bg_power", "bg_flowing", "bg_flow_params", "bg_ct", "bg_lmode",
      "bg_bright", "bg_rgb", "bg_hue", "bg_sat", "nl_br", "active_mode"};

  public static String[] METHODS = {"get_prop", "set_ct_abx", "set_rgb", "set_hsv", "set_bright", "set_power", "toggle",
      "set_default", "start_cf", "stop_cf", "set_scene", "cron_add", "cron_get", "cron_del", "set_adjust", "set_music",
      "set_name", "bg_set_rgb", "bg_set_hsv", "bg_set_ct_abx", "bg_start_cf", "bg_stop_cf", "bg_set_scene",
      "bg_set_default", "bg_set_power", "bg_set_bright", "bg_set_adjust", "bg_toggle", "dev_toggle", "adjust_bright",
      "adjust_ct", "adjust_color", "bg_adjust_bright", "bg_adjust_ct", "bg_adjust_color"};
}
