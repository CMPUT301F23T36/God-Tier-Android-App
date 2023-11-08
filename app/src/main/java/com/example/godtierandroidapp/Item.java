package com.example.godtierandroidapp;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Item implements Serializable {
    private Date dateOfAcquisition;
    private String description;
    private String make;
    private String model;
    private String serialNumber;
    private double estimatedValue;
    private String comment;
    private List<Tag> tags;
    private List<Bitmap> photo;

    // Constructor
    public Item(
        Date dateOfAcquisition,
        String description,
        String make,
        String model,
        String serialNumber,
        double estimatedValue,
        String comment,
        List<Tag> tags,
        List<Bitmap> photo
    ) {
        this.dateOfAcquisition = dateOfAcquisition;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estimatedValue = estimatedValue;
        this.comment = comment;
        this.tags = tags;
        this.photo = photo;
    }

    public Item(
        String description,
        double estimatedValue,
        List<Tag> tags
    ) {
        this.dateOfAcquisition = new Date();
        this.description = description;
        this.make = "";
        this.model = "";
        this.serialNumber = "";
        this.estimatedValue = estimatedValue;
        this.comment = "";
        this.tags = tags;
        this.photo = new ArrayList<>();
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public List<Tag> getTags() {
        return tags;
    }

    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }

    // Getter and Setter methods for dateOfAcquisition
    public Date getDateOfAcquisition() {
        return dateOfAcquisition;
    }

    public void setDateOfAcquisition(Date dateOfAcquisition) {
        this.dateOfAcquisition = dateOfAcquisition;
    }

    // Getter and Setter methods for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and Setter methods for make
    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    // Getter and Setter methods for model
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    // Getter and Setter methods for serialNumber
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    // Getter and Setter methods for estimatedValue
    public double getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(double estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    // Getter and Setter methods for comment
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}