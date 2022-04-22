package it.unibo.ai.didattica.competition.tablut.droptablut;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class TablutTreeNode {
    private Action action;
    private State state;
    private List<TablutTreeNode> children;
    private Optional<Double> value;

    public TablutTreeNode(State state, Action action, List<TablutTreeNode> children) {
        this.state = state;
        this.action = action;
        this.children = children;
    }

    public TablutTreeNode(State state, Action action) {
        this(state, action, new ArrayList<>());
    }

    public State getState() {
        return this.state;
    }

    public Action getAction() {
        return this.action;
    }

    public List<TablutTreeNode> getChildren() {
        return this.children;
    }

    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    public boolean hasValue() {
        return this.value.isPresent();
    }

    public double getValue() {
        return this.value.get();
    }

    public void setValue(double value) {
        this.value = Optional.of(value);
    }

}
