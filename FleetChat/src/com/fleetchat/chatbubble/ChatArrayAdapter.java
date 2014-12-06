package com.fleetchat.chatbubble;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fleetchat.ChatActivity;
import com.fleetchat.MainActivity;
import com.fleetchat.R;

public class ChatArrayAdapter extends ArrayAdapter {

	private ChatActivity ca;
	private TextView chatText;
	private TextView userName;
	private TextView userTime;
	private TextView clientName;
	private TextView clientTime;
	private List chatMessageList = new ArrayList();
	private LinearLayout singleMessageContainer;
	public static ImageView clientPortrait;

	public void add(ChatMessage object) {
		chatMessageList.add(object);
		super.add(object);
	}

	public ChatArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.chatMessageList.size();
	}

	public ChatMessage getItem(int index) {
		return (ChatMessage) this.chatMessageList.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.activity_chat_singlemessage,
					parent, false);
		}
		singleMessageContainer = (LinearLayout) row
				.findViewById(R.id.singleMessageContainer);
		ChatMessage chatMessageObj = getItem(position);
		chatText = (TextView) row.findViewById(R.id.singleMessage);
		userName = (TextView) row.findViewById(R.id.bubble_usr_name);
		userTime = (TextView) row.findViewById(R.id.bubble_usr_time);
		clientName = (TextView) row.findViewById(R.id.bubble_client_name);
		clientTime = (TextView) row.findViewById(R.id.bubble_client_time);
		clientPortrait = new ImageView(getContext());
		clientPortrait = (ImageView) row.findViewById(R.id.bubble_client_por);
		chatText.setText(chatMessageObj.message);
		chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_a
				: R.drawable.bubble_b);
		userName.setText(chatMessageObj.left ? "" : "Me");
		userName.setTextColor(Color.BLACK);
		userTime.setText(chatMessageObj.left ? "" : chatMessageObj.time);
		userTime.setTextColor(Color.BLACK);
		clientName.setText(chatMessageObj.left ? chatMessageObj.user : "");
		clientTime.setText(chatMessageObj.left ? chatMessageObj.time : "");
		clientName.setTextColor(Color.BLACK);
		clientTime.setTextColor(Color.BLACK);
		if (chatMessageObj.left) {
			clientPortrait.setVisibility(View.VISIBLE);
			setPortrait(chatMessageObj.porID);
		} else {
			clientPortrait.setVisibility(View.GONE);
		}
		singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT
				: Gravity.RIGHT);
		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory
				.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	private void setPortrait(String porID) {
		int portraitID = Integer.valueOf(porID);
		Log.i("porID", porID);
		switch (portraitID) {
		case 00:
			clientPortrait.setImageDrawable(getContext().getResources()
					.getDrawable(R.drawable.porknown));
			break;
		case 11:
			clientPortrait.setImageDrawable(getContext().getResources()
					.getDrawable(R.drawable.por011));
			break;
		case 12:
			clientPortrait.setImageDrawable(getContext().getResources()
					.getDrawable(R.drawable.por012));
			break;
		case 13:
			clientPortrait.setImageDrawable(getContext().getResources()
					.getDrawable(R.drawable.por013));
			break;
		case 14:
			clientPortrait.setImageDrawable(getContext().getResources()
					.getDrawable(R.drawable.por014));
			break;
		case 21:
			clientPortrait.setImageDrawable(getContext().getResources()
					.getDrawable(R.drawable.por021));
			break;
		case 22:
			clientPortrait.setImageDrawable(getContext().getResources()
					.getDrawable(R.drawable.por022));
			break;
		case 23:
			clientPortrait.setImageDrawable(getContext().getResources()
					.getDrawable(R.drawable.por023));
			break;
		case 24:
			clientPortrait.setImageDrawable(getContext().getResources()
					.getDrawable(R.drawable.por024));
			break;
		case 31:
			clientPortrait.setImageDrawable(getContext().getResources()
					.getDrawable(R.drawable.por031));
			break;
		case 32:
			clientPortrait.setImageDrawable(getContext().getResources()
					.getDrawable(R.drawable.por032));
			break;
		case 33:
			clientPortrait.setImageDrawable(getContext().getResources()
					.getDrawable(R.drawable.por033));
			break;
		case 34:
			clientPortrait.setImageDrawable(getContext().getResources()
					.getDrawable(R.drawable.por034));
			break;
		}
	}
}
