package softcom.com.dynamicapp.util

import java.text.DecimalFormat

fun formatNumber(formatString: String, number: Int): String {
    val formatter = DecimalFormat(formatString)
    return formatter.format(number)
}