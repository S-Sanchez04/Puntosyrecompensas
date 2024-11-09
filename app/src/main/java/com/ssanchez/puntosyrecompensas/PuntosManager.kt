package com.ssanchez.puntosyrecompensas

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



object PuntosManager {
    private lateinit var usuarioRef: DatabaseReference

    fun getPuntos(mauth: FirebaseAuth, callback: (Int) -> Unit) {
        usuarioRef = FirebaseDatabase.getInstance().reference.child("usuarios").child(mauth.currentUser!!.uid).child("saldoPuntos")

        usuarioRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
        usuarioRef = FirebaseDatabase.getInstance().reference.child("usuarios").child(mauth.currentUser!!.uid).child("saldoPuntos")

        usuarioRef.setValue(puntos).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true) // Éxito
            } else {
                callback(false) // Error
            }
        }
    }

}
