package pt.iade.ei.studycash

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import pt.iade.ei.studycash.model.Carteira
import pt.iade.ei.studycash.model.Orcamento
import pt.iade.ei.studycash.network.ApiClient
import pt.iade.ei.studycash.ui.theme.StudyCashTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class BudgetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { StudyCashTheme { BudgetScreen() } }
    }
}

enum class ChartType(val label: String, val icon: String) {
    DONUT("Circular", "◐"),
    BAR("Barras", "▥"),
    LINE("Linha", "📈")
}

enum class ViewMode(val label: String, val icon: String) {
    EXPENSES("Despesas", "↓"),
    INCOME("Receitas", "↑"),
    GENERAL("Geral", "📊")
}

@Composable
fun BudgetScreen() {
    val darkTeal = Color(0xFF0E5564)
    val lightTeal = Color(0xFF0E97A4)
    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F8F9), Color(0xFFD6F5F7))
    )
    val context = LocalContext.current
    
    val budgetLimit = remember { mutableStateOf("") }
    val totalSpent = remember { mutableStateOf(0.0) }
    val totalIncome = remember { mutableStateOf(0.0) }
    val currentBudget = remember { mutableStateOf<Orcamento?>(null) }
    val expenseDistribution = remember { mutableStateMapOf<String, Double>() }
    val incomeDistribution = remember { mutableStateMapOf<String, Double>() }
    
    val selectedChartType = remember { mutableStateOf(ChartType.DONUT) }
    val selectedViewMode = remember { mutableStateOf(ViewMode.GENERAL) }
    
    val currentMonth = remember { 
        LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("pt", "PT")))
            .replaceFirstChar { it.uppercase() }
    }
    
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    fun loadData() {
        scope.launch {
            try {
                val userId = pt.iade.ei.studycash.data.SessionManager.getUserId(context)
                
                val resTx = if (userId > 0) {
                    ApiClient.transacaoService.byUser(userId)
                } else {
                    ApiClient.transacaoService.all()
                }
                
                if (resTx.isSuccessful && resTx.body() != null) {
                    val txs = resTx.body()!!
                    
                    val currentMonthTxs = txs.filter { tx ->
                        try {
                            val txDate = LocalDate.parse(tx.dataTransacao)
                            txDate.month == LocalDate.now().month && txDate.year == LocalDate.now().year
                        } catch (e: Exception) { false }
                    }
                    
                    val expenses = currentMonthTxs.filter { it.tipo != "Receita" }
                    val incomes = currentMonthTxs.filter { it.tipo == "Receita" }
                    
                    totalSpent.value = expenses.sumOf { it.valor }
                    totalIncome.value = incomes.sumOf { it.valor }
                    
                    expenseDistribution.clear()
                    expenses.forEach { t ->
                        val catName = t.descricao.ifEmpty { "Outros" }
                        expenseDistribution[catName] = expenseDistribution.getOrDefault(catName, 0.0) + t.valor
                    }
                    
                    incomeDistribution.clear()
                    incomes.forEach { t ->
                        val catName = t.descricao.ifEmpty { "Outros" }
                        incomeDistribution[catName] = incomeDistribution.getOrDefault(catName, 0.0) + t.valor
                    }
                }
                
                val resOrc = ApiClient.orcamentoService.all()
                if (resOrc.isSuccessful && resOrc.body() != null) {
                    val mesAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                    val orcamentoAtual = resOrc.body()!!.find { it.mes == mesAtual }
                    if (orcamentoAtual != null) {
                        currentBudget.value = orcamentoAtual
                        // Só preenche o campo se o utilizador já definiu um orçamento
                        if (orcamentoAtual.limite > 0) {
                            budgetLimit.value = orcamentoAtual.limite.toInt().toString()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) { loadData() }

    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) loadData()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
                BudgetHeader(currentMonth)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card de definir orçamento
                    BudgetInputCard(
                        budgetLimit = budgetLimit.value,
                        onBudgetChange = { budgetLimit.value = it },
                        currentBudget = currentBudget.value,
                        totalSpent = totalSpent.value,
                        onSave = { currentBudget.value = it }
                    )
                    
                    // Resumo rápido
                    val budget = budgetLimit.value.replace(",", ".").toDoubleOrNull() ?: 0.0
                    QuickSummaryCard(
                        spent = totalSpent.value,
                        income = totalIncome.value,
                        budget = budget
                    )
                    
                    // Seletores
                    SelectorsCard(
                        selectedMode = selectedViewMode.value,
                        onModeSelected = { selectedViewMode.value = it },
                        selectedType = selectedChartType.value,
                        onTypeSelected = { selectedChartType.value = it }
                    )
                    
                    // Gráfico
                    ChartCard(
                        chartType = selectedChartType.value,
                        viewMode = selectedViewMode.value,
                        expenseDistribution = expenseDistribution,
                        incomeDistribution = incomeDistribution,
                        totalExpenses = totalSpent.value,
                        totalIncome = totalIncome.value,
                        budget = budget
                    )
                    
                    // Detalhes por categoria
                    if (expenseDistribution.isNotEmpty() || incomeDistribution.isNotEmpty()) {
                        CategoryDetailsCard(
                            viewMode = selectedViewMode.value,
                            expenseDistribution = expenseDistribution,
                            incomeDistribution = incomeDistribution,
                            totalExpenses = totalSpent.value,
                            totalIncome = totalIncome.value
                        )
                    }
                    
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun BudgetHeader(currentMonth: String) {
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
                "Orçamento",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = darkTeal
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    currentMonth,
                    color = darkTeal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
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
private fun BudgetInputCard(
    budgetLimit: String,
    onBudgetChange: (String) -> Unit,
    currentBudget: Orcamento?,
    totalSpent: Double,
    onSave: (Orcamento) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val darkTeal = Color(0xFF0E5564)
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEDE9FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💰", fontSize = 20.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Definir Limite Mensal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = darkTeal
                    )
                    Text(
                        "Controle seus gastos",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                }
            }
            
            OutlinedTextField(
                value = budgetLimit,
                onValueChange = onBudgetChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Limite de Gastos") },
                placeholder = { Text("Ex: 500") },
                singleLine = true,
                leadingIcon = {
                    Text(
                        "€",
                        color = darkTeal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = darkTeal,
                    focusedLabelColor = darkTeal,
                    cursorColor = darkTeal
                )
            )
            
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val limite = budgetLimit.replace(",", ".").toDoubleOrNull() ?: 0.0
                            val mesAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                            val userId = pt.iade.ei.studycash.data.SessionManager.getUserId(context)
                            
                            var carteira: Carteira? = null
                            if (userId > 0) {
                                val carteiraRes = ApiClient.carteiraService.firstByUser(userId)
                                if (carteiraRes.isSuccessful) carteira = carteiraRes.body()
                            }
                            
                            val orcamento = Orcamento(
                                idOrcamento = currentBudget?.idOrcamento,
                                mes = mesAtual,
                                limite = limite,
                                gastoAtual = totalSpent,
                                carteira = carteira
                            )
                            
                            val res = if (currentBudget != null) {
                                ApiClient.orcamentoService.update(currentBudget.idOrcamento!!, orcamento)
                            } else {
                                ApiClient.orcamentoService.create(orcamento)
                            }
                            
                            if (res.isSuccessful) {
                                Toast.makeText(context, "Orçamento guardado! ✅", Toast.LENGTH_SHORT).show()
                                res.body()?.let { onSave(it) }
                            } else {
                                Toast.makeText(context, "Erro: ${res.code()}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = darkTeal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (currentBudget != null) "Atualizar" else "Definir Orçamento",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun QuickSummaryCard(spent: Double, income: Double, budget: Double) {
    val progress = if (budget > 0) (spent / budget).toFloat() else 0f
    val isOverBudget = budget > 0 && spent > budget
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Gastos
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF2F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("↓", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Gastos", color = Color(0xFF6B7280), fontSize = 12.sp)
                    Text(
                        String.format("%.0f €", spent),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444)
                    )
                }
                
                // Receitas
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFECFDF5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("↑", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Receitas", color = Color(0xFF6B7280), fontSize = 12.sp)
                    Text(
                        String.format("%.0f €", income),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }
                
                // Orçamento
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE9FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📊", fontSize = 20.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Orçamento", color = Color(0xFF6B7280), fontSize = 12.sp)
                    Text(
                        String.format("%.0f €", budget),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0E5564)
                    )
                }
            }
            
            if (budget > 0) {
                Column {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${(progress * 100).toInt().coerceAtMost(999)}% utilizado",
                            color = Color(0xFF6B7280),
                            fontSize = 12.sp
                        )
                        Text(
                            if (isOverBudget) "Excedido!" else "Disponível: ${String.format("%.0f €", budget - spent)}",
                            color = if (isOverBudget) Color(0xFFEF4444) else Color(0xFF10B981),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f) },
                        color = when {
                            progress > 1f -> Color(0xFFEF4444)
                            progress > 0.8f -> Color(0xFFF59E0B)
                            else -> Color(0xFF10B981)
                        },
                        trackColor = Color(0xFFE5E7EB),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectorsCard(
    selectedMode: ViewMode,
    onModeSelected: (ViewMode) -> Unit,
    selectedType: ChartType,
    onTypeSelected: (ChartType) -> Unit
) {
    val darkTeal = Color(0xFF0E5564)
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Modo de visualização
            Text("Visualizar", fontWeight = FontWeight.SemiBold, color = Color(0xFF6B7280), fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ViewMode.entries.forEach { mode ->
                    val isSelected = mode == selectedMode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) darkTeal else Color(0xFFF3F4F6))
                            .clickable { onModeSelected(mode) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(mode.icon, fontSize = 12.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                mode.label,
                                color = if (isSelected) Color.White else Color(0xFF6B7280),
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            
            // Tipo de gráfico
            Text("Tipo de Gráfico", fontWeight = FontWeight.SemiBold, color = Color(0xFF6B7280), fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChartType.entries.forEach { type ->
                    val isSelected = type == selectedType
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) darkTeal else Color(0xFFE5E7EB),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(if (isSelected) darkTeal.copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { onTypeSelected(type) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(type.icon, fontSize = 18.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                type.label,
                                color = if (isSelected) darkTeal else Color(0xFF6B7280),
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartCard(
    chartType: ChartType,
    viewMode: ViewMode,
    expenseDistribution: Map<String, Double>,
    incomeDistribution: Map<String, Double>,
    totalExpenses: Double,
    totalIncome: Double,
    budget: Double
) {
    val colors = listOf(
        Color(0xFF3B82F6), Color(0xFFF59E0B), Color(0xFFA78BFA), 
        Color(0xFF10B981), Color(0xFFEF4444), Color(0xFF06B6D4)
    )
    val generalColors = listOf(Color(0xFFEF4444), Color(0xFF10B981), Color(0xFF3B82F6))
    
    val dataEntries: List<Map.Entry<String, Double>>
    val totalValue: Double
    val chartTitle: String
    
    when (viewMode) {
        ViewMode.EXPENSES -> {
            dataEntries = expenseDistribution.entries.sortedByDescending { it.value }.take(6)
            totalValue = totalExpenses
            chartTitle = "Despesas por Categoria"
        }
        ViewMode.INCOME -> {
            dataEntries = incomeDistribution.entries.sortedByDescending { it.value }.take(6)
            totalValue = totalIncome
            chartTitle = "Receitas por Categoria"
        }
        ViewMode.GENERAL -> {
            dataEntries = listOf(
                java.util.AbstractMap.SimpleEntry("Despesas", totalExpenses),
                java.util.AbstractMap.SimpleEntry("Receitas", totalIncome),
                java.util.AbstractMap.SimpleEntry("Orçamento", budget)
            ).filter { it.value > 0 }
            totalValue = maxOf(totalExpenses, totalIncome, budget)
            chartTitle = "Visão Geral"
        }
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(chartTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0E5564))
            
            if (dataEntries.isEmpty() || totalValue == 0.0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📊", fontSize = 40.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Sem dados", color = Color(0xFF9CA3AF))
                    }
                }
            } else {
                when (chartType) {
                    ChartType.DONUT -> DonutChart(dataEntries, totalValue, if (viewMode == ViewMode.GENERAL) generalColors else colors, viewMode)
                    ChartType.BAR -> BarChart(dataEntries, totalValue, if (viewMode == ViewMode.GENERAL) generalColors else colors)
                    ChartType.LINE -> LineChart(dataEntries, totalValue, if (viewMode == ViewMode.GENERAL) generalColors else colors, viewMode)
                }
                
                // Legenda
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    dataEntries.forEachIndexed { index, entry ->
                        val color = if (viewMode == ViewMode.GENERAL) generalColors.getOrElse(index) { Color.Gray }
                        else colors.getOrElse(index) { Color.Gray }
                        val percentage = if (totalValue > 0) (entry.value / totalValue * 100).toInt() else 0
                        
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(color)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    entry.key.take(18) + if (entry.key.length > 18) "..." else "",
                                    fontSize = 13.sp,
                                    color = Color(0xFF374151)
                                )
                            }
                            Row {
                                Text(
                                    String.format("%.0f €", entry.value),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("$percentage%", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DonutChart(
    data: List<Map.Entry<String, Double>>,
    total: Double,
    colors: List<Color>,
    viewMode: ViewMode
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(140.dp)) {
            val stroke = 24f
            val padding = stroke / 2
            val chartSize = Size(size.minDimension - padding * 2, size.minDimension - padding * 2)
            var startAngle = -90f
            
            data.forEachIndexed { index, entry ->
                val sweepAngle = if (total > 0) (entry.value / total * 360).toFloat() else 0f
                drawArc(
                    color = colors.getOrElse(index) { Color.Gray },
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = stroke),
                    size = chartSize,
                    topLeft = Offset(padding, padding)
                )
                startAngle += sweepAngle
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total", color = Color(0xFF9CA3AF), fontSize = 12.sp)
            Text(String.format("%.0f €", total), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
private fun BarChart(data: List<Map.Entry<String, Double>>, maxValue: Double, colors: List<Color>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        data.forEachIndexed { index, entry ->
            val percentage = if (maxValue > 0) (entry.value / maxValue).toFloat() else 0f
            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(entry.key.take(12), fontSize = 12.sp, color = Color(0xFF6B7280))
                    Text(String.format("%.0f €", entry.value), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF3F4F6))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(percentage.coerceIn(0f, 1f))
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.getOrElse(index) { Color.Gray })
                    )
                }
            }
        }
    }
}

@Composable
private fun LineChart(data: List<Map.Entry<String, Double>>, maxValue: Double, colors: List<Color>, viewMode: ViewMode) {
    val mainColor = colors.firstOrNull() ?: Color(0xFF0E5564)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val padding = 20f
            val chartWidth = width - padding * 2
            val chartHeight = height - padding * 2
            
            for (i in 0..3) {
                val y = padding + (chartHeight / 3) * i
                drawLine(Color(0xFFE5E7EB), Offset(padding, y), Offset(width - padding, y), 1f)
            }
            
            if (data.isEmpty()) return@Canvas
            
            val pointSpacing = chartWidth / (data.size - 1).coerceAtLeast(1)
            val points = data.mapIndexed { index, entry ->
                val x = padding + pointSpacing * index
                val y = padding + chartHeight * (1 - (entry.value / maxValue).toFloat().coerceIn(0f, 1f))
                Offset(x, y)
            }
            
            if (points.size > 1) {
                val path = Path()
                path.moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { path.lineTo(it.x, it.y) }
                drawPath(path, mainColor, style = Stroke(width = 3f))
            }
            
            points.forEachIndexed { index, point ->
                drawCircle(Color.White, 8f, point)
                drawCircle(colors.getOrElse(index) { mainColor }, 6f, point)
            }
        }
    }
}

@Composable
private fun CategoryDetailsCard(
    viewMode: ViewMode,
    expenseDistribution: Map<String, Double>,
    incomeDistribution: Map<String, Double>,
    totalExpenses: Double,
    totalIncome: Double
) {
    val darkTeal = Color(0xFF0E5564)
    val distribution = when (viewMode) {
        ViewMode.EXPENSES -> expenseDistribution
        ViewMode.INCOME -> incomeDistribution
        ViewMode.GENERAL -> expenseDistribution
    }
    val total = when (viewMode) {
        ViewMode.EXPENSES -> totalExpenses
        ViewMode.INCOME -> totalIncome
        ViewMode.GENERAL -> totalExpenses
    }
    
    if (distribution.isEmpty()) return
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Detalhes", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkTeal)
            
            distribution.entries.sortedByDescending { it.value }.forEach { entry ->
                val percentage = if (total > 0) (entry.value / total).toFloat() else 0f
                
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(entry.key, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text(String.format("%.2f €", entry.value), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    LinearProgressIndicator(
                        progress = { percentage },
                        color = darkTeal,
                        trackColor = Color(0xFFE5E7EB),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                    Text("${(percentage * 100).toInt()}% do total", color = Color(0xFF9CA3AF), fontSize = 11.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetPreview() { StudyCashTheme { BudgetScreen() } }
