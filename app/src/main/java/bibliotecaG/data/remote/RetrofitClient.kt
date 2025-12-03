package bibliotecaG.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // --- CONFIGURACIÓN FINAL ---
    // Usamos DIRECTAMENTE la URL pública de Render que confirmaste que funciona.
    // Nota: La barra final '/' es OBLIGATORIA para Retrofit.
    private const val BASE_URL = "https://api-biblioteca-apgn.onrender.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Nivel BODY para ver exactamente qué envía y recibe la app en el Logcat
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        // Timeouts de 60 segundos (Render puede tardar en "despertar" la primera vez)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val apiService: GameApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameApiService::class.java)
    }
}