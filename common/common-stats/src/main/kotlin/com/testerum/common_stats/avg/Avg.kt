package com.testerum.common_stats.avg

import com.testerum.common_stats.Reducible

data class Avg(val sum: Long, val count: Long) : Reducible<Avg> {

    override fun reduce(other: Avg): Avg {
        return Avg(
                sum = this.sum + other.sum,
                count = this.count + other.count
        )
    }

}
