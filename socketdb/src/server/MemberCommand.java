package server;

import java.util.List;

import org.json.JSONObject;
import member.Member;
public class MemberCommand{
	
	ChatServer chatServer;
	SocketClient sc;
	
	public MemberCommand(SocketClient sc, JSONObject jsonObject) {
		this.chatServer = sc.chatServer;
		this.sc = sc;
		String command = jsonObject.getString("memberCommand");
		switch (command) {
		case "login":
			login(jsonObject);
			break;
		case "registerMember":
			registerMember(jsonObject);
			break;
		case "passwdSearch":
			passwdSearch(jsonObject);
			break;
		case "updateMember":
			updateMember(jsonObject);
			break;
		case "memberDelete":
			memberDelete(jsonObject);
			break;
		case "memberInfo":
			memberInfo();
			break;
		}
		
	}
	
	private void memberInfo() {
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {
			
			String memberData="[INFO]\n";
			
			List<Member> memberList = chatServer.memberRepository.getList();
			for (Member member : memberList) {
				memberData += String.format("[id : %s, pwd : %s, name : %s]\n", member.uid, member.pwd,member.name);
			}
			
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", memberData);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		sc.send(jsonResult.toString());

		sc.close();

	}

	private void memberDelete(JSONObject jsonObject) {
		Member member = new Member(jsonObject);

		JSONObject jsonResult = new JSONObject();
		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {
			chatServer.memberRepository.memberDelete(member);
			jsonResult.put("statusCode", "0");
			jsonResult.put("message",member.getUid()+"  탈퇴했습니다.");

			sc.send(jsonResult.toString());

			sc.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void registerMember(JSONObject jsonObject) {
		Member member = new Member(jsonObject);

		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {

			chatServer.registerMember(member);
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", "회원가입이 완료되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		sc.send(jsonResult.toString());

		sc.close();

	}

	private void updateMember(JSONObject jsonObject) {
		Member member = new Member(jsonObject);

		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {
			chatServer.memberRepository.updateMember(member);
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", "회원정보수정이 정상으로 처리되었습니다");
		} catch (Exception e) {
			e.printStackTrace();
		}

		sc.send(jsonResult.toString());

		sc.close();

	}

	private void login(JSONObject jsonObject) {
		String uid = jsonObject.getString("uid");
		String pwd = jsonObject.getString("pwd");
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {
			Member member = chatServer.findByUid(uid);
			if (null != member && pwd.equals(member.getPwd())) {
				jsonResult.put("statusCode", "0");
				jsonResult.put("message", "로그인 성공");
				jsonResult.put("uid", uid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		sc.send(jsonResult.toString());

		sc.close();
	}

	private void passwdSearch(JSONObject jsonObject) {
		String uid = jsonObject.getString("uid");
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {
			Member member = chatServer.findByUid(uid);
			if (null != member) {
				jsonResult.put("statusCode", "0");
				jsonResult.put("message", "비밀번호 찾기 성공");
				jsonResult.put("pwd", member.getPwd());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		sc.send(jsonResult.toString());

		sc.close();
	}
}
