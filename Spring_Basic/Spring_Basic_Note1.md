1. # Spring Initializr

   ### Spring boot 기반으로 프로젝트를 만들어주는 사이트이다.

   

   - ### Project
     
     - #### 필요한 라이브러리를 가져오고 build 하는 라이프사이클까지 관리하는 tool이다.
       
       - #### Gradle, Maven이 있으며 주로 Gradle을 사용한다.

   

   - ### Spring boot
     
     - #### spring boot 버전을 선택하는 항목이다.
     - #### SNAPSHOT 은 아직 만들고 있는 버전이다.
     - #### M1 은 아직 정식 배포된 것이 아닌 버전이다.

   

   - ### Project Metadata
     
     - #### Group 에는 보통 기업명, 기업 domain 명을 적는다.
     - #### Artifact 는 빌드되어 나올 때의 결과물 명이다.

   

   - ### Dependencies
     
     - #### spring boot 에 어떤 라이브러리를 사용할 것인지 결정한다.
     - #### Spring Web은 web을 사용하게 해주는 엔진이다.
     - #### Thymeleaf 은 HTML 을 만들어주는 템플릿 엔진이다.

   

   # Project

   ## resource

   - #### XML, HTML, properties, 설정파일을 관리하는 영역이다.

   ## gitignore

   - #### git과 연관되어 소스파일만 올릴 수 있게 설정하는 영역이다.

   ## Library

   ### Gradle은 의존관계가 있는 라이브러리를 함께 다운로드 한다.

   - ### Spring Boot Library

     - #### spring-boot-starter-web

       - #### spring-boot-starter-tomcat : 톰캣 (웹서버)

       - #### spring-webmvc : 스프링 웹 MVC

     - #### spring-boot-starter-thymeleaf : 타임리프 템플릿 엔진 (View)

     - #### spring-boot-starter (공통) : 스프링 부트 + 스프링 코어 + 로깅

       - #### spring-boot

         - #### spring-core

       - #### spring-boot-starter-logging

         - #### logback (Log 용도), slf4j (Interface 용도)

   - ### Test Library

     - #### spring-boot-starter-test

       - #### junit : 테스트 프레임워크 (현재 5 version)

       - #### mockito : 목 라이브러리

       - #### assertj : 테스트 코드를 좀 더 편하게 작성하게 도와주는 라이브러리

       - #### spring-test : 스프링 통합 테스트 지원

   

   # Practice

   ```html
   <!-- resource/static/index.html -->
   <!DOCTYPE HTML>
   <html>
   <head>
       <title>Hello</title>
       <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> </head>
   <body>
   Hello
   <a href="/hello">hello</a>
   </body>
   </html>
   ```

   - #### Welcome page 기능을 제공받아서 브라우저 화면을 출력할 수 있다.

   - #### HTML은 thymeleaf 템플릿 엔진을 이용하여 서버에서 브라우저로 알맞은 형식으로 전달한다.

   

   ```java
   package hello.hellospring.controller;
   
   import org.springframework.stereotype.Controller;
   import org.springframework.ui.Model;
   import org.springframework.web.bind.annotation.GetMapping;
   
   @Controller
   public class HelloController {
   
       @GetMapping("hello") // map 어플리케이션에서  /hello라고 들어오면 이 메소드를 호출한다
       public String hello(Model model){
           model.addAttribute("data", "hello!!");
           return "hello";
       }
   }
   
   ```

   - #### @Controller로 이 클래스가 controller 역할을 한다는 것을 선언한다.

   - #### @GetMapping은 get post의 형식으로 이 코드에서는 localhost:8080/hello url로 접속하면 controller 에서 get하기 때문에 hello() 메소드를 실행하게 된다.

   - #### Spring에서 Model을 선언하여 hello() 메소드에 매개변수로 전달한다.

   - #### model 변수에 data라는 key값으로 hello!!라는 value를 정의한 후 template 폴더의 hello라는 이름의 HTML을 전달하고 실행한다.

   ##### 

   ```html
   <!-- resources/templates/hello.html -->
   <!DOCTYPE HTML>
   <html xmlns:th="http://www.thymeleaf.org">
   <head>
       <title>Hello</title>
       <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> </head>
   <body>
   <p th:text="'안녕하세요. ' + ${data}" >안녕하세요. 손님</p>
   </body>
   </html>
   ```

   - #### ty는 thymeleaf를 의미한다.

   - #### ${data}는 HelloController 클래스에서 전달받은 model의 key값을 사용했으므로 value인 hello!!로 대체된다.


   ![WebBrowzer Tomcat SpringContainer](https://user-images.githubusercontent.com/79822924/134794634-3d1a1654-5dab-4133-a0d9-380266482a4c.png)


   - #### 웹 브라우저에서 localhost:8080/hello를 서버로 요청하면 서버 내에서 helloController 내부의 메소드가 실행되어 return 값으로 templates/+{ViewName}+.html 파일을 실행할 준비를 마친 후 브라우저에 변환된 HTML 파일을 전송한다.

   

   # Build

   ## Window

   1. #### $gradlew build --> build 생성한다.

   2. #### $gradlew clean --> build했던 폴더 삭제한다.

   3. #### $gradlew clean build --> build 되었던 기록을 삭제하고 다시 build 한다.

   4. #### java -jar (Project Name)-0.0.1-SNAPSHOT.jar --> spring으로 생성된 jar 실행

   ## Linux

   1. #### $./gradlew build --> build 생성한다.

   2. #### $./gradlew clean --> build했던 폴더 삭제한다.

   3. #### $./gradlew clean build --> build 되었던 기록을 삭제하고 다시 build 한다.

   4. #### java -jar (Project Name)-0.0.1-SNAPSHOT.jar --> spring으로 생성된 jar 실행


   #### Image 출처 : [김영한 스프링 입문](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EC%9E%85%EB%AC%B8-%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8)
