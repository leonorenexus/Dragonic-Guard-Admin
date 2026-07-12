package com.dragonic.guardparent.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.dragonic.guardparent.ui.theme.*
import com.dragonic.guardparent.viewmodel.ParentViewModel

class MainActivity : ComponentActivity() {
    private val vm: ParentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DRAGONICParentTheme {
                ParentApp(vm)
            }
        }
    }
}

@Composable
fun ParentApp(vm: ParentViewModel) {
    val navController = rememberNavController()
    val navItems = listOf(
        Triple("monitor", Icons.Filled.Shield, "Monitor"),
        Triple("apps", Icons.Filled.Apps, "Aplikasi"),
        Triple("usage", Icons.Filled.BarChart, "Pemakaian"),
        Triple("devices", Icons.Filled.DevicesOther, "Perangkat"),
    )

    Scaffold(
        containerColor = PBlack,
        bottomBar = {
            NavigationBar(containerColor = PDeepBlue.copy(alpha = 0.95f), tonalElevation = 0.dp) {
                val entry by navController.currentBackStackEntryAsState()
                val current = entry?.destination
                navItems.forEach { (route, icon, label) ->
                    val sel = current?.hierarchy?.any { it.route == route } == true
                    NavigationBarItem(
                        selected = sel,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, null, tint = if (sel) PCyan else PWhiteDim.copy(0.5f)) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall,
                            color = if (sel) PCyan else PWhiteDim.copy(0.5f)) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = PCyan.copy(0.15f))
                    )
                }
            }
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(PBlack, PDeepBlue, PBlack)))
                .padding(padding)
        ) {
            NavHost(navController, startDestination = "monitor") {
                composable("monitor")  { MonitorScreen(vm) }
                composable("apps")     { AppsScreen(vm) }
                composable("usage")    { UsageScreen(vm) }
                composable("devices")  { DevicesScreen(vm) }
            }
        }
    }
}
