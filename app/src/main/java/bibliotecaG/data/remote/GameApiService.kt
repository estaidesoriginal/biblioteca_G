package bibliotecaG.data.remote

import bibliotecaG.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface GameApiService {

    // ... (Endpoints de JUEGOS, PRODUCTOS y COMPRAS se mantienen igual) ...
    // Solo mostramos los cambios en la sección de USUARIOS y TIENDA (Delete)

    // --- SECCIÓN: BIBLIOTECA ---
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

    // --- SECCIÓN: USUARIOS (Actualizado) ---
    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("usuarios/registro")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // ¡NUEVOS ENDPOINTS PARA ADMIN!
    @GET("usuarios")
    suspend fun getAllUsers(): List<User>

    @DELETE("usuarios/{id}")
    suspend fun deleteUser(@Path("id") id: String)

    @PUT("usuarios/{id}/rol")
    suspend fun updateUserRole(
        @Path("id") id: String,
        @Body body: Map<String, String> // { "role": "MANAGER" }
    ): User

    // --- SECCIÓN: TIENDA ---
    @GET("productos")
    suspend fun getAllProducts(): List<Product>
    @POST("productos")
    suspend fun createProduct(@Body product: Product): Product
    @PUT("productos/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body product: Product): Product
    @DELETE("productos/{id}")
    suspend fun deleteProduct(@Path("id") id: String)

    // --- SECCIÓN: COMPRAS ---
    @POST("compras")
    suspend fun confirmPurchase(@Body items: List<CartItem>, @Query("userId") userId: String): Response<Order>
    @GET("compras")
    suspend fun getAllOrders(): List<Order>
    @PUT("compras/{id}/estado")
    suspend fun updateOrderStatus(@Path("id") id: String, @Body body: Map<String, String>): Response<Order>
}