package com.example.jaylabs.pastreports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jaylabs.home.mainapp.Route
import com.example.jaylabs.models.ModelResponse

@Composable
fun PastReportsScreen(
    navController: NavController,
    viewModel: PastReportsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val reports by viewModel.reports.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(start = 16.dp, top = 70.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                item {
                    Text(
                        text = "Your Past Reports",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(reports.size) { index ->
                    val report=reports[index]
val data=report.toModelResponse()
                    ReportItem(
                        reportName = "Melanoma: ${"%.2f".format(report.melanomaProbability)} | Nevus: ${"%.2f".format(report.nevusProbability)}",
                        reportDescription = report.interpretation
                    ) {
                        navController.navigate("${Route.ReportDetails.route}/$data")
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = reportName, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reportDescription,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
