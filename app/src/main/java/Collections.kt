import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.content.edit
import androidx.core.graphics.drawable.toBitmap
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * 将Drawable保存为Png文件
 * @receiver Drawable
 * @param fileName String
 * @param path String
 */
fun Drawable.saveToFile(fileName: String, path: String) {
    val iconDir = File(path)
    if (!iconDir.exists()) iconDir.mkdirs()
    val icon = File(iconDir, fileName)
    val bitmap = toBitmap()
    val outStream = FileOutputStream(icon)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
    outStream.flush()
    outStream.close()
}

/**
 * 替换SharedPReferences中的数据
 * @receiver SharedPreferences
 * @param values MutableMap<String, *>
 */
fun SharedPreferences.replaceAll(values: MutableMap<String, *>) {
    this.edit {
        values.forEach {
            when (val value = it.value) {
                is Boolean -> this.putBoolean(it.key, value)
                is String -> this.putString(it.key, value)
                is Float -> this.putFloat(it.key, value)
                is Long -> this.putLong(it.key, value)
                is Int -> this.putInt(it.key, value)
            }
        }
    }
}

/**
 * 获取应用shared_prefs文件夹
 * 例如：/data/data/com.example.app/shared_prefs
 * @receiver Context
 * @return File
 */
fun Context.sharedPrefsDir(): File {
    return File(dataRootDir(), "shared_prefs")
}

/**
 * 获取应用私有数据目录
 * 例如：/data/data/com.example.app/
 * @receiver Context
 * @return File
 */
fun Context.dataRootDir(): File = File(filesDir.parentFile.path)

/**
 * 将任意字符转换为MD5
 * @receiver String
 * @return String
 */
fun String.toMD5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(toByteArray())
    return bytes.toHex()
}

/**
 * 转化为SHA256
 * @receiver String
 * @return String
 */
fun String.toSHA256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(toByteArray())
    return bytes.fold("", { str, it -> str + "%02x".format(it) })
}

/**
 * 将二进制数组转16进制
 * @receiver ByteArray
 * @return String
 */
fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}