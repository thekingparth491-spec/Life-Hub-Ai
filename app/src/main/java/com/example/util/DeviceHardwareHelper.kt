package com.example.util

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DeviceBatteryInfo(
    val percentage: Int,
    val isCharging: Boolean,
    val chargePlug: String, // AC, USB, Wireless
    val health: String,     // Good, Overheat, etc.
    val temperatureCelsius: Float,
    val voltageVolts: Float,
    val technology: String
)

data class DeviceStorageInfo(
    val totalBytes: Long,
    val freeBytes: Long,
    val usedBytes: Long,
    val totalGb: Double,
    val freeGb: Double,
    val usedGb: Double,
    val usedPercentage: Int
)

data class GalleryPhotoItem(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val dateAddedStr: String,
    val sizeFormatted: String
)

object DeviceHardwareHelper {

    fun getBatteryInfo(context: Context): DeviceBatteryInfo {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        if (batteryIntent == null) {
            return DeviceBatteryInfo(
                percentage = 85,
                isCharging = false,
                chargePlug = "Battery",
                health = "Good",
                temperatureCelsius = 28.5f,
                voltageVolts = 3.85f,
                technology = "Li-ion"
            )
        }

        val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val pct = if (level >= 0 && scale > 0) (level * 100) / scale else 85

        val status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        val chargePlugInt = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val chargePlug = when (chargePlugInt) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Fast Charger"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB Cable"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Dock"
            else -> if (isCharging) "Charging" else "Unplugged (Battery)"
        }

        val healthInt = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_GOOD)
        val health = when (healthInt) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Optimal Condition"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "High Temperature"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Degraded"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            else -> "Normal"
        }

        val rawTemp = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 280)
        val tempCelsius = rawTemp / 10.0f

        val rawVoltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 3850)
        val voltageVolts = rawVoltage / 1000.0f

        val tech = batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-poly"

        return DeviceBatteryInfo(
            percentage = pct,
            isCharging = isCharging,
            chargePlug = chargePlug,
            health = health,
            temperatureCelsius = tempCelsius,
            voltageVolts = voltageVolts,
            technology = tech
        )
    }

    fun getStorageInfo(): DeviceStorageInfo {
        return try {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            val totalBytes = totalBlocks * blockSize
            val freeBytes = availableBlocks * blockSize
            val usedBytes = (totalBytes - freeBytes).coerceAtLeast(0L)

            val totalGb = totalBytes / (1024.0 * 1024.0 * 1024.0)
            val freeGb = freeBytes / (1024.0 * 1024.0 * 1024.0)
            val usedGb = usedBytes / (1024.0 * 1024.0 * 1024.0)
            val usedPct = if (totalGb > 0) ((usedGb / totalGb) * 100).toInt() else 50

            DeviceStorageInfo(
                totalBytes = totalBytes,
                freeBytes = freeBytes,
                usedBytes = usedBytes,
                totalGb = totalGb,
                freeGb = freeGb,
                usedGb = usedGb,
                usedPercentage = usedPct
            )
        } catch (e: Exception) {
            DeviceStorageInfo(
                totalBytes = 64L * 1024 * 1024 * 1024,
                freeBytes = 32L * 1024 * 1024 * 1024,
                usedBytes = 32L * 1024 * 1024 * 1024,
                totalGb = 64.0,
                freeGb = 32.0,
                usedGb = 32.0,
                usedPercentage = 50
            )
        }
    }

    fun searchGalleryPhotos(context: Context, query: String = "", limit: Int = 30): List<GalleryPhotoItem> {
        val photos = mutableListOf<GalleryPhotoItem>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.SIZE
        )

        val selection = if (query.isNotBlank()) {
            "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?"
        } else null

        val selectionArgs = if (query.isNotBlank()) {
            arrayOf("%$query%")
        } else null

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

                val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

                while (it.moveToNext() && photos.size < limit) {
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn) ?: "Photo_$id.jpg"
                    val dateAddedSec = it.getLong(dateColumn)
                    val sizeBytes = it.getLong(sizeColumn)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val dateStr = sdf.format(Date(dateAddedSec * 1000))
                    val sizeFormatted = formatFileSize(sizeBytes)

                    photos.add(
                        GalleryPhotoItem(
                            id = id,
                            uri = contentUri,
                            displayName = name,
                            dateAddedStr = dateStr,
                            sizeFormatted = sizeFormatted
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Permission not granted or empty gallery in emulator
        }

        return photos
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> String.format(Locale.getDefault(), "%.1f MB", bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> String.format(Locale.getDefault(), "%.0f KB", bytes / 1024.0)
            else -> "$bytes B"
        }
    }
}
