package it.unibo.ai.didattica.competition.tablut.droptablut.interfaces;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.droptablut.TablutTreeNode;

public interface IMinMax {
    Action chooseAction(TablutTreeNode tree, IHeuristic heuristic);
}
