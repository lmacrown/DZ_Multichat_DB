package member;
import java.util.List;

public interface MemberRepository {
	void insertMember(Member member) throws Member.ExistMember, Exception;
	Member findByUid(String uid) throws Member.NotExistUidPwd, Exception;
	void updateMember(Member member) throws Member.NotExistUidPwd, Exception;
	
	
	void loadMember() throws Exception;
	void memberDelete(Member member) throws Exception;
	void memberInfo();
	
	List<Member> getList();
	
}
