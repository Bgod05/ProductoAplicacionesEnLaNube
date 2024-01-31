package com.example.museoapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import android.widget.TextView
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var signInButton: Button
    private lateinit var signUpTextView: TextView
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(8000)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Referenciar los componentes de la vista XML
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        signInButton = findViewById(R.id.btnLogin)
        signUpTextView = findViewById(R.id.tvSignUp)

        // Configurar el evento de clic para el botón de inicio de sesión
        signInButton.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                // Manejo de errores, por ejemplo, mostrar un mensaje de error
            }
        }

        // Configurar el evento de clic para el enlace de registro (sign up)
        signUpTextView.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showToast("Haz iniciado sesión correctamente")
                    val intent = Intent(this, NavigationActivity::class.java)
                    startActivity(intent)
                    // El inicio de sesión fue exitoso
                    // Aquí puedes redirigir a otra actividad o hacer lo que necesites después del inicio de sesión
                } else {
                    // El inicio de sesión falló
                    // Manejo de errores, por ejemplo, mostrar un mensaje de error
                }
            }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}