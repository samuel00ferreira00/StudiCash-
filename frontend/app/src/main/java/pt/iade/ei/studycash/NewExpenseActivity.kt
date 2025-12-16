package pt.iade.ei.studycash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pt.iade.ei.studycash.model.Transacao
import pt.iade.ei.studycash.network.ApiClient
import pt.iade.ei.studycash.ui.theme.StudyCashTheme
import java.time.LocalDate

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
    val darkTeal = Color(0xFF0E5564)
    val lightTeal = Color(0xFF0E97A4)
    val expenseRed = Color(0xFFEF4444)
    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F8F9), Color(0xFFD6F5F7))
    )

    val title = remember { mutableStateOf("") }
    val value = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }
    val date = remember { mutableStateOf(LocalDate.now().toString()) }
    val localizacao = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    val currentLatitude = remember { mutableStateOf<Double?>(null) }
    val currentLongitude = remember { mutableStateOf<Double?>(null) }
    val locationStatus = remember { mutableStateOf("A verificar...") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Função para obter localização
    fun fetchLocation() {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationStatus.value = "A obter GPS..."
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        currentLatitude.value = location.latitude
                        currentLongitude.value = location.longitude
                        locationStatus.value = "GPS obtido!"
                    } else {
                        locationStatus.value = "GPS indisponível"
                    }
                }.addOnFailureListener {
                    locationStatus.value = "Erro ao obter GPS"
                }
            }
        } catch (e: Exception) {
            locationStatus.value = "Erro: ${e.message}"
        }
    }
    
    // Launcher para solicitar permissão
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (fineLocationGranted || coarseLocationGranted) {
            fetchLocation()
        } else {
            locationStatus.value = "Permissão negada"
        }
    }
    
    // Verificar/solicitar permissão ao iniciar
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (hasPermission) {
            fetchLocation()
        } else {
            // Solicitar permissão
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
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
                ExpenseHeader()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TypeSelector(isExpense = true)

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Valor em destaque
                            Column {
                                Text(
                                    "Valor da Despesa",
                                    color = Color(0xFF6B7280),
                                    fontSize = 14.sp
                                )
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = value.value,
                                    onValueChange = { value.value = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("0,00", fontSize = 32.sp, color = Color(0xFFD1D5DB)) },
                                    leadingIcon = {
                                        Text(
                                            "€",
                                            color = expenseRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(16.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = expenseRed,
                                        unfocusedBorderColor = Color(0xFFE5E7EB),
                                        cursorColor = expenseRed
                                    ),
                                    singleLine = true
                                )
                            }

                            OutlinedTextField(
                                value = title.value,
                                onValueChange = { title.value = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Descrição") },
                                placeholder = { Text("Ex: Almoço no RU") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Edit,
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
                                value = category.value,
                                onValueChange = { category.value = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Categoria") },
                                placeholder = { Text("Ex: Alimentação, Transporte") },
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
                                label = { Text("Data") },
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

                            OutlinedTextField(
                                value = localizacao.value,
                                onValueChange = { localizacao.value = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Localização (opcional)") },
                                placeholder = { Text("Ex: Lisboa, Café Central") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.LocationOn,
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
                            
                            // Card de localização GPS
                            if (currentLatitude.value != null && currentLongitude.value != null) {
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFECFDF5)),
                                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF10B981)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("📍", fontSize = 18.sp)
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "GPS Ativo ✓",
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF065F46),
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                "Lat: ${String.format("%.6f", currentLatitude.value)}",
                                                color = Color(0xFF047857),
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                "Lng: ${String.format("%.6f", currentLongitude.value)}",
                                                color = Color(0xFF047857),
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            } else {
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            // Tentar obter localização novamente
                                            val hasPermission = ContextCompat.checkSelfPermission(
                                                context, Manifest.permission.ACCESS_FINE_LOCATION
                                            ) == PackageManager.PERMISSION_GRANTED
                                            if (hasPermission) {
                                                try {
                                                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                                                    locationStatus.value = "A obter GPS..."
                                                    fusedLocationClient.getCurrentLocation(
                                                        Priority.PRIORITY_HIGH_ACCURACY,
                                                        CancellationTokenSource().token
                                                    ).addOnSuccessListener { location ->
                                                        if (location != null) {
                                                            currentLatitude.value = location.latitude
                                                            currentLongitude.value = location.longitude
                                                            locationStatus.value = "GPS obtido!"
                                                        } else {
                                                            locationStatus.value = "GPS indisponível"
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    locationStatus.value = "Erro"
                                                }
                                            }
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFEF3C7)),
                                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF59E0B)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("📍", fontSize = 18.sp)
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                locationStatus.value,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF92400E),
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                "Toque para tentar novamente",
                                                color = Color(0xFFB45309),
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (title.value.isEmpty() || value.value.isEmpty()) {
                                        Toast.makeText(context, "Preencha descrição e valor", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    isLoading.value = true
                                    scope.launch {
                                        try {
                                            val valorDouble = value.value.replace(",", ".").toDoubleOrNull() ?: 0.0
                                            val dataFinal = date.value.ifEmpty { LocalDate.now().toString() }
                                            val userId = pt.iade.ei.studycash.data.SessionManager.getUserId(context)
                                            
                                            val t = Transacao(
                                                descricao = title.value,
                                                valor = valorDouble,
                                                tipo = "Despesa",
                                                dataTransacao = dataFinal,
                                                categoria = null,
                                                latitude = currentLatitude.value,
                                                longitude = currentLongitude.value,
                                                localizacao = localizacao.value.ifEmpty { null }
                                            )
                                            
                                            val res = if (userId > 0) {
                                                ApiClient.transacaoService.createForUser(userId, t)
                                            } else {
                                                ApiClient.transacaoService.create(t)
                                            }
                                            
                                            if (res.isSuccessful) {
                                                Toast.makeText(context, "Despesa adicionada! 💸", Toast.LENGTH_LONG).show()
                                                (context as? ComponentActivity)?.finish()
                                            } else {
                                                Toast.makeText(context, "Erro ao criar: ${res.code()}", Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
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
                                    containerColor = expenseRed,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                enabled = !isLoading.value
                            ) {
                                Text(
                                    "Adicionar Despesa",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
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
private fun ExpenseHeader() {
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
                "Nova Despesa",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = darkTeal
            )
            Text(
                "Registar um gasto",
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
private fun TypeSelector(isExpense: Boolean) {
    val context = LocalContext.current
    val darkTeal = Color(0xFF0E5564)
    val expenseRed = Color(0xFFEF4444)
    val incomeGreen = Color(0xFF10B981)
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Despesa
        ElevatedCard(
            modifier = Modifier
                .weight(1f)
                .clickable { },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (isExpense) Color(0xFFFEF2F2) else Color.White
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isExpense) 8.dp else 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isExpense) Modifier.border(2.dp, expenseRed, RoundedCornerShape(16.dp))
                        else Modifier
                    )
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isExpense) expenseRed else Color(0xFFF3F4F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "↓",
                            color = if (isExpense) Color.White else Color(0xFF9CA3AF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Despesa",
                        fontWeight = if (isExpense) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isExpense) expenseRed else Color(0xFF6B7280)
                    )
                }
            }
        }
        
        // Receita
        ElevatedCard(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    context.startActivity(Intent(context, NewRevenueActivity::class.java))
                    (context as? ComponentActivity)?.finish()
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (!isExpense) Color(0xFFECFDF5) else Color.White
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (!isExpense) 8.dp else 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "↑",
                            color = Color(0xFF9CA3AF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Receita",
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewExpensePreview() {
    StudyCashTheme { NewExpenseScreen() }
}
