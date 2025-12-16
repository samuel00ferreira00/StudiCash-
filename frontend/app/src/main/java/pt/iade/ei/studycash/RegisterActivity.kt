package pt.iade.ei.studycash

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pt.iade.ei.studycash.model.User
import pt.iade.ei.studycash.network.ApiClient
import pt.iade.ei.studycash.ui.theme.StudyCashTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyCashTheme {
                RegisterScreen()
            }
        }
    }
}

@Composable
fun RegisterScreen() {
    val bgTeal = Color(0xFF0E97A4)  // Mesma cor do Login
    val darkTeal = Color(0xFF0E5564)
    val white = Color.White
    val scope = rememberCoroutineScope()

    val email = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgTeal)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo StudyCash",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(24.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Criar Conta",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkTeal
                    )
                    
                    Text(
                        "Preencha os dados para começar",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = username.value,
                        onValueChange = { 
                            username.value = it
                            errorMessage.value = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nome completo") },
                        placeholder = { Text("João Silva") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = null,
                                tint = darkTeal
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = darkTeal,
                            focusedLabelColor = darkTeal,
                            cursorColor = darkTeal
                        )
                    )

                    OutlinedTextField(
                        value = email.value,
                        onValueChange = { 
                            email.value = it
                            errorMessage.value = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("E-mail") },
                        placeholder = { Text("seu@email.com") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Email,
                                contentDescription = null,
                                tint = darkTeal
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = darkTeal,
                            focusedLabelColor = darkTeal,
                            cursorColor = darkTeal
                        )
                    )

                    OutlinedTextField(
                        value = password.value,
                        onValueChange = { 
                            password.value = it
                            errorMessage.value = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Senha") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = darkTeal
                            )
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = darkTeal,
                            focusedLabelColor = darkTeal,
                            cursorColor = darkTeal
                        )
                    )

                    OutlinedTextField(
                        value = confirmPassword.value,
                        onValueChange = { 
                            confirmPassword.value = it
                            errorMessage.value = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirmar senha") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = darkTeal
                            )
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = darkTeal,
                            focusedLabelColor = darkTeal,
                            cursorColor = darkTeal
                        )
                    )

                    if (errorMessage.value.isNotEmpty()) {
                        Text(
                            text = errorMessage.value,
                            color = Color(0xFFEF4444),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            // Validações
                            if (username.value.isEmpty() || email.value.isEmpty() || 
                                password.value.isEmpty() || confirmPassword.value.isEmpty()) {
                                errorMessage.value = "Preencha todos os campos"
                                return@Button
                            }
                            
                            if (password.value != confirmPassword.value) {
                                errorMessage.value = "As senhas não coincidem"
                                return@Button
                            }
                            
                            if (password.value.length < 4) {
                                errorMessage.value = "A senha deve ter pelo menos 4 caracteres"
                                return@Button
                            }

                            isLoading.value = true
                            scope.launch {
                                try {
                                    val user = User(
                                        nome = username.value,
                                        email = email.value,
                                        password = password.value
                                    )
                                    val response = ApiClient.userService.create(user)
                                    
                                    if (response.isSuccessful && response.body() != null) {
                                        Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                                        context.startActivity(Intent(context, LoginActivity::class.java))
                                        (context as? ComponentActivity)?.finish()
                                    } else if (response.code() == 409) {
                                        errorMessage.value = "Este email já está registado"
                                    } else {
                                        errorMessage.value = "Erro ao criar conta: ${response.code()}"
                                    }
                                } catch (e: Exception) {
                                    errorMessage.value = "Erro de conexão. Verifique sua internet."
                                    e.printStackTrace()
                                } finally {
                                    isLoading.value = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = darkTeal,
                            contentColor = white
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading.value
                    ) {
                        if (isLoading.value) {
                            CircularProgressIndicator(
                                color = white,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Criar Conta", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Já tem conta? Iniciar sessão",
                color = white,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    (context as? ComponentActivity)?.finish()
                }
            )
            
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    StudyCashTheme {
        RegisterScreen()
    }
}
