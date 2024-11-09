package com.ssanchez.puntosyrecompensas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {

    private lateinit var Correo: EditText
    private var currentToast: Toast? = null
    private lateinit var Password: EditText
    private lateinit var BtnLogin: Button
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        Correo = findViewById(R.id.correo)
        Password = findViewById(R.id.password)
        BtnLogin = findViewById(R.id.BtnLogin)
        BtnLogin.setOnClickListener {
            val correo = Correo.text.toString()
            val password = Password.text.toString()

            if (correo.isNotEmpty() && password.isNotEmpty()) {
                signInOrSignUp(correo, password)
            } else {
                mostrarMsg("Ingrese todos los campos")
            }
        }
    }

    private fun mostrarMsg(mensaje: String){
        currentToast?.cancel()
        currentToast = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    private fun signInOrSignUp(email: String, password: String) {
        // Primero intentamos iniciar sesión
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // Si el inicio de sesión es exitoso
                onSuccess()
            }
            .addOnFailureListener { exception ->
                if (exception is FirebaseAuthUserCollisionException) {
                    // El usuario ya existe pero la contraseña es incorrecta
                    mostrarMsg("Contraseña incorrecta")
                } else {
                    // Si el usuario no existe, intentamos registrarlo
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            // Si el registro es exitoso
                            saveUserInDatabase(auth.currentUser?.uid, email)
                        }
                        .addOnFailureListener { signUpException ->
                            // Fallo al registrar
                            mostrarMsg("Error al registrarse")
                        }
                }
            }
    }

    private fun onSuccess() {
        val intent = Intent(this, PuntosRecompensas::class.java)
        startActivity(intent)
        finish() // Finaliza la actividad actual para que el usuario no vuelva atrás
    }

    private fun saveUserInDatabase(uid: String?, email: String) {
        if (uid == null) return
        val usuario = Usuario(
            userId = uid,
            nombre = "Juan Mantes", // Aquí podrías agregar lógica para obtener el nombre real del usuario
            correo = email,
            saldoPuntos = 0 // Valor inicial de puntos
        )

        UsuarioManager.setUsuario(usuario) { exito ->
            if (exito) {
                mostrarMsg("Usuario guardado exitosamente")
                onSuccess()
            } else {
                mostrarMsg("Error al guardar en la base de datos:")
            }
        }
    }

    private fun agregarProducto() {
        var database: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("productos")

        // Verificar que los campos no estén vacíos
        // Crear un objeto Producto con los valores proporcionados
        val productoId = database.push().key ?: ""  // Esto genera un ID único automáticamente
        val producto = Producto(
            productoId,
            "-OBBusBMlBQRVPoeHRwH", // Este valor debería ser el comercio asociado (puedes adaptarlo a tu lógica)
            "Limonada",
            "Producto2",
            25,
            "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.laylita.com%2Frecetas%2Flimonada%2F&psig=AOvVaw3MM4IgXi0GpNI4vAZwAnMr&ust=1731177036390000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCQ1JSvzYkDFQAAAAAdAAAAABAE",
            true
        )

        // Agregar el producto a la base de datos
        if (productoId.isNotEmpty()) {
            ProductosManager.setProducto(producto){ exito ->
                if (exito) {
                    mostrarMsg("Producto agregado correctamente")
                } else {
                    mostrarMsg("Error al agregar el producto")
                }
            }
        }else
    {
        mostrarMsg("Por favor, complete todos los campos")
    }
}

}
