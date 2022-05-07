package it.unibo.ai.didattica.competition.tablut.droptablut;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IHeuristic;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IMinMax;

public class MinMaxAlphaBeta implements IMinMax {
    static boolean DEBUG_MODE = true;
    static boolean DEBUG_PRINT_ALL = true;
    static boolean DEBUG_PRINT_INNER = false;

    public static final double BRANCH_LENGTH_WEIGHT = 0.001;

    private boolean verbose = DEBUG_MODE && DTConstants.DEBUG_MODE;
    private int debugCounter = 0;

    @Override
    public Action chooseAction(TablutTreeNode tree, IHeuristic heuristic) {
        if (verbose) {
            System.out.println("Inizio algoritmo alpha-beta...");
            debugCounter = 0;
        }

        /*
        per ogni figlio del primo nodo:
            usa alpha beta per trovare il punteggio migliore che ottieni su quella strada
        
        */
        double bestOverall = minmax(tree, 0, true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, heuristic);
        Action action = null;

        if (verbose) {
            System.out.println(String.format("Alpha-beta eseguito in %d ricorsioni, bestOverall: %f", debugCounter, bestOverall));
            System.out.println(String.format("Punteggio euristico prime mosse (tot mosse: %d):", tree.getChildren().size()));
            for (TablutTreeNode child : tree.getChildren()) {
                System.out.println(String.format(
                    "\t%s -> %s: %s", 
                    child.getAction().getFrom(),
                    child.getAction().getTo(),
                    (child.hasValue() ? child.getValue() : "skip")
                ));
            }
        }

        List<Action> bestMoves = tree.getChildren().stream()
                                            .filter(node -> node.hasValue() && equalsPrecision(bestOverall, node.getValue(), 0.00001))
                                            .map(TablutTreeNode::getAction)
                                            .collect(Collectors.toList());
        

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        // Usa la mossa migliore, o una a caso tra le migliori
        // nel caso ce ne siano a pari merito
        if (!bestMoves.isEmpty()) {
            action = bestMoves.get(random.nextInt(bestMoves.size()));
            if (verbose) {
                System.out.println(String.format("Scelta azione con punteggio %f: %s -> %s (tra %d possibili)", bestOverall, action.getFrom(), action.getTo(), bestMoves.size()));
            }
        }

        // Non trovato, vai a caso
        if (action == null) {
            action = tree.getChildren().get(random.nextInt(tree.getChildren().size())).getAction();
            if (verbose) {
                System.err.println(String.format("warning: Oh no, sto andando a caso: %s -> %s", action.getFrom(), action.getTo()));
                System.out.println(String.format("Oh no, sto andando a caso: %s -> %s", action.getFrom(), action.getTo()));
            }
        }

        return action;
    }

    public double minmax(TablutTreeNode node, int depth, boolean isMaxPlayer, double alpha, double beta, IHeuristic heuristic) {
        return minmax(node, depth, isMaxPlayer, alpha, beta, heuristic, true);
    }

    public double minmax(TablutTreeNode node, int depth, boolean isMaxPlayer, double alpha, double beta, IHeuristic heuristic, boolean prioritizeShorterBranch) {
        if (verbose) {
            debugCounter++;
            if ((depth <= 1 || debugCounter % 1000 == 0 || DEBUG_PRINT_ALL) && DEBUG_PRINT_INNER) {
                System.out.println(String.format("%d | Running for node with %d children %s", 
                    depth, node.getChildren().size(), node.toStringTrace()));
            }
        }

        if (node.isLeaf() || node.getChildren().isEmpty()) {
            double val = heuristic.heuristic(node.getState());
            if (prioritizeShorterBranch)
                val = val - BRANCH_LENGTH_WEIGHT * depth; // preferisci rami più corti
            
            if (verbose && (debugCounter % 1000 == 0 || DEBUG_PRINT_ALL) && DEBUG_PRINT_INNER) {
                System.out.println(String.format("--> %d | Ran heuristic for %s: %f", depth, node, val));
            }

            node.setValue(val);

            return val;
        }

        double bestVal;
        if (isMaxPlayer) {
            bestVal = Double.NEGATIVE_INFINITY;
            for (TablutTreeNode child : node.getChildren()) {
                double val = minmax(child, depth + 1, false, alpha, beta, heuristic, prioritizeShorterBranch);
                if (val != bestVal) {
                    bestVal = Math.max(val, bestVal);
                    if (verbose && (depth <= 1 || debugCounter % 1000 == 0 || DEBUG_PRINT_ALL) && DEBUG_PRINT_INNER) {
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
                double val = minmax(child, depth + 1, true, alpha, beta, heuristic, prioritizeShorterBranch);
                if (val != bestVal) {
                    bestVal = Math.min(val, bestVal);
                    if (verbose && (depth <= 1 || debugCounter % 1000 == 0) && DEBUG_PRINT_INNER) {
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

    private boolean equalsPrecision(double d1, double d2, double prec) {
        return Math.abs(d1 - d2) < prec;
    }
}