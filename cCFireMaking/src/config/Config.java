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
    private boolean fireMake = false;
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

    public boolean isFireMake() {
        return fireMake;
    }

    public void setFireMake(boolean fireMake) {
        this.fireMake = fireMake;
    }
}
