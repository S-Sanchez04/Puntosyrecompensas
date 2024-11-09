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
import com.google.firebase.auth.FirebaseAuth


class PuntosRecompensas : AppCompatActivity() {

    private lateinit var recyclerViewProductos: RecyclerView
    private lateinit var adapter: ProductoAdapter
    private lateinit var database: DatabaseReference
    private lateinit var puntosUsuario: TextView
    private lateinit var mauth: FirebaseAuth
    private lateinit var btnGanarMasPuntos: Button
    private var currentToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        puntosUsuario = findViewById(R.id.puntosUsuario)


        // Configurar el RecyclerView
        recyclerViewProductos = findViewById(R.id.recyclerViewProductos)
        recyclerViewProductos.layoutManager = GridLayoutManager(this, 2)

        // Configurar el botón "Ver recompensas"
        // Mostrar el RecyclerView y cargar los productos


        // Inicializar la referencia de Firebase
        mauth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("productos")
        btnGanarMasPuntos = findViewById(R.id.btnGanarMasPuntos)


        btnGanarMasPuntos.setOnClickListener {
            PuntosManager.getPuntos(mauth) { saldoPuntos ->
                val nuevoSaldoPuntos = saldoPuntos + 500
                PuntosManager.setPuntos(FirebaseAuth.getInstance(), nuevoSaldoPuntos) { exito ->
                    if (exito){
                        actualizaPuntos()
                    } else {
                        mostrarMsg("Error al actualizar el saldo de puntos.")
                    }
                }
            }


        }

        actualizaPuntos()
        cargarProductos()
        recyclerViewProductos.visibility = View.VISIBLE

    }



    private fun actualizaPuntos(){
        PuntosManager.getPuntos(mauth) { saldoPuntos ->
            puntosUsuario.text = saldoPuntos.toString()
        }
    }

    private fun mostrarMsg(mensaje: String){
        currentToast?.cancel()
        currentToast = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    private fun cargarProductos() {
        ProductosManager.buscarProductosPorCampo("estado", true) { productos ->
            adapter = ProductoAdapter(productos) {
                actualizaPuntos()
            }
            recyclerViewProductos.adapter = adapter
        }
    }


}
