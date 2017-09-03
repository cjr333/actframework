package async.controller;

import act.controller.Controller;
import act.db.DbBind;
import act.db.ebean.EbeanDao;
import act.event.EventBus;
import act.inject.Context;
import act.util.PropertySpec;
import async.model.Member;
import async.model.Session;
import org.apache.commons.lang3.StringUtils;
import org.osgl.http.H;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.mvc.annotation.PostAction;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MemberController extends Controller.Util {
  private static final Integer SESSION_EXPIRE_SECONDS = 600;
  private EbeanDao<Long, Member> daoMember;
  private EbeanDao<Long, Session> daoSession;

  @Inject
  public MemberController(EbeanDao<Long, Member> daoMember, EbeanDao<Long, Session> daoSession) {
    this.daoMember = daoMember;
    this.daoSession = daoSession;
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
  public Member signup(String id, String passwd, @Context EventBus eventBus) {
    Member member = new Member();
    member.setId(id);
    member.setPasswd(passwd);
    member.setNickname(UUID.randomUUID().toString().substring(0, 8));
    eventBus.trigger("createMember", member);
    return member;
//    return daoMember.save(member);
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
    cookie.maxAge(SESSION_EXPIRE_SECONDS*2);
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

  @GetAction("/member/list")
  public List<Member> listMembers() {
    return daoMember.findAllAsList();
  }

  @GetAction("/member/{memberSn}")
  public Member showMember(@DbBind("memberSn") Member member) {
    return member;
  }
}
