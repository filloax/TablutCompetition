package it.unibo.ai.didattica.competition.tablut.droptablut.interfaces;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface IHeuristic {
    double heuristic(State state);
}
