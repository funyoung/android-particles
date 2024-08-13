package me.ibrahimsn.particle

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.random.Random

class RainfallEmitter : IEmitter {
    private companion object {
        private const val RAIN_ALPHA = 200
        private const val RAIN_WIDTH = 3F
        private const val RAIN_HEIGHT_MIN = 15F
        private const val RAIN_HEIGHT_MAX = 30F
        private const val RAIN_VX = 0
        private const val RAIN_VY_MIN = 10
        private const val RAIN_VY_MAX = 20
    }

    private val rainParticles = mutableListOf<Particle>()

    private val paintRain = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        strokeWidth = RAIN_WIDTH
    }

    // 设置初始粒子
    override fun setupParticles(
        width: Int,
        height: Int,
        particleMinRadius: Int,
        particleMaxRadius: Int,
        particleCount: Int
    ) {
        rainParticles.clear()

        repeat(particleCount) {
            rainParticles.add(
                Particle(
                    radius = Random.nextFloat() * (RAIN_HEIGHT_MAX - RAIN_HEIGHT_MIN) + RAIN_HEIGHT_MIN,
                    x = Random.nextInt(0, width).toFloat(),
                    y = Random.nextInt(0, height / 4).toFloat(),
                    vx = RAIN_VX,
                    vy = Random.nextInt(RAIN_VY_MIN, RAIN_VY_MAX),
                    alpha = RAIN_ALPHA
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
        rainParticles.forEach { rainParticle ->
            updateRainParticlePosition(rainParticle, width, height)
            drawRainParticle(canvas, rainParticle)
        }
    }

    // 更新雨滴位置
    private fun updateRainParticlePosition(particle: Particle, width: Int, height: Int) {
        particle.y += particle.vy

        // 如果雨滴移出屏幕，则重置到屏幕顶部的随机位置
        if (particle.y > height) {
            particle.x = Random.nextInt(0, width).toFloat()
            particle.y = 0F
        }
    }

    // 绘制雨滴
    private fun drawRainParticle(canvas: Canvas, particle: Particle) {
        paintRain.alpha = particle.alpha
        canvas.drawRect(particle.x, particle.y, particle.x + RAIN_WIDTH, particle.y + particle.radius, paintRain)
    }

    // 设置雨滴颜色
    override fun setLineColor(value: Int) {
        paintRain.color = value
    }

    // 未使用
    override fun setParticleColor(value: Int) {
        // This method is not used in this context
    }
}
