package softcom.com.dynamicapp

import android.app.DatePickerDialog
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import kotlinx.android.synthetic.main.main_activity.*
import softcom.com.dynamicapp.data.Data
import softcom.com.dynamicapp.data.Element
import softcom.com.dynamicapp.data.Page
import softcom.com.dynamicapp.util.GlideApp
import softcom.com.dynamicapp.util.convertDp2Px
import softcom.com.dynamicapp.util.formatNumber
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var data: Data
    private var pageNumber: Int = 1
    private var name: String = ""
    private var page: Page = Page()
    private var last: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            getDataAndShow(loadJSONFromAsset())
        } else {
            onRestoreInstanceState(savedInstanceState)
            updateContent(name, page, last)
        }
    }

    private fun loadJSONFromAsset(): String {
        return try {
            val  inputStream = resources.openRawResource(R.raw.pet_adoption)
            inputStream.bufferedReader().use{it.readText()}
        } catch (ex: Exception) {
            ex.printStackTrace()
            ""
        }
    }


    private fun getDataAndShow(json: String) {
        val gson = Gson()
        data = gson.fromJson(json, Data::class.java)
        if (data.pages.isNotEmpty()) {
            errorView.visibility = View.GONE
            updateContent(data.name, data.pages[0], pageNumber == data.pages.size)
            Log.e(TAG, data.toString())
        } else {
            errorView.visibility = View.VISIBLE
        }
    }

    private fun updateContent(name: String, page: Page, last: Boolean) {
        this.page = page
        this.name = name
        this.last = last
        toolbar?.title = name
        content?.removeAllViews()
        this.last = last
        for (section in page.sections) {
            addHeader(section.label)
            for (element in section.elements) {
                when (element.type) {
                    "embeddedphoto" -> {
                        addImage(element.file, element.unique_id)
                    }
                    "yesno" -> {
                        addYesNo(element)
                    }
                    else -> {
                        addText(element)
                    }
                }
            }
        }
        if (last) {
            addButton(last = true)
        } else {
            addButton(last = false)
        }
    }

    private fun addHeader(head: String) {
        val headerView = TextView(this@MainActivity)
        headerView.text = head
        headerView.textSize = 15f
        headerView.typeface = ResourcesCompat.getFont(this@MainActivity, R.font.montserrat_medium)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(16, 16, 16, 8)
        headerView.layoutParams = layoutParams
        content?.addView(headerView)
    }

    private fun addImage(url: String, id: String) {
        val imageView = ImageView(this@MainActivity)
        val layoutParams = LinearLayout.LayoutParams(
            convertDp2Px(this@MainActivity, 96F), convertDp2Px(this@MainActivity, 96F)
        )
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL
        layoutParams.setMargins(16, 8, 16, 8)
        imageView.layoutParams = layoutParams
        GlideApp.with(this@MainActivity)
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e(TAG, "loading image failed: $e")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageView.setImageDrawable(resource)
                    Log.e(TAG, "image loaded from $dataSource")
                    return true
                }

            }).into(imageView)
        imageView.tag = id
        content?.addView(imageView)

    }

    private fun addText(element: Element) {
        val layout = TextInputLayout(this@MainActivity)
        val view = TextInputEditText(this@MainActivity)
        view.hint = element.label
        view.textSize = 13f
        layout.tag = element.unique_id
        when (element.type) {
            "formattednumeric" -> {
                when (element.keyboard) {
                    "numeric" -> {
                        view.inputType = InputType.TYPE_CLASS_NUMBER
                        view.addTextChangedListener(object : TextWatcher {
                            @RequiresApi(Build.VERSION_CODES.N)
                            override fun afterTextChanged(s: Editable?) {
                                formatNumber(element.formattedNumeric, s.toString().toInt())
                            }

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                            }

                        })
                    }
                }
            }
            "datetime" -> {
                view.inputType = InputType.TYPE_CLASS_DATETIME
                view.isFocusable = false
                view.isClickable = true
                view.setOnClickListener {
                    pickDate(view)
                }
            }
        }
        val childParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        view.layoutParams = childParams
        layout.addView(view)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(16, 8, 16, 8)
        layout.layoutParams = layoutParams
        content?.addView(layout)
    }

    private fun addYesNo(element: Element) {
        val spinner = Spinner(this@MainActivity)
        val options = arrayOf("Yes", "No")
        val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, options)
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
                                            for (i in 0 until content.childCount) {
                                                val v = content.getChildAt(i)
                                                if (rule.targets.contains(v.tag))
                                                    v.visibility = View.VISIBLE
                                            }
                                        }
                                        "hide" -> {
                                            for (i in 0 until content.childCount) {
                                                val v = content.getChildAt(i)
                                                if (rule.targets.contains(v.tag))
                                                    v.visibility = View.GONE
                                            }
                                        }
                                    }
                                } else {
                                    when (rule.otherwise) {
                                        "show" -> {
                                            for (i in 0 until content.childCount) {
                                                val v = content.getChildAt(i)
                                                if (rule.targets.contains(v.tag))
                                                    v.visibility = View.VISIBLE
                                            }
                                        }
                                        "hide" -> {
                                            for (i in 0 until content.childCount) {
                                                val v = content.getChildAt(i)
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
        layoutParams.setMargins(16, 8, 16, 8)
        spinner.layoutParams = layoutParams
        content?.addView(spinner)
    }

    private fun addButton(last: Boolean) {
        val button = Button(this@MainActivity)
        button.isAllCaps = false
        button.background = getDrawable(R.drawable.button_background)
        if (last) {
            button.text = ("Submit")
            button.setOnClickListener {
                onCompleteClicked()
            }
        } else {
            button.text = ("Next Page")
            button.setOnClickListener {
                onNextClicked()
            }
        }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(16, 20, 16, 8)
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL
        button.layoutParams = layoutParams
        content?.addView(button)
    }

    private fun pickDate(view: View) {
        val calendar = Calendar.getInstance()
        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            (view as TextInputEditText).setText(sdf.format(calendar.time))
        }
        DatePickerDialog(
            this@MainActivity, datePickerListener, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun onNextClicked() {
        if (pageNumber < data.pages.size) {
            updateContent(data.name, data.pages[pageNumber++], pageNumber == data.pages.size)
        }
    }

    private fun onCompleteClicked() {

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("name", name)
        outState?.putParcelable("page", page)
        outState?.putBoolean("last", last)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        this.name = savedInstanceState?.getString("name")!!
        this.page = savedInstanceState.getParcelable("page")!!
        this.last = savedInstanceState.getBoolean("last")
    }


    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

}
