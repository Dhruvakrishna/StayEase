package com.example.stayease
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class LoginUiTest {
  @get:Rule val rule = createAndroidComposeRule<MainActivity>()
  @Test fun shows_sign_in() { rule.onNodeWithText("Sign in").assertIsDisplayed() }
}
