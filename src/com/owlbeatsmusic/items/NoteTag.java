package com.owlbeatsmusic.items;

public class NoteTag {

    private String name;
    private final String hexColor;
    private static int notes;

    public void setName(String name) {
        this.name = name;
    }
    public void setNotes(int notes) {
        NoteTag.notes = notes;
    }

    public String getName() {return name;}
    public String getColor() {return hexColor;}
    public int getNotes() {
        return notes;
    }

    public NoteTag(String name, String hexColor) {
        this.name = name;
        this.hexColor = hexColor;
    }
}
