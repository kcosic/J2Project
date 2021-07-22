/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.hr.kcosic.project.utils;

import java.awt.*;
import java.io.*;
import java.util.prefs.Preferences;
import java.util.stream.Stream;
import java.util.Properties;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import main.hr.kcosic.project.models.exceptions.SettingsException;

import javax.swing.filechooser.FileSystemView;

public class FileUtils {
    private static final String LOAD = "Load";
    private static final String SAVE = "Save";

    public static File uploadFileDialog(Window owner, String...extensions) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        Stream.of(extensions).forEach(e -> chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(e.toUpperCase(), "*." + e)
        ));
        chooser.setTitle(LOAD);
        return chooser.showOpenDialog(owner);
    }

    public static File saveFileDialog(Window owner, String...extensions) throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        Stream.of(extensions).forEach(e -> chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(e.toUpperCase(), "*." + e)
        ));
        chooser.setTitle(SAVE);
        File file = chooser.showSaveDialog(owner);
        if (file != null) {
            file.createNewFile();
        }
        return file;
    }

    public static Properties loadSettings(){

        Properties settings = new Properties();
        try (InputStream settingsIS = new FileInputStream("/settings.properties")) {
            // load a properties file
            settings.load(settingsIS);
            if(settings.size() == 0){
                throw new SettingsException("Settings are empty");
            }
        } catch (IOException | SettingsException e) {
            e.printStackTrace();
            try {
                settings = createNewSettings();
                File file = new File("/settings.properties");
                file.createNewFile();
                saveSettings(settings);

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        return settings;
    }

    private static Properties createNewSettings() {
        var settings = new Properties();
        settings.put("numberOfTiles","100");
        settings.put("numberOfPlayers","4");
        settings.put("isOverNetwork","false");
        settings.put("isHardGame","false");
        settings.put("numberOfSnakes","5");
        settings.put("numberOfLadders","1");
        settings.put("fullscreen","false");
        settings.put("resolution","1280x1024");
        return settings;
    }


    private static void saveFile(String path, String data) throws IOException {
        try ( Writer writer = new FileWriter(FileUtils.class.getResource(path).getPath())){
           writer.write(data);
        }
    }

    public static void saveSettings(Properties settings){
        try (OutputStream output = new FileOutputStream("/settings.properties")) {
            // save properties to project root folder
            settings.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }

    }

}
