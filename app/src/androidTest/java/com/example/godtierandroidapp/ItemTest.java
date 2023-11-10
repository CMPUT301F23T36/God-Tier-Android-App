package com.example.godtierandroidapp;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ItemTest {

    private Item item;

    @Before
    public void setUp() {
        item = new Item();
    }

    @Test
    public void testAddTag() {
        Tag tag = new Tag("TestTag");
        item.addTag(tag);

        assertTrue(item.hasTag(tag));
        assertEquals(1, item.getTags().size());
        assertEquals(tag, item.getTags().get(0));
    }

    @Test
    public void testConstructorWithDescriptionAndTags() {
        String description = "TestDescription";
        double estimatedValue = 50.0;
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("Tag1"));
        tags.add(new Tag("Tag2"));

        Item newItem = new Item(description, estimatedValue, tags);

        assertEquals(description, newItem.getDescription());
        assertEquals(estimatedValue, newItem.getEstimatedValue(), 0.01);
        assertEquals(tags, newItem.getTags());
    }

    // can add tests for getters and setters
}

