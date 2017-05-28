package com.example.demo.models;

import com.example.demo.GameOutcome;
import com.example.demo.utils.Logger;
import org.springframework.web.socket.TextMessage;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by mkejji on 27/05/2017.
 */
public class Game {

	private String                                      className = "Game";
    private long                                        id = 0;
    private List<Player>                                playerList = new CopyOnWriteArrayList<>();
    private HashMap<Integer, HashMap<Integer, String>>  grid = new HashMap();
    private String                                      turn = "";
    private int                                         score_X = 0;
    private int                                         score_O = 0;

    public Game(long id) {
        this.id = id;
        init();
    }

    public void init() {
	    for (Integer i = 0; i < 3; i++) {
		    grid.put(i, new HashMap<>());
		    for (Integer j = 0; j < 3; j++) {
			    grid.get(i).put(j, "");
		    }
	    }
    }
    public void addPlayer(Player player) {
        playerList.add(player);
    }

    public long getId() {
        return id;
    }

    public int getScore_X() {
    	return score_X;
    }

    public int getScore_O() {
        return score_O;
    }

    public void debug() {
    	for (Integer i = 0; i < 3; i++) {
    		for (Integer j = 0; j < 3; j++) {
    			try {
				    Logger.d("r" + i.toString() + "c" + j.toString(), grid.get(i).get(j));
			    } catch (Exception e) {
    				e.printStackTrace();
			    }
		    }
	    }
    }
	public boolean hasPlayerSession(String sessid) {
    	for (Player p : playerList) {
    		if (p.getSession().getId().equals(sessid))
    			return true;
	    }
	    return false;
	}

	public void sendToPlayers(TextMessage message) {
        for (Player p: playerList) {
        	try {
        		p.getSession().sendMessage(message);
	        } catch (IOException e ) {
        		e.printStackTrace();
	        }
        }
	}

    public List<Player> getPlayerList() {
        return playerList;
    }

    public boolean addMove(Move m) {
	    Integer y = m.getRow();
	    Integer x = m.getCol();
	    String cell = grid.get(y).get(x);
	    if (m.getPiece().toUpperCase().equals(this.turn) && cell.equals("")) {
		    grid.get(y).put(x, this.turn);
		    this.turn = (this.turn.equals("X")) ? "O" : "X";
		    Logger.d("turn is now", this.turn);
		    return true;
	    }
	    Logger.d(className, "Not executing turn. Turn is " + turn);
	    return false;
    }

    public GameOutcome checkGameOutcome() {
        int takenSpots = 0;
        for (Integer i = 0; i < 3; i++) {
            String line = String.format("%s%s%s", grid.get(i).get(0), grid.get(i).get(1), grid.get(i).get(2));
            String col = String.format("%s%s%s", grid.get(0).get(i), grid.get(1).get(i), grid.get(2).get(i));
	        int lineLen = line.length();
	        int colLen = col.length();
            Logger.d("checkGameOutcome", String.format("Line: %s (%d) - Col: %s (%d)", line, lineLen, col, colLen));
            takenSpots += lineLen;
            if (colLen < 3 && lineLen < 3)
                continue;
            if (line.toUpperCase().equals("OOO") || col.toUpperCase().equals("OOO")) {
            	score_O++;
	            return GameOutcome.O_WIN;
            }
            if (line.toUpperCase().equals("XXX") || col.toUpperCase().equals("XXX")) {
            	score_X++;
	            return GameOutcome.X_WIN;
            }
        }
        String slash = String.format("%s%s%s", grid.get(0).get(2), grid.get(1).get(1), grid.get(2).get(0));
        String antislash = String.format("%s%s%s", grid.get(0).get(0), grid.get(1).get(1), grid.get(2).get(2));
        if (slash.toUpperCase().equals("OOO") || antislash.toUpperCase().equals("OOO")) {
        	score_O++;
	        return GameOutcome.O_WIN;
        }
        if (slash.toUpperCase().equals("XXX") || antislash.toUpperCase().equals("XXX")) {
        	score_X++;
	        return GameOutcome.X_WIN;
        }
        return (takenSpots == 9) ? GameOutcome.DRAW : GameOutcome.PLAYING;
    }

    public void start() {
    	this.turn = "O";
    	Logger.d(className, String.format("Game %d starting...", this.id));
        for (Player p : playerList) {
            try {
            	Move m = new Move("start", "", 0, 0);
                p.getSession().sendMessage(new TextMessage(m.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
