package me.ibrahimsn.particle

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

data class Particle (
    var radius: Float,
    var x: Float,
    var y: Float,
    var vx: Int,
    var vy: Int,
    var alpha: Int
)

class Emitter {
    private val particles = mutableListOf<Particle>()

    private var dx: Float = 0f
    private var dy: Float = 0f
    private var dist: Float = 0f
    private var distRatio: Float = 0f

    private val path = Path()

    // Paints
    private val paintParticles: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        strokeWidth = 2F
    }

    private val paintLines: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 2F
    }

    private fun linkParticles(canvas: Canvas, p1: Particle, p2: Particle) {
        dx = p1.x - p2.x
        dy = p1.y - p2.y
        dist = sqrt(dx * dx + dy * dy)

        if (dist < 220) {
            path.moveTo(p1.x, p1.y)
            path.lineTo(p2.x, p2.y)
            distRatio = (220 - dist) / 220

            paintLines.alpha = (min(p1.alpha, p2.alpha) * distRatio / 2).toInt()
            canvas.drawPath(path, paintLines)

            path.reset()
        }
    }

    fun setupParticles(width: Int, height: Int, particleMinRadius: Int, particleMaxRadius: Int, particleCount: Int) {
        particles.clear()
        for (i in 0 until particleCount) {
            particles.add(
                Particle(
                    Random.nextInt(particleMinRadius, particleMaxRadius).toFloat(),
                    Random.nextInt(0, width).toFloat(),
                    Random.nextInt(0, height).toFloat(),
                    Random.nextInt(-2, 2),
                    Random.nextInt(-2, 2),
                    Random.nextInt(150, 255)
                )
            )
        }
    }

    fun onDraw(canvas: Canvas, width: Int, height: Int, particleLinesEnabled: Boolean, particleCount: Int) {

        for (i in 0 until particleCount) {
            particles[i].x += particles[i].vx
            particles[i].y += particles[i].vy

            if (particles[i].x < 0) {
                particles[i].x = width.toFloat()
            } else if (particles[i].x > width) {
                particles[i].x = 0F
            }

            if (particles[i].y < 0) {
                particles[i].y = height.toFloat()
            } else if (particles[i].y > height) {
                particles[i].y = 0F
            }

            canvas?.let {
                if (particleLinesEnabled) {
                    for (j in 0 until particleCount) {
                        if (i != j) {
                            linkParticles(it, particles[i], particles[j])
                        }
                    }
                }
            }

            paintParticles.alpha = particles[i].alpha
            canvas?.drawCircle(particles[i].x, particles[i].y, particles[i].radius, paintParticles)
        }
    }

    fun setLineColor(value: Int) {
        paintLines.color = value
    }

    fun setParticleColor(value: Int) {
        paintParticles.color = value
    }

}
