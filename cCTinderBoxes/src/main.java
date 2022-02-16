import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

@ScriptManifest(name = "camalCaseTinderbox",
        description = "takes tinderboxes",
        author = "camalCase",
        version = 1.11,
        category = Category.MONEYMAKING,
        image = "https://i.imgur.com/rBMrwvp.png")

public class main extends AbstractScript {
    private Timer timer = new Timer();
    private Image image;

    enum Task {
        GOTO_BANK,
        DEPOSIT,
        GOTO_BOOKSHELF,
        CLICK_DROP,
        PICKUP,
        GOTO_GE,
        SELL
    }
    public Task currentTask;

    // COUNTS
    int twoEight = 0;
    int count = 0;

    // SLEEP INTS
    int shortSleepLow = 200;
    int shortSleepHigh = 600;

    // AREAS
    private final Area draynorBank = new Area(3092, 3245, 3094, 3241);
    private final Area bookshelfTile = new Area(3093, 3253, 3093, 3253);
    private final Area GE = new Area(3162, 3487, 3167, 3485);

    @Override
    public void onStart() {

        try {
            image = ImageIO.read(new URL("https://cdn.discordapp.com/attachments/855565889897758770/872100745405665300/camalCaseTinderboxes_paint.png"));
        } catch (IOException e) {
            log("failed to load img");
        }

        Walking.getAStarPathFinder().addObstacle(new PassableObstacle("Door", "Open", null, null, null));

        currentTask = Task.GOTO_BANK;
    }

    public void onPaint(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0 , 355, null);
        }

        g.drawString("Time ran: " + timer.formatTime(), 10, 375);
        g.drawString("Tinderboxes collected: " + count, 10, 400);
        g.drawString("?/28 on floor: " + twoEight, 10, 440);

    }


    @Override
    public int onLoop() {
        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
        switch (currentTask) {
            case GOTO_BANK:
                if (!draynorBank.contains(getLocalPlayer())) {
                    if (!Walking.isRunEnabled()) {
                        if (Walking.getRunEnergy() > Calculations.random(26,57)) {
                            Walking.toggleRun();
                        }
                    }
                    if (Walking.shouldWalk()) {
                        Walking.walk(draynorBank.getRandomTile());
                    }
                } else {
                    currentTask = Task.DEPOSIT;
                }
                break;
            case DEPOSIT:
                if (Bank.openClosest()) {
                    Bank.depositAllItems();
                    Bank.depositAllEquipment();
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    if (Bank.count("Tinderbox") >= 2000) {
                        currentTask = Task.GOTO_GE;
                        break;
                    } else if (Inventory.isEmpty()) {
                        Bank.close();
                        currentTask = Task.GOTO_BOOKSHELF;
                    }
                }
                break;
            case GOTO_BOOKSHELF: // TILE 3093, 3253
                    if (!bookshelfTile.contains(getLocalPlayer())) {
                        if (!Walking.isRunEnabled()) {
                            if (Walking.getRunEnergy() > Calculations.random(26,57)) {
                                Walking.toggleRun();
                            }
                        }
                        if (Walking.shouldWalk()) {
                            Walking.walk(bookshelfTile.getRandomTile());
                        }
                    } else {
                        currentTask = Task.CLICK_DROP;
                    }
                break;
            case CLICK_DROP:
                GameObject bookshelf = GameObjects.closest(object -> object.hasAction("Search") && object.getID() == 7073);
                sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                if (bookshelf != null) {
                    bookshelf.interact("search");
                    sleep(Calculations.random(60, 190));
                    if (Inventory.contains("Tinderbox")) {
                        Inventory.dropAll();
                        count += 1;
                        twoEight += 1;
                        if (twoEight == 28) {
                            twoEight = 0;
                            currentTask = Task.PICKUP;
                        }
                    }
                }
                break;
            case PICKUP:
                GroundItem tinderBox = GroundItems.closest("Tinderbox");
                try {
                    if (!Inventory.isFull() && tinderBox != null) {
                        tinderBox.interact("Take");
                    } else {
                        currentTask = Task.GOTO_BANK;
                    }
                } catch (NullPointerException i) {
                    currentTask = Task.GOTO_BANK;
                    break;
                }
                break;
            case GOTO_GE:
                if (!GE.contains(getLocalPlayer())) {
                    if (!Walking.isRunEnabled()) {
                        if (Walking.getRunEnergy() > Calculations.random(26,57)) {
                            Walking.toggleRun();
                        }
                    }
                    if (Walking.shouldWalk()) {
                        Walking.walk(GE.getRandomTile());
                    }
                } else {
                    currentTask = Task.SELL;
                }
                break;
            case SELL:
                if (Bank.openClosest()) {
                    Bank.depositAllItems();
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Bank.setWithdrawMode(BankMode.NOTE);
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Bank.withdrawAll("Tinderbox");
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Bank.close();
                    if (GrandExchange.open()) {
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        GrandExchange.collect();
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        if (GrandExchange.sellItem("Tinderbox",Inventory.count("Tinderbox"), (int) (LivePrices.get("Tinderbox") * 0.8))) {
                            sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                            GrandExchange.close();
                            currentTask = Task.GOTO_BANK;
                        }
                    }
                }
                break;
        }
        return 0;
    }
}

