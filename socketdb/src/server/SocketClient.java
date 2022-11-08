package server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.JSONObject;

public class SocketClient {
	// 필드
	ChatServer chatServer;
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	String clientIp;
	String chatName;
	Room room;
	RoomManager roomManager;
	static String chatTitle;

	public SocketClient() {
	}

	// 생성자
	public SocketClient(ChatServer chatServer, Socket socket, RoomManager roomManager) throws Exception {
		try {
			this.chatServer = chatServer;
			this.socket = socket;
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
			InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
			this.clientIp = isa.getHostName();
			this.roomManager = roomManager;

			receive();
		} catch (IOException e) {
		}
	}

	// 메소드: JSON 받기
	public void receive() throws Exception {
		chatServer.threadPool.execute(() -> {
			try {
				boolean stop = false;

				while (true != stop) {
					String receiveJson = dis.readUTF();
					JSONObject jsonObject = new JSONObject(receiveJson);
		
					
					if(jsonObject.has("memberCommand")) {
						new MemberCommand(this, jsonObject);
						stop = true;
					}
					else if(jsonObject.has("chatCommand")) {
						String chatCommand = jsonObject.getString("chatCommand");
						if(chatCommand.equals("message")) {
							String message = jsonObject.getString("data");
							chatServer.sendMessage(this, message);
						}
						else{
						new ChatCommand(this, jsonObject);
						if(!chatCommand.equals("chatstart"))
							stop = true;
						}
					}
					else if(jsonObject.has("fileCommand")) {
						new FileCommand(this, jsonObject);
						stop = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}


	public void sendWithOutMe(String message) {
		JSONObject root = new JSONObject();
		root.put("chatName", this.chatName);
		for (SocketClient c : this.room.clients) {
			if (!c.equals(this)) {
				root.put("message", message);
				String json = root.toString();
				c.send(json);
			}
		}
	}

	public void send(String json) {
		try {
			dos.writeUTF(json);
			dos.flush();
		} catch (IOException e) {
		}
	}

	// 메소드: 연결 종료
	public void close() {
		try {
			socket.close();
		} catch (Exception e) {
		}
	}
}