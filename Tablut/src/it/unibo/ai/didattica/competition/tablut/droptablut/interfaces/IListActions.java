package it.unibo.ai.didattica.competition.tablut.droptablut.interfaces;

import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface IListActions {
    List<Action> getValidActions(State state);
}
