package pt.iade.ei.studycash.model
data class Orcamento(
  val idOrcamento: Long? = null,
  val mes: String,
  val limite: Double,
  val gastoAtual: Double,
  val carteira: Carteira? = null
)