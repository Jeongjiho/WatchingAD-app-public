package com.watchingad.watchingad.main.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.watchingad.watchingad.R
import com.watchingad.watchingad.main.fragment.GambleFragment
import com.watchingad.watchingad.main.fragment.MainFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.watchingad.watchingad.R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.mainFrameLayout, MainFragment())
            .commit()

        val bottomMenu = findViewById<BottomNavigationView>(R.id.bottomMenu)

        bottomMenu.setOnItemSelectedListener{
            println(it.title)

            when(it.itemId) {
                R.id.btnMain -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, MainFragment())
                        .commit()
                }
                R.id.btnGamble -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, GambleFragment())
                        .commit()
                }
            }

            return@setOnItemSelectedListener true
        }
    }

}
