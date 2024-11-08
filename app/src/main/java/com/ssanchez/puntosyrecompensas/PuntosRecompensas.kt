package com.ssanchez.puntosyrecompensas

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.ssanchez.puntosyrecompensas.Producto
import com.ssanchez.puntosyrecompensas.ProductoAdapter

class PuntosRecompensas : AppCompatActivity() {

    private lateinit var recyclerViewProductos: RecyclerView
    private lateinit var adapter: ProductoAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // Configurar el RecyclerView
        recyclerViewProductos = findViewById(R.id.recyclerViewProductos)
        recyclerViewProductos.layoutManager = GridLayoutManager(this, 2)

        // Configurar el botón "Ver recompensas"
        // Mostrar el RecyclerView y cargar los productos

        // Inicializar el adaptador con una lista vacía
        adapter = ProductoAdapter(emptyList())
        recyclerViewProductos.adapter = adapter

        // Inicializar la referencia de Firebase
        database = FirebaseDatabase.getInstance().reference.child("productos")

        recyclerViewProductos.visibility = View.VISIBLE
        cargarProductos()

    }

    private fun cargarProductos() {
        // Obtener los productos desde la base de datos y filtrar por estado
        database.orderByChild("estado").equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productos = mutableListOf<Producto>()

                // Iterar sobre los productos en la base de datos y agregarlos a la lista
                for (productoSnapshot in snapshot.children) {
                    val producto = productoSnapshot.getValue(Producto::class.java)
                    producto?.let { productos.add(it) }
                }

                // Actualizar el adaptador con la nueva lista de productos
                adapter = ProductoAdapter(productos)
                recyclerViewProductos.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores
            }
        })
    }


}
