package pt.iade.ei.studycash.network

import retrofit2.http.*
import retrofit2.Response
import pt.iade.ei.studycash.model.Transacao

interface TransacaoService {
    @GET("api/transacoes")
    suspend fun all(): Response<List<Transacao>>

    @GET("api/transacoes/user/{userId}")
    suspend fun byUser(@Path("userId") userId: Long): Response<List<Transacao>>

    @GET("api/transacoes/carteira/{carteiraId}")
    suspend fun byCarteira(@Path("carteiraId") carteiraId: Long): Response<List<Transacao>>

    @POST("api/transacoes")
    suspend fun create(@Body t: Transacao): Response<Transacao>

    @POST("api/transacoes/user/{userId}")
    suspend fun createForUser(@Path("userId") userId: Long, @Body t: Transacao): Response<Transacao>

    @PUT("api/transacoes/{id}")
    suspend fun update(@Path("id") id: Long, @Body t: Transacao): Response<Transacao>

    @DELETE("api/transacoes/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Unit>
}