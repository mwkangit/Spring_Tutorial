# JPA  Practice Explanation

### ![practice function](/media/mwkang/Klevv/Spring 일지/스프링 입문/09.13/practice function.png)

- ## 기능

  - ### 회원 기능

    - #### 회원 등록

    - #### 회원 조회

  - ### 상품 기능

    - #### 상품 등록

    - #### 상품 수정

    - #### 상품 조회

  - ### 주문 기능

    - #### 상품 주문

    - #### 주문 내역 조회

    - #### 주문 취소

  - ### 기타 요구사항

    - #### 상품은 재고 관리가 필요하다

    - #### 상품의 종류는 도서, 음반, 영화가 있다

    - #### 상품을 카테고리로 구분할 수 있다

    - #### 상품 주문시 배송 정보를 입력할 수 있다

## Domain & Table Structure

![Domain & Table](/media/mwkang/Klevv/Spring 일지/스프링 입문/09.13/Domain & Table.png)

- #### 주문과 상품은 N : N 관계이지만 관계형 데이터베이스와 엔티티에서는 N : N 관계는 사용하지 않으므로 1 : N, N : 1 로 나누어서 표현한다.



## Entity Structure

![Entity Analysis](/media/mwkang/Klevv/Spring 일지/스프링 입문/09.13/Entity Analysis.png)

- #### 각 엔티티를 객체화하여 다른 엔티티에서 사용하는 경우가 표현되어 있다.



## Table Structure

![Table Analysis](/media/mwkang/Klevv/Spring 일지/스프링 입문/09.13/Table Analysis.png)

- #### CITY, STREET, ZIPCODE는 MEMBER, DELIVERY 테이블에 모두 속하며 값이 변경되면 안돼므로 embedded 타입으로 작성한다.

- #### Order by 때문에 ORDER이 잘 인식되지 않는 경우가 있으므로 테이블 이름을 ORDERS로 한다.

- #### 데이터를 저장할 때 같은 정보를 반복적으로 표현하면 낭비이므로 외부키로 정보를 대체한다.

- #### 외부키는 사용빈도가 높은 테이블에 배치한다.  또한, 연관 관계에서 주인인 테이블에 외부키를 배치한다.

- #### CATEGORY, ITEM 테이블은 N : N 관계이지만 관계형 데이터베이스는 N : N 표현이 불가하므로 mapping table인  CATEGORY_ITEM 테이블을 생성하여 1 : N, N : 1 관계를 형성하여 N : N 관계를 대체한다.



# Member Entity



```java
@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private  Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") // order 테이블의 member에 매핑된 거울일 뿐이다
    private List<Order> orders = new ArrayList<>();
}

```

- #### @Id는 기본키를 직접 생성하며 @GeneratedValue는 default strategy가 AUTO이며 키를 자동 생성해준다.

- #### @Embedded를 통해 Address를 받아서 column으로 만들어준다.  @Embedded는 JPA 내장 타입이다.  Address 클래스에는 @Embeddable을 작성하며 값 타입은 기본적으로 값을 변경하면 안돼기 때문에 @Setter은 작성하지 않는다.  @Embeddable에는 기본 생성자를  protected로 선언해야한다.  JPA가 객체를 생성할 때 리플랙션 같은 기술을 사용할 수 있게 지원해야하기 때문이다.

- #### MappedBy를 통해 다른 테이블의 외래키로 기본키를 매핑해줄 수 있다.  테이블이 양방향인 경우 왜래키를 위해 mappedBy를 해줘야한다.



# Order Entity



```java
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

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this); // Order 객체이므로 바로 setOrder()한다
    }
}
```

- #### @Table을 통해 클래스명이 아닌 다른 테이블명을 정할 수 있다.

- #### 지연로딩(LAZY)을 통해 데이터베이스를 즉시 초기화하지 않고 proxy 객체를 이용한다.  연결된 테이블은 직접적으로 테이블 내부 요소에 접근시 쿼리가 전송된다.

- #### 즉시로딩(EAGER)을 통해 한개의 쿼리를 통해 테이블과 EAGER로 연결된 다른 테이블의 정보도 같이 가져온다.

- #### JPQL로 쿼리를 작성한 경우 즉시로딩으로 설정하면 각 테이블에 대한 반환 시점에 모두 조회가 완료되어 있어야하므로 N + 1 문제가 발생한다.  즉, 쿼리를 1개 전송했지만 추가 쿼리가 N개 전송된다는 뜻이다.

- #### 지연로딩의 경우 쿼리를 일일이 전송하여 조회해야한다고 생각하지만 fetch join, 엔티티 그래프, 어노테이션으로 즉시로딩을 구현할 수 있다.  즉, 즉시로딩은 사용하지말고 지연로딩을 사용해야 예상치 못한 쿼리를 방지할 수 있다.

- #### @XToOne 관계는 default로 즉시로딩으로 설정되어있다.

- #### Cascade를 통해 연결한 테이블은 persist시 같이 persist 되게 매핑할 수 있다.  타입을 ALL로 하여 삭제할 때에도 동일하게 지우는 것으로 설정할 수 있다.

- #### @JoinColumn은 외래키를 생성할 때 사용된다.  다른 테이블의 기본키를 이름으로 넣어서 연결할 수 있다.

- #### LocalDateTime은 어노테이션이 없어도 hibernate에서 알아서 지원해준다.

- #### @Enumerated 상태를 직접 작성할 수 있으며 Ordinal, String 타입이 존재하며 개발 중에 정수와 다른 값이 추가되면 기존의 값에 혼동이 오기 때문에 String을 사용하는 것이 권장된다.

- #### 양방향 관계인 경우 동일한 상태를 유지해야하기 때문에 연관관계 메서드를 추가해야한다.  연관관계 메서드는 핵심적으로 제어하는 테이블에 선언한다.



# Item Entity



```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
}

```

- #### 데이터베이스의 상속을 물리 모델로 구현하려면 @Inheritance가 필요하다.  Inheritance에는 JOIN, TABLE_PER_CLASS, SINGLE_TABLE 타입이 존재한다.

  - #### JOIN : 가장 정규화된 타입으로 상위, 하위 테이블이 모두 만들어진다.  하위 테이블에는 외래키 제약조건이 생기며 상위 기본키가 하위의 기본키이면서 외래키로 잡아야한다.

  - #### TABLE_PER_CLASS : 하위 테이블만 만들어진다.  하위 테이블의 기본키는 상위의 기본키이며 상위의 attribute는 모두 하위의 attribute가 된다.

  - #### SINGLE_TABLE : 상위 테이블만 만들어진다.  하위의 attribute는 모두 상위의 attribute가 된다.

- #### @DiscriminatorColumn은 하위 클래스를 구분하는 용도의 column이며 default가 dtype이다.

- #### CATEGORY_ITEM은 CATEGORY에 결합되어있는 객체이므로 외래키 관련 매핑을 CATEGORY로 해줘야한다.

- #### @DiscriminatorValue는 엔티티를 저장할 때 구분 column에 저장할 값이다.  DiscriminatorValue를 이용하지 않을 시 default로 클래스 이름이 사용된다.



# Category Entity



```java
@Entity
@Getter @Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();

    // parent_id, child 뭔지 모르겠다

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    // 연관관계 메서드
    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(this);
    }
}
```

- #### @JoinTable으로 N : N을 구현할 중간 테이블을 생성한다.  객체는 N : N이 가능하지만 관계형 데이터베이스는 collection 관계를 양쪽에서 가질 수 없으므로 1 : N, N : 1로 변환하는 중간 테이블이 있어야 한다.

- #### joinColumns, inverseJoinColumns를 통해 각 테이블의 기본키를 외래키로 받는다.

- #### 계층으로 쭉 내려가려면 부모도 알 수 있어야 하므로 parent와 child를 생성한다.

- #### 중간 테이블에 column을 추가할 수 없고 세밀하게 쿼리를 실행하기 어렵기 때문에 사용하지 않는 것이 좋다.



# Additional Contents



- #### 엔티티에 Setter은 가급적이면 사용하지 않는 것이 좋다.  변경 포인트는 필요 상황에만 사용해야한다.  Setter을 설정했다면 리펙토링으로 제거해야한다.

- #### 컬렉션은 persist하는 순간 영속성 관리를 해야한다.  Hibernate가 컬렉션이 변경된 것을 추적해야 하기 때문에 추적할 수 있는 PersistentBag로 바꾸는 것이다.  이 상태에서 set을 통해 데이터를 변경하면 hibernate가 원하는 메커니즘이 변하므로 시스템 오류가 발생할 수 있다.  즉, 컬렉션은 필드에서 초기화하고 값을 변경하면 안됀다.

- #### Hibernate의 SpringPhysicalNamingStrategy로 엔티티의 필드명을 그대로 테이블의 column 명으로 사용한다.

- #### SpringBoot는 케멀 케이스를 언더스코어로 나누고 점을 언더스코어로 바꾸고 대문자를 소문자로 바꿔서 엔티티(필드)를 테이블(column)으로 변환한다.

- #### ImplicitNamingStrategy는 명시적으로 column,테이블명을 직접 적지 않았을 때 사용한다.(spring.jpa.hibernate.naming.implicit-strategy)

- #### spring.jpa.hibernate.naming.physical-strategy를 수정하여 모든 논리명에 적용되어 실제 테이블에도 적용되는 필수로 추가되어야 하는 문자를 추가할 수 있다



Image 출처 : [김영한 스프링 입문](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EC%9E%85%EB%AC%B8-%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8)