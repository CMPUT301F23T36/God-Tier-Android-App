package com.example.godtierandroidapp;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

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
    private ArrayList<Bitmap> photo;

    public Item() {
        this.dateOfAcquisition = new Date();
        this.description = "";
        this.make = "";
        this.model = "";
        this.serialNumber = "";
        this.estimatedValue = 0;
        this.comment = "";
        this.tags = new ArrayList<>();
        this.photo = new ArrayList<>(4);
    }

    // Constructor
    public Item(
        Date dateOfAcquisition,
        String description,
        String make,
        String model,
        String serialNumber,
        double estimatedValue,
        String comment,
        List<Tag> tags
    ) {
        this.dateOfAcquisition = dateOfAcquisition;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estimatedValue = estimatedValue;
        this.comment = comment;
        this.tags = tags;
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
        //this.photo = new ArrayList<>();
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public List<Tag> getTags() {
        return tags;
    }

    public int getTagCount() { return tags.size(); }

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

    public ArrayList<Bitmap> getPhoto() { return photo; }

    public void setPhoto(ArrayList<Bitmap> photo) { this.photo = photo; }

    public void addPhoto(Bitmap photo) { this.photo.add(photo); }

    public Bitmap getPhoto(int index) {
        try {
            Log.d("GET PHOTO", "Retrieving photo at index" + index);
            return photo.get(index);
        } catch (Exception e) {
            return null;
            // Toast.makeText(this, "Failure: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}