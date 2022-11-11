package client;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.json.JSONObject;

import chat.ChatLogRepositoryDB;
import member.Member;
import server.Room;
import server.RoomManager;

public class ClientControlFile extends ChatClient {
	private Scanner scanner;
	Member member;
	ExitListener exitListener = null;

	static interface ExitListener {
		void afterExit();
	}

	public ClientControlFile(Scanner scanner, Member member, ExitListener exitListener) {
		this.scanner = scanner;
		this.member = member;
		this.exitListener = exitListener;
	}

	// 채팅 로그 출력
	public void printChatLog() throws Exception {
		ChatLogRepositoryDB chatLogRepositoryDB =new ChatLogRepositoryDB();
		connect();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("fileCommand", "chatlog");
		jsonObject.put("Uid", member.getUid());
		send(jsonObject.toString());
		ChatLogReceive();
		disconnect();
	}

	public void ChatLogReceive() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String message = root.getString("chatLogReceive");
		System.out.print("[채팅 로그]  \n" + message);
	}

	public void receive() {
		Thread thread = new Thread(() -> {
			try {
				while (true) {
					String json = dis.readUTF();
					JSONObject root = new JSONObject(json);
					String chatName = root.getString("chatName");
					String message = root.getString("message");
					System.out.println("[" + chatName + "] " + message);
				}
			} catch (Exception e1) {
			}
		});
		thread.start();
	}

	public void sendMessage() {
		try {
			ChatLogRepositoryDB chatLogRepositoryDB = new ChatLogRepositoryDB();
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("chatCommand", "chatstart");
			jsonObject.put("Uid", member.getUid());
			String json = jsonObject.toString();
			send(json);

			receive();

			System.out.println("--------------------------------------------------");
			System.out.println("보낼 메시지를 입력하고 Enter");
			System.out.println("채팅를 종료하려면 q를 입력하고 Enter");
			System.out.println("--------------------------------------------------");
			while (true) {
				String message = scanner.nextLine();
				if (message.toLowerCase().equals("q")) {
					jsonObject.put("chatCommand", "endchat");
					break;
				} else {
					jsonObject = new JSONObject();
					jsonObject.put("chatCommand", "message");
					jsonObject.put("data", message);
					jsonObject.put("chatname", member.getName());

					chatLogRepositoryDB.chatInput(message, member.getUid());
					send(jsonObject.toString());
				}
			}

			jsonObject.put("chatCommand", "endchat");
			json = jsonObject.toString();
			send(json);

			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 파일전송
	public void fileTransfer() throws Exception {
		// 파일이름
		System.out.println("파일 이름을 입력하시오");
		String transFileName = scanner.nextLine();
		File filename = new File("C:\\temp\\" + transFileName);

		if (!filename.exists()) {
			System.out.println("파일 없음");
			return;
		}

		byte[] arr = new byte[(int) filename.length()];
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename));
		in.read(arr);
		in.close();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("fileCommand", "fileTran");
		jsonObject.put("filename", filename.getName());
		jsonObject.put("filetrans", new String(Base64.getEncoder().encode(arr)));

		String json = jsonObject.toString();
		connect();
		send(json);
		disconnect();
		System.out.println("파일 전송 완료");
	}
	// 파일 받기
	public void fileReceive() throws Exception {
		System.out.println("파일 이름을 입력하시오");
		String transFileName = scanner.nextLine();
		connect();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("fileCommand", "fileRe");
		jsonObject.put("filename", transFileName);
		send(jsonObject.toString());

		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		byte[] data = Base64.getDecoder().decode(root.getString("decodeFile").getBytes());

		File workPath = new File("C:/Temp/" + transFileName);
		BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(workPath));
		fos.write(data);
		fos.close();
		disconnect();

		System.out.println("파일 받기 완료");

	}

	// 파일리스트 출력
	public void fileListOutput() throws Exception {
		connect();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("fileCommand", "fileList");
		String json = jsonObject.toString();

		send(json);
		fileListOutputRe();

		disconnect();
	}

	public void fileListOutputRe() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String message = root.getString("fileListOutputReceive");
		System.out.print("[파일 리스트]  \n" + message);
	}

	// 이미지 파일 불러오기
	public void imageRead(String filename) throws Exception {
		Image image1 = null;
		Image image2 = null;
		// 파일로부터 이미지 읽기
		File sourceimage = new File("c:\\temp\\" + filename);
		image1 = ImageIO.read(sourceimage);

		// InputStream으로부터 이미지 읽기
		InputStream is = new BufferedInputStream(new FileInputStream("c:\\temp\\" + filename));
		image2 = ImageIO.read(is);

		JFrame frame = new JFrame();

		JLabel label1 = new JLabel(new ImageIcon(image1));
		JLabel label2 = new JLabel(new ImageIcon(image2));

		frame.getContentPane().add(label1, BorderLayout.CENTER);
		frame.getContentPane().add(label2, BorderLayout.NORTH);

		frame.pack();
		frame.setVisible(true);
	}

	public void exitRoom() {
		if (exitListener != null) {
			exitListener.afterExit();
		}
	}
}
