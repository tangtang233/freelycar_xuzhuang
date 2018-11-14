package com.geariot.platform.freelycar.utils;

import com.geariot.platform.freelycar.entities.Card;

public class BuyCardParamWrapper {
	private int clientId;
	private Card card;
	public Card getCard() {
		return card;
	}
	public int getClientId() {
		return clientId;
	}
	public void setCard(Card card) {
		this.card = card;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
}
