package com.example.contactsapp;

import com.example.contactsapp.controller.MainController;
import com.example.contactsapp.datamodel.ContactSingleton;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        scene.getStylesheets().add("style.css");
        stage.getIcons().add(new Image("/app-icon.png"));

        stage.setResizable(true);
        stage.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            switch (event.getCode().getCode()) {
                case 122: { // 122 = f11
                    if (stage.isFullScreen()){
                        stage.setFullScreen(false);
                    } else {
                        stage.setFullScreen(true);
                    }
                }
            }
        });

        stage.setTitle("Your contacts");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        try {
            ContactSingleton.getInstance().saveContact();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void init() throws Exception {
        try {
            ContactSingleton.getInstance().loadContact();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}