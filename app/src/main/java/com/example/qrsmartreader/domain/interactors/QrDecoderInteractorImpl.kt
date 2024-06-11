package com.example.qrsmartreader.domain.interactors

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import com.example.qrsmartreader.App
import com.example.qrsmartreader.SingleLiveEvent
import com.example.qrsmartreader.domain.entities.AiResultEntity
import com.example.qrsmartreader.ui.interactors.QrDecoderInteractor
import com.example.qrsmartreader.ui.interactors.QrResultsInteractor
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.yanzhenjie.zbar.Image
import com.yanzhenjie.zbar.ImageScanner
import java.util.EnumMap
import javax.inject.Inject

class QrDecoderInteractorImpl : QrDecoderInteractor {
    override val recognisedQrSLE = SingleLiveEvent<String>()

override var aiResult: AiResultEntity? = null

    @Inject
    lateinit var qrResultsInteractor: QrResultsInteractor

    init {
        App().component.inject(this)
    }

    private var working = false
    private val imageScannerZbar = ImageScanner()
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    val scanner = BarcodeScanning.getClient(options)


    override fun decodeQRCodeAsync(app: Application, bitmap: Bitmap) {
        if (!working) working = true else return
//        val image = InputImage.fromByteArray(bytes, w,h,0, InputImage.IMAGE_FORMAT_NV21)
        val image = InputImage.fromBitmap(bitmap, 0)
        var res = ""
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    res = barcodes[0].rawValue.toString()
                    recognisedQrSLE.value = res

                    qrResultsInteractor.addResultToHistory(app, res)


                }
                working = false
            }
            .addOnFailureListener { exception ->
                working = false
            }
        recognisedQrSLE.value = res
        return
    }

    fun decodeQRCode4(intArray: IntArray, h: Int, w: Int): String {
        val hints: MutableMap<DecodeHintType, Boolean?> = EnumMap(
            DecodeHintType::class.java
        )
        hints[DecodeHintType.TRY_HARDER] = true

        var decoded: String? = ""
        val source = RGBLuminanceSource(w, h, intArray)
        decoded = try {
            QRCodeReader()
                .decode(
                    BinaryBitmap(HybridBinarizer(source)),
                    hints
                ).text
        } catch (e: Exception) {
            null
        }
        if (decoded == null) {
            decoded = try {
                QRCodeReader()
                    .decode(
                        BinaryBitmap(GlobalHistogramBinarizer(source)),
                        hints
                    ).text
            } catch (e: Exception) {
                null
            }
        }

        return decoded ?: ""
    }

    fun decodeQRCode3(intArray: IntArray, h: Int, w: Int): String {

        val barcode = Image(w, h, "Y800")
        barcode.setData(intArray)

        var qrCodeString = ""

        val result = imageScannerZbar.scanImage(barcode)
        if (result != 0) {
            val symSet = imageScannerZbar.results
            symSet.forEach { sym ->
                qrCodeString = sym.data
            }
        }

        return qrCodeString

    }
}