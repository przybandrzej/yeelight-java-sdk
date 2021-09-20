package pl.przybysz.yeelight_sdk;

import java.util.Arrays;

public enum Model {
  MONO("mono"), COLOR("colorb"), STRIPE("stripe"), CEILING("ceiling"), BSLAMP("bslamp");

  private String value;

  Model(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public static Model get(String name) {
    return Arrays.stream(Model.values()).filter(it -> it.value.equals(name)).findFirst().orElseThrow(() -> new RuntimeException("No such value"));
  }
}
