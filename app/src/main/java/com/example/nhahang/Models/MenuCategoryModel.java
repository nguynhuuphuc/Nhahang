package com.example.nhahang.Models;

public class MenuCategoryModel {
    String id;
    String name;
    String documentId;


    public MenuCategoryModel() {
    }

    public MenuCategoryModel(String id, String name) {
        this.id = id;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
