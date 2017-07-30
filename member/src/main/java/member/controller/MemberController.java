package member.controller;

import act.controller.Controller;
import act.db.ebean.EbeanDao;
import member.model.Contact;
import org.osgl.mvc.annotation.DeleteAction;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.mvc.annotation.PostAction;
import org.osgl.mvc.annotation.PutAction;

import javax.inject.Inject;

@Controller("/ctct")
public class MemberController extends Controller.Util {
    private EbeanDao<Long, Contact> dao;

    @Inject
    public MemberController(EbeanDao<Long, Contact> dao) {
        this.dao = dao;
    }

    @GetAction("/")
    public Iterable<Contact> list(Contact ctct) {
        return dao.findAll();
    }

    @PostAction
    public void create(Contact ctct) {
        dao.save(ctct);
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

}
