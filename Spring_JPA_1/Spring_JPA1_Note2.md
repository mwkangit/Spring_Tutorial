# Application Architecture

![Application Architecture](https://user-images.githubusercontent.com/79822924/137743686-63ab3d86-8c42-4dc1-af50-6e393520fead.png)

- #### Controller, Web : 웹 계층

- #### Service : 비즈니스 로직, 트랜잭션 처리

- #### Repository : JPA를 직접 사용하는 계층, 엔티티 매니저 이용

- #### Domain : 엔티티가 모여 있는 계층, 모든 계층에서 사용

- #### Repository, Service 계층 개발하고 테스트 케이스를 작성하여 검증 후 웹 계층 적용



# Member Repository & Service & Test

## Member Repository



```java
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList(); // inline 해준다
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name")
                .setParameter("name", name)
                .getResultList();
    }
}

```

- #### @Repository는 @SpringApplication 하위에 있으므로 자동으로 클래스를 bean으로 등록한다.

- #### @RequiredArgsConstructor로 DI를 final을 통해 간편하게 구현할 수 있다. Spring Boot가 @Autowired로 EntityManager injection을 지원해서 가능하다. @PersistenceContext, @PersistenceUnit으로도 EntityManager DI 구현 가능하다. Lombok의 @AllArgsConstructor은 생성자 DI를 자동으로 처리하여 변수만 선언하면 자동으로 매핑되게 한다.

- #### Persist()는 영속성으로 database에 전송되기 전에 key를 기준으로 저장한다. 동시에 domain 의 현재 key값에도 값을 저장한다.

- #### Find()는 첫 번째 인자로 반환 자료형을 넣으며 두 번째 인자로는 데이터를 구별할 key를 넣는다.

- #### 찾으려는 데이터가 다양할 경우 JPQL을 이용한다.

- #### SQL은 table을 대상으로 query를 전송하지만 JPQL은 엔티티 객체를 query의 대상으로 한다.

- #### JPQL에서는 엔티티의 이름을 지정한 후 query를 작성한다.

- #### createQuery() 첫 번째 인자로는 JPQL query를 넣고 두 번째 인자로는 반환 자료형을 넣는다.

- #### getResultList()를 통해 반환 결과를 리스트로 받을 수 있다.

- #### setParameter()은 JPQL query에 특정 기준을 부여할 수 있게 한다.



## Member Service



```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {


    private final MemberRepository memberRepository;


    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member){

        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}

```

- #### JPA의 모든 data 변경, 로직은 모두 @Transaction 내부에서 이루어져야 데이터의 갱신이 이루어지므로 모든 service에 @Transaction을 사용한다.

- #### @Transaction(readOnly = true)는 JPA가 조회하는 곳에서 성능을 최적화하며 default는 false이다. 즉, 데이터를 변경하는 메소드에는 @Transaction을 부여해야 한다.

- #### 사용자들이 같은 이름으로 동시 접근 할 경우를 생각하여 Member name에 제약조건을 부여해야 한다.



## Member Test



```java
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
```

- #### @RunWith(SpringRunner.class)는 junit과 spring을 연결하는 역할을 한다. Spring integration test를 구현할 때 사용하지만 junit 5버전부터 사용할 필요없다.

- #### @SpringBootTest는 spring boot를 띄운 상태로 test를 진행한다는 뜻이다. 즉, @Autowired를 사용할 수 있게 된다.

- #### @Test 옆에 @Rollback(false)를 작성하여 테스트 후 데이터가 남아있게 할 수 있다. @Rollback이 없다면 jpa는 @Transaction으로 테스트가 끝나면 데이터를 다시 삭제 할 것이기 때문에 database에 insert query를 전송하지 않는다.

- #### JPA의 같은 @Transaction 내부에서 엔티티가 같으며 key도 같으면 같은 영속성 컨텍스트로 관리되는 것이므로 비교가 가능하다.

- #### EntityManager의 flush는 database에 query를 강제로 날리는 것으로 영속성 컨텍스트가 database에 반영된다.

- #### Junit 4 이하는 expected = 예외.class를 통해 예외가 발생하면 테스트를 종료할 수 있지만 junit 5부터 expected는 불가하며 assertThrows()를 통해 예외를 검사해야 한다. AssertThrows()는 예외 발생감지 이후 다음 코드도 실행하여 뒤에 fail()으로 오류를 발생시키지 않아도 된다.



### Application.yml



```yaml
spring:
#  datasource:
#    url: jdbc:h2:mem:testdb
#    username : sa
#    password:
#      driver-class-name: org.h2.Driver
#
#  jpa:
#    hibernate:
#      ddl-auto: create
#    properties:
#      hibernate:
#                 show_sql: true
#        format_sql: true

logging:
  level:
    org.hibernate.SQL: debug
    org.hibernate.type: trace

```

- #### Spring boot는 test라는 test 전용 작은 database를 제공한다.

- #### Spring 영역 코드를 모두 삭제해도 spring boot가 자동으로 test  database를 기본 memory board로 인식하여 실행한다.

- #### ddl-auto : create는 database의 엔티티를 모두 drop한 후 create하고 어플리케이션을 실행하며 create-drop은 create와 동일하지만 마지막에 drop을 한번 더 실행하여 완전히 초기화 한다.



# Item Entity



```java
//==비즈니스 로직==// // 재고를 늘리고 줄이는 작업
    //Setter가 아닌 이 메소드로 값 변경한다 (이 엔티티 안에서 모든 것을 해결하는 것이 가장 객체 지향형이다.

    /**
     *  stock 증가
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
```

- #### 엔티티에 비즈니스 로직을 추가하여 응집도를 높일 수 있다. 객체 지향형 프로그래밍으로 stockQuantity가 엔티티에 있으므로 응집력을 높일 수 있다.

- #### 엔티티 자체가 해결가능하므로 비즈니스 로직을 도멘인 계층에 설계한다.



### Creating Exception



```java
public class NotEnoughStockException extends RuntimeException{
    public NotEnoughStockException() {
        super();
    }

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }

}

```

- #### Exception을 상속받고  override하여 예외를 생성할 수 있다.

- #### Throwable cause는 근원적인 다른 예외를 나열하게 한다.



# Item Repository & Service

## Item Repository



```java
@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if(item.getId() == null){
            em.persist(item);
        } else {
            em.merge(item);
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}

```

- #### Item은 처음에 데이터를 저장할 때 id가 없으므로 persist를 하며 아이템 id가 있다면 merge로 엔티티를 업데이트 한다.



## Item Service



```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    public List<Item> fintItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }

}

```

- #### Item Repository를 참조하여 item 저장 및 조회 기능을 생성한다.



# Order Entity



```java
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
```

- #### 동일한 이름의 객체가 매개변수로 여러개 전송될 경우 ...을 이용한다.

- #### 주문한 상품이 여러개일 경우를 대비하여 orderItem에 대해 반복문을 실행한다.



# OrderItem Entity



```java
    //==생성 메서드==// // 아이템 주문하면서 재고까지 감소시켜준다 // 이런 메소드는 service에서 다룬다
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){ // 얼마에 얼마나 샀는가?
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count); // 주문한 만큼 stock에서 제거한다
        return orderItem;
    }

    //==비즈니스 로직==//
    public void cancel() {
        getItem().addStock(count);
    }

    //==조회 로직==//
    /**
     * 주문상품 전체 가격 조회
     */
    public int getTotalPrice(){
        return getOrderPrice() * getCount();
    }
```

- #### GetItem(), getOrderPrice(), getPrice()는 this의 객체를  get하는 뜻이다.



# Order Repository & Service

## Order Repository



```java
@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }
}
```

- #### 저장하는 것과 하나의 데이터를 찾는 것은 다른 service와 동일하지만 전체 탐색은 JPQL, JPA Criteria룰 이용한다.



## Order Service



```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;


    /**
     * 주문
     */
    // 누가 무엇을 몇개를
    @Transactional
    public Long order(Long memberId, Long itemId, int count){

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성
        // 주문 하나만 들어갈 수 있게 설정했다 (여러개 X)
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId){
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        // 주문 취소
        order.cancel();
    }

    //검색
    //가장 복잡
    // public List<Order> findOrders(OrderSearch orderSearch){
        // return orderRepository.findAllByString(orderSearch);
    // }
}
```

- #### 주문할 때 회원Id, 사품Id 모두 필요하므로 MemberRepository, ItemRepository 모두 DI 한다.

- #### 주문할 때 프로그래밍 순서를 준수해야 유지보수하기 쉽다.

- #### OrderItem.createOrderItem()에서 데이터를 모두 set하는 것을 방지하기 위해 orderItem에  protected 생성자를 만들거나 클래스 위에 @NoArgsConstructor(access = AccessLevel.PROTECTED)를 사용하는 것이 좋다.

- #### Cascade로 orderItem, delivery도 같이 persist되어 DeliveryRepository, OrderItemRepository는 필요 없다.

- #### Cascade는 private이거나 특정 참조가 정해져 있을 때 이용한다. Delivery를 다른 엔티티에서도 참조를 한다면 등록, 삭제 시 cascade 때문에 데이터에 오류가 발생할 수 있다.

- #### JPA는  dirty checking을 통해 변경사항을 모두 체크하여 각 영속성 컨텍스트에 대해 query를 작성하여 전송할 필요없이 자동으로 query가 전송된다.

- #### 도메인 모델 패턴은 비즈니스 로직이 대부분 엔티티에 있는 것이며 트랜잭션 스크립트 패턴은 비즈니스 로직이 서비스 계층에 대부분 있는 것이다



## OrderSearch



```java
@Getter @Setter
public class OrderSearch {

    private String memberName; //회원 이름
    private OrderStatus orderStatus; //주문 상태[ORDER, CANCEL]
}

```

- #### 주문 검색할 때 항목으로 사용할 객체만 있는 클래스를 생성한다.



## Order Repository JPQL Search, JPA Criteria Search



```java
    // 검색

    // JPQL 처리
    public List<Order> findAllByString(OrderSearch orderSearch){
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        // 주문 상태 검색
        if(orderSearch.getOrderStatus() != null){
            if(isFirstCondition){
                jpql += " where";
                isFirstCondition = false;
            } else{
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        // 회원 이름 검색
        if(StringUtils.hasText(orderSearch.getMemberName())){
            if(isFirstCondition){
                jpql += " where";
                isFirstCondition = false;
            } else{
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if(orderSearch.getOrderStatus() != null){
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if(StringUtils.hasText(orderSearch.getMemberName())){
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    /**
     * JPA Criteria
     */
    // /JPA Criteria 처리
    public List<Order> findAllByCriteria(OrderSearch orderSearch){
        // 세팅하는 단계
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class); // 시작하는 애
        Join<Object, Object> m = o.join("member", JoinType.INNER); // 멤버와 조인하는 것이다

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if(orderSearch.getOrderStatus() != null){
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원 이름 검색
        if(StringUtils.hasText(orderSearch.getMemberName())){
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);

        return query.getResultList();

    }
```

- ####  JPQL 처리는 직접 JPQL로 query를 생성하여 동적 query를 구현하는 것이다.

- #### JPQL 처리는 길고 번거롭고 실수가 많이 발생할 위험이 있다.

- #### StringUtils.hasText()는 매개변수 값이 있으면 true를 반환한다는 뜻이다.

- #### JPA Criteria은 EntityManager의 CriteriaBuilder, CriteriaQuery를 이용하여 동적 query를 구현하는 것이다.

- #### JPA Criteria는 동적 query에 대한 condition 조합을 보기 좋게 만들기 위해 Predicate을 이용한다.

- #### JPA Criteria는 실무에서 사용하기 매우 어려우며 유지보수도 힘들게 하기 때문에 잘 이용하지 않는다.

- #### JPQL, JPA Criteria의 단점을 해결하기 위해 Querydsl을 사용한다.



# Home Controller & Html

## Home Controller



```java
@Controller
@Slf4j
public class HomeController {



    @RequestMapping("/")
    public String home(){
        log.info("home controller");
        return "home";
    }
}
```

- #### 매핑을 "/"로 하여 초기화면 접근 시 실행할 코드를 프로그래밍한다.

- #### Spring은 로그값을 출력하기 위해 @Slf4j를 사용한다.



## Home Html



```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header">
    <title>Hello</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader" />
    <div class="jumbotron">
        <h1>HELLO SHOP</h1>
        <p class="lead">회원 기능</p>
        <p>
            <a class="btn btn-lg btn-secondary" href="/members/new">회원 가입</a>
            <a class="btn btn-lg btn-secondary" href="/members">회원 목록</a>
        </p>
        <p class="lead">상품 기능</p>
        <p>
            <a class="btn btn-lg btn-dark" href="/items/new">상품 등록</a>
            <a class="btn btn-lg btn-dark" href="/items">상품 목록</a>
        </p>
        <p class="lead">주문 기능</p>
        <p>
            <a class="btn btn-lg btn-info" href="/order">상품 주문</a>
            <a class="btn btn-lg btn-info" href="/orders">주문 내역</a>
        </p>
    </div>
    <div th:replace="fragments/footer :: footer" />
</div> <!-- /container --></body>
</html>
```

- #### Th.replace는 html파일을 include하여 head부분을 가져온 html파일로 바꾸는 것이다. 프로젝트에는 fragments 디렉토리 아래에 header, bodyHeader, footer html파일이 있다. Thymleaf에서 include하는 layout은 일일이 head에 가져와야 하지만 계층형은 head에 생략하여 실무에 많이 사용한다.



# Member Form & Controller & Html

## Member Form



```java
@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "회원 이름은 필수 입니다")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}

```

- #### @NotEmpty는 필수로 입력해야하는 객체를 정해준다. Spring boot 2.3버전부터 gradle dependencies에 implementation 'org.springframework.boot:spring-boot-starter-validation'을 작성해야 한다.

- #### Form 객체를 통해 데이터를 객체에 담아서 웹과 소통할 수 있다.



## Member Controller Create



```java
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
         model.addAttribute("memberForm", new MemberForm());
         return "members/createMemberForm";
    }

    // submit 누르면 post 형식으로 동일한 url인 /members/new로 들어온다
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result){

        // createMemberForm html에 에러시 실행할 코드가 있어야한
        if(result.hasErrors()){
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);

        return "redirect:/";

    }

    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";

    }
}
```

- #### Form 객체를 새로 생성하여 view로 넘어갈 때 데이터를 실어서 보낼 수 있다.

- #### @Valid를 통해 @NotEmpty와 같은 validation을 반영해 줄 수 있다. 코드에서 바로 member 객체에 넣지말고 도메인과 넘어오면 validation으로 나누는 것이 좋다.

- #### BindingResult result는 오류가 발생할 때 view가 튕기는 것을 방지하며 오류가 result에 담겨서 내부의 코드가 실행될 수 있게 한다.

- #### Form data를 그대로 가져가므로 에러 발생 후에도 입력했었던 데이터는 유지된다.

- #### 재로딩을 방지하기 위해 redirect:를 사용한다.



## Member Html Create



```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header" />
<style>
.fieldError {
border-color: #bd2130;
}
</style>
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader"/>
    <form role="form" action="/members/new" th:object="${memberForm}"
          method="post">
        <div class="form-group">
            <label th:for="name">이름</label>
            <input type="text" th:field="*{name}" class="form-control"
                   placeholder="이름을 입력하세요"
                   th:class="${#fields.hasErrors('name')}? 'form-control
fieldError' : 'form-control'">
            <p th:if="${#fields.hasErrors('name')}"
               th:errors="*{name}">Incorrect date</p>
        </div>
        <div class="form-group">
            <label th:for="city">도시</label>
            <input type="text" th:field="*{city}" class="form-control"placeholder="도시를 입력하세요">
        </div>
        <div class="form-group">
            <label th:for="street">거리</label>
            <input type="text" th:field="*{street}" class="form-control"
                   placeholder="거리를 입력하세요">
        </div>
        <div class="form-group">
            <label th:for="zipcode">우편번호</label>
            <input type="text" th:field="*{zipcode}" class="form-control"
                   placeholder="우편번호를 입력하세요">
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
    <br/>
    <div th:replace="fragments/footer :: footer" />
</div> <!-- /container -->
</body>
</html>
```

- #### th:object는 form 안에서 지정한 객체를 계속 사용하겠다는 뜻이다.

- #### th:field="*{name}"은 object 객체 내부의 name을 참조한다는 뜻이다.

- #### "${#fields.hasErrors('name')}? 'form-control fieldError' : 'form-control'"는 name에 에러가 발생했다면 fieldError css를 실행한다는 뜻이다.(view 박스를 붉게 색칠한다)

- #### "${#fields.hasErrors('name')}" th:errors="*{name}">Incorrect date는 name에 에러가 발생하면 validation의 메시지를 출력하게 하는 것이다.



## Member Controller List



```java
@GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";

    }
```

- ####  이 코드는 server side에서 실행되는 것이므로 template engine에서는 선택적으로 엔티티를 사용해도 된다. 하지만 API를 만들 때에는 password와 같은 중요정보가 노출되고 API스택이 변하여 불안정해질 수 있으므로 절대 엔티티를 넘기면 안됀다. 최대한 엔티티는 순수하게 유지하고 데이터 전송을 @Getter, @Setter만 있는 DTO나 Form을 사용하는 것이 권장된다.



## Member Html List



```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header" />
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader" />
    <div>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>#</th>
                <th>이름</th>
                <th>도시</th>
                <th>주소</th>
                <th>우편번호</th>
            </tr>
            </thead>
            <tbody>
            <tr th:each="member : ${members}">
                <td th:text="${member.id}"></td>
                <td th:text="${member.name}"></td>
                <td th:text="${member.address?.city}"></td>
                <td th:text="${member.address?.street}"></td>
                <td th:text="${member.address?.zipcode}"></td>
            </tr>
            </tbody></table>
    </div>
    <div th:replace="fragments/footer :: footer" />
</div> <!-- /container -->
</body>
</html>
```

- #### th:each를 통해 넘어온 엔티티들을 반복적으로 바인딩 할 수 있다.

- #### ?는 엔티티가 null이면 진행하지 않고 다음으로 넘어간다는 뜻이다.



# Item Form & Controller & Html

## Item Form



```java
@Getter @Setter
public class BookForm {

    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    private String author;
    private String isbn;
}

```

- #### 책에 대한 form을 만든 것이며 상품 공통 속성과 책 관련 특별 속성을 정의하였다.



## Item Controller Create



```java
@Controller
@RequiredArgsConstructor
public class BookController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }
    @PostMapping("/items/new")
    public String create(BookForm form){

        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }
}
```

- #### 책에 대해서도 validation을 부여할 수 있다.

- #### 현재  책 객체 만들어서 하나하나 set해주고 있지만 유지보수에 좋은 프로그래밍이 아니다. CreateBook()같은 메소드를 만들어서 인자를 넘겨서 setter을 모두 제거하는 것이 좋다.



## Item Html Create



```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header" />
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader"/>
    <form th:action="@{/items/new}" th:object="${form}" method="post">
        <div class="form-group">
            <label th:for="name">상품명</label>
            <input type="text" th:field="*{name}" class="form-control"
                   placeholder="이름을 입력하세요">
        </div>
        <div class="form-group">
            <label th:for="price">가격</label>
            <input type="number" th:field="*{price}" class="form-control"
                   placeholder="가격을 입력하세요">
        </div>
        <div class="form-group">
            <label th:for="stockQuantity">수량</label>
            <input type="number" th:field="*{stockQuantity}" class="form-
control" placeholder="수량을 입력하세요">
        </div>
        <div class="form-group"><label th:for="author">저자</label>
            <input type="text" th:field="*{author}" class="form-control"
                   placeholder="저자를 입력하세요">
        </div>
        <div class="form-group">
            <label th:for="isbn">ISBN</label>
            <input type="text" th:field="*{isbn}" class="form-control"
                   placeholder="ISBN을 입력하세요">
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
    <br/>
    <div th:replace="fragments/footer :: footer" />
</div> <!-- /container -->
</body>
</html>
```

- #### 앞의 Member Controller Create과 같이 데이터를 입력할 수 있는 view를 제공한다.



## Item Controller List



```java
@GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.fintItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }
```

- #### Member Controller List와 동일하게 엔티티를 그대로 사용하여 목록을 출력하였다.



## Item Html List



```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header" />
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader"/>
    <div>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>#</th>
                <th>상품명</th>
                <th>가격</th>
                <th>재고수량</th>
                <th></th>
            </tr>
            </thead>
            <tbody><tr th:each="item : ${items}">
                <td th:text="${item.id}"></td>
                <td th:text="${item.name}"></td>
                <td th:text="${item.price}"></td>
                <td th:text="${item.stockQuantity}"></td>
                <td>
                    <a href="#" th:href="@{/items/{id}/edit (id=${item.id})}"
                       class="btn btn-primary" role="button">수정</a>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <div th:replace="fragments/footer :: footer"/>
</div> <!-- /container -->
</body>
</html>
```

- #### 전체적인 코드는 Member Html List와 동일하다.

- #### 수정 버튼을 생성하여 아이템을 수정하는 로직으로 이동할 수 있게 한다.



## Item Controller Modify



```java
    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@PathVariable String itemId, @ModelAttribute("form") BookForm form){

        Book book = new Book();
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        // 위의 코드 대신 하나의 메소드로 관리하는 것이 유지보수에 좋다
        // itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

        itemService.saveItem(book);
        return "redirect:items";
    }
```

- #### @PathVariable을 통해 browzer로 부터 받은 특정 값을 사용할 수 있다.

- #### 현재 저장되었던 값을 form 객체에 넣어서 업데이트 html로 보낸다.

- #### GetMapping의 경우 url로 조작이 가능하기 때문에 보안에 취약하다. Service 계층에서 user가 item에 권한이 있는지 체크하는 로직이 있는 것이 좋다.

- #### @ModelAttribute는 html로부터 온 데이터를 그대로 매핑하여 사용한다는 뜻이다. ModelAttribute는 모델을 바로 생성하는 것으로 model.addAttribute()를 하지 않아도 된다.

- #### 수정하는 것이므로 Item.id가 null이 아니어서 repository에서 persist가 아닌 merge를 실행한다.



## Item Html Modify



```java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header" />
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader"/>
    <form th:object="${form}" method="post">
        <!-- id -->
        <input type="hidden" th:field="*{id}" />
        <div class="form-group">
            <label th:for="name">상품명</label>
            <input type="text" th:field="*{name}" class="form-control"
                   placeholder="이름을 입력하세요" />
        </div>
        <div class="form-group">
            <label th:for="price">가격</label>
            <input type="number" th:field="*{price}" class="form-control"
                   placeholder="가격을 입력하세요" />
        </div>
        <div class="form-group">
            <label th:for="stockQuantity">수량</label>
            <input type="number" th:field="*{stockQuantity}" class="form-control" placeholder="수량을 입력하세요" />
        </div>
        <div class="form-group">
            <label th:for="author">저자</label>
            <input type="text" th:field="*{author}" class="form-control"
                   placeholder="저자를 입력하세요" />
        </div>
        <div class="form-group">
            <label th:for="isbn">ISBN</label>
            <input type="text" th:field="*{isbn}" class="form-control"
                   placeholder="ISBN을 입력하세요" />
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
    <div th:replace="fragments/footer :: footer" />
</div> <!-- /container -->
</body>
</html>
```

- #### Id값은 hidden으로 작성하여 사용자에게 보이지 않게 하였다.

- #### 수정한 데이터를 post형식으로 전송하여 데이터가 사용자에게 나타나지 않고 서버에서 처리될 수 있게 하였다.



# Order Controller & Html

## Order Controller Create



```java
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model){

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.fintItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count){

        orderService.order(memberId, itemId, count);
        // 주문 결과 페이지로 가야한다면 order의 반환 아이디를 받고 아이디에 대한 페이지로 return 한다
        return "redirect:/orders";
    }
}
```

- #### @RequestParam은 url을 통해 전송되는 정보(?id=)의 이름을 지정하여 그대로 전달받고 사용할 수 있게 하는 것이다. 즉, html을 통해 전달된 아이디와 동일하게 설정하여 html로부터 그대로 값을 전달 받는 것이다.

- #### OrderService.order()와 같이 service 계층이 엔티티에 대한 의존도가 높고 dirty check과 같은 일을 할 수 있으로 영속성 객체는 service 계층에서 처리하는 것이 좋다. Controller은 Transactional이 적용되지 않으므로 service에 영속성 객체를 넘겨도 service에서 영속성으로 간주하지 않는다. 결론적으로 엔티티 관련 코드는 service 계층에서 처리하는 것이 좋다.

- ####  Submit을 한 후 /orders로 이동하여 Controller에 /orders로 매핑된 메소드를 실행하게 된다.



## Order Html Create



```java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header" />
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader"/>
    <form role="form" action="/order" method="post">
        <div class="form-group">
            <label for="member">주문회원</label>
            <select name="memberId" id="member" class="form-control">
                <option value="">회원선택</option>
                <option th:each="member : ${members}"
                        th:value="${member.id}"
                        th:text="${member.name}" />
            </select>
        </div>
        <div class="form-group">
            <label for="item">상품명</label>
            <select name="itemId" id="item" class="form-control">
                <option value="">상품선택</option>
                <option th:each="item : ${items}"
                        th:value="${item.id}"
                        th:text="${item.name}" />
            </select>
        </div>
        <div class="form-group">
            <label for="count">주문수량</label>
            <input type="number" name="count" class="form-control" id="count"placeholder="주문 수량을 입력하세요">
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
    <br/>
    <div th:replace="fragments/footer :: footer" />
</div> <!-- /container -->
</body>
</html>
```

- #### MemberId, itemId, count 이름으로 입력된 데이터를 전송한다.

- #### Submit 후 주문 상품 목록으로 이동한다.



## Order Controller Search



```java
 @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch")OrderSearch orderSearch, Model model){
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        // @ModelAttribute로 이 코드가 생략되었다고 볼 수 있다
        // Spring MVC를 공부해야 한다
        // model.addAttribute("orderSearch", orderSearch);

        return "order/orderList";
    }
```

- ####  Form에서 전송되어 오는 검색조건이 담겨서 온다.

- #### OrderSearch 기준에 맞는 엔티티를 가져온 후 html에 전송한다.

- #### 최초로 들어온 orderSearch에는 null값이 들어 있을 것이므로 전체 회원, 전체 상태를 모두 검색한다.

- #### 이후에는 html에서 설정한 데이터를 기준으로 검색한다.



## Order Controller Cancel



```java
    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
```

- #### OrderStatus를 cancel로 바꾼 후 다시 목록 html로 돌아간다.



## Order Html Search & Cancel



```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header"/>
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader"/>
    <div>
        <div>
            <form th:object="${orderSearch}" class="form-inline">
                <div class="form-group mb-2">
                    <input type="text" th:field="*{memberName}" class="form-
control" placeholder="회원명"/>
                </div>
                <div class="form-group mx-sm-1 mb-2">
                    <select th:field="*{orderStatus}" class="form-control">
                        <option value="">주문상태</option>
                        <option th:each=
                                        "status : ${T(jpabook.jpashop.domain.OrderStatus).values()}"
                                th:value="${status}"
                                th:text="${status}">option
                        </option>
                    </select>
                </div>
                <button type="submit" class="btn btn-primary mb-2">검색</button>
            </form>
        </div>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>#</th>
                <th>회원명</th>
                <th>대표상품 이름</th>
                <th>대표상품 주문가격</th><th>대표상품 주문수량</th>
                <th>상태</th>
                <th>일시</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <tr th:each="item : ${orders}">
                <td th:text="${item.id}"></td>
                <td th:text="${item.member.name}"></td>
                <td th:text="${item.orderItems[0].item.name}"></td>
                <td th:text="${item.orderItems[0].orderPrice}"></td>
                <td th:text="${item.orderItems[0].count}"></td>
                <td th:text="${item.status}"></td>
                <td th:text="${item.orderDate}"></td>
                <td>
                    <a th:if="${item.status.name() == 'ORDER'}" href="#"
                       th:href="'javascript:cancel('+${item.id}+')'"
                       class="btn btn-danger">CANCEL</a>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <div th:replace="fragments/footer :: footer"/>
</div> <!-- /container -->
</body>
<script>
function cancel(id) {
var form = document.createElement("form");
form.setAttribute("method", "post");
form.setAttribute("action", "/orders/" + id + "/cancel");
document.body.appendChild(form);
form.submit();
}</script>
</html>
```

- #### Th:object를 통해 orderSearch를 참조하고 orderSearch에 선택한 값을 넣고 submit하여 controller가 해당 엔티티를 검색하게 한다. 즉, submit하면 orderSearch에 바인딩 된다.

- #### 처음의 form은 검색에 필요한 view를 나타내며 th:each를 하는 이유는 상태의 종류가 여러개 이므로 모두 사용자에게 출력해주기 위해 있는 것이다.

- #### Controller에서 검색한 데이터는 table로 들어가서 검색한 데이터를 모두 출력한다.

- #### 상태가 ORDER인 항목만 CANCEL 버튼이 생성되며 CANCEL 버튼을 누르면 아래의 cancel 메소드가 실행되어 orderId 데이터를 담아서 Controller의 cancel 메소드로 post된다.





# Additional Contents



- #### Gradle의 dependencies에 spring-boot-devtools는 클래스를 캐싱하지 않아서 프로젝트를 재실행 할 필요 없이 recompile만 하면 변경된 프로젝트 내용을 적용할 수 있다.

- #### Bootstrap의 js,css 파일, jumbotron-narrow.css를 /resource/static에 저장하여 웹의 디자인을 향상시킬 수 있다.

- #### 준영속 엔티티는 영속성 컨텍스트가 더 이상 관리하지 않는 엔티티를 말한다. BookController의 itemService.saveItem(book)을 수정을 시도하는 Book 객체는 이미 database에 한번 저장되어서 식별자가 존재한다. New로 만든 임의의 엔티티이지만 기존 식별자를 가지고 있으므로 준영속 엔티티로 볼 수 있다.

- #### JPA는 값을 바꾸면 persist, commit 없이 자동으로 Transactional 구문이 끝나면 flush를 실행하여 영속성 컨텍스트 중 변경된 부분을 찾아서 database에 업데이트 쿼리를 전송한다. 이러한 방법으로 데이터를 수정하는 것을 dirty check라고 한다.

```java
@Transactional
void update(Item itemParam) { //itemParam: 파리미터로 넘어온 준영속 상태의 엔티티
	Item findItem = em.find(Item.class, itemParam.getId()); //같은 엔티티를 조회한다.
	findItem.setPrice(itemParam.getPrice()); //데이터를 수정한다.
}
```

- #### ItemParam은 준영속 엔티티이지만 findItem은 영속성 엔티티이다. 즉, commit을 하지 않아도 findItem.setPrice()를 하면 Transactional구문이 끝나면 database에  update query가 날라간다. 물론 데이터를 set하는 메소드를 따로 만들어서 처리하는 것이 유지보수에 좋다.

```java
@Transactional
void update(Item itemParam) { //itemParam: 파리미터로 넘어온 준영속 상태의 엔티티
	Item mergeItem = em.merge(itemParam);
}
```

- #### 병합 사용 시 준영속 엔티티인  itemParam을 통해 엔티티가 업데이트 된다. 하지만 itemParam은 여전히 영속성 엔티티가 아니며 JPA를 통해 반환값으로 엔티티를 전달받은 mergeItem은 영속성 엔티티이다.

![merge entity context](https://user-images.githubusercontent.com/79822924/137743835-f83d88ff-32db-40d2-9a58-7f80c9c05ebf.png)

1. #### 캐시에 member과 같은 엔티티가 있는지 확인한다.(보통 없다)

2. #### Database에서 member을 영속성 엔티티로 찾아온다.

3. #### 매개변수로 들어온 member을 영속성 엔티티인 mergeMember에 세팅하고 그 후 바뀐 상태를 영속성으로 반환한다.

- #### 병합은 모든 필드를 교체하기 때문에 값이 없으면 null로 업데이트하는 상황이 발생할 수 있어서 위험하다. 예를 들어, 어떠한 값은 고정하고 다른 값만 바꾸려고 할 때 고정된 값을 입력하지 않으면 null로 변경할 수 있다. 그러므로 실무에서는 dirty check를 사용하는 것이 좋다.

- #### 가독성과 유지보수를 효율적으로 하기 위해 set시 service파일에 update 메소드를 만들어서 매개변수로 데이터를 받거나 service 패키지에 dto를 생성한다.

# Keyboard Shortcut



- #### Ctrl + e 로 프로젝트의 클래스를 최근 사용한 순서로 확인할 수 있다.

- #### Ctrl + shift + F9로 recompile을 실행할 수 있다.

- #### Ctrl 을 두번 누르면 여러줄을 focus하여 동일한 코드를 작성할 수 있다.

- #### Ctrl + shift + u는 문자를 uppercase로 바꿔준다.



Image 출처 : [김영한 스프링 입문](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EC%9E%85%EB%AC%B8-%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8)
