/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.hr.kcosic.project.utils;

import java.io.*;
import java.net.URI;
import java.util.stream.Stream;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import main.hr.kcosic.project.models.XmlProperties;
import main.hr.kcosic.project.models.exceptions.SettingsException;

import javax.swing.filechooser.FileSystemView;

public class FileUtils {
    private static final String LOAD = "Load";
    private static final String SAVE = "Save";
    private static XmlProperties settings;
    private static URI uri;

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

    public static XmlProperties loadSettings(){

        if(uri == null){
            File file = new File("settings.properties");
            uri = file.toURI();
        }

        if(settings == null){

            settings = new XmlProperties();
            try (InputStream settingsIS = new FileInputStream(uri.getPath())) {
                // load a properties file
                //TODO LOAD FROM XML
                settings.load(settingsIS);
                if(settings.size() == 0){
                    throw new SettingsException("Settings are empty");
                }
            } catch (SettingsException | IOException e) {
                LogUtils.logInfo("Settings weren't found, creating new.");
                try {
                    settings = createNewSettings();
                    File file = new File(uri.getPath());
                    file.createNewFile();
                    saveSettings(settings);

                } catch (IOException ioException) {
                    e.printStackTrace();
                }
            }
        }

        return settings;
    }

    private static XmlProperties createNewSettings() {
        var settings = new XmlProperties();
        settings.put("fullscreen","false");
        settings.put("resolution","1280x1024");
        return settings;
    }


    private static void saveFile(String path, String data) throws IOException {
        try ( Writer writer = new FileWriter(FileUtils.class.getResource(path).getPath())){
           writer.write(data);
        }
    }

    public static void saveSettings(XmlProperties settings){

        try (OutputStream output = new FileOutputStream(uri.getPath())) {
            FileUtils.settings = settings;
            //TODO save as XML
            settings.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }

    }

}
