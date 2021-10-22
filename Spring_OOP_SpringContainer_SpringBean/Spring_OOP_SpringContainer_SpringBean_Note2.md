# Spring Container & Bean

- #### ApplicationContext를 spring container이라고 부르며 인터페이스여서 다형성이 적용된다.

- #### AnnotationConfigApplicationContext는  java annotation기반  config설정으로 spring bean을 만든다는 뜻이다.



![스프링빈등록](/media/mwkang/Klevv/Spring 일지/스프링 기본/10.16/스프링빈등록.png)

- #### 형성된 스프링 컨테이너에 key, value형태로 빈을 등록한다.

- #### 빈 이름은 key로 메소드 이름이 default로 들어간다.

- #### 빈 객체는 value로 반환하는 값이 들어간다.



![스프링빈의존관계완료](/media/mwkang/Klevv/Spring 일지/스프링 기본/10.16/스프링빈의존관계완료.png)

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



![ApplicationContext설정사용](/media/mwkang/Klevv/Spring 일지/스프링 기본/10.16/ApplicationContext설정사용.png)

- #### ApplicationContext에 java, XML, 임의로 제작한 설정을 사용하여 빈 등록을 하여 이용할 수 있다.



![BeanDefinition](/media/mwkang/Klevv/Spring 일지/스프링 기본/10.16/BeanDefinition.png)

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



![Autowired](/media/mwkang/Klevv/Spring 일지/스프링 기본/10.16/Autowired.png)

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



# Keyboard Shortcut

- #### soutm은 메소드명으로 sout한다.

- #### soutv는 변수명으로 sout한다.

- #### ctrl + alt + enter은 가르키는 곳 윗 부분에 새로운 줄을 생성한다

- #### ctrl + shift + enter은 가르키는 곳 아래 부분에 새로운 줄을 생성한다.

