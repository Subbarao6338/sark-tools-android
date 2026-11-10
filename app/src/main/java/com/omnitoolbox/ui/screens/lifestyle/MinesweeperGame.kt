package omni.toolbox.ui.screens.lifestyle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun MinesweeperGame() {
    val size = 8
    val mineCount = 10
    var board by remember { mutableStateOf(generateMinesweeper(size, mineCount)) }
    var revealed by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }
    var flagged by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }
    var gameOver by remember { mutableStateOf(false) }
    var gameWon by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Minesweeper", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.border(2.dp, Color.Black)) {
            Column {
                for (r in 0 until size) {
                    Row {
                        for (c in 0 until size) {
                            val pos = r to c
                            val isRevealed = revealed.contains(pos)
                            val isFlagged = flagged.contains(pos)
                            val value = board[r][c]

                            Box(
                                modifier = Modifier
                                    .size(35.dp)
                                    .background(if (isRevealed) Color.LightGray else Color.Gray)
                                    .border(0.5.dp, Color.DarkGray)
                                    .clickable(enabled = !gameOver && !gameWon) {
                                        if (!isFlagged) {
                                            if (value == -1) {
                                                gameOver = true
                                                revealed = revealed + (0 until size).flatMap { row -> (0 until size).map { col -> row to col } }
                                            } else {
                                                revealed = revealMinesweeper(board, revealed, r, c, size)
                                                if (revealed.size == size * size - mineCount) {
                                                    gameWon = true
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isRevealed) {
                                    if (value == -1) Text("💣")
                                    else if (value > 0) Text(value.toString(), fontWeight = FontWeight.Bold, color = when(value) {
                                        1 -> Color.Blue
                                        2 -> Color(0xFF388E3C)
                                        3 -> Color.Red
                                        else -> Color(0xFF7B1FA2)
                                    })
                                } else if (isFlagged) {
                                    Text("🚩")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (gameOver || gameWon) {
            Text(if (gameWon) "YOU WIN!" else "GAME OVER",
                style = MaterialTheme.typography.headlineMedium,
                color = if (gameWon) Color(0xFF388E3C) else Color.Red,
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = {
                board = generateMinesweeper(size, mineCount)
                revealed = emptySet()
                flagged = emptySet()
                gameOver = false
                gameWon = false
            }) { Text("Restart") }
        } else {
            Text("Mines: $mineCount | Flagged: ${flagged.size}", modifier = Modifier.padding(16.dp))
        }
    }
}

fun generateMinesweeper(size: Int, mineCount: Int): Array<IntArray> {
    val board = Array(size) { IntArray(size) }
    var placed = 0
    while (placed < mineCount) {
        val r = Random.nextInt(size)
        val c = Random.nextInt(size)
        if (board[r][c] != -1) {
            board[r][c] = -1
            placed++
        }
    }
    for (r in 0 until size) {
        for (c in 0 until size) {
            if (board[r][c] == -1) continue
            var count = 0
            for (dr in -1..1) {
                for (dc in -1..1) {
                    val nr = r + dr
                    val nc = c + dc
                    if (nr in 0 until size && nc in 0 until size && board[nr][nc] == -1) count++
                }
            }
            board[r][c] = count
        }
    }
    return board
}

fun revealMinesweeper(board: Array<IntArray>, revealed: Set<Pair<Int, Int>>, r: Int, c: Int, size: Int): Set<Pair<Int, Int>> {
    if (r !in 0 until size || c !in 0 until size || revealed.contains(r to c)) return revealed
    var newRevealed = revealed + (r to c)
    if (board[r][c] == 0) {
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                newRevealed = revealMinesweeper(board, newRevealed, r + dr, c + dc, size)
            }
        }
    }
    return newRevealed
}
