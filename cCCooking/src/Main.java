import config.Config;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
import task.impl.BankFood;
import task.impl.BuyRawFood;
import task.impl.Cook;

import java.awt.*;

@ScriptManifest(name = "camalCaseCooker",
        description = "buys and sells food, levels from 1 - 45 then makes swordfish",
        author = "camalCase",
        version = 1.0,
        category = Category.COOKING,
        image = "")

/**
 * @author camalCase
 * @version 1
 * created 20 aug 2021
 * last modified: 20 aug 2021
 * desc: main.
 */
public class Main extends TaskScript {
    Config config = Config.getConfig();

    /**
     * TODO: 20/08/2021
     * im building this so it cooks at edgeville because its close to GE for easy testing and selling
     * but lummy range is better burn rates but requires cooks assistant quest
     * idk about adding the quest but lummy range should be added.
     */

    @Override
    public void onStart() {
        addNodes(new BuyRawFood(), new BankFood(), new Cook());

    }

    public void onPaint(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.drawString(config.getStatus(), 10, 365);
        g.drawString("cooking level" + Skills.getRealLevel(Skill.COOKING), 10, 400);
    }
}