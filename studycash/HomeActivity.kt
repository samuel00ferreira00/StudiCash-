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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.iade.ei.studycash.ui.theme.StudyCashTheme
import androidx.compose.runtime.LaunchedEffect
import pt.iade.ei.studycash.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    val bg = Color(0xFFD6F5F7)
    val utilizadoresCountState = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        RetrofitClient.api.getUtilizadores().enqueue(object : Callback<List<pt.iade.ei.studycash.network.dto.UtilizadorDto>> {
            override fun onResponse(
                call: Call<List<pt.iade.ei.studycash.network.dto.UtilizadorDto>>,
                response: Response<List<pt.iade.ei.studycash.network.dto.UtilizadorDto>>
            ) {
                if (response.isSuccessful) {
                    utilizadoresCountState.value = response.body()?.size
                } else {
                    utilizadoresCountState.value = 0
                }
            }

            override fun onFailure(
                call: Call<List<pt.iade.ei.studycash.network.dto.UtilizadorDto>>,
                t: Throwable
            ) {
                utilizadoresCountState.value = 0
            }
        })
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
        ) {
            HeaderSection(userName = "João Silva")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Utilizadores: ${utilizadoresCountState.value ?: "-"}",
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    BalanceCard(
                        balance = "1.234,50 €",
                        income = "500,00 €",
                        expense = "266,40 €"
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Transações Recentes", style = MaterialTheme.typography.titleMedium)
                        Text("Ver todas", color = Color.Gray, fontSize = 14.sp)
                    }
                }

                val txs = listOf(
                    RecentTx(
                        title = "Almoço RU",
                        subtitle = "Alimentação • Hoje",
                        amount = "- 12.50 €",
                        positive = false
                    ),
                    RecentTx(
                        title = "Mesada",
                        subtitle = "Receita • 26/10/2025",
                        amount = "+ 500.00 €",
                        positive = true
                    )
                )
                items(txs) { tx ->
                    TransactionCard(tx)
                }

                item { Spacer(Modifier.height(8.dp)) }
            }

            AppBottomBar()
        }
    }
}

@Composable
private fun HeaderSection(userName: String) {
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
            Text("Olá,", color = Color.DarkGray)
            Text(userName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
private fun BalanceCard(balance: String, income: String, expense: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = R.drawable.wallet), contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Saldo Atual", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(12.dp))
            Text(balance, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Receitas", color = Color(0xFF10B981))
                    Text(income, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Despesas", color = Color(0xFFEF4444))
                    Text(expense, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

data class RecentTx(
    val title: String,
    val subtitle: String,
    val amount: String,
    val positive: Boolean
)

@Composable
private fun TransactionCard(tx: RecentTx) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (tx.positive) Color(0xFFE8FFF0) else Color(0xFFFFEEEE),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = if (tx.positive) R.drawable.increase else R.drawable.decrease),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(tx.title, fontWeight = FontWeight.SemiBold)
                    Text(tx.subtitle, color = Color.Gray, fontSize = 12.sp)
                }
            }
            Text(
                tx.amount,
                color = if (tx.positive) Color(0xFF10B981) else Color(0xFFEF4444),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End
            )
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
