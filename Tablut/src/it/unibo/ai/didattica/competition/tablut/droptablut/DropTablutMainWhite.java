package it.unibo.ai.didattica.competition.tablut.droptablut;

import java.io.IOException;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.competition.tablut.client.DropTablutClient;

public class DropTablutMainWhite {

	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
		String[] array = new String[]{"WHITE"};
		DropTablutClient.main(array);
	}
	
}
