package tasks.impl;

import config.ShoppingListItem;
import org.dreambot.api.Client;
import org.dreambot.api.script.ScriptManager;
import tasks.AbstractTask;

import java.util.ArrayList;

/**
 * this node read the input
 *
 */
public class ReadNode extends AbstractTask {

    @Override
    public boolean accept() {
        return config.isScriptRunning()
                && !config.getSplitBySemiColon().isEmpty();
    }

    @Override
    public int priority() {
        return 99;
    }

    @Override
    public int execute() {
        config.setPaintNode("Reading list...");

        if (!Client.isLoggedIn()) {
            log("Waiting for login.");
            return 1000;
            // so when ppl use quickstart they dont try to set liveprices b4 logged in.
        }
        String[] itemStrings = config.getSplitBySemiColon().get(0).split(", ", 3);
        int length = itemStrings.length;
        if (itemStrings[0].startsWith("SELL ")) {
            itemStrings[0] = itemStrings[0].replace("SELL ", "");
            // duplicate code from below but for adding to sellingList instead of shoppingList
            // alot of duplicate code here i dont really think its worth cleaning up though
            if (length == 1) {
                log("adding item to sell: " + itemStrings[0]);
                sellingList.add(new ShoppingListItem(itemStrings[0]));
            } else if (length == 2) {
                log("adding item to sell: " + itemStrings[0]);
                sellingList.add(new ShoppingListItem(itemStrings[0], Integer.parseInt(itemStrings[1])));
            } else if (length == 3) {
                log("adding item to sell: " + itemStrings[0]);
                sellingList.add(new ShoppingListItem(itemStrings[0],
                        Integer.parseInt(itemStrings[1]),
                        Integer.parseInt(itemStrings[2])));
            } else {
                log("bad line length for item: " + itemStrings[0]);
            }
            config.popSplitBySemiColon();
            return 300;
        }

        if (length == 2) {
//            ShoppingListItem shoppingListItem = new ShoppingListItem(itemStrings[0], Integer.parseInt(itemStrings[1]));
            log("adding item to buy: " + itemStrings[0]);
            shoppingList.add(new ShoppingListItem(itemStrings[0], Integer.parseInt(itemStrings[1])));
        } else if (length == 3) {
            // for when they include a custom price
            log("adding item to buy: " + itemStrings[0]);
            shoppingList.add(new ShoppingListItem(itemStrings[0],
                    Integer.parseInt(itemStrings[1]),
                    Integer.parseInt(itemStrings[2])));
        } else {
            log("bad line length for item: " + itemStrings[0]);
        }

        config.popSplitBySemiColon();
        return 300;

//        logs left over from debugging (i forgot to add BuyNode in the main class -_-)
//        log(config.getSplitBySemiColon().isEmpty());
//        log(shoppingList.isEmpty());
//        log(shoppingList.size());

    }
}
