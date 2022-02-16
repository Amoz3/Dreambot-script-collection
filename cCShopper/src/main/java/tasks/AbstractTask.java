package tasks;

import config.Config;
import config.ShoppingListItem;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.TaskNode;

import java.util.ArrayList;

public abstract class AbstractTask extends TaskNode {

    // Obtain an instance of ScriptManger to use in tasks, will be used for stopping.
    protected ScriptManager manager = ScriptManager.getScriptManager();

    // Obtain an instance of the Config class to use in our tasks.

    protected Config config = Config.getConfig();


    protected ArrayList<ShoppingListItem> shoppingList = config.getShoppingList();
    protected ArrayList<ShoppingListItem> sellingList = config.getSellingList();

    protected final Area GE = new Area(3159, 3484, 3170, 3495);

    /**
     * Checks whether the task should run.
     */
    public abstract boolean accept();

    /**
     * Executes the task if accept() returns true.
     */
    public abstract int execute();
}