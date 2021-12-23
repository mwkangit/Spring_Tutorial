package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepositoryData extends JpaRepository<Member, Long> {

    // 다른 기능은 많지만 findByName() 같은 메소드는 엔티티에 name, username, none으로 존재할 수 있으므로 직접 만들어서 사용해야 한다
    // findBy가 붙어있으면 (select m from Member m where m.name = ?) 쿼리가 생성된다
    // service 계층에서 사용할 때 findById(memberId).get()으로 사용해야 한다
    List<Member> findByName(String name);

}
