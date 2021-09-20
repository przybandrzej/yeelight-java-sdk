package pl.przybysz.yeelight_sdk;

import com.google.gson.annotations.Expose;
import pl.przybysz.yeelight_sdk.utils.Utils;

/**
 * Represent a command sent to Yeelight device
 */
public class Command {
    /**
     * For unique ID generation
     */
    private static int ID_GENERATOR = 1;
    /**
     * Command ID
     */
    @Expose private int id;
    /**
     * Command method
     */
    @Expose private String method;
    /**
     * Command parameters
     */
    @Expose private Object[] params;

    /**
     * Generate ID for a command
     * @return An unique ID for a command
     */
    private static int generateId() {
        return ID_GENERATOR++;
    }

    /**
     * Command constructor (ID is auto-attributed)
     * @param method Associated method
     * @param params Associated parameters
     */
    public Command(String method, Object... params) {
        this.id = Command.generateId();
        this.method = method;
        this.params = params;
    }

    /**
     * Getter for ID
     * @return Command ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Convert this command to JSON string
     * @return JSON string representation of this command
     */
    public String toJson() {
        return Utils.GSON.toJson(this);
    }

    public String getMethod() {
        return method;
    }

    public Object[] getParams() {
        return params;
    }
}
