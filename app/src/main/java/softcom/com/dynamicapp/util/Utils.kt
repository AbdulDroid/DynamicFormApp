package softcom.com.dynamicapp.util

import android.content.Context
import android.util.Log
import java.text.DecimalFormat

fun formatNumber(formatString: String, number: Int): String {
    val formatter = DecimalFormat(formatString)
    return formatter.format(number)
}

fun convertDp2Px(context: Context, dips: Float): Int {
    val result =  (dips * context.resources.displayMetrics.density + 0.5f).toInt()
    Log.e("DpConv", result.toString())
    return result
}