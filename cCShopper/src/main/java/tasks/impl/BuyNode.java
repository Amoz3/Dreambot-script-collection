package tasks.impl;

import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.MethodProvider$LogType;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import tasks.AbstractTask;

public class BuyNode extends AbstractTask {

    // just used to iterate over shoppinglist
    int count = 0;

    @Override
    public boolean accept() {
        return config.isScriptRunning()
                && config.getSplitBySemiColon().isEmpty()
                && !shoppingList.isEmpty();
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public int execute() {
        config.setPaintNode("Buying...");

        // todo handle full inventories and get gold from bank
        if (GrandExchange.isReadyToCollect()) {
            GrandExchange.collect();
        }

        if (count != shoppingList.size()) {
            if (!shoppingList.get(count).isOrdered()) {
                // add buy code
                if (GE.contains(getLocalPlayer())) {
                    if (GrandExchange.open()) {
                        if (GrandExchange.getFirstOpenSlot() != -1) {
                            if (Inventory.count("Coins") >= (shoppingList.get(count).getItemPrice() * shoppingList.get(count).getItemQuantity())) {
                                if (GrandExchange.buyItem(
                                        shoppingList.get(count).getItemName(),
                                        shoppingList.get(count).getItemQuantity(),
                                        shoppingList.get(count).getItemPrice())) {
                                    shoppingList.get(count).setOrdered(true);
                                    shoppingList.get(count).print();
                                }
                            } else {
                                // not enough coins
                                log("not enough coins for " + shoppingList.get(count).getItemQuantity()
                                        + " " + shoppingList.get(count).getItemName() + "s at "
                                + shoppingList.get(count).getItemPrice());

                            }
                        } else {
                            // no slot opens, sleep then collect
                            Mouse.moveMouseOutsideScreen();
                            sleepUntil(GrandExchange::isReadyToCollect, 60000);
                            GrandExchange.collect();
                            return 100;
                        }
                    }
                } else {
                    // walk to ge
                    if (Walking.shouldWalk()) {
                        Walking.walk(GE.getRandomTile());
                    }
                }
                count++;
            } else {
                // if it has been ordered
                shoppingList.remove(count);

                if (shoppingList.isEmpty() && sellingList.isEmpty()) {
                    log("looks like all the orders have been placed, stopping script...");
                    manager.stop();
                }
                count++;
            }
        } else {
            count = 0;
        }
        return 1000;
    }
}
