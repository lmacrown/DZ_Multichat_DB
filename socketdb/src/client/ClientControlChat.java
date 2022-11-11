package client;

import java.util.Scanner;

import org.json.JSONObject;

import client.ClientControlMember.ExitListener;
import client.ClientControlMember.LoginListener;
import member.Member;

public class ClientControlChat extends ChatClient {
	Member member;
	private Scanner scanner;
	static int roomNumber;

	static interface EnterRoomListener {
		void afterEnter();
	}

	static interface LogOutListener {
		void afterLogOut();
	}

	EnterRoomListener enterRoomListener = null;
	LogOutListener logOutListener = null;

	public ClientControlChat(Scanner scanner, Member member, EnterRoomListener enterRoomListener,
			LogOutListener logOutListener) {
		this.scanner = scanner;
		this.member = member;
		this.enterRoomListener = enterRoomListener;
		this.logOutListener = logOutListener;
	}

	public void chatCreate() {
		try {

			String chatRoomName;
			System.out.println("생성할 채팅방 이름: ");
			chatRoomName = scanner.nextLine();

			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("chatCommand", "chatCreate");
			jsonObject.put("Uid", member.getUid());
			jsonObject.put("chatRoomName", chatRoomName);

			String json = jsonObject.toString();
			send(json);

			messagePrintResponse();

			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void chatEnter() {
		try {
			JSONObject jsonObject = new JSONObject();
			String json = jsonObject.toString();
			String jsons = dis.readUTF();
			String select;
			connect();

			System.out.println("입장할 채팅방 번호: ");
			select = scanner.nextLine();
			jsonObject.put("chatCommand", "isEntered");
			jsonObject.put("chatNo", select);
			send(json);
			
			JSONObject root = new JSONObject(jsons);
			String statusCode = root.getString("isEnteredResponse");
			
			if (statusCode.equals("1")) {
				System.out.println("채팅방 닉네임: ");
				chatName = scanner.nextLine();

				connect();

				jsonObject.put("chatCommand", "chatEnter");
				jsonObject.put("Uid", member.getUid());
				jsonObject.put("chatNo", select);
				jsonObject.put("chatname", chatName);

				roomNumber = Integer.parseInt(select);
				send(json);
				chatEnterResponse();
				disconnect();
			}
			else {
				
			}

			if (enterRoomListener != null) {
				enterRoomListener.afterEnter();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void chatEnterResponse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);

		String statusCode = root.getString("statusCode");
		String message = root.getString("message");
		System.out.println(message);
	}

	public void chatList() {
		try {
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("chatCommand", "chatlist");
			jsonObject.put("Uid", member.getUid());
			String json = jsonObject.toString();
			send(json);

			messagePrintResponse();
			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeRoom() {
		try {
			String select;
			System.out.println("삭제할 채팅방 번호: ");
			select = scanner.nextLine();

			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("chatCommand", "chatrm");
			jsonObject.put("Uid", member.getUid());
			jsonObject.put("chatNo", select);

			String json = jsonObject.toString();
			send(json);

			messagePrintResponse();
			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void messagePrintResponse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String message = root.getString("message");

		System.out.println(message);
	}

	public void logOut() {
		if (logOutListener != null) {
			logOutListener.afterLogOut();
		}
	}
}
