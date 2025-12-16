package pt.iade.ei.studycash.model

data class User(
    val idUser: Long? = null,
    val nome: String,
    val email: String,
    val password: String,
    val notificacoes: Boolean = true
)
