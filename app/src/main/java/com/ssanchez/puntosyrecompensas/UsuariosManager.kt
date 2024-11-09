package com.ssanchez.puntosyrecompensas

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object UsuarioManager {
    private val usuariosRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("usuarios")

    fun setUsuario(usuario: Usuario, onComplete: (Boolean) -> Unit) {
        usuariosRef.child(usuario.userId).setValue(usuario)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getUsuario(uid: String, onResult: (Usuario?) -> Unit) {
        usuariosRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue(Usuario::class.java)
                onResult(usuario)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(null) // Devuelve null en caso de error
            }
        })
    }

    fun getPuntos(mauth: FirebaseAuth, callback: (Int) -> Unit) {
        val puntosRef = usuariosRef.child(mauth.currentUser!!.uid).child("saldoPuntos")

        puntosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val saldoPuntos = snapshot.getValue(Int::class.java) ?: 0
                callback(saldoPuntos)  // Llamada al callback con saldoPuntos
            }

            override fun onCancelled(error: DatabaseError) {
                callback(0) // En caso de error, devuelve 0 como valor predeterminado
            }
        })
    }

    fun setPuntos(mauth: FirebaseAuth, puntos: Int, callback: (Boolean) -> Unit) {
        val puntosRef = usuariosRef.child(mauth.currentUser!!.uid).child("saldoPuntos")

        puntosRef.setValue(puntos).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true) // Éxito
            } else {
                callback(false) // Error
            }
        }
    }

}

data class Usuario(
    val userId: String,
    val nombre: String,
    val correo: String,
    val saldoPuntos: Int
)