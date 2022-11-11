package member;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.json.JSONObject;

import server.SocketClient;

public class MemberRepositoryDB implements MemberRepository {
	Map<String, Member> memberMap = Collections.synchronizedMap(new HashMap<>());
	List<Member> memberList = null;
	SocketClient sc;
	
	Properties prop = new Properties();
	Connection conn = null;
	PreparedStatement pstmt = null;


	private Connection connectDB(){
		try {
			conn = DriverManager.getConnection(prop.getProperty("dbServerConn")
					, prop.getProperty("dbUser")
					, prop.getProperty("dbPasswd"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}




	public synchronized void insertMember(Member member) throws Member.ExistMember {
		try {
			Class.forName(prop.getProperty("driverClass"));
			System.out.println("JDBC 드라이버 로딩 성공");
			conn=connectDB();
			pstmt = conn.prepareStatement(prop.getProperty("EXIST_MEMBER"));
			JSONObject jsonResult = new JSONObject();
			//멤버 존재여부 확인
			pstmt.setString(1, member.getUid());
			ResultSet rs = pstmt.executeQuery();
			int count = 0; 
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
			if (count != 0) {
				throw new Member.ExistMember("[" + member.getUid() + "] 아이디가 존재합니다" );
			}
			
			pstmt.close();
			pstmt = conn.prepareStatement(prop.getProperty("INSERT_MEMBER"));

			//멤버 정보 설정 
			pstmt.setString(1, member.getUid());
			pstmt.setString(2, member.getPwd());
			pstmt.setString(3, member.getName());
			pstmt.executeUpdate();
			memberList.add(member);
			memberMap.put(member.getUid(), member);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Member findByUid(String uid) throws Member.NotExistUidPwd {

		try {
			conn=connectDB();
			Class.forName(prop.getProperty("driverClass"));
			System.out.println("JDBC 드라이버 로딩 성공");

			pstmt = conn.prepareStatement(prop.getProperty("findByUidMember"));
			//멤버 존재여부 확인
			pstmt.setString(1, uid);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Member member = new Member();
				member.setUid(rs.getString("USERID"));
				member.setPwd(rs.getString("PWD"));
				member.setName(rs.getString("NAME"));
				rs.close();
				return member; 
			} else {
				throw new Member.NotExistUidPwd("[" + uid + "] 아이디가 존재하지 않습니다");			
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public synchronized void updateMember(Member member) throws Member.ExistMember {

		try {
			Class.forName(prop.getProperty("driverClass"));
			System.out.println("JDBC 드라이버 로딩 성공");

			conn=connectDB();
			pstmt = conn.prepareStatement(prop.getProperty("EXIST_MEMBER"));

			//멤버 존재여부 확인
			pstmt.setString(1, member.getUid());
			ResultSet rs = pstmt.executeQuery();
			int count = 0; 
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
			if (count != 1) {
				throw new Member.ExistMember("[" + member.getUid() + "] 아이디가 존재하지 않습니다" );
			}
			pstmt.close();

			pstmt = conn.prepareStatement(prop.getProperty("UPDATE_MEMBER"));

			//멤버 정보 설정 
			pstmt.setString(1, member.getPwd());
			pstmt.setString(2, member.getName());
			pstmt.setString(3, member.getUid());
			pstmt.executeUpdate();

			int index = memberList.indexOf(member);
			memberList.set(index, member);
			memberMap.put(member.getUid(), member);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	public void memberDelete(Member member) throws Exception {
		prop.load(new FileInputStream("db.properties"));

		Class.forName(prop.getProperty("driverClass"));
		conn=connectDB();

		pstmt = conn.prepareStatement(prop.getProperty("DELETE_MEMBER"));
		pstmt.setString(1, member.getUid());
		pstmt.executeUpdate();

		pstmt.close();

		memberList.remove(member);
		String key = member.getUid();
		memberMap.remove(key, member);
	}
	//회원관리
	public void memberInfo() {

	}
	
	public List<Member> getList() {
		return memberList;
	}
	
	public Member getMember(String uid) {
		return memberMap.get(uid);
	}
	

	public void loadMember() throws Exception {
		prop.load(new FileInputStream("db.properties"));

		Class.forName(prop.getProperty("driverClass"));
		conn=connectDB();
		memberList = new ArrayList<>();

		pstmt = conn.prepareStatement(prop.getProperty("SELECT_MEMBER"));
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next()) {
			Member dumpMem = new Member();
			dumpMem.uid = rs.getString("USERID");
			dumpMem.pwd = rs.getString("PWD");
			dumpMem.name = rs.getString("NAME");
			memberMap.put(dumpMem.getUid(), dumpMem);
			memberList.add(dumpMem);

		}
		for (Member m : memberList) {
			System.out.printf("%s%n", "ID:" + m.getUid() + " " + "NAME:" + m.getName() + " " + "PW:" + m.getPwd());
			System.out.println(memberMap.get(m.getUid()));
		}

		System.out.println();
		rs.close();


	}

}
