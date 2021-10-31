package hello.core.order;

import hello.core.annotation.MainDiscountPolicy;
import hello.core.discount.DiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
// @RequiredArgsConstructor // lombok으로 DI 자동주입
public class OrderServiceImpl implements OrderService{

//    private final MemberRepository memberRepository = new MemoryMemberRepository();
    // DIP 위반 코드 (new 로 구체 클래스도 의존)
//    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
//    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
    // DIP 준수 코드
    // 완성하기 위해 DiscountPolicy를 알아서 주입해주는 클래스나 메소드를 만들어줘야한다.

    // lombok으로 자동주입되었다
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    /* 필드 의존관계 주입
    @Autowired private MemberRepository memberRepository;
    @Autowired private DiscountPolicy discountPolicy;
    */

    // 수정자 의존관계 주입
    // 수정자 의족관계 단점 설명에 쓰임
    // OrderServiceImplTest에 쓰임
/*
    @Autowired
    public void setMemberRepository(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    @Autowired
    public void setMemberRepository(DiscountPolicy discountPolicy){
        this.discountPolicy = discountPolicy;
    }
*/

    /* 일반 메소드 의존관계 주입
    public void init(MemberRepository memberRepository, DiscountPolicy discountPolicy){
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
    */



    // 생성자 의존관계 주입
    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository, @MainDiscountPolicy DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        // 단일 책임 원칙 잘지켰다. 할인에 대한것은 할인 객체만 변경해주면 되기 때문이다.
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    // 테스트 용도로 사용한다
    public MemberRepository getMemberRepository(){
        return memberRepository;
    }
}
