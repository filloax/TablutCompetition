package it.unibo.ai.didattica.competition.tablut.droptablut;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IHeuristic;

public class DropTablutHeuristic implements IHeuristic {
    private boolean isWhite;

    public DropTablutHeuristic(Turn color) {
        this.isWhite = color.equals(Turn.WHITE);
    }


    @Override
    public double heuristic(State state) {
        Turn turn = state.getTurn();
        if (turn.equals(Turn.BLACKWIN)) {
            if (isWhite) {
                return Double.NEGATIVE_INFINITY;
            } else {
                return Double.POSITIVE_INFINITY;
            }
        } else if (turn.equals(Turn.WHITEWIN)) {
            if (isWhite) {
                return Double.POSITIVE_INFINITY;
            } else {
                return Double.NEGATIVE_INFINITY;
            }
        // } else if (turn.equals(Turn.DRAW)) {
        //     if (isWhite) {
        //         return getWhiteScore(0, 16, 0, 8) * 0.75;
        //     } else {

        //     }
        }

        int numWhite = 0, numBlack = 0;
        int kingX = -1, kingY = -1;
        Pawn board[][] = state.getBoard();

        for (int y = 0; y < board.length; y++)
            for (int x = 0; x < board[0].length; x++) {
                // Si suppone che se non è uno stato di vittoria non arriva fino qua
                // quindi il re è vivo
                if (board[y][x] == Pawn.BLACK) {
                    numBlack++;
                } else if (board[y][x] == Pawn.WHITE) {
                    numWhite++;
                } else if (board[y][x] == Pawn.KING) {
                    kingX = x;
                    kingY = y;
                }
            }

        int numObstacles = 0;
        int numFreeDirections = 0;

        for (Direction dir : Direction.values()) {
            int thisNumObstacles = countKingObstacles(state, dir, kingX, kingY);
            numObstacles += thisNumObstacles;
            if (thisNumObstacles == 0) {
                numFreeDirections++;
            }
        }

        if (isWhite) {
            return getWhiteScore(numWhite, numBlack, numFreeDirections, numObstacles);
        } else {
            return getBlackScore(numWhite, numBlack, numFreeDirections, numObstacles);
        }
    }

    private double getWhiteScore(int numWhite, int numBlack, int numFreeDirections, int numObstacles) {
        return numBlack * -0.5 + numWhite      + numFreeDirections *  5 + numObstacles * -1;
    }

    private double getBlackScore(int numWhite, int numBlack, int numFreeDirections, int numObstacles) {
        return numBlack *  0.5 + numWhite * -1 + numFreeDirections * -5 + numObstacles;
    }

    private int countKingObstacles(State state, Direction dir, int kingX, int kingY) {
        Pawn board[][] = state.getBoard();
        int count = 0;

        if (dir == Direction.LEFT) {
            for (int x = kingX - 1; x >= 0; x--) {
                boolean isEmpty = board[kingY][x] == Pawn.EMPTY;
                // Controlla se è un accampamento
                if (isEmpty && GameConstants.citadels.contains(state.getBox(x, kingY))) {
                    isEmpty = false;
                }

                if (!isEmpty) {
                    count++;
                }
            }
        } else if (dir == Direction.RIGHT) {
            for (int x = kingX + 1; x < 9; x++) {
                boolean isEmpty = board[kingY][x] == Pawn.EMPTY;
                // Controlla se è un accampamento
                if (isEmpty && GameConstants.citadels.contains(state.getBox(x, kingY))) {
                    isEmpty = false;
                }

                if (!isEmpty) {
                    count++;
                }
            }
        } else if (dir == Direction.UP) {
            for (int y = kingY - 1; y >= 0; y--) {
                boolean isEmpty = board[y][kingX] == Pawn.EMPTY;
                // Controlla se è un accampamento
                if (isEmpty && GameConstants.citadels.contains(state.getBox(y, kingX))) {
                    isEmpty = false;
                }

                if (!isEmpty) {
                    count++;
                }
            }
        } else if (dir == Direction.DOWN) {
            for (int y = kingY + 1; y < 9; y++) {
                boolean isEmpty = board[y][kingX] == Pawn.EMPTY;
                // Controlla se è un accampamento
                if (isEmpty && GameConstants.citadels.contains(state.getBox(y, kingX))) {
                    isEmpty = false;
                }

                if (!isEmpty) {
                    count++;
                }
            }
        }

        return count;
    }
}
