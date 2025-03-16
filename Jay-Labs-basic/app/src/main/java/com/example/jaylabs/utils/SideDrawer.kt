package com.example.jaylabs.utils


import CustomAlertDialog
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
    drawerState: DrawerState,
    drawerViewModel: DrawerViewModel = hiltViewModel()
) {
    val userId = firebaseAuth.currentUser?.uid
    val userName = drawerViewModel.userName.collectAsState()

    val backgroundColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    val buttonColor = MaterialTheme.colorScheme.error // Red button for logout

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor) // ✅ Dynamic background
            .padding(16.dp)
    ) {
        // Profile Section
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(R.drawable.user),
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                colorFilter = tint(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Hello,", color = textColor.copy(alpha = 0.7f))
                Text(text = userName.value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
            }
        }

        HorizontalDivider(color = dividerColor, modifier = Modifier.padding(vertical = 8.dp))

        // Menu Items
        Column {
            DrawerItem("Home", Icons.Default.Home) {
                navController.navigate(Route.HomeScreen.route)
                scope.launch { drawerState.close() }
            }
            DrawerItem("Settings", Icons.Default.Settings) {}
            DrawerItem("Help", Icons.Default.Info) {}
        }

        HorizontalDivider(color = dividerColor, modifier = Modifier.padding(vertical = 8.dp))

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
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
            items(List(4) { index -> "Report ${index + 1}" to "Description of Report ${index + 1}" }) { (name, desc) ->
                ReportItem(reportName = name, reportDescription = desc) {
                    navController.navigate("${Route.ReportDetails.route}/$name/$desc")
                    scope.launch { drawerState.close() }
                }
            }
        }

        var showAlertDialog by remember { mutableStateOf(false) }

        // Logout Button
        Button(
            onClick = {
                if (userId != null) {
                    showAlertDialog = true
                } else {
                    navController.navigate(Route.AuthScreen.route)
                }
                scope.launch { drawerState.close() }
            },
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = if (userId != null) "Sign Out" else "Sign In", color = MaterialTheme.colorScheme.onError)
        }

        if (showAlertDialog) {
            CustomAlertDialog(
                onDismiss = { showAlertDialog = false }
            ) {
                showAlertDialog = false
                navController.navigate(Route.AuthScreen.route)
                firebaseAuth.signOut()
            }
        }
    }
}



@Composable
fun DrawerItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val iconColor = MaterialTheme.colorScheme.primary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Icon(icon, contentDescription = title, tint = iconColor) // ✅ Dynamic icon color
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, fontSize = 16.sp, color = textColor) // ✅ Dynamic text color
    }
}

