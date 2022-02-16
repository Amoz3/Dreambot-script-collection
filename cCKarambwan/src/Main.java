import config.Config;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.GameTickListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.widgets.message.Message;
import task.impl.BankNode;
import task.impl.CookNode;
import task.impl.GotoCookNode;
import task.impl.ReupNode;


import java.awt.*;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

@ScriptManifest(name = "camalCaseKarambwan",
        description = "attempts to 1 tick karambwan",
        author = "camalCase",
        version = 1.0,
        category = Category.MONEYMAKING,
        image = "")

/**
 * @author camalCase
 * @version 1
 * created 22 aug 2021
 * last modified: 22 aug 2021
 * desc: main.
 * TODO: add end script if no games necklace
 * TODO REMOVE LIVE PRICES FROM ONSTART
 */
public class Main extends TaskScript implements GameTickListener {
    private int beginningXP;
    private int currentXp;
    private int xpGained;
    private int xpPerHour;

    private long timeBegan;
    private long timeRan;

    private int profit;
    private int gpGained;

    private int totalGpPerHour;
    private int gpPerHour;
    DecimalFormat df = new DecimalFormat("#");

    Config config = Config.getConfig();
    private int totalXpPerHour;

    @Override
    public void onStart() {
        addNodes(new BankNode(), new CookNode(), new ReupNode(), new GotoCookNode());

        beginningXP = Skills.getExperience(Skill.COOKING);
        profit = (LivePrices.get("Cooked karambwan") - 5) - (LivePrices.get("Raw karambwan") + 5);
        timeBegan = System.currentTimeMillis();

    }

    public void onPaint(Graphics g) {
        timeRan = System.currentTimeMillis() - this.timeBegan;
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.black);
        g.fillRect(0, 335, 520, 150);
        g.setColor(Color.white);
        g.drawString("camalCaseKarams: " + config.getStatus(), 10, 365);

        // g.drawString("Time ran: " + timer.formatTime(), 10, 385);
        g.drawString("time ran:" + ft(timeRan), 10, 385);

        g.drawString("Burn %: " + config.calcBurnPercent() + "%" , 10, 415);
        g.drawString("cooking level : " + Skills.getRealLevel(Skill.COOKING), 10, 435);
        currentXp = Skills.getExperience(Skill.COOKING);
        xpGained = currentXp - beginningXP;
        xpPerHour = (int)(xpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
        totalXpPerHour = xpPerHour / 1000; // for showing xp in thousands rather than large unreadable number

        g.drawString("Xp gained: " + xpGained, 300, 385);

        g.drawString("Xp/Hour: " + df.format(totalXpPerHour) + "K", 300, 435);

        gpGained = config.getCookedAmount() * profit;
        gpPerHour = (int)(gpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
        totalGpPerHour = gpPerHour / 1000;

        g.drawString("Gp made: " + gpGained , 300, 365);
        g.drawString("Gp/hour: " + df.format(totalGpPerHour) + "K" , 300, 415);
    }

    @Override
    public void onGameTick() {
        log("tick (main)");
    }

    private String ft(long duration)

    {

        String res = "";

        long days = TimeUnit.MILLISECONDS.toDays(duration);

        long hours = TimeUnit.MILLISECONDS.toHours(duration)

                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));

        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)

                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS

                .toHours(duration));

        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)

                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS

                .toMinutes(duration));

        if (days == 0) {

            res = (hours + ":" + minutes + ":" + seconds);

        } else {

            res = (days + ":" + hours + ":" + minutes + ":" + seconds);

        }

        return res;

    }
}