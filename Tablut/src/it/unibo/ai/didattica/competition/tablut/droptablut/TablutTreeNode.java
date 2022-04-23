package it.unibo.ai.didattica.competition.tablut.droptablut;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class TablutTreeNode {
    private Action action;
    private State state;
    private List<TablutTreeNode> children;
    private Optional<Double> value;
    private TablutTreeNode parent;

    public TablutTreeNode(State state, Action action, List<TablutTreeNode> children, TablutTreeNode parent) {
        this.state = state;
        this.action = action;
        this.children = children;
        this.parent = parent;
        this.value = Optional.empty();
    }

    public TablutTreeNode(State state, Action action, TablutTreeNode parent) {
        this(state, action, new ArrayList<>(), parent);
    }

    public TablutTreeNode(State state, Action action, List<TablutTreeNode> children) {
        this(state, action, children, null);
    }

    public TablutTreeNode(State state, Action action) {
        this(state, action, new ArrayList<>(), null);
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

    @Override
    public String toString() {
        String base = action != null ? String.format("%s -> %s", action.getFrom(), action.getTo()) : "origin";
        if (state.getTurn() == Turn.BLACKWIN)
            base = base + " !B!";
        else if (state.getTurn() == Turn.WHITEWIN)
            base = base + " !W!";
        else if (state.getTurn() == Turn.DRAW)
            base = base + " !D!";
        return "(" + base + ")";
    }

    public String toStringTrace() {
        if (parent == null)
            return toString();
        return parent.toStringTrace() + " ==> " + toString();
    }
}
