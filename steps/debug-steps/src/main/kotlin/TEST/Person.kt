package TEST

import java.util.*


data class Company (
        var name: String?,
        var address: Address?,
        var departmentEmployees: Map<String, Number> = emptyMap(),
        var employees: MutableList<Person> = mutableListOf<Person>()
)

data class Person (
    var name: String?,
    var age: Int?,
    var sex: Sex?,
    var isRightHanded: Boolean?,
    var birthDate: Date?,
    var address: Address?
)

data class Address (
    var street: String?
)

enum class Sex {
    MAN,
    WOMAN,
    UNCLEAR
}