package me.xianglun.blinkblinkbeachadmin.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import me.xianglun.blinkblinkbeachadmin.R
import me.xianglun.blinkblinkbeachadmin.databinding.ActivityMainBinding
import me.xianglun.blinkblinkbeachadmin.ui.createEvent.CreateEventFragment
import me.xianglun.blinkblinkbeachadmin.ui.editEvent.EditEventFragment
import me.xianglun.blinkblinkbeachadmin.ui.eventDetail.EventDetailFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        appBarConfiguration = AppBarConfiguration(
            // fragments placed inside this set will be consider as top-level destination
            setOf(R.id.eventFragment,
                R.id.userFragment,
                R.id.reportFragment,
                R.id.eventDetailFragment,
                R.id.createEventFragment
            )
        )

        binding.apply {
            setContentView(root)
            setSupportActionBar(topAppBar)
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.apply {
            supportFragmentManager.registerFragmentLifecycleCallbacks(object :
                FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewCreated(
                    fm: FragmentManager,
                    f: Fragment,
                    v: View,
                    savedInstanceState: Bundle?,
                ) {
                    when (f) {
                        is EventDetailFragment, is EditEventFragment, is CreateEventFragment -> {
                            topAppBar.isVisible = false
                            bottomNavigationMenu.isVisible = false
                        }
                        else -> {
                            topAppBar.isVisible = true
                            bottomNavigationMenu.isVisible = true
                        }
                    }
                }
            }, true)
        }

        // apply navigation on the action bar
        setupActionBarWithNavController(navController, appBarConfiguration)

        // set up bottom nav bar
        NavigationUI.setupWithNavController(binding.bottomNavigationMenu, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        // make the back button on action bar functional
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}