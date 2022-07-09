package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import task.AbstractTask;

/**
 * @author camalCase
 * @version 1
 * created 20 aug 2021
 * last modified: 20 aug 2021
 * desc: Cook raw food and then banks.
 */
public class Cook extends AbstractTask {

    private final Area RANGE = new Area(3080, 3496, 3078, 3494);

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean accept() {
        return Inventory.contains(config.getRawFood());
    }

    @Override
    public int execute() {
        config.setStatus("Cooking...");
        if (RANGE.contains(getLocalPlayer())) {
            GameObject stove = GameObjects.closest("Stove");
            if (ItemProcessing.isOpen()) {
                sleep(Calculations.random(config.getSleepLow(), config.getSleepHigh()));
                // when you have only raw chickens, it says make raw chickens but when you have a cooked one it says make cooked, this is important for when you get leveled up
                if (ItemProcessing.makeAll(config.getCookedFood())) { // when you have only raw chickens, it says make raw chickens
                    sleep(Calculations.random(config.getSleepLow(), config.getSleepHigh()));
                    sleepUntil(() -> !Inventory.contains(config.getRawFood()) || Dialogues.canContinue(), 30000);
                } else {
                    ItemProcessing.makeAll(config.getRawFood());
                    sleep(Calculations.random(config.getSleepLow(), config.getSleepHigh()));
                    sleepUntil(() -> !Inventory.contains(config.getRawFood()) || Dialogues.canContinue(), 30000);
                }
                return Calculations.random(config.getSleepLow(), config.getSleepHigh());
            }

            if (stove != null && stove.interact("Cook")) {
                sleepUntil(ItemProcessing::isOpen, 5000);
                return Calculations.random(config.getSleepLow(), config.getSleepHigh());
            }
        } else {
            if (Walking.shouldWalk()) {
                if (!Walking.isRunEnabled()) {
                    if (Walking.getRunEnergy() > Calculations.random(12, 25)) {
                        Walking.toggleRun();
                    }
                }
                Walking.walk(RANGE.getRandomTile());
            }
        }
        return 1000;
    }
}
