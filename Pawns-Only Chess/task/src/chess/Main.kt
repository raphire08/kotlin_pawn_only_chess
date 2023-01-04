package chess

const val SIZE = 8
const val VERTEX = "+"
const val ROW_SEP = "-"
const val COL_SEP = "|"
const val W_CHAR = "W"
const val B_CHAR = "B"
const val B_PASSANT_COL = 4
const val W_PASSANT_COL = 3
var b_passant_row = -1
var w_passant_row = -1

//Todo refactor this to user objects and classes
fun main() {
    val state = getState()
    println("Pawns-Only Chess")
    println("First Player's name: ")
    val fName = readln()
    println("Second Player's name: ")
    val sName = readln()
    printBoard(state)
    var flag = true
    var isFirst = true
    while(flag) {
        val currentPlayer = if (isFirst) fName else sName
        val move = handleMove(currentPlayer, isFirst, state)
        if(move == "") {
            flag = false
            println("Bye!")
        } else {
            isFirst = !isFirst
            printBoard(state)
            val status = getGameStatus(state)
            if(!status) {
                flag = false
            }
        }
    }
}

fun getState(): MutableList<MutableList<Int>> {
    val state = MutableList(SIZE) { MutableList(SIZE) { 0 } }
    for (i in 1..SIZE) {
        state[1][i - 1] = 1
        state[6][i - 1] = -1
    }
    return state
}

fun handleMove(
    name: String,
    isFirst: Boolean,
    state: MutableList<MutableList<Int>>,
    ): String {
        println("$name's turn:")
        val move = readln()
        if(move == "exit") {
            return ""
        }
        val status = checkMove(move, isFirst, state)
        return if(status == "") {
            move
        } else {
            println(status)
            handleMove(name, isFirst, state)
        }
}

/**
 * Check is move is straight or diagonal
 * For straight it can be one two jumps in vertical direction
 * For diagonal it has to be only one jump ahead in both the direction
 * For diagonal it can be En passant
 * */
fun checkMove(
    input: String,
    isFirst: Boolean,
    state: MutableList<MutableList<Int>>,
): String {
    val regex = Regex("[a-h][1-8][a-h][1-8]")
    val match = regex.matches(input)
    if (match) {
        val iStart = getIndexFromLetter(input[0])
        val jStart = (input[1]- 1).toString().toInt()
        val valAtStart = state[jStart][iStart]
        val iEnd = getIndexFromLetter(input[2])
        val jEnd = (input[3] - 1).toString().toInt()
        val valAtEnd = state[jEnd][iEnd]
        if(isFirst) {
            if(valAtStart != 1) {
               return "No white pawn at ${input[0]}${input[1]}"
            } else if(jEnd - jStart > 2 || jEnd-jStart <= 0 ) {
                return "Invalid Input"
            } else if(iStart - iEnd > 1 || iStart - iEnd < -1) {
                return "Invalid Input"
            } else if (iStart == iEnd) {
                return if (valAtEnd == -1) {
                    "Invalid Input"
                } else if (jEnd - jStart == 1) {
                    state[jStart][iStart] = 0
                    state[jEnd][iEnd] = 1
                    w_passant_row = -1
                    ""
                } else {
                    return if(jStart == 1) {
                        state[jStart][iStart] = 0
                        state[jEnd][iEnd] = 1
                        w_passant_row = iStart
                        ""
                    } else {
                        "Invalid Input"
                    }
                }
            } else {
                return if(jEnd - jStart == 1 && valAtEnd == -1) {
                    state[jStart][iStart] = 0
                    state[jEnd][iEnd] = 1
                    w_passant_row = -1
                    ""
                } else if (jEnd - jStart == 1 && valAtEnd == 0 && state[jStart][iEnd] == -1) {
                    return if(jStart == B_PASSANT_COL && iEnd == b_passant_row) {
                        state[jStart][iStart] = 0
                        state[jEnd][iEnd] = 1
                        state[jStart][iEnd] = 0
                        w_passant_row = -1
                         ""
                    } else {
                         "Invalid Input"
                    }
                } else {
                    "Invalid Input"
                }
            }
        } else {
            if(valAtStart != -1) {
                return "No black pawn at ${input[0]}${input[1]}"
            } else if(jStart - jEnd > 2 || jStart-jEnd <= 0) {
                return "Invalid Input"
            } else if(iStart - iEnd > 1 || iStart - iEnd < -1) {
                return "Invalid Input"
            } else if (iStart == iEnd) {
                return if (valAtEnd == 1) {
                     "Invalid Input"
                } else if (jStart - jEnd == 1) {
                    state[jStart][iStart] = 0
                    state[jEnd][iEnd] = -1
                    b_passant_row = -1
                    ""
                } else {
                    return if (jStart == 6) {
                        state[jStart][iStart] = 0
                        state[jEnd][iEnd] = -1
                        b_passant_row = iStart
                        ""
                    } else {
                        "Invalid Input"
                    }
                }
            } else {
                return if(jStart - jEnd == 1 && valAtEnd == 1) {
                    state[jStart][iStart] = 0
                    state[jEnd][iEnd] = -1
                    b_passant_row = -1
                    ""
                } else if (jStart - jEnd == 1 && valAtEnd == 0 && state[jStart][iEnd] == 1) {
                    return if(jStart == W_PASSANT_COL && iEnd == w_passant_row) {
                        state[jStart][iStart] = 0
                        state[jEnd][iEnd] = -1
                        state[jStart][iEnd] = 0
                        b_passant_row = -1
                        ""
                    } else {
                        "Invalid Input"
                    }
                } else {
                    "Invalid Input"
                }
            }
        }
    } else {
        return "Invalid Input"
    }
}

fun getIndexFromLetter(l: Char): Int {
    return when (l) {
        'a' -> 0
        'b' -> 1
        'c' -> 2
        'd' -> 3
        'e' -> 4
        'f' -> 5
        'g' -> 6
        'h' -> 7
        else -> -1
    }
}

fun printBoard(state: MutableList<MutableList<Int>>) {
    var board = ""
    for (i in 1..SIZE) {
        board += getRowBoundary()
        for (j in 1..SIZE) {
            board += if (j == 1) {
                "${SIZE+1-i} $COL_SEP "
            } else {
                "$COL_SEP "
            }
            board += if (state[SIZE - i][j-1] == 1) {
                W_CHAR
            } else if (state[SIZE - i][j-1] == -1) {
                B_CHAR
            } else {
                " "
            }
            board += if (j == 8) {
                " $COL_SEP"
            } else {
                " "
            }
        }
        board += "\n"
        if (i == 8) {
            board += getRowBoundary()
        }
    }
    board += getLetters()
    print(board)
}

fun getRowBoundary(): String {
    val unit = "$VERTEX${ROW_SEP.repeat(3)}"
    return "  ${unit.repeat(SIZE)}$VERTEX\n"
}

fun getLetters(): String {
    val edgeSpacing = "  "
    val inBetSpacing = "   "
    return "  ${edgeSpacing}a${inBetSpacing}b" +
            "${inBetSpacing}c${inBetSpacing}d" +
            "${inBetSpacing}e${inBetSpacing}f" +
            "${inBetSpacing}g${inBetSpacing}h\n"
}

fun getGameStatus(state: MutableList<MutableList<Int>>): Boolean {
    var bCount = 0
    var wCount = 0
    var canWhiteMove = false
    var canBlackMove = false
    for (j in 0 until SIZE) {
        for (i in 0 until SIZE) {
            val value = state[j][i]
            if(value == 1) {
                wCount++
                if(j == 7) {
                    printWinner("White")
                    return false
                }
                if(canWhiteMove) {
                    continue
                } else {
                    canWhiteMove = checkIfWhiteCanMove(state, i, j)
                }
            } else if(value == -1) {
                bCount++
                if(j == 0) {
                    printWinner("Black")
                    return false
                }
                if(canBlackMove) {
                    continue
                } else {
                    canBlackMove = checkIfBlackCanMove(state, i, j)
                }
            }
        }
    }
    if(bCount == 0) {
        printWinner("White")
        return false
    } else if(wCount == 0) {
        printWinner("Black")
        return false
    }
    if(!canWhiteMove || !canBlackMove) {
        printDraw()
        return false
    }
    return true
}

fun printWinner(winner: String) {
    println("$winner Wins!")
    println("Bye!")
}

fun printDraw() {
    println("Stalemate!")
    println("Bye!")
}

fun checkIfWhiteCanMove(state: MutableList<MutableList<Int>>, i: Int, j: Int): Boolean {
    if(j < 7) {
        if (state[j+1][i] == 0) {
            return true
        }
        if(i < 7) {
            if(state[j+1][i+1] == -1) {
                return true
            }
        }
        if(i > 0) {
            if(state[j+1][i-1] == -1) {
                return true
            }
        }
    }
    return false
}

fun checkIfBlackCanMove(state: MutableList<MutableList<Int>>, i: Int, j: Int): Boolean {
    if(j > 0) {
        if (state[j-1][i] == 0) {
            return true
        }
        if(i < 7) {
            if(state[j-1][i+1] == 1) {
                return true
            }
        }
        if(i > 0) {
            if(state[j-1][i-1] == 1) {
                return true
            }
        }
    }
    return false
}

