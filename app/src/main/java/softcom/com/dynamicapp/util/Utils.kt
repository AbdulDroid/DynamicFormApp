package softcom.com.dynamicapp.util

import android.content.Context
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

fun formatNumber(formatString: String, number: Int): String {
    val formatter = NumberFormat.getInstance(Locale.getDefault())
    if (formatter is DecimalFormat) {
        formatter.applyPattern(formatString)
    }
    return formatter.format(number)
}

fun convertDp2Px(context: Context, dips: Float): Int {
    val result =  (dips * context.resources.displayMetrics.density + 0.5f).toInt()
    return result
}