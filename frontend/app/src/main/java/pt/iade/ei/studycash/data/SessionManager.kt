package pt.iade.ei.studycash.data

import android.content.Context
import android.content.SharedPreferences
import pt.iade.ei.studycash.model.User

object SessionManager {
    private const val PREF_NAME = "StudyCashSession"
    private const val KEY_USER_ID = "userId"
    private const val KEY_USER_NAME = "userName"
    private const val KEY_USER_EMAIL = "userEmail"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(context: Context, user: User) {
        val editor = getPrefs(context).edit()
        editor.putLong(KEY_USER_ID, user.idUser ?: -1)
        editor.putString(KEY_USER_NAME, user.nome)
        editor.putString(KEY_USER_EMAIL, user.email)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    fun getUserId(context: Context): Long {
        return getPrefs(context).getLong(KEY_USER_ID, -1)
    }

    fun getUserName(context: Context): String {
        return getPrefs(context).getString(KEY_USER_NAME, "Utilizador") ?: "Utilizador"
    }

    fun getUserEmail(context: Context): String {
        return getPrefs(context).getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout(context: Context) {
        val editor = getPrefs(context).edit()
        editor.clear()
        editor.apply()
    }
}

