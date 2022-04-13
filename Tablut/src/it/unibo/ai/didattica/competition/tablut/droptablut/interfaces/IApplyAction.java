package it.unibo.ai.didattica.competition.tablut.droptablut.interfaces;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface IApplyAction {
    State applyAction(State fromState, Action action);
}
