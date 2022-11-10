package client;

import java.util.Scanner;
import org.json.JSONObject;

import member.Member;


public class ClientControlMember extends ChatClient {

	private Scanner scanner = null;
	Member member;
	static interface LoginListener {
		void afterLogin(); 
	}

	static interface ExitListener {
		void afterExit(); 
	}

	LoginListener loginListener = null;
	ExitListener  exitListener = null;

	public ClientControlMember(Scanner scanner
			,Member member
			, LoginListener loginListener
			, ExitListener exitListener) {
		this.scanner = scanner;
		this.member = member;
		this.loginListener = loginListener;
		this.exitListener = exitListener;
	}


	public boolean login() {
		try {
			String uid;
			String pwd;
			boolean result = false;
			System.out.println("\n1. 로그인 작업");
			System.out.print("아이디 : ");
			uid = scanner.nextLine();
			System.out.print("비밀번호 : ");
			pwd = scanner.nextLine();

			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("memberCommand", "login");
			jsonObject.put("uid", uid);
			jsonObject.put("pwd", pwd);

			
			send(jsonObject.toString());
			
			result = loginResponse(uid,pwd);
			
			if (result == true && loginListener != null) {
				loginListener.afterLogin();
			}
			
			disconnect();
			return result;


		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean loginResponse(String uid, String pwd) throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println("로그인 성공");	
			System.out.println(uid+"님이 로그인 하셨습니다.");
			
			member = member.settingMember(uid,pwd,root.getString("name"));
			return true;
		} else {
			System.out.println(message);
			return false;
		}
	}

	// 회원가입
	public boolean registerMember() {
		String uid;
		String pwd;
		String name;

		try {
			System.out.println("[2]회원가입");
			System.out.print("아이디 : ");
			uid = scanner.nextLine();
			System.out.print("비번 : ");
			pwd = scanner.nextLine();
			System.out.print("이름 : ");
			name = scanner.nextLine();
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("memberCommand", "registerMember");
			jsonObject.put("uid", uid);
			jsonObject.put("pwd", pwd);
			jsonObject.put("name", name);
			String json = jsonObject.toString();

			send(json);

			registerMemberResonse();
			System.out.println(name+"님 환영합니다.");

			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public void registerMemberResonse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println(message);
		} else {
			System.out.println(message);
		}
	}



	public boolean passwdSearch() {
		try {
			String uid;

			System.out.println("\n3. 비밀번호 찾기");
			System.out.print("아이디 : ");
			uid = scanner.nextLine();

			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("memberCommand", "passwdSearch");
			jsonObject.put("uid", uid);
			String json = jsonObject.toString();
			send(json);

			passwdSearchResponse();

			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void passwdSearchResponse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println("비밀번호 : " + root.getString("pwd"));
			System.out.println("정상적으로 실행되었습니다");
		} else {
			System.out.println(message);
		}
	}

	public boolean updateMember() {
		String uid;
		String pwd;
		String name;
		try {
			System.out.println("[4]회원정보수정");
			System.out.println("변경할 내용을 입력하세요.");
			System.out.print("아이디 : ");
			uid = scanner.nextLine();
			System.out.print("비번 : ");
			pwd = scanner.nextLine();
			System.out.print("이름 : ");
			name = scanner.nextLine();

			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("memberCommand", "updateMember");
			jsonObject.put("uid", uid);
			jsonObject.put("pwd", pwd);
			jsonObject.put("name", name);
			String json = jsonObject.toString();
			send(json);

			updateMemberResponse();

			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void updateMemberResponse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println(message);
		} else {
			System.out.println(message);
		}
	}

	public boolean memberDelete() {
		String uid;
		String pwd;

		try {
			System.out.println("[5]회원탈퇴");
			System.out.print("아이디 : ");
			uid = scanner.nextLine();
			System.out.print("비번 : ");
			pwd = scanner.nextLine();


			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("memberCommand", "memberDelete");
			jsonObject.put("uid", uid);
			jsonObject.put("pwd", pwd);
			jsonObject.put("name", "");
			String json = jsonObject.toString();
			send(json);

			updateMemberResponse();

			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void memberDeleteResponse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println(message);
		} else {
			System.out.println(message);
		}
	}

	public boolean memberInfo() {


		try {
			System.out.println("[회원목록]");
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("memberCommand", "memberInfo");
			String json = jsonObject.toString();

			send(json);

			memberInfoResonse();
			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	public void memberInfoResonse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");


		if (statusCode.equals("0")) {			
			System.out.println(message);
		} else {
			System.out.println(message);
		}
	}
	public boolean memberExit() {
		if (exitListener != null) {
			exitListener.afterExit();
		}
		return true;
	}


}
