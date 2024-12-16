package com.pymechat.services;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class AppServidor {
    private CopyOnWriteArrayList<ClientThread> clientes = new CopyOnWriteArrayList<>();
    public static AuthService auth = AuthService.getInstance();
    public static ChatService chat = ChatService.getInstance();
    public static void main(String[] args) {
        new AppServidor();
    }

    public AppServidor() {
        try (ServerSocket socketServidor = new ServerSocket(5000)) {
            System.out.println("Servidor iniciado...");
            AppServidor.auth.loadUsers();
            AppServidor.chat.loadGroups();
            while (true) {
                Socket cliente = socketServidor.accept();
                // Iniciamos cliente del servidor
                ClientThread nuevoCliente = new ClientThread(cliente, this);
                new Thread(nuevoCliente).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     public synchronized boolean agregarCliente(ClientThread cliente) {
        // Verificar si el nombre de usuario ya está en uso
        for (ClientThread c : clientes) {
            if (c.getuserId().equals(cliente.getuserId())) {
                return false;
            }
        }
        clientes.add(cliente);
        notificarUsuariosConectados();
        return true;
    }

    public synchronized void eliminarCliente(ClientThread cliente) {
        clientes.remove(cliente);
        notificarUsuariosConectados();
    }

    public synchronized void notificarUsuariosConectados() {
        String usuarios = String.join(",",
                clientes.stream()
                        .map(ClientThread::getuserId)
                        .collect(Collectors.toList()));
        for (ClientThread cliente : clientes) {
            cliente.enviarMensaje("USUARIOS_CONECTADOS:" + usuarios);
        }
    }

    void enviarMensajeATodos(String string, ClientThread aThis) {
        for (ClientThread cliente : clientes) {
            if (cliente != aThis) {
                cliente.enviarMensaje(string);
            }
        }
    }
}
