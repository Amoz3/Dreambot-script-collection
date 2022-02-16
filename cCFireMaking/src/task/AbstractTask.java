package task;

import config.Config;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.script.impl.TaskScript;

/**
 * @author camalCase
 * @version 1
 * created 22 aug 2021
 * last modified: 22 aug 2021
 * desc: extends tasknode to add an instance of config for tasks to use
 */


public abstract class AbstractTask extends TaskNode {
    // Gets an instance of Config object to use in TaskNode
    protected Config config = Config.getConfig();


    // Checks whether the task should run.
    public abstract boolean accept();


    // Executes the task if accept() returns true.
    public abstract int execute();
}
