package com.pymechat.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.pymechat.models.Group;
import com.pymechat.models.Message;

public class ClientThread implements Runnable {
    public static AuthService auth = AuthService.getInstance();
    private Socket socket;
    private DataInputStream dataInput;
    private DataOutputStream dataOutput;
    private String userId;
    private AppServidor servidor;

    public ClientThread(Socket socket, AppServidor servidor) {
        this.socket = socket;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        try {
            dataInput = new DataInputStream(socket.getInputStream());
            dataOutput = new DataOutputStream(socket.getOutputStream());

            // Leer el nombre del usuario
            userId = dataInput.readUTF();
            userId = userId.replace("CONECTADO:", "");
            if (!servidor.agregarCliente(this)) {
                enviarMensaje("El nombre de usuario ya está en uso.");
                socket.close();
                return;
            }

            String mensaje;
            while ((mensaje = dataInput.readUTF()) != null) {
                if (mensaje.startsWith("CONECTADO:")) {
                    mensaje = mensaje.replace("CONECTADO:", "");
                    servidor.notificarUsuariosConectados();
                } else if (mensaje.startsWith("PIDE_GRUPOS")) {
                    // Enviar la lista de grupos
                    String grupos = "GRUPOS:";
                    for (Group grupo : AppServidor.chat.getGroups()) {
                        for (Message msg : grupo.getMessageList()) {
                            grupos += grupo.getId() + ";;" + msg.getName() + ";;" + msg.getMessage() + "|";
                        }
                    }
                    enviarMensaje(grupos);
                } else if (mensaje.startsWith("AGREGAR_MENSAJE_A_GRUPO:")) {
                    // Agregar mensaje a grupo
                    String[] parts = mensaje.replace("AGREGAR_MENSAJE_A_GRUPO:", "").split("\\|");
                    int groupId = Integer.parseInt(parts[0]);
                    String[] messageParts = parts[1].split(";;");
                    Message newMessage = new Message(messageParts[0], messageParts[1], messageParts[2]);
                    AppServidor.chat.addMessageToGroup(groupId, newMessage);
                    servidor.enviarMensajeATodos("AGREGAR_MENSAJE_A_GRUPO:" + groupId+"|"+ newMessage.getName() + ": " + newMessage.getMessage(), this);
                }
                else {
                    servidor.enviarMensajeATodos(userId + ": " + mensaje, this);
                }
            }
        } catch (IOException e) {
            System.out.println(userId + " se ha desconectado.");
        } finally {
            servidor.eliminarCliente(this);
            cerrarConexion();
        }
    }

    public String getuserId() {
        return userId;
    }

    public void enviarMensaje(String mensaje) {
        try {
            dataOutput.writeUTF(mensaje);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cerrarConexion() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
