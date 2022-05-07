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
    private int reducedDepthTurns;
    private int timeoutTurns;
    private int turnCounter;

    public static boolean USE_OPT = true;
    public static boolean CHECK_FOR_TIMEOUT = true;

    public DropTablutClient(String player, String name,
                            int depth,
                            IActionHandler actionHandler, ICreateTree treeCreator, IHeuristic heuristic,
                            IMinMax minMaxer,
                            int reducedDepthTurns)
            throws UnknownHostException, IOException {
        this(player, name, 60, "localhost",
                depth, actionHandler, treeCreator, heuristic,
                minMaxer, reducedDepthTurns);
    }
    public DropTablutClient(String player, String name, int timeout, String ipAddress,
                            int depth,
                            IActionHandler actionHandler, ICreateTree treeCreator, IHeuristic heuristic,
                            IMinMax minMaxer,
                            int reducedDepthTurns)
            throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);
        this.depth = depth;
        this.actionHandler = actionHandler;
        this.treeCreator = treeCreator;
        this.heuristic = heuristic;
        this.minMaxer = minMaxer;
        this.reducedDepthTurns = reducedDepthTurns;
        this.turnCounter = 0;
        this.timeoutTurns = 0;
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

        System.out.println("You are player " + myColor.toString() + ", timeout is " + timeout + "!");
        while (true) {
            try {
                // Aspetta stato dal server
                this.read();

                long timeBefore = System.currentTimeMillis();

                System.out.println("Current state:");
                System.out.println(this.getCurrentState().toString());

                if (this.getCurrentState().getTurn().equals(myColor)) {
                    System.out.println("Our turn!");

                    turnCounter++;
                    System.out.println("Turno numero " + turnCounter);

                    int thisDepth = depth;
                    if (reducedDepthTurns > 0) {
                        thisDepth--;
                        reducedDepthTurns--;
                    }

                    if (minMaxer instanceof MinMaxAlphaBetaOpt) {
                        if (DTConstants.DEBUG_MODE) {
                            System.out.println("Using optimized!");
                        }

                        ((MinMaxAlphaBetaOpt) minMaxer).setMaxDepth(thisDepth);
                        TablutTreeNode firstNode = TablutTreeNode.createNoChildren(this.getCurrentState(), null);

                        if (CHECK_FOR_TIMEOUT) {
                            MinMaxerThread thread = new MinMaxerThread(firstNode, minMaxer, heuristic);
                            thread.start();

                            action = null;

                            // Aspetta al massimo 85% del timeout (che è in secondi)
                            while (System.currentTimeMillis() - timeBefore < timeout * 0.85 * 1000) {
                                try {
                                    Thread.sleep(500);
                                    if (!thread.isAlive()) {
                                        if (DTConstants.DEBUG_MODE) {
                                            System.out.println("Trovato risultato durante il check di tempo massimo!");
                                        }
                                        // Esecuzione terminata entro il tempo limite, salva azione
                                        action = thread.getResult();
                                        break;
                                    }

                                } catch(InterruptedException e) {
                                    e.printStackTrace();
                                    break;
                                }
                            }

                            // Azione trovata, resetta counter di turni di timeout di fila
                            if (action != null) {
                                timeoutTurns = 0;
                            } else {
                                // Ferma il minmaxer, impostando la flag interna a false
                                ((MinMaxAlphaBetaOpt) minMaxer).stop();

                                System.err.println(String.format("Avvicinato troppo al timeout dopo %.2fs (o altro errore), provo a depth bassa (3)",
                                        (System.currentTimeMillis() - timeBefore) * 0.001f));

                                ((MinMaxAlphaBetaOpt) minMaxer).setMaxDepth(3);
                                action = minMaxer.chooseAction(firstNode, heuristic);

                                if (timeoutTurns >= 3) {
                                    if (depth > 3) {
                                        System.err.println("Avvicinato troppo al timeout 3 volte, riduco depth permanentemente");
                                        depth--;
                                    } else {
                                        System.err.println("Non posso abbassare la depth, è già troppo bassa (3)!");
                                    }
                                    timeoutTurns = 0;
                                } else {
                                    timeoutTurns++;
                                }
                            }
                        } else {
                            action = minMaxer.chooseAction(firstNode, heuristic);
                        }
                    } else if (minMaxer instanceof MinMaxAlphaBeta) {
                        if (DTConstants.DEBUG_MODE) {
                            System.out.println("Using not optimized!");
                        }

                        TablutTreeNode tree = treeCreator.generateTree(this.getCurrentState(), thisDepth, actionHandler);
                        action = minMaxer.chooseAction(tree, heuristic);
                    } else {
                        throw new IllegalStateException("argh");
                    }

                    System.out.println("Mossa scelta: " + action);

                    this.write(action);

                    long timeAfter = System.currentTimeMillis();
                    System.out.println("Tempo impiegato per questo turno: " + ((timeAfter - timeBefore) * 0.001) + "s");
                } else if (this.getCurrentState().getTurn().equals(otherColor)) {
                    System.out.println("Waiting for your opponent move... ");
                } else if (this.getCurrentState().getTurn().equals(myWin)) {
                    System.out.println("YOU WIN!");
                    System.out.println("In turn num: " + turnCounter);
                    System.exit(0);
                } else if (this.getCurrentState().getTurn().equals(otherWin)) {
                    System.out.println("YOU LOSE!");
                    System.out.println("In turn num: " + turnCounter);
                    System.exit(0);
                } else if (this.getCurrentState().getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.out.println("In turn num: " + turnCounter);
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
        int depth = 5;
        int reducedDepthTurns = 0;

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

        IMinMax minmaxer;

        if (USE_OPT) {
            minmaxer = new MinMaxAlphaBetaOpt(depth, new ActionHandler());
        } else {
            minmaxer = new MinMaxAlphaBeta();
        }

        DropTablutClient client = new DropTablutClient(
            args[0], 
            "DropTablut",
            timeout,
            address,
            depth,
            new ActionHandler(),
            new TreeCreator(),
            new DropTablutHeuristic(color),
            minmaxer,
            reducedDepthTurns
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

    private class MinMaxerThread extends Thread {
        TablutTreeNode tree;
        IMinMax minmaxer;
        IHeuristic heuristic;
        Action result;

        public MinMaxerThread(TablutTreeNode tree, IMinMax minmaxer, IHeuristic heuristic) {
            this.tree = tree;
            this.minmaxer = minmaxer;
            this.heuristic = heuristic;
            this.result = null;
        }

        @Override
        public void run() {
            result = minmaxer.chooseAction(tree, heuristic);
        }

        public Action getResult() {
            return result;
        }
    }
}
