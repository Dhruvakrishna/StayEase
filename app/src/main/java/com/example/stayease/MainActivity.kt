package com.example.stayease
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.example.stayease.ui.StayEaseTheme
import com.example.stayease.ui.nav.StayEaseNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { StayEaseTheme { Surface { StayEaseNavHost() } } }
  }
}
