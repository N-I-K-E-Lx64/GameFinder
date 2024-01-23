package de.hive.gamefinder.core.utils

fun levenshteinSimilarity(str1: String, str2: String): Double {
    val dp = Array(str1.length + 1) { IntArray(str2.length + 1) }

    for (i in 0..str1.length) {
        for (j in 0..str2.length) {
            when {
                i == 0 -> dp[i][j] = j
                j == 0 -> dp[i][j] = i
                else -> dp[i][j] = minOf(
                    dp[i - 1][j - 1] + if (str1[i - 1] == str2[j - 1]) 0 else 1,
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1
                )
            }
        }
    }

    val levenshteinDistance = dp[str1.length][str2.length]
    return 1 - (levenshteinDistance.toDouble() / maxOf(str1.length, str2.length))
}