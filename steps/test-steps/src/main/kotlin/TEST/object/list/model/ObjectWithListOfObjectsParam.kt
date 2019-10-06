package TEST.`object`.list.model

import java.util.*

data class ObjectWithListOfObjectsParam(val list: List<SimpleObject>)

data class SimpleObject(val name: String,
                        val date: Date)