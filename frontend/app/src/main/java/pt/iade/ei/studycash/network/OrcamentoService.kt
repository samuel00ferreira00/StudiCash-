package pt.iade.ei.studycash.network
import retrofit2.http.*; import retrofit2.Response; import pt.iade.ei.studycash.model.Orcamento
interface OrcamentoService{
 @GET("api/orcamentos") suspend fun all(): Response<List<Orcamento>>
 @POST("api/orcamentos") suspend fun create(@Body o: Orcamento): Response<Orcamento>
 @PUT("api/orcamentos/{id}") suspend fun update(@Path("id") id: Long, @Body o: Orcamento): Response<Orcamento>
 @DELETE("api/orcamentos/{id}") suspend fun delete(@Path("id") id: Long): Response<Unit>
}