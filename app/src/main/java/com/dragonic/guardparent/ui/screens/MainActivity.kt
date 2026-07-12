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
import androidx.compose.ui.graphics.vector.ImageVector
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

data class NavItem(val route: String, val icon: ImageVector, val label: String)

@Composable
fun ParentApp(vm: ParentViewModel) {
    val navController = rememberNavController()
    val navItems = listOf(
        NavItem("monitor", Icons.Filled.Shield, "Monitor"),
        NavItem("apps", Icons.Filled.Apps, "Aplikasi"),
        NavItem("usage", Icons.Filled.BarChart, "Pemakaian"),
        NavItem("devices", Icons.Filled.DevicesOther, "Perangkat"),
    )

    Scaffold(
        containerColor = PBlack,
        bottomBar = {
            NavigationBar(containerColor = PDeepBlue.copy(alpha = 0.95f), tonalElevation = 0.dp) {
                val entry by navController.currentBackStackEntryAsState()
                val currentRoute = entry?.destination?.route
                navItems.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(item.icon, null,
                                tint = if (selected) PCyan else PWhiteDim.copy(0.5f))
                        },
                        label = {
                            Text(item.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selected) PCyan else PWhiteDim.copy(0.5f))
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = PCyan.copy(0.15f)
                        )
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
