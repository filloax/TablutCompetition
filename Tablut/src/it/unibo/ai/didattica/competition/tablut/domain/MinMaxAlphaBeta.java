package it.unibo.ai.didattica.competition.tablut.domain;

import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.droptablut.TablutTreeNode;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IHeuristic;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IMinMax;

public class MinMaxAlphaBeta implements IMinMax {

    public static final boolean DEBUG_MODE = true;

    @Override
    public Action chooseAction(TablutTreeNode tree, IHeuristic heuristic) {
        /*
        per ogni figlio del primo nodo:
            usa alpha beta per trovare il punteggio migliore che ottieni su quella strada
        
        */
        double bestOverall = minmax(tree, true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, heuristic);
        Action action = null;

        for (TablutTreeNode child : tree.getChildren()) {
            if (child.hasValue() && child.getValue() == bestOverall) {
                action = child.getAction();
                if (DEBUG_MODE) {
                    System.out.println(String.format("Scelta azione con punteggio %f: %s -> %s", bestOverall, action.getFrom(), action.getTo()));
                }
            }
        }

        // Non trovato, vai a caso
        if (action == null) {
            Random random = new Random();
            random.setSeed(System.currentTimeMillis());
            action = tree.getChildren().get(random.nextInt(tree.getChildren().size())).getAction();
            if (DEBUG_MODE) {
                System.err.println(String.format("Oh no, sto andando a caso: %s -> %s", action.getFrom(), action.getTo()));
            }
        }
        return action;
    }

    public double minmax(TablutTreeNode node, boolean isMaxPlayer, double alpha, double beta, IHeuristic heuristic) {
        /*
        function minimax(node, depth, isMaximizingPlayer, alpha, beta):
            if node is a leaf node :
                return value of the node
            
            if isMaximizingPlayer :
                bestVal = -INFINITY 
                for each child node :
                    value = minimax(node, depth+1, false, alpha, beta)
                    bestVal = max( bestVal, value) 
                    alpha = max( alpha, bestVal)
                    if beta <= alpha:
                        break
                return bestVal

            else :
                bestVal = +INFINITY 
                for each child node :
                    value = minimax(node, depth+1, true, alpha, beta)
                    bestVal = min( bestVal, value) 
                    beta = min( beta, bestVal)
                    if beta <= alpha:
                        break
            return bestVal
        */
        if (node.isLeaf())
            return heuristic.heuristic(node.getState());

        double bestVal;
        if (isMaxPlayer) {
            bestVal = Double.NEGATIVE_INFINITY;
            for (TablutTreeNode child : node.getChildren()) {
                double val = minmax(child, false, alpha, beta, heuristic);
                bestVal = Math.max(val, bestVal);
                node.setValue(bestVal);
                alpha = Math.max(alpha, bestVal);
                if (beta <= alpha) 
                    break;
            }
            return bestVal;
        } else {
            bestVal = Double.POSITIVE_INFINITY;
            for (TablutTreeNode child : node.getChildren()) {
                double val = minmax(child, true, alpha, beta, heuristic);
                bestVal = Math.min(val, bestVal);
                node.setValue(bestVal);
                beta = Math.min(beta, bestVal);
                if (beta <= alpha) 
                    break;
            }
            return bestVal;
        }
    }
    
}