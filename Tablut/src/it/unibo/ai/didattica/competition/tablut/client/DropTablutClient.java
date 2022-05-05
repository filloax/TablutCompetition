package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.droptablut.*;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IActionHandler;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.ICreateTree;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IHeuristic;
import it.unibo.ai.didattica.competition.tablut.droptablut.interfaces.IMinMax;

public class DropTablutClient extends TablutClient {
    private IActionHandler actionHandler;
    private ICreateTree treeCreator;
    private IHeuristic heuristic;
    private IMinMax minMaxer;
    private int depth;


    public DropTablutClient(String player, String name,
                            int depth,
                            IActionHandler actionHandler, ICreateTree treeCreator, IHeuristic heuristic,
                            IMinMax minMaxer)
            throws UnknownHostException, IOException {
        super(player, name);
        this.depth = depth;
        this.actionHandler = actionHandler;
        this.treeCreator = treeCreator;
        this.heuristic = heuristic;
        this.minMaxer = minMaxer;
    }
    public DropTablutClient(String player, String name, int timeout, String ipAddress,
                            int depth,
                            IActionHandler actionHandler, ICreateTree treeCreator, IHeuristic heuristic,
                            IMinMax minMaxer)
            throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);
        this.depth = depth;
        this.actionHandler = actionHandler;
        this.treeCreator = treeCreator;
        this.heuristic = heuristic;
        this.minMaxer = minMaxer;
    }


    @Override
    public void run() {
        //System.out.println("You are player " + this.getPlayer().toString() + "!");
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

                long timeBefore = System.currentTimeMillis();

                System.out.println("Current state:");
                System.out.println(this.getCurrentState().toString());

                if (this.getCurrentState().getTurn().equals(myColor)) {
                    System.out.println("Our turn!");


                    TablutTreeNode tree = treeCreator.generateTree(this.getCurrentState(), depth, actionHandler);
                    action = minMaxer.chooseAction(tree, heuristic);

                    System.out.println("Mossa scelta: " + action);

                    this.write(action);

                    long timeAfter = System.currentTimeMillis();
                    System.out.println("Tempo impiegato per questo turno: " + ((timeAfter - timeBefore) * 0.001) + "s");
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

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {

		if (args.length == 0) {
			System.out.println("You must specify which player you are (WHITE or BLACK)!");
			System.exit(-1);
		}
        
        int timeout = 60;
        String address = "localhost";
        int depth = 3;

        if (args.length >= 2){
            try {
                timeout=Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.print("Il secondo argomento deve essere un intero");
                System.exit(-1);
            }
        }
        if (args.length >= 3) {
            address=args[2];
        }
        if (args.length >= 4) {
            try {
                depth = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                System.err.print("Il secondo argomento deve essere un intero");
                System.exit(-1);
            }
        }

        System.out.println("Selected this: " + args[0]);

        Turn color = ((args[0].toUpperCase().equals("WHITE")) ? Turn.WHITE : Turn.BLACK);

        DropTablutClient client = new DropTablutClient(
            args[0], 
            "DropTablut",
            timeout,
            address,
            depth,
            new ActionHandler(),
            new TreeCreator(),
            new DropTablutHeuristic(color),
            new MinMaxAlphaBeta()
        );

        client.run();
        
	}

    public IActionHandler getActionHandler() {
        return this.actionHandler;
    }

    public void setActionHandler(IActionHandler actionHandler) {
        this.actionHandler = actionHandler;
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

    public int getDepth() {
        return this.depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

}
