package main.hr.kcosic.project.utils;

import main.hr.kcosic.project.controllers.*;
import main.hr.kcosic.project.models.*;
import main.hr.kcosic.project.models.enums.DataType;
import main.hr.kcosic.project.models.enums.SettingsEnum;
import main.hr.kcosic.project.models.enums.SvgEnum;
import main.hr.kcosic.project.models.enums.ViewEnum;
import main.hr.kcosic.project.models.exceptions.BoardException;
import main.hr.kcosic.project.models.exceptions.EndOfBoardException;
import main.hr.kcosic.project.models.exceptions.GameStateException;
import main.hr.kcosic.project.models.exceptions.SettingsException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DocsUtils {
    public static void generateDocs() throws IOException {
        StringBuilder sb;
        sb = startHtml("Index");
        addLinks(sb);
        endHtml(sb, "Index");
        createHtmlFile(sb,BoardController.class);
        createHtmlFile(sb,HostController.class);
        createHtmlFile(sb,HotSeatController.class);
        createHtmlFile(sb,JoinController.class);
        createHtmlFile(sb,MainMenuController.class);
        createHtmlFile(sb,MyController.class);
        createHtmlFile(sb,NetworkController.class);
        createHtmlFile(sb,NewGameController.class);
        createHtmlFile(sb,OptionsController.class);
        createHtmlFile(sb,GameServer.class);
        createHtmlFile(sb,DataType.class);
        createHtmlFile(sb,SettingsEnum.class);
        createHtmlFile(sb,SvgEnum.class);
        createHtmlFile(sb,ViewEnum.class);
        createHtmlFile(sb,EndOfBoardException.class);
        createHtmlFile(sb,GameStateException.class);
        createHtmlFile(sb,SettingsException.class);
        createHtmlFile(sb,Board.class);
        createHtmlFile(sb,DataWrapper.class);
        createHtmlFile(sb,GameState.class);
        createHtmlFile(sb,Ladder.class);
        createHtmlFile(sb,Player.class);
        createHtmlFile(sb,Snake.class);
        createHtmlFile(sb,Tile.class);
        createHtmlFile(sb,XmlProperties.class);
        createHtmlFile(sb,ChatService.class);
        createHtmlFile(sb,DocsUtils.class);
        createHtmlFile(sb,DrawingUtils.class);
        createHtmlFile(sb,LogUtils.class);
        createHtmlFile(sb,MessageUtils.class);
        createHtmlFile(sb,NetworkUtils.class);
        createHtmlFile(sb,ReflectionUtils.class);
        createHtmlFile(sb,SceneUtils.class);
        createHtmlFile(sb,SerializationUtils.class);
    }

    private static void createHtmlFile(StringBuilder sb, Class<?> clazz){
        sb = startHtml(clazz.getSimpleName());
        ReflectionUtils.readClassAndMembersInfo(clazz, sb);
        endHtml(sb, clazz.getSimpleName());
    }

    private static void addLinks(StringBuilder sb) {
        sb.append(  "<div>" +
                        "<ul>" +
                            "<li><b>Controllers</b>" +
                            "<br/>" +
                                "<ul>" +
                                    "<li><a href=\"./BoardController.html\">BoardController</a></li>" +
                                    "<li><a href=\"./HostController.html\">HostController</a></li>" +
                                    "<li><a href=\"./HotSeatController.html\">HotSeatController</a></li>" +
                                    "<li><a href=\"./JoinController.html\">JoinController</a></li>" +
                                    "<li><a href=\"./MainMenuController.html\">MainMenuController</a></li>" +
                                    "<li><a href=\"./NetworkController.html\">NetworkController</a></li>" +
                                    "<li><a href=\"./NewGameController.html\">NewGameController</a></li>" +
                                    "<li><a href=\"./OptionsController.html\">OptionsController</a></li>" +
                                    "<li><a href=\"./MyController.html\">MyController</a></li>" +
                                    "<li><a href=\"./GameServer.html\">GameServer</a></li>" +
                                "</ul>" +
                            "</li>" +
                            "<li><b>Models</b>>" +
                            "<br/>" +
                                "<ul>" +
                                    "<li><a href=\"./Board.html\">Board</a></li>" +
                                    "<li><a href=\"./DataWrapper.html\">DataWrapper</a></li>" +
                                    "<li><a href=\"./GameState.html\">GameState</a></li>" +
                                    "<li><a href=\"./Ladder.html\">Ladder</a></li>" +
                                    "<li><a href=\"./Player.html\">Player</a></li>" +
                                    "<li><a href=\"./Snake.html\">Snake</a></li>" +
                                    "<li><a href=\"./Tile.html\">Tile</a></li>" +
                                    "<li><a href=\"./XmlProperties.html\">XmlProperties</a></li>" +
                                    "<li><a href=\"./ChatService.html\">ChatService</a></li>" +
                                "</ul>" +
                            "</li>" +
                            "<li><b>Enums</b>" +
                            "<br/>" +
                                "<ul>" +
                                    "<li><a href=\"./DataType.html\">DataType</a></li>" +
                                    "<li><a href=\"./SettingsEnum.html\">SettingsEnum</a></li>" +
                                    "<li><a href=\"./SvgEnum.html\">SvgEnum</a></li>" +
                                    "<li><a href=\"./ViewEnum.html\">ViewEnum</a></li>" +
                                "</ul>" +
                            "</li>" +
                            "<li><b>Exceptions</b>" +
                            "<br/>" +
                                "<ul>" +
                                    "<li><a href=\"./BoardException.html\">BoardException</a></li>" +
                                    "<li><a href=\"./EndOfBoardException.html\">EndOfBoardException</a></li>" +
                                    "<li><a href=\"./GameStateException.html\">GameStateException</a></li>" +
                                    "<li><a href=\"./SettingsException.html\">SettingsException</a></li>" +
                                "</ul>" +
                            "</li>" +
                            "<li><b>Utils</b>" +
                            "<br/>" +
                                "<ul>" +
                                    "<li><a href=\"./DocsUtils.html\">DocsUtils</a></li>" +
                                    "<li><a href=\"./DrawingUtils.html\">DrawingUtils</a></li>" +
                                    "<li><a href=\"./LogUtils.html\">LogUtils</a></li>" +
                                    "<li><a href=\"./MessageUtils.html\">MessageUtils</a></li>" +
                                    "<li><a href=\"./NetworkUtils.html\">NetworkUtils</a></li>" +
                                    "<li><a href=\"./ReflectionUtils.html\">ReflectionUtils</a></li>" +
                                    "<li><a href=\"./SceneUtils.html\">SceneUtils</a></li>" +
                                    "<li><a href=\"./SerializationUtils.html\">SerializationUtils</a></li>" +
                                "</ul>" +
                            "</li>" +
                        "</ul>" +
                    "</div>");
    }

    private static StringBuilder startHtml(String name){
        var sb = new StringBuilder();
        sb.append(  "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                        "<head>\n" +
                            "<meta charset=\"UTF-8\">\n" +
                            "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                            "<title>" + name + "</title>\n" +
                            "<style>" +
                                "body {" +
                                    "font-family: 'Trebuchet MS', sans-serif;" +
                                    "background-color: #dbdbdb;" +
                                "}" +
                            "</style>" +
                        "</head>" +
                        "<body>" +
                            "<nav>" +
                                "<a href=\"./Index.html\">Index</a>" +
                            "</nav>");
        return sb;
    }

    private static void endHtml(StringBuilder sb, String name){
        sb.append("</body>" +
                "</html>");
        File dir = new File("./docs");
        if(!dir.exists())
        {

            LogUtils.logInfo(dir.mkdirs()+"");
        }
        File file = new File(dir.getPath()+"/"+name+".html");

        try (OutputStream output = new FileOutputStream(file.getPath())) {
            output.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException io) {
            io.printStackTrace();
        }

    }
}
