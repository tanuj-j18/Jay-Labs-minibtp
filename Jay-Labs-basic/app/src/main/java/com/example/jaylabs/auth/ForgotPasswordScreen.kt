package com.example.jaylabs.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.jaylabs.R
import com.example.jaylabs.mainapp.Route
import com.example.jaylabs.ui.theme.Orange
import com.example.jaylabs.utils.JayLabsTextField

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    val verticalScroll = rememberScrollState()
    val context = LocalContext.current
    val uiState by viewModel.forgotPasswordState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthState.Success -> {
                Toast.makeText(context, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (uiState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Box {
        Image(
            painter = painterResource(R.drawable.ic_auth_bg),
            contentDescription = null,
            modifier = modifier.fillMaxSize()
        )

        IconButton(
            onClick = { navController.popBackStack()},
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 32.dp, start = 16.dp)
                .size(32.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(verticalScroll),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(text = "Forgot Password", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            JayLabsTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter your email") }
            )

            Spacer(Modifier.height(16.dp))

            Button(
                enabled = email.isNotEmpty() && uiState !is AuthState.Loading,
                onClick = { viewModel.resetPassword(email) },
                modifier = Modifier.width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                if (uiState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Send Reset Email")
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}
