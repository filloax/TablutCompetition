package it.unibo.ai.didattica.competition.tablut.droptablut;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IHeuristic;
import org.junit.Before;
import org.junit.Test;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

import java.util.*;

import static org.junit.Assert.*;


public class AlphaBetaTest {
    MinMaxAlphaBeta minmaxer;
    IHeuristic heuristic;
    Map<State, Integer> stateHeuristicMap;
    Map<Integer, State> heuristicStateMap;
    List<Action> checkActions;

    @Before
    public void before() {
        MinMaxAlphaBeta.DEBUG_MODE = true;
        MinMaxAlphaBeta.DEBUG_PRINT_ALL = true;
        MinMaxAlphaBeta.DEBUG_PRINT_INNER = true;

        minmaxer = new MinMaxAlphaBeta();
        stateHeuristicMap = new HashMap<>();
        heuristicStateMap = new HashMap<>();
        checkActions = new ArrayList<>();

        createCheckActions();
        createMockStates();

        heuristic = new IHeuristic() {
            @Override
            public float heuristic(State state) {
                return stateHeuristicMap.get(state);
            }
        };
    }

    // Usa stati mock assegnati a valori fissi per euristica
    public void createMockStates() {
        for (int i = 0; i < 10; i++) {
            State state = new StateTablut();
            stateHeuristicMap.put(state, i);
            heuristicStateMap.put(i, state);
        }
    }

    public void createCheckActions() {
        checkActions.add(0, new Action("A5", "B5", Turn.WHITE));
        checkActions.add(1, new Action("A6", "B6", Turn.WHITE));
        checkActions.add(2, new Action("A7", "B7", Turn.WHITE));
    }

    private TablutTreeNode createNodeWithHeuristicValue(int val) {
        Action placeholderAction = new Action("A1", "A2", Turn.WHITE);
        return new TablutTreeNode(heuristicStateMap.get(val), placeholderAction);
    }

    public TablutTreeNode createTestTree() {
        State placeholderState = new StateTablut();
        Action placeholderAction = new Action("A1", "A2", Turn.WHITE);

        // Valori presi dall'esempio di alpha-beta
        // nelle slide, profondità 2
        List<Integer> values = new ArrayList<>();
        values.add(8);
        values.add(9);
        values.add(4);
        values.add(5);
        values.add(9);
        values.add(6);
        values.add(3);
        values.add(9);
        values.add(8);

        // Contorto ma facilmente copincollabile
        List<TablutTreeNode> leafNodes = new ArrayList<TablutTreeNode>(9);
        for (int i = 0; i < 9; i++) {
            leafNodes.add(createNodeWithHeuristicValue(values.get(i)));
        }

        TablutTreeNode tree  = new TablutTreeNode(placeholderState, placeholderAction);
        TablutTreeNode node1 = new TablutTreeNode(placeholderState, checkActions.get(0));
        TablutTreeNode node2 = new TablutTreeNode(placeholderState, checkActions.get(1));
        TablutTreeNode node3 = new TablutTreeNode(placeholderState, checkActions.get(2));

        tree.getChildren().add(node1);
        tree.getChildren().add(node2);
        tree.getChildren().add(node3);

        node1.getChildren().add(leafNodes.get(0));
        node1.getChildren().add(leafNodes.get(1));
        node1.getChildren().add(leafNodes.get(2));
        node2.getChildren().add(leafNodes.get(3));
        node2.getChildren().add(leafNodes.get(4));
        node2.getChildren().add(leafNodes.get(5));
        node3.getChildren().add(leafNodes.get(6));
        node3.getChildren().add(leafNodes.get(7));
        node3.getChildren().add(leafNodes.get(8));

        return tree;
    }

    @Test
    public void basicTest() {
        TablutTreeNode tree = createTestTree();
        // Non chooseAction visto che questo albero non ha azioni, è di test
        // chiamato come viene chiamato in MinMaxAlphaBeta, con max = false
        // per seguire l'esempio delle slide
        double bestOverall = minmaxer.minmax(tree, 0, true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, heuristic, false);
        assertEquals(5.0, bestOverall, 0.01);
    }

    @Test
    public void testShortBranch() {
        TablutTreeNode tree = createTestTree();

        TablutTreeNode nodeToExtend = tree.getChildren().get(2).getChildren().get(0);
        nodeToExtend.getChildren().add(createNodeWithHeuristicValue(5));
        nodeToExtend.getChildren().add(createNodeWithHeuristicValue(1));
        nodeToExtend.getChildren().add(createNodeWithHeuristicValue(1));

        // Non chooseAction visto che questo albero non ha azioni, è di test
        // chiamato come viene chiamato in MinMaxAlphaBeta, con max = false
        // per seguire l'esempio delle slide
        double bestOverall = minmaxer.minmax(tree, 0, true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, heuristic, true);
        assertEquals(5 - MinMaxAlphaBeta.BRANCH_LENGTH_WEIGHT * 2, bestOverall, 0.0001);
    }

    @Test
    public void testChooseAction() {
        TablutTreeNode tree1 = createTestTree();
        // Non chooseAction visto che questo albero non ha azioni, è di test
        // chiamato come viene chiamato in MinMaxAlphaBeta, con max = false
        // per seguire l'esempio delle slide
        Action chosenAction = minmaxer.chooseAction(tree1, heuristic);
        assertEquals(checkActions.get(1), chosenAction);

        TablutTreeNode tree2 = createTestTree();
        TablutTreeNode nodeToExtend = tree2.getChildren().get(2).getChildren().get(0);
        nodeToExtend.getChildren().add(createNodeWithHeuristicValue(5));
        nodeToExtend.getChildren().add(createNodeWithHeuristicValue(1));
        nodeToExtend.getChildren().add(createNodeWithHeuristicValue(1));

        Action chosenAction2 = minmaxer.chooseAction(tree2, heuristic);
        assertEquals(checkActions.get(1), chosenAction2);
    }
}
