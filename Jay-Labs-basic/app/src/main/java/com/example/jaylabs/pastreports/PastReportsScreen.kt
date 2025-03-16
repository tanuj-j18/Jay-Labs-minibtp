package com.example.jaylabs.pastreports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jaylabs.mainapp.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastReportsScreen(
    navController: NavController
) {
    val reports = remember {
        List(30) { index ->
            "Report ${index + 1}" to "This is the detailed description of Report ${index + 1}."
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)  // ✅ Accounts for Scaffold padding
                .padding(horizontal = 16.dp, vertical = 70.dp),  // ✅ Adjusts for left-right margins
            horizontalAlignment = Alignment.CenterHorizontally
        ) {




            LazyColumn(
                modifier = Modifier.fillMaxSize(),  // ✅ Full height scrolling
                contentPadding = PaddingValues(bottom = 16.dp) // ✅ Prevents bottom content clipping
            ) {
                item {
                    Text(text = "Your Past Reports", modifier = Modifier.fillMaxWidth(), fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                items(reports) { (reportName, reportDescription) ->
                    ReportItem(
                        reportName = reportName,
                        reportDescription = reportDescription
                    ) {
                        navController.navigate("${Route.ReportDetails.route}/$reportName/$reportDescription")
                    }
                }
            }
        }
    }
}

@Composable
fun ReportItem(reportName: String, reportDescription: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = reportName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reportDescription,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
