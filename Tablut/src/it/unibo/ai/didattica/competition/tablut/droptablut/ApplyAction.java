package it.unibo.ai.didattica.competition.tablut.droptablut;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IApplyAction;

public class ApplyAction implements IApplyAction {

    @Override
    public State applyAction(State fromState, Action action) {
        // Questo suppone che Turn venga copiato e non sia un riferimento
        Turn startTurn = fromState.getTurn();
		State state = this.movePawn(fromState, action);
		
		if (startTurn.equalsTurn("B"))
		{
			state = this.checkCaptureBlack(state, action);
		}
		if (startTurn.equalsTurn("W"))
		{
			state = this.checkCaptureWhite(state, action);
		}

        return state;
    }
    
    	
    // Anche questo originalmente in GameTablut.java
	/**
	 * This method move the pawn in the board
	 * @param state is the initial state
	 * @param a is the action of a pawn
	 * @return is the new state of the game with the moved pawn
	 */
	private State movePawn(State state, Action a) {
		State.Pawn pawn = state.getPawn(a.getRowFrom(), a.getColumnFrom());
		State.Pawn[][] newBoard = state.getBoard();
		//State newState = new State();
		//libero il trono o una casella qualunque
		if(newBoard.length==9)
		{
			if(a.getColumnFrom()==4 && a.getRowFrom()==4)
			{
				newBoard[a.getRowFrom()][a.getColumnFrom()]= State.Pawn.THRONE;
			}
			else
			{
				newBoard[a.getRowFrom()][a.getColumnFrom()]= State.Pawn.EMPTY;
			}
		}
		if(newBoard.length==7)
		{
			if(a.getColumnFrom()==3 && a.getRowFrom()==3)
			{
				newBoard[a.getRowFrom()][a.getColumnFrom()]= State.Pawn.THRONE;
			}
			else
			{
				newBoard[a.getRowFrom()][a.getColumnFrom()]= State.Pawn.EMPTY;
			}
		}
		
		//metto nel nuovo tabellone la pedina mossa
		newBoard[a.getRowTo()][a.getColumnTo()]=pawn;
		//aggiorno il tabellone
		state.setBoard(newBoard);
		//cambio il turno
		if(state.getTurn().equalsTurn(State.Turn.WHITE.toString()))
		{
			state.setTurn(State.Turn.BLACK);
		}
		else
		{
			state.setTurn(State.Turn.WHITE);
		}
		
		
		return state;
	}
	
    /**
	 * This method check if a pawn is captured and if the game ends
	 * @param state the state of the game
	 * @param a the action of the previous moved pawn
	 * @return the new state of the game
	 */
	private State checkCaptureWhite(State state, Action a)
	{
		//controllo se mangio a destra
		if(a.getColumnTo()<state.getBoard().length-2 && state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("B") && (state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("W")||state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("T")||state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("K")))
		{
			state.removePawn(a.getRowTo(), a.getColumnTo()+1);
			// this.movesWithutCapturing=-1;
		}
		//controllo se mangio a sinistra
		if(a.getColumnTo()>1 && state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("B") && (state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("W")||state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("T")||state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("K")))
		{
			state.removePawn(a.getRowTo(), a.getColumnTo()-1);
			// this.movesWithutCapturing=-1;
		}
		//controllo se mangio sopra
		if(a.getRowTo()>1 && state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("B") && (state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("W")||state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("T")||state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("K")))
		{
			state.removePawn(a.getRowTo()-1, a.getColumnTo());
			// this.movesWithutCapturing=-1;
		}
		//controllo se mangio sotto
		if(a.getRowTo()<state.getBoard().length-2 && state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("B") && (state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("W")||state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("T")||state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("K")))
		{
			state.removePawn(a.getRowTo()+1, a.getColumnTo());
			// this.movesWithutCapturing=-1;
		}
		//controllo se ho vinto
		if(a.getRowTo()==0 || a.getRowTo()==state.getBoard().length-1 || a.getColumnTo()==0 || a.getColumnTo()==state.getBoard().length-1)
		{
			if(state.getPawn(a.getRowTo(), a.getColumnTo()).equalsPawn("K"))
			{
				state.setTurn(State.Turn.WHITEWIN);
			}
		}
		
		//controllo il pareggio
		// if(this.movesWithutCapturing>=this.movesDraw && (state.getTurn().equalsTurn("B")||state.getTurn().equalsTurn("W")))
		// {
		// 	state.setTurn(State.Turn.DRAW);
		// }
		// this.movesWithutCapturing++;
		return state;
	}
	
	/**
	 * This method check if a pawn is captured and if the game ends
	 * @param state the state of the game
	 * @param a the action of the previous moved pawn
	 * @return the new state of the game
	 */
	private State checkCaptureBlack(State state, Action a)
	{
		//controllo se mangio a destra
		if(a.getColumnTo()<state.getBoard().length-2 && (state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("W")||state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("K")) && (state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("B")||state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("T")))
		{
			//nero-re-trono N.B. No indexOutOfBoundException perch� se il re si trovasse sul bordo il giocatore bianco avrebbe gi� vinto
			if(state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("K") && state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("T"))
			{
				//ho circondato su 3 lati il re?
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("B") && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			//nero-re-nero
			if(state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("K") && state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("B"))
			{
				//mangio il re?
				if(!state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("T") && !state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("T"))
				{
					if(!(a.getRowTo()*2 + 1==9 && state.getBoard().length==9) && !(a.getRowTo()*2 + 1==7 && state.getBoard().length==7))
					{
						state.setTurn(State.Turn.BLACKWIN);
					}	
				}						
				//ho circondato su 3 lati il re?
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("B") && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("T"))
				{
					state.setTurn(State.Turn.BLACKWIN);
				}
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("T") && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
				}
			}			
			//nero-bianco-trono/nero
			if(state.getPawn(a.getRowTo(), a.getColumnTo()+1).equalsPawn("W"))
			{
				state.removePawn(a.getRowTo(), a.getColumnTo()+1);
				// this.movesWithutCapturing=-1;
			}
			
		}
		//controllo se mangio a sinistra
		if(a.getColumnTo()>1 && (state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("W")||state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("K")) && (state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("B")||state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("T")))
		{
			//trono-re-nero
			if(state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("K") && state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("T"))
			{
				//ho circondato su 3 lati il re?
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("B") && state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			//nero-re-nero
			if(state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("K") && state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("B"))
			{
				//mangio il re?
				if(!state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("T") && !state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("T"))
				{
					if(!(a.getRowTo()*2 + 1==9 && state.getBoard().length==9) && !(a.getRowTo()*2 + 1==7 && state.getBoard().length==7))
					{
						state.setTurn(State.Turn.BLACKWIN);
					}
				}
				//ho circondato su 3 lati il re?
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("B") && state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("T"))
				{
					state.setTurn(State.Turn.BLACKWIN);
				}
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("T") && state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			//trono/nero-bianco-nero
			if(state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("W"))
			{
				state.removePawn(a.getRowTo(), a.getColumnTo()-1);
				// this.movesWithutCapturing=-1;
			}
		}
		//controllo se mangio sopra
		if(a.getRowTo()>1 && (state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("W")||state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("K")) && (state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("B")||state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("T")))
		{
			//nero-re-trono 
			if(state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("K") && state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("T"))
			{
				//ho circondato su 3 lati il re?
				if(state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("B") && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
				}
			}			
			//nero-re-nero
			if(state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("K") && state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("B"))
			{
				//ho circondato su 3 lati il re?
				if(state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("B") && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("T"))
				{
					state.setTurn(State.Turn.BLACKWIN);
				}
				if(state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("T") && state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
				}
				//mangio il re?
				if(!state.getPawn(a.getRowTo()-1, a.getColumnTo()-1).equalsPawn("T") && !state.getPawn(a.getRowTo()-1, a.getColumnTo()+1).equalsPawn("T"))
				{
					if(!(a.getRowTo()*2 + 1==9 && state.getBoard().length==9) && !(a.getRowTo()*2 + 1==7 && state.getBoard().length==7))
					{
						state.setTurn(State.Turn.BLACKWIN);
					}
				}
			}			
			//nero-bianco-trono/nero
			if(state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("W"))
			{
				state.removePawn(a.getRowTo()-1, a.getColumnTo());
				// this.movesWithutCapturing=-1;
			}
		}
		//controllo se mangio sotto
		if(a.getRowTo()<state.getBoard().length-2 && (state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("W")||state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("K")) && (state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("B")||state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("T")))
		{
			//nero-re-trono
			if(state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("K") && state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("T"))
			{
				//ho circondato su 3 lati il re?
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("B") && state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
				}
			}			
			//nero-re-nero
			if(state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("K") && state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("B"))
			{
				//ho circondato su 3 lati il re?
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("B") && state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("T"))
				{
					state.setTurn(State.Turn.BLACKWIN);
				}
				if(state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("T") && state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
				}
				//mangio il re?
				if(!state.getPawn(a.getRowTo()+1, a.getColumnTo()+1).equalsPawn("T") && !state.getPawn(a.getRowTo()+1, a.getColumnTo()-1).equalsPawn("T"))
				{
					if(!(a.getRowTo()*2 + 1==9 && state.getBoard().length==9) && !(a.getRowTo()*2 + 1==7 && state.getBoard().length==7))
					{
						state.setTurn(State.Turn.BLACKWIN);
					}
				}
			}		
			//nero-bianco-trono/nero
			if(state.getPawn(a.getRowTo()+1, a.getColumnTo()).equalsPawn("W"))
			{
				state.removePawn(a.getRowTo()+1, a.getColumnTo());
				// this.movesWithutCapturing=-1;
			}			
		}
		//controllo il re completamente circondato
		if(state.getPawn(4, 4).equalsPawn(State.Pawn.KING.toString()) && state.getBoard().length==9)
		{
			if(state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(5, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);
			}
		}
		if(state.getPawn(3, 3).equalsPawn(State.Pawn.KING.toString()) && state.getBoard().length==7)
		{
			if(state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(2, 3).equalsPawn("B") && state.getPawn(3, 2).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);
			}
		}
		//controllo regola 11
		if(state.getBoard().length==9)
		{
			if(a.getColumnTo()==4 && a.getRowTo()==2)
			{
				if(state.getPawn(3, 4).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B") && state.getPawn(5, 4).equalsPawn("B"))
				{
					state.removePawn(3, 4);
					// this.movesWithutCapturing=-1;
				}
			}
			if(a.getColumnTo()==4 && a.getRowTo()==6)
			{
				if(state.getPawn(5, 4).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B") && state.getPawn(3, 4).equalsPawn("B"))
				{
					state.removePawn(5, 4);
					// this.movesWithutCapturing=-1;
				}
			}
			if(a.getColumnTo()==2 && a.getRowTo()==4)
			{
				if(state.getPawn(4, 3).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K") && state.getPawn(3, 4).equalsPawn("B") && state.getPawn(5, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B"))
				{
					state.removePawn(4, 3);
					// this.movesWithutCapturing=-1;
				}
			}
			if(a.getColumnTo()==6 && a.getRowTo()==4)
			{
				if(state.getPawn(4, 5).equalsPawn("W") && state.getPawn(4, 4).equalsPawn("K") && state.getPawn(4, 3).equalsPawn("B") && state.getPawn(5, 4).equalsPawn("B") && state.getPawn(3, 4).equalsPawn("B"))
				{
					state.removePawn(4, 5);
					// this.movesWithutCapturing=-1;
				}
			}
		}
		
		
		//controllo il pareggio
		// if(this.movesWithutCapturing>=this.movesDraw && (state.getTurn().equalsTurn("B")||state.getTurn().equalsTurn("W")))
		// {
		// 	state.setTurn(State.Turn.DRAW);
		// }
		// this.movesWithutCapturing++;
		return state;
	}


}