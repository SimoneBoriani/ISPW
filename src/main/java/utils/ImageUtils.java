package utils;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Logger;

public class ImageUtils {

    private static final String BASE_PATH = "/images/carsphoto/";
    private static final String DEFAULT_IMAGE = "no_image.png";
    private static Logger logger = Logger.getLogger(ImageUtils.class.getName());

    private ImageUtils() {
        //Costruttore
    }
    public static Image loadCarImage(String imageName) {

        String finalName = (imageName != null && !imageName.trim().isEmpty()) ? imageName : DEFAULT_IMAGE;
        String fullPath = BASE_PATH + finalName;

        try {

            InputStream stream = ImageUtils.class.getResourceAsStream(fullPath);

            if (stream != null) {
                return new Image(stream);
            } else {
                logger.info("File non trovato: Uso default.");
            }
        } catch (Exception e) {
            logger.info("Errore caricamento: Uso default.");
        }

        return new Image(Objects.requireNonNull(ImageUtils.class.getResourceAsStream(BASE_PATH + DEFAULT_IMAGE)));
    }
}