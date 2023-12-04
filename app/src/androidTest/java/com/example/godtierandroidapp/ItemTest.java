package com.example.godtierandroidapp;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
//public class ItemTest {
//
//    private Item item;
//
//    @Before
//    public void setUp() {
//        item = new Item();
//    }
//
//    @Test
//    public void testAddTag() {
//        Tag tag = new Tag("TestTag");
//        item.addTag(tag);
//
//        assertTrue(item.hasTag(tag));
//        assertEquals(1, item.getTags().size());
//        assertEquals(tag, item.getTags().get(0));
//    }
//
//    @Test
//    public void testConstructorWithDescriptionAndTags() {
//        String description = "TestDescription";
//        double estimatedValue = 50.0;
//        List<Tag> tags = new ArrayList<>();
//        tags.add(new Tag("Tag1"));
//        tags.add(new Tag("Tag2"));
//
//        Item newItem = new Item(description, estimatedValue, tags);
//
//        assertEquals(description, newItem.getDescription());
//        assertEquals(estimatedValue, newItem.getEstimatedValue(), 0.01);
//        assertEquals(tags, newItem.getTags());
//    }
//
//    // can add tests for getters and setters
//}
//

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

public class ItemTest {

    private Item item;

    @Before
    public void setUp() {
        // Initialize a sample Item for testing
        item = new Item();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(item.getDateOfAcquisition());
        assertEquals("", item.getDescription());
        assertEquals("", item.getMake());
        assertEquals("", item.getModel());
        assertEquals("", item.getSerialNumber());
        assertEquals(0, item.getEstimatedValue(), 0.001);
        assertEquals("", item.getComment());
        assertEquals(0, item.getTags().size());
        assertEquals(0, item.photos().size());
    }

    @Test
    public void testParameterizedConstructor() {
        Date acquisitionDate = new Date();
        String description = "Test Item";
        String make = "Test Make";
        String model = "Test Model";
        String serialNumber = "Test Serial";
        double estimatedValue = 100.0;
        String comment = "Test Comment";
        ArrayList<Tag> tags = new ArrayList<>();
        ArrayList<Uri> photos = new ArrayList<>();

        Item customItem = new Item(
                acquisitionDate, description, make, model, serialNumber,
                estimatedValue, comment, tags, photos
        );

        assertEquals(acquisitionDate, customItem.getDateOfAcquisition());
        assertEquals(description, customItem.getDescription());
        assertEquals(make, customItem.getMake());
        assertEquals(model, customItem.getModel());
        assertEquals(serialNumber, customItem.getSerialNumber());
        assertEquals(estimatedValue, customItem.getEstimatedValue(), 0.001);
        assertEquals(comment, customItem.getComment());
        assertEquals(tags, customItem.getTags());
        assertEquals(photos, customItem.photos());
    }

    @Test
    public void testAddPhoto() {
        Uri photoUri = Uri.parse("content://test/photo");
        item.addPhoto(photoUri);

        assertEquals(1, item.photos().size());
        assertTrue(item.photos().contains(photoUri));
    }

    @Test
    public void testAddTag() {
        Tag tag = new Tag("TestTag");
        item.addTag(tag);

        assertEquals(1, item.getTags().size());
        assertTrue(item.getTags().contains(tag));
    }

    @Test
    public void testGetUriStrings() {
        Uri photoUri = Uri.parse("content://test/photo");
        item.addPhoto(photoUri);

        ArrayList<String> uriStrings = item.getUriStrings();

        assertEquals(1, uriStrings.size());
        assertEquals(photoUri.toString(), uriStrings.get(0));
    }

    // Add more tests based on your specific requirements and methods in the Item class
}

