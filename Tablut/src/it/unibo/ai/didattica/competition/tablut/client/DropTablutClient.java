package it.unibo.ai.didattica.competition.tablut.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.droptablut.TablutTreeNode;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IApplyAction;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.ICreateTree;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IListActions;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IMinMax;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IHeuristic;

public class DropTablutClient extends TablutClient {
    private IListActions actionLister;
    private ICreateTree treeCreator;
    private IHeuristic heuristic;
    private IMinMax minMaxer;
    private IApplyAction actionApplier;
    private int depth;


    public DropTablutClient(String player, String name, 
            int depth,
            IListActions actionLister, ICreateTree treeCreator, IHeuristic heuristic, 
            IMinMax minMaxer, IApplyAction actionApplier)
            throws UnknownHostException, IOException {
        super(player, name);
        this.depth = depth;
        this.actionLister = actionLister;
        this.treeCreator = treeCreator;
        this.heuristic = heuristic;
        this.minMaxer = minMaxer;
        this.actionApplier = actionApplier;
    }


    @Override
    public void run() {
        System.out.println("You are player " + this.getPlayer().toString() + "!");
		Action action;
		try {
			this.declareName();
		} catch (Exception e) {
			e.printStackTrace();
		}

        Turn myColor = this.getPlayer();
        Turn otherColor = myColor.equals(Turn.WHITE) ? Turn.BLACK : Turn.WHITE;
        Turn myWin = myColor.equals(Turn.WHITE) ? Turn.WHITEWIN : Turn.BLACKWIN;
        Turn otherWin = myColor.equals(Turn.WHITE) ? Turn.BLACKWIN : Turn.WHITEWIN;

        System.out.println("You are player " + myColor.toString() + "!");
        while (true) {
            try {
                // Aspetta stato dal server
                this.read();

                System.out.println("Current state:");
                System.out.println(this.getCurrentState().toString());

                if (this.getCurrentState().getTurn().equals(myColor)) {
                    System.out.println("Our turn!");
                    
                    TablutTreeNode tree = treeCreator.generateTree(this.getCurrentState(), depth, actionLister, actionApplier);
                    action = minMaxer.chooseAction(tree, heuristic);

                    this.write(action);
                } else if (this.getCurrentState().getTurn().equals(otherColor)) {
                    System.out.println("Waiting for your opponent move... ");
                } else if (this.getCurrentState().getTurn().equals(myWin)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                } else if (this.getCurrentState().getTurn().equals(otherWin)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                } else if (this.getCurrentState().getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }



    public IListActions getActionLister() {
        return this.actionLister;
    }

    public void setActionLister(IListActions actionLister) {
        this.actionLister = actionLister;
    }

    public ICreateTree getTreeCreator() {
        return this.treeCreator;
    }

    public void setTreeCreator(ICreateTree treeCreator) {
        this.treeCreator = treeCreator;
    }

    public IHeuristic getHeuristic() {
        return this.heuristic;
    }

    public void setHeuristic(IHeuristic heuristic) {
        this.heuristic = heuristic;
    }

    public IMinMax getMinMaxer() {
        return this.minMaxer;
    }

    public void setMinMaxer(IMinMax minMaxer) {
        this.minMaxer = minMaxer;
    }

    public IApplyAction getActionApplier() {
        return this.actionApplier;
    }

    public void setActionApplier(IApplyAction actionApplier) {
        this.actionApplier = actionApplier;
    }

    public int getDepth() {
        return this.depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

}
