package hello.core.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberServiceImpl implements MemberService{

    // interface에 의존중이지만 구현체에도 의  존중이다. 즉, 추상화에도 의존하고 구체화에도 의존한다. DIP 위반
//    private final MemberRepository memberRepository = new MemoryMemberRepository();

    private final MemberRepository memberRepository;

    @Autowired // ac.getBean(MemberRepository.class) 와 같은 뜻이다
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    // 테스트 용도롤 사용한다
    public MemberRepository getMemberRepository(){
        return  memberRepository;
    }
}
