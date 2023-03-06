package com.owlbeatsmusic.items;

import com.owlbeatsmusic.Window;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class NoteFile {

    public NoteFile(String name, ArrayList<File> files) {
        tags.add(com.owlbeatsmusic.Window.noteTags.get(0));
        this.name = name;
        this.files = files;
    }

    private String name;
    private ArrayList<File> files = new ArrayList<>();
    private ArrayList<NoteTag> tags = new ArrayList<>();
    private boolean isPinned = false;

    public String getName() {return name;}
    public ArrayList<File> getFiles() {return files;}
    public ArrayList<NoteTag> getTags() {return tags;}
    public ArrayList<NoteTag> getTagsWithoutAll() {
        ArrayList<NoteTag> temp = (ArrayList<NoteTag>) this.tags.clone();
        temp.remove(0);
        return temp;
    }
    public boolean getIsPinned() {
        return isPinned;
    }

    public void setName(String name) {this.name = name;}
    public void setFiles(int index, File file) {
        this.files.set(index, file);
    }

    public void addTag(NoteTag tag) {
        tags.add(tag);
        tag.setNotes(tag.getNotes()+1);
    }
    public void removeTag(NoteTag tag) {
        tags.remove(tag);
        tag.setNotes(tag.getNotes()-1);
    }
    public void setTags(ArrayList<NoteTag> tags) {
        this.tags.clear();
        this.tags.add(Window.noteTags.get(0));
        this.tags.addAll(tags);
    }
    public void setPinned(boolean isPinned) {
        this.isPinned = isPinned;
    }

    public void openFile(int index) {
        Desktop dt = Desktop.getDesktop();
        try {
            dt.open(files.get(index));
        } catch (IOException ignored) {}

    }

}
