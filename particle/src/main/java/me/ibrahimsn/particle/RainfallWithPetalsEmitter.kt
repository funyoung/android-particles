package me.ibrahimsn.particle

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

class RainfallWithPetalsEmitter : IEmitter {
    private companion object {
        // 雨滴参数
        private const val RAIN_ALPHA = 200
        private const val RAIN_WIDTH = 3F
        private const val RAIN_HEIGHT_MIN = 15F
        private const val RAIN_HEIGHT_MAX = 30F
        private const val RAIN_VX = 0
        private const val RAIN_VY_MIN = 10
        private const val RAIN_VY_MAX = 20

        // 花瓣参数
        private const val PETAL_ALPHA_MIN = 150
        private const val PETAL_ALPHA_MAX = 255
        private const val PETAL_VX_MIN = -3
        private const val PETAL_VX_MAX = 3
        private const val PETAL_VY_MIN = 3
        private const val PETAL_VY_MAX = 8
        private const val PETAL_ROTATION_MIN = -10
        private const val PETAL_ROTATION_MAX = 10

        private const val DEFAULT_STROKE_WIDTH = 2F
        private val DEFAULT_PARTICLE_STYLE = Paint.Style.FILL
        private val DEFAULT_LINE_STYLE = Paint.Style.FILL_AND_STROKE
    }

    private val rainParticles = mutableListOf<Particle>()
    private val petalParticles = mutableListOf<Particle>()
    private val path = Path()

    private val paintRain = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        strokeWidth = RAIN_WIDTH
    }

    private val paintPetals = Paint().apply {
        isAntiAlias = true
        style = DEFAULT_PARTICLE_STYLE
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
        rainParticles.clear()
        petalParticles.clear()

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

            petalParticles.add(
                Particle(
                    radius = Random.nextInt(particleMinRadius, particleMaxRadius).toFloat(),
                    x = Random.nextInt(0, width).toFloat(),
                    y = Random.nextInt(0, height / 4).toFloat(),
                    vx = Random.nextInt(PETAL_VX_MIN, PETAL_VX_MAX),
                    vy = Random.nextInt(PETAL_VY_MIN, PETAL_VY_MAX),
                    alpha = Random.nextInt(PETAL_ALPHA_MIN, PETAL_ALPHA_MAX)
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
        // 绘制雨滴
        rainParticles.forEach { rainParticle ->
            updateRainParticlePosition(rainParticle, width, height)
            drawRainParticle(canvas, rainParticle)
        }

        // 绘制花瓣
        petalParticles.forEach { petalParticle ->
            updatePetalParticlePosition(petalParticle, width, height)
            drawPetalParticle(canvas, petalParticle)
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

    // 更新花瓣位置
    private fun updatePetalParticlePosition(particle: Particle, width: Int, height: Int) {
        particle.x += particle.vx
        particle.y += particle.vy

        // 如果花瓣移出屏幕，则重置到屏幕顶部的随机位置
        if (particle.y > height || particle.x < 0 || particle.x > width) {
            particle.x = Random.nextInt(0, width).toFloat()
            particle.y = 0F
            particle.vx = Random.nextInt(PETAL_VX_MIN, PETAL_VX_MAX)
            particle.vy = Random.nextInt(PETAL_VY_MIN, PETAL_VY_MAX)
        }
    }

    // 绘制雨滴
    private fun drawRainParticle(canvas: Canvas, particle: Particle) {
        paintRain.alpha = particle.alpha
        canvas.drawRect(particle.x, particle.y, particle.x + RAIN_WIDTH, particle.y + particle.radius, paintRain)
    }

    // 绘制花瓣
    private fun drawPetalParticle(canvas: Canvas, particle: Particle) {
        paintPetals.alpha = particle.alpha
        canvas.save()
        canvas.rotate(
            Random.nextInt(PETAL_ROTATION_MIN, PETAL_ROTATION_MAX).toFloat(),
            particle.x + particle.radius / 2,
            particle.y + particle.radius / 2
        )
        canvas.drawOval(
            particle.x,
            particle.y,
            particle.x + particle.radius,
            particle.y + particle.radius * 2,
            paintPetals
        )
        canvas.restore()
    }

    // 设置雨滴颜色
    override fun setLineColor(value: Int) {
        paintRain.color = value
    }

    // 设置花瓣颜色
    override fun setParticleColor(value: Int) {
        paintPetals.color = value
    }
}
