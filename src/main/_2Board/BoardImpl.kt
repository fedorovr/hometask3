package _2Board

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
