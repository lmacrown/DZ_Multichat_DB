package multichat;

import org.json.JSONObject;

public class ChatCommand{
	
	ChatServer chatServer;
	RoomManager roomManager;
	SocketClient sc;
	
	public ChatCommand(SocketClient sc, JSONObject jsonObject) {
		this.chatServer = sc.chatServer;
		this.roomManager = sc.roomManager;
		this.sc = sc;
		
		String command = jsonObject.getString("chatCommand");
		
		try {
			switch (command) {
			case "chatlist":
				chatList();
				break;
			case "chatCreate":
				chatCreate(jsonObject);
				break;
			case "chatEnter":
				chatEnter(jsonObject);
				break;
			case "chatrm":
				removeRoom(jsonObject);
				break;
			case "endchat":
				sc.sendWithOutMe("님이 나갔습니다.");
				break;
			case "chatstart":
				startChat(jsonObject);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void chatList() {

		String roomStatus;
		roomManager.updateRoom();
		roomStatus = "[room status]\n";
		if (roomManager.rooms.size() > 0) {
			for (Room room : roomManager.rooms) {
				roomStatus += String.format("{no : %s, title : %s}\n", room.no, room.title);
			}
			roomStatus = roomStatus.substring(0, roomStatus.length() - 1);
		}

		JSONObject jsonResult = new JSONObject();

		jsonResult.put("message", roomStatus);

		sc.send(jsonResult.toString());

		sc.close();

	}

	public void startChat(JSONObject jsonObject) {

		sc.chatName = jsonObject.getString("chatName");
		sc.room = roomManager.loadRoom(sc.chatName);
		sc.sendWithOutMe("님이 들어오셨습니다.");
		sc.room.clients.add(sc);

	}



	public void removeRoom(JSONObject jsonObject) {

		int chatNo = Integer.parseInt(jsonObject.getString("chatNo"));
		JSONObject jsonResult = new JSONObject();
		Room target = null;
		jsonResult.put("message", "해당번호 채팅방이 존재하지 않습니다.");

		for (Room room : roomManager.rooms) {
			if (room.no == chatNo) {
				target = room;
				jsonResult.put("message", room.title + " 방을 삭제했습니다.");

			}
		}
		if(target != null)
			roomManager.destroyRoom(target);

		sc.send(jsonResult.toString());

		sc.close();

	}

	public void chatCreate(JSONObject jsonObject) {

		String chatRoomName = jsonObject.getString("chatRoomName");
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "0");
		System.out.println(sc.room);

		roomManager.createRoom(chatRoomName, sc);
		System.out.println("[채팅서버] 채팅방 개설 ");
		System.out.println("[채팅서버] 현재 채팅방 갯수 " + roomManager.rooms.size());

		jsonResult.put("message", chatRoomName + " 채팅방이 생성되었습니다.");

		sc.send(jsonResult.toString());

		sc.close();
	}

	public void chatEnter(JSONObject jsonObject) {

		int chatNo = Integer.parseInt(jsonObject.getString("chatNo"));
		sc.chatName = jsonObject.getString("data");

		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "해당번호 채팅방이 존재하지 않습니다.");

		for (Room room : roomManager.rooms) {
			if (room.no == chatNo) {
				jsonResult.put("statusCode", "0");
				jsonResult.put("message", chatNo + "번 방에 입장했습니다.");
				sc.room = room;
				room.entryRoom(sc);
				sc.chatTitle = room.title;
				break;
			}

		}

		sc.send(jsonResult.toString());

		sc.close();
	}
}
