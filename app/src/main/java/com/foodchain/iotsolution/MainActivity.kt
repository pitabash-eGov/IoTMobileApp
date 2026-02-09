package com.foodchain.iotsolution

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.foodchain.iotsolution.data.local.DataStoreManager
import com.foodchain.iotsolution.presentation.navigation.NavGraph
import com.foodchain.iotsolution.ui.theme.IoTSolutionTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by dataStoreManager.isDarkTheme.collectAsState(initial = false)
            IoTSolutionTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
