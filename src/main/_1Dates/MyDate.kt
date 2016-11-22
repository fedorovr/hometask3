package _1Dates

data class MyDate(val year: Int, val month: Int, val dayOfMonth: Int) : Comparable<MyDate> {
    override fun compareTo(other: MyDate): Int =
            (year - other.year) * 10000 + (month - other.month) * 100 + (dayOfMonth - other.dayOfMonth)
}

class DateRange(override val start: MyDate, override val endInclusive: MyDate) : ClosedRange<MyDate>, Iterable<MyDate> {
    override fun iterator(): Iterator<MyDate> = DateIterator(this)
    override operator fun contains(value: MyDate): Boolean = start <= value && value <= endInclusive
}

class DateIterator(val rangeDate: DateRange) : Iterator<MyDate> {
    var currentDate: MyDate = rangeDate.start
    override fun hasNext(): Boolean = currentDate <= rangeDate.endInclusive
    override fun next(): MyDate {
        val current = currentDate
        currentDate = currentDate.nextDay()
        return current
    }
}

data class NTimeInterval(val timeInterval: TimeInterval, val n: Int)

operator fun MyDate.rangeTo(other: MyDate) = DateRange(this, other)
operator fun MyDate.plus(timeInterval: TimeInterval) = addTimeIntervals(timeInterval, 1)
operator fun MyDate.plus(nTimeInterval: NTimeInterval) = addTimeIntervals(nTimeInterval.timeInterval, nTimeInterval.n)

enum class TimeInterval {
    DAY,
    WEEK,
    YEAR
}

operator fun TimeInterval.times(n: Int) = NTimeInterval(this, n)
