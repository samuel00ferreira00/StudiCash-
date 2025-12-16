package pt.iade.ei.studycash

import android.os.Bundle
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import pt.iade.ei.studycash.network.ApiClient
import pt.iade.ei.studycash.ui.theme.StudyCashTheme

class TransactionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyCashTheme {
                TransactionsScreen()
            }
        }
    }
}

@Composable
fun TransactionsScreen() {
    val darkTeal = Color(0xFF0E5564)
    val lightTeal = Color(0xFF0E97A4)
    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F8F9), Color(0xFFD6F5F7))
    )
    val context = LocalContext.current
    val selectedFilter = remember { mutableStateOf("Todas") }
    val searchQuery = remember { mutableStateOf("") }
    
    val transactions = remember { mutableStateListOf<TxItem>() }
    val incomeState = remember { mutableStateOf("0,00 €") }
    val expenseState = remember { mutableStateOf("0,00 €") }
    val isLoading = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

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
                    
                    val mapped = list.map { t ->
                        if (t.tipo == "Receita") totalIncome += t.valor
                        else totalExpense += t.valor
                        
                        TxItem(
                            id = t.idTransacao ?: -1L,
                            title = t.descricao,
                            subtitle = "${t.tipo} • ${t.dataTransacao}",
                            amount = "${if (t.tipo == "Receita") "+" else "-"} ${String.format("%.2f", t.valor)} €",
                            positive = (t.tipo == "Receita"),
                            latitude = t.latitude,
                            longitude = t.longitude,
                            localizacao = t.localizacao
                        )
                    }
                    transactions.clear()
                    transactions.addAll(mapped.reversed())
                    
                    incomeState.value = String.format("%.2f €", totalIncome)
                    expenseState.value = String.format("%.2f €", totalExpense)
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
                item { TransactionsHeader() }
                
                item {
                    SummaryCards(
                        income = incomeState.value,
                        expense = expenseState.value,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                
                item {
                    SearchBar(
                        query = searchQuery.value,
                        onQueryChange = { searchQuery.value = it },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                
                item {
                    FilterChips(
                        selected = selectedFilter.value,
                        onSelect = { selectedFilter.value = it },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                val filtered = when (selectedFilter.value) {
                    "Receitas" -> transactions.filter { it.positive }
                    "Despesas" -> transactions.filter { !it.positive }
                    else -> transactions
                }.filter {
                    searchQuery.value.isEmpty() || 
                    it.title.contains(searchQuery.value, ignoreCase = true)
                }
                
                if (filtered.isEmpty() && !isLoading.value) {
                    item {
                        EmptyTransactionsCard(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                } else {
                    items(filtered) { tx ->
                        val parts = tx.subtitle.split(" • ")
                        val cat = parts.getOrNull(0) ?: "Outro"
                        val date = parts.getOrNull(1) ?: ""
                        val amount = parseAmountToDouble(tx.amount)
                        TransactionRow(
                            tx = tx,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            DetailTransactionsActivity.start(
                                context = context,
                                transactionId = tx.id,
                                isIncome = tx.positive,
                                title = tx.title,
                                category = cat,
                                date = date,
                                amount = kotlin.math.abs(amount),
                                notes = "",
                                latitude = tx.latitude,
                                longitude = tx.longitude,
                                localizacao = tx.localizacao
                            )
                        }
                    }
                }
                
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun TransactionsHeader() {
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
                "Transações",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = darkTeal
            )
            Text(
                "Histórico completo",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
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
private fun SummaryCards(income: String, expense: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFECFDF5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("↑", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Receitas", color = Color(0xFF6B7280), fontSize = 12.sp)
                    Text(income, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                }
            }
        }
        
        ElevatedCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFEF2F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("↓", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Despesas", color = Color(0xFF6B7280), fontSize = 12.sp)
                    Text(expense, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                }
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val darkTeal = Color(0xFF0E5564)
    
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Pesquisar transações...", color = Color(0xFF9CA3AF)) },
        leadingIcon = {
            Icon(
                Icons.Outlined.Search,
                contentDescription = null,
                tint = darkTeal
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = darkTeal,
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
private fun FilterChips(selected: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    val darkTeal = Color(0xFF0E5564)
    val filters = listOf("Todas", "Receitas", "Despesas")
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selected
            Surface(
                modifier = Modifier.clickable { onSelect(filter) },
                color = if (isSelected) darkTeal else Color.White,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = if (isSelected) 4.dp else 2.dp
            ) {
                Text(
                    text = filter,
                    color = if (isSelected) Color.White else Color(0xFF6B7280),
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    fontSize = 14.sp
                )
            }
        }
    }
}

data class TxItem(
    val id: Long, 
    val title: String, 
    val subtitle: String, 
    val amount: String, 
    val positive: Boolean,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val localizacao: String? = null
)

@Composable
private fun TransactionRow(tx: TxItem, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                Text("💸", fontSize = 32.sp)
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
                "Comece a registar as suas receitas e despesas para ver o seu histórico financeiro aqui.",
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

private fun parseAmountToDouble(text: String): Double {
    val cleaned = text.replace("€", "")
        .replace(" ", "")
        .replace("+", "")
        .replace(',', '.')
    return cleaned.toDoubleOrNull() ?: 0.0
}

@Preview(showBackground = true)
@Composable
fun TransactionsPreview() {
    StudyCashTheme { TransactionsScreen() }
}
