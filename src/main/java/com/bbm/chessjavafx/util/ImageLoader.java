package com.bbm.chessjavafx.util;

import javafx.scene.image.Image;

import java.util.Objects;

public class ImageLoader {
    public static Image loadImage(String path) {
        return new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/" + path)));
    }
}
