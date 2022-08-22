package chess

fun main() {
    println(" Pawns-Only Chess")
    println("First Player's name:")
    val firstName = readln()
    println("Second Player's name:")
    val secondName = readln()
    val boardObj = Board()

    var turn = 0
    var currTurn: String
    var stop = false
    var pMove = ""
    val validMoveRegex = Regex("[a-h][1-8][a-h][1-8]")

    var initPos = ""
    var toPos = ""
    var invalidInput = false

    while (!stop) {
        if (!invalidInput) boardObj.printBoard()

        if (boardObj.isGameOver()) break

        invalidInput = false
        currTurn = if (turn % 2 == 0) "W" else "B"

        if (!boardObj.hasValidMoves(currTurn)) {
            println("Stalemate!")
            break
        }

        if (currTurn == "W") {
            println("$firstName's turn:")
        } else {
            println("$secondName's turn:")
        }

        pMove = readln()
        if (pMove == "exit") {
            stop = true
        } else if (!validMoveRegex.matches(pMove)) {
            println("Invalid Input")
            invalidInput = true
        } else {
            initPos = pMove.substring(0, 2)
            toPos = pMove.substring(2, 4)

            if (!validStartPawn(initPos, currTurn, boardObj)) {
                if (currTurn == "W") {
                    println("No white pawn at $initPos")
                } else {
                    println("No black pawn at $initPos")
                }
                invalidInput = true
            } else if (!validEndPawn(initPos, toPos, currTurn, boardObj)) {
                println("Invalid Input")
                invalidInput = true
            } else {
                boardObj.lastPosList.add(toPos)
                turn++
            }
        }
    }
    println("Bye!")
}

fun validStartPawn(startingPos: String, pawnColor: String, board: Board): Boolean {
    val x = translateX(startingPos)
    val y = translateY(startingPos)

    return board.getBoardElem(x, y) == pawnColor
}

fun validEndPawn(startingPos: String, endPos: String, pawnColor: String, board: Board): Boolean {
    val x = translateX(startingPos)
    val y = translateY(startingPos)
    val toX = translateX(endPos)
    val toY = translateY(endPos)
    val lastX = translateX(board.lastPosList[board.lastPosList.lastIndex])
    val lastY = translateY(board.lastPosList[board.lastPosList.lastIndex])

    if (toX < 0 || toX > 7) return false
    if (toY < 0 || toY > 7) return false

    if (pawnColor == "W") {
        val yMovement = y - toY
        val xMovement = x - toX

        if (y == 6 && yMovement == 2 && x == toX && board.updateBoard(
                x,
                y,
                toX,
                toY
            )
        ) { //move 2 forward from only from start (y == 6)
            board.firstMoveList.add(true)
            return true
        } else if (yMovement == 1 && x == toX && board.updateBoard(x, y, toX, toY)) { //move 1 forward
            board.firstMoveList.add(false)
            return true
        } else if (yMovement == 1
            && (xMovement == 1 || xMovement == -1)
            && board.getBoardElem(toX, toY) == "B"
        ) { //capture (diagonal move with "b" at end position)
            board.firstMoveList.add(false)
            return board.capture(x, y, toX, toY, pawnColor, lastX, lastY, false)
        } else if (yMovement == 1
            && y == 3
            && (xMovement == 1 || xMovement == -1)
            && board.firstMoveList[board.firstMoveList.lastIndex]
            && board.getBoardElem(toX, toY) == " "
            && (lastX - x == 1 || lastX - x == -1)
            && board.getBoardElem(lastX, lastY) == "B"
        ) { //en passant only on 5th (y == 3) with B next to it
            board.firstMoveList.add(false)
            return board.capture(x, y, toX, toY, pawnColor, lastX, lastY, true)
        } else {
            return false
        }
    } else if (pawnColor == "B") {
        val yMovement = y - toY
        val xMovement = x - toX
        if (y == 1
            && yMovement == -2
            && x == toX
            && board.updateBoard(x, y, toX, toY)
        ) { //move 2 forward from only from start (y == 1)
            board.firstMoveList.add(true)
            return true
        } else if (yMovement == -1
            && x == toX
            && board.updateBoard(x, y, toX, toY)
        ) { //move 1 forward
            board.firstMoveList.add(false)
            return true
        } else if (yMovement == -1
            && (xMovement == 1 || xMovement == -1)
            && board.getBoardElem(toX, toY) == "W"
        ) { //capture (diagonal move with "w" at end position)
            board.firstMoveList.add(false)
            return board.capture(x, y, toX, toY, pawnColor, lastX, lastY, false)
        } else if (yMovement == -1
            && y == 4
            && (xMovement == 1 || xMovement == -1)
            && board.firstMoveList[board.firstMoveList.lastIndex]
            && board.getBoardElem(toX, toY) == " "
            && (lastX - x == 1 || lastX - x == -1)
            && board.getBoardElem(lastX, lastY) == "W"
        ) { //en passant only on 4th (y == 4) with "w" next to it
            board.firstMoveList.add(false)
            return board.capture(x, y, toX, toY, pawnColor, lastX, lastY, true)
        } else {
            return false
        }
    } else {
        return false
    }
}

fun translateX(move: String): Int {
    when (move.substring(0, 1)) {
        "a" -> {
            return 0
        }

        "b" -> {
            return 1
        }

        "c" -> {
            return 2
        }

        "d" -> {
            return 3
        }

        "e" -> {
            return 4
        }

        "f" -> {
            return 5
        }

        "g" -> {
            return 6
        }

        else -> {
            return 7
        }
    }
}

fun translateY(move: String): Int {
    when (move.substring(1, 2)) {
        "8" -> {
            return 0
        }

        "7" -> {
            return 1
        }

        "6" -> {
            return 2
        }

        "5" -> {
            return 3
        }

        "4" -> {
            return 4
        }

        "3" -> {
            return 5
        }

        "2" -> {
            return 6
        }

        else -> {
            return 7
        }
    }
}

class Board(private var whitePieces: Int = 8, private var blackPieces: Int = 8) {

    private val boardList = mutableListOf(
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "),
        mutableListOf("B", "B", "B", "B", "B", "B", "B", "B"),
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "),
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "),
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "),
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "),
        mutableListOf("W", "W", "W", "W", "W", "W", "W", "W"),
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " ")
    )
    val firstMoveList = mutableListOf(false)
    val lastPosList = mutableListOf("  ")

    private fun removePiece(capture: String) {
        when (capture) {
            "B" -> whitePieces--
            "W" -> blackPieces--
        }
    }

    fun isGameOver(): Boolean {
        return if (whitePieces <= 0) {
            println("Black Wins!")
            true
        } else if (blackPieces <= 0) {
            println("White Wins!")
            true
        } else pawnAtOppositeRank()
    }

    private fun pawnAtOppositeRank(): Boolean {
        for (i in boardList[0].indices) {
            if (boardList[0][i] == "W") {
                println("White Wins!")
                return true
            }
            if (boardList[7][i] == "B") {
                println("Black Wins!")
                return true
            }
        }
        return false
    }

    fun printBoard() {
        var currLine: String
        val border = "  +---+---+---+---+---+---+---+---+"
        println(border)
        for (i in boardList.indices) {
            currLine = "${8 - i} "
            for (j in boardList[i].indices) {
                currLine += "| ${boardList[i][j]} "
            }
            currLine += "|"
            println(currLine)
            println(border)
        }
        println("    a   b   c   d   e   f   g   h")
    }

    fun getBoardElem(x: Int, y: Int): String {
        return boardList[y][x]
    }

    fun updateBoard(fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        return if (boardList[toY][toX] != " ") {
            false
        } else {
            val pawn = boardList[fromY][fromX]

            boardList[fromY][fromX] = boardList[toY][toX]
            boardList[toY][toX] = pawn
            true
        }
    }

    fun capture(
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int,
        currTurn: String,
        lastX: Int,
        lastY: Int,
        isEnPassant: Boolean
    ): Boolean {
        if (isEnPassant) {
            return if (updateBoard(fromX, fromY, toX, toY)) {
                boardList[lastY][lastX] = " "
                removePiece(currTurn)
                true
            } else {
                false
            }
        } else {
            return if (boardList[toY][toX] != " " && boardList[toY][toX] != currTurn) {
                boardList[toY][toX] = currTurn
                boardList[fromY][fromX] = " "
                removePiece(currTurn)
                true
            } else {
                false
            }
        }
    }

    fun hasValidMoves(currTurn: String): Boolean {
        var count = 0
        Loop@ for (y in boardList.indices) {
            if (y != 0 && y != boardList.lastIndex) { //omit 0 and 7 as those are winning rows
                for (x in boardList[y].indices) {
                    if (boardList[y][x] == currTurn) {
                        count++
                        if (y == 4 && x == 7 && count > 7 && checkIfHasValidMoves(x, y, currTurn)) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun checkIfHasValidMoves(x: Int, y: Int, currTurn: String): Boolean {
        return canMoveOneForward(x, y, currTurn) || canMoveTwoForward(x, y, currTurn) || canCapture(
            x,
            y,
            currTurn
        ) || canEnPassantCapture(x, y, currTurn)
    }

    fun canMoveOneForward(x: Int, y: Int, currTurn: String): Boolean {
        return if (currTurn == "W") {
            y in 1..6 && boardList[y - 1][x] == " "
        } else {
            y in 1..6 && boardList[y + 1][x] == " "
        }
    }

    fun canMoveTwoForward(x: Int, y: Int, currTurn: String): Boolean {
        return if (currTurn == "W") {
            y == 6 && boardList[y - 2][x] == " "
        } else {
            y == 2 && boardList[y + 2][x] == " "
        }
    }

    fun canCapture(x: Int, y: Int, currTurn: String, print: Boolean = false): Boolean {
        val yMovement = if (currTurn == "W") -1 else 1
        val captureTarget = if (currTurn == "W") "B" else "W"
        val leftCaptureX = x - 1
        val rightCaptureX = x + 1

        val canLeftCapture =
            leftCaptureX > 0 && leftCaptureX <= boardList[y + yMovement].lastIndex && boardList[y + yMovement][leftCaptureX] == captureTarget
        val canRightCapture =
            rightCaptureX > 0 && rightCaptureX <= boardList[y + yMovement].lastIndex && boardList[y + yMovement][rightCaptureX] == captureTarget

        return canLeftCapture || canRightCapture
    }

    fun canEnPassantCapture(x: Int, y: Int, currTurn: String): Boolean {
        if (!firstMoveList[firstMoveList.lastIndex]) {
            return false
        } else {
            val lastX = translateX(lastPosList[lastPosList.lastIndex])
            val lastY = translateY(lastPosList[lastPosList.lastIndex])
            val leftCaptureX = x - 1
            val rightCaptureX = x + 1


            if (currTurn == "W" && y == 3 && y == lastY && boardList[lastY][lastX] == "B" && (lastX == leftCaptureX || lastX == rightCaptureX)) {
                val canLeftCapture =
                    leftCaptureX > 0 && leftCaptureX <= boardList[y - 1].lastIndex && boardList[y - 1][leftCaptureX] == " "
                val canRightCapture =
                    rightCaptureX > 0 && rightCaptureX <= boardList[y - 1].lastIndex && boardList[y - 1][rightCaptureX] == " "
                return canLeftCapture || canRightCapture
            } else if (currTurn == "B" && y == 4 && y == lastY && boardList[lastY][lastX] == "W" && (lastX == leftCaptureX || lastX == rightCaptureX)) {
                val canLeftCapture =
                    leftCaptureX > 0 && leftCaptureX <= boardList[y + 1].lastIndex && boardList[y + 1][leftCaptureX] == " "
                val canRightCapture =
                    rightCaptureX > 0 && rightCaptureX <= boardList[y + 1].lastIndex && boardList[y + 1][rightCaptureX] == " "
                return canLeftCapture || canRightCapture
            } else {
                return false
            }
        }
    }
}