package it.unibo.ai.didattica.competition.tablut.droptablut;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class TablutTreeNode {
    private State state;
    private List<TablutTreeNode> children;
    private Optional<Double> value;

    public TablutTreeNode(State state, List<TablutTreeNode> children) {
        this.state = state;
        this.children = children;
    }

    public TablutTreeNode(State state) {
        this(state, new ArrayList<>());
    }

    public State getState() {
        return this.state;
    }

    public List<TablutTreeNode> getChildren() {
        return this.children;
    }

    public double getValue() {
        return this.value.get();
    }

    public void setValue(double value) {
        this.value = Optional.of(value);
    }

}
