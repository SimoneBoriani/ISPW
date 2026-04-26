package utils;

import javafx.scene.image.Image;
import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Logger;

public class ImageUtils {

    private static final String BASE_PATH = "/images/carsphoto/";
    private static final String DEFAULT_IMAGE = "no_image.png";
    private static final Logger logger = Logger.getLogger(ImageUtils.class.getName());

    private ImageUtils() {
        // Costruttore privato
    }

    public static Image loadCarImage(String imageName) {

        String safeFilename = DEFAULT_IMAGE;

        if (imageName != null && !imageName.trim().isEmpty()) {
            safeFilename = new File(imageName.trim()).getName();
        }

        String fullPath = BASE_PATH + safeFilename;

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