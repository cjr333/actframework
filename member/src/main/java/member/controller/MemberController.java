package member.controller;

import act.controller.Controller;
import act.db.ebean.EbeanDao;
import member.model.Contact;
import member.model.Member;
import org.osgl.mvc.annotation.DeleteAction;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.mvc.annotation.PostAction;
import org.osgl.mvc.annotation.PutAction;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Controller("/ctct")
public class MemberController extends Controller.Util {
    private EbeanDao<Long, Contact> dao;
    private EbeanDao<Long, Member> daoMember;

    @Inject
    public MemberController(EbeanDao<Long, Contact> dao, EbeanDao<Long, Member> daoMember) {
        this.dao = dao;
        this.daoMember = daoMember;

        createTableInDerby();
    }

    @GetAction("/")
    public Iterable<Contact> list(Contact ctct) {
        return dao.findAll();
    }

    @PostAction
    public void create(Contact ctct) {
        dao.save(ctct);
    }

    @GetAction("/member")
    public Iterable<Member> listMember(Member member) {
        return daoMember.findAll();
    }

    @PostAction("/member")
    public void createMember(Member member) {
        daoMember.save(member);
    }

    @GetAction("/{id}")
    public Contact show(long id) {
        return dao.findById(id);
    }

    @PutAction("/{id}/addr")
    public void updateAddress(long id, String value) {
        Contact ctct = dao.findById(id);
        notFoundIfNull(ctct);
        ctct.setAddress(value);
        dao.save(ctct);
    }

    @DeleteAction
    public void delete(long id) {
        dao.deleteById(id);
    }

    private void createTableInDerby() {
        Connection conn = null;
        Statement stmt = null;

        // JavaDB Embed Driver
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";

        // ConnectionURL
        String connectionURL = "jdbc:derby:session";

        // Create Table Query
        String createString = "CREATE TABLE member "
                + "(id INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
                + " fn VARCHAR(255), "
                + " ln VARCHAR(255), "
                + " addr VARCHAR(255)) ";

        // Load Driver
        try {
            Class.forName(driver);
        } catch (java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }

        // get Connection
        try {
            conn = DriverManager.getConnection(connectionURL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create Table
        try {
            stmt = conn.createStatement();
            System.out.println("Creating table...");
            stmt.execute(createString);
        } catch (Exception e) {
            System.out.println("Table exist...");
        } finally {
            if (stmt != null) try {
                stmt.close();
            } catch (Exception e2) {
            }
        }
    }
}
