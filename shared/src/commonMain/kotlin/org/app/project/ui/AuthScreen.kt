package org.app.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.project.presentation.AuthViewModel
import org.app.project.theme.Ink
import org.app.project.theme.InkSoft
import org.app.project.theme.Paper
import org.app.project.theme.Rose

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val error by viewModel.error.collectAsState()
    val info by viewModel.info.collectAsState()
    val busy by viewModel.busy.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        Box(
            modifier = Modifier.size(52.dp).border(2.dp, Ink, CircleShape).background(Rose, CircleShape),
            contentAlignment = Alignment.Center
        ) { Text("K", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4B1528)) }

        Spacer(Modifier.height(16.dp))
        Text("Kimchii", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text(if (isSignUp) "Start your archive" else "Welcome back", fontSize = 13.sp, color = InkSoft)

        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = email, onValueChange = { email = it; viewModel.clearError() },
            label = { Text("Email") }, singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it; viewModel.clearError() },
            label = { Text("Password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth()
        )

        error?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, fontSize = 13.sp, color = Color(0xFFA32D2D))
        }
        info?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, fontSize = 13.sp, color = InkSoft)
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                if (isSignUp) viewModel.signUp(email, password)
                else viewModel.signIn(email, password)
            },
            enabled = !busy && email.isNotBlank() && password.isNotBlank(),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (busy) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Paper)
            else Text(if (isSignUp) "Create account" else "Sign in", fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(16.dp))
        Text(
            if (isSignUp) "Already have an account? Sign in" else "New here? Create an account",
            fontSize = 13.sp, color = Rose,
            modifier = Modifier.clickable { isSignUp = !isSignUp; viewModel.clearMessages() }
        )
    }
}