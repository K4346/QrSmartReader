package com.example.qrsmartreader.domain

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.qrsmartreader.domain.entities.AiResultEntity
import java.nio.FloatBuffer
import java.util.PriorityQueue
import kotlin.math.max
import kotlin.math.min

object DataProcess {

    const val BATCH_SIZE = 1
    const val IMAGE_INPUT_SIZE = 640
    const val PIXEL_SIZE = 3
    const val FILE_NAME = "yolov8_pose_n_fp16.onnx"

    var aiResult: AiResultEntity? = null


    fun imageToBitmap(img: Bitmap): Bitmap {
        // val bitmap = imageProxy.toBitmap()
        return Bitmap.createScaledBitmap(
            img
//            bitmap
            , IMAGE_INPUT_SIZE, IMAGE_INPUT_SIZE, true
        )
    }

    fun bitmapToFloatBuffer(bitmap: Bitmap): FloatBuffer {
        val imageSTD = 255f
        val buffer =
            FloatBuffer.allocate(BATCH_SIZE * PIXEL_SIZE * IMAGE_INPUT_SIZE * IMAGE_INPUT_SIZE)
        buffer.rewind()

        val area = IMAGE_INPUT_SIZE * IMAGE_INPUT_SIZE
        val bitmapData = IntArray(area)
        bitmap.getPixels(
            bitmapData,
            0,
            bitmap.width,
            0,
            0,
            bitmap.width,
            bitmap.height
        )
        for (i in 0 until IMAGE_INPUT_SIZE - 1) {
            for (j in 0 until IMAGE_INPUT_SIZE - 1) {
                val idx = IMAGE_INPUT_SIZE * i + j
                val pixelValue = bitmapData[idx]
                buffer.put(idx, ((pixelValue shr 16 and 0xff) / imageSTD))
                buffer.put(idx + area, ((pixelValue shr 8 and 0xff) / imageSTD))
                buffer.put(idx + area * 2, ((pixelValue and 0xff) / imageSTD))
            }
        }
        buffer.rewind()
        return buffer
    }


    fun outputsToNPMSPredictions(outputs: Array<*>): ArrayList<FloatArray> {
        val confidenceThreshold = 0.4f
        val rows: Int
        val cols: Int
        val results = ArrayList<FloatArray>()

        (outputs[0] as Array<*>).also {
            rows = it.size
            cols = (it[0] as FloatArray).size
        }

        val output = Array(cols) { FloatArray(rows) }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                output[j][i] = (((outputs[0] as Array<*>)[i]) as FloatArray)[j]
            }
        }

        var maxq = -100f
        for (i in 0 until cols) {
            if (output[i][4] > maxq) {
                maxq = output[i][4]
            }
        }
        for (i in 0 until cols) {
            if (output[i][4] > confidenceThreshold) {
                val xPos = output[i][0]
                val yPos = output[i][1]
                val width = output[i][2]
                val height = output[i][3]

                val x1 = max(xPos - width / 2f, 0f)
                val x2 = min(xPos + width / 2f, IMAGE_INPUT_SIZE - 1f)
                val y1 = max(yPos - height / 2f, 0f)
                val y2 = min(yPos + height / 2f, IMAGE_INPUT_SIZE - 1f)

                output[i][0] = x1
                output[i][1] = y1
                output[i][2] = x2
                output[i][3] = y2

                results.add(output[i])
            }
        }
        return nms(results)
    }

    private fun nms(results: ArrayList<FloatArray>): ArrayList<FloatArray> {
        val list = ArrayList<FloatArray>()
        val pq = PriorityQueue<FloatArray>(5) { o1, o2 ->
            o1[4].compareTo(o2[4])
        }

        pq.addAll(results)

        while (pq.isNotEmpty()) {
            val detections = pq.toTypedArray()
            val max = detections[0]
            list.add(max)
            pq.clear()

            for (k in 1 until detections.size) {
                val detection = detections[k]
                val rectF = RectF(detection[0], detection[1], detection[2], detection[3])
                val maxRectF = RectF(max[0], max[1], max[2], max[3])
                val iouThreshold = 0.5f
                if (boxIOU(maxRectF, rectF) < iouThreshold) {
                    pq.add(detection)
                }
            }
        }
        return list
    }


    private fun boxIOU(a: RectF, b: RectF): Float {
        return boxIntersection(a, b) / boxUnion(a, b)
    }


    private fun boxIntersection(a: RectF, b: RectF): Float {
        val w = overlap(
            (a.left + a.right) / 2f, a.right - a.left,
            (b.left + b.right) / 2f, b.right - b.left
        )

        val h = overlap(
            (a.top + a.bottom) / 2f, a.bottom - a.top,
            (b.top + b.bottom) / 2f, b.bottom - b.top
        )
        return if (w < 0 || h < 0) 0f else w * h
    }

    private fun boxUnion(a: RectF, b: RectF): Float {
        val i = boxIntersection(a, b)
        return (a.right - a.left) * (a.bottom - a.top) + (b.right - b.left) * (b.bottom - b.top) - i
    }

    private fun overlap(x1: Float, w1: Float, x2: Float, w2: Float): Float {
        val l1 = x1 - w1 / 2
        val l2 = x2 - w2 / 2
        val left = max(l1, l2)
        val r1 = x1 + w1 / 2
        val r2 = x2 + w2 / 2
        val right = min(r1, r2)
        return right - left
    }
}