package pt.iade.ei.studycash.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object ApiClient {
    // Para EMULADOR Android: use 10.0.2.2 (aponta para localhost da máquina)
    // Para DISPOSITIVO FÍSICO: use o IP da tua rede (ex: 10.208.204.4)
    // IMPORTANTE: O telefone e o computador devem estar na mesma rede Wi-Fi!
    private const val BASE_URL = "http://172.20.10.3:8080/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userService: UserService = retrofit.create(UserService::class.java)
    val categoriaService: CategoriaService = retrofit.create(CategoriaService::class.java)
    val carteiraService: CarteiraService = retrofit.create(CarteiraService::class.java)
    val metaService: MetaService = retrofit.create(MetaService::class.java)
    val orcamentoService: OrcamentoService = retrofit.create(OrcamentoService::class.java)
    val transacaoService: TransacaoService = retrofit.create(TransacaoService::class.java)
}
