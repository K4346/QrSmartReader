package com.example.qrsmartreader.ui.custom_views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.core.graphics.values

class QrPointsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {
    private val pointPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }


    private val pointSize = 20f // Размер точек в пикселях
    private val pointCount = 4 // Количество точек

//  NOTE:  Координаты точек, важно заметить что это координаты пикселей, для получения правильных значений стоит смотреть с помощью getCurrentPointOfImageCoord
    val points = MutableList(pointCount) { PointF() } // Инициализация списка точек
    private var activePointIndex: Int? = null
    private var density: Float = 0f
    private lateinit var matrix: Matrix

    private lateinit var initPoints: FloatArray

    var firstDrawFlag = true


    fun init(bitmap: Bitmap?, points: FloatArray?) {
        this.setImageBitmap(bitmap)

        isClickable = true
        setOnTouchListener(PointTouchListener())

        // Получение плотности пикселей устройства
        val metrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(metrics)
        density = metrics.density
        // Расположение изначальных точек на экране
        val screenWidth = metrics.widthPixels.toFloat()
        val screenHeight = metrics.heightPixels.toFloat()

        if (points != null) {
            initPoints = points
        } else {
            this.points[0].set(screenWidth / 4, screenHeight / 4)
            this.points[1].set(3 * screenWidth / 4, screenHeight / 4)
            this.points[2].set(screenWidth / 4, screenHeight / 2)
            this.points[3].set(3 * screenWidth / 4, screenHeight / 2)
            firstDrawFlag = false
        }


    }


    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)

        matrix = imageMatrix
        if (firstDrawFlag) {
            val p1 = getCoordByDp(initPoints[0], initPoints[1])
            val p2 = getCoordByDp(initPoints[2], initPoints[3])
            val p3 = getCoordByDp(initPoints[4], initPoints[5])
            val p4 = getCoordByDp(initPoints[6], initPoints[7])
            this.points[0].set(p1.x, p1.y)
            this.points[1].set(p2.x, p2.y)
            this.points[2].set(p3.x, p3.y)
            this.points[3].set(p4.x, p4.y)
        }
        firstDrawFlag = false
        canvas?.let {
            for (point in this.points) {
                val dbPointF = PointF(point.x, point.y)
                canvas.drawRect(
                    dbPointF.x - pointSize,
                    dbPointF.y - pointSize,
                    dbPointF.x + pointSize,
                    dbPointF.y + pointSize,
                    pointPaint
                )
            }
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    private inner class PointTouchListener : OnTouchListener {
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            val action = event.action
            val x = event.x
            val y = event.y

            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    activePointIndex = findClosestPointIndex(x, y)
                }

                MotionEvent.ACTION_MOVE -> {
                    activePointIndex?.let {
                        val point = getCurrentPointOfImageCoord(x, y)
                        if (point.x > 0 && point.x < this@QrPointsView.drawable.intrinsicWidth / density && point.y > 0 && point.y < this@QrPointsView.drawable.intrinsicHeight / density) {
                            points[it].x = x
                            points[it].y = y

                            invalidate()
                        }
                    }
                }

                MotionEvent.ACTION_UP -> {
                    activePointIndex = null
                }
            }

            return true
        }

        private fun findClosestPointIndex(x: Float, y: Float): Int? {
            for (i in points.indices) {
                val point = points[i]
                val dx = x - point.x
                val dy = y - point.y
                val distanceSquared = dx * dx + dy * dy
                if (distanceSquared <= pointSize * pointSize * pointSize / 2) {
                    return i
                }
            }
            return null
        }
    }

    fun getCurrentPointOfImageCoord(x: Float, y: Float): PointF {

        Log.d("QRCodeSelectionView", "Point x = $x px, y = $y px")

        val transformedX = (x - matrix?.values()?.get(2)!! ?: 0f) / (matrix?.values()?.get(0) ?: 1f)
        val transformedY = (y - matrix?.values()?.get(5)!! ?: 0f) / (matrix?.values()?.get(4) ?: 1f)
        val dpX = transformedX / density
        val dpY = transformedY / density
        Log.d("QRCodeSelectionView", "Point x = $dpX dp, y = $dpY dp")
        return PointF(dpX, dpY)
    }

    private fun getCoordByDp(dx: Float, dy: Float): PointF {
        val transformedX =
            (dx * density * (matrix?.values()?.get(0) ?: 1f)) + (matrix?.values()?.get(2)!! ?: 0f)
        val transformedY =
            (dy * density * (matrix?.values()?.get(4) ?: 1f)) + (matrix?.values()?.get(5)!! ?: 0f)
        return PointF(transformedX, transformedY)
    }

    private fun sortedClockwisePoints(): List<PointF> {
        val firstPoint: PointF
        val secondPoint: PointF
        val thirstPoint: PointF
        val fourthPoint: PointF
        val sortByX = points.map { getCurrentPointOfImageCoord(it.x,it.y) }.sortedBy { it.x }
        if (sortByX[1].y >= sortByX[0].y) {
            firstPoint = sortByX[0]
            thirstPoint = sortByX[1]
        } else {
            firstPoint = sortByX[1]
            thirstPoint = sortByX[0]
        }
        if (sortByX[3].y >= sortByX[2].y) {
            secondPoint = sortByX[2]
            fourthPoint = sortByX[3]
        } else {
            secondPoint = sortByX[3]
            fourthPoint = sortByX[2]
        }
        return listOf(firstPoint, secondPoint, thirstPoint, fourthPoint)
    }

    fun getRatioPoints(): List<PointF> {
        val width = drawable.intrinsicWidth / density
        val height = drawable.intrinsicHeight / density
        return sortedClockwisePoints().map {
            PointF(it.x / width, it.y / height)
        }
    }
}

