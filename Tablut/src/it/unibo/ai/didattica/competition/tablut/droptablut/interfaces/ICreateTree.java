package it.unibo.ai.didattica.competition.tablut.droptablut.interfaces;

import java.util.TreeSet;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.droptablut.TablutTreeNode;

public interface ICreateTree {
    TablutTreeNode generateTree(State fromState, int depth, IListActions validActionsLister, 
        IApplyAction actionApplier);
}
