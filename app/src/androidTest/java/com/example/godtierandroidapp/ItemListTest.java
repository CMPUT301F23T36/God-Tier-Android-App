package com.example.godtierandroidapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static org.junit.Assert.*;

import android.widget.TextView;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ItemListTest {
    @Rule
    public ActivityScenarioRule<ItemListView> scenario = new
            ActivityScenarioRule<ItemListView>(ItemListView.class);

    @Before
    public void setUp() {
        scenario.getScenario().onActivity(ItemListView::clearList);
    }
    private void mockUserItem1() {

// Type "Phone" in the description field
        onView(withId(R.id.description_field)).perform(ViewActions.clearText());
        onView(withId(R.id.description_field)).perform(ViewActions.typeText("Phone"));
// Type "150" in the value field
        onView(withId(R.id.estimated_value_field)).perform(ViewActions.clearText());
        onView(withId(R.id.estimated_value_field)).perform(ViewActions.typeText("150"));
// Type "Apple" in the Make field
        onView(withId(R.id.make_field)).perform(ViewActions.clearText());
        onView(withId(R.id.make_field)).perform(ViewActions.typeText("Apple"));
// Type "iPhone XR" in the Model field
        onView(withId(R.id.model_field)).perform(ViewActions.clearText());
        onView(withId(R.id.model_field)).perform(ViewActions.typeText("XR"));
// Type "NDPX86XIKBJY" in the serial no field
        onView(withId(R.id.serial_no_field)).perform(ViewActions.clearText());
        onView(withId(R.id.serial_no_field)).perform(ViewActions.typeText("NDPX86XIKBJY"));
// Click on confirm Item button
        onView(withId(R.id.item_detail_confirm)).perform(click());
    }
    private void mockUserItem2() {

// Type "Phone" in the description field
        onView(withId(R.id.description_field)).perform(ViewActions.clearText());
        onView(withId(R.id.description_field)).perform(ViewActions.typeText("Cell Phone"));
// Type "150" in the value field
        onView(withId(R.id.estimated_value_field)).perform(ViewActions.clearText());
        onView(withId(R.id.estimated_value_field)).perform(ViewActions.typeText("200"));
// Type "Apple" in the Make field
        onView(withId(R.id.make_field)).perform(ViewActions.clearText());
        onView(withId(R.id.make_field)).perform(ViewActions.typeText("Samsung"));
// Type "iPhone XR" in the Model field
        onView(withId(R.id.model_field)).perform(ViewActions.clearText());
        onView(withId(R.id.model_field)).perform(ViewActions.typeText("Galaxy S"));
// Type "NDPX86XIKBJY" in the serial no field
        onView(withId(R.id.serial_no_field)).perform(ViewActions.clearText());
        onView(withId(R.id.serial_no_field)).perform(ViewActions.typeText("BDHFG89BDJHFB"));
// Click on confirm Item button
        onView(withId(R.id.item_detail_confirm)).perform(click());
    }

    @Test
    public void testAddItem() {
// Click on add Item button
        onView(withId(R.id.add_item_button)).perform(click());
// Add details
        mockUserItem1();
// Check if item has been added
        onView(withId(R.id.textViewDescription)).check(matches(withText("Phone")));
        onView(withId(R.id.textViewEstimatedValue)).check(matches(withText("Estimated Value: $150.0")));
    }

    @Test
    public void testEditItem() {
// Click on add Item button
        onView(withId(R.id.add_item_button)).perform(click());
// Add details
        mockUserItem1();
// Click on first item in list
        onView(withId(R.id.recyclerView)) .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
// Change details
        mockUserItem2();
// Check to see that details have changed and remain
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.description_field)).check(matches(withText("Cell Phone")));
        onView(withId(R.id.estimated_value_field)).check(matches(withText("200.0")));
        onView(withId(R.id.make_field)).check(matches(withText("Samsung")));
        onView(withId(R.id.model_field)).check(matches(withText("Galaxy S")));
        onView(withId(R.id.serial_no_field)).check(matches(withText("BDHFG89BDJHFB")));
    }

    @Test
    public void testRemoveItem() {
// Click on add Item button
        onView(withId(R.id.add_item_button)).perform(click());
// Add details
        mockUserItem1();
// Click on first item in list
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
// Click remove item
        onView(withId(R.id.item_detail_delete)).perform(click());
// Check that Item does not exist
        onView(withText("Phone")).check(doesNotExist());
    }
    // Add test for filter and sort
}
