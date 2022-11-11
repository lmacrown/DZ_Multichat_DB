package member;
import java.io.Serializable;
import java.util.Objects;
import org.json.JSONObject;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Member implements Serializable {
	private static final long serialVersionUID = 1449132512754742285L;
	public String uid;
	public String pwd;
	public String name;

	public static class ExistMember extends Exception {
		public ExistMember(String reason) {
			super(reason);
		}
	}

	public static class NotExistMember extends Exception {
		public NotExistMember(String reason) {
			super(reason);
		}
	}

	public static class NotExistUidPwd extends Exception {
		public NotExistUidPwd(String reason) {
			super(reason);
		}
	}

	public Member(String uid, String pwd, String name) {
		super();
		this.uid = uid;
		this.pwd = pwd;
		this.name = name;
	}

	public Member(JSONObject jsonObject) {
		uid = jsonObject.getString("uid");
		pwd = jsonObject.getString("pwd");
	}

	public Member() {
	}


	public Member settingMember(String uid, String pwd, String name) {
		this.uid = uid;
		this.pwd = pwd;
		this.name = name;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Member other = (Member) obj;
		return Objects.equals(uid, other.uid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uid);
	}
}