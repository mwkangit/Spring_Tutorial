package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // FK로 선언한 것이다 // 여기가 변경되면 member_id FK 값이 다른 멤버로 변경된다
    private Member member;

    // @BatchSize(size = 1000)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // orderItem의 order로 매핑된다
    private List<OrderItem> orderItems = new ArrayList<>();

    /*
    CASCADE 이전에는 이렇게 코딩해야 했다
    persist(orderItemA);
    persist(orderItemB);
    persist(orderItemC);
    persist(order);

    CASCADE 하면 orderItem 없어도 된다
    persist(order);

     */

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // 어노테이션 없어도 hibernate가 알아서 지원해준다
    // SpringPhysicalNamingStrategy로 camel case를 언더스코어로 바꾼다 (order_date)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]

    //연관관계 메서드
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this); // List이므로 getOrder() 후 add 한다
    }

    public void addOrderItem(OrderItem orderItem){ // OrderItem이 주인이므로 메소드명을 addOrderItem으로 한다
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this); // Order 객체이므로 바로 setOrder()한다
    }

    //==생성 메서드==// // 주문 생성 여기서 완결 (응집) // 이런 메소드는 service에서 다룬다
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==// // Cancel 하면 주문 취소 되며 stock에 취소한 만큼 다시 추가해야 한다
    /**
     * 주문 취소
     */
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){ // COMP로 이미 배송되었으므로 취소 불가
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem: orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice(){
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

}
