package com.owlbeatsmusic;

import com.owlbeatsmusic.files.FileManager;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {

        Window window = new Window();
        window.launch();

    }
}