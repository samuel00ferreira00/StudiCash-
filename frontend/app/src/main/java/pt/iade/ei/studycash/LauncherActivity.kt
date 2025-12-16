package pt.iade.ei.studycash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import pt.iade.ei.studycash.data.SessionManager

class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Verificar se o utilizador está logado
        val isLoggedIn = SessionManager.isLoggedIn(this)
        
        if (isLoggedIn) {
            // Redirecionar para Home se já está logado
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            // Redirecionar para Login se não está logado
            startActivity(Intent(this, LoginActivity::class.java))
        }
        
        finish() // Fechar o LauncherActivity
    }
}
