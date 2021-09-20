package pl.przybysz.yeelight_sdk;

public class CommandResult {

  private boolean isOk = false;
  private Error error = null;
  private CronSettings cronSettings = null;
  private Object[] params = null;

  public Boolean getOk() {
    return isOk;
  }

  public void setOk(Boolean ok) {
    isOk = ok;
  }

  public Error getError() {
    return error;
  }

  public void setError(Error error) {
    this.error = error;
  }

  public CronSettings getCronSettings() {
    return cronSettings;
  }

  public void setCronSettings(CronSettings cronSettings) {
    this.cronSettings = cronSettings;
  }

  public Object[] getParams() {
    return params;
  }

  public void setParams(Object[] params) {
    this.params = params;
  }

  public static class CronSettings {
    private int type;
    private int delay;
    private int mix;

    public int getType() {
      return type;
    }

    public void setType(int type) {
      this.type = type;
    }

    public int getDelay() {
      return delay;
    }

    public void setDelay(int delay) {
      this.delay = delay;
    }

    public int getMix() {
      return mix;
    }

    public void setMix(int mix) {
      this.mix = mix;
    }
  }

  public static class Error {
    private int code;
    private String message;

    public int getCode() {
      return code;
    }

    public void setCode(int code) {
      this.code = code;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }
}
