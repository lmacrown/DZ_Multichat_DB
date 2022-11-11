package client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


interface ControlManager{
	public ControlManager getObject();
}

public class ChatClient implements ControlManager{

	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	public static String chatName;
	
	public ChatClient() {
	}

	public void connect() throws IOException {
		socket = new Socket("localhost", 50001);
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		System.out.println("[클라이언트] 서버에 연결됨");
	}

	public void send(String json) throws IOException {
		dos.writeUTF(json);
		dos.flush();
	}

	public void disconnect() throws IOException {
		socket.close();
	}
	
	public ControlManager getObject(){
		return null;
	}
}