package com.pymechat;

import java.io.IOException;
import java.net.Socket;

import com.pymechat.models.User;
import com.pymechat.services.AuthService;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class AppCliente extends Application {

    private static final AuthService authService = AuthService.getInstance();
    private static Scene scene;
    private static User user;

    @Override
    public void start(Stage stage) throws IOException {
        // Cargar usuarios desde users.json
        stage.setTitle("PyMeChat");
        authService.loadUsers();
        scene = new Scene(loadFXML("login"), 800, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static boolean authenticate(String username, String password) {
        user = authService.verify(username, password); // Cambiar a logica de servidor
        return user != null;
    }

    public static User getUser() {
        return user;
    }

    static void setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppCliente.class.getResource(fxml + ".fxml"));
        Parent parent = fxmlLoader.load();

        // Si estamos cargando la vista del chat, configurar el controlador
        if (fxml.equals("chat")) {
            ChatController chatController = fxmlLoader.getController();
            // Crear el socket y configurar el controlador con el usuario autenticado
            Socket socket = new Socket("localhost", 5000);
            chatController.initializeDependencies(socket, getUser());
        }

        scene.setRoot(parent);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppCliente.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
