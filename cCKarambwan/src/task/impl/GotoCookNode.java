package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import task.AbstractTask;

/**
 * @author camalCase
 * @version 1
 * created 24 aug 2021
 * last modified: 26 aug 2021
 * desc: teleports to burthorpe and walks to north side of banker west side of fire.
 */
public class GotoCookNode extends AbstractTask {
    private final Area GE = new Area(3172, 3473, 3158, 3496);
    private final Tile COOK_SPOT = new Tile(3042, 4973, 1);
    private final Area COOK_AREA = new Area(3042, 4973,3041, 4972, 1);

    @Override
    public boolean accept() {
        return config.isGotoCook();
    }

    @Override
    public int execute() {
        if (Inventory.contains("Raw karambwan")) {
            if (Bank.openClosest()) {
                Bank.depositAllItems();
                return 0;
            }
            return 0;
        }
        config.setStatus("going to cook");
        if (Bank.isOpen()) {
            Bank.depositAllItems();
            Bank.close();
        }
        if (GE.contains(getLocalPlayer())) {
            Equipment.open();
            sleep(Calculations.random(config.getSleepTimeLow(), config.getSleepTimeHigh()));
            Equipment.getItemInSlot(EquipmentSlot.AMULET).interact("Burthorpe");
            sleep(Calculations.random(4000, 6000));
        }
        if (getLocalPlayer().getTile() != COOK_SPOT) {
            if (Walking.shouldWalk()) {
                Walking.walk(COOK_SPOT);
            }
        }
        if (COOK_AREA.contains(getLocalPlayer())) { // added this because it would pause once it reached COOK_SPOT tile
            config.setGotoCook(false);
            log("starting cooking!");
            return 0;
        }

        return 0;
    }
}
