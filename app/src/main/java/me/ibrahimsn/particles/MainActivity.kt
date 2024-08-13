package me.ibrahimsn.particles

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.ibrahimsn.particle.LinkedWebEmitter
import me.ibrahimsn.particle.MeteorShowerEmitter
import me.ibrahimsn.particle.ParticleView
import me.ibrahimsn.particle.RainfallEmitter
import me.ibrahimsn.particle.RainfallWithPetalsEmitter
import me.ibrahimsn.particle.SnowfallEmitter

class MainActivity : AppCompatActivity() {

    private lateinit var particleView: ParticleView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        particleView = findViewById(R.id.particleView)
//        particleView.attach(LinkedWebEmitter())
//        particleView.attach(MeteorShowerEmitter())
//        particleView.attach(SnowfallEmitter())
//        particleView.attach(RainfallWithPetalsEmitter())
        particleView.attach(RainfallEmitter())
    }

    override fun onResume() {
        super.onResume()
        particleView.resume()
    }

    override fun onPause() {
        super.onPause()
        particleView.pause()
    }
}
