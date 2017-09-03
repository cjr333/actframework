package async.event;

import act.db.ebean.EbeanDao;
import act.event.On;
import async.model.Member;

import javax.inject.Inject;
import java.util.List;

public class AsyncDao {
    private EbeanDao<Long, Member> daoMember;

    @Inject
    public AsyncDao(EbeanDao<Long, Member> daoMember) {
        this.daoMember = daoMember;
    }

    @On(value = "createMember", async = true)
    public Member createMember(Member newMember) {
        return daoMember.save(newMember);
    }

    @On(value = "listMembers")
    public List<Member> listMembers() {
        return daoMember.findAllAsList();
    }
}
