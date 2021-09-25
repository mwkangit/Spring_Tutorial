# Static Contents

### Welcome Page와 같이 파일을 web browser에 바로 전송하는 방법이다



![static contents image](/media/mwkang/Klevv/Spring 일지/스프링 입문/08.31/static contents image.png)

- #### Controller이 우선 순위를 가지므로 Controller에서 hello-static이 있는지 확인한다.

- #### Controller에 hello-static이 없다면 spring boot는 spring 내부에서 hello-static.html을 찾아본다.

- #### HTML을 찾았다면 그대로 web browser에 HTML 형식으로 전달한다.



# MVC, Template Engine



- #### View는 화면을 출력하는 영역에 집중하며 다른 작업은 하지 않는 것이 좋으며 Controller는 business 로직 관련이나 내부적인 상황을 처리하는 것이 집중하는 것이 좋으므로 View와 Controller는 분리하는 것이 좋다.



```java
@Controller
public class HelloController {

    @GetMapping("hello-mvc")
	public String helloMvc(@RequestParam("name") String name, Model model) {
        model.addAttribute("name", name);
        return "hello-template";
    }
}
```

- #### @RequestParam 뒤에는 required 매개변수를 사용할 수 있는데 True이면 client가 무조건 요구되는 인자를 입력해야 한다. (required는 True가 default값이다) (ex : localhost:8080/hello-mvc?name=Spring)



```html
<html xmlns:th="http://www.thymeleaf.org">
    <body>
        <p th:text="'hello ' + ${name}">hello!empty</p>
    </body>
</html>
```

- #### Template engine이 실행되어 값이 HTML로 전송되면 hello!empty가 아닌 'hello ' + ${name}이 브라우저로 전송된다.



![MVC, Template Engine image](/media/mwkang/Klevv/Spring 일지/스프링 입문/08.31/MVC, Template Engine image.png)

- #### ViewResovler는 View를 찾아주고 Template에 연결하는 역할을 한다.  즉, 찾은 View를 Thymeleaf가 처리하여 매개변수 값을 변환 후 HTML로 브라우저에 전송한다.



# API



```java
@Controller
public class HelloController {

    @GetMapping("hello-api")
    @ResponseBody
    public Hello helloApi(@RequestParam("name") String name) {
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }
    static class Hello {
        private String name;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
}
```

- #### API는 JSON을 기본으로 하고 있으며 JSON은 기본적으로 key, value 구조이다.

- #### HTML을 이용하지 않고 직접 HTTP body 영역에 데이터를 전송한다.

- #### 객체 자체를 전송하는 것이 가능하게 되어있다.



![Using ResponseBody image](/media/mwkang/Klevv/Spring 일지/스프링 입문/08.31/Using ResponseBody image.png)

- #### @ResponseBody로 HttpMessageConverter를 viewResolver대신 사용한다.

- #### 기본 문자처리는 StringHttpMessageConverter를 이용한다.

- #### 기본 객체처리는 MappingJackson2HttpMessageConverter를 이용한다

- #### 클라이언트의 HTTP Accept 헤더오 서버의 Controller 반환 타입 정보를 조합하여 HttpMessageConverter이 선택된다.



# Practice

### 회원 관리 예제

1. #### 데이터 : 회원 ID, 이름

2. #### 기능 : 회원 등록, 조회

3. #### 아직 데이터 저장소가 선정되지 않은 가사 시나리오

4. #### 중복된 이름은 회원 등록 불가



#### 자세한 내용은 git url 참조 : 



# New Knowledge

- #### Optional은 null로 반환 되는 값을 optional로 감싸서 표현하기 때문에 객체에 대한 반환값은 optional로 하는 것이 좋다.

- #### 실무에서는 동시성 문제가 발생할 수 있기 때문에 공유되는 변수는 HashMap보다는 CurrentHashMap을 사용한다..

- #### store.values().stream().filter(member -> member.getName().equals(name)).findAny(); 는 람다표현으로 stream()으로 loop을 돌면서 filter()을 통해 멤버중 name과 같은 이름을 가진 member을 반환하는데 findAny()로 순서 상관없이 먼저 발견한 member을 반환한다는 것이다.

- #### 자바는 테스트 케이스를 JUnit이라는 Framework로 테스트를 실행하여 간편하게 프로젝트를 테스트한다.

- #### @AfterEach는 테스트 메소드 하나 끝날때마다 호출되는 메소드이고  @BeforeEach는 테스트 메소드가 시작하기 전에 호출되는 메소드이고 @Test는 테스트를 진행하는 메소드이다.

- #### 테스트 케이스를 비교하는 라이브러리로 Assertions를 이용하며 assertThat(), assertEquals(), isEqualTo() 메소드를 자주 이용한다.

- #### findById().get()은 optional에서 값을 바로 꺼낼 때 이용하지만 권장하지는 않는다.

- #### 테스트 케이스는 given, when, then으로 나눠서 작성하는 것이 좋으며 given은 뭐가 주어졌는지, when은 주어진 것을 실행 했을때, then은 어떠한 결과가 나와야하는지를 의미한다.

- #### 테스트 및 소스코드에서 다 같은 객체를 공유할 때 Dependency Injection(DI)를 이용하여 같은 객체를 각 클래스에 넘겨주게 된다.

- #### TDD는 테스트 케이스를 먼저 만들고 개발을 시작하는 순서인 방법론이다.

- #### 테스트 케이스에서는 모든 테스트 메소드를 한번에 실행할 수 있지만 메소드 실행 순서는 보장되지 않는다.  이러한 상황에서 각 메소드가 데이터베이스를 갱신하면 서로 예상한 데이터 input이 다르게 되어 오류가 발생할 수 있다.  문제점을 해결하기 위해 @AfterEach로 각 테스트 메소드 종료 시 데이터를 reset하도록 한다.

- #### 테스트 코드는 실제 코드에 포함되지 않기 때문에 메소드명을 한글로 작성해도 된다.



# Keyboard Shortcut

- #### Shift + F6 --> 같은 이름을 가진 문자를 한번에 고칠 수 있다.

- #### ctrl + alt + v --> 코드에 대한 반환 결과 변수를 코드 앞에 자동으로 만들어준다.

- #### ctrl + shift + T --> 클래스 내부에서 누르면 테스트 케이스를 자동으로 만들어준다.

- #### ctrl + alt + m --> 코드 지정 후 누르면 지정된 코드를 따로 메소드로 생성한다.

- #### ctrl + B --> 지정된 객체의 원형 화면이 출력된다.

- #### alt + insert constructor --> 클래스가 생성자를 생성하여 DI를 수행할 수 있게 한다.

Image 출처 : [김영한 스프링 입문](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EC%9E%85%EB%AC%B8-%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8)