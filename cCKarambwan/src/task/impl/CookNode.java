package task.impl;

import com.google.crypto.tink.proto.Keyset;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.input.Keyboard;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.GameTickListener;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.core.Instance;
import task.AbstractTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * @author camalCase
 * @version 1
 * created 22 aug 2021
 * last modified: 22 aug 2021
 * desc: main.
 */

public class CookNode extends AbstractTask implements GameTickListener {
    public char cookKey = '2';
    // pos (5369, 5502, -2508) pitch: 283 yaw: 763 zoom: 677

    @Override
    public boolean accept() {
        return !config.isGotoCook() &&
                !config.isReup() &&
                Inventory.contains("Raw karambwan");
    }

    @Override
    public int execute() {
        config.setStatus("cooking");

        if (Inventory.contains("Raw karambwan")) {
            if (Bank.close()) { // 23 aug got stuck with an open bank window so i added this
                //Keyboard.holdKey(cookKey, () -> !Inventory.contains("Raw karambwan"), 10000 );
                Keyboard.holdSpace(() -> !Inventory.contains("Raw karambwan"), 10000);
                GameObject fire = GameObjects.closest("Fire");
                try {
                    Inventory.getItemInSlot(27).useOn(fire);
                } catch (NullPointerException e) {
                    log("not enough karambwans, going shopping");
                    config.setReup(true);
                    return 0;
                }
                sleep(Calculations.random(280, 320));
            }
        }


        return 0;

    }

    @Override
    public void onGameTick() {
        log("tick");
    }
}
