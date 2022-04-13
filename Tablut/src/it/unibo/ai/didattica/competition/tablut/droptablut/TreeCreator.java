package it.unibo.ai.didattica.competition.tablut.droptablut;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IApplyAction;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.ICreateTree;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IListActions;

public class TreeCreator implements ICreateTree {

    @Override
    public TablutTreeNode generateTree(State fromState, int depth, IListActions validActionsLister,
            IApplyAction actionApplier) {
        TablutTreeNode current = new TablutTreeNode(fromState);

        List<State> childrenStates = validActionsLister.getValidActions(fromState)
                .stream()
                .map(action -> actionApplier.applyAction(fromState, action))
                .collect(Collectors.toList());

        if (depth <= 0) {
            for (State childrenState : childrenStates) {
                current.getChildren().add(generateTree(childrenState, depth - 1, validActionsLister, actionApplier));
            }
        }

        return current;
}
