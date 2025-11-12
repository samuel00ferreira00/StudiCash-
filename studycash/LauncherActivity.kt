package pt.iade.ei.studycash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.iade.ei.studycash.ui.theme.StudyCashTheme

class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyCashTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    LauncherMenu(
                        onOpen = { clazz -> startActivity(Intent(this, clazz)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LauncherMenu(onOpen: (Class<*>) -> Unit) {
    val activities: List<Pair<String, Class<*>>> = listOf(
        "Welcome (MainActivity)" to MainActivity::class.java,
        "Login" to LoginActivity::class.java,
        "Register" to RegisterActivity::class.java,
        "Home" to HomeActivity::class.java,
        "Transactions" to TransactionsActivity::class.java,
        "New Revenue" to NewRevenueActivity::class.java,
        "New Expense" to NewExpenseActivity::class.java,
        "Profile" to ProfileActivity::class.java,
        "Goals" to GoalsActivity::class.java,
        "Budget" to BudgetActivity::class.java,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 48.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        activities.forEach { (label, clazz) ->
            Button(
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                onClick = { onOpen(clazz) }
            ) { Text(label) }
        }
    }
}
