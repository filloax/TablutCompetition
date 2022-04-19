package it.unibo.ai.didattica.competition.tablut.droptablut;

import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IListActions;

public class ListActions implements IListActions {

    @Override
    public List<Action> getValidActions(State state) {
        List<Action> out = new ArrayList<>();

        // Se lo stato non è finale
        if (state.getTurn() == Turn.BLACK || state.getTurn() == Turn.WHITE) {
            Pawn[][] board = state.getBoard();
            Pawn correctPawn = (state.getTurn() == Turn.BLACK) ? Pawn.BLACK : Pawn.WHITE;

            for (int y = 0; y < board.length; y++)
                for (int x = 0; x < board[0].length; x++) {
                    Pawn pawn = board[y][x];
                    if (pawn == correctPawn) {
                        out.addAll(checkPawnMoves(x, y, board.length, board.length, state));
                    }
                }
        }

        return out;
    }

    // Restituisci tutte le mosse che può fare una pedina
    private List<Action> checkPawnMoves(int x, int y, int sizex, int sizey, State state) {
        List<Action> out = new ArrayList<>();

        String from = coordStringFromIntCoords(x, y); // (0, 1) -> A2
        for (int nextX = 0; nextX < sizex; nextX++) {
            // Non cella di partenza
            if (nextX != x) {
                String to = coordStringFromIntCoords(nextX, y);
                Action action = new Action(from, to, state.getTurn());
                if (checkMove(state, action)) {
                    out.add(action);
                }
            }
        }

        for (int nextY = 0; nextY < sizey; nextY++) {
            if (nextY != y) {
                String to = coordStringFromIntCoords(x, nextY);
                Action action = new Action(from, to, state.getTurn());
                if (checkMove(state, action)) {
                    out.add(action);
                }
            }
        }

        return out;
    }

    private String coordStringFromIntCoords(int x, int y) {
        String row = Integer.toString(y + 1);
        /*
        public int getColumnFrom() {
		    return Character.toLowerCase(this.from.charAt(0)) - 97;
	    }
        */
        char column = (char) (x + 97);

        return column + row;
    }
    
    // Originalmente in GameTablut.java, adattata per controllo puro
	private boolean checkMove(State state, Action a)
	{
		//this.loggGame.fine(a.toString());
		//controllo la mossa
		// if(a.getTo().length()!=2 || a.getFrom().length()!=2)
		// {
		// 	this.loggGame.warning("Formato mossa errato");
		// 	throw new ActionException(a);
		// }
		int columnFrom = a.getColumnFrom();
		int columnTo = a.getColumnTo();
		int rowFrom = a.getRowFrom();
		int rowTo = a.getRowTo();
		
		//controllo se sono fuori dal tabellone
		// if(columnFrom>state.getBoard().length-1 || rowFrom>state.getBoard().length-1 || rowTo>state.getBoard().length-1 || columnTo>state.getBoard().length-1 || columnFrom<0 || rowFrom<0 || rowTo<0 || columnTo<0)
		// {
		// 	this.loggGame.warning("Mossa fuori tabellone");
		// 	throw new BoardException(a);			
		// }
		
		//controllo che non vada sul trono
		if(state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.THRONE.toString()))
		{
			return false;
		}
		
		//controllo la casella di arrivo
		if(!state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.EMPTY.toString()))
		{
			return false;
		}
		
		//controllo se cerco di stare fermo
		// if(rowFrom==rowTo && columnFrom==columnTo)
		// {
		// 	this.loggGame.warning("Nessuna mossa");
		// 	throw new StopException(a);
		// }
		
		//controllo se sto muovendo una pedina giusta
		// if(state.getTurn().equalsTurn(State.Turn.WHITE.toString()))
		// {
		// 	if(!state.getPawn(rowFrom, columnFrom).equalsPawn("W") && !state.getPawn(rowFrom, columnFrom).equalsPawn("K"))
		// 	{
		// 		this.loggGame.warning("Giocatore "+a.getTurn()+" cerca di muovere una pedina avversaria");
		// 		throw new PawnException(a);
		// 	}
		// }
		// if(state.getTurn().equalsTurn(State.Turn.BLACK.toString()))
		// {
		// 	if(!state.getPawn(rowFrom, columnFrom).equalsPawn("B"))
		// 	{
		// 		this.loggGame.warning("Giocatore "+a.getTurn()+" cerca di muovere una pedina avversaria");
		// 		throw new PawnException(a);
		// 	}
		// }
		
		//controllo di non muovere in diagonale
		// if(rowFrom != rowTo && columnFrom != columnTo)
		// {
		// 	this.loggGame.warning("Mossa in diagonale");
		// 	throw new DiagonalException(a);
		// }
		
		//controllo di non scavalcare pedine
		if(rowFrom==rowTo)
		{
			if(columnFrom>columnTo)
			{
				for(int i=columnTo; i<columnFrom; i++)
				{
					if(!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString()))
					{
                        return false;
					}
				}
			}
			else
			{
				for(int i=columnFrom+1; i<=columnTo; i++)
				{
					if(!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString()))
					{
                        return false;
					}
				}
			}
		}
		else
		{
			if(rowFrom>rowTo)
			{
				for(int i=rowTo; i<rowFrom; i++)
				{
					if(!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString()) && !state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString()))
					{
                        return false;
					}
				}
			}
			else
			{
				for(int i=rowFrom+1; i<=rowTo; i++)
				{
					if(!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString()) && !state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString()))
					{
                        return false;
					}
				}
			}
		}
		
		return true;
	}
}
