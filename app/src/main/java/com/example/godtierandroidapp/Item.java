package com.example.godtierandroidapp;



import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Item implements Serializable {
    private Date dateOfAcquisition;
    private String description;
    private String make;
    private String model;
    private String serialNumber;
    private double estimatedValue;
    private String comment;
    private ArrayList<Tag> tags;
    public transient ArrayList<Uri> photo;

    public Item() {
        this.dateOfAcquisition = new Date();
        this.description = "";
        this.make = "";
        this.model = "";
        this.serialNumber = "";
        this.estimatedValue = 0;
        this.comment = "";
        this.tags = new ArrayList<>();
        this.photo = new ArrayList<>();
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
        ArrayList<Tag> tags,
        ArrayList<Uri> photo
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
        ArrayList<Tag> tags
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

    // database returns data in hashmap format
    public Item(HashMap map) {
        photo = new ArrayList<>();
        ArrayList<String> uris = (ArrayList<String>) map.get("uriStrings");
        for (String uri : uris) {
            photo.add(Uri.parse(uri));
        }

        Log.d("ITEM", String.valueOf(map));
        estimatedValue = ((Number) map.get("estimatedValue")).doubleValue();
        serialNumber = (String) map.get("serialNumber");
        description = (String) map.get("description");
        comment = (String) map.get("comment");
        model = (String) map.get("model");

        tags = new ArrayList<>();
        long tagCount = (long) map.get("tagCount");
        if (tagCount != 0) {
            ArrayList tagsMap = (ArrayList) map.get("tags");
            for (int i = 0; i < tagCount; ++i) {
                HashMap tagsStruct = (HashMap) tagsMap.get(i);
                tags.add(new Tag((String) tagsStruct.get("name")));
            }
        }

        HashMap<String, Object> dateAq = (HashMap<String, Object>) map.get("dateOfAcquisition");
        long date = (long) dateAq.get("date");
        long hours = (long) dateAq.get("hours");
        long seconds = (long) dateAq.get("seconds");
        long month = (long) dateAq.get("month");
        long year = (long) dateAq.get("year");
        long minutes = (long) dateAq.get("minutes");

        dateOfAcquisition = new Date((int)year, (int)month, (int)date,
                (int)hours, (int)minutes, (int)seconds);
        make = (String) map.get("make");
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        // Convert Uri objects to strings and store them
        ArrayList<byte[]> uriStrings = new ArrayList<>();
        for (Uri uri : photo) {
            if (uri != null) {
                Log.d("uri", uri.toString());
                uriStrings.add(uri.toString().getBytes());
            } else { continue; }
        }
        out.writeObject(uriStrings);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        // Initialize the transient field after deserialization
        photo = new ArrayList<>();

        // Reconstruct Uri objects from stored strings
        ArrayList<byte[]> uriStrings = (ArrayList<byte[]>) in.readObject();
        for (byte[] uriString : uriStrings) {
            try {
                if (uriString != null) {
                    photo.add(Uri.parse(uriString.toString()));
                } else {
                    continue;
                }
            }
            catch (Exception e) {}
        }
        //photo = (ArrayList<Uri>) in.readObject();
    }

    public void addPhoto(Uri newPhoto) {
        if (photo == null) {
            photo = new ArrayList<>();
        }
        photo.add(newPhoto);
    }

    public ArrayList<String> getUriStrings() {
        ArrayList<String> uris = new ArrayList<>();
        for (Uri uri : photo) {
            uris.add(uri.toString());
        }
        return uris;
    }

    public void setUriStrings(ArrayList<String> uris) {
        photo = new ArrayList<>();
        for (String uri : uris) {
            photo.add(Uri.parse(uri));
        }
    }

    public ArrayList<Uri> photos() {
        return photo;
    }
    public void photosSet(ArrayList<Uri> photo) {
        this.photo = photo;
    }
    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public ArrayList<Tag> getTags() {
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
}