package pt.iade.ei.studycash.network

import pt.iade.ei.studycash.network.dto.UtilizadorDto
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("api/utilizadores")
    fun getUtilizadores(): Call<List<UtilizadorDto>>
}