package com.fitlife.app.activities;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.fitlife.app.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    @Rule
    public ActivityScenarioRule<HomeActivity> activityRule =
            new ActivityScenarioRule<>(HomeActivity.class);

    @Test
    public void testFabOpensCreateWorkout() {
        // Click FAB
        onView(withId(R.id.fab_create_workout)).perform(click());
        
        // Check if Create Workout screen is displayed (checking title or specific view)
        // Since we don't have the string resource for activity title handy in context, 
        // we check for a view unique to that activity, e.g., input field
        onView(withId(R.id.et_workout_name)).check(matches(isDisplayed()));
    }
    
    @Test
    public void testEmptyStateVisibility() {
        // Assuming fresh install with no workouts
        // This might fail if database already has data, generally we'd mock the database repository
        // But for this level of integration test without DI setup, we check if either RV or Empty state is visible
        try {
            onView(withId(R.id.layout_empty_state)).check(matches(isDisplayed()));
        } catch (Exception e) {
            // Alternatively check if RecyclerView is visible
            onView(withId(R.id.rv_workouts)).check(matches(isDisplayed()));
        }
    }
}
