# Spring Container & Bean

- #### ApplicationContext를 spring container이라고 부르며 인터페이스여서 다형성이 적용된다.

- #### AnnotationConfigApplicationContext는  java annotation기반  config설정으로 spring bean을 만든다는 뜻이다.


![스프링빈등록](https://user-images.githubusercontent.com/79822924/138466945-aee3344e-7c76-4d46-a72f-820e5c56ea15.png)

- #### 형성된 스프링 컨테이너에 key, value형태로 빈을 등록한다.

- #### 빈 이름은 key로 메소드 이름이 default로 들어간다.

- #### 빈 객체는 value로 반환하는 값이 들어간다.


![스프링빈의존관계완료](https://user-images.githubusercontent.com/79822924/138466978-6a05b33b-d2aa-47c6-b5b7-aa6716e497c7.png)


- #### 스프링 컨테이너는 설정 정보를 참고하여 의존관계(DI)를 주입한다.

- #### 자바코드로 스프링 빈을 등록하면 생성자를 호출하면서 의존관계 주입도 한번에 처리된다.

```java
    @Test
    @DisplayName("애플리케이션 빈 출력하기")
    public void findApplicationBean() throws Exception {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

            if(beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION){
                Object bean = ac.getBean(beanDefinitionName);
                System.out.println("name = " + beanDefinitionName + " object = " + bean);
            }

        }

    }
```

- #### getBeanDefinitionNames()는 스프링에 등록된 모든 빈 이름을 조회한다.

- #### getRole()에서 ROLE_APPLICATION은 일반적으로 사용자가 정의한 빈이고 ROLE_INFRASTRUCTURE은 스프링이 내부에서 사용하는 빈이다.

- #### getBean()을 통해 등록된 빈을 조회한다. 파라미터로는 빈 이름, 반환형을 넣는다(빈 이름만 넣어도 실행 가능하다).

- #### 스프링 빈을 타입으로 조회 시 같은 타입의 스프링 빈이 둘 이상이면 오류가 발생하여 빈 이름을 넣어서 사용해야한다.

- #### getBeanOfType()을 사용하면 해당 타입의 모든 빈을 조회할 수 있다.

- #### 스프링 비은 상속관계를 반영하여 부모 타입으로 조회하면 자식 타입도 함께 조회한다. 즉, object 타입으로 조회하면 모든 스프링 빈을 조회한다.

- #### 빈에 등록할 때에는 인터페이스를 반환하는 것이 의존관계 생성 시 유용하다.

- #### BeanFactory는 스프링 컨테이너의 최상위 인터페이스이며 스프링 빈을 관리하고 조회한다.

- #### ApplicationContext는 BeanFactory의 빈 관리기능과 편리한 부가기능을 이용하는 것이다(ApplicationContext는 BeanFactory이외에 다른 인터페이스도 상속받아서 여러 부가 기능을 제공한다).



![ApplicationContext설정사용](https://user-images.githubusercontent.com/79822924/138467018-1a13ceb5-b16a-4814-9626-35f8696cd138.png)

- #### ApplicationContext에 java, XML, 임의로 제작한 설정을 사용하여 빈 등록을 하여 이용할 수 있다.



![BeanDefinition](https://user-images.githubusercontent.com/79822924/138467043-a582ddf4-4ece-4539-8c1d-7418edf20f49.png)

- #### 스프링 컨테이너는 BeanDefinition을 통해 메타정보를 기반으로 스프링 빈을 생성한다. 스프링 컨테이너는 BeanDefinition을 상속받아서 역할과 구현을 나눴다. 즉, 스프링 컨테이너는 설정정보가 java인지 XML인지 알 필요없이 BeanDefinition을 통해 정보를 인지하면 된다.

- #### BeanDefinition에는 Scope(싱글톤), lazyInit(지연처리 여부) 등 정보가 있다.



# Singleton Pattern

- #### 싱글톤 패턴이란 클래스의 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴이다.

  

```java
public class SingletonService {

    private static final SingletonService instance = new SingletonService();

    public static SingletonService getInstance(){
        return instance;
    }

    private SingletonService(){
    }

    public void logic(){
        System.out.println("싱글톤 객체 로직 호출");
    }

}

```

- #### 객체를 미리 생성해두는 가장 단순하지만 안전한 싱글톤 구현 방법이다.

- #### 클래스가 자기자신을 내부에 private static으로 하나 가지고 있어서 객체가 class level에 올라가므로 실행 시 딱 1개만 생성된다.

- #### 생성자를 private으로 선언해서 외부에서 new 키워드를 사용한 객체 생성을 막아서 유일한 객체를 유지한다.

- #### 이러한 방법으로 싱글톤을 이용하면 의존관계상 클라이언트가 구체 클래스에 의존하여 DIP와 OCP 원칙을 위반한다.

- #### 또한, 유연성이 떨어져서 DI를 적용하기 어려운 상황이 발생하기도 한다.



```java
    @Test
    @DisplayName("스프링 컨테이너와 싱글톤")
    void springContainer(){

//        AppConfig appConfig = new AppConfig();
        ApplicationContext ac = new AnnotationConfigApplicationContext((AppConfig.class));

        // 1. 조회 : 호출할 때 마다 객체를 생성
        MemberService memberService1 = ac.getBean("memberService", MemberService.class);

        // 2. 조회 : 호출할 때 마다 객체를 생성
        MemberService memberService2 = ac.getBean("memberService", MemberService.class);

        // 참조값이 다른 것을 확인
        System.out.println("memberService1 = " + memberService1);
        System.out.println("memberService2 = " + memberService2);

        // memberService1 == memberService2
        assertThat(memberService1).isSameAs(memberService2);
    }
```

- #### 스프링 컨테이너는 싱글톤 패턴 문제점을 해결하고 기본적으로 객체를 싱글톤으로 만들어서 저장한다. 스프링 빈이 싱글톤으로 관리되는 빈이다.

- #### 스프링 컨테이너처럼 싱글톤 객체를 생성하고 관리하는 기능을 싱글톤 레지스트리라고 한다.

- #### 스프링 컨테이너로 DIP, OCP, 테스트, private 생성자로부터 자유로울 수 있다.

- #### 스프링 컨테이너는 사용자의 요청이 올 때마다 객체를 생성하는 것이 아니라 이미 만들어진 객체를 공유해서 효율적으로 재사용할 수 있다.

- #### 싱글톤 객체는 상태를 유지하게 설계하면 안돼며 무상태로 설계해야 한다. 즉, 의존적인 필드가 있으면 안돼고 읽기만 가능한 값 변경이 가능한 필드는 존재하면 안됀다. 필요하다면 자바에서 공유되지 않는 지역변수, 파라미터, threadlocal을 사용해야 한다. 자칫하면 사용자의 개인 데이터가 변하는 현상이 발생할 수 있다.



```java
    @Bean
    public MemberService memberService(){
        System.out.println("call AppConfig.memberService");
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public OrderService orderService(){
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }
    
        @Bean
    public MemberRepository memberRepository() {
        System.out.println("call AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }
```

- #### memberService와 orderService 빈을 통해 MemberRepository 객체가 연속 생성되어 싱글톤이 깨지는 것처럼 보이지만 MemberRepository는 빈에 싱글톤으로 1개의 객체만 등록된다.

```java
    @Test
    void configurationDeep(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        AppConfig bean = ac.getBean(AppConfig.class);

        System.out.println("bean = " + bean.getClass());
    }
```



- #### 스프링은 클래스의 바이트코드를 조작하는 라이브러리를 이용하여 싱글톤에 적용한다.

- #### 빈에 등록된 객체의 클래스의 타입을 출력하면 CGLIB를 볼 수 있는데 이것은 내가 만든 클래스가 아니라 스프링이 CGLIB라는 바이트코드 조작 라이브러리를 사용하여 @Configuration이 붙은 클래스를 상속받은 임의의 다른 클래스를 만들고 그 다른 클래스를 스프링 빈에 등록한 것이다.

- #### CGLIB로 만들어진 다른 클래스는 @Bean이 붙은 메소드마다 이미 스프링 빈이 존재하면 존재하는 빈을 반환하고 스프링 빈이 없으면 생성하여 등록하고 반환하는 코드가 동적으로 생성되어 싱글톤을 보장한다.

- #### 이때 @Configuration 없이 @Bean만 존재하면 CGLIB 라이브러리를 지원하지 않아서 싱글톤이 보장되지 않을 수 있다.



# Component Scan

```java
@Configuration
@ComponentScan(
        basePackages = {"hello.core"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {



}

```

```java
@Component
public class OrderServiceImpl implements OrderService{


    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
}
```

- #### @Component를 통해 Bean에 자동으로 등록할 수 있으며 @Autowired를 통해 의존관계를 자동으로 주입할 수 있다.

- #### 인터페이스가 아닌 구현 클래스에 @Component를 작성하는 것이다.

- #### @ComponentScan은 @Component가 붙은 모든 클래스를 스프링 빈으로 등록한다. 이때 default로 클래스명으로 빈에 등록하지만 맨 앞글자만 소문자로 사용한다. @Component에 인자로 빈 이름을 부여할 수 있다.



![Autowired](https://user-images.githubusercontent.com/79822924/138467093-b160b7c7-0c51-4a3c-8fe9-7a30169279f0.png)

- #### @Autowired를 생성자에 지정하면 스프링 컨테이너가 자동으로 해당 스프링 빈을 찾아서 의존관계를 주입한다. 이때 기본 조회 전략은 타입이 같은 빈을 찾아서 주입하는 것이며 getBean(반환 클래스 타입)과 같은 의미이다.



```java
@ComponentScan(
        basePackages = {"hello.core"}
)
```

- #### basePackages로 탐색할 패키지의 시작위치를 지정할 수 있다. 이 패키지를 포함하여 하위 패키지를 모두 탐색한다.

- #### basePackageClasses로 지정한 클래스의 패키지를 탐색 시작 위치로 지정할 수 있다.

- #### @ComponentScan의 default는 어노테이션이 붙은 클래스의 패키지를 시작 위치로 지정한다.

- #### @ComponentScan이 붙은 설정 정보 클래스는 프로젝트의 최상단에 배치하는 것이 좋다.

- #### 스프링 부트 대표 시작 정보인 @SpringBootApplication도 프로젝트 시작 루트 위치에 배치되어 있으며 이 어노테이션 안에 @ComponentScan이 내재되어 있다.

- #### @ComponentScan은 @Component(컴포넌트 스캔에서 사용), @Controller(스프링 MVC 컨트롤러에서 사용), @Service(스프링 비즈니스 로직에서 사용), @Repository(스프링 데이터 접근 계층에서 사용), @Configuration(스프링 설정 정보에서 사용)을 대상으로 빈에 등록한다.

- #### 각 어노테이션은 스프링이 부가기능을 부여한다. @Controller은 스프링 MVC 컨트롤러로 인식, @Repository는 스프링 데이터 접근 계층으로 인식하고 데이터 계층의 예외를 스프링 예외로 변환하고, @Configuration은 스프링 설정 정보로 인식하고 스프링 빈이 싱글톤을 유지하도록 처리하고, @Service는 특별한 처리는 없지만 핵심 비즈니스 로직이 존재하는 비즈니스 계층을 인식하는데 도움을 준다.

- #### 어노테이션은 상속관계라는 것이 없는 것이 일반적이지만 어노테이션이 특정 어노테이션을 상속하는 것처럼 인식할 수 있는 것은 자바 언어가 지원하는 것이 아닌 스프링이 지원하여 가능한 것이다.



```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyExcludeComponent {
}
```

```java
@MyExcludeComponent
public class BeanB {
}
```

```java
	@Configuration
    @ComponentScan(
            includeFilters = {
                @Filter(type = FilterType.ANNOTATION, classes = MyIncludeComponent.class)
                             },
            excludeFilters = {
                @Filter(type = FilterType.ANNOTATION, classes = MyExcludeComponent.class),
                @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = BeanA.class)
            }
        
    )
    static class ComponentFilterAppConfig{
    }
```

- #### 어노테이션을 새로 생성하여 ComponentScan기능을 부여한 뒤 각 어노테이션에 대해 포함할지 결정할 수 있다.

- #### FilterType에는 5가지가 있다.

  - #### ANNOTATION : 기본값이며 어노테이션을 인식하여 동작한다.

  - #### ASSIGNABLE_TYPE : 지정한 타입과 자식 타입을 인식해서 동작한다. 즉, 클래스를 직접 지정한다.

  - #### ASPECTJ : AspectJ 패턴을 사용한다.

  - #### REGEX : 정규 표현식이다.

  - #### CUSTOM : TypeFilter이라는 인터페이스를 직접 구현하여 처리한다.



```java
@Configuration
@ComponentScan(
        basePackages = {"hello.core"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {
    @Bean(name = "memoryMemberRepository")
    public MemberRepository memberRepository(){
        return new MemoryMemberRepository();
    }
}
```

```java
@Component
public class MemoryMemberRepository implements MemberRepository{}
```

- #### 자동 빈 등록이 중복되어 충돌이 일어나면  ConflictingBeanDefinitionException 예외가 발생하여 실행을 중지한다.

- #### 자동 빈 등록과 수동 빈 등록이 중복되어 실행되면 스프링 부트에서 오류가 발생하도록 해놓았다.

- #### Application.properties에서 default로 overriding이 false로 되어 있는 것을 true로 바꾸면 수동 빈 등록이 자동 빈 등록보다 우선권을 가지게 되어 overriding이 가능해진다.



# Auto Dependency Injection



```java
@Component
public class OrderServiceImpl implements OrderService {
	private final MemberRepository memberRepository;
	private final DiscountPolicy discountPolicy;
	@Autowired
	public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
	this.memberRepository = memberRepository;
	this.discountPolicy = discountPolicy;
	}
}
```

- #### 생성자 주입은 생성자를 통해 클래스 객체 생성 시점에 바로 의존관계를 주입하는 것이다.

- #### 생성자 주입 시 생성자가 1개이면 @Autowired를 제외할 수 있다.

- #### 생성자 주입은 스프링 컨테이너가 만들어질 때 동시에 의존관계 주입이 일어난다.

- #### 생성자 주입은 의존관계가 어플리케이션 종료 시점까지 1번만 호출되어 불변하게 유지할 수 있다. 하지만 수정자 주입은 setter 메소드를 public으로 열어둬서 누군가가 의존관계를 변경하게 될 수도 있으므로 위험한 설계가 될 수 있다.

- #### 수정자 의존관계인 경우 NPE가 발생할 수 있는데 이것은 setter 메소드를 실행하지 않아서 발생한다. 하지만 이미 setter 메소드를 감싸는 클래스를 선언하지 못하므로 setter 메소드를 실행할 수 없는 문제가 발생한다. 또한, 누락된 의존관계를 관리하기 까다롭다.

- #### 생성자 주입은 final 키워드를 사용하여 생성자에서 혹시라도 값이 설정되지 않는 오류를 컴파일 시점에서 방지할 수 있다. Final 키워드는 선언과 정의를 동시에 해야하는 키워드이다.

- #### 생성자 주입은 프레임워크에 의존하지 않고 순수한 자바 언어의 특징을 잘 살리는 방법이다.

- #### 생성자 주입을 선택하고 필요 시 수정자 주입을 선택하고 필드 주입은 테스트 이외에 사용하지 않는 것이 좋다.



```java
@Component
public class OrderServiceImpl implements OrderService {
	private MemberRepository memberRepository;
	private DiscountPolicy discountPolicy;
	@Autowired
	public void setMemberRepository(MemberRepository memberRepository) {
	this.memberRepository = memberRepository;
	}
	@Autowired
	public void setDiscountPolicy(DiscountPolicy discountPolicy) {
	this.discountPolicy = discountPolicy;
	}
}
```

- #### 수정자 주입은 자바 빈 프로퍼티 규약에 있는 수정자 메소드를 사용하는 방식을 사용한 의존관계 주입 방법이다.

- #### Setter을 호출하는 방식으로 컨테이너의 빈을 조작하는 것이 가능해서 위험하다.

- #### @Autowired는 default로 주입할 대상이 없으면 오류가 발생하지만 @Autowired(required = false)를 통해 선택적 주입을 구현할 수 있다.



```java
@Component
public class OrderServiceImpl implements OrderService {
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private DiscountPolicy discountPolicy;
}
```

- #### 필드 주입은 필드에 바로 의존관계를 주입하는 것이다.

- #### 외부에서 변경이 불가능해서 테스트하기 힘들다는 단점이 있다.

- #### DI 프레임워크가 없다면 아무것도 할 수 없다.

- #### 주로 테스트 코드나 스프링 설정을 목적으로 하는 @Configuration 같은 곳에서 사용한다.

- #### @SpringBootTest를 이용하여 스프링 컨테이너를 테스트에 통합하여 @Autowired를 자유롭게 사용할 수 있다. @SpringBootTest는 자동으로 스프링 컨테이너에 테스트 토합하여 사용한다.



```java
@Component
public class OrderServiceImpl implements OrderService {
	private MemberRepository memberRepository;
	private DiscountPolicy discountPolicy;
	@Autowired
	public void init(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
	this.memberRepository = memberRepository;
	this.discountPolicy = discountPolicy;
	}
}
```

- #### 일반 메소드 주입은 일반 메소드를 통해 의존관계를 주입하는 것이다.

- #### 한번에 여러 필드를 주입할 수 있지만 생성자 주입으로 모두 가능하기 때문에 잘 사용하지 않는다.



```java
    static class TestBean{

        @Autowired(required = false)
        public void SetNoBean1(Member noBean1){
            System.out.println("noBean1 = " + noBean1);
        }

        @Autowired
        public void SetNoBean2(@Nullable Member noBean2){
            System.out.println("noBean1 = " + noBean2);
        }

        @Autowired
        public void SetNoBean3(Optional<Member> noBean3){
            System.out.println("noBean3 = " + noBean3);
        }

    }
```

- #### 주입할 스프링 빈이 없어도 동작할 수 있는 방법이 3가지 있다.

  - #### @Autowired(required = false) : 자동 주입할 대상이 없으면 수정자 메소드 자체가 호출이 되지 않는다.

  - #### org.springframework.lang.@Nullable : 자동 주입할 대상이 없으면 null 입력된다. 

  - #### Optional<> : 자동 주입할 대상이 없으면 Optional.empty가 입력된다. 값이 있으면 optional로 감싼다. Optional은 값이 null일 수도 있는 조건도 감싸는 자료형으로 많이 사용된다.



```java
@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	private final MemberRepository memberRepository;
	private final DiscountPolicy discountPolicy;
}
```

```groovy
configurations {
	compileOnly{
		extendsFrom annotationProcessor
	}
}	
dependencies{
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'
	testCompileOnly 'org.projectlombok:lombok'
	testAnnotationProcessor 'org.projectlombok:lombok'
}
```

- #### Lombok은 객체에 대한 getter, setter을 자동으로 생성하는 어노테이션을 제공하는 동시에 @RequiredArgsConstructor을 통해 생성자 주입을 간편하게 구현할 수 있게 한다. 즉, lombok은 자바의 어노테이션 프로세서 기능을 이용하여 컴파일 시점에 생성자 코드를 자동을 생성한다. 실제 클래스를 열어보면 생성자가 만들어진 것을 확인 할 수 있다.

- #### Gradle에 dependencies를 추가하면 lombok을 이용할 수 있다.



```java
@Component
public class FixDiscountPolicy implements DiscountPolicy {}
```

```java
@Component
public class RateDiscountPolicy implements DiscountPolicy {}
```

- #### 타입으로 조회 시 같은 타입의 빈이 2개 이상 존재하면 NoUniqueBeanDefinitionException 오류가 발생한다.

- #### 오류를 피하기 위해 하위 타입을 지정할 수 있지만 이것은  역할이 아닌 구현에 의존하는 것으로 DIP를 위배하여 유연성이 떨어질 수 있다.



```java
@Autowired
private DiscountPolicy rateDiscountPolicy
```

- #### @Autowired는 타입 매칭 시 2개 이상으로 타입이 존재하면 필드 이름, 파라미터 이름으로 매칭을 재시도 한다.



```java
@Component
@Qualifier("mainDiscountPolicy")
public class RateDiscountPolicy implements DiscountPolicy {}
```

```java
@Autowired
public OrderServiceImpl(MemberRepository memberRepository,
@Qualifier("mainDiscountPolicy") DiscountPolicy
discountPolicy) {
	this.memberRepository = memberRepository;
	this.discountPolicy = discountPolicy;
}
```

- #### @Qualifier는 추가 구분자를 붙여줘서 빈을 구별하도록 하는 방법이다. 빈 이름을 변경하는 것이 아닌 추가적인 구분자를 제공하는 것이다.

- #### 타입 매칭 시 Qualifier끼리 매칭을 시도한 뒤 빈 이름으로 매칭한다. 만약 아직까지 빈을 찾지 못하면 예외를 발생한다.



```java
@Component
@Primary
public class RateDiscountPolicy implements DiscountPolicy {}

@Component
public class FixDiscountPolicy implements DiscountPolicy {}
```

- #### @Primary로 우선권을 부여하여 타입 매칭을 할 수 있다.

- #### 주요 처리를 위한 부분에는 @Primary를 사용하여 간편하게 빈을 이용하고 서브 처리를 위한 부분에는 @Qualifier을 사용하여 필요한 순간에 빈을 사용하는 방법으로 구현하는 것이 좋다.

- #### 스프링은 자동보다 수동이, 넓은 범위의 선택권보다 좁은 범위의 선택권이 우선 순위가 높아서 @Primary와 @Qualifier이 동시에 사용되면 @Qualifier이 우선권을 가진다.



```java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier("mainDiscountPolicy")
public @interface MainDiscountPolicy {
}

```

- #### 어노테이션을 직접 생성하여 Qualifier과 같은 function을 직접 구현할 수 있다.



```java
@Test
    void findAllBean(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class);

        DiscountService discountService = ac.getBean(DiscountService.class);
        Member member = new Member(1L, "userA", Grade.VIP);
        int discountPrice = discountService.discount(member, 10000, "fixDiscountPolicy");

        assertThat(discountService).isInstanceOf(DiscountService.class);
        assertThat(discountPrice).isEqualTo(1000);

        int rateDiscountPrice = discountService.discount(member, 20000, "rateDiscountPolicy");

        assertThat(rateDiscountPrice).isEqualTo(2000);
    }

    static class DiscountService{
        private final Map<String, DiscountPolicy> policyMap;
        private final List<DiscountPolicy> policies;

        @Autowired
        public DiscountService(Map<String, DiscountPolicy> policyMap, List<DiscountPolicy> policies) {
            this.policyMap = policyMap;
            this.policies = policies;
            System.out.println("policyMap = " + policyMap);
            System.out.println("policies = " + policies);
        }

        public int discount(Member member, int price, String discountCode) {
            DiscountPolicy discountPolicy = policyMap.get(discountCode);
            return discountPolicy.discount(member, price);
        }
    }
```

- #### List는 이름 없이 인스턴스에 대한 정보를 입력받고 Map은 이름이 key로 저장되고 빈 정보 타입이 value로 저장된다. 즉, 같은 타입으로 되어있는 빈이 모두 Map에 저장되는 것이다.

- #### Map에 입력된 빈은 이름이 key값이므로 이름을 통해 value를 반환할 수 있다.

- #### 만약 해당 타입 빈이 존재하지 않는다면 list와 map은 비어있는 값을 가지게 된다.



## DI in Business

- #### 어플리케이션은 2가지 로직으로 나눌 수 있다.

  - #### 업무 로직 빈 : 컨트롤러, 서비스. 리포지토리 등 비즈니스 요구사항을 개발할 때 추가되거나 변경된다.

  - #### 기술 지원 빈 : 공통 관심사(AOP) 등을 처리할 때 사용되며 업무 로직을 지원하기 위한 하부 기술이나 공통 기술이다.

- #### 업무 로직은 숫자가 많으며 유사한 패턴이 존재하여 자동 기능을 적극 사용하는 것이 좋다.

- #### 기술 지원 로직은 수가 적고 광범위하게 영향을 미치기 때문에 가급적 수동 빈 등록을 사용하여 빠르게 파악할 수 있도록 하는 것이 좋다.

- #### 비즈니스 로직 중에서도 다형성을 적극 활용할 때 수동 빈 등록으로 명확하게 표현하는 것이 좋다. 설정정보를 한눈에 보지 못한다면 오류가 발생해도 탐지하기 어렵다. 또 다른 방법으로는 자동 빈 등록을 해도 특정 패키지에 같이 묶어서 한눈에 알아볼 수 있도록 하는 것이다.

- #### 즉, 스프링 부트가 아닌 내가 직접 기술 지원 객체를 스프링 빈에 등록한다면 수동으로 등록해서 명확하게 들어내는 것이 좋다.



# Keyboard Shortcut

- #### soutm은 메소드명으로 sout한다.

- #### soutv는 변수명으로 sout한다.

- #### ctrl + alt + enter은 가르키는 곳 윗 부분에 새로운 줄을 생성한다

- #### ctrl + shift + enter은 가르키는 곳 아래 부분에 새로운 줄을 생성한다.



Image 출처 : [김영한 스프링 입문](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EC%9E%85%EB%AC%B8-%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8)

