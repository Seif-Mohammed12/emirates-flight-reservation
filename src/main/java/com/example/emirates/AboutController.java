package com.example.emirates;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

public class AboutController {

    @FXML
    private AnchorPane aboutContainer;
    @FXML
    private Label label1, label2, label3, label4, label5, label6, label7;

    @FXML
    public void initialize() {
        Font customFontLarge = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 50);
        label1.setFont(customFontLarge);
        label2.setFont(customFontLarge);
        label3.setFont(customFontLarge);
        label4.setFont(customFontLarge);
        label5.setFont(customFontLarge);
        label6.setFont(customFontLarge);
        label7.setFont(customFontLarge);
    }
}
