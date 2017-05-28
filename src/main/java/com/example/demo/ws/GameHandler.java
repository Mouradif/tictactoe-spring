package com.example.demo.ws;

import com.example.demo.GameOutcome;
import com.example.demo.models.Game;
import com.example.demo.models.Move;
import com.example.demo.models.Player;
import com.example.demo.utils.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by mkejji on 27/05/2017.
 */
public class GameHandler extends TextWebSocketHandler {

	private List<Player>    playerList = new CopyOnWriteArrayList<>();
	private List<Game>      gameList = new CopyOnWriteArrayList<>();
	private long            playerId = 0;
	private long            gameId = 0;
	private final String    className = "GameHandler";

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		Player player = new Player(++playerId, session);
		playerList.add(player);
		for (Game g: gameList) {
			long size = g.getPlayerList().size();
			if (size == 1) {
				player.setPiece("X");
				Logger.d(className, "Player X connected");
				g.addPlayer(player);
				g.start();
				return;
			}
		}
		player.setPiece("O");
		Logger.d(className, "Player O connected");
		Game game = new Game(++gameId);
		game.addPlayer(player);
		gameList.add(game);
	}

	public Player getPlayerBySessId(String id) {
		for (Player p : playerList) {
			if (p.getSession().getId().equals(id))
				return p;
		}
		return null;
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		boolean foundGame = false;
		Logger.d(className, message.getPayload());
		Player sender = getPlayerBySessId(session.getId());
		if (sender == null) {
			Logger.d(className, "Couldn't find sender");
		}
		else {
			for (Game g : gameList) {
				if (g.hasPlayerSession(session.getId())) {
					foundGame = true;
					Logger.d(className, "Found Game");
					try {
						JSONObject o = new JSONObject(message.getPayload());
						Move m = new Move("move", sender.getPiece(), o.getInt("r"), o.getInt("c"));
						if (g.addMove(m)) {
							g.sendToPlayers(new TextMessage(m.toString()));
							if (g.checkGameOutcome() != GameOutcome.PLAYING) {
								m = new Move("win", "", g.getScore_O(), g.getScore_X());
								g.sendToPlayers(new TextMessage(m.toString()));
								g.init();
							}
						} else {
							Logger.d(className, "Can't move");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				}
			}
			if (!foundGame)
				Logger.d(className, "Couldn't find game");
		}
	}
}
