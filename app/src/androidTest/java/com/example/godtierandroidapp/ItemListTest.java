package com.example.godtierandroidapp;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static org.junit.Assert.*;

public class ItemListTest {

    private ItemList itemList;
    private Item item1;
    private Item item2;

    @Before
    public void setUp() {
        itemList = new ItemList();
        item1 = new Item(new Date(), "Item1", "Make1", "Model1", "SN1", 100.0, "Comment1", Collections.emptyList(), Collections.emptyList());
        item2 = new Item(new Date(), "Item2", "Make2", "Model2", "SN2", 150.0, "Comment2", Collections.emptyList(), Collections.emptyList());
    }

    @Test
    public void testAddItem() {
        itemList.addItem(item1);
        itemList.addItem(item2);

        assertEquals(2, itemList.size());
        assertEquals(item1, itemList.getItem(0));
        assertEquals(item2, itemList.getItem(1));
    }

    @Test
    public void testUpdateItem() {
        itemList.addItem(item1);
        itemList.addItem(item2);

        Item updatedItem = new Item(new Date(), "UpdatedItem", "UpdatedMake", "UpdatedModel", "UpdatedSN", 200.0, "UpdatedComment", Collections.emptyList(), Collections.emptyList());
        itemList.updateItem(0, updatedItem);

        assertEquals(updatedItem, itemList.getItem(0));
    }

    @Test
    public void testRemoveItem() {
        itemList.addItem(item1);
        itemList.addItem(item2);

        itemList.removeItem(item1);

        assertEquals(1, itemList.size());
        assertEquals(item2, itemList.getItem(0));
    }

    // Add test for filter and sort
}
