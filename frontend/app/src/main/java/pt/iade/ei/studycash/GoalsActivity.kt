package pt.iade.ei.studycash

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect
import kotlinx.coroutines.launch
import pt.iade.ei.studycash.model.Meta
import pt.iade.ei.studycash.model.User
import pt.iade.ei.studycash.network.ApiClient
import pt.iade.ei.studycash.ui.theme.StudyCashTheme
import java.time.LocalDate

class GoalsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { StudyCashTheme { GoalsScreen() } }
    }
}

@Composable
fun GoalsScreen() {
    val darkTeal = Color(0xFF0E5564)
    val lightTeal = Color(0xFF0E97A4)
    val goalYellow = Color(0xFFF59E0B)
    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F8F9), Color(0xFFD6F5F7))
    )

    val title = remember { mutableStateOf("") }
    val value = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }
    val date = remember { mutableStateOf(LocalDate.now().plusMonths(1).toString()) }
    val metas = remember { mutableStateListOf<Meta>() }
    val isLoading = remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val refreshTrigger = remember { mutableStateOf(0) }

    // Recarrega os dados sempre que o ecrã fica visível (onResume)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshTrigger.value += 1
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Carregar metas quando refreshTrigger muda
    LaunchedEffect(refreshTrigger.value) {
        try {
            val userId = pt.iade.ei.studycash.data.SessionManager.getUserId(context)
            if (userId > 0) {
                val res = ApiClient.metaService.byUser(userId)
                if (res.isSuccessful && res.body() != null) {
                    metas.clear()
                    metas.addAll(res.body()!!)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
                GoalsHeader()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card para criar nova meta
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
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF3C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🎯", fontSize = 18.sp)
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Nova Meta",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = darkTeal
                                )
                            }

                            OutlinedTextField(
                                value = title.value,
                                onValueChange = { title.value = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Nome da Meta") },
                                placeholder = { Text("Ex: Viagem de férias") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Star,
                                        contentDescription = null,
                                        tint = goalYellow
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = darkTeal,
                                    focusedLabelColor = darkTeal,
                                    cursorColor = darkTeal
                                )
                            )

                            OutlinedTextField(
                                value = value.value,
                                onValueChange = { value.value = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Valor Objetivo") },
                                placeholder = { Text("0,00") },
                                singleLine = true,
                                leadingIcon = {
                                    Text(
                                        "€",
                                        color = goalYellow,
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

                            OutlinedTextField(
                                value = category.value,
                                onValueChange = { category.value = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Categoria") },
                                placeholder = { Text("Ex: Viagem, Educação") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        Icons.AutoMirrored.Outlined.List,
                                        contentDescription = null,
                                        tint = darkTeal
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = darkTeal,
                                    focusedLabelColor = darkTeal,
                                    cursorColor = darkTeal
                                )
                            )

                            OutlinedTextField(
                                value = date.value,
                                onValueChange = { date.value = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Data Limite") },
                                placeholder = { Text("AAAA-MM-DD") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.DateRange,
                                        contentDescription = null,
                                        tint = darkTeal
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = darkTeal,
                                    focusedLabelColor = darkTeal,
                                    cursorColor = darkTeal
                                )
                            )

                            Button(
                                onClick = {
                                    if (title.value.isEmpty() || value.value.isEmpty()) {
                                        Toast.makeText(context, "Preencha nome e valor", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    isLoading.value = true
                                    scope.launch {
                                        try {
                                            val userId = pt.iade.ei.studycash.data.SessionManager.getUserId(context)
                                            val valorDouble = value.value.replace(",", ".").toDoubleOrNull() ?: 0.0
                                            
                                            val meta = Meta(
                                                nome = title.value,
                                                valorAtual = 0.0,
                                                valorObjetivo = valorDouble,
                                                dataInicio = LocalDate.now().toString(),
                                                dataFim = date.value,
                                                user = if (userId > 0) User(idUser = userId, nome = "", email = "", password = "") else null
                                            )
                                            
                                            val res = ApiClient.metaService.create(meta)
                                            if (res.isSuccessful) {
                                                Toast.makeText(context, "Meta criada! 🎯", Toast.LENGTH_SHORT).show()
                                                title.value = ""
                                                value.value = ""
                                                category.value = ""
                                                date.value = LocalDate.now().plusMonths(1).toString()
                                                
                                                if (userId > 0) {
                                                    val reloadRes = ApiClient.metaService.byUser(userId)
                                                    if (reloadRes.isSuccessful && reloadRes.body() != null) {
                                                        metas.clear()
                                                        metas.addAll(reloadRes.body()!!)
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(context, "Erro: ${res.code()}", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                                            e.printStackTrace()
                                        } finally {
                                            isLoading.value = false
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = goalYellow,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                enabled = !isLoading.value
                            ) {
                                Text(
                                    "Criar Meta",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Título das metas
                    if (metas.isNotEmpty()) {
                        Text(
                            "As suas metas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkTeal,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Metas existentes ou estado vazio
                    if (metas.isEmpty()) {
                        EmptyGoalsCard()
                    } else {
                        metas.forEach { meta ->
                    GoalCard(
                                title = meta.nome,
                                current = meta.valorAtual,
                                target = meta.valorObjetivo,
                                deadline = meta.dataFim ?: "",
                        onClick = {
                            DetailGoalsActivity.start(
                                context = context,
                                        metaId = meta.idMeta ?: -1L,
                                        title = meta.nome,
                                        category = category.value.ifEmpty { "Geral" },
                                        current = meta.valorAtual,
                                        target = meta.valorObjetivo,
                                        deadline = meta.dataFim ?: "",
                                        startDate = meta.dataInicio ?: "",
                                        status = if (meta.valorAtual >= meta.valorObjetivo) "Concluída" else "Em andamento"
                                    )
                                }
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun GoalsHeader() {
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
                "Metas",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = darkTeal
            )
            Text(
                "Alcance seus objetivos",
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
private fun GoalCard(
    title: String,
    current: Double,
    target: Double,
    deadline: String,
    onClick: () -> Unit
) {
    val darkTeal = Color(0xFF0E5564)
    val goalYellow = Color(0xFFF59E0B)
    val progress = if (target > 0) (current / target).coerceIn(0.0, 1.0).toFloat() else 0f
    val percentage = (progress * 100).toInt()
    val isCompleted = current >= target

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isCompleted) Color(0xFFECFDF5) else Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (isCompleted) "✅" else "🎯", fontSize = 20.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF1F2937)
                        )
                        if (deadline.isNotEmpty()) {
                            Text(
                                "Até $deadline",
                                color = Color(0xFF9CA3AF),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "$percentage%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (isCompleted) Color(0xFF10B981) else goalYellow
                    )
                }
            }

            Column {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        String.format("%.2f €", current),
                        fontWeight = FontWeight.Medium,
                        color = darkTeal
                    )
                    Text(
                        String.format("%.2f €", target),
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9CA3AF)
                    )
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    color = if (isCompleted) Color(0xFF10B981) else goalYellow,
                    trackColor = Color(0xFFE5E7EB),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
private fun EmptyGoalsCard() {
    val darkTeal = Color(0xFF0E5564)
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
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
                    .background(Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
            ) {
                Text("🎯", fontSize = 32.sp)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Nenhuma meta criada",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = darkTeal
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Crie metas financeiras para alcançar os seus objetivos! Use o formulário acima para adicionar a sua primeira meta.",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoalsPreview() {
    StudyCashTheme { GoalsScreen() }
}
