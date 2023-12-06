package com.sy.im.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * 图片压缩工具
 *@Author：sy
 *@Date：2023/11/28
 */
object CompressImageUtils {

    private const val IMAGE_MAX_SIZE = 1 * 1024 * 1024  // 图像的最大大小 1MB

    private const val jpegMime = "image/jpeg"

    private const val webpMime = "image/webp"

    private const val gifMime = "image/gif"

    private const val jpeg = "jpeg"

    suspend fun compressImage(context: Context, imageUri: Uri): ByteArray? {
        return withContext(context = Dispatchers.IO) {  // 使用withContext切换到IO线程执行后续代码，图像设计磁盘IO
            try {

                // 获得图像的mime类型，默认jpeg
                val imageMimeType = getMimeType(context, imageUri)

                // 是否为动画图像
                val isAnimatedImage = imageMimeType == gifMime || imageMimeType == webpMime

                // 用于存储最终的压缩结果
                val result: ByteArray?

                val inputStream = context.contentResolver.openInputStream(imageUri)
                result = if (inputStream == null) {
                    null
                } else {
                    val byteArray = inputStream.readBytes() // 保存为字节数组
                    inputStream.close()
                    if (isAnimatedImage || byteArray.size <= IMAGE_MAX_SIZE) {
                        byteArray
                    } else {
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                        compressImage(
                            bitmap = bitmap,
                            maxSize = IMAGE_MAX_SIZE,
                        )
                    }
                }
                return@withContext result
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return@withContext null
        }
    }

    private suspend fun compressImage(bitmap: Bitmap, maxSize: Int) : ByteArray {
        return withContext(context = Dispatchers.IO) {     // 切换到IO线程
            val byteArrayOutputStream = ByteArrayOutputStream()
            // 初始化图像压缩质量为 100
            var quality = 100
            // 无限循环，直到图像满足了指定的大小要求
            while (true) {
                byteArrayOutputStream.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
                if (byteArrayOutputStream.size() > maxSize) {
                    quality -= 10
                } else {
                    break
                }
            }

            return@withContext byteArrayOutputStream.toByteArray()
        }
    }

    fun createFileName(context: Context, imageUri: Uri): String {
        var mimeType = getMimeType(context = context, imageUri = imageUri)
        return createFileName(mimeType = mimeType)
    }

    /*
     * 图像的MIME类型是指在互联网上传输图像数据时，用于标识图像类型的一种标准。
     */
    private fun getMimeType(context: Context, imageUri: Uri) : String {
        val mimeTypeMap = MimeTypeMap.getSingleton()
        // 使用ContentResolver获取MIME类型
        return mimeTypeMap.getExtensionFromMimeType(context.contentResolver.getType(imageUri)) ?: jpegMime
    }

    private fun createFileName(mimeType: String): String {
        val date = Date()
        val simpleDateFormat = SimpleDateFormat("HHmmssSSS", Locale.getDefault())
        val time = simpleDateFormat.format(date)
        // 获得图像的扩展名
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?:jpeg
        return "sim_$time.$extension"
    }

}