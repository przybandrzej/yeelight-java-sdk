package io.github.przybandrzej.yeelight;

import java.util.Arrays;

public enum ColorMode {
  COLOR(1), TEMPERATURE(2), HSV(3);

  private int value;

  ColorMode(int val) {
    this.value = val;
  }

  public int getValue() {
    return this.value;
  }

  public static ColorMode valueOf(int value) {
    return Arrays.stream(ColorMode.values()).filter(it -> it.value == value).findFirst().orElseThrow(() -> new RuntimeException("No such value"));
  }
}
