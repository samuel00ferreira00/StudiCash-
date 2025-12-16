package pt.iade.ei.studycash

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import pt.iade.ei.studycash.models.RecentTx
import pt.iade.ei.studycash.network.ApiClient
import pt.iade.ei.studycash.ui.theme.StudyCashTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyCashTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    val darkTeal = Color(0xFF0E5564)
    val lightTeal = Color(0xFF0E97A4)
    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F8F9), Color(0xFFD6F5F7))
    )
    val context = LocalContext.current

    val transactions = remember { mutableStateListOf<RecentTx>() }
    val balance = remember { mutableStateOf("0,00 €") }
    val income = remember { mutableStateOf("0,00 €") }
    val expense = remember { mutableStateOf("0,00 €") }
    val userName = remember { mutableStateOf("Utilizador") }
    val isLoading = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        userName.value = pt.iade.ei.studycash.data.SessionManager.getUserName(context)
    }

    fun loadData() {
        scope.launch {
            isLoading.value = true
            try {
                val userId = pt.iade.ei.studycash.data.SessionManager.getUserId(context)
                
                val res = if (userId > 0) {
                    ApiClient.transacaoService.byUser(userId)
                } else {
                    ApiClient.transacaoService.all()
                }
                
                if (res.isSuccessful && res.body() != null) {
                    val list = res.body()!!
                    var totalIncome = 0.0
                    var totalExpense = 0.0
                    
                    val recentTxs = list.map { t ->
                        if (t.tipo == "Receita") totalIncome += t.valor
                        else totalExpense += t.valor
                        
                        RecentTx(
                            title = t.descricao,
                            subtitle = "${t.tipo} • ${t.dataTransacao}",
                            amount = "${if (t.tipo == "Receita") "+" else "-"} ${String.format("%.2f", t.valor)} €",
                            positive = (t.tipo == "Receita")
                        )
                    }
                    
                    transactions.clear()
                    transactions.addAll(recentTxs.reversed())
                    
                    val currentBalance = totalIncome - totalExpense
                    balance.value = String.format("%.2f €", currentBalance)
                    income.value = String.format("%.2f €", totalIncome)
                    expense.value = String.format("%.2f €", totalExpense)
                }
                
                if (userId > 0) {
                    try {
                        val carteiraRes = ApiClient.carteiraService.firstByUser(userId)
                        if (carteiraRes.isSuccessful && carteiraRes.body() != null) {
                            val carteira = carteiraRes.body()!!
                            balance.value = String.format("%.2f €", carteira.saldo)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                userName.value = pt.iade.ei.studycash.data.SessionManager.getUserName(context)
                loadData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HeaderSection(userName = userName.value)
                }

                item {
                    BalanceCard(
                        balance = balance.value,
                        income = income.value,
                        expense = expense.value,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    QuickActionsCard(modifier = Modifier.padding(horizontal = 16.dp))
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Transações Recentes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkTeal
                        )
                        if (transactions.isNotEmpty()) {
                            Text(
                                "Ver todas",
                                fontSize = 14.sp,
                                color = lightTeal,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable {
                                    context.startActivity(Intent(context, TransactionsActivity::class.java))
                                }
                            )
                        }
                    }
                }

                if (transactions.isEmpty() && !isLoading.value) {
                    item {
                        EmptyTransactionsCard(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                } else {
                    items(transactions.take(5)) { tx ->
                        TransactionItem(
                            tx = tx,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun HeaderSection(userName: String) {
    val context = LocalContext.current
    val darkTeal = Color(0xFF0E5564)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Olá,",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )
            Text(
                userName,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = darkTeal
            )
        }
        
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { context.startActivity(Intent(context, ProfileActivity::class.java)) },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = "Perfil",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun BalanceCard(
    balance: String,
    income: String,
    expense: String,
    modifier: Modifier = Modifier
) {
    val darkTeal = Color(0xFF0E5564)
    val lightTeal = Color(0xFF0E97A4)
    
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(darkTeal, lightTeal)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.wallet),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Saldo Atual",
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(Modifier.height(16.dp))
                
                Text(
                    balance,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(Modifier.height(24.dp))
                
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Receitas
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("↑", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Receitas", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text(income, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    
                    // Despesas
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEF4444).copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("↓", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Despesas", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text(expense, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Botão Receita
        ElevatedCard(
            modifier = Modifier
                .weight(1f)
                .clickable { context.startActivity(Intent(context, NewRevenueActivity::class.java)) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFECFDF5)),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Receita",
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        // Botão Despesa
        ElevatedCard(
            modifier = Modifier
                .weight(1f)
                .clickable { context.startActivity(Intent(context, NewExpenseActivity::class.java)) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFEF2F2)),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("-", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Despesa",
                    color = Color(0xFFEF4444),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun TransactionItem(tx: RecentTx, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (tx.positive) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (tx.positive) "↑" else "↓",
                        color = if (tx.positive) Color(0xFF10B981) else Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        tx.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        tx.subtitle,
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                }
            }
            Text(
                tx.amount,
                color = if (tx.positive) Color(0xFF10B981) else Color(0xFFEF4444),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun EmptyTransactionsCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val darkTeal = Color(0xFF0E5564)
    
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                Text("📊", fontSize = 32.sp)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Sem transações ainda",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = darkTeal
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Comece a registar as suas receitas e despesas para acompanhar as suas finanças.",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { context.startActivity(Intent(context, NewRevenueActivity::class.java)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Receita", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = { context.startActivity(Intent(context, NewExpenseActivity::class.java)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Despesa", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    StudyCashTheme {
        HomeScreen()
    }
}
