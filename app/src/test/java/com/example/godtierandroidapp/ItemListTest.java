package com.example.godtierandroidapp;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;

public class ItemListTest {
    private ItemList itemList;
    private ItemList mockItemList(){
        ItemList itemList = new ItemList();
        itemList.addItem(mockItem());
        return itemList;
    }

    private Item mockItem(){
        return new Item(new Date(), "Item", "Make", "Model", "SN", 123.0, "Comment", Collections.emptyList(), Collections.emptyList());
    }

    @Before
    public void setUp() {
        itemList = mockItemList();
    }

    @Test
    public void testAddItem() {
        Item item1 = new Item(new Date(), "Item1", "Make1", "Model1", "SN1", 321.0, "Comment1", Collections.emptyList(), Collections.emptyList());
        Item item2 = new Item(new Date(), "Item2", "Make2", "Model2", "SN2", 132.0, "Comment2", Collections.emptyList(), Collections.emptyList());
        itemList.addItem(item1);
        itemList.addItem(item2);
        assertEquals(3, itemList.size());
        assertEquals(item1, itemList.getItem(1));
        assertEquals(item2, itemList.getItem(2));
    }

    @Test
    public void testUpdateItem() {
        Item updatedItem = new Item(new Date(), "UpdatedItem", "UpdatedMake", "UpdatedModel", "UpdatedSN", 200.0, "UpdatedComment", Collections.emptyList(), Collections.emptyList());
        itemList.updateItem(0, updatedItem);
        assertEquals(updatedItem, itemList.getItem(0));
    }

    @Test
    public void testRemoveItem() {
        Item item1 = new Item(new Date(), "Item1", "Make1", "Model1", "SN1", 321.0, "Comment1", Collections.emptyList(), Collections.emptyList());
        itemList.addItem(item1);
        assertEquals(2, itemList.size());
        itemList.removeItem(item1);
        assertEquals(1, itemList.size());
        assertFalse(itemList.getItems().contains(item1));
    }
}
