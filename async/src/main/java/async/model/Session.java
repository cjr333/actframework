package async.model;

import act.db.DB;

import javax.persistence.Entity;
import javax.persistence.Id;

@DB("h2")
@Entity(name = "session")
public class Session {
    @Id
    private Long memberSn;
    private Long expiredAt;

    public Long getMemberSn() {
        return memberSn;
    }

    public void setMemberSn(Long memberSn) {
        this.memberSn = memberSn;
    }

    public Long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Long expiredAt) {
        this.expiredAt = expiredAt;
    }
}
