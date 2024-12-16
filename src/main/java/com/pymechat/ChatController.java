package com.pymechat;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.pymechat.models.Group;
import com.pymechat.models.Message;
import com.pymechat.models.User;
import com.pymechat.services.ChatService;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ChatController {

    private Map<String, StringBuilder> mensajesPorGrupo; // Historial de mensajes por grupo
    private Map<String, List<String>> usuariosPorGrupo;  // Lista de usuarios por grupo
    private int grupoActual;
    public static ChatService chat = ChatService.getInstance();
    private User user;

    @FXML
    private Button btnDirectMessage;

    @FXML
    private Text chatTitle;

    @FXML
    private VBox chatArea;

    @FXML
    private TextField messageInput;

    @FXML
    private Button sendButton;

    @FXML
    private ListView<String> userList;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private BorderPane root;

    public ChatController() throws IOException {

    }

    public void initializeDependencies(Socket socket, User user) {
        this.user = user;
        ClientController clientController = new ClientController(socket, user, this);
        chat.loadGroups(); // Si necesitas cargar grupos
        if (user.getType().equals("Administrativo")) {
            cambiarGrupo(0, "Administrativos");
        } else if (user.getType().equals("Auxiliares")) {
            cambiarGrupo(3, "Auxiliares");
        } else if (user.getType().equals("Emergencia")) {
            cambiarGrupo(5, "Emergencias");
        } else if (user.getType().equals("Medicos")) {
            cambiarGrupo(2, "Medicos");
        } else if (user.getType().equals("Examenes")) {
            cambiarGrupo(4, "Exámenes");
        } else if (user.getType().equals("Pabellon")) {
            cambiarGrupo(1, "Pabellon");
        } else {
            cambiarGrupo(5, "Emergencia");
        }
    }

    public void cambiarGrupo(int idGrupo, String nuevoGrupo) {
        grupoActual = idGrupo;
        chatTitle.setText(nuevoGrupo);
        Group grupo = chat.getGroupByName(nuevoGrupo);
        chatArea.getChildren().clear();
        
        List<Message> mensajes = grupo.getMessageList();
        for (Message mensaje : mensajes) {
            addTexto(mensaje.getName() +": "+mensaje.getMessage(), mensaje.getName().equals(this.user.getName()));
        }
    }

    public void addActionListener(Runnable accion) {
        sendButton.setOnAction(e -> accion.run());
        messageInput.setOnAction(e -> accion.run());
    }

    public void addTexto(String texto, boolean propio) {
        HBox mensajeBox = new HBox();
        Text mensaje = new Text(texto);
        mensaje.setStyle("-fx-background-color: lightgray; -fx-padding: 10; -fx-background-radius: 10;");

        if (propio) {
            mensaje.setStyle("-fx-background-color: lightblue; -fx-padding: 10; -fx-background-radius: 10;");
        } else {
            mensaje.setStyle("-fx-background-color: lightgreen; -fx-padding: 10; -fx-background-radius: 10;");
        }

        mensajeBox.getChildren().add(mensaje);
        chatArea.getChildren().add(mensajeBox);
    }

    public String getTexto() {
        String texto = messageInput.getText();
        messageInput.clear();
        return texto;
    }

    public void updateUsuariosConectados(String[] usuarios) {
        System.out.println("Usuarios conectados: " + Arrays.toString(usuarios));
        userList.getItems().setAll(usuarios);
    }

    @FXML
    void sendMessage() {
        String message = messageInput.getText();
        LocalDateTime actualDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = actualDateTime.format(formatter);
        // chat.addMessageToGroup(chat.getGroupByName(grupoActual).getId(), new Message(this.user.getName(), message, formattedDate)); // Cambiar a logica de servidor
        addTexto(this.user.getName()+": " + message, true);
        messageInput.clear();
    }

    @FXML
    void toAdministrativos(ActionEvent event) {
        cambiarGrupo(0, "Administrativo");
    }

    @FXML
    void toAuxiliares(ActionEvent event) {
        cambiarGrupo(3, "Auxiliares");
    }

    @FXML
    void toEmergencias(ActionEvent event) {
        cambiarGrupo(5, "Emergencias");
    }

    @FXML
    void toMedicos(ActionEvent event) {
        cambiarGrupo(2, "Medicos");
    }

    @FXML
    void toExamenes(ActionEvent event) {
        cambiarGrupo(4, "Examenes");
    }

    @FXML
    void toPabellon(ActionEvent event) {
        cambiarGrupo(1, "Pabellon");
    }

    @FXML
    void logOut(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    public int getGrupoActual() {
        return grupoActual;
    }

}
