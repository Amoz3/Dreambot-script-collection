package config;



/**
 * @author camalCase
 * @version 1
 * created 22 aug 2021
 * last modified: 22 aug 2021
 * desc: singleton class to hold global settings
 */
public class Config {
    private static final Config config = new Config();
    private String status = "starting script";
    private boolean reup = false;
    private boolean gotoCook = false;
    private int burntAmount = 0;
    private int cookedAmount = 0;
    private int burnPercent = 0;
    private int sleepTimeLow = 300;
    private int sleepTimeHigh = 620;

    // hide constructor
    private Config(){}

    public static Config getConfig() {
        return config;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCookedAmount() {
        return cookedAmount;
    }

    public void setCookedAmount(int cookedAmount) {
        this.cookedAmount = cookedAmount;
    }

    public int getBurntAmount() {
        return burntAmount;
    }

    public void setBurntAmount(int burntAmount) {
        this.burntAmount = burntAmount;
    }

    public int getBurnPercent() {
        return burnPercent;
    }

    public float calcBurnPercent() {
        float total = getBurntAmount() + getCookedAmount();
        float percent = getBurntAmount() / total;
        return percent * 100;
    }

    public boolean isReup() {
        return reup;
    }

    public void setReup(boolean reup) {
        this.reup = reup;
    }

    public int getSleepTimeHigh() {
        return sleepTimeHigh;
    }

    public int getSleepTimeLow() {
        return sleepTimeLow;
    }

    public boolean isGotoCook() {
        return gotoCook;
    }

    public void setGotoCook(boolean gotocook) {
        this.gotoCook = gotocook;
    }
}
