package it.unibo.ai.didattica.competition.tablut.droptablut;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IActionHandler;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IHeuristic;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IMinMax;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MinMaxAlphaBetaOpt implements IMinMax {
    static boolean DEBUG_MODE = true;
    static boolean DEBUG_PRINT_ALL = true;
    static boolean DEBUG_PRINT_INNER = false;

    public static final double BRANCH_LENGTH_WEIGHT = 0.001;
    public static final double RANDOM_WEIGHT = 0.00001;

    private boolean verbose = DEBUG_MODE && DTConstants.DEBUG_MODE;
    private int debugCounter = 0;

    private int maxDepth;
    private IActionHandler actionHandler;
    private TablutTreeNode bestNode;
    private boolean running; // per multithreading, visto che java non lascia fermare i thread

    public MinMaxAlphaBetaOpt(int maxDepth, IActionHandler actionHandler) {
        this.maxDepth = maxDepth;
        this.actionHandler = actionHandler;
        this.bestNode = null;
    }

    @Override
    public Action chooseAction(TablutTreeNode tree, IHeuristic heuristic) {
        if (verbose) {
            System.out.println("Inizio algoritmo alpha-beta...");
            System.out.println("Profondità: " + maxDepth);
            debugCounter = 0;
        }

        running = true;
        bestNode = null;
        double bestOverall = minmax(tree, 0, true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, heuristic);
        Action action = null;

        if (!running) {
            System.err.println("Thread di MinMaxAlphaBetaOpt terminato anticipatamente");
            return null;
        }

        if (verbose) {
            System.out.println(String.format("Alpha-beta eseguito in %d ricorsioni, bestOverall: %f", debugCounter, bestOverall));
            System.out.println(String.format("Nodo migliore: %s", bestNode));
        }

        if (bestNode != null) {
            action = bestNode.getAction();
            System.out.println(String.format("Scelta azione con punteggio %f: %s -> %s", bestOverall, action.getFrom(), action.getTo()));
        }

        if (action == null) {
            Random random = new Random();
            random.setSeed(System.currentTimeMillis());

            List<Action> validActions = actionHandler.getValidActions(tree.getState());

            action = validActions.get(random.nextInt(validActions.size()));
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

        // Se dall'esterno è stato chiamato stop,
        // chiudi ogni ramo il prima possibile
        if (!running) {
            return -1;
        }

        if (verbose) {
            debugCounter++;
            if ((depth <= 1 || debugCounter % 1000 == 0 || DEBUG_PRINT_ALL) && DEBUG_PRINT_INNER) {
                System.out.println(String.format("%d | Running for node %s",
                    depth, node.toStringTrace()));
            }
        }

        if (node.isLeaf() || depth >= maxDepth) {
            double val = heuristic.heuristic(node.getState());
            if (prioritizeShorterBranch)
                val = val - BRANCH_LENGTH_WEIGHT * depth; // preferisci rami più corti
//            val = val + (Math.random() * RANDOM_WEIGHT);
            
            if (verbose && (debugCounter % 1000 == 0 || DEBUG_PRINT_ALL) && DEBUG_PRINT_INNER) {
                System.out.println(String.format("--> %d | Ran heuristic for %s: %f", depth, node, val));
            }

            return val;
        }

        double bestVal;
        if (isMaxPlayer) {
            bestVal = Double.NEGATIVE_INFINITY;
            List<Action> validActions = actionHandler.getValidActions(node.getState());
            for (Action action : validActions) {
                State state = actionHandler.applyAction(node.getState(), action);
                TablutTreeNode child = TablutTreeNode.createNoChildren(state, action);
                double val = minmax(child, depth + 1, false, alpha, beta, heuristic, prioritizeShorterBranch);

                // Se dall'esterno è stato chiamato stop,
                // chiudi ogni ramo il prima possibile
                if (!running) {
                    return -1;
                }

                if (val != bestVal) {
                    bestVal = Math.max(val, bestVal);
                    if (val == bestVal && depth == 0) {
                        bestNode = child;
                        if (verbose && debugCounter % 1000 == 0 && DEBUG_PRINT_INNER) {
                            System.out.println(String.format("%d | Cambiato bestNode a %s (bestVal %f)",
                                    depth, node, bestVal));
                        }
                    }
                }
                alpha = Math.max(alpha, bestVal);
                if (beta <= alpha) 
                    break;
            }
            return bestVal;
        } else {
            bestVal = Double.POSITIVE_INFINITY;
            List<Action> validActions = actionHandler.getValidActions(node.getState());
            for (Action action : validActions) {
                State state = actionHandler.applyAction(node.getState(), action);
                TablutTreeNode child = TablutTreeNode.createNoChildren(state, action);
                double val = minmax(child, depth + 1, true, alpha, beta, heuristic, prioritizeShorterBranch);

                // Se dall'esterno è stato chiamato stop,
                // chiudi ogni ramo il prima possibile
                if (!running) {
                    return -1;
                }

                if (val != bestVal) {
                    bestVal = Math.min(val, bestVal);
                    if (val == bestVal && depth == 0) {
                        bestNode = child;
                        if (verbose && debugCounter % 1000 == 0 && DEBUG_PRINT_INNER) {
                            System.out.println(String.format("%d | Cambiato bestNode a %s (bestVal %f)",
                                    depth, node, bestVal));
                        }
                    }
                }
                beta = Math.min(beta, bestVal);
                if (beta <= alpha) 
                    break;
            }
            return bestVal;
        }
    }

    public void stop() {
        this.running = false;
    }

    private boolean equalsPrecision(double d1, double d2, double prec) {
        return Math.abs(d1 - d2) < prec;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public IActionHandler getActionHandler() {
        return actionHandler;
    }

    public void setActionHandler(IActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }
}