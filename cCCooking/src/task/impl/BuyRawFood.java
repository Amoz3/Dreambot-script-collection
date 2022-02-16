package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import task.AbstractTask;

/**
 * @author camalCase
 * @version 1
 * created 20 aug 2021
 * last modified: 20 aug 2021
 * desc: buys chicken.
 */
public class BuyRawFood extends AbstractTask {
    private final Area GE = new Area(3162, 3487, 3167, 3485);

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public boolean accept() {
        return config.isBuyFood();
    }

    @Override
    public int execute() {
        config.setStatus("Buying and selling.");
        if (GE.contains(getLocalPlayer())) {
            if (!Inventory.contains("Coins")) {
                if (Bank.openClosest()) {
                    if (Bank.contains(config.getRawFood())) {
                        config.setBuyFood(false);
                        return 0;
                    }
                    if (Bank.contains("Coins")) {
                        Bank.withdrawAll("Coins");
                        sleep(Calculations.random(config.getSleepLow(), config.getSleepHigh()));
                        Bank.close();
                    } else {
                        log("out of coins");
                        ScriptManager manager = ScriptManager.getScriptManager();
                        manager.stop();
                    }
                }
            }
            if (GrandExchange.open() && Inventory.contains("Coins")) {
                // BUYS CHICKENS APPROPRIATE TO EXP UNTIL LVL 25, AT 25 WE WILL GO TO BARB VILLAGE FISHING AREA
                // THE MATH FOR AMOUNT IS EXP FOR LVL 25, MINUS UR CURRENT XP, 30 IS EXP/CHICKEN & 2 IS BECAUSE 50% BURN RATE
                // TODO ADD FUNCTIONALITY FOR MORE THAN JUST CHICKEN.
                if (GrandExchange.buyItem(config.getRawFood(), 500, (int)(LivePrices.get(config.getRawFood()) * 1.3))) {
                    if (sleepUntil(GrandExchange::isReadyToCollect, 120000)) { // 2 min timeout
                        GrandExchange.collect();
                        sleep(Calculations.random(config.getSleepLow(), config.getSleepHigh()));
                        GrandExchange.close();
                        sleep(Calculations.random(config.getSleepLow(), config.getSleepHigh()));
                        if (Bank.openClosest()) {
                            sleep(Calculations.random(config.getSleepLow(), config.getSleepHigh()));
                            Bank.depositAllItems();
                            sleep(Calculations.random(config.getSleepLow(), config.getSleepHigh()));
                            sleep(Calculations.random(config.getSleepLow(), config.getSleepHigh()));
                            if (Inventory.isEmpty()) {
                                config.setBuyFood(false);
                                return 0;
                            }
                        }
                    }
                }
            }
        } else {
            if (Walking.shouldWalk()){
                if (!Walking.isRunEnabled()) {
                    if (Walking.getRunEnergy() > Calculations.random(12, 25)) {
                        Walking.toggleRun();
                    }
                }
                Walking.walk(GE.getRandomTile());
            }
        }
        return 0;
    }
}
