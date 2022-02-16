package task.impl;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.input.Keyboard;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.omg.PortableServer.REQUEST_PROCESSING_POLICY_ID;
import task.AbstractTask;

import java.awt.*;
import java.security.Key;


/**
 * @author camalCase
 * @version 1
 * created 23 aug 2021
 * last modified: 24 aug 2021
 * desc: teleports to ge and sells and buys karambwan.
 */

public class ReupNode extends AbstractTask {
    private final Area GE = new Area(3172, 3473, 3158, 3496);
    private final Tile cooktile = new Tile(3043, 4972,1);

    @Override
    public boolean accept() {
        return config.isReup();
    }

    @Override
    public int execute() {

        config.setStatus("shopping");
        sleep(400);
        if (!GE.contains(getLocalPlayer())) {
            // teleport to ge
            if (Equipment.contains(11982) ||
                    Equipment.contains(11980) ||
                    Equipment.contains(11984) ||
                    Equipment.contains(11986) ||
                    Equipment.contains(11988) ||
                    Equipment.contains(11990)) { // numbers for rings of wealth, strings didnt work
                Equipment.open();
                Equipment.getItemInSlot(EquipmentSlot.RING).interact("Grand Exchange");
                sleep(Calculations.random(4000, 6000));
            } else {
                // get ring of wealth from bank and equip then return
                log("no ring of wealth found");
                manager.stop();
            }
        } else {
            if (!Inventory.isItemSelected()) {
                if (Bank.openClosest()) {
                    sleepUntil(Bank::isOpen, 20000);
                    Bank.depositAllItems();
                    sleep(Calculations.random(config.getSleepTimeLow(), config.getSleepTimeHigh()));
                    if (Bank.count("Raw karambwan") > 28) {
                        config.setReup(false);
                        config.setGotoCook(true);
                    } else if (Bank.count("Cooked karambwan") > 1) {
                        Bank.setWithdrawMode(BankMode.NOTE);
                        sleep(Calculations.random(config.getSleepTimeLow(), config.getSleepTimeHigh()));
                        Bank.withdrawAll("Cooked karambwan");
                        sleep(Calculations.random(config.getSleepTimeLow(), config.getSleepTimeHigh()));
                        Bank.close();
                        sleepUntil(Bank::close, 10000);
                        sleep(Calculations.random(config.getSleepTimeLow(), config.getSleepTimeHigh()));
                        GrandExchange.open();
                        sleepUntil(GrandExchange::isOpen, 10000);
                        GrandExchange.sellItem("Cooked karambwan", Inventory.count("Cooked karambwan"), LivePrices.get("Cooked karambwan") - 5);
                        sleepUntil(GrandExchange::isReadyToCollect, 180000);
                        GrandExchange.collect();
                    } else if (Bank.count("Cooked karambwan") == 0 && !(Bank.count("Raw karambwan") > 28) && Bank.contains("Coins")) {
                        // if you got no cooked karambwan and have no more than 28 raw ones, and coins
                        // buy raw ones
                        Bank.withdrawAll("Coins");
                        sleep(Calculations.random(config.getSleepTimeLow(), config.getSleepTimeHigh()));
                        Bank.close();
                        sleepUntil(Bank::close, 5000);
                        sleep(Calculations.random(config.getSleepTimeLow(), config.getSleepTimeHigh()));
                        GrandExchange.open();
                        sleepUntil(GrandExchange::isOpen, 5000);
                        GrandExchange.buyItem("Raw karambwan", Inventory.count("Coins") / (LivePrices.get("Raw karambwan") + 5), (LivePrices.get("Raw karambwan") + 5));
                        sleepUntil(GrandExchange::isReadyToCollect, 240000);
                        GrandExchange.collect();
                        sleep(Calculations.random(config.getSleepTimeLow(), config.getSleepTimeHigh()));
                        GrandExchange.close();
                        sleep(Calculations.random(config.getSleepTimeLow(), config.getSleepTimeHigh()));
                        if (Bank.openClosest()) {
                            Bank.depositAllItems();
                            sleep(Calculations.random(config.getSleepTimeLow(), config.getSleepTimeHigh()));
                            Bank.close();
                        }
                        config.setReup(false);
                        config.setGotoCook(true);
                        return 0;

                    }
                }

            } else {
                // sometimes it would select a karambwan and not be able to open the bank
                Inventory.deselect();
            }
        }
        return 0;
    }
}
