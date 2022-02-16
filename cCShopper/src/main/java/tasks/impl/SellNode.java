package tasks.impl;

import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.walking.impl.Walking;
import tasks.AbstractTask;

public class SellNode extends AbstractTask {
    int count = 0;

    @Override
    public boolean accept() {
        return config.isScriptRunning()
                && config.getSplitBySemiColon().isEmpty()
                && shoppingList.isEmpty();
    }

    @Override
    public int execute() {
        // comment
        config.setPaintNode("Selling...");
        if (GrandExchange.isReadyToCollect()) {
            GrandExchange.collect();
        }

        if (count != sellingList.size()) {
            if (!sellingList.get(count).isOrdered()) {
                // add buy code
                if (GE.contains(getLocalPlayer())) {
                    if (Inventory.contains(sellingList.get(count).getItemName())) {
                        if (GrandExchange.open()) {
                            if (GrandExchange.getFirstOpenSlot() != -1) {
                                // here we need logic to use sell all.
                                if (sellingList.get(count).getItemQuantity() > 0) {
                                    // if a positive quantity has been supplied
                                    if (GrandExchange.sellItem(
                                            sellingList.get(count).getItemName(),
                                            sellingList.get(count).getItemQuantity(),
                                            sellingList.get(count).getItemPrice())) {
                                        sellingList.get(count).setOrdered(true);
                                        sellingList.get(count).print();
                                    }
                                } else {
                                    // if a negative was supplied
                                    if (GrandExchange.sellItem(
                                            sellingList.get(count).getItemName(),
                                            Inventory.count(sellingList.get(count).getItemName()),
                                            sellingList.get(count).getItemPrice())) {
                                        sellingList.get(count).setOrdered(true);
                                        sellingList.get(count).print();
                                    }
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
                        // if you dont have the sell item in inv
                        if (GrandExchange.isOpen()) GrandExchange.close();

                        if (Bank.openClosest()) {
                            if (Bank.count(sellingList.get(count).getItemName()) > sellingList.get(count).getItemQuantity()
                            || sellingList.get(count).getItemQuantity() < 0) {
                                Bank.withdrawAll(sellingList.get(count).getItemName());
                                return 300; // think i need to return after withdrawing to not increase count and gloss over item
                            } else {
                                log("you dont seem to have enough " + sellingList.get(count).getItemName() + " to sell.");
                                sellingList.remove(count);
                                return 100;
                            }
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
                sellingList.remove(count);

                if (sellingList.isEmpty() && shoppingList.isEmpty()) {
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
