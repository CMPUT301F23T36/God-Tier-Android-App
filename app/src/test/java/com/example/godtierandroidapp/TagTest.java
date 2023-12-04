package com.example.godtierandroidapp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.example.godtierandroidapp.tag.Tag;

@RunWith(RobolectricTestRunner.class)
public class TagTest {

    private Tag tag;

    @Before
    public void setup() {
        tag = new Tag("TestTag");
    }

    @Test
    public void testCompareTo() {
        Tag sameTag = new Tag("TestTag");
        Tag differentTag = new Tag("DifferentTag");

        assertEquals(0, tag.compareTo(sameTag));
        assertTrue(tag.compareTo(differentTag) > 0);
        assertTrue(differentTag.compareTo(tag) < 0);
    }

    @Test
    public void testEquals() {
        Tag sameTag = new Tag("TestTag");
        Tag differentTag = new Tag("DifferentTag");
        Object notATag = new Object();

        assertEquals(tag, sameTag);
        assertNotEquals(tag, differentTag);
        assertNotEquals(tag, notATag);
    }

    @Test
    public void testGetName() {
        assertEquals("TestTag", tag.getName());
    }

    @Test
    public void testSetName() {
        tag.setName("NewName");
        assertEquals("NewName", tag.getName());
    }

    @Test
    public void testConstructor() {
        assertEquals("TestTag", tag.getName());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        objectOutputStream.writeObject(tag);
        objectOutputStream.close();

        byte[] serializedTag = outputStream.toByteArray();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedTag);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        Tag deserializedTag = (Tag) objectInputStream.readObject();
        objectInputStream.close();

        assertEquals(tag, deserializedTag);
    }
}
