package com.tschuchort.soundshare.soundshare

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.firebase.ui.auth.ResultCodes
import com.firebase.ui.auth.IdpResponse



class MainActivity : AppCompatActivity() {
    private val signInRequestCode = 123
    private var user: FirebaseUser? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        signIn()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == signInRequestCode) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == ResultCodes.OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().currentUser
            }
            else {
                Toast.makeText(this, "failed to sign in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn() {
        val providers = listOf(
                AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build())

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                signInRequestCode)
    }
}
