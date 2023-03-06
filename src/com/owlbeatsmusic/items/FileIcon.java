package com.owlbeatsmusic.items;

public class FileIcon {
    private String name;
    private String pathToIcon;

    public String getName() {
        return name;
    }

    public String getPathToIcon() {
        return pathToIcon;
    }

    public FileIcon(String name, String pathToIcon) {
        this.name = name;
        this.pathToIcon = pathToIcon;
    }
}
