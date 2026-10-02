package com.scorpions.agriscan.ui.screens.auth

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.scorpions.agriscan.ui.components.AgriScanTopBar
import com.scorpions.agriscan.ui.components.ErrorBanner
import com.scorpions.agriscan.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit,
    isUrdu: Boolean = false
) {
    val state by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AgriScanTopBar(
            title = if (state.isLoginMode)
                if (isUrdu) "لاگ ان" else "Login"
            else
                if (isUrdu) "رجسٹر" else "Register",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = AgriGreen700
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (state.isLoginMode)
                    if (isUrdu) "خوش آمدید" else "Welcome Back"
                else
                    if (isUrdu) "اکاؤنٹ بنائیں" else "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AgriGreen900
            )

            Spacer(Modifier.height(24.dp))

            if (state.errorMessage != null) {
                ErrorBanner(message = state.errorMessage!!)
                Spacer(Modifier.height(16.dp))
            }

            if (state.successMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AgriGreen50)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AgriGreen700)
                        Spacer(Modifier.width(8.dp))
                        Text(state.successMessage!!, color = AgriGreen900)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            if (!state.isLoginMode) {
                OutlinedTextField(
                    value = state.fullName,
                    onValueChange = { viewModel.updateFullName(it) },
                    label = { Text(if (isUrdu) "پورا نام" else "Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.updatePhone(it) },
                label = { Text(if (isUrdu) "فون نمبر" else "Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text(if (isUrdu) "پاس ورڈ" else "Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (!state.isLoginMode) {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.district,
                    onValueChange = { viewModel.updateDistrict(it) },
                    label = { Text(if (isUrdu) "ضلع" else "District") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.submit() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isLoading && state.phone.isNotBlank() && state.password.isNotBlank(),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreen700)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = if (state.isLoginMode)
                            if (isUrdu) "لاگ ان" else "Login"
                        else
                            if (isUrdu) "رجسٹر" else "Register",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = { viewModel.toggleMode() }) {
                Text(
                    text = if (state.isLoginMode)
                        if (isUrdu) "اکاؤنٹ نہیں ہے؟ رجسٹر کریں" else "Don't have an account? Register"
                    else
                        if (isUrdu) "پہلے سے اکاؤنٹ ہے؟ لاگ ان کریں" else "Already have an account? Login",
                    color = AgriGreen700
                )
            }
        }
    }
}
