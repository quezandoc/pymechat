package com.pymechat.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.pymechat.models.User;

import javafx.application.Platform;

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
            if (!servidor.agregarCliente(this)) {
                enviarMensaje("El nombre de usuario ya está en uso.");
                socket.close();
                return;
            }

            System.out.println(userId + " se ha conectado.");
            String mensaje;
            while ((mensaje = dataInput.readUTF()) != null) {
                servidor.enviarMensajeATodos(userId + ": " + mensaje, this);
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
