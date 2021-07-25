package com.example.fundoos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

const val TAG: String = "EmailPassword"

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText

    private lateinit var mAuth: FirebaseAuth

    lateinit var service: Service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        findViewById<Button>(R.id.buttonRegister).setOnClickListener(this)
        findViewById<Button>(R.id.buttonSignIn).setOnClickListener(this)

        service = Service()

    }

    private fun registerUser() {

        var email = editTextEmail.text.toString().trim()
        var password = editTextPassword.text.toString().trim()

        if (email.isEmpty()) {
            editTextEmail.error = "Please Enter Email Address"
            editTextEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Please Enter A Valid Email Address"
            editTextEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            editTextPassword.error = "Please Enter Password"
            editTextPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            editTextPassword.error = "Minimum length of the Password should be 6"
            editTextPassword.requestFocus()
            return
        }

        service.createAccount(email, password, listener)
    }

    override fun onClick(view: View?) {

        when (view?.id) {
            R.id.buttonSignIn -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

            }

            R.id.buttonRegister -> {
                registerUser()
            }

            else -> null
        }

    }

    private val listener: AuthListener = object : AuthListener {
        override fun onRegister(status: Boolean, exception: String?) {
            if (status) {

                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                Toast.makeText(applicationContext, "Registration Succeed", Toast.LENGTH_LONG)
                    .show()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()

            } else {
                // If sign in fails, display a message to the user.
                Log.d(TAG, "createUserWithEmail:Failed")
                Toast.makeText(applicationContext, exception, Toast.LENGTH_LONG).show()
            }
        }

        override fun onLogin(status: Boolean, exception: String?) {
            TODO("Not yet implemented")
        }

        override fun onResetPassword(status: Boolean) {
            TODO("Not yet implemented")
        }

        override fun onFacebookLogin(status: Boolean, exception: String?) {
            TODO("Not yet implemented")
        }

    }

}
