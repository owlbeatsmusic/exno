package com.owlbeatsmusic;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class StarterWindow extends JFrame {
    public void launch() {
        try {
            this.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("src/com/owlbeatsmusic/EXNO860z484.png")))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.pack();
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    new Window().launch();
                } catch (UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException | ClassNotFoundException | IOException ignored) {}
            }
        });
    }
}
