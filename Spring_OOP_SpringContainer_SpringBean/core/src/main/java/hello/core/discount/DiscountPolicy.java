package hello.core.discount;

import hello.core.member.Member;

public interface DiscountPolicy {

    /**
     * @return 할인 대상 금액(할인된 금액 반환)
     */
    int discount(Member member, int price);

}
