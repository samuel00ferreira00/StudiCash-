package pt.iade.ei.studycash.network
import retrofit2.http.*; import retrofit2.Response; import pt.iade.ei.studycash.model.Categoria
interface CategoriaService{
 @GET("api/categorias") suspend fun all(): Response<List<Categoria>>
 @POST("api/categorias") suspend fun create(@Body c: Categoria): Response<Categoria>
 @PUT("api/categorias/{id}") suspend fun update(@Path("id") id: Long, @Body c: Categoria): Response<Categoria>
 @DELETE("api/categorias/{id}") suspend fun delete(@Path("id") id: Long): Response<Unit>
}