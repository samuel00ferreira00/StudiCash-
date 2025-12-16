package pt.iade.ei.studycash.network

import retrofit2.http.*
import retrofit2.Response
import pt.iade.ei.studycash.model.Carteira

interface CarteiraService {
    @GET("api/carteiras")
    suspend fun all(): Response<List<Carteira>>

    @GET("api/carteiras/user/{userId}")
    suspend fun byUser(@Path("userId") userId: Long): Response<List<Carteira>>

    @GET("api/carteiras/user/{userId}/first")
    suspend fun firstByUser(@Path("userId") userId: Long): Response<Carteira>

    @POST("api/carteiras")
    suspend fun create(@Body c: Carteira): Response<Carteira>

    @PUT("api/carteiras/{id}")
    suspend fun update(@Path("id") id: Long, @Body c: Carteira): Response<Carteira>

    @DELETE("api/carteiras/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Unit>
}