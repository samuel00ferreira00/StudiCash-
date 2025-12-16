package pt.iade.ei.studycash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pt.iade.ei.studycash.data.SessionManager
import pt.iade.ei.studycash.network.ApiClient
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
    val darkTeal = Color(0xFF0E5564)
    val lightTeal = Color(0xFF0E97A4)
    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F8F9), Color(0xFFD6F5F7))
    )
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val userName = remember { mutableStateOf("Utilizador") }
    val userEmail = remember { mutableStateOf("") }
    val totalTransactions = remember { mutableStateOf(0) }
    val totalSaved = remember { mutableStateOf(0.0) }
    
    LaunchedEffect(Unit) {
        userName.value = SessionManager.getUserName(context)
        userEmail.value = SessionManager.getUserEmail(context)
        
        // Carregar estatísticas
        scope.launch {
            try {
                val userId = SessionManager.getUserId(context)
                if (userId > 0) {
                    val res = ApiClient.transacaoService.byUser(userId)
                    if (res.isSuccessful && res.body() != null) {
                        val txs = res.body()!!
                        totalTransactions.value = txs.size
                        val income = txs.filter { it.tipo == "Receita" }.sumOf { it.valor }
                        val expense = txs.filter { it.tipo != "Receita" }.sumOf { it.valor }
                        totalSaved.value = income - expense
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    Scaffold(
        bottomBar = { AppBottomBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header com avatar grande
                ProfileHeader(userName.value, userEmail.value)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Estatísticas
                    StatsRow(
                        transactions = totalTransactions.value,
                        saved = totalSaved.value
                    )
                    
                    SettingsCard {
                        GoalsSetting()
                    }
                    
                    // Botão de logout
                    LogoutButton()
                    
                    // Versão da app
                    Text(
                        "StudyCash v1.0.0",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(userName: String, userEmail: String) {
    val darkTeal = Color(0xFF0E5564)
    val lightTeal = Color(0xFF0E97A4)
    
    val initials = userName.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifEmpty { "U" }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar grande com gradiente
        Box(
            modifier = Modifier
                .size(100.dp)
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(lightTeal, darkTeal)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            )
        }
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            userName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = darkTeal
        )
        
        Text(
            userEmail,
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )
        
        Spacer(Modifier.height(8.dp))
        
        // Badge de estudante
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(darkTeal.copy(alpha = 0.1f))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                "✨ Estudante",
                color = darkTeal,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun StatsRow(transactions: Int, saved: Double) {
    val darkTeal = Color(0xFF0E5564)
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFECFDF5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📊", fontSize = 20.sp)
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "$transactions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = darkTeal
                )
                Text(
                    "Transações",
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp
                )
            }
        }
        
        ElevatedCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💰", fontSize = 20.sp)
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    String.format("%.0f €", saved),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = if (saved >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                Text(
                    "Saldo",
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            content()
        }
    }
}

@Composable
private fun GoalsSetting() {
    val context = LocalContext.current
    val darkTeal = Color(0xFF0E5564)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { context.startActivity(Intent(context, GoalsActivity::class.java)) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Star,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Metas Financeiras", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text("Configure suas metas", color = Color(0xFF9CA3AF), fontSize = 12.sp)
            }
        }
        Icon(
            Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF9CA3AF)
        )
    }
}

@Composable
private fun LogoutButton() {
    val context = LocalContext.current
    
    Button(
        onClick = {
            SessionManager.logout(context)
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFEF2F2),
            contentColor = Color(0xFFEF4444)
        )
    ) {
        Text("Sair da Conta", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    StudyCashTheme { ProfileScreen() }
}
