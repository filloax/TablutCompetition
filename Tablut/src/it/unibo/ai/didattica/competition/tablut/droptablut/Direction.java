package it.unibo.ai.didattica.competition.tablut.droptablut;

public enum Direction {
    LEFT(0),
    RIGHT(1),
    UP(2),
    DOWN(3),
    ;

    private final int val;

    private Direction(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}