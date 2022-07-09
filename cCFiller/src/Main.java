import config.Config;
import gui.GUI;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
import org.dreambot.api.utilities.Timer;
import task.impl.BankItems;
import task.impl.Fill;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

@ScriptManifest(name = "camalCaseFiller",
        description = "Fills containers with water",
        author = "camalCase",
        version = 2.1,
        category = Category.MONEYMAKING,
        image = "https://i.imgur.com/xZDK0OO.png")


/**
 * @author camalCase
 * @version 1
 * 25th aug updates
 * desc: main, adds nodes & starts gui.
 */
public class Main extends TaskScript {
    //FOR PAINT
    Image image;
    Config config = Config.getConfig();
    private final Timer runtime = new Timer();


    private int totalGpPerHour;
    DecimalFormat df = new DecimalFormat("#");

    @Override
    public void onStart() {

        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            gui.createGUI();
        });

        // NODES
        addNodes(new BankItems(), new Fill());

        // FOR PAINT
        try {
            image = ImageIO.read(new URL("https://i.imgur.com/zet4Los.png")); // https://i.imgur.com/zet4Los.png
        } catch (IOException e) {
            log("failed to load img");
        }
    }

    public void onPaint(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0 , 335, null);
        } else {
            g.setColor(Color.cyan);
            g.fillRect(0, 335, 520, 150);
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Time ran: " + runtime.formatTime(), 10, 365);
        g.drawString(config.getStatus(), 10, 385);

        // GP PER HOUR
        int gpGained = config.getAmountFilled() * config.getProfit();
        // 3600000.0D i didnt test if this works the same was but i think it will
        int gpPerHour = (int) (gpGained / runtime.elapsed());

        g.drawString("GP MADE: " + gpGained, 10, 405);
        g.drawString("GP/HOUR: " + df.format((gpPerHour) / 1000) + "K" , 10, 425);

    }
}
