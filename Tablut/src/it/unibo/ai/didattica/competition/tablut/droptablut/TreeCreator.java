package it.unibo.ai.didattica.competition.tablut.droptablut;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IApplyAction;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.ICreateTree;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IListActions;

public class TreeCreator implements ICreateTree {

    @Override
    public TablutTreeNode generateTree(State fromState, int depth, 
            IListActions validActionsLister,
            IApplyAction actionApplier) {
        TablutTreeNode current = new TablutTreeNode(fromState);

        if (depth <= 0) {
            List<State> childrenStates = validActionsLister.getValidActions(fromState)
                .stream()
                .map(action -> actionApplier.applyAction(fromState, action))
                .collect(Collectors.toList());

            for (State childrenState : childrenStates) {
                if (   childrenState.getTurn() == Turn.BLACKWIN 
                    || childrenState.getTurn() == Turn.WHITEWIN
                    || childrenState.getTurn() == Turn.DRAW
                    ) 
                {
                    current.getChildren().add(new TablutTreeNode(childrenState));
                } else {
                    current.getChildren().add(generateTree(childrenState, depth - 1, validActionsLister, actionApplier));
                }
            }
        }

        return current;
}
