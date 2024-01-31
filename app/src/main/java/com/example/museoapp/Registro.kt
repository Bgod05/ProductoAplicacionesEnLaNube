package com.example.museoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.museoapp.database.Utils.Companion.PUBLISHABLE_KEY
import com.example.museoapp.model.User
import com.example.museoapp.database.remote.RetrofitClient
import com.example.museoapp.model.CustomerModel
import com.example.museoapp.model.PaymentIntentModel
import com.google.firebase.firestore.FirebaseFirestore
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


class Registro : AppCompatActivity() {
    //Nuevos cambios para el registro
    private lateinit var editTextUser: TextInputEditText
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextLastname: TextInputEditText

    private lateinit var rbPremium: RadioGroup

    private lateinit var rbPremiumYes: RadioButton
    private lateinit var rbPremiumNo: RadioButton



    private lateinit var firestore: FirebaseFirestore
    //  -----------------------------------------

    // Stripe
    private lateinit var paymentSheet: PaymentSheet

    private var customerId: String = ""
    private var ephemeralKey: String = ""
    private var clientSecret: String = ""

    //  -----------------------------------------
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        //Nuevos cambios para el registro
        firestore = FirebaseFirestore.getInstance()

        editTextUser = findViewById(R.id.edtUser)
        editTextName = findViewById(R.id.edtName)
        editTextLastname = findViewById(R.id.edtLastname)

        rbPremiumYes = findViewById(R.id.rb_yes)
        rbPremiumNo = findViewById(R.id.rb_no)
        rbPremium = findViewById(R.id.rbg_premium)

        editTextEmail = findViewById(R.id.edtEmail)
        editTextPassword = findViewById(R.id.edtPassword)
        btnRegister = findViewById(R.id.btnRegister)

        //  -----------------------------------------
        // Stripe
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        getCustomerId()
        PaymentConfiguration.init(this, PUBLISHABLE_KEY)
        //  -----------------------------------------

        // si es premium mandar true si no false
        rbPremium.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_yes -> {
                    rbPremiumYes.isChecked = true
                    // si premium es true cambiar el texto de btnRegister a pagar sino dejarlo como registrar
                    btnRegister.text = "Pagar"
                    btnRegister.setOnClickListener {
                        paymentFlow()
                    }
                }

                R.id.rb_no -> {
                    rbPremiumNo.isChecked = true
                    btnRegister.text = "Registrar"
                    btnRegister.setOnClickListener {
                        val email = editTextEmail.text.toString()
                        val password = editTextPassword.text.toString()

                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            registerUser(email, password)
                        } else {
                            // Manejo de errores, por ejemplo, mostrar un mensaje de error
                        }
                    }
                }
            }
        }
        //  -----------------------------------------

        // Configurar el evento de clic para el botón de registro
        /*    btnRegister.setOnClickListener {
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    registerUser(email, password)
                } else {
                    // Manejo de errores, por ejemplo, mostrar un mensaje de error
                }
            }*/
    }

    private fun registerUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    // Nuevos cambios para el registro -> Guardar en firestore
                    if (user != null) {
                        firestore.collection("users").document(user.uid).set(
                            User(
                                id = user.uid,
                                user = editTextUser.text.toString(),
                                name = editTextName.text.toString(),
                                lastname = editTextLastname.text.toString(),
                                email = user.email!!,
                                isPremium = rbPremiumYes.isChecked
                            ).toMap()
                        )
                    }
                    showToast("Haz registrado correctamente")
                    val intent = Intent(this, NavigationActivity::class.java)
                    startActivity(intent)
                } else {
                    // El registro falló
                    // Manejo de errores, por ejemplo, mostrar un mensaje de error
                }
            }
    }
    // Stripe Functions

    private val handler = CoroutineExceptionHandler { _, exception ->
        // Manejar la excepción aquí
        showToast("Error: ${exception.message}")
    }

    private fun paymentFlow() {
        paymentSheet.presentWithPaymentIntent(
            clientSecret,
            PaymentSheet.Configuration(
                "MuseoApp",
                PaymentSheet.CustomerConfiguration(
                    customerId,
                    ephemeralKey
                )
            )
        )
    }

    private fun getCustomerId() {
        lifecycleScope.launch(handler + Dispatchers.IO) {
            val response: Response<CustomerModel> = RetrofitClient.instance.getCustomerId()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    customerId = response.body()!!.id
                    getEphemeralKey(customerId)
                }
            }
        }
    }

    private fun getEphemeralKey(customerId: String) {

        lifecycleScope.launch(handler + Dispatchers.IO) {
            val response: Response<CustomerModel> =
                RetrofitClient.instance.getEphemeralKey(customerId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    ephemeralKey = response.body()!!.id
                    getPaymentIntent(customerId, ephemeralKey)
                }
            }
        }

    }

    private fun getPaymentIntent(customerId: String, ephemeralKey: String) {
        lifecycleScope.launch(handler + Dispatchers.IO) {
            val response: Response<PaymentIntentModel> =
                RetrofitClient.instance.getPaymentIntent(customerId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    clientSecret = response.body()!!.client_secret

                    Toast.makeText(this@Registro, "Process payment", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                showToast("Pago cancelado")
            }

            is PaymentSheetResult.Failed -> {
                showToast("Error en el pago")
            }

            is PaymentSheetResult.Completed -> {
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    registerUser(email, password)
                } else {
                    // Manejo de errores, por ejemplo, mostrar un mensaje de error
                }
                showToast("Pago completado")
            }
        }
    }

    //  -----------------------------------------


    private fun showToast(message: String) {
        Toast.makeText(this@Registro, message, Toast.LENGTH_SHORT).show()
    }
}