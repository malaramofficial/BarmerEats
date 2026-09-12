package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AuthState
import com.example.ui.admin.AdminPanel
import com.example.ui.components.*
import com.example.ui.customer.CustomerApp
import com.example.ui.restaurant.RestaurantPanel
import com.example.ui.rider.RiderApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.FoodDeliveryViewModel
import com.example.viewmodel.FoodDeliveryViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainOrchestrator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainOrchestrator() {
    val context = LocalContext.current
    val application = context.applicationContext as android.app.Application
    val viewModel: FoodDeliveryViewModel = viewModel(
        factory = FoodDeliveryViewModelFactory(application)
    )

    val currentRole by viewModel.currentRole.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val authState by viewModel.authState.collectAsState()

    var showNotificationsSheet by remember { mutableStateOf(false) }

    if (authState !is AuthState.Authenticated) {
        LoginScreen(viewModel)
    } else {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .background(DeepCrimson)
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SandyGold, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Moped, "BarmerEats", tint = DeepCrimson)
                            }
                            Column {
                                Text(
                                    "BarmerEats",
                                    color = SandyGold,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Local Food. Local Delivery.",
                                    color = SandyGold.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.logoutUser() },
                                modifier = Modifier.testTag("logout_button")
                            ) {
                                Icon(Icons.Default.ExitToApp, "Sign Out", tint = SandyGold)
                            }
                            IconButton(
                                onClick = { showNotificationsSheet = true },
                                modifier = Modifier.testTag("notification_bell")
                            ) {
                                BadgedBox(badge = {
                                    if (notifications.isNotEmpty()) {
                                        Badge(containerColor = DesertOrange) {
                                            Text(notifications.size.toString(), color = Color.White)
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Notifications, "Notifications", tint = SandyGold)
                                }
                            }
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
            ) {
                when (currentRole) {
                    "CUSTOMER" -> CustomerApp(viewModel)
                    "RESTAURANT" -> RestaurantPanel(viewModel)
                    "RIDER" -> RiderApp(viewModel)
                    "ADMIN" -> AdminPanel(viewModel)
                }

                if (showNotificationsSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showNotificationsSheet = false },
                        containerColor = SandyGold
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "System Notifications",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepCrimson
                                )
                                IconButton(onClick = { showNotificationsSheet = false }) {
                                    Icon(Icons.Default.Close, "Close")
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            if (notifications.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No notifications yet.", color = Color.Gray)
                                }
                            } else {
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(notifications) { notif ->
                                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp)
                                            ) {
                                                Text(
                                                    notif.title,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = DeepCrimson
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    notif.message,
                                                    fontSize = 12.sp,
                                                    color = CharcoalGray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
