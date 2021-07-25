package com.example.fundoos

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class Service {

    private lateinit var mAuth: FirebaseAuth

    fun createAccount(email: String, password: String, listener: AuthListener) {

        mAuth = FirebaseAuth.getInstance()

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                listener.onRegister(true, null)
            } else {
                // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)

                if (task.exception is FirebaseAuthUserCollisionException) {
                    listener.onRegister(false, "Your Email address is Already Registered")
                } else {
                    task.exception?.message?.let {
                        listener.onRegister(false, it)
                    }
                }
            }
        // [END create_user_with_email]
        }
    }

    fun signInAccount(email: String, password: String, listener: AuthListener) {

        mAuth = FirebaseAuth.getInstance()

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCustomToken:success")
                listener.onLogin(true, null)

            } else {
                // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCustomToken:failure")
                listener.onLogin(false, "Login Failed")
            }
        }
    }

    fun resetPassword(email: String,listener: AuthListener) {

        mAuth = FirebaseAuth.getInstance()

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener() { task ->
            if(task.isSuccessful) {
                Log.d(TAG, "SendResetPasswordLinkToMail:Success")
                listener.onResetPassword(true)
            }
        }
    }

    fun signInWithCredential(credential: AuthCredential, listener: AuthListener) {

        mAuth = FirebaseAuth.getInstance()

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    listener.onFacebookLogin(true, null)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    listener.onFacebookLogin(false, "UsingFacebookId:Login Failed")
                }
            }
    }

}
