package com.example.nhahang.Models;

import java.io.Serializable;

public class TableModel implements Serializable {
    String documentId;
    String id;
    String description;
    String status;
    String location;

    public TableModel() {
    }

    public TableModel(String id, String description, String status, String location) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.location = location;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
