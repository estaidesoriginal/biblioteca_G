package bibliotecaG.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    val name: String,
    val email: String,
    @SerializedName("role") val role: String = "USER",
    val token: String? = null
)