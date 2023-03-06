package com.owlbeatsmusic;

import com.owlbeatsmusic.items.NoteFile;
import com.owlbeatsmusic.items.NoteTag;
import com.owlbeatsmusic.items.FileIcon;
import com.owlbeatsmusic.misc.Share;
import com.owlbeatsmusic.files.FileManager;
import com.owlbeatsmusic.window.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Window extends JFrame implements ListSelectionListener {

    public static ArrayList<NoteTag>  noteTags = new ArrayList<>();
    public static ArrayList<NoteFile> noteFiles = new ArrayList<>();

    public DefaultListModel<NoteFile> notesModel = new DefaultListModel<>();
    public JList<NoteFile> listOfNotes = new JList<>();

    public DefaultListModel<NoteTag> sidebarModel = new DefaultListModel<>();
    public JList<NoteTag> sideBarList = new JList<>();

    public DefaultListModel<FileIcon> topSidebarModel = new DefaultListModel<>();
    public JList<FileIcon> topSidebarList = new JList<>();

    public DefaultListModel<NoteTag> popupTagsModel = new DefaultListModel<>();
    public JList<NoteTag> popupTagsList = new JList<>();


    public JLabel noteIcon = new JLabel();
    public JLabel noteName = new JLabel("");
    public BackgroundMenuBar menuBar = new BackgroundMenuBar();
    public JMenu selectedMenu;
    public JMenu shareMenu;
    public NoteTag selectedNoteTag;
    public JPanel fileIconRow = new JPanel();
    public JLayeredPane sideBarLayers;
    public JPanel imagePanel;
    public JPanel notesPanel;
    public JPanel sideBar;
    public JPanel windowsToolbarButtonRow;
    JPanel westRegionPanel;


    public void newNote() throws IOException {
        String name = JOptionPane.showInputDialog(null, "Name of note?", null);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.showOpenDialog(fileChooser);

        JScrollPane jscrollpane=new JScrollPane();
        jscrollpane.setViewportView(popupTagsList);
        JOptionPane.showMessageDialog(null, jscrollpane, "Select Value", JOptionPane.PLAIN_MESSAGE);

        ArrayList<File> files = new ArrayList<>();
        Collections.addAll(files, fileChooser.getSelectedFiles());

        NoteFile noteFile = new NoteFile(name, files);
        noteFile.setTags((ArrayList<NoteTag>) popupTagsList.getSelectedValuesList());
        noteFiles.add(noteFile);

        /*imagePanel.updateUI(); */
        notesModel.clear();
        for (NoteFile noteFileIteration : noteFiles) {
            if (noteFileIteration.getTags().contains(selectedNoteTag)) {
                notesModel.addElement(noteFileIteration);
            }
        }
        int selectedIndex = sideBarList.getSelectedIndex();
        sideBarList.removeAll();
        sidebarModel.clear();
        for (int i = 0; i < noteTags.toArray().length; i++) {
            sidebarModel.addElement(noteTags.get(i));
        }
        sideBarList.setSelectedIndex(selectedIndex);
        sideBarList.updateUI();

        FileManager.saveNotes("src/com/owlbeatsmusic/files/noteFiles.exno", noteFiles);
    }
    public void pin() {
        listOfNotes.getSelectedValue().setPinned(true);
        refreshSelectMenu();
    }
    public void editTags() {
        JScrollPane jscrollpane = new JScrollPane();
        ArrayList<Integer> selectedIndices = new ArrayList<>();
        for (int i = 1; i < noteTags.toArray().length; i++) {
            if (listOfNotes.getSelectedValue().getTagsWithoutAll().contains(noteTags.get(i))) {
                selectedIndices.add(i);
            }
        }
        popupTagsList.clearSelection();
        popupTagsList.setSelectedIndices(selectedIndices.stream().mapToInt(i->i).toArray());
        jscrollpane.setViewportView(popupTagsList);
        JOptionPane.showMessageDialog(null, jscrollpane, "Select Value", JOptionPane.PLAIN_MESSAGE);
        listOfNotes.getSelectedValue().setTags((ArrayList<NoteTag>) popupTagsList.getSelectedValuesList());
        /*imagePanel.updateUI(); */
        notesModel.clear();
        for (NoteFile noteFileIteration : noteFiles) {
            if (noteFileIteration.getTags().contains(selectedNoteTag)) {
                notesModel.addElement(noteFileIteration);
            }
        }
    }
    public void rename() {
        String name = JOptionPane.showInputDialog(null, "Rename note to...", null);
        if (name != null) {
            listOfNotes.getSelectedValue().setName(name);
            listOfNotes.clearSelection();
            listOfNotes.updateUI();
        }
    }
    public void delete() {
        int input = JOptionPane.showConfirmDialog(null, "Do you want to proceed?", "Select an Option...", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        if (input == 0) {
            listOfNotes.getSelectedValue().setPinned(false);
            noteFiles.remove(listOfNotes.getSelectedValue());
            listOfNotes.clearSelection();
            /*imagePanel.updateUI(); */
            notesModel.clear();
            for (NoteFile noteFile : noteFiles) {
                if (noteFile.getTags().contains(selectedNoteTag)) {
                    notesModel.addElement(noteFile);
                }
            }
            refreshSelectMenu();
        }
    }

    public void refreshSelectMenu() {
        menuBar.remove(selectedMenu);
        selectedMenu.removeAll();
        JMenuItem editTagItem = new JMenuItem(new AbstractAction("Edit Tags") {
            public void actionPerformed(ActionEvent ae) {
                editTags();
            }
        });
        selectedMenu.add(editTagItem);

        selectedMenu.add(new JSeparator());

        try {
            ArrayList<File> files = listOfNotes.getSelectedValue().getFiles();
            for (int i = 0; i < files.toArray().length; i++) {
                int finalI = i;
                selectedMenu.add(new JMenuItem(new AbstractAction("Open File " + finalI + " (" + files.get(finalI).getName().split("\\.")[files.get(finalI).getName().split("\\.").length-1] + ")") {
                    public void actionPerformed(ActionEvent ae) {
                        listOfNotes.getSelectedValue().openFile(finalI);
                    }
                }));
            }
            menuBar.add(selectedMenu);
            menuBar.updateUI();
        } catch (NullPointerException nullPointerException) {
            menuBar.remove(selectedMenu);
            menuBar.updateUI();
        }

        shareMenu.updateUI();
        menuBar.updateUI();

    }

    public void valueChanged(ListSelectionEvent e) {

        if (e.getSource() == listOfNotes) {
            try {
                fileIconRow.removeAll();
                fileIconRow.updateUI();

                noteName.setText(listOfNotes.getSelectedValue().getName());
                noteIcon.setIcon(FileSystemView.getFileSystemView().getSystemIcon(listOfNotes.getSelectedValue().getFiles().get(0)));

                for (File file : listOfNotes.getSelectedValue().getFiles()) {
                    JButton button = new JButton(FileSystemView.getFileSystemView().getSystemIcon(file));
                    button.addActionListener(buttonEvent -> {
                        Desktop dt = Desktop.getDesktop();
                        try {
                            dt.open(file);
                        } catch (IOException ignored) {}
                    });
                    button.setBorderPainted(false);
                    button.setPreferredSize(new Dimension(20, 25));
                    fileIconRow.add(button);
                }

                fileIconRow.add(Box.createHorizontalGlue());

                JButton deleteButton = new JButton(Lists.resizeIcon("src/com/owlbeatsmusic/images/delete.png"));
                deleteButton.addActionListener(buttonEvent -> delete());
                deleteButton.setBorderPainted(false);
                deleteButton.setPreferredSize(new Dimension(20, 25));
                fileIconRow.add(deleteButton);

                JButton renameButton = new JButton(Lists.resizeIcon("src/com/owlbeatsmusic/images/rename.png"));
                renameButton.addActionListener(buttonEvent -> rename());
                renameButton.setBorderPainted(false);
                renameButton.setPreferredSize(new Dimension(20, 25));
                fileIconRow.add(renameButton);

                JButton tagButton = new JButton(Lists.resizeIcon("src/com/owlbeatsmusic/images/tag.png"));
                tagButton.addActionListener(buttonEvent -> editTags());
                tagButton.setBorderPainted(false);
                tagButton.setPreferredSize(new Dimension(20, 25));
                fileIconRow.add(tagButton);

                JButton pinButton = new JButton(Lists.resizeIcon("src/com/owlbeatsmusic/images/pin.png"));
                pinButton.addActionListener(buttonEvent -> pin());
                pinButton.setBorderPainted(false);
                pinButton.setPreferredSize(new Dimension(20, 25));
                fileIconRow.add(pinButton);

                fileIconRow.updateUI();
            } catch(NullPointerException ignored) {}
        }
        if (e.getSource() == sideBarList) {
            selectedNoteTag = sideBarList.getSelectedValue();
            listOfNotes.clearSelection();
            /*imagePanel.updateUI(); */
            notesModel.clear();
            for (NoteFile noteFile : noteFiles) {
                if (noteFile.getTags().contains(selectedNoteTag)) {
                    notesModel.addElement(noteFile);
                }
            }
        }
        if (e.getSource() == topSidebarList) {
            listOfNotes.clearSelection();
            /*imagePanel.updateUI(); */
            notesModel.clear();
            if (topSidebarList.getSelectedIndex() == 0) {
                for (NoteFile noteFile : noteFiles) {
                    notesModel.addElement(noteFile);
                }
            }
            else if (topSidebarList.getSelectedIndex() == 1) {
                for (NoteFile noteFile : noteFiles) {
                    if (noteFile.getIsPinned()) notesModel.addElement(noteFile);
                }
            }

        }
        refreshSelectMenu();
        /*imagePanel.updateUI(); */
        //notesPanel.updateUI();
        windowsToolbarButtonRow.updateUI();
    }

    public static ImageIcon resizeIcon(String path) {
        ImageIcon imageIcon = new ImageIcon(path); // load the image to a imageIcon
        Image image = imageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(16, 16,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        return new ImageIcon(newimg);
    }

    public void createPanels() throws IOException {
        westRegionPanel = new JPanel();
        westRegionPanel.setLayout(new BorderLayout());
        westRegionPanel.setMaximumSize(new Dimension(500, 1000000));
        westRegionPanel.setMaximumSize(new Dimension(500, 1000000));
        this.add(westRegionPanel, BorderLayout.CENTER);

        JPanel mostSideBar = new JPanel();
        mostSideBar.setPreferredSize(new Dimension(200, 100000));
        mostSideBar.setBackground(FileManager.sideBarBg);
        FlowLayout mostSideBarLayout = (FlowLayout)mostSideBar.getLayout();
        mostSideBarLayout.setVgap(0);
        menuBar.setPreferredSize(new Dimension(200, 30));
        menuBar.setBorder(new EmptyBorder(0,0,0,0));
        menuBar.setColor(FileManager.sideBarBg);
        mostSideBar.add(menuBar);
        JLabel allTitle = new JLabel();
        allTitle.setText("View");
        allTitle.setBorder(new EmptyBorder(10,0,0,0));
        allTitle.setFont(new Font("Segoe UI", Font.PLAIN,  10));
        mostSideBar.add(allTitle);
        mostSideBar.add(topSidebarList);
        JLabel tagsTitle = new JLabel();
        tagsTitle.setText("Tags");
        tagsTitle.setBorder(new EmptyBorder(30,0,0,0));
        tagsTitle.setFont(new Font("Segoe UI", Font.PLAIN,  10));
        mostSideBar.add(tagsTitle);
        mostSideBar.add(sideBarList);
        JScrollPane jscrollpane = new JScrollPane();
        jscrollpane.setViewportView(mostSideBar);
        jscrollpane.setBorder(new EmptyBorder(0,0,0,0));
        jscrollpane.setWheelScrollingEnabled(true);
        jscrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        westRegionPanel.add(jscrollpane, BorderLayout.WEST);

        sideBarLayers = new JLayeredPane();
        sideBarLayers.setPreferredSize(new Dimension(300, 10000));
        sideBarLayers.setBorder(new EmptyBorder(0,0,0,0));setBackground(Color.red);

        sideBar = new JPanel();
        sideBar.setBounds(0,0,320,600);
        FlowLayout sideBarLayout = (FlowLayout)sideBar.getLayout();
        sideBarLayout.setVgap(0);
        sideBar.setPreferredSize(new Dimension(300, 10000));
        sideBar.setBorder(new EmptyBorder(0,0,0,0));setBackground(Color.red);
        sideBar.setBackground(FileManager.noteListBg);

        imagePanel = new JPanel();
        FlowLayout imagePanelLayout = (FlowLayout)imagePanel.getLayout();
        imagePanelLayout.setVgap(0);
        BufferedImage scaled = ImageIO.read(new File("src/com/owlbeatsmusic/images/20210628_141402-01.JPEG")).getSubimage(
                0,
                0,
                1000,
                1000
        );
        sideBar.add(imagePanel);
        //imagePanel.add(new JLabel(new ImageIcon(scaled.getScaledInstance(610, 610,  java.awt.Image.SCALE_SMOOTH))));
        sideBarLayers.add(sideBar, 1);

        JButton windows_minimize = new JButton(new ImageIcon("src/com/owlbeatsmusic/images/windowstoolbaricons/minimize.png"));
        windows_minimize.addActionListener(buttonEvent -> delete());
        windows_minimize.setBorderPainted(false);
        windows_minimize.setContentAreaFilled(false);
        windows_minimize.setFocusPainted(false);
        windows_minimize.setOpaque(false);
        windows_minimize.setPreferredSize(new Dimension(46, 32));

        JButton windows_maximize = new JButton(new ImageIcon("src/com/owlbeatsmusic/images/windowstoolbaricons/maximize.png"));
        windows_maximize.addActionListener(buttonEvent -> delete());
        windows_maximize.setBorderPainted(false);
        windows_maximize.setContentAreaFilled(false);
        windows_maximize.setFocusPainted(false);
        windows_maximize.setOpaque(false);
        windows_maximize.setPreferredSize(new Dimension(46, 32));

        JButton windows_close = new JButton(new ImageIcon("src/com/owlbeatsmusic/images/windowstoolbaricons/close.png"));
        windows_close.setBackground(new Color(0,0,0,0));
        windows_close.addActionListener(buttonEvent -> this.dispose());
        windows_close.setBorderPainted(false);
        windows_close.setContentAreaFilled(false);
        windows_close.setFocusPainted(false);
        windows_close.setOpaque(false);
        windows_close.setPreferredSize(new Dimension(46, 32));

        JButton add_button = new JButton(new ImageIcon("src/com/owlbeatsmusic/images/windowstoolbaricons/add.png"));
        add_button.setBackground(new Color(0,0,0,0));
        add_button.addActionListener(buttonEvent -> {
            try {
                newNote();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        add_button.setBorderPainted(false);
        add_button.setContentAreaFilled(false);
        add_button.setFocusPainted(false);
        add_button.setOpaque(false);
        add_button.setPreferredSize(new Dimension(46, 32));

        windowsToolbarButtonRow = new JPanel();
        windowsToolbarButtonRow.setBorder(new EmptyBorder(0,0,0,0));
        windowsToolbarButtonRow.setBackground(FileManager.noteListBg);
        windowsToolbarButtonRow.setLayout(new BoxLayout(windowsToolbarButtonRow, BoxLayout.X_AXIS));
        windowsToolbarButtonRow.setBounds(0,0,321,32);
        sideBarLayers.add(windowsToolbarButtonRow, 2);

        windowsToolbarButtonRow.add(add_button);
        windowsToolbarButtonRow.add(Box.createHorizontalGlue());
        windowsToolbarButtonRow.add(windows_minimize);
        windowsToolbarButtonRow.add(windows_maximize);
        windowsToolbarButtonRow.add(windows_close);

        notesPanel = new JPanel();
        notesPanel.setBounds(0,0,310,600);
        notesPanel.setPreferredSize(new Dimension(310, 10000));
        notesPanel.setMinimumSize(new Dimension(310, 10000));
        notesPanel.add(new Panel(), BorderLayout.CENTER);
        FlowLayout notesPanelLayout = (FlowLayout)notesPanel.getLayout();
        notesPanelLayout.setVgap(0);
        notesPanel.setBorder(new EmptyBorder(30,0,0,0));
        notesPanel.setBackground(FileManager.noteListBg);
        notesPanel.add(listOfNotes, BorderLayout.CENTER);
        sideBarLayers.add(notesPanel, 3);

        westRegionPanel.add(sideBarLayers, BorderLayout.CENTER);

        fileIconRow.setLayout(new BoxLayout(fileIconRow, BoxLayout.LINE_AXIS));
        fileIconRow.setPreferredSize(new Dimension(100000, 30));
        fileIconRow.setBackground(FileManager.bottomBarBg);
        westRegionPanel.add(fileIconRow, BorderLayout.SOUTH);
    }
    public void createMenuBar() {
        this.remove(menuBar);
        menuBar.setOpaque(true);
        selectedMenu = new JMenu("Selected");
        menuBar.removeAll();
        menuBar.setFont(new Font("Segoe UI", Font.PLAIN,  12));
        menuBar.setBorder(new EmptyBorder(0,0,0,0));

        /*imagePanel.updateUI(); */
        notesModel.clear();
        for (NoteFile noteFile : noteFiles) {
            if (noteFile.getTags().contains(selectedNoteTag)) {
                notesModel.addElement(noteFile);
            }
        }
        JMenu fileMenu = new JMenu("File");
        fileMenu.setBackground(FileManager.sideBarBg);
        JMenuItem newFile = new JMenuItem(new AbstractAction("Add New note") {
            public void actionPerformed(ActionEvent ae) {
                try {
                    newNote();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        fileMenu.add(newFile);

        menuBar.add(fileMenu);

        JMenu tagsMenu = new JMenu("Tags");
        tagsMenu.setBackground(FileManager.sideBarBg);
        JMenuItem newTagItem = new JMenuItem(new AbstractAction("Add New Tag") {
            public void actionPerformed(ActionEvent ae) {
                String name = JOptionPane.showInputDialog(null, "Name of tag?", null);
                String color = (String) JOptionPane.showInputDialog(null, "Choose now...",
                        "The Choice of a Lifetime", JOptionPane.QUESTION_MESSAGE, null,
                        new String[]{
                                "<html><b style=\"color:#ff0000 \">Red</b>",
                                "<html><b style=\"color:#ffa500\">Orange</b>",
                                "<html><b style=\"color:#ffff00\">Yellow</b>",
                                "<html><b style=\"color:#008000\">Green</b>",
                                "<html><b style=\"color:#0000ff\">Blue</b>",
                                "<html><b style=\"color:#4b0082\">Indigo</b>",
                                "<html><b style=\"color:#ee82ee\">Violet</b>",
                        },
                        null);
                String hexColor = "ffffff";
                if (color.contains("Red")) hexColor = "ff0000";
                if (color.contains("Orange")) hexColor = "ffa500";
                if (color.contains("Yellow")) hexColor = "ffff00";
                if (color.contains("Green")) hexColor = "008000";
                if (color.contains("Blue")) hexColor = "0000ff";
                if (color.contains("Indigo")) hexColor = "4b0082";
                if (color.contains("Violet")) hexColor = "ee82ee";
                NoteTag createdTag = new NoteTag(name, hexColor);
                noteTags.add(createdTag);
                sidebarModel.addElement(createdTag);
                popupTagsModel.addElement(createdTag);

                int selectedIndex = sideBarList.getSelectedIndex();
                sideBarList.removeAll();
                sidebarModel.clear();
                for (int i = 0; i < noteTags.toArray().length; i++) {
                    sidebarModel.addElement(noteTags.get(i));
                }
                sideBarList.setSelectedIndex(selectedIndex);
                sideBarList.updateUI();
            }
        });

        tagsMenu.add(newTagItem);
        menuBar.add(tagsMenu);

        shareMenu = new JMenu("Share");
        shareMenu.setBackground(FileManager.sideBarBg);
        JMenuItem shareMail = new JMenuItem(new AbstractAction("Mail") {
            public void actionPerformed(ActionEvent ae) {
                Share.mail(listOfNotes.getSelectedValue());
            }
        });
        shareMenu.add(shareMail);

        JMenuItem shareGmail = new JMenuItem(new AbstractAction("Gmail") {
            public void actionPerformed(ActionEvent ae) {
                Share.gmail(listOfNotes.getSelectedValue());
            }
        });
        shareMenu.add(shareGmail);

        JMenuItem shareTwitter = new JMenuItem(new AbstractAction("Twitter") {
            public void actionPerformed(ActionEvent ae) {
                Share.twitter(listOfNotes.getSelectedValue());
            }
        });
        shareMenu.add(shareTwitter);

        menuBar.add(shareMenu);
    }
    public void createLists() {
        listOfNotes.setSelectionBackground(FileManager.noteListBgSelected);
        listOfNotes.setSelectionForeground(FileManager.noteListFgSelected);
        listOfNotes.setBackground(FileManager.noteListBg);
        listOfNotes.setForeground(FileManager.noteListFg);
        listOfNotes.setModel(notesModel);
        listOfNotes.setFixedCellWidth(300);
        listOfNotes.setCellRenderer(new NoteCellRenderer());
        listOfNotes.addListSelectionListener(this);
        listOfNotes.setFont(new Font("Consolas", Font.PLAIN,20));

        /*imagePanel.updateUI(); */
        for (NoteFile noteFile : noteFiles) {
            notesModel.addElement(noteFile);
        }
        listOfNotes.setSelectedIndex(0);


        topSidebarList.setSelectionBackground(FileManager.sideBarBgSelected);
        topSidebarList.setSelectionForeground(FileManager.sideBarFgSelected);
        topSidebarList.setBackground(FileManager.sideBarBg);
        topSidebarList.setForeground(FileManager.sideBarFg);
        topSidebarList.setModel(topSidebarModel);
        topSidebarList.setFixedCellWidth(180);
        topSidebarList.setCellRenderer(new TopSidebarCellRenderer());
        topSidebarList.addListSelectionListener(this);
        topSidebarList.setFont(new Font("Consolas", Font.PLAIN,14));
        topSidebarModel.addElement(new FileIcon("All Notes", "src/com/owlbeatsmusic/images/all.png"));
        topSidebarModel.addElement(new FileIcon("Pinned", "src/com/owlbeatsmusic/images/pin.png"));
        topSidebarList.setSelectedIndex(0);


        sideBarList.setSelectionBackground(FileManager.sideBarBgSelected);
        sideBarList.setSelectionForeground(FileManager.sideBarFgSelected);
        sideBarList.setBackground(FileManager.sideBarBg);
        sideBarList.setForeground(FileManager.sideBarFg);
        sideBarList.setModel(sidebarModel);
        sideBarList.setFixedCellWidth(180);
        sideBarList.setCellRenderer(new SidebarCellRenderer());
        sideBarList.addListSelectionListener(this);
        sideBarList.setFont(new Font("Consolas", Font.PLAIN,13));

        for (int i = 0; i < noteTags.toArray().length; i++) {
            sidebarModel.addElement(noteTags.get(i));
        }
        sideBarList.setSelectedIndex(0);
        sideBarList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        popupTagsList.setModel(popupTagsModel);
        popupTagsList.setCellRenderer(new SidebarCellRenderer());
        popupTagsList.addListSelectionListener(this);
        popupTagsList.setFont(new Font("Consolas", Font.PLAIN,13));
        for (int i = 1; i < noteTags.toArray().length; i++) {
            popupTagsModel.addElement(noteTags.get(i));
        }
        popupTagsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }


    public void launch() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        FileManager.setExnoThemeFile("src/com/owlbeatsmusic/files/defaultTheme.exno");

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(520, 600);
        this.setLocation(500, 300);
        this.setIconImage(new ImageIcon("src/com/owlbeatsmusic/icon.png").getImage());
        this.setTitle("exno");
        this.setUndecorated(true);


        noteTags.add(new NoteTag("All", "fffff"));
        noteTags.add(new NoteTag("Work", "fffd3d"));
        noteTags.add(new NoteTag("School", "ebc334"));

        FileManager.loadNotes("src/com/owlbeatsmusic/files/noteFiles.exno");

        createPanels();
        createMenuBar();
        createLists();

        this.setVisible(true);
        windowsToolbarButtonRow.updateUI();

    }
    class NoteCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            this.setBorder(new EmptyBorder(5,5, 30, 10));

            NoteFile noteFile = (NoteFile) value;
            String name = noteFile.getName();
            if (name.length() > 25) {
                name = name.substring(0, 22) + "...";
            }
            ArrayList<File> files = noteFile.getFiles();

            // &nbsp (space charcter in html)
            StringBuilder tagsString = new StringBuilder();
            for (int i = 1; i < noteFile.getTags().toArray().length; i++) {
                tagsString.append("<b style=\"background-color:#" + noteFile.getTags().get(i).getColor() + ";\" > &nbsp " + noteFile.getTags().get(i).getName() + "&nbsp </b><b> &nbsp<b>");
            }

            String filesString = files.toArray().length+" Files";
            if (files.toArray().length == 1) {
                filesString = files.toArray().length+" File";
            }

            String labelText = "<html><b style=\"font-size:13px\">" + name + "</b>" +
                    "<br/> <small><i>" + filesString + "</i></small>" +
                    "<br/><b><small style=\"font-size:10px\">" +
                    tagsString +
                    "</small></b>";
            setText(labelText);

            return this;
        }
    }
    class SidebarCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            this.setBorder(new EmptyBorder(5,5, 5, 5));

            NoteTag noteTag = (NoteTag) value;
            String name = noteTag.getName();
            String hexColor = noteTag.getColor();

            String labelText = "<html><b style=\"background-color:#" + hexColor + ";\" > &nbsp " + name + "&nbsp </b><b> &nbsp<b><small>";
            if (noteTag.getName().startsWith("<custom>")) {
                labelText = "<html><b style=\"color:#" + hexColor + ";\" >"+name.split("<custom>")[1]+"</b>";
                setEnabled(false);
                setBackground(Color.decode("#"+hexColor));
                setForeground(FileManager.sideBarFg);
            }
            setText(labelText);

            return this;
        }


    }
    class TopSidebarCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            this.setBorder(new EmptyBorder(5,5, 5, 5));

            FileIcon item = (FileIcon) value;

            setIcon(Lists.resizeIcon(item.getPathToIcon()));
            setText("<html><b>"+item.getName()+"</b>");

            return this;
        }


    }

    public class BackgroundMenuBar extends JMenuBar {
        Color bgColor=Color.WHITE;

        public void setColor(Color color) {
            bgColor=color;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(bgColor);
            g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

        }
    }
}