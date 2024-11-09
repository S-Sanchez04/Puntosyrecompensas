package com.ssanchez.puntosyrecompensas

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

}

data class Usuario(
    val userId: String,
    val nombre: String,
    val correo: String,
    val saldoPuntos: Int
)