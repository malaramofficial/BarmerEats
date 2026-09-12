package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuthState
import com.example.viewmodel.FoodDeliveryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: FoodDeliveryViewModel) {
    val authState by viewModel.authState.collectAsState()
    val isFirebaseConfigured by viewModel.isFirebaseConfigured.collectAsState()

    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("CUSTOMER") } // CUSTOMER, RESTAURANT, RIDER
    var showPassword by remember { mutableStateOf(false) }

    // Errors
    var validationError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCrimson)
    ) {
        // Sunset desert fort backdrop decoration
        BarmerFortBackdrop(modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Branding Title
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SandyGold),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Moped,
                    contentDescription = "BarmerEats Logo",
                    tint = DeepCrimson,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "BarmerEats",
                color = SandyGold,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Local Food. Local Delivery.",
                color = SandyGold.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card panel holding Auth controls
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SandyGold.copy(alpha = 0.95f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isSignUpMode) "Register New Account" else "Secure Login",
                        color = DeepCrimson,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Input Form Fields
                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name (नाम)") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeepCrimson,
                                focusedLabelColor = DeepCrimson
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("auth_name_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number (फ़ोन)") },
                            leadingIcon = { Icon(Icons.Default.Phone, null) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeepCrimson,
                                focusedLabelColor = DeepCrimson
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("auth_phone_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address (ईमेल)") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepCrimson,
                            focusedLabelColor = DeepCrimson
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("auth_email_field")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password (पासवर्ड)") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepCrimson,
                            focusedLabelColor = DeepCrimson
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("auth_password_field")
                    )

                    // Role selector during sign up (ADMIN is forbidden from selectable signup!)
                    if (isSignUpMode) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Register As:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalGray,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val roles = listOf("CUSTOMER" to "Customer", "RESTAURANT" to "Kitchen", "RIDER" to "Rider")
                            roles.forEach { (code, label) ->
                                val isSelected = selectedRole == code
                                Button(
                                    onClick = { selectedRole = code },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) DeepCrimson else Color.LightGray.copy(alpha = 0.4f),
                                        contentColor = if (isSelected) Color.White else CharcoalGray
                                    ),
                                    modifier = Modifier.weight(1f).testTag("signup_role_$code")
                                ) {
                                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Error states
                    val displayError = validationError ?: (authState as? AuthState.Error)?.message
                    AnimatedVisibility(visible = displayError != null) {
                        if (displayError != null) {
                            Text(
                                text = "⚠️ $displayError",
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }

                    // Authenticating Loader state
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = DeepCrimson, modifier = Modifier.padding(12.dp))
                    } else {
                        Button(
                            onClick = {
                                validationError = null
                                if (email.isBlank() || password.isBlank()) {
                                    validationError = "Please enter your email and password."
                                    return@Button
                                }
                                if (isSignUpMode) {
                                    if (name.isBlank() || phone.isBlank()) {
                                        validationError = "Please fill in all details to register."
                                        return@Button
                                    }
                                    if (phone.length < 10) {
                                        validationError = "Please enter a valid 10-digit phone number."
                                        return@Button
                                    }
                                    viewModel.signUpUser(email.trim(), name.trim(), phone.trim(), selectedRole)
                                } else {
                                    viewModel.signInUser(email.trim())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("auth_submit_button")
                        ) {
                            Text(
                                text = if (isSignUpMode) "Register (पंजीकरण करें)" else "Sign In (लॉग इन करें)",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isSignUpMode) "Already have an account? Sign In" else "New to BarmerEats? Create Account",
                        color = CharcoalGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable {
                                isSignUpMode = !isSignUpMode
                                validationError = null
                            }
                            .testTag("auth_toggle_mode")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sandbox or Cloud Indicator Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = SandyGold.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isFirebaseConfigured) Icons.Default.CloudQueue else Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = if (isFirebaseConfigured) Color.Green else Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFirebaseConfigured) "Firebase Cloud Authentication Active" else "Developer Sandbox Mode active. Tap any email to log in.",
                        color = Color.White,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
