package com.capstone.mangbeli.ui

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.capstone.mangbeli.R
import com.capstone.mangbeli.ui.signup.SignUpActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MenuActivityTest {
    @get:Rule
    val activity = ActivityScenarioRule(MenuActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun loadSignUp_Success() {
        Espresso.onView(withId(R.id.btn_register)).perform(ViewActions.click())
        Intents.intended(hasComponent(SignUpActivity::class.java.name))
        Espresso.onView(withId(R.id.tv_title_register)).check(ViewAssertions.matches(isDisplayed()))
    }
}