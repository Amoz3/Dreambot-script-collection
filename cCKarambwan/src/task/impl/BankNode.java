package task.impl;

import config.Config;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import task.AbstractTask;

/**
 * @author camalCase
 * @version 1
 * created 22 aug 2021
 * last modified: 22 aug 2021
 * desc: main.
 */

public class BankNode extends AbstractTask {
    @Override
    public boolean accept() {
        return !Inventory.contains("Raw karambwan") && !config.isReup() && !config.isGotoCook();
    }

    @Override
    public int execute() {
        config.setStatus("banking");
        config.setBurntAmount(config.getBurntAmount() + Inventory.count("Burnt karambwan") );
        config.setCookedAmount(config.getCookedAmount() + Inventory.count("Cooked karambwan"));

        if (!Inventory.contains("Raw karambwan")) {
            if (!Inventory.isItemSelected()) {
                if (Bank.openClosest()) {
                    if (Bank.contains("Raw karambwan")) {
                        if (Bank.depositAllItems()) {
                            sleep(Calculations.random(220, 405));
                            if (Bank.withdrawAll("Raw karambwan")) {
                                sleep(Calculations.random(500, 1042));
                                Bank.close();
                            }
                        }
                    } else {
                        config.setReup(true);
                        Bank.close();
                        return 0;
                    }
                }
            } else {
                Inventory.deselect();
                sleep(50);
                return 0;
            }
        }
        return 0;
    }
}
