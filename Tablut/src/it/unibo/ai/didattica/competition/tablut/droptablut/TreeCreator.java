package it.unibo.ai.didattica.competition.tablut.droptablut;

import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IApplyAction;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.ICreateTree;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IListActions;



public class TreeCreator implements ICreateTree {
    private int state =0;


    @Override
    public TablutTreeNode generateTree(State fromState, int depth, 
            IListActions validActionsLister,
            IApplyAction actionApplier) {
        if (DTConstants.DEBUG_MODE) {
            System.out.println(String.format("Start tree creation"));
        }
        return generateTreeRec(fromState, depth, validActionsLister, actionApplier, null, null);
    }

    public TablutTreeNode generateTreeRec(State fromState, int depth, 
    IListActions validActionsLister, IApplyAction actionApplier, 
    Action action, TablutTreeNode parent) {
        TablutTreeNode current = new TablutTreeNode(fromState, action, parent);

        if (DTConstants.DEBUG_MODE || state %100000 ==0) {
            System.out.println(String.format("\t%d | %s: gen tree -- state: %d", depth, current, state));
        }

        if (depth > 0) {
            List<Action> possibleActions = validActionsLister.getValidActions(fromState);
            if (DTConstants.DEBUG_MODE ) {
                System.out.println(String.format("\t%d | %s: child actions %d", depth, current, possibleActions.size()));
            }

            for (Action childAction : possibleActions) {
                State childState = actionApplier.applyAction(fromState, childAction);
                if (   childState.getTurn() == Turn.BLACKWIN 
                    || childState.getTurn() == Turn.WHITEWIN
                    || childState.getTurn() == Turn.DRAW
                    ) 
                {
                    current.getChildren().add(new TablutTreeNode(childState, childAction, current));

                } else {
                    state++;
                    current.getChildren().add(generateTreeRec(childState, depth - 1, validActionsLister, actionApplier, childAction, current));
                }
            }
        }

        return current;
    }
}
