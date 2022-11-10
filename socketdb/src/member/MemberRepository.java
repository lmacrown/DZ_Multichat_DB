package member;
import java.util.List;
import java.util.Map;

public interface MemberRepository {
	public static final Map<String, Member> memberMap = null;
	void insertMember(Member member) throws Member.ExistMember, Exception;
	Member findByUid(String uid) throws Member.NotExistUidPwd, Exception;
	void updateMember(Member member) throws Member.NotExistUidPwd, Exception;
	public Member getMember(String uid);
	
	void loadMember() throws Exception;
	void memberDelete(Member member) throws Exception;
	void memberInfo();
	
	List<Member> getList();
	
}
