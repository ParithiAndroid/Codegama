package com.parithidb.cgnews.ui.home

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.parithidb.cgnews.R
import com.parithidb.cgnews.databinding.ActivityMainBinding
import com.parithidb.cgnews.util.SharedPrefHelper
import com.parithidb.cgnews.util.ThemeSwitcher
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sharedPref: SharedPrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeSwitcher.applySavedTheme(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = SharedPrefHelper(this)

        setSupportActionBar(binding.ablHome.findViewById(R.id.mtbHome))

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcvHome) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.searchFragment, R.id.accountFragment)
        )

        binding.bnvHome.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }



    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        updateThemeIcon(menu)
        updateProfilePic(menu)
        return true
    }

    private fun updateProfilePic(menu: Menu) {
        val profilePic = sharedPref.getProfilePic()
        val item = menu.findItem(R.id.profilePic)

        if (!profilePic.isNullOrEmpty()) {
            Glide.with(this)
                .load(Uri.parse(profilePic))
                .circleCrop()
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        item.icon = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        updateThemeIcon(menu)
        return super.onPrepareOptionsMenu(menu)
    }

    private fun updateThemeIcon(menu: Menu) {
        menu.findItem(R.id.action_theme_switch)?.icon =
            ContextCompat.getDrawable(this, ThemeSwitcher.getThemeIconRes(this))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_theme_switch -> {
                ThemeSwitcher.toggleTheme(this, binding.main)
                invalidateOptionsMenu() // refresh icon immediately
                true
            }
            R.id.profilePic -> {
                if (navController.currentDestination?.id != R.id.accountFragment) {
                    navController.navigate(R.id.accountFragment)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            R.id.searchFragment, R.id.accountFragment -> {
                navController.navigate(R.id.homeFragment)
            }
            R.id.homeFragment -> {
                // Close app
                finish()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

}