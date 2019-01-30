package json.utils.map_dsl

import java.util.TreeMap

class MapDsl {

    private val map = TreeMap<String, Any?>()

    operator fun String.rem(body: MapDsl.() -> Unit): Int {
        val builder = MapDsl()

        builder.body()

        map[this] = builder.build()

        return 0
    }

    operator fun String.rem(value: Any?): Int {
        map[this] = value
        return 0
    }

    fun build() = map

}

fun createMap(body: MapDsl.() -> Unit): Map<String, Any?> {
    val builder = MapDsl()

    builder.body()

    return builder.build()
}

fun main() {

    println(
            createMap {
                "company" % {
                    "driver" % {
                        "name" % "John"
                    }
                }
            }
    )

}
