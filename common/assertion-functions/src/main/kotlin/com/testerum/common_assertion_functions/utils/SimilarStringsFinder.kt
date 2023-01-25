package com.testerum.common_assertion_functions.utils

import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.similarity.LevenshteinDistance
import java.util.*

private val MAX_DIFFERENCE_PERCENTAGE = 50

fun didYouMean(desiredString: String,
               existingStrings: Iterable<String>): String {
    val similarStrings: List<String> = existingStrings.findSimilarStrings(desiredString)

    if (similarStrings.isEmpty()) {
        return ""
    } else if (similarStrings.size == 1) {
        return "; did you mean [${similarStrings[0]}]?"
    } else {
        return "; did you mean one of $similarStrings?"
    }
}

fun Iterable<String>.findSimilarStrings(desiredString: String): List<String> {
    var minDistance = Integer.MAX_VALUE
    var minDistanceStrings = TreeSet<String>()

    for (existingString in this) {
        val distance = calculateStringDistance(desiredString, existingString, MAX_DIFFERENCE_PERCENTAGE)

        if (distance == -1) {
            continue
        }
        
        if (distance == minDistance) {
            minDistanceStrings.add(existingString)
        } else if (distance < minDistance) {
            minDistance = distance
            minDistanceStrings = TreeSet<String>().apply {
                add(existingString)
            }
        }
    }

    return minDistanceStrings.toList()
}

/**
 * @return distance between the two strings, or -1 if the strings are unacceptably different
 */
private fun calculateStringDistance(source: String,
                                    target: String,
                                    minimumSimilarityPercentage: Int): Int {
    val maximumAllowedDistance: Int = calculateMaximumAllowedDistance(source, target, minimumSimilarityPercentage)

    return LevenshteinDistance(maximumAllowedDistance)
        .apply(
            target.lowercase(),
            source.lowercase(),
        )
}

private fun calculateMaximumAllowedDistance(source: String,
                                            target: String,
                                            minimumSimilarityPercentage: Int): Int {
    val lengthOfLongestString = Math.max(source.length, target.length)

    return lengthOfLongestString * (100 - minimumSimilarityPercentage) / 100

}
