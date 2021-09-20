package pl.przybysz.yeelight_sdk.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Yeelight utility class
 */
public class Utils {
  /**
   * Gson constant (for JSON reading/writing)
   */
  public static Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().create();

  public static Type MAP_TYPE_TOKEN = new TypeToken<Map<String, Object>>() {
  }.getType();

  /**
   * Utility class: can not be instanciated
   */
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

  public static String[] PROPERTIES = {"power", "bright", "ct", "rgb", "hue", "sat", "color_mode", "flowing",
      "delayoff", "flow_params", "music_on", "name", "bg_power", "bg_flowing", "bg_flow_params", "bg_ct", "bg_lmode",
      "bg_bright", "bg_rgb", "bg_hue", "bg_sat", "nl_br", "active_mode"};

  public static String[] METHODS = {"get_prop", "set_ct_abx", "set_rgb", "set_hsv", "set_bright", "set_power", "toggle",
      "set_default", "start_cf", "stop_cf", "set_scene", "cron_add", "cron_get", "cron_del", "set_adjust", "set_music",
      "set_name", "bg_set_rgb", "bg_set_hsv", "bg_set_ct_abx", "bg_start_cf", "bg_stop_cf", "bg_set_scene",
      "bg_set_default", "bg_set_power", "bg_set_bright", "bg_set_adjust", "bg_toggle", "dev_toggle", "adjust_bright",
      "adjust_ct", "adjust_color", "bg_adjust_bright", "bg_adjust_ct", "bg_adjust_color"};
}
