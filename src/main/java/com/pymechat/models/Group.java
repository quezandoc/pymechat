package com.pymechat.models;

import java.util.List;

public class Group {
        private int id;
        private String name;
        private List<String> writePermissions;
        private List<Message> messageList;

        // Getters and Setters

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Message> getMessageList() {
            return messageList;
        }

        public void setMessageList(List<Message> messageList) {
            this.messageList = messageList;
        }

        public void addMessage(Message message) {
            if (this.messageList != null) {
                this.messageList.add(message);
            }
        }
    
}
