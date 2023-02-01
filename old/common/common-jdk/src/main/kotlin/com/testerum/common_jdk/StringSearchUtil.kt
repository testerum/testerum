package com.testerum.common_jdk

private val TOKEN_SEPARATORS: Array<String> =  arrayOf(" ","\n",",",".",":",";","/","\"","'","#","{","}","[","]","<",">","(",")","*","&","%","|","?","!")

fun String?.containsSearchStringParts(searchedString: String?): Boolean {
    if(this.isNullOrBlank()) return false
    if(searchedString.isNullOrBlank()) return true

    val lowerMainText = this.toLowerCase()
    val mainTextTokens = lowerMainText.split(*TOKEN_SEPARATORS)

    val lowerSearchedString = searchedString.toLowerCase()
    val stringParts = lowerSearchedString.split(*TOKEN_SEPARATORS)

    var isEverySearchedPartFound = true
    for (stringPart in stringParts) {
        if (mainTextTokens.contains(stringPart)) {
            continue
        }

        isEverySearchedPartFound = false
        break
    }

    return isEverySearchedPartFound
}
