package com.pymechat;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private final Alert alerta = new Alert(AlertType.INFORMATION);

    public LoginController() {
        alerta.setTitle("Error");
        alerta.setHeaderText("Error de autenticación");
        alerta.setContentText("Usuario o contraseña incorrectos");
    }

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    void tryLogin(ActionEvent event) throws IOException {

        if (AppCliente.authenticate(usernameField.getText(), passwordField.getText())) {
            AppCliente.setRoot("chat");
        } else {
            this.alerta.showAndWait();
        }
    }

}
