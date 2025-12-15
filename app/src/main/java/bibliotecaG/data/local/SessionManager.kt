package bibliotecaG.data.local

import android.content.Context
import android.content.SharedPreferences
import bibliotecaG.data.model.User
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_USER = "current_user"
        private const val KEY_TOKEN = "auth_token"
    }

    /**
     * Guarda el usuario completo en memoria.
     */
    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().putString(KEY_USER, userJson).apply()
    }

    /**
     * Guarda el token (útil para futuras peticiones autenticadas).
     */
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    /**
     * Recupera el usuario guardado si existe.
     */
    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(userJson, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Borra los datos al cerrar sesión.
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}