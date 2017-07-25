package ui.template.controllers;

import act.controller.Controller;
import act.util.PropertySpec;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.mvc.annotation.PostAction;
import ui.template.Model.Member;

public class MemberController extends Controller.Util {
    @GetAction("/")
    public void home() {
        render();
    }

    @PostAction("/signup")
    @PropertySpec(value = {"id", "nickname"})
    public Member signup(String id, String pwd) {
        Member member = new Member();
        member.setId(id);
        member.setPwd(pwd);
        member.setNickname("test");
        return member;
    }

    @PostAction("/signin")
    public void signin(String id, String pwd) {
        render(id);
    }

    @PostAction("/modifyPwd")
    public void modifyPwd(String pwd) {
        ok();
    }

    @PostAction("/signout")
    public void signout() {
        template("home");
    }

    @PostAction("/delete")
    public void delete() {
        template("home");
    }

    @GetAction("/thymeleaf")
    public void thymeleafHome() {
    }

    @PostAction("/thymeleaf/signin")
    public void thymeleafSignin(String id, String pwd) {
        render(id);
    }
}
