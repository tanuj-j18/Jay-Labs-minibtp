package com.example.jaylabs.utils


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jaylabs.R
import com.example.jaylabs.mainapp.Route
import com.example.jaylabs.pastreports.ReportItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun DrawerContent(
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    navController: NavController,
    scope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState
) {
    val userId = firebaseAuth.currentUser?.uid
    val userName = remember { mutableStateOf("User") }

    // Fetch user data efficiently
    LaunchedEffect(userId) {
        userId?.let { uid ->
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    userName.value = document.getString("fullName") ?: "User"
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Profile Section
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(R.drawable.user),
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Hello,", color = Color.Gray)
                Text(text = userName.value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Menu Items
        Column {
            DrawerItem("Home", Icons.Default.Home) {
                navController.navigate(Route.HomeScreen.route)
                scope.launch { drawerState.close() }
            }
            DrawerItem("Settings", Icons.Default.Settings) {}
            DrawerItem("Help", Icons.Default.Info) {}
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Reports Section
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Text(
                    text = "Recent Reports",
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            items(List(4) { index -> "Report ${index + 1}" to "Description of Report ${index + 1}" }) { (name, desc) ->
                ReportItem(reportName = name, reportDescription = desc) {
                   navController.navigate("${Route.ReportDetails.route}/$name/$desc")
                    scope.launch {
                        drawerState.close()
                    }
                }
            }
        }

        // Logout Button
        Button(
            onClick = {
                firebaseAuth.signOut()
                navController.navigate(Route.AuthScreen.route)
                scope.launch { drawerState.close() }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = if (userId != null) "Sign Out" else "Sign In", color = Color.White)
        }
    }
}


@Composable
fun DrawerItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Icon(icon, contentDescription = title, tint = Color.Gray)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, fontSize = 16.sp)
    }
}
