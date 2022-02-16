package task;

import config.Config;
import org.dreambot.api.script.TaskNode;


/**
 * @author camalCase
 * @version 1
 * created 20 aug 2021
 * last modified: 20 aug 2021
 * desc: extends TaskNode to include config object so i dont have to get it in every task.
 */

public abstract class AbstractTask extends TaskNode {
    // Gets an instance of Config object to use in TaskNode
    protected Config config = Config.getConfig();


    // Checks whether the task should run.
    public abstract boolean accept();


    // Executes the task if accept() returns true.
    public abstract int execute();
}
