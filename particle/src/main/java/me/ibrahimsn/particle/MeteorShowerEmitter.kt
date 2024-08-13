package me.ibrahimsn.particle

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

class MeteorShowerEmitter : IEmitter {
    private companion object {
        private const val ALPHA_MIN = 150
        private const val ALPHA_MAX = 255
        private const val VX_MIN = -10
        private const val VX_MAX = 10
        private const val VY_MIN = 5
        private const val VY_MAX = 15
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
                    x = Random.nextInt(0, width).toFloat(), // 屏幕顶部的随机位置
                    y = Random.nextInt(0, height / 4).toFloat(), // 屏幕上方四分之一区域内的随机位置
                    vx = Random.nextInt(VX_MIN, VX_MAX),
                    vy = Random.nextInt(VY_MIN, VY_MAX),
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

        // 移动出屏幕的粒子重置到屏幕顶部
        if (particle.y > height || particle.x < 0 || particle.x > width) {
            particle.x = Random.nextInt(0, width).toFloat()
            particle.y = 0F
            particle.vx = Random.nextInt(VX_MIN, VX_MAX)
            particle.vy = Random.nextInt(VY_MIN, VY_MAX)
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
