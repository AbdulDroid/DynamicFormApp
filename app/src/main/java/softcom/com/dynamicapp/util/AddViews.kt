package softcom.com.dynamicapp.util

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.View
import android.widget.*
import com.squareup.picasso.Picasso
import softcom.com.dynamicapp.R
import softcom.com.dynamicapp.data.Element
import softcom.com.dynamicapp.data.Section
import java.text.SimpleDateFormat
import java.util.*


fun addViews(context: Context, section: Section, container: LinearLayout) {
    section.elements.forEach {
        when (it.type) {
            "embeddedphoto" -> {
                addImage(context, it.file, it.unique_id, container)
            }
            "yesno" -> {
                addYesNo(context, it, container)
            }
            else -> {
                addText(context, it, container)
            }
        }
    }
}

fun addHeader(context: Context, head: String, views: LinearLayout) {
    val headerView = TextView(context).apply {
        text = head
        textSize = 15f
        setTextColor(ContextCompat.getColor(context, R.color.colorText))
        typeface = ResourcesCompat.getFont(context, R.font.montserrat_medium)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(
            16, 16, 16, 8
        )
        this.layoutParams = layoutParams
    }
    views.addView(headerView)
}

fun addButtons(
    context: Context, last: Boolean, views: LinearLayout, prevListener: View.OnClickListener,
    listener: View.OnClickListener
) {
    val container = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(
            8, 8, 8, 8
        )
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL
        val prevButton = Button(context).apply {
            isAllCaps = false
            background = context.getDrawable(R.drawable.button_background)
            setOnClickListener(prevListener)
            text = ("Back")
            setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            this.layoutParams = layoutParams
        }
        addView(prevButton)
        val button = Button(context).apply {
            isAllCaps = false
            background = context.getDrawable(R.drawable.button_background)
            text = if (last) {
                ("Submit")
            } else {
                ("Next Page")
            }
            setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            setOnClickListener(listener)
            this.layoutParams = layoutParams
        }
        addView(button)
        val parentParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        parentParams.setMargins(
            16, 24, 16, 32
        )
        parentParams.gravity = Gravity.CENTER_HORIZONTAL
        this.layoutParams = parentParams
    }
    views.addView(container)
}

private fun addImage(context: Context, url: String, id: String, view: LinearLayout) {
    val imageView = ImageView(context).apply {
        val layoutParams = LinearLayout.LayoutParams(
            convertDp2Px(context, 96F), convertDp2Px(context, 96F)
        )
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL
        layoutParams.setMargins(
            16, 8, 16, 8
        )
        this.layoutParams = layoutParams
        Picasso.get()
            .load(url)
            .into(this)
        this.tag = id
    }
    view.addView(imageView)

}

private fun addText(context: Context, element: Element, views: LinearLayout) {
    val layout = TextInputLayout(context).apply {
        tag = element.unique_id
        val view = TextInputEditText(context).apply {
            hint = element.label
            if (element.isMandatory)
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_asterisk, 0, 0, 0)
            textSize = 14f
            if (element.value.isNotEmpty()) {
                setText(element.value)
            }
            setTextColor(ContextCompat.getColor(context, R.color.colorText))
            setHintTextColor(ContextCompat.getColor(context, R.color.colorTextSecondary))
            tag = element.unique_id
            when (element.type) {
                "datetime" -> {
                    inputType = InputType.TYPE_CLASS_DATETIME
                    isFocusable = false
                    isClickable = true
                    setOnClickListener {
                        if (element.mode.equals("date", true))
                            pickDate(it, element)
                        else if (element.mode.equals("time", true))
                            pickTime(it, element)
                    }
                }
                "text" -> {
                    inputType = if (element.label.equals("Email Address", true))
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    else
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS
                    addTextChangedListener(setTextWatcher(element))
                }
                "formattednumeric" -> {
                    keyListener = DigitsKeyListener.getInstance("0123456789-+ ")

                    addTextChangedListener(setTextWatcher(element))
                }
            }
        }
        val childParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        addView(view, 0, childParams)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(
            16, 12, 16, 12
        )
        this.layoutParams = layoutParams
    }
    views.addView(layout)
}

private fun addYesNo(context: Context, element: Element, views: LinearLayout) {
    val text = TextView(context).apply {
        text = (element.label)
        tag = element.unique_id
        val tLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tLayoutParams.setMargins(
            16, 8, 16, 4
        )
        layoutParams = tLayoutParams
    }
    views.addView(text)
    val spinner = Spinner(context).apply {
        val options = arrayOf("Yes", "No")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = adapter
        if (element.value.isNotEmpty() && element.value.equals("Yes", true))
            setSelection(0)
        else
            setSelection(1)
        tag = element.unique_id
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view != null) {
                    if (element.rules.isNotEmpty()) {
                        element.rules.forEach {
                            if (it.condition == "equals") {
                                element.value = parent.getItemAtPosition(position).toString()
                                if (parent.getItemAtPosition(position).toString() == it.value) {
                                    when (it.action) {
                                        "show" -> {
                                            for (i in 0 until views.childCount) {
                                                val v = views.getChildAt(i)
                                                if (it.targets.contains(v.tag))
                                                    v.visibility = View.VISIBLE
                                            }
                                        }
                                        "hide" -> {
                                            for (i in 0 until views.childCount) {
                                                val v = views.getChildAt(i)
                                                if (it.targets.contains(v.tag))
                                                    v.visibility = View.GONE
                                            }
                                        }
                                    }
                                } else {
                                    when (it.otherwise) {
                                        "show" -> {
                                            for (i in 0 until views.childCount) {
                                                val v = views.getChildAt(i)
                                                if (it.targets.contains(v.tag))
                                                    v.visibility = View.VISIBLE
                                            }
                                        }
                                        "hide" -> {
                                            for (i in 0 until views.childCount) {
                                                val v = views.getChildAt(i)
                                                if (it.targets.contains(v.tag))
                                                    v.visibility = View.GONE
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(
            16, 4, 16, 8
        )
        this.layoutParams = layoutParams
    }
    views.addView(spinner)
}

private fun setTextWatcher(element: Element): TextWatcher {
    var isDeleting = false
    var isAdded = false
    return object : TextWatcher {
        @SuppressLint("SetTextI18n")
        override fun afterTextChanged(s: Editable?) {
            if (isDeleting || isAdded)
                return
            isAdded = true
            //TODO not that this condition works for cases where the character mask element is '#'
            if (element.keyboard == "numeric" && element.formattedNumeric.isNotEmpty()) {
                val sLength = s?.length!!
                if (sLength < element.formattedNumeric.length) {
                    if (element.formattedNumeric[sLength] != '#')
                        s.append(element.formattedNumeric[sLength])
                    else if (element.formattedNumeric[sLength - 1] != '#')
                        s.insert(sLength - 1, element.formattedNumeric, sLength - 1, sLength)
                }
                element.value = s.toString()
            } else
                element.value = s?.toString()!!
            isAdded = false
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            isDeleting = count > after
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

    }
}

private fun pickDate(view: View, element: Element) {
    val calendar = Calendar.getInstance()
    val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        (view as TextInputEditText).setText(sdf.format(calendar.time))
        element.value = sdf.format(calendar.time)
    }
    DatePickerDialog(
        view.context, datePickerListener, calendar
            .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.maxDate = Calendar.getInstance().timeInMillis
    }.show()
}

@SuppressLint("SetTextI18n")
private fun pickTime(view: View, element: Element) {
    val calendar = Calendar.getInstance()
    val hours = calendar.get(Calendar.HOUR_OF_DAY)
    val mins = calendar.get(Calendar.MINUTE)
    TimePickerDialog(
        view.context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            (view as TextInputEditText).setText("$hourOfDay:$mins")
            element.value = "$hourOfDay:$minute"
        }, hours, mins, false
    ).show()
}