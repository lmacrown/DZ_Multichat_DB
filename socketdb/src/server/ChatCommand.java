package server;

import org.json.JSONObject;

import chat.ChattingRepositoryDB;

public class ChatCommand {

	ChatServer chatServer;
	RoomManager roomManager;
	SocketClient sc;
	ChattingRepositoryDB chattingRepositoryDB = new ChattingRepositoryDB();

	public ChatCommand(SocketClient sc, JSONObject jsonObject) {
		this.chatServer = sc.chatServer;
		this.roomManager = sc.roomManager;
		this.sc = sc;

		String command = jsonObject.getString("chatCommand");

		try {
			switch (command) {
			case "chatlist":
				chatList(jsonObject);
				break;
			case "chatCreate":
				chatCreate(jsonObject);
				break;
			case "isentered":
				isEntered(jsonObject);
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
	
	public void isEntered(JSONObject jsonObject) throws Exception{
		sc.clientUid = jsonObject.getString("Uid");
		String chatNo=jsonObject.getString("chatNo");
		chattingRepositoryDB.IsEntered(chatNo, sc.clientUid);
		
	}

	public void chatList(JSONObject jsonObject) throws Exception {
		// 파일
		sc.clientUid = jsonObject.getString("Uid");
		sc.room = roomManager.loadRoom(sc.clientUid);
		// DB
		chattingRepositoryDB.selectChat(roomManager);

		// roomManager.updateRoom(); /*파일*/
		String roomStatus = "[room status]\n";
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

		sc.clientUid = jsonObject.getString("Uid");
		sc.loadMember(sc.clientUid);
		sc.room = roomManager.loadRoom(sc.clientUid);
		sc.sendWithOutMe("님이 들어오셨습니다.");
		sc.room.clients.add(sc);

	}

	public void removeRoom(JSONObject jsonObject) throws Exception {
		sc.clientUid = jsonObject.getString("Uid");
		sc.room = roomManager.loadRoom(sc.clientUid);
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
		// 파일
		if (target != null)
			roomManager.destroyRoom(target);
		// DB
		if (target != null)
			chattingRepositoryDB.DelectChat(chatNo);
		sc.send(jsonResult.toString());

		sc.close();

	}

	public void chatCreate(JSONObject jsonObject) throws Exception {
		sc.clientUid = jsonObject.getString("Uid");
		sc.room = roomManager.loadRoom(sc.clientUid);

		String chatRoomName = jsonObject.getString("chatRoomName");
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "0");

		roomManager.createRoom(chatRoomName);
		System.out.println("[채팅서버] 채팅방 개설 ");
		System.out.println("[채팅서버] 현재 채팅방 갯수 " + roomManager.rooms.size());

		jsonResult.put("message", chatRoomName + " 채팅방이 생성되었습니다.");

		sc.send(jsonResult.toString());
		chattingRepositoryDB.insertChat(chatRoomName);
		sc.close();
	}

	public void chatEnter(JSONObject jsonObject) throws Exception {
		sc.clientUid = jsonObject.getString("Uid");
		sc.room = roomManager.loadRoom(sc.clientUid);

		int chatNo = Integer.parseInt(jsonObject.getString("chatNo"));
		sc.chatName = jsonObject.getString("chatname");

		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "해당번호 채팅방이 존재하지 않습니다.");

		for (Room room : roomManager.rooms) {
			if (room.no == chatNo) {
				jsonResult.put("statusCode", "0");
				jsonResult.put("message", chatNo + "번 방에 입장했습니다.");
				room.entryRoom(sc);
				chattingRepositoryDB.enterChat(chatNo,sc.clientUid,sc.chatName);
				sc.room = room;
				sc.chatTitle = room.title;
				break;
			}

		}

		sc.send(jsonResult.toString());

		sc.close();
	}
}
