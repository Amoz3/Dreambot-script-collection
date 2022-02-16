package config;

import org.dreambot.api.script.ScriptManager;

import java.util.ArrayList;
import java.util.List;

// singleton for global vars
public class Config {
    private String paintNode = "Starting..."; // this was made for debugging but is now for script status
    private boolean scriptRunning;
    private ArrayList<String> splitBySemiColon;
    private ArrayList<ShoppingListItem> shoppingList = new ArrayList<ShoppingListItem>();
    private ArrayList<ShoppingListItem> sellingList = new ArrayList<ShoppingListItem>();

    private Config() {}
    private static final Config config = new Config();

    public static Config getConfig() {
        return config;
    }

    public boolean isScriptRunning() {
        return scriptRunning;
    }

    public void setScriptRunning(boolean scriptRunning) {
        this.scriptRunning = scriptRunning;
    }

    public List<String> getSplitBySemiColon() {
        return splitBySemiColon;
    }

    public void popSplitBySemiColon() {
        this.splitBySemiColon.remove(0);
    }

    public void setSplitBySemiColon(ArrayList<String> splitBySemiColon) {
        this.splitBySemiColon = splitBySemiColon;
    }

    public ArrayList<ShoppingListItem> getShoppingList() {
        return shoppingList;
    }

    public ArrayList<ShoppingListItem> getSellingList() {
        return sellingList;
    }

    public String getPaintNode() {
        return paintNode;
    }

    public void setPaintNode(String paintNode) {
        this.paintNode = paintNode;
    }
}
