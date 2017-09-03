package async.model;

import act.db.DB;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@DB("h2")
@Entity(name = "member")
public class Member {
    @Id
    private Long memberSn;
    @Column(unique = true)
    private String id;
    private String passwd;
    private String nickname;

    public Long getMemberSn() {
        return memberSn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
