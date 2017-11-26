package com.tschuchort.soundshare.soundshare

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.airbnb.epoxy.SimpleEpoxyController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.ResultCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val signInRequestCode = 123
    private var user: FirebaseUser? = null
    val controller = SimpleEpoxyController()
    val firestore by lazy { FirebaseFirestore.getInstance() }

    var latestSounds: List<Sound> = listOf(
            Sound("test1"),
            Sound("test2"),
            Sound("test3"),
            Sound("test4"),
            Sound("test5"),
            Sound("test6"),
            Sound("test7"),
            Sound("test8"),
            Sound("test9"),
            Sound("test10"),
            Sound("test20"),
            Sound("test30"),
            Sound("test40"),
            Sound("test50"),
            Sound("test60"),
            Sound("test70"),
            Sound("test80")
            )
    var favoriteSounds: List<Sound> = emptyList()
    var mySounds: List<Sound> = emptyList()

    fun setLoading(loading: Boolean) {
        if(loading) {
            progress_bar.visibility = View.VISIBLE
            recycler.visibility = View.INVISIBLE
        }
        else {
            progress_bar.visibility = View.INVISIBLE
            recycler.visibility = View.VISIBLE
        }

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> showLatest()
            R.id.navigation_mine -> showMine()
            R.id.navigation_favorites -> showFavorites()
            else -> return@OnNavigationItemSelectedListener false
        }
        true
    }

    private fun onItemClick(sound: Sound) {

    }

    private fun showFavorites() {
        supportActionBar!!.title = "Favorites"
        controller.setModels(favoriteSounds.map { SoundModel(it, this::onItemClick) })
    }

    private fun showMine() {
        supportActionBar!!.title = "My Sounds"
        controller.setModels(mySounds.map { SoundModel(it, this::onItemClick) })

    }

    private fun showLatest() {
        supportActionBar!!.title = "My Sounds"
        controller.setModels(latestSounds.map { SoundModel(it, this::onItemClick) })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        signIn()
        recycler.adapter = controller.adapter
        recycler.layoutManager = LinearLayoutManager(this)
        setLoading(true)

        floatingActionButton.setOnClickListener {
            val intent = Intent( this, RecordingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == signInRequestCode) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == ResultCodes.OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().currentUser
                onSignedIn()
            }
            else {
                Toast.makeText(this, "failed to sign in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onSignedIn() {
        firestore.collection("Soundfiles")
                .get()
                .addOnSuccessListener { task ->
                    latestSounds = task.documents.map { Sound(it.data["url"].toString()) }
                    showLatest()
                    setLoading(false)
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


