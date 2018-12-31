package softcom.com.dynamicapp

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.gson.Gson
import kotlinx.android.synthetic.main.main_activity.*
import softcom.com.dynamicapp.data.Data
import softcom.com.dynamicapp.data.Page
import softcom.com.dynamicapp.data.Section
import softcom.com.dynamicapp.util.addButtons
import softcom.com.dynamicapp.util.addHeader
import softcom.com.dynamicapp.util.addViews

class MainActivity : AppCompatActivity() {

    private lateinit var data: Data
    private var pageNumber: Int = 0
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
        restart.setOnClickListener {
            invalidateData()
            updateContent(data.name, data.pages[0], false)
            restart.visibility = View.GONE
            errorView.visibility = View.GONE
            pageCount.visibility = View.VISIBLE
            content.visibility = View.VISIBLE
        }
    }

    private fun loadJSONFromAsset(): String {
        return try {
            val inputStream = resources.openRawResource(R.raw.pet_adoption)
            inputStream.bufferedReader().use { it.readText() }
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
            updateContent(data.name, data.pages[pageNumber], pageNumber == data.pages.size-1)
            Log.e(TAG, data.toString())
        } else {
            errorView.visibility = View.VISIBLE
        }
    }

    private fun updateContent(name: String, page: Page, last: Boolean) {
        pageCount.text = String.format(getString(R.string.page_count), pageNumber+1, data.pages.size)
        this.page = page
        this.name = name
        this.last = last
        toolbar?.title = name
        content?.removeAllViews()
        this.last = last
        for (section in page.sections) {
            addHeader(this@MainActivity, section.label, content)
            addViews(this@MainActivity, section, content)
        }
        if (last) {
            addButtons(this@MainActivity, true, content, View.OnClickListener {
                onPrevClicked()
            }, View.OnClickListener {
                onCompleteClicked(page.sections)
            })
        } else {
            addButtons(this@MainActivity, false, content, View.OnClickListener {
                onPrevClicked()
            }, View.OnClickListener {
                onNextClicked(page.sections)
            })
        }
    }

    private fun onNextClicked(sections: List<Section>) {
        var checker = false
        for (section in sections) {
            for (element in section.elements) {
                for (i in 0 until content.childCount) {
                    val view = content.getChildAt(i)
                    if (view.tag == element.unique_id && element.isMandatory) {
                        if (view is TextInputLayout) {
                            if (element.value.isEmpty()) {
                                view.isErrorEnabled = true
                                view.error = ("This field is mandatory")
                                view.editText?.requestFocus()
                                checker = true
                            } else {
                                view.error = null
                                view.isErrorEnabled = false
                            }
                        }
                    }
                }
            }
        }
        if (pageNumber < data.pages.size && !checker) {
            pageNumber += 1
            if (pageNumber > data.pages.size - 1) {
                pageNumber = data.pages.size - 1
            }
            updateContent(data.name, data.pages[pageNumber], pageNumber == data.pages.size-1)
        }
    }

    private fun onPrevClicked() {
        if (pageNumber > 0 && (data.pages.indexOf(page) != 0)) {
            pageNumber -= 1
            if (pageNumber >= 0)
                updateContent(data.name, data.pages[pageNumber], pageNumber == data.pages.size-1)
            else {
                pageNumber = 0
                updateContent(data.name, data.pages[pageNumber], pageNumber == data.pages.size-1)
            }
        }
    }

    private fun onCompleteClicked(sections: List<Section>) {
        var checker = false
        for (section in sections) {
            for (element in section.elements) {

                for (i in 0 until content.childCount) {
                    val view = content.getChildAt(i)
                    if (view.tag == element.unique_id && element.isMandatory) {
                        if (view is TextInputLayout) {
                            if (element.value.isEmpty()) {
                                view.isErrorEnabled = true
                                view.error = ("This field is mandatory")
                                view.editText?.requestFocus()
                                checker = true
                            } else {
                                view.error = null
                                view.isErrorEnabled = false
                            }
                        }
                    }
                }
            }
        }

        if (!checker) {
            errorView.text = ("${data.name} completed successfully")
            pageNumber = 0
            page = data.pages[pageNumber]
            content.visibility = View.GONE
            pageCount.visibility = View.GONE
            errorView.visibility = View.VISIBLE
            restart.visibility = View.VISIBLE
        }
    }

    private fun invalidateData() {
        for (page in data.pages) {
            for (section in page.sections) {
                for (element in section.elements) {
                    element.value = ""
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable("data", data)
        outState?.putString("name", name)
        outState?.putParcelable("page", page)
        outState?.putBoolean("last", last)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        this.data = savedInstanceState?.getParcelable("data")!!
        this.name = savedInstanceState.getString("name")!!
        this.page = savedInstanceState.getParcelable("page")!!
        this.last = savedInstanceState.getBoolean("last")
    }


    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

}
