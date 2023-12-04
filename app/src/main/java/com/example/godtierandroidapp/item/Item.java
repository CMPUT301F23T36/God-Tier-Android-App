package com.example.godtierandroidapp.item;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import com.example.godtierandroidapp.tag.Tag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Represents an item in the user's list.
 * Contains a date of acquisition, description, make, model, serial number, estimated value,
 * comment, a list of tags, and a list of photos.
 *
 * @author Alex, Boris, Travis, George, Vinayan
 */
public class Item implements Serializable {
    private Date dateOfAcquisition;
    private String description;
    private String make;
    private String model;
    private String serialNumber;
    private double estimatedValue;
    private String comment;
    private List<Tag> tags;
    private int color = Color.TRANSPARENT;
    public transient ArrayList<Uri> photo;

    /**
     * Default constructor.
     */
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

    /**
     * Full constructor.
     */
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

    /**
     * Constructor for common values for testing purposes.
     */
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

    /**
     * Constructor for the Item Hashmap returned by Firebase.
     */
    public Item(HashMap map) {
        photo = new ArrayList<>();
        ArrayList<String> uris = (ArrayList<String>) map.get("uriStrings");
        if (uris != null) {
            for (String uri : uris) {
                photo.add(Uri.parse(uri));
            }
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

    /**
     * Simple serialization writer. Photo URIs are converted to strings before being serialized.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        ArrayList<String> uriStrings = new ArrayList<>();
        for (Uri uri : photo) {
            Log.d("uri", uri.toString());
            uriStrings.add(uri.toString());
        }
        out.writeObject(uriStrings);
    }

    /**
     * Simple serialization reader. Photo URIs are parsed from deserialized strings.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        photo = new ArrayList<>();

        ArrayList<String> uriStrings = (ArrayList<String>) in.readObject();
        for (String uriString : uriStrings) {
            try {
                photo.add(Uri.parse(uriString));
            } catch (Exception e) {}
        }
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
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

    /**
     * Not a `get` method to prevent firebase from trying to directly serialize these URIs.
     */
    public ArrayList<Uri> photos() {
        return photo;
    }

    /**
     * Not a `set` method to prevent firebase from trying to directly deserialize URIs.
     */
    public void photosSet(ArrayList<Uri> photo) {
        this.photo = photo;
    }
  
    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void setTags(List<Tag> newTags) {
        tags = newTags;
    }
    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public List<Tag> getTags() {
        return tags;
    }

    public int getTagCount() {
        return tags.size();
    }

    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }

    public Date getDateOfAcquisition() {
        return dateOfAcquisition;
    }

    public void setDateOfAcquisition(Date dateOfAcquisition) {
        this.dateOfAcquisition = dateOfAcquisition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

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