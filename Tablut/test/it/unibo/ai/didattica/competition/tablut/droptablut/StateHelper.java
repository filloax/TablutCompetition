package it.unibo.ai.didattica.competition.tablut.droptablut;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class StateHelper {
    public static State stateFromString(Turn turn, String from) {
        /*
        Riferimento:
        StringBuffer result = new StringBuffer();
		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board.length; j++) {
				result.append(this.board[i][j].toString());
				if (j == 8) {
					result.append("\n");
				}
			}
		}
		return result.toString();

        */
        String[] lines = from.split("\n");
        State out = new StateTablut();
        Pawn[][] board = out.getBoard();

        if (lines.length != 9)
            throw new IllegalArgumentException(String.format("Wrong number of lines: was %d, expected 9", lines.length));

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.length() != 9)
                throw new IllegalArgumentException(String.format("Wrong line length at line %d: was %d, expected 9", i, line.length()));
            for (int j = 0; j < line.length(); j++) {
                String pawnChar = "" + line.charAt(j);
                if (pawnChar.equals("_")) pawnChar = "O";

                Pawn pawn = Pawn.fromString(pawnChar);
                board[i][j] = pawn;
            }
        }

        out.setTurn(turn);
        out.setBoard(board);

        return out;
    }
}
