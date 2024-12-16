package com.pymechat.services;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pymechat.models.User;

public class AuthService {

    private static AuthService instance;
    private final ArrayList<User> users = new ArrayList<>();

    private AuthService() {
        // Private constructor to prevent instantiation
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public void loadUsers() {
        String filePath = "data/users.json";
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<User>>() {
            }.getType();
            ArrayList<User> usuarios = gson.fromJson(reader, listType);

            for (User user : usuarios) {
                this.users.add(user);
            }
            System.out.println("Users loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User verify(String userId, String password) {
        for (User user : this.users) {
            if (user.getId().equals(userId) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public User getUserbyId(String userId) {
        for (User user : this.users) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }
}
