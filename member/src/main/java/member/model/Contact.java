package member.model;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity(name = "ctct")
public class Contact {
    @Id
    private Long id;
    private String fn;
    private String ln;
    private String addr;

    public long getId() {
        return null == id ? -1 : id;
    }

    public String getFirstName() {
        return fn;
    }

    public void setFirstName(String fn) {
        this.fn = fn;
    }

    public String getLastName() {
        return ln;
    }

    public void setLastName(String ln) {
        this.ln = ln;
    }

    public String getAddress() {
        return addr;
    }

    public void setAddress(String addr) {
        this.addr = addr;
    }
}
