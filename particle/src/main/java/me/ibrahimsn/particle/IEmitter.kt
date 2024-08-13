package me.ibrahimsn.particle

import android.graphics.Canvas

interface IEmitter {
    fun setParticleColor(value: Int)
    fun setLineColor(value: Int)
    fun onDraw(
        it: Canvas,
        width: Int,
        height: Int,
        particleLinesEnabled: Boolean,
        particleCount: Int
    )

    fun setupParticles(
        width: Int,
        height: Int,
        particleMinRadius: Int,
        particleMaxRadius: Int,
        particleCount: Int
    )
}