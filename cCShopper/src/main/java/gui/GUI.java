package gui;
import config.Config;
import org.dreambot.api.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class GUI {
    Config config = Config.getConfig();
    public void createGUI() {
        JFrame jframe = new JFrame("Shopper by camalCase");
        jframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jframe.setLocationRelativeTo(Client.getInstance().getApplet());

        jframe.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea("input items 2 buy");
        jframe.add(textArea);


        JPanel south = new JPanel();
        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                config.setScriptRunning(true);
                config.setSplitBySemiColon(new ArrayList<>(Arrays.asList(textArea.getText().split("\n"))));
                // todo add check to make sure text input is valid, then close frame
                jframe.dispose();
            }
        });
        south.add(startButton);

        jframe.getContentPane().add(BorderLayout.SOUTH, south);
        jframe.setSize(300, 500);
        jframe.setVisible(true);
    }
}
