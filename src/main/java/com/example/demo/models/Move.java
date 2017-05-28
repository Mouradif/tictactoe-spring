package com.example.demo.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mkejji on 28/05/2017.
 */
public class Move {
	private String  type = "";
	private String  piece = "";
	private Integer row = null;
	private Integer col = null;

	public Move(String type, String piece, Integer row, Integer col) {
		this.type = type;
		this.piece = piece;
		this.col = col;
		this.row = row;
	}

	public String getPiece() {
		return piece;
	}

	public String getType() {
		return type;
	}

	public Integer getRow() {
		return row;
	}

	public Integer getCol() {
		return col;
	}

	public String toString() {
		JSONObject  o = new JSONObject();

		try {
			o.put("type", type);
			o.put("piece", piece);
			o.put("col", col);
			o.put("row", row);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o.toString();
	}
}
