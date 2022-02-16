import config.Config;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.wrappers.widgets.message.Message;
import task.impl.ChopNode;
import task.impl.FireNode;

import java.awt.*;

@ScriptManifest(name = "camalCaseFiremaking",
        description = "chops logs and then cuts them",
        author = "camalCase",
        version = 1.0,
        category = Category.FIREMAKING,
        image = "")

/**
 * @author camalCase
 * @version 1
 * created 22 aug 2021
 * last modified: 22 aug 2021
 * desc: main.
 */
public class Main extends TaskScript implements ChatListener {
    Config config = Config.getConfig();
    private final Area GE = new Area(3172, 3483, 3158, 3496);



    @Override
    public void onStart() {
        addNodes(new FireNode(), new ChopNode());
    }

    public void onPaint(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.drawString(config.getStatus(), 10, 365);
        g.drawString("firemaking level " + Skills.getRealLevel(Skill.FIREMAKING) ,10 , 400);
    }

    @Override
    public void onGameMessage(Message message) {
        if (message.getMessage().contains("You can't ")) {
            log("cant set fire, moving");
            Walking.walk(GE.getRandomTile());
            sleep(1000);
            sleepUntil(() -> !getLocalPlayer().isMoving(), 5000);
        }
    }
}