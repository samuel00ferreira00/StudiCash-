package pt.iade.ei.studycash

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.iade.ei.studycash.ui.theme.StudyCashTheme

class NewExpenseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyCashTheme {
                NewExpenseScreen()
            }
        }
    }
}

@Composable
fun NewExpenseScreen() {
    val bg = Color(0xFFD6F5F7)
    val darkTeal = Color(0xFF0E5564)

    val title = remember { mutableStateOf("") }
    val value = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }
    val date = remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
        ) {
            Header()

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TypeSelector()

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Título", fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = title.value,
                            onValueChange = { title.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Ex: Almoço no RU") },
                            singleLine = true,
                        )

                        Text("Valor", fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = value.value,
                            onValueChange = { value.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("0,00") },
                            singleLine = true,
                            leadingIcon = { Text("€ ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Text("Categoria", fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = category.value,
                            onValueChange = { category.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Selecione uma categoria") },
                            singleLine = true,
                            readOnly = true,
                            trailingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.downnarrow),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )

                        Text("Data", fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = date.value,
                            onValueChange = { date.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("") },
                            singleLine = true,
                            readOnly = true
                        )

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = { /* no-op */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = darkTeal, contentColor = Color.White),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Adicionar Despesa", fontSize = 16.sp)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.weight(1f))
            AppBottomBar()
        }
    }
}

@Composable
private fun Header() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Nova despesa", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Adicione uma despesa", color = Color.DarkGray, fontSize = 14.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = { context.startActivity(Intent(context, ProfileActivity::class.java)) }) { Image(painter = painterResource(id = R.drawable.user), contentDescription = "Perfil", modifier = Modifier.size(24.dp)) }
            Text("Perfil", fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}

@Composable
private fun TypeSelector() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // Despesa (ativa)
        Box(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, Color(0xFF0E5564), RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp)
                        .background(Color(0xFF0E5564), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.decrease),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text("Despesa", fontWeight = FontWeight.SemiBold)
            }
        }
        // Receita (inativa)
        Box(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, Color(0xFFDFE6E9), RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp)
                        .background(Color(0xFFEAECEF), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.increase),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text("Receita", color = Color.DarkGray)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NewExpensePreview() {
    StudyCashTheme { NewExpenseScreen() }
}
