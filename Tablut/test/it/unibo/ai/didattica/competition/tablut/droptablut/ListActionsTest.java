package it.unibo.ai.didattica.competition.tablut.droptablut;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IListActions;
import org.junit.Before;
import org.junit.Test;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;


public class ListActionsTest {
	IListActions actionsLister;

	@Before
	public void before() {
		actionsLister = new ListActions();
	}

	@Test
    public void testNoObstacles() {
		String boardStr = String.join("\n",
				"_________",
				"_________",
				"_________",
				"_________",
				"_________",
				"_________",
				"__W______",
				"_________",
				"_________"
		);
		State state = StateHelper.stateFromString(Turn.WHITE, boardStr);
		List<Action> actions = actionsLister.getValidActions(state);

		assertEquals(16, actions.size());
		assertEquals("c7", actions.get(0).getFrom().toLowerCase());
    }

	@Test
	public void testObstaclesCitadels() {
		String boardStr = String.join("\n",
				"_________",
				"_________",
				"_________",
				"_________",
				"____W____",
				"_________",
				"_________",
				"_________",
				"_________"
		);
		State state = StateHelper.stateFromString(Turn.WHITE, boardStr);
		List<Action> actions = actionsLister.getValidActions(state);

		assertEquals(8, actions.size());
	}

	@Test
	public void testObstaclesPawns() {
		String boardStr = String.join("\n",
				"_________",
				"_________",
				"___W_____",
				"_________",
				"___WT____",
				"_________",
				"___B_____",
				"_________",
				"_________"
		);
		State state = StateHelper.stateFromString(Turn.WHITE, boardStr);
		List<Action> actions = actionsLister.getValidActions(state);

		assertEquals(10 + 3, actions.size());
	}


	@Test
	public void testCitadelsBlack() {
		String boardStr = String.join("\n",
				"_________",
				"_________",
				"_________",
				"_________",
				"B___T_B__",
				"_________",
				"_________",
				"_________",
				"_________"
		);
		State state = StateHelper.stateFromString(Turn.BLACK, boardStr);
		List<Action> actions = actionsLister.getValidActions(state);

		assertEquals(11 + 9, actions.size());
	}
}
