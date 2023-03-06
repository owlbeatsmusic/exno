package com.owlbeatsmusic.files;

import com.owlbeatsmusic.Window;
import com.owlbeatsmusic.items.NoteFile;
import com.owlbeatsmusic.items.NoteTag;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class FileManager {

    public static Color noteListBg;
    public static Color noteListFg;
    public static Color noteListBgSelected;
    public static Color noteListFgSelected;
    public static Color sideBarBg;
    public static Color sideBarFg;
    public static Color sideBarBgSelected;
    public static Color sideBarFgSelected;
    public static Color bottomBarBg;

    public static void setExnoThemeFile(String pathToExnoThemeFile) throws IOException {
        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(new File(pathToExnoThemeFile).toPath(), Charset.defaultCharset());
        if (lines.get(0).startsWith("<THEMEFILE>")) {
            noteListBg = Color.decode(lines.get(1).strip().split("=")[1].strip());
            noteListFg = Color.decode(lines.get(2).strip().split("=")[1].strip());
            noteListBgSelected = Color.decode(lines.get(3).strip().split("=")[1].strip());
            noteListFgSelected = Color.decode(lines.get(4).strip().split("=")[1].strip());
            sideBarBg = Color.decode(lines.get(5).strip().split("=")[1].strip());
            sideBarFg = Color.decode(lines.get(6).strip().split("=")[1].strip());
            sideBarBgSelected = Color.decode(lines.get(7).strip().split("=")[1].strip());
            sideBarFgSelected = Color.decode(lines.get(8).strip().split("=")[1].strip());
            bottomBarBg = Color.decode(lines.get(9).strip().split("=")[1].strip());
        }
    }

    public static void saveNotes(String pathToExnoSaveFile, ArrayList<NoteFile> noteFiles) throws IOException {
        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(new File(pathToExnoSaveFile).toPath(), Charset.defaultCharset());
        StringBuilder output = new StringBuilder();
        if (lines.get(0).startsWith("<NOTEFILES>")) {
            output.append("<NOTEFILES>");
            output.append(System.lineSeparator());
            output.append(System.lineSeparator());

            for (NoteFile noteFile : noteFiles) {
                output.append("<FILE>");
                output.append(System.lineSeparator());
                output.append("name=");
                output.append(noteFile.getName());
                output.append(System.lineSeparator());

                output.append("files=");
                for (File file : noteFile.getFiles()) {
                    output.append(file.getAbsolutePath());
                    output.append("-&&&-");
                }
                output.append(System.lineSeparator());

                output.append("tags=");
                for (NoteTag noteTag : noteFile.getTags()) {
                    output.append(noteTag.getName());
                    output.append("-&-");
                    output.append(noteTag.getColor());
                    output.append("-&&&-");
                }
                output.append(System.lineSeparator());

                output.append("isPinned=");
                output.append(noteFile.getIsPinned());
                output.append(System.lineSeparator());
                output.append(System.lineSeparator());

                System.out.println(output);
            }
        }
    }

    public static void loadNotes(String pathToExnoSaveFile) throws IOException {
        ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(new File(pathToExnoSaveFile).toPath(), Charset.defaultCharset());
        ArrayList<Integer> fileIndexes = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("<FILE>")) {
                fileIndexes.add(i);
            }
        }

        if (lines.get(0).startsWith("<NOTEFILES>")) {
            for (int index : fileIndexes) {
                String name = lines.get(index+1).split("name=")[1];

                ArrayList<File> files = new ArrayList<>();
                for (String fileToPath : lines.get(index + 2).split("files=")[1].split("-&&&-")) {
                    files.add(new File(fileToPath));
                }

                ArrayList<NoteTag> noteTags = new ArrayList<>();
                for (String noteTagString : lines.get(index + 3).split("tags=")[1].split("-&&&-")) {
                    String noteTagName = noteTagString.split("-&-")[0].replace(System.lineSeparator(), "");
                    String noteTagColor = noteTagString.split("-&-")[1].replace(System.lineSeparator(), "");
                     for (NoteTag noteTag : Window.noteTags) {
                         if (noteTag.getName().equals(noteTagName) && noteTag.getColor().equals(noteTagColor) && noteTag != Window.noteTags.get(0)) {
                             noteTags.add(noteTag);
                         }
                     }
                }

                boolean isPinned = Boolean.parseBoolean(lines.get(index+4).split("=")[1].replace(System.lineSeparator(), ""));

                NoteFile noteFile = new NoteFile(name, files);
                noteFile.setTags(noteTags);
                noteFile.setPinned(isPinned);
                Window.noteFiles.add(noteFile);
            }
        }
    }
}