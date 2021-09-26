# 컴포넌트 스캔과 자동 의존관계 설정

### Controller가 Service를 통해 데이터에 접근 할 때 의존관계에 있다고 할 수 있다.  이러한 과정에서 Spring Container에서 Spring Bean이 관리된다고 표현한다.



```java
package hello.hellospring.controller;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MemberController {
    private final MemberService memberService;
    
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
}
```

- #### Spring Container은 spring 실행 시 생성되며 controller을 작동시키고 controller은 생성자를 실행시킨다.

- #### @Autowired를 통해 DI를 구현한다.  Spring bean에 등록된 MemberService를 자동으로 연결하여 가져온다.

- #### @Component 어노테이션이 존재해야 spring bean에 자동 등록된다.

- #### @Controller, @Service, @Repository는 @Component를 포함하여 spring bean에 자동 등록된다.

- #### 생성자에 @Autowired를 사용하면 객체 생성 시점에 spring container에서 해당 spring bean을 찾아서 주입한다.  생성자가 1개만 있으면 @Autowired는 생략 가능하다.

- #### Spring은 spring container에 spring bean을 등록할 때, 기본으로 싱글톤으로 등록한다.  싱글톤이란 모두 같은 인스턴스 하나만 사용하도록 객체 1개만 등록한다는 뜻이다.

- #### Spring 프로젝트 패키지 중 기본 패키지에 @Component가 있어야 Spring이 빌드 시 container에 bean으로 넣어준다.

- #### Controller를 통해 외부 요청을 받고 Service에서 비즈니스 로직을 만들고 Repository에서 데이터를 저장한다.



# 자바 코드로 직접 스프링 빈 등록하기



```java
package hello.hellospring;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import hello.hellospring.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    
    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }
}
```

- #### @Configuration, @Bean을 사용하여 직접 자바 코드로 bean에 넣어준다.

- #### Controller, Service, Repository는 어노테이션을 설정 후@Autowired로 자바 코드로 넣은 bean을 전달받을 수 있다. 

- #### DI에는 필드 주입, setter 주입, 생성자 주입이 있다.  세 방법 중 

  - #### 필드 주입 : 직접 객체 생성하는 부분에 어노테이션을 선언하는 것이다.

  - #### setter 주입 : setter을 통해 객체를 주입하는 것이다.

  - #### 생성자 주입 : 일반적인 방법으로 생성자를 통해 주입한다.

- #### 실무에서 일반적으로 컴포넌트 스캔을 이용하지만 구현 클래스를 변경해야 하는 상황이라면 설정을 통해 spring bean으로 등록한다. (ex : 아직 database를 만들지 않않은 상태로 프로그래밍 했지만 database가 완성되어 프로그램에 삽입해야하는 경우)



# 회원 등록



```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
    <body>
        <div class="container">
            <form action="/members/new" method="post">
                <div class="form-group">
                    <label for="name">이름</label>
                    <input type="text" id="name" name="name" placeholder="이름을
입력하세요">
                </div>
                <button type="submit">등록</button>
            </form>
        </div> <!-- /container -->
    </body>
</html>
```

- #### form 내부는 /member/new 주소에 post방식으로 넘어온다는 뜻이다.

- #### input은 값을 입력할 수 있는 HTML 태그이다.

- #### name = "name"이므로 @PostMapping에 선언된 name 변수에 입력 값이 넣어진다.



```java
@Controller
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping(value = "/members/new")
    public String createForm() {
        return "members/createMemberForm";
    }
    
        @PostMapping(value = "/members/new")
    public String create(MemberForm form) {
        Member member = new Member();
        member.setName(form.getName());
        memberService.join(member);
        return "redirect:/";
    }
}
```

```java
package hello.hellospring.controller;

public class MemberForm {
    private String name;

    public String getName() {
        return name;
    }    

    public void setName(String name) {
        this.name = name;
    }
}
```

- #### HTML로 전달된 입력 값은 create() 메소드의 매개변수인 MemberForm의 name에 저장된다.  이때, spring에서 알아서 setName() 메소드를 호출하여 변수에 값을 넣는다.

- #### "redirect:/"는 다시 첫 화면으로 돌아가라는 의미이다.

- #### 이로써 입력된 값은 Arraylist에 저장된다.

- #### 단, database를 이용하는 것이 아니기 때문에 서버를 종료하면 모든 데이터는 초기화된다.



# 회원 조회



```java
    @GetMapping(value = "/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
```

- #### Model에 List를 넣은 후 HTML로 전송한다.



```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<div class="container">
    <div>
        <table>
            <thead><tr>
                <th>#</th>
                <th>이름</th>
            </tr>
            </thead>
            <tbody>
            <tr th:each="member : ${members}">
                <td th:text="${member.id}"></td>
                <td th:text="${member.name}"></td>
            </tr>
            </tbody>
        </table>
    </div>
</div> <!-- /container -->
</body>
</html>
```

- #### each를 통해  전달받은 List내부의 값을 loop을 돌면서 확인한다.

- #### List 각 객체 내부의 값을 (ID, name) 출력한다.  이때 ID, name은 자바 소스에 private으로 설정되어 있지만 HTML이 알아서 getter, setter을 이용하여 변수에 접근한다.



# Keyboard Shortcut

- #### ctrl + p --> 메소드에 어떤 매개변수가 들어가야 하는지 알려준다.



Image 출처 : [김영한 스프링 입문](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EC%9E%85%EB%AC%B8-%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8)

