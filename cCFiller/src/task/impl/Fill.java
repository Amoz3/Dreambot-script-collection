package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import task.AbstractTask;

/**
 * @author camalCase
 * @version 1
 * 25th aug refactor
 * desc: uses empty container on falador water jug.
 */

public class Fill extends AbstractTask {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean accept() {
        return config.isScriptRunning() && Inventory.contains(config.getContainer());
    }

    @Override
    public int execute() {
        config.setStatus("Filling empty " + config.getContainer());
        Item emptyContainer = Inventory.get(config.getContainer());
        if (emptyContainer != null) {
            if (!Walking.isRunEnabled()) {
                Walking.toggleRun();
            }
            GameObject waterpump = GameObjects.closest("Waterpump");
            if (waterpump != null && emptyContainer.useOn(waterpump)) {
                sleepUntil(() -> !Inventory.contains(config.getContainer()), 35000);
            }
        }
        return Calculations.random(200, 600);
    }
}
