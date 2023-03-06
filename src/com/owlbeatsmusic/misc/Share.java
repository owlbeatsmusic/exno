package com.owlbeatsmusic.misc;

import com.owlbeatsmusic.items.NoteFile;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Share {

    public static void openShareMenu() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame menu = new JFrame();
        menu.setSize(new Dimension(200,150));
        menu.setLayout(new FlowLayout());
        menu.setResizable(false);

        JButton mailButton = new JButton();
        mailButton.setText("Default Mail");
        mailButton.setPreferredSize(new Dimension(200, 30));
        menu.add(mailButton);

        JButton gmailButton = new JButton();
        gmailButton.setText("Gmail (browser)");
        gmailButton.setPreferredSize(new Dimension(200, 30));
        menu.add(gmailButton);

        JButton twitterButton = new JButton();
        twitterButton.setText("Twitter (Browser)");
        twitterButton.setPreferredSize(new Dimension(200, 30));
        menu.add(twitterButton);

        menu.setVisible(true);
    }

    public static void mail(NoteFile noteFile){
        try {
            Desktop desktop = Desktop.getDesktop();
            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.MAIL)) {
                StringBuilder files = new StringBuilder();
                for (int i = 0; i < noteFile.getFiles().size(); i++) {
                    files.append(noteFile.getFiles().get(i).getAbsolutePath().replace("\\", "\\\\"));
                }
                URI mail = URI.create("mailto:?subject=" + noteFile.getName() + "&cc=&body=&Attach="+"c:\\test\\test.doc");
                desktop.mail(mail);
            } else {
                throw new RuntimeException("Mail not supported)");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void twitter(NoteFile noteFile){
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI("https://twitter.com/compose/tweet"));
            StringSelection selection = new StringSelection("Test");
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            JOptionPane.showMessageDialog(null, "Message copied to clipboard", "InfoBox: " + "Tweet", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
    public static void gmail(NoteFile noteFile){
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI("https://mail.google.com/mail/#inbox?compose=new"));
            StringSelection selection = new StringSelection("Test");
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            JOptionPane.showMessageDialog(null, "Message copied to clipboard", "InfoBox: " + "Gmail", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }



}
