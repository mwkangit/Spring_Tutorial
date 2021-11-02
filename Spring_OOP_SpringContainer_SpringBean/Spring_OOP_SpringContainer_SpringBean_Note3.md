# Bean Lifecycle Callback



```java
private String url;
public NetworkClient() {
	System.out.println("생성자 호출, url = " + url);
	connect();
	call("초기화 연결 메시지");
}
public void setUrl(String url) {
	this.url = url;
}
//서비스 시작시 호출
public void connect() {
	System.out.println("connect: " + url);
}
public void call(String message) {
	System.out.println("call: " + url + " message = " + message);
}
//서비스 종료시 호출
public void disconnect() {
	System.out.println("close: " + url);
}
```

```java
@Test
    public void lifeCycleTest(){
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClient client = ac.getBean(NetworkClient.class);
        ac.close();
    }

    @Configuration
    static class LifeCycleConfig{

        @Bean
        public NetworkClient networkClient(){
            NetworkClient networkClient = new NetworkClient();
            networkClient.setUrl("http://hello-spring.dev");
            return networkClient;
        }
    }
```

- #### 스프링 빈 생성과 삭제의 순간이 존재하여 각 순간에 필요한 로직을 정의할 수 있다.

- #### ConfigurableApplicationContext는 AnnotationConfigApplicationContext의 부모 인터페이스 개념이다.

- #### 현재 코드는 임의로 설정하여  url도 set되어있지 않아서 생성 시점을 알 수 있어도 삭제 시점은 알 수 없다.

- #### 스프링은 의존관계 주입이 완료되면 스프링 빈에게 콜백 메소드를 통해 초기화 시점을 알려주는 기능을 제공한다. 또한 동일하게 스프링 컨테이너가 종료되기 직전에 소멸 콜백 메소드도 제공한다.

- #### 스프링 빈의 라이프사이클은 스프링 컨테이너 생성 -> 스프링 빈 생성 -> 의존관계 주입 -> 초기화 콜백 -> 사용 -> 소멸전 콜백 -> 스프링 종료 로 이루어진다.

- #### 여기서 초기화 콜백은 빈이 생성되고 빈의 의존관계 주입이 완료된 직후에 호출된다. 소멸전 콜백은 빈이 소멸되기 직전에 호출된다.

- #### 객체를 생성하는 생성자 부분과 초기화하는 초기화 부분을 명확하게 나누는 것이 좋다. 초기화 부분은 생성된 값을 활용하여 외부 커넥션 등 무거운 동작을 수행하며 동작을 지연시켜서 사용자 요청 전까지 동작을 안하고 요청 시 바로 초기화하게 만들 수도 있기 때문이다.



```java
public class NetworkClient implements InitializingBean, DisposableBean {
    @Override
    public void afterPropertiesSet() throws Exception
    {
		connect();
		call("초기화 연결 메시지");
    }
	@Override
	public void destroy() throws Exception {
		disConnect();
    }
}
```

- #### InitializingBean 인터페이스는 afterPropertiesSet()메소드로 초기화를 지원한다.

- #### DisposableBean 인터페이스는 destroy()메소드로 소멸을 지원한다.

- #### 이 인터페이스는 스프링 전용 인터페이스이다.

- #### 이름을 변경할 수 없으며 내가 코드를 고칠 수 없는 외부 라이브러리에 적용할 수 없다.



```java
public void init() {
	System.out.println("NetworkClient.init");
	connect();
	call("초기화 연결 메시지");
}
public void close() {
	System.out.println("NetworkClient.close");
	disConnect();
}
```

```java
@Configuration
static class LifeCycleConfig {
	@Bean(initMethod = "init", destroyMethod = "close")
	public NetworkClient networkClient() {
		NetworkClient networkClient = new NetworkClient();
		networkClient.setUrl("http://hello-spring.dev");
		return networkClient;
	}
}
```

- #### @Bean(initMethod = , destroyMethod=) 를 통해 직접 일반 메소드를 초기화 메소드와 소멸 메소드로 지정할 수 있다. 메소드명을 통해 호출 가능하다.

- #### 이 방법은 메소드 이름을 자유롭게 부여할 수 있다.

- #### 스프링 코드에 의존하지 않는다.

- #### 설정 정보를 사용하므로 코드를 고칠 수 없는 외부 라이브러리에도 초기화, 소멸 메소드를 적용할 수 있다.

- #### @Bean의 destroyMethod 속성은 default 값이 (inferred)인데 이것은 close, shutdown이라는 이름의 메서드를 자동으로 호출한다는 뜻이다. 추론 기능을 사용하지 않으려면 destroyMethod =""를 하면 된다.



```java
    // 이걸로 생성 콜백 구현
    @PostConstruct
    public void init() {
        System.out.println("NetworkClient.afterPropertiesSet");
        connect();
        call("초기화 연결 메시지");
    }

    // 이걸로 소멸 콜백 구현
    @PreDestroy
    public void close() {
        System.out.println("NetworkClient.destroy");
        disconnect();
    }
```

```java
        @Bean
        public NetworkClient networkClient(){
            NetworkClient networkClient = new NetworkClient();
            networkClient.setUrl("http://hello-spring.dev");
            return networkClient;
        }
```

- #### JSR-250 자바 표준을 따르는 방법으로 스프링에 종속적이지 않다.

- #### 외부 라이브러리에는 적용하지 못한다. 만약 외부 라이브러리를 초기화, 소멸 해야하면 @Bean기능을 이용해야 한다.



# Bean Scope



- #### 싱글톤은 기본 스코프이며 스프링 컨테이너의 시작과 종료까지 유지되는 가장 넓은 범위의 스코프이다.

- #### 프로토타입은 스프링 컨테이너가 프로토타입 빈 생성, 의존관계 주입, 초기화 까지만 관리하고 더이상 관리하지 않는 짧은 범위의 스코프이다. 초기화 과정이 끝나면 이후에는 client에서 객체를 이용하게 된다.

- #### 웹 스코프에는 3가지가 있다.

  - #### request : 웹 request가 오고 response가 나갈때 까지 유지되는 스코프이다.

  - #### session : 웹 세션이 생성되고 종료될 때 까지 유지되는 스코프이다.

  - #### application : 웹의 서블릿 컨텍스트와 같은 범위로 유지되는 긴 범위의 스코프이다.



![prototypebean1](/media/mwkang/Klevv/Spring 일지/스프링 기본/10.31/prototypebean1.png)

- #### 스프링 컨테이너는 요청한 빈에 대해 의존관계 주입, 초기화 단계까지 관리한다.



![prototypebean2](/media/mwkang/Klevv/Spring 일지/스프링 기본/10.31/prototypebean2.png)

- #### 과정을 마친 빈을 client에 반환하지만 요청이 오면 항상 새로운 프로토타입 빈을 생성해서 반환한다.

- #### 초기화 과정까지만 스프링 컨테이너가 관리하므로 @PreDestroy와 같은 소멸 메소드는 호출되지 않는다.



```java
    @Scope("prototype")
    static class PrototypeBean{
        @PostConstruct
        public void init(){
            System.out.println("PrototypeBean.init");
        }

        @PreDestroy
        public void destroy(){
            System.out.println("PrototypeBean.destroy");
        }
    }
```

- #### Scope 범위를 프로토타입으로 설정하여 프로토타입 빈을 구현할 수 있다.

- #### 프로토타입 스코프의 빈은 스프링 컨테이너에서 빈을 조회할 때 생성되고, 초기화 메소드도 실행된다.

- #### 생성된 프로토타입 스코프는 스프링 컨테이너가 관리하지 않기 때문에 소멸 메소드를 호출하지 않는다. 그래서 client가 빈을 관리하고 소멸 메소드도 직접 해야한다.



```java
static class ClientBean {
private final PrototypeBean prototypeBean;
	@Autowired
	public ClientBean(PrototypeBean prototypeBean) {
		this.prototypeBean = prototypeBean;
	}
	public int logic() {
		prototypeBean.addCount();
		int count = prototypeBean.getCount();
		return count;
	}
}

@Scope("prototype")
static class PrototypeBean {
	private int count = 0;
	
    public void addCount() {
        count++;
	}
    
	public int getCount() {
		return count;
	}
	
    @PostConstruct
	public void init() {
		System.out.println("PrototypeBean.init " + this);
	}
	@PreDestroy
	public void destroy() {
		System.out.println("PrototypeBean.destroy");
	}
}
```

- #### 일반적으로 프로토타입 빈 2개 생성후 각각의 client 변수를 증가하면 따로 증가한다.

- #### 위 코드의 경우 싱글톤 빈 이 프로토타입 빈을 의존관계로 주입받고 있으므로 생성된 프로토타입 빈은 소멸되지 않고 싱글톤 빈 내부에 계속 남게 된다. 결국 의존관계를 주입받은 싱글톤 빈에서 계속 프로토타입 빈 인스턴스를 이용하게 되는 것이다.

- #### 프로토타입 빈은 사용할 때마다 생성되어야 효율이 좋기 때문에 이러한 방법은 보완해야한다.



```java
static class ClientBean {
	@Autowired
	private ApplicationContext ac;
	public int logic() {
		PrototypeBean prototypeBean = ac.getBean(PrototypeBean.class);
		prototypeBean.addCount();
		int count = prototypeBean.getCount();
		return count;
	}
}
```

- #### 싱글톤 빈이 프로토타입 빈을 사용할 때마다 스프링 컨테이너에 요청하는 방법이다.

- #### 의존관계를 외부에서 주입(DI)받는게 아니라 직접 필요한 의존관계를 찾는 것을  Dependency Lookup(DL) 의존관계 조회(탐색)이라고 한다.

- #### 위 코도와 같이 ApplicationContext 전체를 주입받게 되면 스프링 컨테이너에 종속적인 코드가 되고 단위 테스트도 어려워진다.



```java
	@Autowired
	private ObjectProvider<PrototypeBean> prototypeBeanProvider;
	public int logic() {
		PrototypeBean prototypeBean = prototypeBeanProvider.getObject();
		prototypeBean.addCount();
		int count = prototypeBean.getCount();
		return count;
	}
```

- #### ObjectProvider의 getObject()는 스프링 컨테이너를 통해 해당 빈을 찾아서 반환하는 DL을 실행한다.

- #### ObjectProvider은 스프링이 자동으로 DI 주입으로 사용이 가능하다.

- #### GetObject() 호출시 그때서야 스프링 컨테이너에서 프로토타입 빈을 찾아서 반환한다.

- #### ObjectProvider은 프로토타입을 위해 있는 것이 아닌 DL을 더 편리하게 도와주기 위한 용도로 사용하며 스프링에 의존적이다.



```java
	@Autowired
	private Provider<PrototypeBean> provider;
	public int logic() {
			PrototypeBean prototypeBean = provider.get();
			prototypeBean.addCount();
			int count = prototypeBean.getCount();
			return count;
	}
```

```groovy
implementation 'javax.inject:javax.inject:1'
```

- #### Provider은 JSR-330 자바 표준을 사용하며 gradle에 javax.inject:javax.inject:1 라이브러리를 추가해야 한다.

- #### Provider은 간단하게 get() 함수를 통해 DL을 실행한다. 

- #### 자바 표준을 사용하기 때문에 스프링에 의존적이지 않다.

- #### 두 빈이 서로 의존관계를 형성할 경우 먼저 사용하는 빈에 대해 provider이 알아서 의존관계를 형성하도록 할 수 있다.

- #### 프로토타입 만들 시 주로 ObjectProvider을 이용하게 된다. 대부분 스프링에서 제공하는 기능을 이용하고 특별한 경우 자바 표준을 이용하는 것이 좋다.



![web_scope](/media/mwkang/Klevv/Spring 일지/스프링 기본/10.31/web_scope.png)

- #### 웹 스코프는 웹환경에서만 동작하며 스프링이 해당 스코프의 종료시점까지 관리하여 소멸 메소드가 호출된다.

- #### 웹 스코프는 4가지로 나눌 수 있다.

  - #### request : HTTP 요청이 들어오고 나갈 때까지 유지되는 스코프이며 각각의 HTTP 요청마다 별도의 빈 인스턴스가 생성되고 관리된다.

  - #### session : HTTP Session과 동일한 생명주기를 가지는 스코프이다.

  - #### application :  서블릿 컨텍스트(ServletContext)와 동일한 생명주기를 가지는 스코프이다.

  - #### websocket : 웹 소켓과 동일한 생명주기를 가지는 스코프이다.

- #### 동시 2명이상의 사용자가 HTTP 요청을 해도 각각 다른 스프링 빈이 생성되며 서비스 요청을 해도 각각의 빈에 요청이 들어가서 각각의 서비스가 실행된다.

- #### 즉, HTTP request에 맞춰서 모든 것이 각각 할당된다.



```groovy
implementation 'org.springframework.boot:spring-boot-starter-web'
```

- #### Web환경이 동작하기 위해 web 라이브러리를 gradle에 추가해야한다.

- #### 스프링 부트는 웹 라이브러리가 없으면 AnnotationConfigApplicationContext를 기반으로 어플리케이션을 구동한다. 웹 라이브러리가 추가되면 웹과 관련된 추가 설정과 환경들이 필요하므로 AnnotationConfigServletWebServerApplicationContext를 기반으로 어플리케이션을 구동한다.

```groovy
server.port=9090
```

- #### Application.properties에서 포트를 변경할 수 있다.



```java
@Component
@Scope(value = "request")
public class MyLogger {
	private String uuid;
	private String requestURL;
	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}
	public void log(String message) {
		System.out.println("[" + uuid + "]" + "[" + requestURL + "] " + message);
	}
@PostConstructpublic void init() {
	uuid = UUID.randomUUID().toString();
	System.out.println("[" + uuid + "] request scope bean create:" + this);
	}
@PreDestroy
	public void close() {
	System.out.println("[" + uuid + "] request scope bean close:" + this);
	}
}
```

```java
@Controller
@RequiredArgsConstructor
public class LogDemoController {
    private final LogDemoService logDemoService;
	private final MyLogger myLogger;
	@RequestMapping("log-demo")
	@ResponseBody
	public String logDemo(HttpServletRequest request){
	String requestURL = request.getRequestURL().toString();
	myLogger.setRequestURL(requestURL);
	myLogger.log("controller test");
	logDemoService.logic("testId");
	return "OK";
	}
}
```

```java
@Service
@RequiredArgsConstructor
public class LogDemoService {
	private final MyLogger myLogger;
	public void logic(String id) {
		myLogger.log("service id = " + id);
	}
}
```

- #### UUID는 유일한 고유의 ID라고 할 수 있다. 즉, UUID로 HTTP 요청을 구분할 수 있따.

- #### @Scope(value = "request")로 설정하여 빈이 HTTP 요청 당 하나씩 생성되며 HTTP 요청이 끝나는 시점에 소멸된다. 정확한 빈 생성 시점은 스프링 컨테이너 요청 시점이다.

- #### @ResponseBody는 뷰 화면 없이 문자를 바로 반환할 때 사용한다. 즉, 메소드의 문자열을 그대로 응답으로 보낼 수 있다.

- #### HttpServletRequest는 자바에서 제공하는 표준 servlet 규약으로 고객 요청 정보를 받을 수 있다.

- #### getRequestURL()를 통해 고객이 요청한 URL을 확인할 수 있다.

- #### RequestURL을 저장하는 부분은 컨트롤러보다 공통 처리가 가능한 스프링 인터셉터나 서블릿 필터 같은 곳을 활용하는 것이 좋다.

- #### 인터셉터란 HTTP request가 컨트롤러 호출을 하기 직전에 공통화하여 처리를 하는 것이다.

- #### Request scope을 사용하지 않고 파라미터로 모든 정보를 서비스 계층에 넘기면 파라미터가 많아서 지저분해진다. RequestURL과 같은 웹 관련 정보가 웹과 관련없는 서비스 계층에 넘어가게 된다. 웹 관련 부분은 컨트롤러까지만 사용해야 한다. 서비스 계층은 웹 기술에 종속되지 않고 가급적 순수하게 유지하는 것이 좋다.

- #### 현재 위의 코드에 의존관계 주입 시 request가 오지 않았기 때문에 request 빈이 생성되지 않았다. 따라서 위 코드는 오류를 발생하여 실행되지 않는다.



```java
@Controller
@RequiredArgsConstructor
public class LogDemoController {
	private final LogDemoService logDemoService;
	private final ObjectProvider<MyLogger> myLoggerProvider;
	@RequestMapping("log-demo")
	@ResponseBody
	public String logDemo(HttpServletRequest request) {
		String requestURL = request.getRequestURL().toString();
		MyLogger myLogger = myLoggerProvider.getObject();
		myLogger.setRequestURL(requestURL);myLogger.log("controller test");
		logDemoService.logic("testId");
		return "OK";
	}
}
```



```java
@Service
@RequiredArgsConstructor
public class LogDemoService {
	private final ObjectProvider<MyLogger> myLoggerProvider;
	public void logic(String id) {
		MyLogger myLogger = myLoggerProvider.getObject();
		myLogger.log("service id = " + id);
	}
}
```

- #### MyLogger을 찾을 수 있는 DL인 ObjectProvider을 주입하는 방법으로 해결 가능하다.

- #### 이전에는 의존관계를 바로 형성하려고 했지만 지금 코드는 의존관계를 사용자의 request가 온 시점에 형성하여 새로운 request 빈을 만든다.

- #### ObjectProvider을 사용하여 getObject() 호출 시점까지 request scope 빈 생성을 지연할 수 있다. 즉, 스프링 컨테이너에 요청하는 시점을 지연하는 것이다.

- #### getObject()를 컨트롤러와 서비스 계층에서 각각 한번씩 따로 호출해도 같은 HTTP 요청이면 같은 스프링 빈이 반환된다.



```java
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MyLogger {
}
```

- #### Proxy를 사용하여 이전의 오류가 발생하던 코드를 정상적으로 실행되게 만들 수 있다.

- #### ProxyMode에서 적용대상이 클래스이면 TARGET_CLASS를 선택하고 인터페이스이면 INTERFACES를 선택한다.

- #### 프록시는 가짜 클래스를 만들어서 사용하는 방법이다.

- #### CGLIB 라이브러리는 바이트코드 조작을 통해 지정된 클래스에 대한 가짜 클래스를 생성하여 의존관계 주입에 사용한다. 그 후 프록시 객체의 기능을 수행하고자할 때 진짜 클래스를 호출하여 의존관계를 형성 후 사용하게 된다.



![proxy](/media/mwkang/Klevv/Spring 일지/스프링 기본/10.31/proxy.png)

- #### 프록시 객체는 요청이 오면 그때 내부에서 진짜 빈을 요청하는 위임 로직이 들어있다.

- #### 사용자가 프록시에게 로직을 호출하면 프록시는 진짜 객체의 로직을 호출하여 사용자 입장에서 사실 원본인지 아닌지 모르게 동일하게 사용이 가능하여 다형성을 형성할 수 있다.

- #### 프록시 객체로 client는 싱글톤 빈을 사용하듯 편리하게 request scope을 이용할 수 있다.

- #### 객체 조회를 필요한 시점까지 지연처리 해야할 때 provider이나 프록시를 이용한다.

- #### 다형성과 DI컨테이너의 장점은 단지 어노테이션 설정 변경만으로 원본 객체를 프록시 객체로 대체할 수 있다는 것이다.

- #### 각 메소드들의 실행 시간을 알아볼때 프록시와 AOP를 사용하면 매우 편리하다



Image 출처 : [김영한 스프링 입문](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EC%9E%85%EB%AC%B8-%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8)