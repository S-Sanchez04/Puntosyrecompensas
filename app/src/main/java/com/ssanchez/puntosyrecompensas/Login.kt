package com.ssanchez.puntosyrecompensas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {
    private lateinit var Correo: EditText
    private lateinit var Password: EditText
    private lateinit var BtnLogin: Button
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Correo = findViewById(R.id.correo)
        Password = findViewById(R.id.password)
        BtnLogin = findViewById(R.id.BtnLogin)

        BtnLogin.setOnClickListener{
            var correo = Correo.text.toString()
            var password = Password.text.toString()
            if(correo.isNotEmpty() && password.isNotEmpty()){
                signInOrSignUp(correo, password)
            }else{
                Toast.makeText(this,"Ingrese todos los campos", Toast.LENGTH_SHORT).show()
            }
        }


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
                    Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                } else {
                    // Si el usuario no existe, intentamos registrarlo
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            // Si el registro es exitoso
                            saveUserInDatabase(auth.currentUser?.uid, email)
                            onSuccess()
                        }
                        .addOnFailureListener { signUpException ->
                            // Fallo al registrar
                            Toast.makeText(this, "Error al registrarse", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    private fun onSuccess(){
        val intent = Intent(this, PuntosRecompensas::class.java)
        this.startActivity(intent)
        this.finish() // Finaliza la actividad actual para que el usuario no vuelva atrás
    }

    private fun saveUserInDatabase(uid: String?, email: String) {
        if (uid == null) return

        val database = FirebaseDatabase.getInstance().reference
        val user = mapOf(
            "email" to email,
            "points" to 0 // Agrega cualquier otro dato inicial que necesites
        )

        // Guarda el usuario en la referencia de "Users" con su UID
        database.child("Users").child(uid).setValue(user)
            .addOnSuccessListener {
                // Usuario guardado exitosamente en la base de datos
            }
            .addOnFailureListener { exception ->
                // Maneja el error
                Toast.makeText(this, "Error al guardar en la base de datos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}