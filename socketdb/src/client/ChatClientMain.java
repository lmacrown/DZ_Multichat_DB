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
	String methodSetter();
	
	enum MenuMode {
		USER_MANAGEMENT
		,CHAT_ROOM_MANAGEMENT  
		,FILE_CHAT_MANAGEMENT  
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

	public String methodSetter() {
		String methodManager="memberManagement.";
		return methodManager;
	}
}

class MenuChatRoomManagement implements Menu {
	public void displayMenu() {
		System.out.println();
		System.out.println("1. 채팅방 목록");
		System.out.println("2. 채팅방 생성");
		System.out.println("3. 채팅방 입장");
		System.out.println("4. 채팅방 삭제");
		System.out.println("q. 로그 아웃");
		System.out.print("메뉴 선택 => ");
	}

	public String methodSetter() {
		String methodManager="chatManagement.";
		return methodManager;
	}
}

class MenuFileManagement implements Menu {
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

	public String methodSetter() {
		String methodManager="fileManagement.";
		return methodManager;
	}
}

class MenuFactory {
	private static Map<Menu.MenuMode, Menu> menuMap = new HashMap<>();
	static {
		menuMap.put(Menu.MenuMode.USER_MANAGEMENT, new MenuUserManagement());
		menuMap.put(Menu.MenuMode.CHAT_ROOM_MANAGEMENT, new MenuChatRoomManagement());
		menuMap.put(Menu.MenuMode.FILE_CHAT_MANAGEMENT, new MenuFileManagement());
	}

	public static Menu get(Menu.MenuMode menu) {
		return menuMap.get(menu);
	}
}

public class ChatClientMain {
	private static Menu menu ;
	static Map<String, Method> actionUserManagement = new HashMap<>(); 
	static Properties prop = new Properties();
	static Scanner scanner = new Scanner(System.in);
	static Member member = new Member();
	static Menu.MenuMode mode = Menu.MenuMode.USER_MANAGEMENT;
	
	static ClientControlMember memberClient = new ClientControlMember(scanner, member,
			() -> {modeSetter(Menu.MenuMode.CHAT_ROOM_MANAGEMENT);}, 
			() -> {scanner.close();System.out.println("프로그램 종료됨");
			});

	static ClientControlChat chatClient = new ClientControlChat(scanner, member,
			() -> {mode = Menu.MenuMode.FILE_CHAT_MANAGEMENT;
			menu = MenuFactory.get(mode);}, 
			() -> {mode = Menu.MenuMode.USER_MANAGEMENT;
			menu = MenuFactory.get(mode);
			});

	static ClientControlFile fileClient = new ClientControlFile(scanner,member, 
			() -> {mode = Menu.MenuMode.CHAT_ROOM_MANAGEMENT;
			menu = MenuFactory.get(mode);
			});

	public static void main(String[] args) throws Exception {
		prop.load(new FileInputStream(new File("db.properties")));

		for(Menu.MenuMode mode: Menu.MenuMode.values()) {
			Class cls = clsSetter(mode);
			menu = MenuFactory.get(mode);
			String methodManager=menu.methodSetter();
			int count = Integer.parseInt(prop.getProperty(methodManager+"count"));
			for (int i=1;i<=count;i++) {
				String methodKey = methodManager + i;
				String methodName = prop.getProperty(methodKey);
				String [] methodInfo = methodName.split(",");
				Method method = cls.getMethod(methodInfo[1]);

				actionUserManagement.put(mode.ordinal()+methodInfo[0], method);
				System.out.println(method);
			}
		}
		clientManagement();
	}


	public static void clientUi() {
		menu.displayMenu();
	}
	public static void modeSetter(Menu.MenuMode mMode) {
		mode = mMode;
		menu = MenuFactory.get(mode);
	}
	private static Class clsSetter(Menu.MenuMode menu) {

		Class cls = null;
		Menu.MenuMode mode = menu;
		switch(mode){
		case USER_MANAGEMENT:
			cls = memberClient.getClass();
			break;
		case CHAT_ROOM_MANAGEMENT:
			cls = chatClient.getClass();
			break;
		case FILE_CHAT_MANAGEMENT:
			cls = fileClient.getClass();
			break;
		}
		return cls;
	}


	private static void clientManagement() {
		Object ret=null;
		
		menu = MenuFactory.get(mode);
		while(true) {
			clientUi();
			String menuNum = scanner.nextLine();
			try {
				switch(mode){
				case USER_MANAGEMENT:
					ret = actionUserManagement.get(mode.ordinal()+menuNum).invoke(memberClient);
					break;
				case CHAT_ROOM_MANAGEMENT:
					actionUserManagement.get(mode.ordinal()+menuNum).invoke(chatClient);
					break;
				case FILE_CHAT_MANAGEMENT:
					actionUserManagement.get(mode.ordinal()+menuNum).invoke(fileClient);
					break;	
				}
				if (ret instanceof Boolean) {
					if (((Boolean)ret).booleanValue()) {
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	} 
}
