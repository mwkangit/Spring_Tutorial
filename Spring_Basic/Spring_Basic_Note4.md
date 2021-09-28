# H2 Database

### 개발이나 테스트 용도로 가볍고 편리한 database, 웹 화면을 제공해주는 database이다.



- #### 버전을 스프링 부트 버전에 맞춘 후 chmod 755 h2.sh로 권한을 부여한다.

- #### jdbc:h2:~/test로  home 경로에 test라는 database를 생성후 jdbc:h2:tcp://localhost/~/test 로 접근한다.



```sql
drop table if exists member CASCADE;
create table member
(
id
bigint generated by default as identity,
name varchar(255),
primary key (id)
);
```

- #### Query를 통해 member를 저장할 테이블을 생성한다.



```java
// build.gradle
implementation 'org.springframework.boot:spring-boot-starter-jdbc'
runtimeOnly 'com.h2database:h2'

// application.properties
spring.datasource.url=jdbc:h2:tcp://localhost/~/test
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
```

- #### build.gradle에 jdbc, h2 관련 라이브러리를 추가한다.

- #### application.properties에 h2 database와 연결을 추가한다.

# JDBC Repository 구현

```java
	private final DataSource dataSource;
    public JdbcMemberRepository(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
    @Override
    public Member save(Member member) {
        String sql = "insert into member(name) values(?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, member.getName());
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                member.setId(rs.getLong(1));
            } else {
                throw new SQLException("id 조회 실패");
            }
            return member;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }
    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select * from member where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();if(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void close(Connection conn) throws SQLException {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }
```

- #### Application.properties에서 자동으로 datasource를 bean에 넣어줘서 DI로 datasource를 사용하면 된다.

- #### Database와 DataSourceUtils.getConnection()를 통해 연결하고 PreparedStatement를 통해 query를 전송하고 ResultSet을 통해 결과값을 받는다.

- #### PreparedStatement 인자로 Statement.RETURN_GENERATED_KEYS를 넣어서 insert시 key 값을 반환 받을 수 있게 한다.

- #### ExecuteUpdate()를 통해 database에 값을 저자하고 getGeneratedKeys()를 통해 값을 key값에 따라 반환받는다.

- #### ExecuteQuery()를 통해 query를 실행하여 key값으로 값을 조회할 수 있다.

- #### Database와 연결하여 사용 후 반드시 DataSourceUtils.releaseConnection()를 통해 연결을 해제해줘야 중첩 연결을 방지할 수 있다.

  

```java
     @Bean
     public MemberRepository memberRepository(){
       //  return new MemoryMemberRepository();
         return new JdbcMemberRepository(dataSource);
     }
```

- #### Config 소스파일은 Assembly라고 부르며 나머지 코드는 건들지 않고 추가하여 객체지향형의 다형성을 활용할 수 있다.

- #### DI를 사용하여 개방-폐쇄 원칙(OCP)을 잘 준수하고 기존 코드를 전혀 손대지 않고, 설정만으로 구현 클래스를 변경할 수 있다.



# Spring 통합 테스트

### Spring Container와 Database까지 연결한 통합 테스트를 진행한다.



```java
@SpringBootTest
@Transactional
class MemberServiceIntegrationTest {
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
//Given
        Member member = new Member();
        member.setName("hello");
//When
        Long saveId = memberService.join(member);//Then
        Member findMember = memberRepository.findById(saveId).get();
        assertEquals(member.getName(), findMember.getName());
    }
    @Test
    public void 중복_회원_예외() throws Exception {
//Given
        Member member1 = new Member();
        member1.setName("spring");
        Member member2 = new Member();
        member2.setName("spring");
//When
        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> memberService.join(member2));//예외가 발생해야 한다.
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }
}
```

- #### @SpringBootTest를 통해 spring container와 테스트를 함께 실행한다.

- #### @Transactional은 test 실행 시 database로 데이터 insert query를 실행하고 test가 끝나면 rollback하여 database에 저장하였던 데이터가 모두 지워진다.  즉, @Transactional로 @AfterEach는 필요없으며 안전하게 다음 테스트 메소드를 이용할 수 있다.

- #### 통합 테스트보다는 단위 테스트를 주기적으로 하는 것이 좋으며 test 전용 database를 따로 제작하기도 한다.



# Spring JDBCTemplate

### JDBC API의 반복 코드를 대부분 제거하지만 SQL은 직접 작성해야한다.



```java
	private final JdbcTemplate jdbcTemplate;
    public JdbcTemplateMemberRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Override
    public Member save(Member member) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("member").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", member.getName());
        Number key = jdbcInsert.executeAndReturnKey(new
                MapSqlParameterSource(parameters));
        member.setId(key.longValue());
        return member;}
    @Override
    public Optional<Member> findById(Long id) {
        List<Member> result = jdbcTemplate.query("select * from member where id = ?", memberRowMapper(), id);
        return result.stream().findAny();
    }
    @Override
    public List<Member> findAll() {
        return jdbcTemplate.query("select * from member", memberRowMapper());
    }
    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = jdbcTemplate.query("select * from member where name = ?", memberRowMapper(), name);
        return result.stream().findAny();
    }
    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setId(rs.getLong("id"));
            member.setName(rs.getString("name"));
            return member;
        };
    }
```

- #### WithTableName()을 통해 사입할 table명을 명시하고 usingGneratedKeyColumns()를 통해 key열의 이름을 명시해준다.

- #### Parameters hashmap에 database에 저장할 요소를 저장한다.

- #### ExecuteAndReturnKey( new MapSqlParameterSource() )를 통해 데이터를 저장 후 key값을 가져온다.



# JPA

### JPA는 기존의 반복 코드를 줄이고 SQL도 JPA가 직접 만들어서 실행해준다.  또한, SQL과 데이터 중심의 설계에서 객체 중심의 설계로 전환할 수 있다.



```java
// build.gradle
// implementation 'org.springframework.boot:spring-boot-starter-jdbc'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
```

- #### JPA 라이브러리는 jdbc를 포함한다.



```java
// application.properties
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=none
```

- #### Show-sql은 JPA가 생성하는 sql을 출력한다.

- #### Ddl-auto = none이면 테이블을 자동으로 생성하는 기능을 사용하지 않는 것이고 create이면 entity 정보를 바탕으로 테이블을 직접 생성해준다.



```java

@Entity
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // 고객이 아닌 시스템이 정하는 값이다
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

```

- #### @Entity를 이용하여 jpa가 관리하는 entity임을 나타내준다.

- #### GenerationType.IDENTITY를 통해 database가 알아서 ID값을 지정해주게 한다.



```java
public class JpaMemberRepository implements MemberRepository {
    private final EntityManager em;
    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }
    public Member save(Member member) {
        em.persist(member);
        return member;
    }
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
    public Optional<Member> findByName(String name) {
            List<Member> result = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
        return result.stream().findAny();
    }
}
```

- #### Application.properties를 통해 자동으로 EntityManager이 bean에 등록되며 jpa는 EntityManager로 database를 관리한다.

- #### Persist()는 데이터를 database에 영구 저장한다는 뜻이다.

- #### Find()는 데이터를 조회할 때 사용하며 인자로는 조회할 타입, 식별자를 넣는다.

- #### CreateQuery는 query에 객체를 삽입하여 객체를 대상으로 query를 만들지만 sql로 자동 전환되어 database로 전송된다.  이러한 기능을 JPQL이라고 한다.



```java
@Transactional
public class MemberService {}
```

- #### JPA는 해당 클래스의 메소드를 실행할 때 transaction을 시작하고 메소드가 정상 종료되면 transaction을 commit한다.  만약 runtime error이 발생하면 rollback하므로 JPA를 통한 모든 데이터 변경은 transaction안에서 실행해야 한다.

- #### @Commit은 소스파일 실행 혹은 테스트 시 database에 변경 내용을 남겨 놓는다는 어노테이션이다.



# Spring Data JPA

### JPA를 Spring으로 감쌓서 제공하는 기술로 JPA를 편리하게 사용하게 해주는 기술이다.



```java
public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository {
    @Override
    Optional<Member> findByName(String name);
}
```

- #### Spring Data JPA는 인터페이스 상에서 모두 해결할 수 있다.

- #### JpaRepository의 인자로는 사용할 객체의 타입, key의 타입을 넣는다.

- #### Spring Data JPA가 JpaRepository를 받고 있으며 구현체를 자동으로 만들어주고 Spring Bean에 자동으로 등록한다.

- #### JpaRepository에 이미 find ~ save 메소드가 만들어져 있어서 override되어 사용되지만 findByName은 특정 key값으로 조회하는 기능이 아니며 대상 범위가 프로젝트마다 다를 수 있으므로 직접 메소드를 선언해야한다.

- #### FindByName() 메소드는 메소드명에 find와 뒤의 Name을 이용하여 Spring Data JPA가 자동으로 select m from Member m where m.name = ? 이라는 JPQL을 만들어준다.



![Spring Data JPA](/media/mwkang/Klevv/Spring 일지/스프링 입문/09.05/Spring Data JPA.png)

- #### 80% 정도의 단순화 query는 Spring Data JPA의 인터페이스로 구현하는 것이 가능하다.

- #### 복잡한 동적 쿼리는 Querydsl 라이브러리를 사용하여 query를 자바 코드로 안전하게 작성할 수 있고 동적 query도 편리하게 작성할 수 있다.



# AOP

### AOP(Aspect Oriented Programming)은 공통 관심 사항(cross-cutting concern)과 핵심 관심 사항(core concern)을 분리하여 원하는 곳에 공통 관심 사항을 적용할 수 있게 한다.



```java
@Component
@Aspect
public class TimeTraceAop {
    @Around("execution(* hello.hellospring..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        System.out.println("START: " + joinPoint.toString());
        try {
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("END: " + joinPoint.toString()+ " " + timeMs +
                    "ms");
        }
    }
}
```

- #### @Aspect는 AOP를 적용하겠다는 어노테이션이다.

- #### @Component를 적용해도 되지만 config에 @Bean으로 bean에 등록하기도 한다.

- #### @Around로 공통 관심사를 적용할 메소드를 지정한다.  패키지명, 클래스면, 파라미터 타입을 순서대로 작성하며 코드의 의미는 패키지 하위 모든 메소드에 적용한다는 것이다.  * hello.hellospring.service..*(..)이면 service패키지 내부 메소드에만 공통 관심사가 적용된다.

- #### JointPoint.proceed()를 통해 메소드를 실행하고 종료되면  finally{}의 코드가 실행되어 각 메소드의 실행 시간이 console에 출력된다.   Proceed() 메소드는 필요시 다른 메소드로 변경 가능하다.



![AOP Proxy](/media/mwkang/Klevv/Spring 일지/스프링 입문/09.05/AOP Proxy.png)

- #### DI를 이용하여 container은 실제 메소드를 실행하기 전에 가짜 메소드인 proxy를 만들어서 먼저 실행한다.  실제 메소드는 proxy가 종료 후 실행된다.  즉, proxy를 통해 미리 공통 관심사를 실행한 후 실제 메소드 한번 더 실행하는 것이다.

- #### GetClass() 메소드는 CGLIB를 이용한다.  CGLIB는 CG library로 멤버 서비스를 복제하여 코드를 조작하는 기술이다.





Image 출처 : [김영한 스프링 입문](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EC%9E%85%EB%AC%B8-%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8)