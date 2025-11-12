package pt.iade.ei.studycash

import android.os.Bundle
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.iade.ei.studycash.ui.theme.StudyCashTheme

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyCashTheme { ProfileScreen() }
        }
    }
}

@Composable
fun ProfileScreen() {
    val bg = Color(0xFFD6F5F7)
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
        ) {
            Header()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserCard()
                MetricsRow()
                NotificationsRow()
                GoalsRow()
                LogoutButton()
                Spacer(Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            AppBottomBar()
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Perfil", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Gerencie sua conta", color = Color.DarkGray, fontSize = 14.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = { /* no-op */ }) {
                Image(painter = painterResource(id = R.drawable.user), contentDescription = "Perfil", modifier = Modifier.size(24.dp))
            }
            Text("Perfil", fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}

@Composable
private fun UserCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF0E5564), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) { Text("JS", color = Color.White, fontWeight = FontWeight.Bold) }
            Column {
                Text("João Silva", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text("joao.silva@email.com", color = Color.Gray, fontSize = 14.sp)
                Text("Estudante de Engenharia", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun MetricsRow() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("128", fontWeight = FontWeight.Bold)
                Text("Transações", color = Color.Gray, fontSize = 12.sp)
            }
        }
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("850 €", fontWeight = FontWeight.Bold)
                Text("Economizado", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun NotificationsRow() {
    val enabled = remember { mutableStateOf(true) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Notificações", fontWeight = FontWeight.SemiBold)
                Text("Receber alertas e lembretes", color = Color.Gray, fontSize = 12.sp)
            }
            Switch(checked = enabled.value, onCheckedChange = { enabled.value = it })
        }
    }
}

@Composable
private fun GoalsRow() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column { Text("Metas Financeiras", fontWeight = FontWeight.SemiBold); Text("Configure suas metas de economia", color = Color.Gray, fontSize = 12.sp) }
            Image(painter = painterResource(id = R.drawable.arrow), contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun LogoutButton() {
    Button(
        onClick = { /* no-op */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
    ) {
        Text("Sair da Conta")
    }
}


@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    StudyCashTheme { ProfileScreen() }
}
