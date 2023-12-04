package com.example.godtierandroidapp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import org.robolectric.RobolectricTestRunner;

import com.example.godtierandroidapp.item.Item;
import com.example.godtierandroidapp.tag.Tag;

@RunWith(RobolectricTestRunner.class)
public class ItemTest {

    private Item testItem;

    @Before
    public void setUp() {
        // Initialize a sample Item object before each test
        testItem = new Item();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(testItem.getDateOfAcquisition());
        assertEquals("", testItem.getDescription());
        assertEquals("", testItem.getMake());
        assertEquals("", testItem.getModel());
        assertEquals("", testItem.getSerialNumber());
        assertEquals(0, testItem.getEstimatedValue(), 0.001);
        assertEquals("", testItem.getComment());
        assertNotNull(testItem.getTags());
        assertNotNull(testItem.photos());
        assertEquals(Color.TRANSPARENT, testItem.getColor());
    }

    @Test
    public void testParameterizedConstructor() {
        Date currentDate = new Date();
        ArrayList<Tag> tags = new ArrayList<>();
        ArrayList<Uri> photos = new ArrayList<>();

        Item parameterizedItem = new Item(
                currentDate, "Test Description", "Test Make", "Test Model",
                "Test Serial", 100.0, "Test Comment", tags, photos
        );

        assertEquals(currentDate, parameterizedItem.getDateOfAcquisition());
        assertEquals("Test Description", parameterizedItem.getDescription());
        assertEquals("Test Make", parameterizedItem.getMake());
        assertEquals("Test Model", parameterizedItem.getModel());
        assertEquals("Test Serial", parameterizedItem.getSerialNumber());
        assertEquals(100.0, parameterizedItem.getEstimatedValue(), 0.001);
        assertEquals("Test Comment", parameterizedItem.getComment());
        assertEquals(tags, parameterizedItem.getTags());
        assertEquals(photos, parameterizedItem.photos());
    }

    @Test
    public void testAddPhoto() {
        Uri testUri = Uri.parse("test_uri");
        testItem.addPhoto(testUri);
        assertTrue(testItem.photos().contains(testUri));
    }

    @Test
    public void testGetUriStrings() {
        ArrayList<Uri> testUris = new ArrayList<>();
        testUris.add(Uri.parse("uri1"));
        testUris.add(Uri.parse("uri2"));
        testItem.photosSet(testUris);

        ArrayList<String> uriStrings = testItem.getUriStrings();
        assertEquals("uri1", uriStrings.get(0));
        assertEquals("uri2", uriStrings.get(1));
    }

    // Add more tests for other methods as needed...

    @Test
    public void testAddTag() {
        Tag testTag = new Tag("TestTag");
        testItem.addTag(testTag);
        assertTrue(testItem.getTags().contains(testTag));
    }

    @Test
    public void testSetTags() {
        List<Tag> testTags = new ArrayList<>();
        testTags.add(new Tag("Tag1"));
        testTags.add(new Tag("Tag2"));
        testItem.setTags(testTags);
        assertEquals(testTags, testItem.getTags());
    }

    @Test
    public void testRemoveTag() {
        Tag testTag = new Tag("ToRemove");
        testItem.addTag(testTag);
        testItem.removeTag(testTag);
        assertFalse(testItem.getTags().contains(testTag));
    }

    @Test
    public void testGetTagCount() {
        testItem.addTag(new Tag("Tag1"));
        testItem.addTag(new Tag("Tag2"));
        assertEquals(2, testItem.getTagCount());
    }

    @Test
    public void testHasTag() {
        Tag testTag = new Tag("TestTag");
        testItem.addTag(testTag);
        assertTrue(testItem.hasTag(testTag));
    }

    @Test
    public void testSerialization() {
        testItem.setDescription("Test Description");
        testItem.setEstimatedValue(50.0);
        testItem.addTag(new Tag("Tag1"));
        testItem.addTag(new Tag("Tag2"));
        testItem.addPhoto(Uri.parse("test_uri"));

        try {
            // Serialize the object
            byte[] serializedItem = serialize(testItem);

            // Deserialize the object
            Item deserializedItem = deserialize(serializedItem);


            // Check if the deserialized object is equal to the original object
            assertEquals("Date of Acquisition", testItem.getDateOfAcquisition(), deserializedItem.getDateOfAcquisition());
            assertEquals("Description", testItem.getDescription(), deserializedItem.getDescription());
            assertEquals("Make", testItem.getMake(), deserializedItem.getMake());
            assertEquals("Model", testItem.getModel(), deserializedItem.getModel());
            assertEquals("Serial Number", testItem.getSerialNumber(), deserializedItem.getSerialNumber());
            assertEquals("Estimated Value", testItem.getEstimatedValue(), deserializedItem.getEstimatedValue(), 0.001);
            assertEquals("Comment", testItem.getComment(), deserializedItem.getComment());
            assertEquals("Tags", testItem.getTags(), deserializedItem.getTags());
            assertEquals("Color", testItem.getColor(), deserializedItem.getColor());
            assertEquals("Photo URI", testItem.getUriStrings(), deserializedItem.getUriStrings());
        } catch (Exception e) {
            fail("Serialization/deserialization failed: " + e.getMessage());
        }
    }

    private byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    private <T> T deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        //noinspection unchecked
        return (T) objectInputStream.readObject();
    }

    @Test
    public void testGetterAndSetterMethods() {
        Date currentDate = new Date();
        testItem.setDateOfAcquisition(currentDate);
        assertEquals(currentDate, testItem.getDateOfAcquisition());

        testItem.setDescription("Test Description");
        assertEquals("Test Description", testItem.getDescription());

        testItem.setMake("Test Make");
        assertEquals("Test Make", testItem.getMake());

        testItem.setModel("Test Model");
        assertEquals("Test Model", testItem.getModel());

        testItem.setSerialNumber("Test Serial");
        assertEquals("Test Serial", testItem.getSerialNumber());

        testItem.setEstimatedValue(100.0);
        assertEquals(100.0, testItem.getEstimatedValue(), 0.001);

        testItem.setComment("Test Comment");
        assertEquals("Test Comment", testItem.getComment());
    }

    @Test
    public void testAddPhotoAndGetUriStrings() {
        Uri testUri1 = Uri.parse("test_uri1");
        Uri testUri2 = Uri.parse("test_uri2");

        testItem.addPhoto(testUri1);
        testItem.addPhoto(testUri2);

        assertTrue(testItem.photos().contains(testUri1));
        assertTrue(testItem.photos().contains(testUri2));

        ArrayList<String> uriStrings = testItem.getUriStrings();
        assertEquals("test_uri1", uriStrings.get(0));
        assertEquals("test_uri2", uriStrings.get(1));
    }

    @Test
    public void testSetUriStrings() {
        ArrayList<String> uriStrings = new ArrayList<>();
        uriStrings.add("uri1");
        uriStrings.add("uri2");

        testItem.setUriStrings(uriStrings);

        ArrayList<Uri> photos = testItem.photos();
        assertEquals(Uri.parse("uri1"), photos.get(0));
        assertEquals(Uri.parse("uri2"), photos.get(1));
    }

    @Test
    public void testHashMapConstructor() throws Exception {
        HashMap<String, Object> itemMap = new HashMap<>();
        itemMap.put("uriStrings", new ArrayList<String>() {{
            add("uri1");
            add("uri2");
        }});
        itemMap.put("estimatedValue", 75.0);
        itemMap.put("serialNumber", "SN123");
        itemMap.put("description", "Test Description");
        itemMap.put("comment", "Test Comment");
        itemMap.put("model", "Test Model");
        itemMap.put("tagCount", 2L);

        ArrayList<HashMap<String, Object>> tagsList = new ArrayList<>();
        tagsList.add(new HashMap<String, Object>() {{
            put("name", "Tag1");
        }});
        tagsList.add(new HashMap<String, Object>() {{
            put("name", "Tag2");
        }});
        itemMap.put("tags", tagsList);

        HashMap<String, Object> dateAq = new HashMap<>();
        dateAq.put("date", 1L);
        dateAq.put("hours", 12L);
        dateAq.put("seconds", 30L);
        dateAq.put("month", 0L); // Month is 0-based in Java Date
        dateAq.put("year", 2023L);
        dateAq.put("minutes", 45L);
        itemMap.put("dateOfAcquisition", dateAq);

        itemMap.put("make", "Test Make");

        Item newItem = new Item(itemMap);

        assertEquals("SN123", newItem.getSerialNumber());
        assertEquals("Test Description", newItem.getDescription());
        assertEquals("Test Comment", newItem.getComment());
        assertEquals("Test Model", newItem.getModel());
        assertEquals(75.0, newItem.getEstimatedValue(), 0.001);
        assertEquals("Test Make", newItem.getMake());

        assertEquals(2, newItem.getTagCount());
        assertTrue(newItem.hasTag(new Tag("Tag1")));
        assertTrue(newItem.hasTag(new Tag("Tag2")));

        assertEquals("uri1", newItem.getUriStrings().get(0));
        assertEquals("uri2", newItem.getUriStrings().get(1));
    }

    @Test
    public void testGetTags() {
        List<Tag> testTags = new ArrayList<>();
        testTags.add(new Tag("Tag1"));
        testTags.add(new Tag("Tag2"));
        testItem.setTags(testTags);
        assertEquals(testTags, testItem.getTags());
    }
}
