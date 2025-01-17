package com.zenenta.myhelper.extension

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.zenenta.myhelper.R
import com.zenenta.myhelper.util.AppConstants.TIME_STAMP_FORMAT
import java.net.URL
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun visible() = View.VISIBLE
fun invisible() = View.INVISIBLE
fun gone() = View.GONE

const val dateFormat = "yyyy-MM-dd"

fun logs(message: String? = "message") {
    Log.d("RESPONSE", message.def())
}

fun logs(tag: String?, message: String?) {
    Log.d(tag, message ?: "message")
}

fun logs(tag: String?, vararg str: String) {
    var message = ""
    for ((i, s) in str.withIndex()) {
        message += if (i == str.size - 1) s else "$s - "
    }
    Log.d(tag, message)
}

fun loge(message: String?) {
    Log.e("ERROR", message ?: "message")
}

fun loge(tag: String, message: String?) {
    Log.e(tag, message ?: "message")
}

fun longLogs(longString: String, tag: String = "RESPONS") {
    val maxLogSize = 3000
    for (i in 0..longString.length / maxLogSize) {
        val start = i * maxLogSize
        var end = (i + 1) * maxLogSize
        end = if (end > longString.length) longString.length else end
        logs(tag, longString.substring(start, end))
    }
}

fun Context.setToolbar(view: Toolbar, title: String) {
    (this as AppCompatActivity).setSupportActionBar(view)
    this.supportActionBar!!.title = title
    this.supportActionBar!!.setDisplayShowHomeEnabled(true)
    this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
}

fun Activity.setTransparantStatusBar() {
    val w: Window = this.window
    w.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
}

fun Activity.hidenKeyboard() {
    this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
}

fun Fragment.hideKeyboard() {
    val imm: InputMethodManager =
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view!!.windowToken, 0)
}

fun Context.setError(editText: EditText, message: String = "Kolom Tidak Boleh Kosong") {
    editText.error = message
    editText.requestFocus()
}

fun Activity.isCameraPermissionGranted(context: Context, REQUEST_PERMISSION_CAMERA: Int): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        if (context.checkSelfPermission(Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            // Show the permission request
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION_CAMERA
            )
            false
        }
    } else {
        true
    }
}

fun getFragmentWidthPercentage(percentage: Int): Int {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    return (rect.width() * percent).toInt()
}

fun Context.verticalLayoutManager(): LinearLayoutManager {
    val layoutManager = LinearLayoutManager(this)
    layoutManager.orientation = LinearLayoutManager.VERTICAL
    return layoutManager
}

fun Fragment.verticalLayoutManager(): LinearLayoutManager {
    val layoutManager = LinearLayoutManager(requireActivity())
    layoutManager.orientation = LinearLayoutManager.VERTICAL
    return layoutManager
}

fun Context.horizontalLayoutManager(): LinearLayoutManager {
    val layoutManager = LinearLayoutManager(this)
    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
    return layoutManager
}

fun Fragment.horizontalLayoutManager(): LinearLayoutManager {
    val layoutManager = LinearLayoutManager(requireActivity())
    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
    return layoutManager
}

@SuppressLint("SimpleDateFormat")
fun convertTglUTC(date: String, ygDimau: String): String {
    var hasil = ""
    val frmtlama = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val dateFormat = SimpleDateFormat(frmtlama)
    try {
        val dd = dateFormat.parse(date)
        // tambah 7 jam untuk indonesia
        val hour: Long = 3600 * 1000 // in milli-seconds.
        val newDate = Date(dd!!.time + 7 * hour)
        dateFormat.applyPattern(ygDimau)
        hasil = dateFormat.format(newDate)

    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return hasil
}

fun Context.shareTo(message: String) {
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_TEXT, message)
    intent.type = "text/plain"
    startActivity(Intent.createChooser(intent, "Share to.."))
}

fun Context.copyText(text: String, showToast: Boolean = true) {
    val copyManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val copyText = ClipData.newPlainText("text", text)
    copyManager.setPrimaryClip(copyText)

    if (showToast)
        Toast.makeText(this, "Text Berhasil di salin", Toast.LENGTH_LONG).show()
}

fun Activity.popUpMenu(view: View, list: List<String>, onClicked: (String) -> Unit) {
    val popupMenu = PopupMenu(ContextThemeWrapper(this, R.style.CustomPopupMenu), view)
    popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
        onClicked.invoke(it.toString())
        return@OnMenuItemClickListener true
    })
    list.forEach {
        popupMenu.menu.add(it)
    }
    popupMenu.show()
}

fun Context.popUpMenu(view: View, list: List<String>, onClicked: (String) -> Unit) {
    val popupMenu = PopupMenu(ContextThemeWrapper(this, R.style.CustomPopupMenu), view)
    popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
        onClicked.invoke(it.toString())
        return@OnMenuItemClickListener true
    })
    list.forEach {
        popupMenu.menu.add(it)
    }
    popupMenu.show()
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toTimeStamp(format: String = TIME_STAMP_FORMAT): String {
    return DateTimeFormatter.ofPattern(format).format(this)
}

fun TextView.setErrors(
    message: String = "Oopss, gagal memuat data!\nCoba lagi!",
    onClick: (() -> Unit?)? = null
) {
    this.text = message
    this.setOnClickListener {
        onClick?.invoke()
    }
}

fun delayFunction(r: Runnable, duration: Long = 300) {
    Handler(Looper.getMainLooper()).postDelayed(r, duration)
}

@SuppressLint("HardwareIds")
fun Context.getImei(): String {

    fun getDeviceID(): String {
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
            .toString()
    }

    val imei: String
    val permisI = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
    imei = if (permisI == PackageManager.PERMISSION_GRANTED) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val telephonyManager =
                    this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                telephonyManager.imei
            } catch (e: Exception) {
                getDeviceID()
            }
        } else getDeviceID()
    } else {
        "unknown"
    }

    return imei.uppercase()
}

fun checkPrefix(mPhone: String): String {
    when (mPhone.substring(0, 4)) {
        "0831", "0832", "0833", "0835", "0836", "0837", "0838", "0839" -> {
            return "Axis"
        }

        "0817", "0818", "0819", "0859", "0877", "0878", "0879" -> {
            return "XL"
        }

        "0811", "0812", "0813", "0821", "0822", "0823", "0851", "0852", "0853", "0854" -> {
            return "Telkomsel"
        }

        "0814", "0815", "0816", "0855", "0856", "0857", "0858" -> {
            return "Indosat"
        }

        "0895", "0896", "0897", "0898", "0899" -> {
            return "Tri"
        }

        "0881", "0882", "0883", "0884", "0885", "0886", "0887", "0888", "0889" -> {
            return "Smartfren"
        }

        "0828" -> {
            return "Ceria"
        }

        else -> {
            return "non"
        }
    }
}

fun Int?.def(v: Int = 0): Int {
    return this ?: v
}

fun String?.def(v: String = ""): String {
    return this ?: v
}

fun Double?.def(v: Double = 0.0): Double {
    return this ?: v
}

fun Long?.def(v: Long = 0L): Long {
    return this ?: v
}

fun Context.isOffline(): Boolean {
    return !isOnline()
}

fun Fragment.isOffline(): Boolean {
    return !requireActivity().isOnline()
}

fun Fragment.isOnline(): Boolean {
    return requireActivity().isOnline()
}

fun Context.isOnline(): Boolean {
    val context = this
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            logs("Internet:NetworkCapabilities.TRANSPORT_CELLULAR")
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            logs("Internet:NetworkCapabilities.TRANSPORT_WIFI")
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            logs("Internet:NetworkCapabilities.TRANSPORT_ETHERNET")
            return true
        }
    }
    logs("Internet:NetworkCapabilities.NO_INTERNET_CONNECTION")
    return false
}


inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}


fun Bitmap.toBlackAndWhite(): Bitmap {
    val width = this.width
    val height = this.height
    // create output bitmap
    val bmOut = Bitmap.createBitmap(width, height, this.config)
    // color information
    var a: Int
    var r: Int
    var g: Int
    var b: Int
    var pixel: Int
    for (x in 0 until width) {
        for (y in 0 until height) {
            // get pixel color
            pixel = this.getPixel(x, y)
            a = Color.alpha(pixel)
            r = Color.red(pixel)
            g = Color.green(pixel)
            b = Color.blue(pixel)
            var gray = (0.2989 * r + 0.5870 * g + 0.1140 * b).toInt()
            // use 128 as threshold, above -> white, below -> black
            gray = if (gray > 128) {
                255
            } else {
                0
            }
            // set new pixel color to output bitmap
            bmOut.setPixel(x, y, Color.argb(a, gray, gray, gray))
        }
    }
    return bmOut
}

fun Bitmap.addPaddingLeft(paddingLeft: Int): Bitmap {
    val outputBitmap = Bitmap.createBitmap(width + paddingLeft, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    canvas.drawColor(Color.WHITE)
    canvas.drawBitmap(this, paddingLeft.toFloat(), 0f, null)
    return outputBitmap
}

fun String.convertUrlToBitmap(): Bitmap? {
    var bitmap: Bitmap? = null
    try {
        val url = URL(this)
        val connection = url.openConnection()
        connection.doInput = true
        connection.connect()
        val input = connection.getInputStream()
        bitmap = BitmapFactory.decodeStream(input)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return bitmap
}