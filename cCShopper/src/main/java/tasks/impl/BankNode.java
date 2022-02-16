package tasks.impl;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import tasks.AbstractTask;

public class BankNode extends AbstractTask {
    @Override
    public boolean accept() {
        return config.isScriptRunning() && Inventory.isFull() && GE.contains(getLocalPlayer())
                || config.isScriptRunning() && !Inventory.contains("Coins") && GE.contains(getLocalPlayer());
    }

    @Override
    public int priority() {
        return 5;
    }

    @Override
    public int execute() {
        config.setPaintNode("Banking...");

        if (GrandExchange.isOpen()) {
            GrandExchange.close();
        }

        if (Bank.openClosest()) {
            if (Bank.depositAllItems()) {
                sleepUntil(Inventory::isEmpty, 2500);
                if (Bank.contains("Coins")) {
                    Bank.withdrawAll("Coins");
                    sleepUntil(() -> !Bank.contains("Coins"), 2500);
                } else {
                    if (!Bank.contains("Coins") && !Inventory.contains("Coins")) {
                        log("no coins found, stopping script");
                        manager.stop();
                    }


                }
            }
        }
        return 0;
    }
}
