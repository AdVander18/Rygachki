package com.example.rygachki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rygachki.data.CounterStore
import com.example.rygachki.ui.screens.HomeScreen
import com.example.rygachki.ui.screens.StatsScreen
import com.example.rygachki.ui.theme.RygachkiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RygachkiTheme {
                val context = LocalContext.current
                val counterStore = remember(context) { CounterStore(context.applicationContext) }
                RygachkiApp(counterStore)
            }
        }
    }
}

@Composable
private fun RygachkiApp(counterStore: CounterStore) {
    val navController = rememberNavController()

    Surface(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = Routes.HOME) {
            composable(Routes.HOME) {
                HomeScreen(
                    counterStore = counterStore,
                    onOpenStats = { navController.navigate(Routes.STATS) }
                )
            }
            composable(Routes.STATS) {
                StatsScreen(
                    counterStore = counterStore,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

private object Routes {
    const val HOME = "home"
    const val STATS = "stats"
}
