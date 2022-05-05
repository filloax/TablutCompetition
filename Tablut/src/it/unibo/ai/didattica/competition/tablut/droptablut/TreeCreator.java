package it.unibo.ai.didattica.competition.tablut.droptablut;

import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.ICreateTree;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IActionHandler;


public class TreeCreator implements ICreateTree {
    private static final boolean DEBUG_MODE = true;

    private int debugCounter = 0;
    private boolean verbose = DEBUG_MODE && DTConstants.DEBUG_MODE;

    @Override
    public TablutTreeNode generateTree(State fromState, int depth, 
            IActionHandler actionHandler) {
        if (verbose) {
            System.out.println(String.format("Start tree creation"));
            debugCounter = 0;
        }

        TablutTreeNode out = generateTreeRec(fromState, depth, actionHandler, null, null);

        if (verbose) {
            System.out.println(String.format("Recursive function calls: %d", debugCounter));
        }

        return out;
    }

    public TablutTreeNode generateTreeRec(State fromState, int depth,
                                          IActionHandler actionHandler,
                                          Action action, TablutTreeNode parent) {
        TablutTreeNode current = new TablutTreeNode(fromState, action, parent);

        if (verbose && debugCounter % 100000 == 0) {
            System.out.println(String.format("\t%d | %s: gen tree -- counter: %d", depth, current, debugCounter));
        }

        if (depth > 0) {
            List<Action> possibleActions = actionHandler.getValidActions(fromState);
            if (verbose && debugCounter % 10000 == 0) {
                System.out.println(String.format("\t%d | %s: child actions %d", depth, current, possibleActions.size()));
            }

            for (Action childAction : possibleActions) {
                State childState = actionHandler.applyAction(fromState, childAction);
                if (   childState.getTurn() == Turn.BLACKWIN 
                    || childState.getTurn() == Turn.WHITEWIN
                    || childState.getTurn() == Turn.DRAW
                    ) 
                {
                    current.getChildren().add(new TablutTreeNode(childState, childAction, current));
//                    if (childState.getTurn() == Turn.WHITEWIN && depth==3){
//                        System.out.println(String.format("\t%d | ho trovato una vittoria bianca con la mossa %s dallo state:\n ", depth, current));
//                        System.out.println(childState);
//                    }

                } else {
                    if (verbose) {
                        debugCounter++;
                    }

                    current.getChildren().add(generateTreeRec(childState, depth - 1, actionHandler, childAction, current));
                }
            }
        }

        return current;
    }
}
