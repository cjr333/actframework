package ui.template.controllers;

import act.controller.Controller;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.mvc.annotation.PostAction;

public class MemberController extends Controller.Util {
    @GetAction("/")
    public void home() {
    }

    @PostAction("/signup")
    public void signup(String id, String pwd) {
        System.out.println(id);
        ok();
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
}
