package utils;

import javafx.scene.image.Image;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Logger;

public class ImageUtils {

    private static final Logger logger = Logger.getLogger(ImageUtils.class.getName());

    private static final String DEFAULT_IMAGE = "no_image.png";

    private static final String BASE_PATH =
            System.getProperty("car_images_path", "/images/carsphoto/");

    private ImageUtils() {
        //Costruttore privato
    }

    public static Image loadCarImage(String imageName) {

        String safeFilename = DEFAULT_IMAGE;

        if (imageName != null && !imageName.trim().isEmpty()) {
            safeFilename = new File(imageName.trim()).getName();
        }

        String fullPath = BASE_PATH + safeFilename;

        try (InputStream stream = ImageUtils.class.getResourceAsStream(fullPath)) {

            if (stream != null) {
                return new Image(stream);
            } else {
                logger.info("File non trovato: uso default.");
            }

        } catch (Exception e) {
            logger.warning("Errore caricamento immagine: " + e.getMessage());
        }

        return new Image(
                Objects.requireNonNull(
                        ImageUtils.class.getResourceAsStream(BASE_PATH + DEFAULT_IMAGE)
                )
        );
    }
}