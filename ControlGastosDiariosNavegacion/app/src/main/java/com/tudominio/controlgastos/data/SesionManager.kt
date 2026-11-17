package com.tudominio.controlgastos.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extensión de Context para crear el DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sesion")

class SesionManager(private val context: Context) {

    // Claves para guardar datos en DataStore
    companion object {
        private val KEY_ESTA_AUTENTICADO = booleanPreferencesKey("esta_autenticado")
        private val KEY_USUARIO_ID = stringPreferencesKey("usuario_id")
        private val KEY_USUARIO_NOMBRE = stringPreferencesKey("usuario_nombre")
        private val KEY_TOKEN = stringPreferencesKey("token")
    }

    // Flow que indica si el usuario está autenticado
    val estaAutenticado: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_ESTA_AUTENTICADO] ?: false
        }

    // Flow con el ID del usuario (nullable)
    val usuarioId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_USUARIO_ID]
        }

    // Flow con el nombre del usuario (nullable)
    val usuarioNombre: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_USUARIO_NOMBRE]
        }

    // Guardar sesión después de login exitoso
    suspend fun guardarSesion(usuarioId: String, usuarioNombre: String, token: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ESTA_AUTENTICADO] = true
            preferences[KEY_USUARIO_ID] = usuarioId
            preferences[KEY_USUARIO_NOMBRE] = usuarioNombre
            preferences[KEY_TOKEN] = token
        }
    }

    // Cerrar sesión (borrar todos los datos)
    suspend fun cerrarSesion() {
        context.dataStore.edit { preferences ->
            preferences.clear() // Borra todas las claves
        }
    }

    // Obtener token de forma síncrona (para usar en llamadas a API)
    suspend fun obtenerToken(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[KEY_TOKEN] }
            .first()
    }
}
