package softcom.com.dynamicapp.util

import android.text.Editable
import android.text.TextWatcher
import android.util.Log

class MaskedWatcher(private val formatString: String): TextWatcher {
    var deleting = false
    var added = false

    override fun afterTextChanged(s: Editable?) {
        if (added || deleting)
            return
        added = true

        val editLength = s?.length!!
        if (editLength < formatString.length) {
            if (formatString[editLength] != '#')
                s.append(formatString[editLength])
            else if (formatString[editLength-1] != '#')
                s.insert(editLength-1, formatString, editLength-1, editLength)
        }
        Log.e("Formatter", "Formatted String:$s")
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        deleting = count > after
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }
}