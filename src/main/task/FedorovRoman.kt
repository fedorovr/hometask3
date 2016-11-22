package task

import _2Board.Cell
import _2Board.Direction
import _2Board.GameBoard
import _2Board.SquareBoard
import java.time.LocalDate

// 1. Решение для MyDate. Скопировать ниже содержание файла MyDate.kt после того, как прошли все тесты.
// package _1Dates

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


// 2. Решение для GameBoard. Скопировать ниже содержание файла BoardImpl.kt после того, как прошли все тесты.
//package _2Board

data class CellImpl(override val i: Int, override val j: Int) : Cell {}

open class SquareBoardImpl(override val width: Int) : SquareBoard {
    override fun getCell(i: Int, j: Int): Cell =
            if (0 < i && i <= width && 0 < j && j <= width) CellImpl(i, j) else throw IllegalArgumentException()

    override fun getCellOrNull(i: Int, j: Int): Cell? =
            try {
                getCell(i, j)
            } catch (e: IllegalArgumentException) {
                null
            }

    override fun getAllCells(): Collection<Cell> =
            IntRange(1, width).map { r -> IntRange(1, width).map { CellImpl(r, it) } }.flatMap { it.asIterable() }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> =
            jRange.map { CellImpl(i, it) }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> =
            iRange.map { CellImpl(it, j) }

    override fun Cell.getNeighbour(direction: Direction): Cell? =
            getCellOrNull(this.i + direction.toInt().first, this.j + direction.toInt().second)

    fun Direction.toInt(): Pair<Int, Int> =
            when {
                this == Direction.UP -> Pair(-1, 0)
                this == Direction.LEFT -> Pair(0, -1)
                this == Direction.RIGHT -> Pair(0, 1)
                this == Direction.DOWN -> Pair(1, 0)
                else -> throw IllegalArgumentException()
            }
}

class GameBoardImpl<T>(override val width: Int) : GameBoard<T>, SquareBoardImpl(width) {
    val container: MutableMap<Cell, T?> = mutableMapOf()

    init {
        getAllCells().forEach { container[it] = null }
    }

    // call getCell to check is cell inside board and get instance of class that overrides equals right way
    override fun get(cell: Cell): T? = container[getCell(cell.i, cell.j)]

    override fun set(cell: Cell, value: T?) {
        container[getCell(cell.i, cell.j)] = value
    }

    override fun get(i: Int, j: Int): T? = container[getCell(i, j)]

    override fun set(i: Int, j: Int, value: T?) {
        container[getCell(i, j)] = value
    }

    override fun contains(value: T): Boolean = value in container.values

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> =
            container.filter { predicate(it.value) }.map { it.key }

    override fun any(predicate: (T?) -> Boolean): Boolean = container.any { predicate(it.value) }

    override fun all(predicate: (T?) -> Boolean): Boolean = container.all { predicate(it.value) }
}

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(width)


// I don't like red highlighting so:
fun MyDate.nextDay() = addTimeIntervals(TimeInterval.DAY, 1)

fun MyDate.addTimeIntervals(timeInterval: TimeInterval, number: Int): MyDate {
    val localDate = LocalDate.of(year, month, dayOfMonth)
    val newDate = when (timeInterval) {
        TimeInterval.DAY -> localDate.plusDays(number.toLong())
        TimeInterval.WEEK -> localDate.plusWeeks(number.toLong())
        TimeInterval.YEAR -> localDate.plusYears(number.toLong())
    }
    return MyDate(newDate.year, newDate.monthValue, newDate.dayOfMonth)
}
