package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.GameObject;
import task.AbstractTask;


/**
 * @author camalCase
 * @version 1
 * created 22 aug 2021
 * last modified: 22 aug 2021
 * desc: chops until full inv.
 */

public class ChopNode extends AbstractTask {
    private final Area TREES = new Area(3153, 3505, 3141, 3514);

    @Override
    public boolean accept() {
        return Inventory.contains("Tinderbox") && !config.isFireMake();
    }

    @Override
    public int execute() {
        config.setStatus("chopping");
        if (TREES.contains(getLocalPlayer())) {
            if (Inventory.isFull()) {
                config.setFireMake(true);
            }
            sleep(Calculations.random(340, 560));
            GameObject normalTree = GameObjects.closest(x -> x.getName().contains("Tree") && x.hasAction("Chop down") && TREES.contains(x));
            if (normalTree != null && normalTree.interact("Chop down")) {
                sleepUntil(() -> !getLocalPlayer().isMoving(), 3000);
                if (getLocalPlayer().isAnimating()) {
                    sleepUntil(() -> !getLocalPlayer().isAnimating(), 12000);
                }
            }
        } else {
            if (Walking.shouldWalk() && !getLocalPlayer().isMoving()){
                if (!Walking.isRunEnabled()) {
                    if (Walking.getRunEnergy() > Calculations.random(12, 25)) {
                        Walking.toggleRun();
                    }
                }
                Walking.walk(TREES.getRandomTile());
            }

        }
        return 0;
    }
}
