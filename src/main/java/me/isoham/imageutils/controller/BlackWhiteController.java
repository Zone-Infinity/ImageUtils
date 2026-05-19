package me.isoham.imageutils.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class BlackWhiteController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/images/bnw/{filename}")
    public ResponseEntity<byte[]> getBlackAndWhiteImage(
            @PathVariable String filename
    ) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            // get the image
            BufferedImage originalImage = ImageIO.read(filePath.toFile());

            if (originalImage == null) {
                return ResponseEntity.badRequest().build();
            }

            // create a new image
            BufferedImage grayscaleImage = new BufferedImage(
                    originalImage.getWidth(),
                    originalImage.getHeight(),
                    BufferedImage.TYPE_BYTE_GRAY
            );

            // draw in the image
            Graphics graphics = grayscaleImage.getGraphics();
            graphics.drawImage(originalImage, 0, 0, null);
            graphics.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ImageIO.write(grayscaleImage, "jpg", outputStream);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(outputStream.toByteArray());

        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
