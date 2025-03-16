package com.example.jaylabs.home

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.jaylabs.R
import com.example.jaylabs.utils.JayLabsTextField

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,

) {
    val context= LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Submit Your Image",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Image Selection
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color(0xFFF3E5F5), RoundedCornerShape(8.dp))
                .clickable { imagePickerLauncher.launch("image/*") },
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
                    painter = painterResource(id = R.drawable.ic_add_photo), // Use your icon
                    contentDescription = "Add Image",
                    tint = Color.Gray,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description Input
        JayLabsTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Add Label") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = {
                Toast.makeText(context,"Submitting",Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedImageUri != null && description.isNotBlank()
        ) {
            Text(text = "Submit", fontSize = 16.sp)
        }
    }
}
