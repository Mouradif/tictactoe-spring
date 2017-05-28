package com.example.demo.models;

import org.springframework.web.socket.WebSocketSession;

/**
 * Created by mkejji on 27/05/2017.
 */
public class Player {

    private long                id = 0;
    private WebSocketSession    session;
    private String              piece = "";

    public Player(long id, WebSocketSession session) {
        this.id = id;
        this.session = session;
    }

	public void setPiece(String p) {
    	this.piece = p;
	}

    public long getId() {
        return id;
    }

	public String getPiece() {
		return piece;
	}

    public WebSocketSession getSession() {
        return session;
    }
}
