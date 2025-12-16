package pt.iade.ei.studycash.network

import retrofit2.http.*
import retrofit2.Response
import pt.iade.ei.studycash.model.User

interface UserService {
    @POST("api/users")
    suspend fun create(@Body user: User): Response<User>

    @GET("api/users")
    suspend fun all(): Response<List<User>>

    @GET("api/users/{id}")
    suspend fun getById(@Path("id") id: Long): Response<User>

    @POST("api/users/login")
    suspend fun login(@Body credentials: Map<String, String>): Response<User>

    @PUT("api/users/{id}")
    suspend fun update(@Path("id") id: Long, @Body user: User): Response<User>

    @DELETE("api/users/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Unit>
}
