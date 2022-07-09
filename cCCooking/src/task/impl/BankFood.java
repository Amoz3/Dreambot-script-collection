package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Player;
import task.AbstractTask;

/**
 * @author camalCase
 * @version 1
 * created 20 aug 2021
 * last modified: 20 aug 2021
 * desc: Banks cooked food.
 */
public class BankFood extends AbstractTask {
    private final Area EDGEBANK = new Area(3093, 3497, 3097, 3494);


    @Override
    public int priority() {
        return 2;
    }

    @Override
    public boolean accept() {
        // in this task rawFood is set to a name so it will always start here
        return !Inventory.contains(config.getRawFood());
    }

    @Override
    public int execute() {
        config.setStatus("Banking food...");
        // Check what food needs to be cooked
        if (Skills.getRealLevel(Skill.COOKING) < 45) {
            if (Skills.getRealLevel(Skill.COOKING) < 25) {
                config.setRawFood("Raw chicken");
                config.setCookedFood("Cooked chicken");
            } else {
                config.setRawFood("Raw salmon");
                config.setCookedFood("Salmon");
            }
        } else { // todo i commented these out because i dont think its profitable
           // config.setRawFood("Raw swordfish");
           //  config.setCookedFood("Swordfish");
            config.setRawFood("Raw salmon");
            config.setCookedFood("Salmon");
        }

        // deposits all items and withdraws raw food
        if (EDGEBANK.contains(getLocalPlayer()) && Bank.open()) {
            if (!Bank.getWithdrawMode().equals(BankMode.ITEM) && Bank.setWithdrawMode(BankMode.ITEM)) {
                return Calculations.random(config.getSleepLow(), config.getSleepHigh());
            }
            if (!Inventory.isEmpty() && !Inventory.contains(config.getRawFood())) {
                Bank.depositAllItems();
                return Calculations.random(config.getSleepLow(), config.getSleepHigh());
            }
            if (Bank.contains(config.getRawFood())) {
                Bank.withdrawAll(config.getRawFood());
                return Calculations.random(config.getSleepLow(), config.getSleepHigh());
            } else {
                config.setBuyFood(true);
                Bank.close();
                return Calculations.random(config.getSleepLow(), config.getSleepHigh());
            }

        } else {
            if (Walking.shouldWalk()){
                if (!Walking.isRunEnabled()) {
                    if (Walking.getRunEnergy() > Calculations.random(12, 25)) {
                        Walking.toggleRun();
                    }
                }
                Walking.walk(EDGEBANK.getRandomTile());
            }
        }
        return 400;
    }
}


