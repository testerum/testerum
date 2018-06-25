package com.testerum.common_spring.lists_merger

import lombok.NonNull
import org.springframework.beans.factory.FactoryBean
import java.util.*

class ListMergerFactoryBean<T> constructor(@NonNull vararg lists: List<T>) : FactoryBean<List<T>> {

    @NonNull
    private val mergedLists: List<T> = mergeLists(*lists)

    @NonNull
    private fun mergeLists(@NonNull vararg lists: List<T>): List<T> {
        val result = ArrayList<T>()

        for (list in lists) {
            result.addAll(list)
        }

        return result
    }

    override fun isSingleton(): Boolean {
        return true
    }

    override fun getObject(): List<T> {
        return mergedLists
    }

    override fun getObjectType(): Class<*> {
        return List::class.java
    }

}
