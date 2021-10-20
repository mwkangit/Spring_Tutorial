# Before Starting Spring

- #### Spring은  EJB에서 발전한 프레임워크이다. EJB의 매우 어려운 로직을 해결하기 위해 spring이 탄생하여 객체 지향형 프로그래밍을 사용하기 용이해졌다.

- #### Spring Boot는 스프링을 도와주는 도구로 다른 기능을 사용하기 쉽게 해준다. Reactive programming을 가능하게 하여 non-blocking과 asynchronous programming이 가능하다.

- #### 객체 지향형 프로그램에는 5가지 원칙이 있다.

  #### SOLID

  - #### SRP : 단일 책임 원칙으로 한 클래스는 하나의 책임만 가져야 한다.  변경 사항이 있을 때 파급 효과가 적으면 단일 책임 원칙이 잘 적용된 것이다.

  - #### OCP : 개방-폐쇄 원칙은 확장에는 열려 있으나 변경에는 닫혀 있어야 한다. 즉, 새로운 클래스를 생성해도 client 코드는 변경하지 않고 그대로 사용해야 한다. 객체를 생성하고, 연관관계를 맺어주는 별도의 조립, 설정자가 필요하다. Spring Container의 DI, IoC를 사용해야 구현가능 하다

  - #### LSP : 리스코프 치환 원칙은 정확한 기능을 구현해야한다는 것이다. 즉, 하위 클래스는 인터페이스 규약을 지켜야 한다.

  - #### ISP : 인터페이스 분리 원칙은 하나의 큰 인터페이스를 적절한 여러개의 인터페이스로 관리하여 객체 지향형을 만드는 것이다.

  - #### DIP : 의존관계 역전 원칙은 클라이언트가 인터페이스만 의존해야한다는 것이다. 하지만 인터페이스는 하위 클래스에 상속되어 객체를 만들 때 클라이언트는 하위 클래스도 의존하게 된다. DIP는 spring container의 DI, IoC를 사용해야 구현가능 하다.



# Member

- ### 다형성과 SOLID를 반영하기 위해 각 객체는 인터페이스로 형성 후 상속하여 클래스로 구현한다.

![Member Domain Relation](/media/mwkang/Klevv/Spring 일지/스프링 기본/Member Domain Relation.png)

![Member Class Diagram](/media/mwkang/Klevv/Spring 일지/스프링 기본/Member Class Diagram.png)

- #### Member 도메인과 클래스 다이어그램을 통해 member의 다형성을 확보하기 위해 여러개의 인터페이스와 클래스로 나누었다.



```java
public class Member {

    private Long id;
    private String name;
    private Grade grade;

    public Member(Long id, String name, Grade grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}

```



```java
public class MemoryMemberRepository implements MemberRepository{

    // 원래 concurrent HashMap 사용해야 한다
    private static Map<Long, Member> store = new HashMap<>();

    @Override
    public void save(Member member) {
        store.put(member.getId(), member);
    }

    @Override
    public Member findById(Long memberId) {
        return store.get(memberId);
    }
}

```



```java
public class MemberServiceImpl implements MemberService{

    // interface에 의존중이지만 구현체에도 의존중이다. 즉, 추상화에도 의존하고 구체화에도 의존한다. DIP 위반
    private final MemberRepository memberRepository = new MemoryMemberRepository();



    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}

```

- #### 인터페이스를 생성하여 어떠한 기능을 사용할 것인지 명시한 뒤 데이터를 다루는 repository와 client가 접근 할 service를 나누어서 프로그래밍 하였다.



# Order

![Order Domain](/media/mwkang/Klevv/Spring 일지/스프링 기본/Order Domain.png)

![Order Class Diagram](/media/mwkang/Klevv/Spring 일지/스프링 기본/Order Class Diagram.png)

- #### Order 도메인과 클래스 다이어그램을 통해 사용할 메소드를 지정하여 다형성을 확보하였다.



```java
public class Order {

    private Long memberId;
    private String itemName;
    private int itemPrice;
    private int discountPrice;

    public Order(Long memberId, String itemName, int itemPrice, int discountPrice) {
        this.memberId = memberId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.discountPrice = discountPrice;
    }

    public int calculatePrice(){
        return itemPrice - discountPrice;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }

    // order 객체를 출력하면 toString()이 출력된다
    @Override
    public String toString() {
        return "Order{" +
                "memberId=" + memberId +
                ", itemName='" + itemName + '\'' +
                ", itemPrice=" + itemPrice +
                ", discountPrice=" + discountPrice +
                '}';
    }
}

```



```java
public class OrderServiceImpl implements OrderService{

    private final MemberRepository memberRepository = new MemoryMemberRepository();
    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        // 단일 책임 원칙 잘지켰다. 할인에 대한것은 할인 객체만 변경해주면 되기 때문이다.
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}

```



```java
public class FixDiscountPolicy implements DiscountPolicy{

    private int discountFixAmount = 1000; // 1000원 할인

    @Override
    public int discount(Member member, int price) {
        // enum은 == 사용 가능하다
        if(member.getGrade() == Grade.VIP){
            return discountFixAmount;
        } else {
            return 0;
        }
    }
}

```

```java
public class RateDiscountPolicy implements DiscountPolicy {

    private int discountPercent = 10;

    @Override
    public int discount(Member member, int price) {
        if(member.getGrade() == Grade.VIP){
            return price * discountPercent / 100;
        } else {
            return 0;
        }
    }
}

```

- #### Order에 toString() 메소드를 적용하여 내부의 데이터를 모두 출력할 수 있게 하였다.

- #### 정액 할인과 정률 할인은 도메인에 작성한대로 객체화하여 따로 구현하여 정률 할인 도입 예정이면 간편하게 변경할 수 있도록 하였다.



![DIP, OCP 불충분](/media/mwkang/Klevv/Spring 일지/스프링 기본/DIP, OCP 불충분.png)

```java
//    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
```



- #### DiscountPolicy  인터페이스를 FixDiscountPolicy, RateDiscountPolicy 두개로 나누고 상속하여 다형성을 완성한 것 같지만 아니다. OrderServiceImpl 클래스에서 discountPolicy를 정액 혹은 정률 할인 클래스로 연결해줘야 한다. 이때 정액, 정률 클래스에도 의존하게 되므로 DIP를 만족하지 못 한다. 또한, 할인 정책을 바꿀 때마다 new로 객체를 client 코드에서 변경해줘야 하므로 OCP도 만족하지 못 한다.



```java
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;
```



- #### 객체 지향형 프로그래밍을 준수하기 위해 client 코드에서 인터페이스에만 의존하도록 해야 한다.

- #### 현재 NPE(Null Pointer Exception)이 발생하므로 다른 클래스 및 메서드에서 구현 객체를 대신 생성하고 주입해야 한다.



# 관심사 분리

- ### 관심사 분리는 객체를 생성하고 연결하는 역할과 실행하는 역할을 명확하게 분리하는 것이다.

- ### Application의 전체 동작 방식을 구성하기 위해, 구현 객체를 생성하고, 연결하는 책임을 가지는 별도의 설정 클래스인 AppConfig를 생성한다.(구성 및 설정 클래스)

![AppConfig 생성자 주입](/media/mwkang/Klevv/Spring 일지/스프링 기본/AppConfig 생성자 주입.png)

```java
public class AppConfig {

    // service에 DIP를 확보하기 위해 객체를 주입하는 생성자 주입을 구현한다
    public MemberService memberService(){
        return new MemberServiceImpl(memberRepository());
    }

    // memberService, orderService에 memoryRepository를 생성한다
    // 이렇게 하여 각자의 역할을 나눠 줄 수 있다
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    public OrderService orderService(){
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    public DiscountPolicy discountPolicy(){
//        return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }
}
```

```java
// MemberServiceImpl 생성자 주입을 위한 생성자 추가
public class MemberServiceImpl implements MemberService{

    // interface에 의존중이지만 구현체에도 의존중이다. 즉, 추상화에도 의존하고 구체화에도 의존한다. DIP 위반
//    private final MemberRepository memberRepository = new MemoryMemberRepository();

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
}
```



```java
// OrderServiceImpl 생성자 주입을 위한 생성자 추가
public class OrderServiceImpl implements OrderService{

//    private final MemberRepository memberRepository = new MemoryMemberRepository();
    // DIP 위반 코드 (new 로 구체 클래스도 의존)
//    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
//    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
    // DIP 준수 코드
    // 완성하기 위해 DiscountPolicy를 알아서 주입해주는 클래스나 메소드를 만들어줘야한다.
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
}
```

- #### AppConfig에서 application의 실제 동작에 필요한 구현 객체를 생성한다.

- #### 생성한 객체 인스턴스의 참조(레퍼런스)를 생성자를 통해서 주입(injection)해준다. 이러한 로직을 생성자 주입이라고 한다.

- #### 생성자 주입을 통해 DIP를 완성할 수 있다.

- #### DI(Dependency Injection)은 의존관계를 외부에서 주입한다는 뜻이다.

- #### Config 클래스 또한 refactoring하여 역할에 따른 구현이 잘 보일 수 있게 해야한다.(중복 제거 후 변경에 용이하게 한다)

![사용, 구성 영역](/media/mwkang/Klevv/Spring 일지/스프링 기본/사용, 구성 영역.png)

- #### AppConfig로 application이 사용 영역과 객체를 생성하고 구성하는 영역으로 분리되었다.

- #### 어떠한 변경 사항이 적용될 때 사용 영역의 코드는 변경하지 않으며 구성 영역 코드만 변경한다.

- #### 변경사항이 적용되어도 client 코드는 변경해 줄 필요가 없으므로 OCP가 완성되었다.



# IoC, DI

- ### IoC(Inversion of Control)은 제어의 역전으로 개발자가 호출하는게 아닌 framework가 제어권을 통제하여 대신 호출하는 것이다. 즉, AppConfig가 제어권을 가진다. AppConfig가 정해주는대로 인터페이스가 구현 객체를 실행한다.

- #### Framework는 내가 작성한 코드를 제어하고, 대신 실행하는 것이다. 테스트 수행 시 JUnit test framework가 자신만의 lifecycle생성 후 제어권을 가져서 대신 실행한다.

- #### Library는 개발자가 작성한 코드가 직접 제어의 흐름을 담당하여 직접 불러서 호출하는 것이다.

- #### DI(Dependency Injection)은 의존관계 주입으로 정적인 클래스 의존 관계, 실행 시점에 결정되는 동적인 객체(인스턴스) 의존 관계로 분리된다.

  - #### 정적인 클래스 의존관계 : import 코드 및 클래스 다이어그램을 확인하여 application을 실행하지 않아도 의존관계를 확인할 수 있다.

  - #### 동적인 객체 인스턴스 의존 관계 : Application 실행 시점에 생성된 객체 인스턴스의 참조가 연결된 의존 관계다.

- #### DI를 사용하여 client 코드를 변경하지 않고 client가 호출하는 대상의 타입 인스턴스를 변경할 수 있다. 또한, 정적인 클래스 의존관계를 변경하지 않고 동적인 객체 인스턴스 의존관계를 쉽게 변경할 수 있다.

- #### AppConfig와 같이 객체를 생성하고 관리하면서 의존관계를 연결해 주는 것을 IoC 컨테이너, DI 컨테이너라고 한다. Applcation을 한 클래스에서 조립한다고 하여 어샘블러, 오브젝트 팩토리라고도 한다.



# Spring 기반 변경

```java
@Configuration
public class AppConfig {

    // service에 DIP를 확보하기 위해 객체를 주입하는 생성자 주입을 구현한다
    @Bean
    public MemberService memberService(){
        return new MemberServiceImpl(memberRepository());
    }

    // memberService, orderService에 memoryRepository를 생성한다
    // 이렇게 하여 각자의 역할을 나눠 줄 수 있다
    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService(){
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public DiscountPolicy discountPolicy(){
//        return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }
}

```

```java
// MemberApp에 Spring Container사용
public class MemberApp {

    public static void main(String[] args) {
//        AppConfig appConfig = new AppConfig();
//        MemberService memberService = appConfig.memberService();
//        MemberService memberService = new MemberServiceImpl();

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);

        // Long 타입이므로 L 붙인다
        Member member = new Member(1L, "memberA", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);
        System.out.println("new member " + member.getName());
        System.out.println("find Member = " + findMember.getName());

    }
}
```

```java
// OrderApp에 Spring Container사용
public class OrderApp {
    public static void main(String[] args) {

//        AppConfig appConfig = new AppConfig();
//        MemberService memberService =appConfig.memberService();
//        OrderService orderService = appConfig.orderService();
//        MemberService memberService = new MemberServiceImpl(null);
//        OrderService orderService = new OrderServiceImpl(null, null);

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
        OrderService orderService = applicationContext.getBean("orderService", OrderService.class);

        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId, "itemA", 10000);

        System.out.println("order = " + order.toString());
        System.out.println("order.calculatePrice() = " + order.calculatePrice());


    }
}

```

- #### @Configuration은 application의 구성정보를 담당한다는 뜻이다.

- #### @Bean은 spring container에 등록할 항목을 지정하는 것이다.

- #### Spring은 모든 것이 ApplicationContext로 시작된다. Spring container이라고 생각해도 되며 bean이 등록된 객체를 관리한다.

- #### ApplicationContext에 configuration은 AnnotationConfigApplicationContext 선언하며 인자로 configuration 클래스를 넣어서 내부의 bean을 모두 등록한다.

- #### Application.getBean()은 spring container에 등록된 bean을 가져오는 것으로 첫 번째 인자로 메서드명을 넣고 두 번째 인자로 타입을 넣는다.

- #### Spring container에 등록된 객체를 spring bean이라고 한다.



Image 출처 : [김영한 스프링 입문](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EC%9E%85%EB%AC%B8-%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8)