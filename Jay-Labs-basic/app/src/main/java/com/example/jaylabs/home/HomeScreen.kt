package com.example.jaylabs.home

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.jaylabs.R
import com.example.jaylabs.mainapp.Route
import com.example.jaylabs.models.ModelResponse
import com.example.jaylabs.utils.JayLabsTextField

@Composable
fun HomeScreen(modifier: Modifier = Modifier,viewModel: HomeViewModel= hiltViewModel(),navController:NavController) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }
    val homeUiState by viewModel.modelResponse.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {

    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }
    Box(modifier = Modifier.fillMaxSize()) {


        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Submit Your Image",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Image Picker Box
            //here
            ImagePickerBox(
                selectedImageUri = selectedImageUri,
                onImageClick = { imagePickerLauncher.launch("image/*") }
            )

///
            //

            Spacer(modifier = Modifier.height(16.dp))

            // Description Input
            JayLabsTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Add Label", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button with Improved Colors
            Button(
                onClick = {
                    viewModel.getSelectedUri(selectedImageUri)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                enabled = selectedImageUri != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(text = "Submit", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

        }

        when (homeUiState) {
            is HomeViewModel.HomeEvent.Empty -> {
                // Handle empty state if necessary
            }

            is HomeViewModel.HomeEvent.Error -> {
                val errorMessage = (homeUiState as HomeViewModel.HomeEvent.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()

            }

            is HomeViewModel.HomeEvent.Loading -> {
                CircularProgressIndicator(modifier = Modifier.size(80.dp).align(Alignment.Center))
            }

            is HomeViewModel.HomeEvent.Success -> {
                val data =
                    (homeUiState as HomeViewModel.HomeEvent.Success<ModelResponse>).data.prediction
                Toast.makeText(context, "Prediction $data", Toast.LENGTH_SHORT).show()
                navController.navigate("${Route.ReportDetails.route}/$data")

            }
        }

    }
}

// Image Picker Box with Improved Colors & Borders
@Composable
fun ImagePickerBox(selectedImageUri: Uri?, onImageClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(200.dp)
            .clickable { onImageClick() }
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .border(
                BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)),
                RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selectedImageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(selectedImageUri),
                contentDescription = "Selected Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_photo),
                contentDescription = "Add Image",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
