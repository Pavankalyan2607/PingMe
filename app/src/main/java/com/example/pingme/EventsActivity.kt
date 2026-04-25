package com.example.pingme

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pingme.ui.theme.PingMeTheme

class EventsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PingMeTheme {
                EventsScreen(
                    events = listOf(
                        EventItem(
                            title = "Project Submission",
                            date = "20 Mar 2026",
                            time = "10:00 AM",
                            notes = "Submit PingMe ICA files before deadline"
                        ),
                        EventItem(
                            title = "Team Meeting",
                            date = "22 Mar 2026",
                            time = "2:30 PM",
                            notes = "Discuss app progress and next sprint tasks"
                        ),
                        EventItem(
                            title = "Doctor Appointment",
                            date = "25 Mar 2026",
                            time = "6:00 PM",
                            notes = "Carry medical documents"
                        )
                    ),
                    onAddEventClick = {
                        Toast.makeText(
                            this,
                            "Navigate to Add Event screen",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onDeleteClick = {
                        Toast.makeText(
                            this,
                            "Delete action from MainActivity navigation version",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}

data class EventItem(
    val title: String,
    val date: String,
    val time: String,
    val notes: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    events: List<EventItem>,
    onAddEventClick: () -> Unit,
    onDeleteClick: (Int) -> Unit
) {
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
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "PingMe Events",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddEventClick,
                    containerColor = Color.White,
                    contentColor = Color(0xFF1565C0)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Event"
                    )
                }
            }
        ) { innerPadding ->

            if (events.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No upcoming events.\nTap + to add a new reminder.",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(events) { index, event ->
                        EventCard(
                            event = event,
                            onDeleteClick = {
                                onDeleteClick(index)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: EventItem,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.96f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = "Event Icon",
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = event.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Date: ${event.date}",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )

                    Text(
                        text = "Time: ${event.time}",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Event",
                        tint = Color(0xFFFF5722)
                    )
                }
            }

            if (event.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Notes: ${event.notes}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}