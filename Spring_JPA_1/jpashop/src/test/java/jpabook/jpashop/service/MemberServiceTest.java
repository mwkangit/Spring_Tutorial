package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    // @Autowired  EntityManager em;

@Test
public void 회원가입() throws Exception {
    //given
     Member member = new Member();
     member.setName("kim");

    //when
    Long saveId = memberService.join(member);

    //then
    // em.flush();
    assertEquals(member, memberRepository.findOne(saveId));
}

@Test
public void 중복_회원_예외() throws Exception {
    //given
    Member member1 = new Member();
    member1.setName("kim");

    Member member2 = new Member();
    member2.setName("kim");

    //when
    memberService.join(member1);
    //try{
    memberService.join(member2); // 예외 발생해야 한다!
    /*}catch (IllegalStateException e){
        return; // 저 예외 발생하면 그냥 리턴
    }*/

    //then
    assertThrows(IllegalStateException.class, () -> memberService.join(member2)); // JUNIT5에서 하는법
    // fail("예외가 발생해야 한다."); // 이 코드 도달하면 잘못된 것이다
}


}