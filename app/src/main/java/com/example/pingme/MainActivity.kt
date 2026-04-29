package com.example.pingme

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pingme.ui.theme.PingMeTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import android.Manifest



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        setContent {
            PingMeTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }

    @Composable
    fun AppNavigation(navController: NavHostController) {

        var events by remember {
            mutableStateOf<List<EventItem>>(emptyList())
        }

        NavHost(
            navController = navController,
            startDestination = "splash"
        ) {

            composable("splash") {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }

            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("events") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onSignUpClick = {
                        navController.navigate("signup")
                    }
                )
            }

            composable("signup") {
                SignupScreen(
                    onSignupSuccess = {
                        navController.navigate("events") {
                            popUpTo("signup") { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable("events") {
                val currentUser = FirebaseAuth.getInstance().currentUser

                LaunchedEffect(currentUser?.uid) {
                    if (currentUser != null) {
                        FirebaseFirestore.getInstance()
                            .collection("events")
                            .whereEqualTo("userId", currentUser.uid)
                            .get()
                            .addOnSuccessListener { result ->
                                events = result.documents.map { doc ->
                                    EventItem(
                                        title = doc.getString("title") ?: "",
                                        date = doc.getString("date") ?: "",
                                        time = doc.getString("time") ?: "",
                                        notes = doc.getString("notes") ?: ""
                                    )
                                }
                            }
                    }
                }

                MainEventsWithBottomNav(
                    events = events,
                    onAddEventClick = {
                        navController.navigate("add_event")
                    },
                    onDeleteClick = { index ->
                        events = events.toMutableList().apply {
                            removeAt(index)
                        }
                    },
                    onLogoutClick = {
                        FirebaseAuth.getInstance().signOut()
                        events = emptyList()
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                )
            }

            composable("add_event") {
                AddEventScreen(
                    onSaveClick = { newEvent ->
                        events = events + newEvent
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    @Composable
    fun SplashScreen(onSplashFinished: () -> Unit) {
        LaunchedEffect(Unit) {
            delay(3000)
            onSplashFinished()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1565C0),
                            Color(0xFFFF9800)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PingMe",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Never miss an important moment",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}