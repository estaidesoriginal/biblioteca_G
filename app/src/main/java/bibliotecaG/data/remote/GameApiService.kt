package bibliotecaG.data.remote

import bibliotecaG.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface GameApiService {
    // ... (Mantén tus endpoints anteriores de Juegos, Usuarios, Productos) ...
    // Solo estoy agregando/modificando la sección de compras abajo:

    // --- SECCIÓN: BIBLIOTECA (Juegos) ---
    @GET("juegos")
    suspend fun getAllGames(): List<Game>

    @GET("juegos/{id}")
    suspend fun getGameById(@Path("id") id: String): Game

    @POST("juegos")
    suspend fun createGame(@Body game: Game): Game

    @PUT("juegos/{id}")
    suspend fun updateGame(@Path("id") id: String, @Body game: Game): Game

    @DELETE("juegos/{id}")
    suspend fun deleteGame(@Path("id") id: String)

    // --- SECCIÓN: USUARIOS ---
    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("usuarios/registro")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // --- SECCIÓN: TIENDA ---
    @GET("productos")
    suspend fun getAllProducts(): List<Product>

    @POST("productos")
    suspend fun createProduct(@Body product: Product): Product

    @PUT("productos/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body product: Product): Product

    @DELETE("productos/{id}")
    suspend fun deleteProduct(@Path("id") id: String)

    // --- SECCIÓN: COMPRAS (ADMIN) ---

    // Crear compra (Cliente)
    @POST("compras")
    suspend fun confirmPurchase(
        @Body items: List<CartItem>,
        @Query("userId") userId: String
    ): Response<Order> // Ahora devuelve Order mapeado

    // Ver todas las compras (Admin)
    @GET("compras")
    suspend fun getAllOrders(): List<Order>

    // Cambiar estado (Admin)
    @PUT("compras/{id}/estado")
    suspend fun updateOrderStatus(
        @Path("id") id: String,
        @Body body: Map<String, String> // Enviamos { "status": "nuevo_estado" }
    ): Response<Order>
}