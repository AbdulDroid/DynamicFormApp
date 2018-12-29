package softcom.com.dynamicapp.data

data class Response(var id: String = "",
                    var name: String = "",
                    var pages: List<Page> = ArrayList())

data class Page(var label: String = "",
                var sections: List<Section> = ArrayList())

data class Section(var label: String = "",
                   var elements: List<Element> = ArrayList())

data class Element(var type: String = "",
                   var file: String = "",
                   var unique_id: String = "",
                   var label: String = "",
                   var isMandatory: Boolean = true,
                   var keyboard: String = "",
                   var formattedNumeric: String = "",
                   var mode: String = "",
                   var rules: List<Rule> = ArrayList())

data class Rule(var condition: String = "",
                var value: String = "",
                var action: String = "",
                var otherwise: String = "",
                var targets: List<String> = ArrayList())