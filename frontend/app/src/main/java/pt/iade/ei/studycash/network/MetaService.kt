package pt.iade.ei.studycash.network

import retrofit2.http.*
import retrofit2.Response
import pt.iade.ei.studycash.model.Meta

interface MetaService {
    @GET("api/metas")
    suspend fun all(): Response<List<Meta>>

    @GET("api/metas/user/{userId}")
    suspend fun byUser(@Path("userId") userId: Long): Response<List<Meta>>

    @POST("api/metas")
    suspend fun create(@Body m: Meta): Response<Meta>

    @PUT("api/metas/{id}")
    suspend fun update(@Path("id") id: Long, @Body m: Meta): Response<Meta>

    @DELETE("api/metas/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Unit>
}