package main.hr.kcosic.project.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import main.hr.kcosic.project.models.enums.SettingsEnum;
import main.hr.kcosic.project.models.enums.SvgEnum;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class SceneUtils {

    public static Stage stage = null;
    /**
     * Creates Stage object for given view with given title.
     * @param view Type of ViewEnum.
     * @param stageTitle Title of Stage.
     * @param settings Game settings.
     * @return Loaded stage object.
     * @throws IOException If view is invalid and scene was not created, IOException is thrown.
     */
    public static Stage createStage(String view, String stageTitle, Properties settings) throws IOException {
        String resolution = null;
        if(settings.containsKey(SettingsEnum.RESOLUTION)){
            resolution = settings.get(SettingsEnum.RESOLUTION).toString();
        }
        var isFullscreen = Boolean.parseBoolean(settings.get(SettingsEnum.FULLSCREEN).toString());


        Parent root = FXMLLoader.load(Objects.requireNonNull(SceneUtils.class.getResource(view)));
        if(stage == null){
            stage = new Stage();
        }
        stage.setTitle(stageTitle != null ? stageTitle : "");
        stage.setScene(new Scene(root));
        stage.setMaximized(isFullscreen);
        stage.setResizable(false);
        if(resolution != null && !resolution.isEmpty()){
            stage.setWidth(Double.parseDouble(resolution.substring(0, resolution.indexOf('x'))));
            stage.setHeight(Double.parseDouble(resolution.substring(resolution.indexOf('x') + 1)));
        }

        return stage;
    }

    /**
     * Creates Stage object for given view with given title.
     */
    public static void replaceStage( Stage newStage) {
        stage = newStage;
    }

    public static void createAndReplaceStage(String view, String stageTitle, Properties settings) throws IOException{
        replaceStage(createStage(view, stageTitle, settings));
    }



    public static void closeStage(Node node) {
        node.getScene().getWindow().hide();
    }

    public static ImageView getImageFromSvg(SvgEnum svg){

        BufferedImageTranscoder trans = new BufferedImageTranscoder();

        // In my case I have added a file "svglogo.svg" in my project source folder within the default package.
        // Use any SVG file you like! I had success with http://en.wikipedia.org/wiki/File:SVG_logo.svg
        try (InputStream file =SceneUtils.class.getResourceAsStream(
                svg.getResourcePath())) {
            TranscoderInput transIn = new TranscoderInput(file);
            try {
                trans.transcode(transIn, null);
                // Use WritableImage if you want to further modify the image (by using a PixelWriter)
                Image img = SwingFXUtils.toFXImage(trans.getBufferedImage(), null);
                return new ImageView(img);
            } catch (TranscoderException ex) {
                throw new RuntimeException(ex);
            }
        }
        catch (IOException io) {
            throw new RuntimeException(io);

        }

    }

    private static class BufferedImageTranscoder extends ImageTranscoder {

        private BufferedImage img = null;

        @Override
        public BufferedImage createImage(int width, int height) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void writeImage(BufferedImage img, TranscoderOutput to) throws TranscoderException {
            this.img = img;
        }

        public BufferedImage getBufferedImage() {
            return img;
        }
    }
}
