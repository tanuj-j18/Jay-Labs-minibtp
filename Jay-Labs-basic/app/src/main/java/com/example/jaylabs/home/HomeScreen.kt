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
import com.example.jaylabs.home.mainapp.Route
import com.example.jaylabs.utils.JayLabsTextField

@Composable
fun HomeScreen(modifier: Modifier = Modifier,viewModel: HomeViewModel= hiltViewModel(),navController:NavController) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }
    val homeUiState by viewModel.modelResponse.collectAsStateWithLifecycle()
    var selectedSex by remember { mutableStateOf("male") }
    var selectedAge by remember { mutableStateOf("") }
    var selectedSite by remember { mutableStateOf("torso") }

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

            // Sex Dropdown
            JayLabsTextField(
                value = selectedSex,
                onValueChange = { selectedSex = it },
                label = { Text("Sex (e.g., male, female)") },
                modifier = Modifier.fillMaxWidth()
            )

// Age Input
            Spacer(modifier = Modifier.height(8.dp))
            JayLabsTextField(
                value = selectedAge,
                onValueChange = { selectedAge = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )

// Anatomical Site Input
            Spacer(modifier = Modifier.height(8.dp))
            AnatomicalSiteDropdown(
                selectedSite = selectedSite,          // Pass the state variable to display
                onSiteSelected = { newSelection ->    // Provide the lambda to update the state
                    selectedSite = newSelection
                },
                modifier = Modifier.fillMaxWidth()    // Apply the same modifier
            )


            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button with Improved Colors
            Button(
                onClick = {
                    if (selectedAge.isNotBlank()) {
                        viewModel.getResponse(
                            imageUri = selectedImageUri,
                            sex = selectedSex,
                            age = selectedAge,
                            anatomSite = selectedSite
                        )
                    } else {
                        Toast.makeText(context, "Please enter age", Toast.LENGTH_SHORT).show()
                    }
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

        when (val state = homeUiState) { // Safe smart casting
            is HomeViewModel.HomeEvent.Empty -> {
                // Handle empty state if necessary
            }

            is HomeViewModel.HomeEvent.Error -> {
                LaunchedEffect(state) {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }

            is HomeViewModel.HomeEvent.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(80.dp))
                }
            }

            is HomeViewModel.HomeEvent.Success -> {
                LaunchedEffect(state) {
                    val data = state.data
                    Toast.makeText(context, "Prediction $data", Toast.LENGTH_SHORT).show()
                    navController.navigate("${Route.ReportDetails.route}/$data")
                    viewModel.resetState()
                }
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


// Define the list outside the composable if it's static
private val anatomicalSites = listOf(
    "head/neck",
    "upper extremity",
    "lower extremity",
    "torso",
    "palms/soles",
    "oral/genital"
)

@OptIn(ExperimentalMaterial3Api::class) // Required for ExposedDropdownMenuBox
@Composable
fun AnatomicalSiteDropdown(
    selectedSite: String,
    onSiteSelected: (String) -> Unit,
    modifier: Modifier = Modifier // Allow passing modifiers from the caller
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }, // Standard way to toggle
        modifier = modifier // Apply passed-in modifier to the Box
    ) {
        TextField(
            value = selectedSite,
            onValueChange = {}, // No change needed as it's read-only
            label = { Text("Anatomical Site") },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(), // Use default dropdown colors
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor() // **** THIS IS THE IMPORTANT CHANGE ****
            // Tells the Box that this TextField is the anchor for the menu
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false } // Close when dismissed
        ) {
            anatomicalSites.forEach { site ->
                DropdownMenuItem(
                    text = { Text(site) }, // Use the 'text' lambda for content
                    onClick = {
                        onSiteSelected(site)
                        expanded = false // Close menu after selection
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding // Default padding
                )
            }
        }
    }
}



@Composable
fun AnatomicalSiteDropdownPreview() {
    var selected by remember { mutableStateOf(anatomicalSites[0]) } // Start with a default selection

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            AnatomicalSiteDropdown(
                selectedSite = selected,
                onSiteSelected = { selected = it },
                modifier = Modifier.padding(16.dp) // Add padding in preview
            )
        }
    }
}