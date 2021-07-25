package com.example.fundoos

import android.app.DownloadManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.json.JSONObject
import java.lang.Exception
import java.util.*


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var editTextEmail: EditText
    private lateinit var  editTextPassword: EditText

    private lateinit var mAuth: FirebaseAuth

    lateinit var authStateListener: FirebaseAuth.AuthStateListener

    lateinit var accessTokenTacker: AccessTokenTracker

    lateinit var service: Service

    private lateinit var callbackManager: CallbackManager

    lateinit var facebookLoginButton: LoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.buttonLogin).setOnClickListener(this)
        findViewById<Button>(R.id.buttonSignUp).setOnClickListener(this)
        findViewById<Button>(R.id.forgotPasswordButton).setOnClickListener(this)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        service = Service()

        FacebookSdk.sdkInitialize(applicationContext)

        facebookLoginButton = findViewById(R.id.facebook_login_button)

        facebookLoginButton.setOnClickListener(this)



        facebookLoginButton.setReadPermissions(listOf("email", "picture"))

        callbackManager = CallbackManager.Factory.create()

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser != null){
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        } else {
            Toast.makeText(applicationContext, "Login Failed", Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(view: View?) {

        when(view?.id) {
            R.id.buttonSignUp -> {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            R.id.buttonLogin -> userLogin()

            R.id.forgotPasswordButton -> {
                onForgotPasswordButton()
            }

            R.id.facebook_login_button -> {
                onFacebookLoginButton()
            }

            else -> null
        }
    }

    private fun userLogin() {

        var email = editTextEmail.text.toString().trim()
        var password = editTextPassword.text.toString().trim()

        if(email.isEmpty()) {
            editTextEmail.error = "Please Enter Email Address"
            editTextEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Please Enter A Valid Email Address"
            editTextEmail.requestFocus()
            return
        }

        if(password.isEmpty()) {
            editTextPassword.error = "Please Enter Password"
            editTextPassword.requestFocus()
            return
        }

        if(password.length < 6) {
            editTextPassword.error = "Minimum length of the Password should be 6"
            editTextPassword.requestFocus()
            return
        }


        // [START sign_in_with_email]
        service.signInAccount(email, password, listener)
        // [END sign_in_with_email]
    }


    private fun onForgotPasswordButton() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Forgot Password")
        val view = layoutInflater.inflate(R.layout.dialog_forgot_password,null)
        val username = view.findViewById<EditText>(R.id.editText_username)
        builder.setView(view)
        builder.setPositiveButton("Reset") { _, _ ->
            forgotPassword(username)
        }
        builder.setNegativeButton("Close") { _, _ -> }
        builder.show()
    }


    private fun forgotPassword(userName : EditText) {
        if(userName.text.toString().isEmpty()) {
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userName.text.toString()).matches()) {
            return
        }

        service.resetPassword(userName.text.toString(), listener)
    }

    private fun onFacebookLoginButton() {
        facebookLoginButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                Log.d("FacebookAuthentication", "onSuccess$result")
                handleFacebookToken(result?.accessToken)
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                intent.putExtra("image", "xyz")
                startActivity(intent)
            }

            override fun onCancel() {
                Log.d("FacebookAuthentication", "Cancelled")
            }

            override fun onError(error: FacebookException?) {
                Log.d("FacebookAuthentication", "Error$error")
            }

        })

        accessTokenTacker = object: AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(
                oldAccessToken: AccessToken?,
                currentAccessToken: AccessToken?
            ) {
                if(currentAccessToken == null) {
                    LoginManager.getInstance().logOut()
                }
            }
        }

    }



    private fun handleFacebookToken(accessToken: AccessToken?) {
        Log.d("FacebookAuthentication", "handleFacebookToken$accessToken")


        val credential = accessToken?.token?.let { FacebookAuthProvider.getCredential(it) }


        if (credential != null) {
            service.signInWithCredential(credential, listener)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)

//        if(requestCode == 2) {
//            val graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { obj, reponse ->
//                try {
//                    if(obj.has("id")) {
//                        Log.d("facebook", obj.getString("Email").toString())
//                        Log.d("facebook", JSONObject(obj.getString("picture")).getJSONObject("data").getString("url").toString())
////                        val intent = Intent(applicationContext, HomeActivity::class.java)
////                        intent.putExtra("Email",  obj.getString("Email").toString())
////                        intent.putExtra("picture", JSONObject(obj.getString("picture")).getJSONObject("data").getString("url")).toString()
////                        startActivity(intent)
//                    }
//
//                } catch (e: Exception) {
//
//                }
//            }
//            val param = Bundle()
//            param.putString("fields", "email,picture.type(large),id")
//            graphRequest.parameters = param
//            graphRequest.executeAsync()
//        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private val listener: AuthListener = object : AuthListener {
        override fun onRegister(status: Boolean, exception: String?) {
            TODO("Not yet implemented")
        }

        override fun onLogin(status: Boolean, exception: String?) {
            if (status) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "SignInWithEmail:success")
                Toast.makeText(applicationContext, "Authentication Succeed", Toast.LENGTH_LONG)
                    .show()

                val user = mAuth.currentUser
                updateUI(user)

            } else {
                // If sign in fails, display a message to the user.
                Log.d(TAG, "SignInWithEmail:Failed")
                Toast.makeText(applicationContext, exception, Toast.LENGTH_LONG).show()
                updateUI(null)
            }
        }

        override fun onResetPassword(status: Boolean) {
            if(status) {
                Toast.makeText(applicationContext, "Reset Password Link Sent To Mail", Toast.LENGTH_LONG).show()
            }
        }

        override fun onFacebookLogin(status: Boolean, exception: String?) {
            if(status) {
                Log.d("FacebookAuthentication", "SignInWithFacebookId:success")
                Toast.makeText(applicationContext, "UsingFacebookAuthentication Succeed", Toast.LENGTH_LONG)
                    .show()

            } else {
                Log.d("FacebookAuthentication", "SignInWithFacebookId:Failed")
                Toast.makeText(applicationContext, exception, Toast.LENGTH_SHORT).show()

            }
        }
    }

}