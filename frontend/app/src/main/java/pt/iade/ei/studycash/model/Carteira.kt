package pt.iade.ei.studycash.model
data class Carteira(
  val idCarteira: Long? = null,
  val saldo: Double,
  val user: User? = null
)