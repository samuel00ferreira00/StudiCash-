package pt.iade.ei.studycash

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.iade.ei.studycash.ui.theme.StudyCashTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyCashTheme {
                WelcomeScreen()
            }
        }
    }
}

@Composable
fun WelcomeScreen() {
    val bgBlue = Color(0xFF1E3A8A)
    val accentYellow = Color(0xFFFFD54F)
    val buttonTeal = Color(0xFF2EC4B6)
    val white = Color(0xFFFFFFFF)
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBlue),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo StudyCash",
                modifier = Modifier.size(300.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            val title = buildAnnotatedString {
                append("Bem-vindo ao ")
                withStyle(SpanStyle(color = accentYellow, fontWeight = FontWeight.ExtraBold)) {
                    append("StudyCash!")
                }
            }
            Text(
                text = title,
                color = white,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Controle seu dinheiro,\nalcance seus objetivos.",
                color = white.copy(alpha = 0.9f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { context.startActivity(Intent(context, LoginActivity::class.java)) },
                colors = ButtonDefaults.buttonColors(containerColor = buttonTeal, contentColor = white),
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .height(56.dp),
                shape = ButtonDefaults.shape
            ) {
                Text(text = "Começar", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            val loginText = buildAnnotatedString {
                withStyle(SpanStyle(color = white.copy(alpha = 0.9f))) { append("Já tem uma conta? ") }
                pushStringAnnotation(tag = "register", annotation = "register")
                withStyle(SpanStyle(color = white, textDecoration = TextDecoration.Underline, fontWeight = FontWeight.SemiBold)) {
                    append("Criar conta")
                }
                pop()
            }
            ClickableText(
                text = loginText,
                onClick = { offset ->
                    loginText.getStringAnnotations(tag = "register", start = offset, end = offset)
                        .firstOrNull()?.let {
                            context.startActivity(Intent(context, RegisterActivity::class.java))
                        }
                },
                style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, textAlign = TextAlign.Center, color = white)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    StudyCashTheme {
        WelcomeScreen()
    }
}