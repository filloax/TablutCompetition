package it.unibo.ai.didattica.competition.tablut.droptablut;

import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
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
        return generateTreeRec(fromState, depth, validActionsLister, actionApplier, null, null);
    }

    public TablutTreeNode generateTreeRec(State fromState, int depth, 
    IListActions validActionsLister, IApplyAction actionApplier, 
    Action action, TablutTreeNode parent) {
        TablutTreeNode current = new TablutTreeNode(fromState, action, parent);

        if (depth > 0) {
            List<Action> possibleActions = validActionsLister.getValidActions(fromState);

            for (Action childAction : possibleActions) {
                State childState = actionApplier.applyAction(fromState, childAction);
                if (   childState.getTurn() == Turn.BLACKWIN 
                    || childState.getTurn() == Turn.WHITEWIN
                    || childState.getTurn() == Turn.DRAW
                    ) 
                {
                    current.getChildren().add(new TablutTreeNode(childState, childAction, current));
                } else {
                    current.getChildren().add(generateTreeRec(childState, depth - 1, validActionsLister, actionApplier, childAction, current));
                }
            }
        }

        return current;
    }
}
