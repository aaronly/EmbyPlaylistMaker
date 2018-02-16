package us.echols.embyplaylistmaker.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import us.echols.embyplaylistmaker.data.model.User
import us.echols.embyplaylistmaker.util.MyLogger
import us.echols.embyplaylistmaker.util.debug
import us.echols.embyplaylistmaker.util.info
import java.io.File
import java.io.FileInputStream
import java.util.*

class FileHelper(context: Context) : MyLogger {

    private val guidsDirectory = File(context.filesDir, "guids")
    private val imageDirectory = File(context.filesDir, "images")
    private val userImageDirectory = File(imageDirectory, "users")

    fun saveUserImage(user: User, bitmapImage: Bitmap): String {
        checkImageFileLocations()

        val imageFile = File(userImageDirectory, "${user.name}.png")

        imageFile.outputStream().use { thing ->
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, thing)
        }
//        var fos: FileOutputStream? = null
//
//        try {
//            fos = FileOutputStream(imageFile)
//            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } finally {
//            fos?.close()
//        }
        return imageFile.absolutePath
    }

    fun getUserImage(user: User): Bitmap? {
        checkImageFileLocations()

        val imageFile = File(userImageDirectory, "${user.name}.png")
        return BitmapFactory.decodeStream(FileInputStream(imageFile))
    }

    private fun saveDeviceId(): String {
        checkGUIDsLocations()

        val deviceId = UUID.randomUUID().toString()

        val deviceIdFile = File(guidsDirectory, "device.png")
        val stream = deviceIdFile.outputStream()

        stream.bufferedWriter().use { writer ->
            writer.write(deviceId)
        }

        return deviceId
    }

    fun getDeviceId(): String {
        checkGUIDsLocations()

        var deviceId: String? = null

        val stream = File(guidsDirectory, "device.png").inputStream()

        stream.buffered().reader().use { reader ->
            deviceId = reader.readText()
        }

        return deviceId ?: saveDeviceId()
    }

    private fun checkImageFileLocations() {
        if (!imageDirectory.exists()) {
            if (imageDirectory.mkdirs()) {
                debug("Created ${imageDirectory.absolutePath}")
            } else {
                info("Could not create ${imageDirectory.absolutePath}")
            }
        } else {
            debug("Found ${imageDirectory.absolutePath}")
        }

        if (!userImageDirectory.exists()) {
            if (userImageDirectory.mkdirs()) {
                debug("Created ${userImageDirectory.absolutePath}")
            } else {
                info("Could not create ${userImageDirectory.absolutePath}")
            }
        } else {
            debug("Found ${userImageDirectory.absolutePath}")
        }
    }

    private fun checkGUIDsLocations() {
        if (!guidsDirectory.exists()) {
            if (guidsDirectory.mkdirs()) {
                debug("Created ${guidsDirectory.absolutePath}")
            } else {
                info("Could not create ${guidsDirectory.absolutePath}")
            }
        } else {
            debug("Found ${guidsDirectory.absolutePath}")
        }

    }
}