package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuthState
import com.example.viewmodel.FoodDeliveryViewModel

@Composable
fun LoginScreen(viewModel: FoodDeliveryViewModel) {
    val authState by viewModel.authState.collectAsState()
    val isFirebaseConfigured by viewModel.isFirebaseConfigured.collectAsState()
    var signup by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("CUSTOMER") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Box(Modifier.fillMaxSize().background(DeepCrimson)) {
        BarmerFortBackdrop(Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)))
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Moped, null, tint = SandyGold, modifier = Modifier.size(64.dp))
            Text("BarmerEats", color = SandyGold, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text("Local Food. Local Delivery.", color = SandyGold.copy(alpha = .8f))
            Spacer(Modifier.height(24.dp))

            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = SandyGold.copy(alpha = .96f))) {
                Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (signup) "Create Account" else "Secure Login", color = DeepCrimson, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))

                    if (signup) {
                        OutlinedTextField(name, { name = it }, label = { Text("Full Name") }, leadingIcon = { Icon(Icons.Default.Person, null) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(phone, { phone = it }, label = { Text("Phone Number") }, leadingIcon = { Icon(Icons.Default.Phone, null) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), singleLine = true, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(10.dp))
                    }

                    OutlinedTextField(email, { email = it }, label = { Text("Email") }, leadingIcon = { Icon(Icons.Default.Email, null) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(password, { password = it }, label = { Text("Password") }, leadingIcon = { Icon(Icons.Default.Lock, null) }, visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth())

                    if (signup) {
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("CUSTOMER" to "Customer", "RESTAURANT" to "Restaurant", "RIDER" to "Rider").forEach { (code, label) ->
                                Button(onClick = { role = code }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (role == code) DeepCrimson else Color.Gray)) { Text(label, fontSize = 10.sp) }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    val shownError = error ?: (authState as? AuthState.Error)?.message
                    if (shownError != null) Text("⚠️ $shownError", color = Color.Red, fontSize = 12.sp)
                    if (authState is AuthState.Loading) CircularProgressIndicator(color = DeepCrimson)
                    else Button(
                        onClick = {
                            error = null
                            when {
                                email.isBlank() -> error = "Email is required."
                                password.length < 6 -> error = "Password must contain at least 6 characters."
                                signup && name.isBlank() -> error = "Name is required."
                                signup && phone.length < 10 -> error = "Enter a valid phone number."
                                signup -> viewModel.signUpUser(email.trim(), password, name.trim(), phone.trim(), role)
                                else -> viewModel.signInUser(email.trim(), password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson)
                    ) { Text(if (signup) "Create Account" else "Sign In", color = Color.White, fontWeight = FontWeight.Bold) }

                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = { signup = !signup; error = null }) { Text(if (signup) "Already have an account? Sign in" else "Create a new account") }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                if (isFirebaseConfigured) "Firebase Authentication active" else "Firebase Authentication is required. Sandbox login is disabled.",
                color = if (isFirebaseConfigured) Color.Green else Color.White,
                fontSize = 11.sp
            )
        }
    }
}
