import config.Config;
import config.ShoppingListItem;
import gui.GUI;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
import tasks.impl.BankNode;
import tasks.impl.BuyNode;
import tasks.impl.ReadNode;
import tasks.impl.SellNode;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

@ScriptManifest(name = "camalCaseShopper",
        description = "Buys custom items from ge",
        author = "camalCase",
        version = 2,
        category = Category.MISC,
        image = "https://i.imgur.com/R5GrVxK.png")

public class Main extends TaskScript {
    Config config = Config.getConfig();
    //TODO ADD SELLING
    //TODO ADD K (THOUSAND) & M (MILLION) RECOGNITION, CONSULT LEETCODE
    //TODO ADD SAVING OF SHOPPING LIST

    @Override
    public void onStart() {


        // start gui
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            gui.createGUI();
        });
        addNodes(new ReadNode(), new BuyNode(), new BankNode(), new SellNode());

    }

    @Override
    public void onStart(String... params) {

        String cleanedParams = Arrays.toString(params).replace("[", "");
        cleanedParams = cleanedParams.replace("]", "");
        config.setSplitBySemiColon(new ArrayList<>(Arrays.asList(cleanedParams.split("\n"))));
        addNodes(new ReadNode(), new BuyNode(), new BankNode(), new SellNode());
        config.setScriptRunning(true);

    }

    @Override
    public void onPaint(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.drawString(config.getPaintNode(), 10, 365);
    }
}
