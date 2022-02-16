package config;

import org.dreambot.api.script.ScriptManager;

/**
 * @author camalCase
 * @version 1
 * created 20 aug 2021
 * last modified: 20 aug 2021
 * desc: singleton class to store global settings
 */

public class Config {
    private static final Config config = new Config();
    private boolean buyFood = false; // in bank task, when you run out of raw food it will set this bool to true and BuyRawFood task will run
    private String rawFood = "";
    private String cookedFood = ""; // strings for the names of the raw / cooked food for your level
    private String status = "starting script";
    private final int sleepLow = 100;
    private final int sleepHigh = 300; // these are ints for calculations.random() sleeps throughout the tasks they only have getters right now.
    ScriptManager manager = ScriptManager.getScriptManager();

    // buyFood bool setter
    public void setBuyFood(boolean buyFood) {
        this.buyFood = buyFood;
    }
    // buyFood getter
    public boolean isBuyFood() {
        return buyFood;
    }

    // hide constructor.
    private Config() {}

    public static Config getConfig() {
        return config;
    }

    public String getCookedFood() {
        return cookedFood;
    }

    public void setCookedFood(String cookedFood) {
        this.cookedFood = cookedFood;
    }

    public String getRawFood() {
        return rawFood;
    }

    public void setRawFood(String rawFood) {
        this.rawFood = rawFood;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSleepHigh() {
        return sleepHigh;
    }

    public int getSleepLow() {
        return sleepLow;
    }
}
