package task.impl;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.message.Message;
import task.AbstractTask;

/**
 * @author camalCase
 * @version 1
 * created 22 aug 2021
 * last modified: 22 aug 2021
 * desc: Sets fire to logs in inv, chatlistener in main moves the bot to ge when it cant set fire.
 */

public class FireNode extends AbstractTask {
    private final Area GE = new Area(3172, 3483, 3158, 3496);

    @Override
    public boolean accept() {
        return Inventory.contains("Tinderbox") && config.isFireMake();
    }

    @Override
    public int execute() {
        if (!Inventory.contains("Logs")) {
            config.setFireMake(false);
        }
        config.setStatus("firemaking");
        if (Inventory.contains("logs") && getLocalPlayer().isStandingStill()) {
            Inventory.get("Tinderbox").useOn("logs");
            sleepUntil(() -> getLocalPlayer().isMoving(), 20000);
        }
        return 0;
    }


}
