package client;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import member.Member;

interface Menu {
	void displayMenu();
	
	enum MenuMode {
		 USER_MANAGEMENT
		,CHATTING_MANAGEMENT  
		,CHAT_ROOM_MANAGEMENT  
	}
	
}

class MenuUserManagement implements Menu {
	public void displayMenu() {
		System.out.println();
		System.out.println("1. 로그인");
		System.out.println("2. 회원가입");
		System.out.println("3. 비밀번호검색");
		System.out.println("4. 회원정보수정");
		System.out.println("5. 회원탈퇴");
		System.out.println("6. 전체회원목록");
		System.out.println("q. 프로그램 종료");
		System.out.print("메뉴 선택 => ");
	}
}

class MenuChattingManagement implements Menu {
	public void displayMenu() {
		System.out.println();
		System.out.println("1. 채팅방 목록");
		System.out.println("2. 채팅방 생성");
		System.out.println("3. 채팅방 입장");
		System.out.println("4. 채팅방 삭제");
		System.out.println("q. 로그 아웃");
		System.out.print("메뉴 선택 => ");
	}
}

class MenuChatRoomManagement implements Menu {
	public void displayMenu() {
		System.out.println();
		System.out.println("1. 채팅 로그 출력");
		System.out.println("2. 메세지 입력");
		System.out.println("3. 파일전송");
		System.out.println("4. 파일목록 조회");
		System.out.println("5. 파일 다운");
		System.out.println("q. 채팅방 나가기");
		System.out.print("메뉴 선택 => ");
	}
}

class MenuFactory {
	private static Map<Menu.MenuMode, Menu> menuMap = new HashMap<>();
	static {
		menuMap.put(Menu.MenuMode.USER_MANAGEMENT, new MenuUserManagement());
		menuMap.put(Menu.MenuMode.CHATTING_MANAGEMENT, new MenuChattingManagement());
		menuMap.put(Menu.MenuMode.CHAT_ROOM_MANAGEMENT, new MenuChatRoomManagement());
	}
	
	public static Menu get(Menu.MenuMode menu) {
		return menuMap.get(menu);
	}
}



public class ChatClientMain {
	
	private static Menu menu = MenuFactory.get(Menu.MenuMode.USER_MANAGEMENT);
	
	public static void clientUi() {
		menu.displayMenu();
	}

	static boolean stop = false;
	static boolean isMember = true;
	static boolean isEnter = false;
//	static MenuMode menuLayer = MenuMode.USER_MANAGEMENT;
	static Properties prop = new Properties();
	static Scanner scanner = new Scanner(System.in);
	static Map<String, Method> actionUserManagement = new HashMap<>(); 
	static Member member = new Member();
	static ClientControlMember memberClient = new ClientControlMember(scanner, member,() -> {
		menu = MenuFactory.get(Menu.MenuMode.CHATTING_MANAGEMENT);
	}, () -> {
		scanner.close();
		stop = true;
		System.out.println("프로그램 종료됨");
	});
	static ClientControlChat chattingClient = new ClientControlChat(member);
	static ClientControlFile fileClient = new ClientControlFile(member);
	
	
	
	public static void main(String[] args) throws Exception {
		
		prop.load(new FileInputStream(new File("db.properties")));
		
		final int count = Integer.parseInt(prop.getProperty("memberManagement.count", "7"));
		Class cls = memberClient.getClass();
		
		for (int i=1;i<=count;i++) {
			String methodKey = "memberManagement." + i;
			String methodName = prop.getProperty(methodKey);
			String [] methodInfo = methodName.split(",");
			Method method = cls.getMethod(methodInfo[1]);
			
			actionUserManagement.put(methodInfo[0], method);
		}
		
		
		while(!stop) {
			
			memberManagement();
			
			while(isMember && !isEnter) {
				clientUi();
				String menuNum = scanner.nextLine();
				switch(menuNum) {
				case "1":
					chattingClient.chatList();

					break;

				case "2":
					chattingClient.chatCreate(scanner);
					break;
				case "3":
					isEnter = chattingClient.chatEnter(scanner);
					if(isEnter) {
						//menuLayer = MenuMode.CHAT_ROOM_MANAGEMENT;
						menu = new MenuChatRoomManagement();
					}
					break;
				case "4":
					chattingClient.removeRoom(scanner);
					break;
				case "Q", "q":
					//menuLayer = MenuMode.USER_MANAGEMENT;
					menu = MenuFactory.get(Menu.MenuMode.USER_MANAGEMENT);
					System.out.println("로그아웃");
					isMember= false;
					break;

				}
			}

			

			while(isEnter) {
				clientUi();
				String menuNum = scanner.nextLine();
				switch(menuNum) {
				case "1"://채팅 로그 출력"
					fileClient.printChatLog();
					break;
				case "2"://메세지 입력"
					chattingClient.sendMessage(scanner);
					break;
				case "3"://파일 전송
					fileClient.fileTrasfer(scanner);
					break;
				case "4"://파일 목록 조회
					fileClient.fileListOutput();
					break;
				case "5"://파일 받기
					fileClient.fileReceive(scanner);
					break;
				case "Q", "q":
					isEnter= false;
					menu = MenuFactory.get(Menu.MenuMode.CHATTING_MANAGEMENT);
					System.out.println("채팅방에서 나갔습니다.");
					break;

				}
			}
		}

	}

	private static void memberManagement() {
		
		while(true) {

			clientUi();

			String menuNum = scanner.nextLine();
			try {
				Object ret = actionUserManagement.get(menuNum).invoke(memberClient);
				if (ret instanceof Boolean) {
					if (((Boolean)ret).booleanValue()) {
						return;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	} 

}
