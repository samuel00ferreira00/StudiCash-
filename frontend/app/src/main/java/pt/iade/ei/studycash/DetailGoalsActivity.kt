package pt.iade.ei.studycash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pt.iade.ei.studycash.network.ApiClient
import pt.iade.ei.studycash.ui.theme.StudyCashTheme

class DetailGoalsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val metaId = intent.getLongExtra(EXTRA_META_ID, -1L)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Meta"
        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Geral"
        val current = intent.getDoubleExtra(EXTRA_CURRENT, 0.0)
        val target = intent.getDoubleExtra(EXTRA_TARGET, 100.0)
        val deadline = intent.getStringExtra(EXTRA_DEADLINE) ?: ""
        val startDate = intent.getStringExtra(EXTRA_START_DATE) ?: ""
        val status = intent.getStringExtra(EXTRA_STATUS) ?: "Em andamento"

        setContent {
            StudyCashTheme {
                DetailGoalsScreen(
                    metaId = metaId,
                    title = title,
                    category = category,
                    initialCurrent = current,
                    target = target,
                    deadline = deadline,
                    startDate = startDate,
                    status = status
                )
            }
        }
    }

    companion object {
        private const val EXTRA_META_ID = "extra_meta_id"
        private const val EXTRA_TITLE = "extra_title"
        private const val EXTRA_CATEGORY = "extra_category"
        private const val EXTRA_CURRENT = "extra_current"
        private const val EXTRA_TARGET = "extra_target"
        private const val EXTRA_DEADLINE = "extra_deadline"
        private const val EXTRA_START_DATE = "extra_start_date"
        private const val EXTRA_STATUS = "extra_status"

        fun start(
            context: Context,
            metaId: Long,
            title: String,
            category: String,
            current: Double,
            target: Double,
            deadline: String,
            startDate: String,
            status: String
        ) {
            val i = Intent(context, DetailGoalsActivity::class.java).apply {
                putExtra(EXTRA_META_ID, metaId)
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_CATEGORY, category)
                putExtra(EXTRA_CURRENT, current)
                putExtra(EXTRA_TARGET, target)
                putExtra(EXTRA_DEADLINE, deadline)
                putExtra(EXTRA_START_DATE, startDate)
                putExtra(EXTRA_STATUS, status)
            }
            context.startActivity(i)
        }
    }
}

@Composable
fun DetailGoalsScreen(
    metaId: Long,
    title: String,
    category: String,
    initialCurrent: Double,
    target: Double,
    deadline: String,
    startDate: String,
    status: String
) {
    val darkTeal = Color(0xFF0E5564)
    val lightTeal = Color(0xFF0E97A4)
    val goalYellow = Color(0xFFF59E0B)
    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F8F9), Color(0xFFD6F5F7))
    )
    
    val currentValue = remember { mutableStateOf(initialCurrent) }
    val progress = if (target > 0.0) (currentValue.value / target).coerceIn(0.0, 1.0) else 0.0
    val percentage = (progress * 100).toInt()
    val isCompleted = currentValue.value >= target
    val remaining = (target - currentValue.value).coerceAtLeast(0.0)
    
    val context = LocalContext.current
    val showContributionDialog = remember { mutableStateOf(false) }
    val contributionAmount = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { (context as? ComponentActivity)?.finish() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = darkTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            "Detalhes da Meta",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkTeal
                        )
                        Text(
                            title,
                            color = Color(0xFF6B7280),
                            fontSize = 14.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card principal com progresso circular
                    ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Ícone/Status
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(if (isCompleted) Color(0xFFECFDF5) else Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (isCompleted) "✅" else "🎯", fontSize = 28.sp)
                            }
                            
                            Spacer(Modifier.height(12.dp))
                            
                            Text(
                                title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = darkTeal
                            )
                            
                            Text(
                                category,
                                color = Color(0xFF6B7280),
                                fontSize = 14.sp
                            )
                            
                            Spacer(Modifier.height(24.dp))
                            
                            // Progresso circular grande
                            ProgressRing(
                                progress = progress.toFloat(),
                                percentage = percentage,
                                current = currentValue.value,
                                target = target,
                                isCompleted = isCompleted
                            )
                            
                            Spacer(Modifier.height(24.dp))
                            
                            // Barra de progresso linear
                            Column(Modifier.fillMaxWidth()) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Progresso", color = Color(0xFF6B7280), fontSize = 12.sp)
                                    Text("$percentage%", color = darkTeal, fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { progress.toFloat() },
                                    color = if (isCompleted) Color(0xFF10B981) else goalYellow,
                                    trackColor = Color(0xFFE5E7EB),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                )
                            }
                        }
                    }
                    
                    // Card de informações
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Informações", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkTeal)
                            
                            // Valor atual
                            InfoRow(
                                icon = "💰",
                                label = "Valor Atual",
                                value = formatCurrency(currentValue.value),
                                valueColor = darkTeal
                            )
                            
                            // Valor objetivo
                            InfoRow(
                                icon = "🎯",
                                label = "Objetivo",
                                value = formatCurrency(target),
                                valueColor = goalYellow
                            )
                            
                            // Falta
                            InfoRow(
                                icon = "📊",
                                label = "Falta",
                                value = formatCurrency(remaining),
                                valueColor = if (isCompleted) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                            
                            // Data limite
                            if (deadline.isNotEmpty()) {
                                InfoRow(
                                    icon = "📅",
                                    label = "Data Limite",
                                    value = deadline,
                                    valueColor = Color(0xFF6B7280)
                                )
                            }
                            
                            // Status
                            InfoRow(
                                icon = if (isCompleted) "✅" else "⏳",
                                label = "Status",
                                value = if (isCompleted) "Concluída!" else status,
                                valueColor = if (isCompleted) Color(0xFF10B981) else goalYellow
                            )
                        }
                    }
                    
                    // Formulário de contribuição (se não concluída)
                    if (!isCompleted) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFECFDF5)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("💵", fontSize = 18.sp)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Adicionar Contribuição",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = darkTeal
                                    )
                                }
                                
                                OutlinedTextField(
                                    value = contributionAmount.value,
                                    onValueChange = { contributionAmount.value = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Valor a adicionar") },
                                    placeholder = { Text("0,00") },
                                    singleLine = true,
                                    leadingIcon = {
                                        Text(
                                            "€",
                                            color = Color(0xFF10B981),
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(start = 12.dp)
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF10B981),
                                        focusedLabelColor = Color(0xFF10B981),
                                        cursorColor = Color(0xFF10B981)
                                    )
                                )
                                
                                Button(
                                    onClick = {
                                        val amount = contributionAmount.value.replace(",", ".").toDoubleOrNull()
                                        if (amount != null && amount > 0 && metaId > 0) {
                                            isLoading.value = true
                                            scope.launch {
                                                try {
                                                    val userId = pt.iade.ei.studycash.data.SessionManager.getUserId(context)
                                                    val newValue = currentValue.value + amount
                                                    
                                                    val meta = pt.iade.ei.studycash.model.Meta(
                                                        idMeta = metaId,
                                                        nome = title,
                                                        valorAtual = newValue,
                                                        valorObjetivo = target,
                                                        dataInicio = startDate,
                                                        dataFim = deadline,
                                                        user = if (userId > 0) pt.iade.ei.studycash.model.User(idUser = userId, nome = "", email = "", password = "") else null
                                                    )
                                                    
                                                    val res = ApiClient.metaService.update(metaId, meta)
                                                    if (res.isSuccessful) {
                                                        currentValue.value = newValue
                                                        Toast.makeText(context, "Contribuição de ${formatCurrency(amount)} adicionada! 🎉", Toast.LENGTH_SHORT).show()
                                                        contributionAmount.value = ""
                                                    } else {
                                                        Toast.makeText(context, "Erro ao atualizar: ${res.code()}", Toast.LENGTH_SHORT).show()
                                                    }
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                                                    e.printStackTrace()
                                                } finally {
                                                    isLoading.value = false
                                                }
                                            }
                                        } else if (metaId <= 0) {
                                            Toast.makeText(context, "Erro: Meta não identificada", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Insira um valor válido", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isLoading.value
                                ) {
                                    if (isLoading.value) {
                                        androidx.compose.material3.CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text("Adicionar", fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                    
                    // Botões de ação
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { (context as? ComponentActivity)?.finish() },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = darkTeal)
                        ) {
                            Text("Voltar", fontWeight = FontWeight.SemiBold)
                        }
                        
                        if (isCompleted) {
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Parabéns! Meta concluída! 🎉", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("🎉 Celebrar!", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: String, label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Text(label, color = Color(0xFF6B7280), fontSize = 14.sp)
        }
        Text(value, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

@Composable
private fun ProgressRing(
    progress: Float,
    percentage: Int,
    current: Double,
    target: Double,
    isCompleted: Boolean
) {
    val darkTeal = Color(0xFF0E5564)
    val goalYellow = Color(0xFFF59E0B)
    val completedGreen = Color(0xFF10B981)
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(160.dp)
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val stroke = 14f
            val diameter = size.minDimension - stroke
            val topLeft = Offset(stroke / 2, stroke / 2)
            
            // Fundo
            drawArc(
                color = Color(0xFFE5E7EB),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            
            // Progresso
            drawArc(
                color = if (isCompleted) completedGreen else goalYellow,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "$percentage%",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) completedGreen else darkTeal
            )
            Text(
                "${formatCurrency(current)} / ${formatCurrency(target)}",
                fontSize = 11.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

private fun formatCurrency(value: Double): String {
    return String.format("%.2f €", value)
}

@Preview(showBackground = true)
@Composable
fun DetailGoalsPreview() {
    StudyCashTheme {
        DetailGoalsScreen(
            metaId = 1L,
            title = "Viagem de Férias",
            category = "Entretenimento",
            initialCurrent = 150.0,
            target = 500.0,
            deadline = "2025-06-15",
            startDate = "2025-01-01",
            status = "Em andamento"
        )
    }
}
