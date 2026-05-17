package com.example.madhumargaagriculture

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.madhumargaagriculture.auth.FirebaseAuthManager
import com.example.madhumargaagriculture.viewmodel.LogViewModel
import com.example.madhumargaagriculture.viewmodel.HiveViewModel
import com.example.madhumargaagriculture.viewmodel.HarvestViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Brush

@Composable
fun DashboardScreen(
    viewModel: LogViewModel,
    hiveViewModel: HiveViewModel,
    harvestViewModel: HarvestViewModel,
    navController: NavHostController
) {
    val authManager = remember { FirebaseAuthManager() }
    val logs by viewModel.logs.collectAsState(initial = emptyList())
    val hives by hiveViewModel.allHives.collectAsState(initial = emptyList())
    val harvests by harvestViewModel.allHarvests.collectAsState(initial = emptyList())

    val visible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible.value = true
    }

    AnimatedVisibility(
        visible = visible.value,
        enter = fadeIn(animationSpec = tween(600)) +
                slideInVertically(initialOffsetY = { it / 2 }),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE8F5E9), Color.White)
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            // TOP GREETING
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dashboard",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "Hello, Beekeeper!",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                IconButton(onClick = { 
                    authManager.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // STATISTICS CARDS
            Text(
                text = "Key Statistics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedStatCard("Total Hives", hives.size)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedStatCard("Yield (kg)", harvests.sumOf { it.quantity }.toInt())
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedStatCard("Active Hives", hives.size) // Can be updated with real logic
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedStatCard("Alerts", 2) // Dummy for now
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // RECENT ACTIVITIES
            Text(
                text = "Recent Activities",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            if (logs.isEmpty()) {
                Text("No recent activities", fontSize = 14.sp, color = Color.Gray)
            } else {
                logs.take(3).forEach { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(log.logText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text(formatDate(log.timestamp), fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ALERTS
            Text(
                text = "Priority Alerts",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(8.dp))
            AlertCard(
                title = "Queen Missing",
                message = "Hive #3 requires inspection immediately."
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun AlertCard(
    title: String,
    message: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "⚠ $title",
                color = Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun AnimatedStatCard(title: String, value: Int) {

    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 800),
        label = "countUp",
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = animatedValue.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
        }
    }
}

