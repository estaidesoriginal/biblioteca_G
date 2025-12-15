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

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().putString(KEY_USER, userJson).apply()
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(userJson, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
