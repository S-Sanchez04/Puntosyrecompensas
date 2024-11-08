package com.ssanchez.puntosyrecompensas

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PuntosRecompensas : AppCompatActivity() {

    private lateinit var recyclerViewProductos: RecyclerView
    private lateinit var verRecompensas: TextView
    private lateinit var adapter: ProductoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar el RecyclerView
        recyclerViewProductos = findViewById(R.id.recyclerViewProductos)
        recyclerViewProductos.layoutManager = GridLayoutManager(this, 2)

        // Configurar el botón "Ver recompensas"
        verRecompensas = findViewById(R.id.verRecompensas)
        verRecompensas.setOnClickListener {
            // Mostrar el RecyclerView y cargar los productos
            recyclerViewProductos.visibility = View.VISIBLE
            cargarProductos()
        }

        // Inicializar el adaptador con una lista vacía
        adapter = ProductoAdapter(emptyList())
        recyclerViewProductos.adapter = adapter
    }

    private fun cargarProductos() {
        // Crear lista de productos de ejemplo con imágenes desde drawable
        val productos = listOf(
            Producto("Graná", 100, "https://mojo.generalmills.com/api/public/content/geavWbc7i0eptb2tGb-CyA_gmi_hi_res_jpeg.jpeg?v=5f30c0a4&t=16e3ce250f244648bef28c5949fb99ff"),
            Producto("La carreta", 150, "https://cdn0.celebritax.com/sites/default/files/styles/watermark_100/public/recetas/limonada.jpg"),
            Producto("La Komilona", 400, "https://www.semana.com/resizer/v2/GBBYJH5YMZC6PEINHE3HZZH4TY.jpg?auth=f21d7fbf15c15316b80dd213fb2c635e4445db8e08133172d69a4956d7f417db&smart=true&quality=75&width=1280&fitfill=false"),
            Producto("Donde Juancho", 250, "https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/A_small_cup_of_coffee.JPG/377px-A_small_cup_of_coffee.JPG"),
            Producto("El Fogón", 300, "https://www.cundinamarca.gov.co/wcm/connect/cc2b1871-5c81-434d-b204-1126e322921c/6.jpg?MOD=AJPERES&CACHEID=ROOTWORKSPACE-cc2b1871-5c81-434d-b204-1126e322921c-mbv4mho")
        )

        // Actualizar el adaptador con la nueva lista de productos
        adapter = ProductoAdapter(productos)
        recyclerViewProductos.adapter = adapter
    }
}
