package TEST

data class Person (
    var name: String?,
    var age: Int?,
    var sex: Sex?,
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