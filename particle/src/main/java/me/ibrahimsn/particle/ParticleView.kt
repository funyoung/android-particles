package me.ibrahimsn.particle

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

class ParticleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = R.attr.ParticleViewStyle
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    private var surfaceViewThread: SurfaceViewThread? = null
    private var hasSurface: Boolean = false

    // Attribute Defaults
    private var _particleCount = 20

    @Dimension
    private var _particleMinRadius = 5

    @Dimension
    private var _particleMaxRadius = 10

    @ColorInt
    private var _particlesBackgroundColor = Color.BLACK

    @ColorInt
    private var _particleColor = Color.WHITE

    @ColorInt
    private var _particleLineColor = Color.WHITE

    private var _particleLinesEnabled = true

    // Core Attributes
    var particleCount: Int
        get() = _particleCount
        set(value) {
            _particleCount = when {
                value > 50 -> 50
                value < 0 -> 0
                else -> value
            }
        }

    var particleMinRadius: Int
        @Dimension get() = _particleMinRadius
        set(@Dimension value) {
            _particleMinRadius = when {
                value <= 0 -> 1
                value >= particleMaxRadius -> 1
                else -> value
            }
        }

    var particleMaxRadius: Int
        @Dimension get() = _particleMaxRadius
        set(@Dimension value) {
            _particleMaxRadius = when {
                value <= particleMinRadius -> particleMinRadius + 1
                else -> value
            }
        }

    var particlesBackgroundColor: Int
        @ColorInt get() = _particlesBackgroundColor
        set(@ColorInt value) {
            _particlesBackgroundColor = value
        }

    var particleColor: Int
        @ColorInt get() = _particleColor
        set(@ColorInt value) {
            _particleColor = value
            emitter?.setParticleColor(value)
        }

    var particleLineColor: Int
        @ColorInt get() = _particleLineColor
        set(@ColorInt value) {
            _particleLineColor = value
            emitter?.setLineColor(value)
        }

    var particleLinesEnabled: Boolean
        get() = _particleLinesEnabled
        set(value) {
            _particleLinesEnabled = value
        }

    init {
        obtainStyledAttributes(attrs, defStyleAttr)
        if (holder != null) holder.addCallback(this)
        hasSurface = false
    }

    private fun obtainStyledAttributes(attrs: AttributeSet, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ParticleView,
            defStyleAttr,
            0
        )

        try {
            particleCount = typedArray.getInt(
                R.styleable.ParticleView_particleCount,
                particleCount
            )

            particleMinRadius = typedArray.getInt(
                R.styleable.ParticleView_particleMinRadius,
                particleMinRadius
            )

            particleMaxRadius = typedArray.getInt(
                R.styleable.ParticleView_particleMaxRadius,
                particleMaxRadius
            )

            particlesBackgroundColor = typedArray.getColor(
                R.styleable.ParticleView_particlesBackgroundColor,
                particlesBackgroundColor
            )

            particleColor = typedArray.getColor(
                R.styleable.ParticleView_particleColor,
                particleColor
            )

            particleLineColor = typedArray.getColor(
                R.styleable.ParticleView_particleLineColor,
                particleLineColor
            )

            particleLinesEnabled = typedArray.getBoolean(
                R.styleable.ParticleView_particleLinesEnabled,
                particleLinesEnabled
            )
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            typedArray.recycle()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        hasSurface = true

        if (surfaceViewThread == null) {
            surfaceViewThread = SurfaceViewThread()
        }

        surfaceViewThread?.start()
    }

    fun resume() {
        if (surfaceViewThread == null) {
            surfaceViewThread = SurfaceViewThread()

            if (hasSurface) {
                surfaceViewThread?.start()
            }
        }
    }

    fun pause() {
        surfaceViewThread?.requestExitAndWait()
        surfaceViewThread = null
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        hasSurface = false
        surfaceViewThread?.requestExitAndWait()
        surfaceViewThread = null
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        // ignored
    }

    private fun setupParticles() {
        if (null == emitter) {
            attach(Emitter())
        }
        emitter?.setupParticles(width, height, particleMinRadius, particleMaxRadius, particleCount)
    }

    private inner class SurfaceViewThread : Thread() {

        private var running = true
        private var canvas: Canvas? = null

        override fun run() {
            setupParticles()

            while (running) {
                try {
                    canvas = holder.lockCanvas()

                    synchronized (holder) {
                        // Clear screen every frame
                        canvas?.let {
                            it.drawColor(particlesBackgroundColor, PorterDuff.Mode.SRC)
                            emitter?.onDraw(it, width, height, particleLinesEnabled, particleCount)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas)
                    }
                }
            }
        }

        fun requestExitAndWait() {
            running = false

            try {
                join()
            } catch (e: InterruptedException) {
                // ignored
            }
        }
    }

    private var emitter: Emitter? = null
    fun attach(emitter: Emitter) {
        this.emitter = emitter
        emitter?.run {
            setParticleColor(_particleColor)
            setLineColor(_particleLineColor)
        }
    }
}