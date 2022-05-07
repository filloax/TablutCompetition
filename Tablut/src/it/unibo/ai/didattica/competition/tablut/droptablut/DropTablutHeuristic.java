package it.unibo.ai.didattica.competition.tablut.droptablut;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IHeuristic;

public class DropTablutHeuristic implements IHeuristic {
    private boolean isWhite;

    public static final int WHITE_START_NUM = 8;
    public static final int BLACK_START_NUM = 16;

    public DropTablutHeuristic(Turn color) {
        this.isWhite = color.equals(Turn.WHITE);
    }

    @Override
    public float heuristic(State state) {
        Turn turn = state.getTurn();
        if (turn.equals(Turn.BLACKWIN)) {
            if (isWhite) {
                return -1000;//Double.NEGATIVE_INFINITY;
            } else {
                return 1000; //Double.POSITIVE_INFINITY;
            }
        } else if (turn.equals(Turn.WHITEWIN)) {
            if (isWhite) {
                return 1000; //Double.POSITIVE_INFINITY;
            } else {
                return -1000;//Double.NEGATIVE_INFINITY;
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
        float kingCapturePct = calcKingCapturePct(kingX,kingY,state);
        float pctWhite = numWhite / (float) WHITE_START_NUM;
        float pctBlack = numBlack / (float) BLACK_START_NUM;

        if (isWhite) {
            return getWhiteScore(pctWhite, 1 - pctBlack, numFreeDirections, numObstacles, kingCapturePct);
        } else {
            return getBlackScore(pctBlack, 1 - pctWhite, numFreeDirections, numObstacles, kingCapturePct);
        }
    }

    private float calcKingCapturePct(int kingX, int kingY, State state) {
        float pct;
        Pawn board[][] = state.getBoard();

        if(board[kingY][kingX] == Pawn.THRONE){ //sul trono
            pct = (
                    (board[kingY+1][kingX]==Pawn.BLACK?1:0)
                    + (board[kingY-1][kingX]==Pawn.BLACK?1:0)
                    + (board[kingY][kingX+1]==Pawn.BLACK?1:0)
                    + (board[kingY][kingX-1]==Pawn.BLACK?1:0)
                ) * 0.25f;
        }
        else if(board[kingY+1][kingX]==Pawn.THRONE ||
                board[kingY-1][kingX]==Pawn.THRONE ||
                board[kingY][kingX+1]==Pawn.THRONE ||
                board[kingY][kingX-1]==Pawn.THRONE){ //adiacente al trono
            pct = (
                    (board[kingY+1][kingX]==Pawn.BLACK?1:0)
                    + (board[kingY-1][kingX]==Pawn.BLACK?1:0)
                    + (board[kingY][kingX+1]==Pawn.BLACK?1:0)
                    + (board[kingY][kingX-1]==Pawn.BLACK?1:0)
                ) *0.33f;
        }
        else { //non adiacente al trono
            pct = (
                    (board[kingY+1][kingX]==Pawn.BLACK?1:0)
                    + (board[kingY-1][kingX]==Pawn.BLACK?1:0)
                    + (board[kingY][kingX+1]==Pawn.BLACK?1:0)
                    + (board[kingY][kingX-1]==Pawn.BLACK?1:0)
                ) * 0.5f
                + (
                    (DTConstants.citadels.contains(state.getBox(kingY+1, kingX))?1:0)
                    + (DTConstants.citadels.contains(state.getBox(kingY-1, kingX))?1:0)
                    + (DTConstants.citadels.contains(state.getBox(kingY, kingX+1))?1:0)
                    + (DTConstants.citadels.contains(state.getBox(kingY, kingX-1))?1:0)
                ) * 0.5f;
            ;
        }
                
        return pct;
    }


    private float getWhiteScore(float livingOursPct, float eatenTheirsPct, int numFreeDirections, int numObstacles, float kingCapturePct) {
        return livingOursPct * 25 + eatenTheirsPct * 20 + numFreeDirections *  20 + kingCapturePct * -5; // + numObstacles * -1;
    }

    private float getBlackScore(float livingOursPct, float eatenTheirsPct, int numFreeDirections, int numObstacles, float kingCapturePct) {
        return livingOursPct * 20 + eatenTheirsPct * 25 + numFreeDirections * -20 + kingCapturePct * 20; // + numObstacles;
    }

    private int countKingObstacles(State state, Direction dir, int kingX, int kingY) {
        Pawn board[][] = state.getBoard();
        int count = 0;

        if (dir == Direction.LEFT) {
            for (int x = kingX - 1; x >= 0; x--) {
                boolean isEmpty = board[kingY][x] == Pawn.EMPTY;
                // Controlla se è un accampamento
                if (isEmpty && DTConstants.citadels.contains(state.getBox(kingY, x))) {
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
                if (isEmpty && DTConstants.citadels.contains(state.getBox(kingY, x))) {
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
                if (isEmpty && DTConstants.citadels.contains(state.getBox(y, kingX))) {
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
                if (isEmpty && DTConstants.citadels.contains(state.getBox(y, kingX))) {
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
