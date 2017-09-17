package showoff.controller;

import act.controller.Controller;
import org.osgl.mvc.annotation.PostAction;
import showoff.model.Member;

public class EchoController extends Controller.Util {
    @PostAction("/echo")
    public Member echo(Member member) {
        return member;
    }
}
