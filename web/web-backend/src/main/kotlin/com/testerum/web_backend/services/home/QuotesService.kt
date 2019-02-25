package com.testerum.web_backend.services.home

import com.testerum.common_kotlin.emptyToNull
import com.testerum.model.home.Quote
import org.apache.commons.io.IOUtils
import java.io.InputStream
import java.util.*

class QuotesService {

    companion object {
        private const val QUOTES_CLASSPATH_LOCATION = "home-quotes.txt"
        private const val TEXT_AUTHOR_SEPARATOR = "%%"
    }

    private val quotes: List<Quote> = loadQuotes()

    private val random = Random()

    private fun loadQuotes(): List<Quote> {
        val classLoader = HomeFrontendService::class.java.classLoader

        val inputStream: InputStream = classLoader.getResourceAsStream(QUOTES_CLASSPATH_LOCATION)
                ?: throw IllegalStateException("could not find classpath resource [$QUOTES_CLASSPATH_LOCATION]")

        val quotesContent = inputStream.use {
            IOUtils.toString(it, Charsets.UTF_8)
        }

        return quotesContent.lines()
                .filter { it.isNotBlank() }
                .map { parseLine(it) }
    }

    fun parseLine(line: String): Quote {
        val indexOfSeparator = line.indexOf(TEXT_AUTHOR_SEPARATOR)

        val text: String
        val author: String?
        if (indexOfSeparator == -1) {
            text = line
            author = null
        } else {
            text = line.substring(0, indexOfSeparator)
            author = line.substring(indexOfSeparator + TEXT_AUTHOR_SEPARATOR.length)
                    .emptyToNull()
        }

        return Quote(text = text, author = author)
    }

    fun getRandomQuote(): Quote {
        val randomIndex = random.nextInt(quotes.size)

        return quotes[randomIndex]
    }


}
