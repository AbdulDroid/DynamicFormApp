package softcom.com.dynamicapp.util

import android.content.Context
import android.text.Editable
import android.util.Log

fun formatNumber(formatString: String, str: Editable) {
    val editLength = str.length
    if (editLength < formatString.length) {
        if (formatString[editLength] != '#')
            str.append(formatString[editLength])
        else if (formatString[editLength-1] != '#')
            str.insert(editLength-1, formatString, editLength-1, editLength)
    }
    Log.e("Formatter", "Formatted String:$str")
}

fun convertDp2Px(context: Context, dips: Float): Int {
    val result =  (dips * context.resources.displayMetrics.density + 0.5f).toInt()
    return result
}