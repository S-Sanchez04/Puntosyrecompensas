package com.ssanchez.puntosyrecompensas

import android.app.Dialog
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.snapshot.StringNode

class ProductoAdapter(private val productos: List<Producto>, private val onPuntosActualizados: () -> Unit) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_redimible, parent, false)
        return ProductoViewHolder(view, onPuntosActualizados)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto)
    }

    override fun getItemCount(): Int = productos.size

    class ProductoViewHolder(itemView: View, private val onPuntosActualizados: () -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: Button = itemView.findViewById(R.id.productPrice)
        private val comercioName: TextView = itemView.findViewById(R.id.comercioName)
        private lateinit var mauth: FirebaseAuth
        private var currentToast: Toast? = null

        fun bind(producto: Producto) {
            productName.text = producto.nombre
            productPrice.text = "${producto.precio} "
            mauth = FirebaseAuth.getInstance()
            Glide.with(itemView.context).load(producto.imagenUrl).into(productImage)
            productPrice.setOnClickListener {
                val loadingDialog = showLoadingDialog()
                CompraManager.setCompra(
                    context = itemView.context,
                    producto = producto,
                    userId = mauth.currentUser!!.uid,
                    onCompraCompletada = {
                        loadingDialog.dismiss()
                        mostrarTexto("${producto.nombre} comprado exitosamente!")
                        onPuntosActualizados()
                    },
                    onCompraFallida = {
                        loadingDialog.dismiss()
                        mostrarTexto("Puntos insuficientes!")
                        onPuntosActualizados()
                    }
                )
            }


            val database = FirebaseDatabase.getInstance()
            val comercioRef = database.getReference("comercios").child(producto.comercioId)
            comercioRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val comercio = dataSnapshot.getValue(Comercio::class.java)
                    comercioName.text =
                        comercio!!.nombre // Actualiza el nombre del comercio en la vista
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Manejar el error, si es necesario
                }
            })
        }


        private fun mostrarTexto(mensaje: String) {
            currentToast?.cancel()
            currentToast = Toast.makeText(itemView.context, mensaje, Toast.LENGTH_SHORT)
            currentToast?.show()
        }

        private fun showLoadingDialog(): Dialog {
            val dialog = Dialog(itemView.context)
            dialog.setContentView(R.layout.dialog_loading)
            dialog.setCancelable(false) // No se puede cerrar tocando fuera del diálogo
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
            return dialog
        }

    }

}
data class Comercio(
    val comercioId: String,
    val nombre: String,
    val propietarioId: String,
    val ubicacion: String,
    val correo: String
){
    constructor(): this("", "", "" ,"", "" )
}


