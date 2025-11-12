package pt.iade.ei.studycash

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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

class GoalsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { StudyCashTheme { GoalsScreen() } }
    }
}

@Composable
fun GoalsScreen() {
    val bg = Color(0xFFD6F5F7)

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
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                            placeholder = { Text("Ex: Viagem") },
                            singleLine = true
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
                            readOnly = true
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E5564), contentColor = Color.White),
                            shape = RoundedCornerShape(14.dp)
                        ) { Text("Adicionar meta", fontSize = 16.sp) }
                    }
                }

                GoalCard()
            }

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
            Text("Metas", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Adicione uma nova meta", color = Color.DarkGray, fontSize = 14.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = { context.startActivity(Intent(context, ProfileActivity::class.java)) }) {
                Image(painter = painterResource(id = R.drawable.user), contentDescription = "Perfil", modifier = Modifier.size(24.dp))
            }
            Text("Perfil", fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}

@Composable
private fun GoalCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Viagem", fontWeight = FontWeight.SemiBold)
                Column(horizontalAlignment = Alignment.End) {
                    Text("Valor da meta", color = Color.Gray, fontSize = 12.sp)
                    Text("220.00 €", fontWeight = FontWeight.Medium)
                }
            }
            Text("70.00 €", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Progresso", color = Color.Gray, fontSize = 12.sp)
            LinearProgressIndicator(progress = 0.7f, color = Color(0xFF0E5564))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("")
                Text("70%", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GoalsPreview() {
    StudyCashTheme { GoalsScreen() }
}
