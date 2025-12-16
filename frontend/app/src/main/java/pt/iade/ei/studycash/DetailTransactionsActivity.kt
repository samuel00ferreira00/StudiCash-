package pt.iade.ei.studycash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pt.iade.ei.studycash.network.ApiClient
import pt.iade.ei.studycash.ui.theme.StudyCashTheme
import android.net.Uri
import java.net.URLEncoder
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView

class DetailTransactionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val transactionId = intent.getLongExtra(EXTRA_TRANSACTION_ID, -1L)
        val isIncome = intent.getBooleanExtra(EXTRA_IS_INCOME, true)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Transação"
        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Geral"
        val date = intent.getStringExtra(EXTRA_DATE) ?: ""
        val amount = intent.getDoubleExtra(EXTRA_AMOUNT, 0.0)
        val notes = intent.getStringExtra(EXTRA_NOTES) ?: ""
        val latitude = if (intent.hasExtra(EXTRA_LATITUDE)) intent.getDoubleExtra(EXTRA_LATITUDE, 0.0) else null
        val longitude = if (intent.hasExtra(EXTRA_LONGITUDE)) intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0) else null
        val localizacao = intent.getStringExtra(EXTRA_LOCALIZACAO)

        setContent {
            StudyCashTheme {
                DetailTransactionScreen(
                    transactionId = transactionId,
                    isIncome = isIncome,
                    title = title,
                    category = category,
                    date = date,
                    amount = amount,
                    notes = notes,
                    latitude = latitude,
                    longitude = longitude,
                    localizacao = localizacao
                )
            }
        }
    }

    companion object {
        private const val EXTRA_TRANSACTION_ID = "extra_transaction_id"
        private const val EXTRA_IS_INCOME = "extra_is_income"
        private const val EXTRA_TITLE = "extra_title"
        private const val EXTRA_CATEGORY = "extra_category"
        private const val EXTRA_DATE = "extra_date"
        private const val EXTRA_AMOUNT = "extra_amount"
        private const val EXTRA_NOTES = "extra_notes"
        private const val EXTRA_LATITUDE = "extra_latitude"
        private const val EXTRA_LONGITUDE = "extra_longitude"
        private const val EXTRA_LOCALIZACAO = "extra_localizacao"

        fun start(
            context: Context,
            transactionId: Long,
            isIncome: Boolean,
            title: String,
            category: String,
            date: String,
            amount: Double,
            notes: String,
            latitude: Double? = null,
            longitude: Double? = null,
            localizacao: String? = null
        ) {
            val i = Intent(context, DetailTransactionsActivity::class.java).apply {
                putExtra(EXTRA_TRANSACTION_ID, transactionId)
                putExtra(EXTRA_IS_INCOME, isIncome)
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_CATEGORY, category)
                putExtra(EXTRA_DATE, date)
                putExtra(EXTRA_AMOUNT, amount)
                putExtra(EXTRA_NOTES, notes)
                latitude?.let { putExtra(EXTRA_LATITUDE, it) }
                longitude?.let { putExtra(EXTRA_LONGITUDE, it) }
                localizacao?.let { putExtra(EXTRA_LOCALIZACAO, it) }
            }
            context.startActivity(i)
        }
    }
}

@Composable
fun DetailTransactionScreen(
    transactionId: Long,
    isIncome: Boolean,
    title: String,
    category: String,
    date: String,
    amount: Double,
    notes: String,
    latitude: Double? = null,
    longitude: Double? = null,
    localizacao: String? = null
) {
    val darkTeal = Color(0xFF0E5564)
    val incomeGreen = Color(0xFF10B981)
    val expenseRed = Color(0xFFEF4444)
    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F8F9), Color(0xFFD6F5F7))
    )
    
    val amountColor = if (isIncome) incomeGreen else expenseRed
    val amountPrefix = if (isIncome) "+" else "-"
    val typeText = if (isIncome) "Receita" else "Despesa"
    val typeEmoji = if (isIncome) "💰" else "💸"
    val typeBgColor = if (isIncome) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val showDeleteDialog = remember { mutableStateOf(false) }
    val isDeleting = remember { mutableStateOf(false) }

    // Diálogo de confirmação de exclusão
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🗑️", fontSize = 24.sp)
                }
            },
            title = {
                Text(
                    "Eliminar Transação",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    "Tens a certeza que queres eliminar \"$title\"? Esta ação não pode ser desfeita.",
                    textAlign = TextAlign.Center,
                    color = Color(0xFF6B7280)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (transactionId > 0) {
                            isDeleting.value = true
                            scope.launch {
                                try {
                                    val res = ApiClient.transacaoService.delete(transactionId)
                                    if (res.isSuccessful) {
                                        Toast.makeText(context, "Transação eliminada! 🗑️", Toast.LENGTH_SHORT).show()
                                        (context as? ComponentActivity)?.finish()
                                    } else {
                                        Toast.makeText(context, "Erro ao eliminar: ${res.code()}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isDeleting.value = false
                                    showDeleteDialog.value = false
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = expenseRed),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isDeleting.value
                ) {
                    if (isDeleting.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Eliminar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text("Cancelar", color = Color(0xFF6B7280))
                }
            }
        )
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
                            "Detalhes",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkTeal
                        )
                        Text(
                            typeText,
                            color = amountColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card principal com valor
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
                            // Ícone do tipo
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(typeBgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(typeEmoji, fontSize = 32.sp)
                            }
                            
                            Spacer(Modifier.height(16.dp))
                            
                            // Título
                            Text(
                                title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = darkTeal,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(Modifier.height(8.dp))
                            
                            // Badge do tipo
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(typeBgColor)
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    typeText,
                                    color = amountColor,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                            
                            Spacer(Modifier.height(24.dp))
                            
                            // Valor grande
                            Text(
                                "$amountPrefix${formatCurrency(amount)}",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = amountColor
                            )
                            
                            Spacer(Modifier.height(8.dp))
                            
                            Text(
                                date,
                                fontSize = 14.sp,
                                color = Color(0xFF9CA3AF)
                            )
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
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Informações",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = darkTeal
                            )
                            
                            Spacer(Modifier.height(12.dp))
                            
                            // Categoria
                            DetailInfoRow(
                                icon = "📂",
                                label = "Categoria",
                                value = category.ifEmpty { "Sem categoria" }
                            )
                            
                            // Data
                            DetailInfoRow(
                                icon = "📅",
                                label = "Data",
                                value = date.ifEmpty { "Não definida" }
                            )
                            
                            // Valor
                            DetailInfoRow(
                                icon = if (isIncome) "📈" else "📉",
                                label = "Valor",
                                value = "$amountPrefix${formatCurrency(amount)}",
                                valueColor = amountColor
                            )
                            
                            // Tipo
                            DetailInfoRow(
                                icon = typeEmoji,
                                label = "Tipo",
                                value = typeText,
                                valueColor = amountColor
                            )
                        }
                    }
                    
                    // Card de localização (se tiver localização manual ou GPS)
                    if (!localizacao.isNullOrEmpty() || (latitude != null && longitude != null)) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📍", fontSize = 18.sp)
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        "Localização",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = darkTeal
                                    )
                                }
                                Spacer(Modifier.height(12.dp))
                                
                                // Mostrar localização manual se existir
                                if (!localizacao.isNullOrEmpty()) {
                                    Text(
                                        localizacao,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1F2937)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                                
                                // Mostrar mapa se existirem coordenadas GPS
                                if (latitude != null && longitude != null) {
                                    // Mapa interativo com Google Maps embed (não redireciona)
                                    val googleMapsEmbedUrl = "https://maps.google.com/maps?q=$latitude,$longitude&z=16&output=embed"
                                    
                                    // Container do mapa
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                                    ) {
                                        // Mapa interativo dentro da app
                                        AndroidView(
                                            factory = { ctx ->
                                                WebView(ctx).apply {
                                                    webViewClient = WebViewClient()
                                                    settings.javaScriptEnabled = true
                                                    settings.domStorageEnabled = true
                                                    settings.setSupportZoom(true)
                                                    settings.builtInZoomControls = true
                                                    settings.displayZoomControls = false
                                                    settings.loadWithOverviewMode = true
                                                    settings.useWideViewPort = true
                                                    setBackgroundColor(android.graphics.Color.parseColor("#F3F4F6"))
                                                    loadUrl(googleMapsEmbedUrl)
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(280.dp)
                                        )
                                        
                                        // Barra de informação
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White)
                                                .padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFECFDF5)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("📍", fontSize = 16.sp)
                                            }
                                            Spacer(Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    "Localização da transação",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = darkTeal
                                                )
                                                Text(
                                                    "Lat: ${String.format("%.6f", latitude)} | Lng: ${String.format("%.6f", longitude)}",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF9CA3AF)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Card de notas (se existirem)
                    if (notes.isNotEmpty() && notes != "Nenhuma") {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📝", fontSize = 18.sp)
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        "Notas",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = darkTeal
                                    )
                                }
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    notes,
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280),
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                    
                    // Botões de ação
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botão Editar
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = darkTeal)
                        ) {
                            Icon(
                                Icons.Outlined.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Editar", fontWeight = FontWeight.SemiBold)
                        }
                        
                        // Botão Eliminar
                        Button(
                            onClick = { showDeleteDialog.value = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = expenseRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Eliminar", fontWeight = FontWeight.SemiBold)
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    icon: String,
    label: String,
    value: String,
    valueColor: Color = Color(0xFF1F2937)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Text(label, color = Color(0xFF6B7280), fontSize = 14.sp)
        }
        Text(
            value,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            fontSize = 14.sp
        )
    }
}

private fun formatCurrency(value: Double): String {
    return String.format("%.2f €", value)
}

@Preview(showBackground = true)
@Composable
fun DetailTransactionPreview() {
    StudyCashTheme {
        DetailTransactionScreen(
            transactionId = 1L,
            isIncome = true,
            title = "Mesada",
            category = "Família",
            date = "26/10/2025",
            amount = 500.00,
            notes = "Recebido mensalmente dos meus pais",
            latitude = 38.7223,
            longitude = -9.1393
        )
    }
}
