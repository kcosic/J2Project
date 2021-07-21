/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.hr.kcosic.project.utils;

import java.io.*;
import java.util.stream.Stream;
import java.util.Properties;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.swing.filechooser.FileSystemView;

public class FileUtils {
    private static final String LOAD = "Load";
    private static final String SAVE = "Save";

    public static File uploadFileDialog(Window owner, String...extensions) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        Stream.of(extensions).forEach(e -> {
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(e.toUpperCase(), "*." + e)
            );
        });
        chooser.setTitle(LOAD);
        return chooser.showOpenDialog(owner);
    }

    public static File saveFileDialog(Window owner, String...extensions) throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        Stream.of(extensions).forEach(e -> {
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(e.toUpperCase(), "*." + e)
            );
        });
        chooser.setTitle(SAVE);
        File file = chooser.showSaveDialog(owner);
        if (file != null) {
            file.createNewFile();
        }
        return file;
    }


    private static void CreateNewSettings(String path) throws IOException {
        /*var settings =new HashMap<String, Object>(){{
            put("numberOfTiles","100");
            put("numberOfPlayers","4");
            put("isOverNetwork","false");
            put("isHardGame","false");
            put("numberOfSnakes","5");
            put("numberOfLadders","1");
            put("fullscreen","false");
            put("resolution","1280x1024");
        }};
        File file = new File(path);
        file.createNewFile();
        try ( Writer writer = new FileWriter(FileUtils.class.getResource(path).getPath())){
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> entry : settings.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                sb.append(key);
                sb.append("=");
                sb.append(value.toString());
                sb.append(System.getProperty("line.separator"));
            }
            writer.write(sb.toString());

        }*/
    }

    public static Properties loadSettings() {

        Properties settings = new Properties();

        try (InputStream input = FileUtils.class.getResourceAsStream("/settings.properties")) {
            // load a properties file
            settings.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return settings;
    }


    private static void saveFile(String path, String data) throws IOException {
        try ( Writer writer = new FileWriter(FileUtils.class.getResource(path).getPath())){
           writer.write(data);
        }
    }

    public static void saveSettings(Properties settings){
        try (OutputStream output = new FileOutputStream("/settings.properties")) {
            var copy = new Properties();

            settings.forEach((key,value)->{
                LogUtils.logConfig(key.toString() + "->" + value.toString());
            });
            // save properties to project root folder
            settings.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        }

    }

}
