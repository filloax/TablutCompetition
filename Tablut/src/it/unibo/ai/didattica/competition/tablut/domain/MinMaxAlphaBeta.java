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
        double bestOverall = minmax(tree, 0, true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, heuristic);
        Action action = null;

        if (DEBUG_MODE) {
            System.out.println(String.format("Punteggio euristico mosse (tot mosse: %d):", tree.getChildren().size()));
            for (TablutTreeNode child : tree.getChildren()) {
                System.out.println(String.format(
                    "\t%s -> %s: %s", 
                    child.getAction().getFrom(),
                    child.getAction().getTo(),
                    (child.hasValue() ? child.getValue() : "skip")
                ));
            }
        }

        for (TablutTreeNode child : tree.getChildren()) {
            if (child.hasValue() && child.getValue() == bestOverall) {
                action = child.getAction();
                if (DEBUG_MODE) {
                    System.out.println(String.format("Scelta azione con punteggio %f: %s -> %s", bestOverall, action.getFrom(), action.getTo()));
                }
                break;
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

    public double minmax(TablutTreeNode node, int depth, boolean isMaxPlayer, double alpha, double beta, IHeuristic heuristic) {
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

        if (DEBUG_MODE) {
            System.out.println(String.format("%d | Running for node with %d children %s", 
                depth, node.getChildren().size(), node.toStringTrace()));
        }

        if (node.isLeaf()) {
            double val = heuristic.heuristic(node.getState());
            if (DEBUG_MODE) {
                System.out.println(String.format("--> %d | Ran heuristic for %s: %f", depth, node, val));
            }
            return val;
        }

        double bestVal;
        if (isMaxPlayer) {
            bestVal = Double.NEGATIVE_INFINITY;
            for (TablutTreeNode child : node.getChildren()) {
                double val = minmax(child, depth + 1, false, alpha, beta, heuristic);
                if (val != bestVal) {
                    bestVal = Math.max(val, bestVal);
                    if (DEBUG_MODE && node.getAction() != null) {
                        System.out.println(String.format("%d | Setting value of node %s to %f", 
                            depth, node, bestVal));
                    }
                    node.setValue(bestVal);
                }
                alpha = Math.max(alpha, bestVal);
                if (beta <= alpha) 
                    break;
            }
            return bestVal;
        } else {
            bestVal = Double.POSITIVE_INFINITY;
            for (TablutTreeNode child : node.getChildren()) {
                double val = minmax(child, depth + 1, true, alpha, beta, heuristic);
                if (val != bestVal) {
                    bestVal = Math.min(val, bestVal);
                    if (DEBUG_MODE && node.getAction() != null) {
                        System.out.println(String.format("%d | Setting value of node %s to %f", 
                            depth, node, bestVal));
                    }
                    node.setValue(bestVal);
                }
                beta = Math.min(beta, bestVal);
                if (beta <= alpha) 
                    break;
            }
            return bestVal;
        }
    }
    
}