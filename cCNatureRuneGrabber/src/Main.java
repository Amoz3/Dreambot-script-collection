import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

@ScriptManifest(
        name = "NatureRuneGrabber",
        description = "Grabs nature runes in wildy.",
        author = "camalCase",
        version = 1.0, category = Category.MONEYMAKING,
        image = "https://i.imgur.com/Rn18biO.jpg")



public class Main extends AbstractScript {
    // TASKS
    enum Tasks {
        GOTO_GE,
        CHECK_FOR_GEAR,
        BUY_GEAR, // this will include runes
        GOTO_STAFF,
        BUY_STAFF,
        EQUIP_GEAR,
        GOTO_SPLASH,
        SPLASH, // Level ups done.
        GOTO_EDGEVILLE,
        RESTOCK,
        GOTO_DITCH,
        CROSS,
        GOTO_NATS,
        TELEGRAB,
    }
    public Tasks currentTask;

    // GEAR BOOLS
    boolean HasHelm = false;
    boolean HasBody = false;
    boolean HasLegs = false;
    boolean HasShield = false;
    boolean HasStaff = false;
    boolean HasAirRune = false;
    boolean HasMindRune = false;

    boolean HasNats = false;
    boolean AirReup = false;
    boolean LawReup = false;
    boolean FoodReup = false;

    // GEAR STRINGS
    String Helmet = "Iron full helm";
    String Body = "Iron platebody";
    String Legs = "Iron platelegs";
    String Shield = "Iron kiteshield";
    String Staff = "Cursed goblin staff";
    String Food = "Jug of wine";
    int foodAmount = 20;
    // SLEEP INTS
    int shortSleepLow = 500;
    int shortSleepHigh = 1000;

    int longSleepLow = 1200;
    int longSleepHigh = 2221;
    // WORLD
    int currentWorld;

    // AREAS
    private final Area GE = new Area(3162, 3487, 3167, 3485);
    private final Area Seagulls = new Area(3027, 3237, 3034, 3236);
    private final Area Edgeville = new Area(3093, 3497, 3097, 3494);
    private final Area Nats = new Area(3303, 3859, 3301, 3861);
    private final Area Diango = new Area(3081, 3247, 3084, 3244);
    private final Area DraynorBank = new Area(3092, 3245, 3094, 3241);
    private final Area Ditch = new Area(3244, 3518, 3242, 3520);

    // STUFF FOR PAINT
    private Timer timer = new Timer();
    Image image;
    int CastCount;
    int NatCount;
    int MagicLvl;


    public void onStart() {
        // obstacles
        Walking.getAStarPathFinder().addObstacle(new PassableObstacle("Wilderness Ditch", "Cross", null, null, null));
        // FOR PAINT
        try {
            image = ImageIO.read(new URL("https://cdn.discordapp.com/attachments/685413095501201409/871751261064679464/NatGrabberPaintWithTitle.png"));
        } catch (IOException e) {
            log("failed to load img");
        }
        MagicLvl = Skills.getRealLevel(Skill.MAGIC);
        CastCount = Inventory.count("Law rune");
        NatCount = Inventory.count("Nature rune");
        // TODO: check if you already have all splashing items equipped, it will take 3-3:30 hours to go from 1-33, this will be useful
        // TODO: check if you're already 33, go straight into checking for telegrab runes > telegrabbing
        currentTask = Tasks.GOTO_GE;

        if (Equipment.contains(Helmet) && Equipment.contains(Body) && Equipment.contains(Legs) && Equipment.contains(Shield) && Equipment.contains(Staff) && Inventory.contains("Air rune") && Inventory.contains("Mind rune")) {
            log("seems like you are ready to go splashing!");
            currentTask = Tasks.GOTO_SPLASH;
        }
        if (Skills.getRealLevel(Skill.MAGIC) >= 33) {
            log("you are already lvl 33!");
            currentTask = Tasks.GOTO_GE; //TODO I CHANGED THIS FOR TESTING
        }
        if (Skills.getRealLevel(Skill.MAGIC) >= 33 && Nats.contains(getLocalPlayer()) && Inventory.contains("Law rune")) {
            log("you're ready to telegrab some runes!");
            currentTask = Tasks.TELEGRAB;
        }
    }


    @Override
    public void onPaint(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0 , 335, null);
        }
        g.drawString("Time ran: " + timer.formatTime(), 10, 355);
        g.drawString("Casts left: " + CastCount, 55, 380);
        g.drawString("Nature runes in Inv: " + NatCount, 55, 425);
        g.drawString("Magic Level: " + MagicLvl, 55, 465);

    }

    @Override
    public int onLoop() {
        switch (currentTask) {
            case GOTO_GE:
                if (!GE.contains(getLocalPlayer())) {
                    // EAT FOOD, FOR COMING BACK FROM TELEGRABBING
                    if (Inventory.contains(Food) && (getLocalPlayer().getHealthPercent() != 100)) {
                        Inventory.interact(Food, "Drink");
                    }
                    // ENABLE RUN
                    if (!Walking.isRunEnabled()) {
                        if (Walking.getRunEnergy() > Calculations.random(26,57)) {
                            Walking.toggleRun();
                        }
                    }
                    // WALKING
                    if (Walking.shouldWalk()) {
                        Walking.walk(GE.getRandomTile());
                    }
                } else if (Skills.getRealLevel(Skill.MAGIC) >= 33) {
                    currentTask = Tasks.RESTOCK;
                } else  {
                    currentTask = Tasks.CHECK_FOR_GEAR;
                }
                break;

            case CHECK_FOR_GEAR:
                if (Bank.openClosest()) {
                    Bank.depositAllItems();
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));

                    if (Bank.contains(Helmet)) {
                        log("Iron full helm found!");
                        HasHelm = true;
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    }
                    if (Bank.contains(Body)) {
                        log("Iron platebody found!");
                        HasBody = true;
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    }
                    if (Bank.contains(Legs)) {
                        log("Iron platelegs found!");
                        HasLegs = true;
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    }
                    if (Bank.contains(Shield)) {
                        log("Iron kiteshield found!");
                        HasShield = true;
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    }
                    if (Bank.contains(Staff)) {
                        log("Cursed goblin staff found!");
                        HasStaff = true;
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    }
                    if (Bank.count("Air rune") >= 3318) {
                        log("you have enough air runes");
                        HasAirRune = true;
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    }
                    if (Bank.count("Mind rune") >= 3318) {
                        log("you have enough mind runes");
                        HasMindRune = true;
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    }

                    if (HasHelm && HasBody && HasLegs && HasShield && HasMindRune && HasAirRune) {
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        Bank.close();
                        currentTask = Tasks.GOTO_STAFF;
                    } else {
                        currentTask = Tasks.BUY_GEAR;
                        sleep(1000);
                        return 0;
                    }

                    break;
                }

            case BUY_GEAR:
                // it will take 3318 of air and mind runes from 1 - 33
                log("buying gear...");
                // GET PRICES

                int HelmetPrice = (int) (LivePrices.get(Helmet) * 2.3);
                int BodyPrice = (int) (LivePrices.get(Body) * 2.3);
                int LegsPrice = (int) (LivePrices.get(Legs) * 2.3);
                int ShieldPrice = (int) (LivePrices.get(Shield) * 2.3);
                int MindPrice = (LivePrices.get("Mind Rune") + 2);
                int AirPrice = (LivePrices.get("Air rune") + 2);

                // TAKE OUT GOLD COINS
                if (!Inventory.contains("Coins")) {
                    if (Bank.openClosest()) {
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        if (Bank.contains("Coins")){
                            Bank.withdrawAll("Coins");
                            sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                            Bank.close();
                            sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                            return 0;
                        } else if (!Inventory.contains("Coins") && !Bank.contains("Coins")) {
                            log("no coins found, stopping script.");
                            stop();
                        }
                    }
                } else {
                    // ACTUALLY BUY SHIT
                    if (GrandExchange.open()) {
                        GrandExchange.cancelAll();
                        GrandExchange.collect();
                    }
                    if (!HasHelm) {
                        log("attempting to buy helm");
                        if (GrandExchange.open()) {
                            if (GrandExchange.buyItem(Helmet, 1, HelmetPrice)) {
                                if (sleepUntil(GrandExchange::isReadyToCollect, 60000)) {
                                    GrandExchange.collect();
                                    HasHelm = true;
                                }
                            }
                        }
                    }

                    if (!HasBody) {
                        log("attempting to buy body");
                        if (GrandExchange.open()) {
                            if (GrandExchange.buyItem(Body, 1, BodyPrice)) {
                                if (sleepUntil(GrandExchange::isReadyToCollect, 60000)) {
                                    GrandExchange.collect();
                                    HasBody = true;
                                }
                            }
                        }
                    }

                    if (!HasLegs) {
                        log("attempting to buy legs");
                        if (GrandExchange.open()) {
                            if (GrandExchange.buyItem(Legs, 1, LegsPrice)) {
                                if (sleepUntil(GrandExchange::isReadyToCollect, 60000)) {
                                    GrandExchange.collect();
                                    HasLegs = true;
                                }
                            }
                        }
                    }

                    if (!HasShield) {
                        log("attempting to buy shield");
                        if (GrandExchange.open()) {
                            if (GrandExchange.buyItem(Shield, 1, ShieldPrice)) {
                                if (sleepUntil(GrandExchange::isReadyToCollect, 60000)) {
                                    GrandExchange.collect();
                                    HasShield = true;
                                }
                            }
                        }
                    }
                    if (!HasAirRune) {
                        log("attempting to buy air runes");
                        if (GrandExchange.open()) {
                            if (GrandExchange.buyItem("Air rune", 3318, AirPrice)) {
                                if (sleepUntil(GrandExchange::isReadyToCollect, 60000)) {
                                    GrandExchange.collect();
                                    HasMindRune = true;
                                }
                            }
                        }
                    }

                    if (!HasAirRune) {
                        log("attempting to buy mind runes");
                        if (GrandExchange.open()) {
                            if (GrandExchange.buyItem("Mind rune", 3318, MindPrice)) {
                                if (sleepUntil(GrandExchange::isReadyToCollect, 60000)) {
                                    GrandExchange.collect();
                                    HasAirRune = true;
                                }
                            }
                        }
                    }
                    if (HasHelm && HasBody && HasLegs && HasShield && HasMindRune && HasAirRune) {
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        GrandExchange.close();
                        log("buy staff from draynor");
                        currentTask = Tasks.GOTO_STAFF;
                    } else {
                        sleep(1000);
                        return 0;
                    }


                }
                break;

            case GOTO_STAFF:
                if (!Diango.contains(getLocalPlayer())) {
                    if (!Walking.isRunEnabled()) {
                        if (Walking.getRunEnergy() > Calculations.random(26,57)) {
                            Walking.toggleRun();
                        }
                    }
                    if (Walking.shouldWalk()) {
                        Walking.walk(Diango.getRandomTile());
                    }
                } else {
                    log("you are at diango!");
                    currentTask = Tasks.BUY_STAFF;
                    stop();
                }

            case BUY_STAFF:
                if (!Inventory.contains("Coins")) {
                    if (Bank.openClosest()) {
                        Bank.withdrawAll("Coins");
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        Bank.close();
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    }
                }
                if (Shop.open("Diango")) {
                    Shop.purchase(Staff, 1);
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Shop.close();
                }
                sleep(1000);
                if (Inventory.contains(Staff)) {
                    if (Walking.shouldWalk()) {
                        Walking.walk(DraynorBank.getRandomTile());
                    }
                    sleep(5021);
                    currentTask = Tasks.EQUIP_GEAR;
                    break;
                }
                break;
            case EQUIP_GEAR:
                // UNEQUIP WORN ITEMS, THIS IS TO MAKE SURE WE ARENT ALREADY WEARING THE ARMOUR
                // THIS WONT STOP MAGIC AMULETS RUINING THE SPLASH BECAUSE IT DOESNT REMOVE THEM!
                Equipment.unequip(EquipmentSlot.HAT);
                sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                Equipment.unequip(EquipmentSlot.CHEST);
                sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                Equipment.unequip(EquipmentSlot.LEGS);
                sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                Equipment.unequip(EquipmentSlot.SHIELD);
                sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                Equipment.unequip(EquipmentSlot.WEAPON);

                // withdraw and equip splashing setup.
                if  (Bank.openClosest()) {
                    Bank.depositAllItems();
                    sleep(Calculations.random(1000, 2000)); // if this sleep is too short, it will skip withdrawing the helm
                    Bank.withdraw(Helmet);
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Bank.withdraw(Body);
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Bank.withdraw(Legs);
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Bank.withdraw(Shield);
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Bank.withdraw(Staff);
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Bank.close();
                    sleep(1000);
                    Inventory.interact(Helmet, "Wear");
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Inventory.interact(Body, "Wear");
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Inventory.interact(Legs, "Wear");
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Inventory.interact(Shield, "Wear");
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Inventory.interact(Staff, "Wield");
                    sleep(1000);
                }

                // check everything is equipped, withdraw runes.
                if (Equipment.contains(Helmet) && Equipment.contains(Body) && Equipment.contains(Legs) && Equipment.contains(Shield) && Equipment.contains(Staff)) {
                    log("everything is equipped!");
                    if (Bank.openClosest()) {
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        Bank.withdrawAll("Mind Rune");
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        Bank.withdrawAll("Air Rune");
                    }
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    currentTask = Tasks.GOTO_SPLASH;
                    log("Going to seagulls!");
                    break;
                }
            case GOTO_SPLASH:
                if (!Seagulls.contains(getLocalPlayer())) {
                    if (!Walking.isRunEnabled()) {
                        if (Walking.getRunEnergy() > Calculations.random(26,57)) {
                            Walking.toggleRun();
                        }
                    }
                    if (Walking.shouldWalk()) {
                        Walking.walk(Seagulls.getRandomTile());
                    }
                } else {
                    currentTask = Tasks.SPLASH;
                    break;
                }
                break;
            case SPLASH:
                if (Skills.getRealLevel(Skill.MAGIC) >= 33) {
                    log("you have reached level 33!");
                    currentTask = Tasks.GOTO_EDGEVILLE;
                    break;
                }
                if (!Combat.isAutoRetaliateOn()) {
                    Combat.toggleAutoRetaliate(true);
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                }
                if (Magic.getAutocastSpell() != Normal.WIND_STRIKE) {
                    log("setting autocast to wind strike!");
                    Magic.setAutocastSpell(Normal.WIND_STRIKE);
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                }
                NPC Seagull = NPCs.closest(r -> r != null && r.getName().equals("Seagull") && r.hasAction("Attack") && !r.isInCombat() && !r.isHealthBarVisible());
                if (!getLocalPlayer().isInCombat()) {
                    Seagull.interact("Attack");
                    //sleep(60000);
                } else {
                    if (Calculations.random(1, 20) == 3) {
                    getLocalPlayer().getCharacterInteractingWithMe().interact("Attack");
                        sleep(Calculations.random(15000, 18200));
                    }
                    sleep(Calculations.random(1000, 1500));
                }
                break;
            // PAST LVL 33 MAGIC
            case GOTO_EDGEVILLE:
                    currentTask = Tasks.RESTOCK;
                break;
            case RESTOCK:
                int LawPrice = (LivePrices.get("Law rune") + 2);
                int FoodPrice = (LivePrices.get(Food) + 1);
                AirPrice = (LivePrices.get("Air rune") + 2);

                if (Bank.openClosest()) {
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Bank.depositAllItems();
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    if (!Inventory.isEmpty()) {
                        break;
                    }
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    Bank.withdrawAll("Coins");
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    HasNats = Bank.contains("Nature rune");
                    if (HasNats) {
                        Bank.withdrawAll("Nature rune");
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    }

                    FoodReup = Bank.count(Food) >= 10;
                    LawReup = Bank.count("Law rune") >= 100;
                    AirReup = Bank.count("Air rune") >= 100;
                }

                if (HasNats) {
                    if (GrandExchange.open()) {
                        if (GrandExchange.sellItem("Nature rune", Inventory.count("Nature rune"), LivePrices.get("Nature rune"))) {
                            if (sleepUntil(GrandExchange::isReadyToCollect, 60000)) {
                                GrandExchange.collect();
                                HasNats = false;
                            }
                        }
                    }
                }

                if (!LawReup) {
                    log("attempting to buy law runes");
                    if (GrandExchange.open()) {
                        if (GrandExchange.buyItem("Law rune", 100, LawPrice)) {
                            if (sleepUntil(GrandExchange::isReadyToCollect, 60000)) {
                                GrandExchange.collect();
                                LawReup = true;
                            }
                        }
                    }
                }

                if (!AirReup) {
                    log("attempting to buy air runes");
                    if (GrandExchange.open()) {
                        if (GrandExchange.buyItem("Air rune", 100, AirPrice)) {
                            if (sleepUntil(GrandExchange::isReadyToCollect, 60000)) {
                                GrandExchange.collect();
                                AirReup = true;
                            }
                        }
                    }
                }

                if (!FoodReup) {
                    log("attempting to buy Food");
                    if (GrandExchange.open()) {
                        if (GrandExchange.buyItem(Food, foodAmount, FoodPrice)) {
                            if (sleepUntil(GrandExchange::isReadyToCollect, 60000)) {
                                GrandExchange.collect();
                                FoodReup = true;
                            }
                        }
                    }
                }


                if (LawReup && AirReup && FoodReup) {
                    GrandExchange.close();
                    if (Bank.openClosest()) {
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        Bank.depositAllItems();
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        Bank.depositAllEquipment();
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        Bank.withdraw("Law rune", 100);
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        Bank.withdraw("Air rune", 100);
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        Bank.withdraw(Food, foodAmount);
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        CastCount = Inventory.count("Law rune");
                        MagicLvl = Skills.getRealLevel(Skill.MAGIC);
                        Bank.close();
                        currentTask = Tasks.GOTO_DITCH;
                    }

                }
                break;
            case GOTO_DITCH:
                if (!Ditch.contains(getLocalPlayer())) {

                    if (Walking.shouldWalk()) {
                        Walking.walk(Ditch.getRandomTile());
                    }
                } else {
                    currentTask = Tasks.CROSS;
                }
                break;
            case CROSS:
                GameObject wildernessDitch = GameObjects.closest(object -> object.hasAction("Cross") && object != null && object.getName().equals("Wilderness Ditch"));
                if (wildernessDitch.interact("Cross")) {
                    sleep(Calculations.random(longSleepLow, longSleepHigh));
                    WidgetChild enterwild = Widgets.getChildWidget(475, 11);
                    if (enterwild != null) {
                        enterwild.interact();
                    }
                }

                sleep(Calculations.random(longSleepLow, longSleepHigh));
                if (!Ditch.contains(getLocalPlayer())) {
                    currentTask = Tasks.GOTO_NATS;
                }

                break;
            case GOTO_NATS: // TODO TESTING THIS RN
                if (Combat.isAutoRetaliateOn()) {
                    Combat.toggleAutoRetaliate(false);
                }
                if (Inventory.contains(Food) && (getLocalPlayer().getHealthPercent() != 100)) {
                    Inventory.interact(Food, "Drink");
                    if (!Walking.isRunEnabled()) {
                        Walking.toggleRun();
                    }
                }

                if (!Nats.contains(getLocalPlayer())) {
                    if (!Walking.isRunEnabled()) {
                        if (Walking.getRunEnergy() > Calculations.random(26,57)) {
                            Walking.toggleRun();
                        }
                    }
                    if (Walking.shouldWalk()) {
                        log("walking");
                        Walking.walk(Nats.getRandomTile());
                    }
                } else {
                    log("you are at Nats!");
                    currentTask = Tasks.TELEGRAB;
                }
                break;

            case TELEGRAB:
                if (Inventory.contains("Law rune") && Inventory.contains("Air rune")) { //change to if have law rune
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    log("law runes found, looking for nat rune....");
                    sleepUntil(Client::isLoggedIn, 30000);
                    GroundItem NatureRune = GroundItems.closest("Nature rune");
                    sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                    if (NatureRune != null) {
                        log("nat found, casting telegrab");
                        Magic.castSpell(Normal.TELEKINETIC_GRAB);
                        NatureRune.interact("Cast");
                        sleep(Calculations.random(shortSleepLow, shortSleepHigh));
                        CastCount = Inventory.count("Law rune");
                        MagicLvl = Skills.getRealLevel(Skill.MAGIC);
                    } else {
                        log("no nat found, hopping world.");
                        NatCount = Inventory.count("Nature rune");
                        currentWorld = Client.getCurrentWorld();
                        World hop = Worlds.getRandomWorld(w -> w.isF2P() && !w.isPVP() && w.getMinimumLevel() == 0);
                        WorldHopper.hopWorld(hop);
                        sleepUntil(() -> Client.getCurrentWorld() != currentWorld, 10000);

                        return 0;
                    }
                } else {
                    log("out of runes, going back to GE to restock.");
                    NatCount = Inventory.count("Nature rune");
                    currentTask = Tasks.GOTO_GE;
                    break;
                }

        }
        return 0;
    }
}
