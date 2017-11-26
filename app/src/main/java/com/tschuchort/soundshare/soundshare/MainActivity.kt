package com.tschuchort.soundshare.soundshare

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.epoxy.SimpleEpoxyController
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.firebase.ui.auth.ResultCodes
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.fragment_latest.view.*


class MainActivity : AppCompatActivity() {
    private val signInRequestCode = 123
    private var user: FirebaseUser? = null
    private val feedFragment by lazy { FeedFragment() }
    private val mineFragment by lazy { MineFragment() }
    private val favoritesFragment by lazy { FavoritesFragment() }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val selectedFragment: BaseFragment = when (item.itemId) {
            R.id.navigation_home -> feedFragment
            R.id.navigation_dashboard -> mineFragment
            R.id.navigation_notifications -> favoritesFragment
            else -> return@OnNavigationItemSelectedListener false
        }

        setFragment(selectedFragment)
        true
    }

    private fun setFragment(frag: BaseFragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.feed_frame, frag)

        transaction.runOnCommit { supportActionBar!!.title = frag.title }

        transaction.commit()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        signIn()
        setFragment(feedFragment)
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

    abstract class BaseFragment(val layoutId: Int, val title: String) : Fragment() {
        val controller = SimpleEpoxyController()

        override fun onSaveInstanceState(outState: Bundle?) {
            super.onSaveInstanceState(outState)
            controller.onSaveInstanceState(outState)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            if(savedInstanceState != null) {
                controller.onRestoreInstanceState(savedInstanceState)
            }
        }

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater!!.inflate(layoutId, container, false)

            view.recycler.adapter = controller.adapter
            view.recycler.layoutManager = LinearLayoutManager(context)
            return view
        }
    }

    class FavoritesFragment : BaseFragment(R.layout.fragment_favorites, "Favorites") {

    }

    class FeedFragment : BaseFragment(R.layout.fragment_latest, "Latest") {

    }

    class MineFragment : BaseFragment(R.layout.fragment_mine, "My Sounds") {

    }

}


