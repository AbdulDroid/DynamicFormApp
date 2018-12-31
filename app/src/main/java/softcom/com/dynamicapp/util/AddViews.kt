package softcom.com.dynamicapp.util

import android.app.DatePickerDialog
import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
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
    for (element in section.elements) {
        when (element.type) {
            "embeddedphoto" -> {
                addImage(context, element.file, element.unique_id, container)
            }
            "yesno" -> {
                addYesNo(context, element, container)
            }
            else -> {
                addText(context, element, container)
            }
        }
    }
}

fun addHeader(context: Context, head: String, views: LinearLayout) {
    val headerView = TextView(context)
    headerView.text = head
    headerView.textSize = 15f
    headerView.typeface = ResourcesCompat.getFont(context, R.font.montserrat_medium)
    val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
    layoutParams.setMargins(
        convertDp2Px(context, 16f), convertDp2Px(context, 16f),
        convertDp2Px(context, 16f), convertDp2Px(context, 8f)
    )
    headerView.layoutParams = layoutParams
    views.addView(headerView)
}

fun addButtons(
    context: Context, last: Boolean, views: LinearLayout, prevListener: View.OnClickListener,
    listener: View.OnClickListener
) {
    val container = LinearLayout(context)
    container.orientation = LinearLayout.HORIZONTAL
    val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
    layoutParams.setMargins(
        convertDp2Px(context, 8f), convertDp2Px(context, 8f),
        convertDp2Px(context, 8f), convertDp2Px(context, 8f)
    )
    layoutParams.gravity = Gravity.CENTER_HORIZONTAL
    val prevButton = Button(context)
    prevButton.isAllCaps = false
    prevButton.background = context.getDrawable(R.drawable.button_background)
    prevButton.setOnClickListener(prevListener)
    prevButton.text = ("Back")
    prevButton.layoutParams = layoutParams
    container.addView(prevButton)
    val button = Button(context)
    button.isAllCaps = false
    button.background = context.getDrawable(R.drawable.button_background)
    if (last) {
        button.text = ("Submit")
    } else {
        button.text = ("Next Page")
    }
    button.setOnClickListener(listener)
    button.layoutParams = layoutParams
    container.addView(button)
    val parentParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
    parentParams.setMargins(
        convertDp2Px(context, 16f), convertDp2Px(context, 24f),
        convertDp2Px(context, 16f), convertDp2Px(context, 16f)
    )
    parentParams.gravity = Gravity.CENTER_HORIZONTAL
    container.layoutParams = parentParams
    views.addView(container)
}

private fun addImage(context: Context, url: String, id: String, view: LinearLayout) {
    val imageView = ImageView(context)
    val layoutParams = LinearLayout.LayoutParams(
        convertDp2Px(context, 96F), convertDp2Px(context, 96F)
    )
    layoutParams.gravity = Gravity.CENTER_HORIZONTAL
    layoutParams.setMargins(
        convertDp2Px(context, 16f), convertDp2Px(context, 8f),
        convertDp2Px(context, 16f), convertDp2Px(context, 8f)
    )
    imageView.layoutParams = layoutParams
    Picasso.get()
        .load(url)
        .into(imageView)
    imageView.tag = id
    view.addView(imageView)

}

private fun addText(context: Context, element: Element, views: LinearLayout) {
    val layout = TextInputLayout(context)
    val view = TextInputEditText(context)
    view.hint = element.label
    view.textSize = 13f
    layout.tag = element.unique_id
    when (element.type) {
        "datetime" -> {
            view.inputType = InputType.TYPE_CLASS_DATETIME
            view.isFocusable = false
            view.isClickable = true
            view.setOnClickListener {
                pickDate(view, element)
            }
        }
        "text" -> {
            if (element.label.equals("Email Address", true))
                view.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            else
                view.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            view.addTextChangedListener(setTextWatcher(element))
        }
        "formattednumeric" -> {
            view.inputType = InputType.TYPE_CLASS_NUMBER
            view.addTextChangedListener(setTextWatcher(element))
        }
    }

    val childParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
    )
    layout.addView(view, 0, childParams)
    val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
    layoutParams.setMargins(
        convertDp2Px(context, 16f), convertDp2Px(context, 8f),
        convertDp2Px(context, 16f), convertDp2Px(context, 8f)
    )
    layout.layoutParams = layoutParams
    views.addView(layout)
}

private fun addYesNo(context: Context, element: Element, views: LinearLayout) {
    val spinner = Spinner(context)
    val options = arrayOf("Yes", "No")
    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, options)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.adapter = adapter
    spinner.setSelection(1)
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            if (view != null) {
                if (element.rules.isNotEmpty()) {
                    for (rule in element.rules) {
                        if (rule.condition == "equals") {
                            if (parent.getItemAtPosition(position).toString() == rule.value) {
                                when (rule.action) {
                                    "show" -> {
                                        for (i in 0 until views.childCount) {
                                            val v = views.getChildAt(i)
                                            if (rule.targets.contains(v.tag))
                                                v.visibility = View.VISIBLE
                                        }
                                    }
                                    "hide" -> {
                                        for (i in 0 until views.childCount) {
                                            val v = views.getChildAt(i)
                                            if (rule.targets.contains(v.tag))
                                                v.visibility = View.GONE
                                        }
                                    }
                                }
                            } else {
                                when (rule.otherwise) {
                                    "show" -> {
                                        for (i in 0 until views.childCount) {
                                            val v = views.getChildAt(i)
                                            if (rule.targets.contains(v.tag))
                                                v.visibility = View.VISIBLE
                                        }
                                    }
                                    "hide" -> {
                                        for (i in 0 until views.childCount) {
                                            val v = views.getChildAt(i)
                                            if (rule.targets.contains(v.tag))
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
        convertDp2Px(context, 16f), convertDp2Px(context, 8f),
        convertDp2Px(context, 16f), convertDp2Px(context, 8f)
    )
    spinner.layoutParams = layoutParams
    views.addView(spinner)
}

private fun setTextWatcher(element: Element): TextWatcher {
    return object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            element.value = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

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
    ).show()
}