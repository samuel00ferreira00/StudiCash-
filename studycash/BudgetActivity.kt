package pt.iade.ei.studycash

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.iade.ei.studycash.ui.theme.StudyCashTheme

class BudgetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { StudyCashTheme { BudgetScreen() } }
    }
}

@Composable
fun BudgetScreen() {
    val bg = Color(0xFFD6F5F7)

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
        ) {
            Header()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard()
                DistributionCard()
                PerCategoryCard()
            }
            AppBottomBar()
        }
    }
}

@Composable
private fun Header() {
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
            Text("Orçamento", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Acompanhe seus gastos", color = Color.DarkGray, fontSize = 14.sp)
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
private fun SummaryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text("Gasto Total", color = Color.Gray, fontSize = 12.sp); Text("597.00 €", fontSize = 22.sp, fontWeight = FontWeight.Bold) }
                Column(horizontalAlignment = Alignment.End) { Text("Orçamento", color = Color.Gray, fontSize = 12.sp); Text("850.00 €", fontSize = 22.sp, fontWeight = FontWeight.Bold) }
            }
            Text("Progresso", color = Color.Gray, fontSize = 12.sp)
            LinearProgressIndicator(progress = 0.7f, color = Color(0xFF0E5564))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("✔ ", color = Color(0xFF10B981))
                Text("Seus gastos estão dentro do orçamento", color = Color(0xFF10B981))
            }
        }
    }
}

@Composable
private fun DistributionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Distribuição de Gastos", fontWeight = FontWeight.SemiBold)
            // Donut chart simples (placeholder)
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(160.dp)) {
                    val stroke = 28f
                    val size = Size(size.minDimension, size.minDimension)
                    drawArc(Color(0xFF3B82F6), 0f, 120f, false, style = Stroke(width = stroke), size = size)
                    drawArc(Color(0xFFF59E0B), 120f, 80f, false, style = Stroke(width = stroke), size = size)
                    drawArc(Color(0xFFA78BFA), 200f, 60f, false, style = Stroke(width = stroke), size = size)
                    drawArc(Color(0xFF10B981), 260f, 60f, false, style = Stroke(width = stroke), size = size)
                    drawArc(Color(0xFF6B7280), 320f, 40f, false, style = Stroke(width = stroke), size = size)
                }
            }
            // Legenda simples
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                LegendRow(Color(0xFF3B82F6), "Alimentação")
                LegendRow(Color(0xFFF59E0B), "Educação")
                LegendRow(Color(0xFFA78BFA), "Entretenimento")
                LegendRow(Color(0xFF6B7280), "Outros")
                LegendRow(Color(0xFF10B981), "Transporte")
            }
        }
    }
}

@Composable
private fun LegendRow(color: Color, label: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(color, RoundedCornerShape(2.dp)))
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
private fun PerCategoryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Por Categoria", fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Alimentação")
                Text("78%", color = Color(0xFFF59E0B))
            }
            Text("234.00 € de 300.00 €", color = Color.Gray, fontSize = 12.sp)
            LinearProgressIndicator(progress = 0.78f, color = Color(0xFFF59E0B))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BudgetPreview() { StudyCashTheme { BudgetScreen() } }
