package com.ssanchez.puntosyrecompensas

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class Producto(
    val productoId: String,
    val comercioId: String,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val imagenUrl: String,
    val estado: Boolean
){
    constructor(): this("", "", "" ,"",0,"", false)
}

object ProductosManager {
    private val productosRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("productos")

    fun obtenerProducto(id: String, callback: (Producto?) -> Unit) {
        val productoRef = productosRef.child(id)
        productoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val producto = snapshot.getValue(Producto::class.java)
                callback(producto)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun setProducto(producto: Producto, callback: (Boolean) -> Unit) {
        productosRef.child(producto.productoId).setValue(producto).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    fun buscarProductosPorCampo(campo: String, valor: Any, callback: (List<Producto>) -> Unit) {
        val query = when (valor) {
            is Boolean -> productosRef.orderByChild(campo).equalTo(valor)
            is Double -> productosRef.orderByChild(campo).equalTo(valor)
            is String -> productosRef.orderByChild(campo).equalTo(valor)
            else -> {
                callback(emptyList())
                return
            }
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productos = mutableListOf<Producto>()
                for (productoSnapshot in snapshot.children) {
                    val producto = productoSnapshot.getValue(Producto::class.java)
                    producto?.let { productos.add(it) }
                }
                callback(productos)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }
}
