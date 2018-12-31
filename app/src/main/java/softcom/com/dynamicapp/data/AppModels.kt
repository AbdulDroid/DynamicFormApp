package softcom.com.dynamicapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(var id: String = "",
                    var name: String = "",
                    var pages: List<Page> = ArrayList()): Parcelable

@Parcelize
data class Page(var label: String = "",
                var sections: ArrayList<Section> = ArrayList()): Parcelable
@Parcelize
data class Section(var label: String = "",
                   var elements: List<Element> = ArrayList()): Parcelable
@Parcelize
data class Element(var type: String = "",
                   var file: String = "",
                   var unique_id: String = "",
                   var label: String = "",
                   var isMandatory: Boolean = true,
                   var keyboard: String = "",
                   var value: String = "",
                   var formattedNumeric: String = "",
                   var mode: String = "",
                   var rules: List<Rule> = ArrayList()): Parcelable
@Parcelize
data class Rule(var condition: String = "",
                var value: String = "",
                var action: String = "",
                var otherwise: String = "",
                var targets: List<String> = ArrayList()): Parcelable