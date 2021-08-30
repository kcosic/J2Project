/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.hr.kcosic.project.utils;

import main.hr.kcosic.project.models.XmlProperties;
import main.hr.kcosic.project.models.exceptions.SettingsException;

import java.io.*;
import java.net.URI;

public class SettingsUtils {

    private static XmlProperties settings;
    private static URI uri;

    public static XmlProperties loadSettings(){

        if(uri == null){
            File file = new File("settings.properties");
            uri = file.toURI();
        }

        if(settings == null){

            settings = new XmlProperties();
            LogUtils.logInfo("URI->"+uri.getPath());
            try (InputStream settingsIS = new FileInputStream(uri.getPath())) {
                settings.load(settingsIS);
                if(settings.size() == 0){
                    throw new SettingsException("Settings are empty");
                }
            } catch (SettingsException | IOException e) {

                LogUtils.logInfo(e.getMessage());
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
        try ( Writer writer = new FileWriter(SettingsUtils.class.getResource(path).getPath())){
           writer.write(data);
        }
    }

    public static void saveSettings(XmlProperties settings){

        try (OutputStream output = new FileOutputStream(uri.getPath())) {
            SettingsUtils.settings = settings;
            settings.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }

    }

}
