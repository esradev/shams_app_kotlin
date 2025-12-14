package ir.wpstorm.shams.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object DownloadHelper {

    suspend fun downloadFile(
        context: Context,
        fileUrl: String,
        fileName: String,
        onProgress: ((Float) -> Unit)? = null
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext null
                }

                val fileLength = connection.contentLength
                val fileDir = File(context.filesDir, "shams_app")
                if (!fileDir.exists()) fileDir.mkdirs()

                val file = File(fileDir, fileName)
                val outputStream = FileOutputStream(file)

                connection.inputStream.use { input ->
                    outputStream.use { output ->
                        val buffer = ByteArray(8192)
                        var totalRead = 0L
                        var bytesRead: Int

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalRead += bytesRead

                            if (fileLength > 0 && onProgress != null) {
                                val progress = totalRead.toFloat() / fileLength.toFloat()
                                withContext(Dispatchers.Main) {
                                    onProgress(progress)
                                }
                            }
                        }
                    }
                }

                file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
