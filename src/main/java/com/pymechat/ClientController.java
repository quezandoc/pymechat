package com.pymechat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import com.pymechat.models.User;

import javafx.application.Platform;

public class ClientController {

    private DataInputStream dataInput;
    private DataOutputStream dataOutput;
    private User user;
    private ChatController chatController;
    private List<List<String>> groups;
    private int actualGroupIndex;

    public ClientController(Socket socket, User user, ChatController chatController) {
        this.user = user;
        this.chatController = chatController;
        try {
            dataInput = new DataInputStream(socket.getInputStream());
            dataOutput = new DataOutputStream(socket.getOutputStream());

            // Enviar el nombre del usuario al servidor
            dataOutput.writeUTF(this.user.getName());

            // Vamos a solicitar todos los grupos al servidor
            dataOutput.writeUTF("PIDE_GRUPOS");

            new Thread(this::listen).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listen() {
        try {
            while (true) {
                String texto = dataInput.readUTF();
                //  MAneja la lista de view de usuarios conectados
                if (texto.startsWith("USUARIOS_CONECTADOS:")) {
                    String[] usuarios = texto.replace("USUARIOS_CONECTADOS:", "").split(",");
                    Platform.runLater(() -> chatController.updateUsuariosConectados(usuarios));
                }
                // Maneja la lista de grupos
                if (texto.startsWith("GRUPOS:")) {
                    String[] grupos = texto.replace("GRUPOS:", "").split("|");
                    for (String grupo : grupos) {
                        String[] mensajes = grupo.split(";;");
                        groups.add(List.of(mensajes));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToServer(String mensaje) {
        try {
            dataOutput.writeUTF(mensaje);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getGrupoById(int id) {
        if (id >= 0 && id < groups.size()) {
            return groups.get(id);
        } else {
            return null;
        }
    }
}
