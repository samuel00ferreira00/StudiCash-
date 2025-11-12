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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    val bg = Color(0xFFD6F5F7)

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
        ) {
            TransactionsHeader()

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { SummaryRow(income = "700.00 €", expense = "162.30 €") }
                item { SearchAndFilters() }

                val txs = listOf(
                    TxItem("Almoço IADE", "Alimentação • 27/10/2025", "- 12.50 €", false),
                    TxItem("Material escolar", "Alimentação • 27/10/2025", "- 45.50 €", false)
                )
                items(txs) { tx -> TransactionRow(tx) }
                item { Spacer(Modifier.height(8.dp)) }
            }

            AppBottomBar()
        }
    }
}

@Composable
private fun TransactionsHeader() {
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
            Text("Transações", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Histórico completo", color = Color.DarkGray, fontSize = 14.sp)
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
private fun SummaryRow(income: String, expense: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ElevatedCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFE8FFF0)),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Receitas", color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Text(income, fontWeight = FontWeight.Bold)
            }
        }
        ElevatedCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFFEEEE)),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Despesas", color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Text(expense, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SearchAndFilters() {
    val query = remember { mutableStateOf("") }

    OutlinedTextField(
        value = query.value,
        onValueChange = { query.value = it },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Buscar transações...") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )

    Spacer(Modifier.height(12.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(label = "Todas", selected = true)
        FilterChip(label = "Receitas")
        FilterChip(label = "Despesas")
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean = false) {
    val bg = if (selected) Color(0xFF0E5564) else Color(0xFFE6F0F2)
    val fg = if (selected) Color.White else Color(0xFF1F2937)
    Surface(
        color = bg,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = label,
            color = fg,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

data class TxItem(val title: String, val subtitle: String, val amount: String, val positive: Boolean)

@Composable
private fun TransactionRow(tx: TxItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                Text(tx.title, fontWeight = FontWeight.SemiBold)
                Text(tx.subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            Text(
                tx.amount,
                color = if (tx.positive) Color(0xFF10B981) else Color(0xFFEF4444),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionsPreview() {
    StudyCashTheme { TransactionsScreen() }
}
