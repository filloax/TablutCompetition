package it.unibo.ai.didattica.competition.tablut.droptablut;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IApplyAction;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ApplyActionTest {
    IApplyAction actionApplier;

    // In generale, si suppone che il controllo che la
    // mossa sia valida sia già stato fatto

    @Before
    public void before() {
        actionApplier = new ApplyAction();
    }

    @Test
    public void testApplyActionBasicMove() {
        String boardStr = String.join("\n",
                "_________",
                "_________",
                "_________",
                "_________",
                "___WT____",
                "_________",
                "_________",
                "_________",
                "_________"
        );
        State state = StateHelper.stateFromString(State.Turn.WHITE, boardStr);
        Action action = new Action("D5", "D2", State.Turn.WHITE);
        State result = actionApplier.applyAction(state, action);
        assertEquals(State.Pawn.EMPTY, result.getPawn(4, 3));
        assertEquals(State.Pawn.WHITE, result.getPawn(1, 3));
        assertEquals(State.Turn.BLACK, result.getTurn());
    }

    @Test
    public void testApplyActionBasicCapture() {
        String boardStr = String.join("\n",
                "_________",
                "___W_____",
                "___B_____",
                "_________",
                "___WT____",
                "_________",
                "_________",
                "_________",
                "_________"
        );
        State state = StateHelper.stateFromString(State.Turn.WHITE, boardStr);
        Action action = new Action("D5", "D4", State.Turn.WHITE);
        State result = actionApplier.applyAction(state, action);
        assertEquals(State.Pawn.EMPTY, result.getPawn(4, 3));
        assertEquals(State.Pawn.WHITE, result.getPawn(3, 3));
        assertEquals(State.Pawn.EMPTY, result.getPawn(2, 3));
        assertEquals(State.Pawn.WHITE, result.getPawn(1, 3));
    }

    @Test
    public void testApplyActionCastleCapture() {
        String boardStr = String.join("\n",
                "_________",
                "_________",
                "___W_____",
                "__W___W__",
                "___BTB___",
                "_________",
                "_________",
                "_________",
                "_________"
        );

        State state = StateHelper.stateFromString(State.Turn.WHITE, boardStr);
        Action leftCapture = new Action("C4", "C5", State.Turn.WHITE);
        Action rightCapture = new Action("G4", "G5", State.Turn.WHITE);
        Action leftNoCapture = new Action("D3", "D4", State.Turn.WHITE);

        State result1 = actionApplier.applyAction(state, leftCapture);
        System.out.println("Result1");
        System.out.println(result1);
        assertEquals(State.Pawn.WHITE, result1.getPawn(4, 2));
        assertEquals(State.Pawn.EMPTY, result1.getPawn(4, 3));
        State result2 = actionApplier.applyAction(state, rightCapture);
        System.out.println("Result2");
        System.out.println(result2);
        assertEquals(State.Pawn.EMPTY, result2.getPawn(4, 5));
        assertEquals(State.Pawn.WHITE, result2.getPawn(4, 6));
        State result3 = actionApplier.applyAction(state, leftNoCapture);
        System.out.println("Result3");
        System.out.println(result3);
        assertEquals(State.Pawn.WHITE, result3.getPawn(3, 3));
        assertEquals(State.Pawn.BLACK, result3.getPawn(4, 3));
    }

    // Mancano da fare nel caso: test cattura neri, test cattura neri con accampamenti, test cattura re, test vittoria
}
