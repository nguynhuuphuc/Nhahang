package com.example.nhahang.Models;

public class IgnoreEvent {
    private boolean isIgnore ;
    private int conversation_id;
    private String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public IgnoreEvent(int conversation_id, String source,boolean isIgnore) {
        this.isIgnore = isIgnore;
        this.conversation_id = conversation_id;
        this.source = source;
    }

    public IgnoreEvent(int conversation_id, String source) {
        this.conversation_id = conversation_id;
        this.source = source;
        isIgnore = true;
    }

    public IgnoreEvent(int conversation_id) {
        this.conversation_id = conversation_id;
        isIgnore = true;
    }

    public boolean isIgnore() {
        return isIgnore;
    }

    public void setIgnore(boolean ignore) {
        isIgnore = ignore;
    }

    public int getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(int conversation_id) {
        this.conversation_id = conversation_id;
    }
}
