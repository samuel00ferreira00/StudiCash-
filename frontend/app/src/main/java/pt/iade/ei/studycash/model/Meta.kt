package pt.iade.ei.studycash.model
data class Meta(
  val idMeta: Long? = null,
  val nome: String,
  val valorAtual: Double,
  val valorObjetivo: Double,
  val dataInicio: String? = null,
  val dataFim: String? = null,
  val user: User? = null
)