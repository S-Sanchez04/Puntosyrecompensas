package com.ssanchez.puntosyrecompensas


import android.content.Context
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object CompraManager {

    fun setCompra(context: Context, producto: Producto, userId: String, onCompraCompletada: () -> Unit, onCompraFallida: () -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val mauth: FirebaseAuth = FirebaseAuth.getInstance()

        UsuarioManager.getPuntos(mauth){ saldoPuntos ->
            if (saldoPuntos >= producto.precio) {
                val nuevoSaldoPuntos = saldoPuntos - producto.precio
                UsuarioManager.setPuntos(mauth, nuevoSaldoPuntos){ exito ->
                    if(exito){
                        val recompensaRef = database.getReference("redenciones").push()
                        val recompensa = Recompensa(
                            rendencionId = recompensaRef.key ?: "",
                            userId = userId,
                            productoId = producto.productoId,
                            nombreProducto = producto.nombre,
                            puntosUsados = producto.precio,
                            fechaRedencion = Timestamp.now()
                        )
                        recompensaRef.setValue(recompensa).addOnCompleteListener {
                            onCompraCompletada()
                        }
                    }
                }

            } else {
                onCompraFallida()
            }
        }
    }
}
data class Recompensa(
    val rendencionId: String,
    val userId: String,
    val productoId: String,
    val nombreProducto: String,
    val puntosUsados: Int,
    val fechaRedencion: Timestamp?
){
    constructor(): this("", "", "" ,"",0,Timestamp.now(), )
}
