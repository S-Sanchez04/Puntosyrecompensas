package com.ssanchez.puntosyrecompensas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.snapshot.StringNode

class ProductoAdapter(private val productos: List<Producto>) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_redimible, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto)
    }

    override fun getItemCount(): Int = productos.size

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: Button = itemView.findViewById(R.id.productPrice)
        private val comercioName: TextView = itemView.findViewById(R.id.comercioName)

        fun bind(producto: Producto) {
            productName.text = producto.nombre
            productPrice.text = "${producto.precio} "
            // Cargar la imagen usando Glide
            Glide.with(itemView.context).load(producto.imagenUrl).into(productImage)
            productPrice.setOnClickListener{
                Toast.makeText(itemView.context, "${producto.nombre} seleccionado!", Toast.LENGTH_SHORT).show()
            }

            val database = FirebaseDatabase.getInstance()
            val comercioRef = database.getReference("comercios").child(producto.comercioId)
            comercioRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val comercio = dataSnapshot.getValue(Comercio::class.java)
                    comercioName.text = comercio!!.nombre // Actualiza el nombre del comercio en la vista
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Manejar el error, si es necesario
                }
            })
        }
    }
}


data class Usuario(
    val userId: String,
    val nombre: String,
    val correo: String,
    val saldoPuntos: String
)

data class Comercio(
    val comercioId: String,
    val nombre: String,
    val propietarioId: String,
    val ubicacion: String
){
    constructor(): this("", "", "" ,"" )
}

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


data class Recompensa(
    val rendencionId: String,
    val userId: String,
    val productoId: String,
    val nombreProducto: String,
    val puntosUsados: String,
    val fechaRedencion: Timestamp
)

