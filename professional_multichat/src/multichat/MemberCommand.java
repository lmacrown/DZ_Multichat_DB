package multichat;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class MemberCommand {

	ChatServer chatServer;
	SocketClient sc;

	public MemberCommand(SocketClient sc, JSONObject jsonObject) throws Exception {
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

	private void memberInfo() throws Exception {
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		Statement stmt = ChatServer.conn1.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM USER_INFO");
		
		List<Member> memberList = new ArrayList<>();
		while(rs.next()){
			jsonResult.put("uid",rs.getString("userid"));
			jsonResult.put("pwd",rs.getString("userpwd"));
			jsonResult.put("name",rs.getString("name"));
			Member mem=new Member(jsonResult);
			memberList.add(mem);
		}
	
		jsonResult.put("statusCode", "0");
		jsonResult.put("memberlist", memberList);

		sc.send(jsonResult.toString());
		sc.close();
		//List<Member> memberList = chatServer.memberRepository.memberList;
	}

	private void memberDelete(JSONObject jsonObject) throws Exception {
		Member member = new Member(jsonObject);

		JSONObject jsonResult = new JSONObject();
		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {
			chatServer.findByUid(member.getUid());
			chatServer.deleteMemberInfo(member);
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", "회원탈퇴가 정상적으로 이루어졌습니다.");

			sc.send(jsonResult.toString());

			sc.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void registerMember(JSONObject jsonObject) throws Exception {
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

	private void updateMember(JSONObject jsonObject) throws Exception {
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

	private void login(JSONObject jsonObject) throws Exception {
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

	private void passwdSearch(JSONObject jsonObject) throws Exception {
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
