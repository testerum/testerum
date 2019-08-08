package json_support.utils

import com.testerum.model.expressions.json.util.MapMerger
import json_support.utils.map_dsl.createMap
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.util.*
import org.hamcrest.Matchers.`is` as Is

class MapMergerTest {

    // todo: add more tests

    @Test
    fun test() {
        assertThat<LinkedHashMap<String, Any?>>(
                MapMerger.override(
                        base = createMap {
                            "company" % {
                                "driver" % {
                                    "name" % "Alex"
                                    "job" % "truck driver"
                                }
                                "manager" % {
                                    "name" % "Bob"
                                    "age" % 41
                                }
                            }
                        },
                        overrides = createMap {
                            "company" % {
                                "driver" % {
                                    "name" % "Alex"
                                    "age" % 35
                                }
                                "manager" % "Bob"
                            }
                        }
                ),
                Is(equalTo(
                        createMap {
                            "company" % {
                                "driver" % {
                                    "age" % 35
                                    "job" % "truck driver"
                                    "name" % "Alex"
                                }
                                "manager" % "Bob"
                            }
                        }
                ))
        )
    }

}
