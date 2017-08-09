package member.controller;

import act.controller.Controller;
import act.db.DbBind;
import act.db.ebean.EbeanDao;
import act.util.PropertySpec;
import member.model.Member;
import member.model.Session;
import org.apache.commons.lang3.StringUtils;
import org.osgl.http.H;
import org.osgl.mvc.annotation.Before;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.mvc.annotation.PostAction;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.Instant;
import java.util.UUID;

public class MemberController extends Controller.Util {
  private static final Long SESSION_EXPIRE_SECONDS = 600L;
  private EbeanDao<Long, Member> daoMember;
  private EbeanDao<Long, Session> daoSession;

  static {
    createTableInDerby();
  }

  @Inject
  public MemberController(EbeanDao<Long, Member> daoMember, EbeanDao<Long, Session> daoSession) {
    this.daoMember = daoMember;
    this.daoSession = daoSession;
  }

  @Before(except = {"home", "signup", "signin", "showMember"})
  public void checkSignin(H.Request request) {
    Session session = getSession(request);
    if (session != null) {
      session.setExpiredAt(Instant.now().getEpochSecond() + SESSION_EXPIRE_SECONDS);
      daoSession.save(session);
    } else {
      redirect("/");
    }
  }

  @Before(only = {"home"})
  public void redirectSigned(H.Request request) {
    Session session = getSession(request);
    if (session != null) {
      redirect("signin");
    }
  }

  private Session getSession(H.Request request) {
    H.Cookie cookie = request.cookie("memberSn");
    if (cookie != null && StringUtils.isNotEmpty(cookie.value())) {
      Session session = daoSession.findById(Long.parseLong(cookie.value()));
      if (session != null && session.getExpiredAt() > Instant.now().getEpochSecond()) {
        return session;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  @GetAction("/")
  public void home() {
    render();
  }

  @PostAction("/signup")
  @PropertySpec(value = {"memberSn", "id", "nickname"})
  public Member signup(String id, String passwd) {
    Member member = new Member();
    member.setId(id);
    member.setPasswd(passwd);
    member.setNickname(UUID.randomUUID().toString().substring(0, 8));
    return daoMember.save(member);
  }

  @PostAction("/signin")
  public void signin(H.Response response, String id, String passwd) {
    Iterable<Member> members = daoMember.findBy("id,passwd", id, passwd);
    notFoundIf(members.iterator().hasNext() == false);

    // 세션 저장
    Member member = members.iterator().next();
    Session session = daoSession.findById(member.getMemberSn());
    if (session == null) {
      session = new Session();
      session.setMemberSn(member.getMemberSn());
    }
    session.setExpiredAt(Instant.now().getEpochSecond() + SESSION_EXPIRE_SECONDS);
    daoSession.save(session);

    // 쿠키 저장
    H.Cookie cookie = new H.Cookie("memberSn");
    cookie.value(member.getMemberSn().toString());
    response.addCookie(cookie);
  }

  @GetAction("/signin")
  public void signed(H.Request request) {
    Session session = getSession(request);
    String nickname = daoMember.findById(session.getMemberSn()).getNickname();
    template("signin", nickname);
  }

  @PostAction("/modify")
  public void modify(H.Request request, String passwd, String nickname) {
    Session session = getSession(request);
    Member member = daoMember.findById(session.getMemberSn());
    if (passwd != null) {
      member.setPasswd(passwd);
    }
    if (nickname != null) {
      member.setNickname(nickname);
    }
    daoMember.save(member);
  }

  @PostAction("/signout")
  public void signout(H.Request request, H.Response response) {
    deleteSession(request, response);

    template("home");
  }

  @PostAction("/delete")
  public void delete(H.Request request, H.Response response) {
    // Member 정보 삭제
    Member member = daoMember.findById(getSession(request).getMemberSn());
    daoMember.delete(member);

    // 세션 삭제
    deleteSession(request, response);

    template("home");
  }

  private void deleteSession(H.Request request, H.Response response) {
    // 세션 삭제
    Session session = getSession(request);
    daoSession.delete(session);

    // 쿠키 제거
    H.Cookie cookie = new H.Cookie("memberSn");
    cookie.value("");
    response.addCookie(cookie);
  }

  @GetAction("/member/{memberSn}")
  public Member showMember(@DbBind("memberSn") Member member) {
    return member;
  }

  private static void createTableInDerby() {
    Connection conn = null;
    Statement stmt = null;

    // JavaDB Embed Driver
    String driver = "org.apache.derby.jdbc.EmbeddedDriver";

    // ConnectionURL
    String connectionURL = "jdbc:derby:session";

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

    // Drop Table
    try {
      stmt = conn.createStatement();
      System.out.println("Dropping table...");
      stmt.execute("DROP TABLE session");
    } catch (Exception e) {
      System.out.println("Table isn't exist...");
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (Exception e2) {
        }
      }
    }

    // Create Table
    try {
      // Create Table Query
      String createString = "CREATE TABLE session "
          + "(member_sn BIGINT NOT NULL, "
          + " expired_at BIGINT) ";

      stmt = conn.createStatement();
      System.out.println("Creating table...");
      stmt.execute(createString);
    } catch (Exception e) {
      System.out.println("Table exist...");
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (Exception e2) {
        }
      }
    }
  }
}
