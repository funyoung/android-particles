package me.ibrahimsn.particle

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

class LinkedWebEmitter : IEmitter {
    private companion object {
        private const val ALPHA_MIN = 150
        private const val ALPHA_MAX = 255
        private const val X_STEP_MIN = -2
        private const val X_STEP_MAX = 2
        private const val Y_STEP_MIN = -2
        private const val Y_STEP_MAX = 2
        private const val LINE_DIST_MAX = 220
        private const val DEFAULT_STROKE_WIDTH = 2F
        private val DEFAULT_PARTICLE_STYLE = Paint.Style.FILL
        private val DEFAULT_LINE_STYLE = Paint.Style.FILL_AND_STROKE
    }
    
    private val particles = mutableListOf<Particle>()
    private val path = Path()

    private val paintParticles = Paint().apply {
        isAntiAlias = true
        style = DEFAULT_PARTICLE_STYLE
        strokeWidth = DEFAULT_STROKE_WIDTH
    }

    private val paintLines = Paint().apply {
        isAntiAlias = true
        style = DEFAULT_LINE_STYLE
        strokeWidth = DEFAULT_STROKE_WIDTH
    }

    // 设置初始粒子
    override fun setupParticles(
        width: Int,
        height: Int,
        particleMinRadius: Int,
        particleMaxRadius: Int,
        particleCount: Int
    ) {
        particles.clear()
        repeat(particleCount) {
            particles.add(
                Particle(
                    radius = Random.nextInt(particleMinRadius, particleMaxRadius).toFloat(),
                    x = Random.nextInt(0, width).toFloat(),
                    y = Random.nextInt(0, height).toFloat(),
                    vx = Random.nextInt(X_STEP_MIN, X_STEP_MAX),
                    vy = Random.nextInt(Y_STEP_MIN, Y_STEP_MAX),
                    alpha = Random.nextInt(ALPHA_MIN, ALPHA_MAX)
                )
            )
        }
    }

    // 在canvas上绘制
    override fun onDraw(
        canvas: Canvas,
        width: Int,
        height: Int,
        particleLinesEnabled: Boolean,
        particleCount: Int
    ) {
        particles.forEachIndexed { i, particle ->
            updateParticlePosition(particle, width, height)
            if (particleLinesEnabled) {
                drawLinks(canvas, particle, i, particleCount)
            }
            drawParticle(canvas, particle)
        }
    }

    // 更新粒子位置
    private fun updateParticlePosition(particle: Particle, width: Int, height: Int) {
        particle.x += particle.vx
        particle.y += particle.vy

        // 边界检测并回绕
        particle.x = when {
            particle.x < 0 -> width.toFloat()
            particle.x > width -> 0F
            else -> particle.x
        }

        particle.y = when {
            particle.y < 0 -> height.toFloat()
            particle.y > height -> 0F
            else -> particle.y
        }
    }

    // 绘制粒子
    private fun drawParticle(canvas: Canvas, particle: Particle) {
        paintParticles.alpha = particle.alpha
        canvas.drawCircle(particle.x, particle.y, particle.radius, paintParticles)
    }

    // 绘制粒子之间的连线
    private fun drawLinks(canvas: Canvas, particle: Particle, i: Int, particleCount: Int) {
        for (j in i + 1 until particleCount) {
            linkParticles(canvas, particle, particles[j])
        }
    }

    // 连接两个粒子
    private fun linkParticles(canvas: Canvas, p1: Particle, p2: Particle) {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        val dist = sqrt(dx * dx + dy * dy)

        if (dist < LINE_DIST_MAX) {
            path.moveTo(p1.x, p1.y)
            path.lineTo(p2.x, p2.y)

            val distRatio = (LINE_DIST_MAX - dist) / LINE_DIST_MAX
            paintLines.alpha = (min(p1.alpha, p2.alpha) * distRatio / 2).toInt()
            canvas.drawPath(path, paintLines)

            path.reset()
        }
    }

    // 设置连线颜色
    override fun setLineColor(value: Int) {
        paintLines.color = value
    }

    // 设置粒子颜色
    override fun setParticleColor(value: Int) {
        paintParticles.color = value
    }
}
