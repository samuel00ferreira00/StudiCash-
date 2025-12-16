package pt.iade.ei.studycash.model
data class Transacao(
  val idTransacao: Long? = null,
  val descricao: String,
  val valor: Double,
  val tipo: String,
  val dataTransacao: String,
  val carteira: Carteira? = null,
  val categoria: Categoria? = null,
  val latitude: Double? = null,
  val longitude: Double? = null,
  val localizacao: String? = null
)