package com.fleetchat.chatbubble;

public class ChatMessage {
	public boolean left;
	public String message;
	public String user;
	public String time;

	public ChatMessage(boolean left, String message,String user, String time) {
		super();
		this.left = left;
		this.message = message;
		this.user = user;
		this.time = time;
	}
}
