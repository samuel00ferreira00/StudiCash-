package pt.iade.ei.studycash

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppBottomBar() {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.padding(bottom = 5.dp),
        tonalElevation = 6.dp,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomItem(R.drawable.home, "Início") {
                context.startActivity(Intent(context, HomeActivity::class.java))
            }
            BottomItem(R.drawable.lock, "Transações") {
                context.startActivity(Intent(context, TransactionsActivity::class.java))
            }
            BottomCenter(R.drawable.plusbutton) {
                context.startActivity(Intent(context, NewRevenueActivity::class.java))
            }
            BottomItem(R.drawable.piechart, "Orçamento") {
                context.startActivity(Intent(context, BudgetActivity::class.java))
            }
            BottomItem(R.drawable.target, "Metas") {
                context.startActivity(Intent(context, GoalsActivity::class.java))
            }
        }
    }
}

@Composable
private fun BottomCenter(@DrawableRes icon: Int, onClick: () -> Unit) {
    Image(
        painter = painterResource(id = icon),
        contentDescription = null,
        modifier = Modifier
            .size(24.dp)
            .clickable { onClick() }
    )
}

@Composable
private fun BottomItem(@DrawableRes icon: Int, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }) {
        Image(painter = painterResource(id = icon), contentDescription = label, modifier = Modifier.size(24.dp))
        Text(label, fontSize = 12.sp)
    }
}
