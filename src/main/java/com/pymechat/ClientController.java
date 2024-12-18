package com.pymechat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import com.pymechat.models.User;

import javafx.application.Platform;

public class ClientController {

    private DataInputStream dataInput;
    private DataOutputStream dataOutput;
    private User user;
    private ChatController chatController;
    private ArrayList<ArrayList<String>> groups = new ArrayList<>();
    private int actualGroupIndex;

    public ClientController(Socket socket, User user, ChatController chatController) {
        this.user = user;
        this.chatController = chatController;
        try {
            dataInput = new DataInputStream(socket.getInputStream());
            dataOutput = new DataOutputStream(socket.getOutputStream());

            // Enviar el nombre del usuario al servidor
            dataOutput.writeUTF("CONECTADO:"+this.user.getName());

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
                else if (texto.startsWith("GRUPOS:")) {
                    processGroups(texto);
                    Platform.runLater(() -> chatController.reloadChat());
                }
                else if (texto.startsWith("RELOADCHATS")) {
                    dataOutput.writeUTF("PIDE_GRUPOS");
                }
                else {
                    System.out.println("Mensaje no reconocido: " + texto);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processGroups(String texto) {
        if (!texto.startsWith("GRUPOS:")) return; // Validación inicial

        // Eliminar el prefijo "GRUPOS:" y dividir por "|"
        String[] grupos = texto.replace("GRUPOS:", "").split("\\|");

        // HashMap para agrupar mensajes por id de grupo
        HashMap<Integer, ArrayList<String>> groupMap = new HashMap<>();

        for (String grupo : grupos) {
            if (grupo.isEmpty()) continue; // Evitar grupos vacíos

            String[] partes = grupo.split(";;");
            if (partes.length >= 3) {
                int groupId = Integer.parseInt(partes[0]); // Primer elemento: ID
                String name = partes[1];                  // Segundo elemento: Nombre
                String message = partes[2];               // Tercer elemento: Mensaje

                // Construir el mensaje final
                String fullMessage = name + ": " + message;

                // Agregar el mensaje al grupo correspondiente en el HashMap
                groupMap.putIfAbsent(groupId, new ArrayList<>());
                groupMap.get(groupId).add(fullMessage);
            }
        }

        // Limpiar y reconstruir la lista principal
        groups.clear();
        groups.addAll(groupMap.values());

    }

    private void printGroups() {
        System.out.println("Mensajes agrupados:");
        for (int i = 0; i < groups.size(); i++) {
            System.out.println("Grupo " + (i + 1) + ": " + groups.get(i));
        }
    }

    public void sendToServer(String mensaje) {
        try {
            if (mensaje.startsWith("AGREGAR_MENSAJE_A_GRUPO:")){
                String[] parts = mensaje.split("\\|");
                int groupId = Integer.parseInt(parts[0].replace("AGREGAR_MENSAJE_A_GRUPO:", ""));
                String[] messageParts = parts[1].split(";;");
                groups.get(groupId).add(messageParts[0] + ": " + messageParts[1]);
            }
            dataOutput.writeUTF(mensaje);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getGrupoById(int id) {
        if (id >= 0 && id < groups.size()) {
            return groups.get(id);
        } else {
            return null;
        }
    }
}
