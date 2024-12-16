package com.pymechat.services;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pymechat.models.Group;
import com.pymechat.models.Message;

public class ChatService {

    private static ChatService instance;
    private final ArrayList<Group> groups = new ArrayList<>();

    private ChatService() {
        // private constructor to prevent instantiation
    }

    public static synchronized ChatService getInstance() {
        if (instance == null) {
            instance = new ChatService();
        }
        return instance;
    }

    public void loadGroups() {
        String filePath = "data/groups.json";
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Group>>() {
            }.getType();
            ArrayList<Group> grupos = gson.fromJson(reader, listType);

            for (Group group : grupos) {
                this.groups.add(group);
            }
            System.out.println("Groups loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Group getGroupByName(String groupName) {
        for (Group group : this.groups) {
            if (group.getName().equalsIgnoreCase(groupName)) {
                return group;
            }
        }
        return null;
    }

    public void addMessageToGroup(int groupId, Message message) {
        Group group = getGroupChat(groupId);
        if (group != null) {
            group.addMessage(message);
            saveGroups();
        } else {
            System.out.println("Group not found");
        }
    }

    public Group getGroupChat(int groupId) {
        if (groupId < 0 || groupId >= this.groups.size()) {
            return null;
        } else {
            return this.groups.get(groupId);
        }
    }

    private void saveGroups() {
        String filePath = "data/groups.json";
        try (FileWriter writer = new FileWriter(filePath)) {
            Gson gson = new Gson();
            gson.toJson(this.groups, writer);
            System.out.println("Group saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
