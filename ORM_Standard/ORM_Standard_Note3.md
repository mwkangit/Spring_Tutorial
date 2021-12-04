# JPQL



## Object Oriented Query



### About JPQL

```java
String jpql = "select m From Member m where m.name like ‘%hello%'";
List<Member> result = em.createQuery(jpql, Member.class)
    .getResultList();
```

- #### JPA는 엔티티 객체를 중심으로 개발하며 검색도 테이블이 아닌 엔티티 객체를 대상으로 한다.

- #### 하지만 모든 데이터베이스를 객체로 변환하여 검색하는 것은 불가능하므로 어플리케이션이 필요한 데이터만 데이터베이스에서 불러오려면 결국 SQL이 필요하다.

- #### JPQL은 SQL을 추상화한 객체 지향 쿼리 언어이다.

- #### JPQL은 SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN을 지원한다.

- #### 위 코드는 Member라는 객체를 m이라고 지정하고 Member의 name 속성이 hello인 Member 객체만 요청한다는 뜻이다.

- #### JPQL을 사용하여 데이터베이스 SQL에 의존하지 않을 수 있다.

- #### JPQL은 단순 문자열이어서 동적 쿼리를 만들기 어렵다.



### About Criteria



```java
//Criteria 사용 준비
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Member> query = cb.createQuery(Member.class);
//루트 클래스 (조회를 시작할 클래스)
Root<Member> m = query.from(Member.class);
//쿼리 생성 
CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), “kim”));
List<Member> resultList = em.createQuery(cq).getResultList();
```

- #### Criteria는 자바 표준에 등록되어 있다.

- #### Cb.createQuery(Member.class);는 Member과 관련된 쿼리를 한다는 것이다.

- #### Root\<Member> m = query.from(Member.class);는  쿼리를 코드로 짜고 Member 대상으로 FROM 절을 적용한 것이다.

- #### Cq는 실질적으로 쿼리를 생성하는 부분이다.

- #### Criteria는 문자가 아닌 자바코드로 JPQL을 작성할 수  있다.

- #### JPQL의 빌더역할을 하며 JPA의 공식 기능이다.

- #### Criteria는 너무 복잡하고 실용성이 없어서 QueryDSL을 사용하는 것을 권장한다.



### About QueryDSL



```java
//JPQL
//select m from Member m where m.age > 18
JPAFactoryQuery query = new JPAQueryFactory(em);
QMember m = QMember.member;
List<Member> list =
    query.selectFrom(m)
    .where(m.age.gt(18))
    .orderBy(m.name.desc())
    .fetch();
```

- #### QueryDSL은 오픈소스 라이브러리이다.

- #### 문자가 아닌 자바코드로 JPQL을 작성할 수 있으며 JPQL의 빌더 역할을 한다.

- #### 컴파일 시점에 문법 오류를 찾을 수 있으며 동적쿼리를 작성하기 편리하다.

- #### 단순하고 직관적이며 JPQL과 1 : 1 관계를 형성할 수 있어서 실무에 사용하기 좋다.



### About Native SQL



```java
String sql = “SELECT ID, AGE, TEAM_ID, NAME FROM MEMBER WHERE NAME = ‘kim’";
List<Member> resultList = em.createNativeQuery(sql, Member.class).getResultList();
```

- #### Native SQL은 JPA가 제공하는 SQL을 직접 사용할 수 있는 기능이다.

- #### 데이터베이스에 의존할 수 있다.

- #### 쿼리를 작성할 때 flush()에 주의한다.

- #### Flush()는 commit, query에 내포되어 있다.  하지만 JPA와 관련없는 데이터베이스 연결의 쿼리를 날리면 flush()가 일어나지 않기 때문에 오류가 발생한다. 즉, 강제로 쿼리 전에 flush() 해야하는 상황이 발생할 수 있는 것이다.



## JPQL Logic


### Query & TypedQuery


>select_문 :: =
>select_절
>from_절
>[where_절]
>[groupby_절]
>[having_절]
>[orderby_절]
>update_문 :: = update_절 [where_절]
>delete_문 :: = delete_절 [where_절]

>select
>COUNT(m), // 회원 수
>SUM(m.age), // 나이 합
>AVG(m.age), // 평균 나이
>MAX(m.age), // 최대 나이
>MIN(m.age) // 최소 나이
>from Member m

- #### 엔티티와 속성은 대소문자를 구분하고 키워드는 대소문자를 구분하지 않는다.

- #### As 키워드는 생략 가능하다.



```java
// TypedQuery
TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);

// Query
Query query3 = em.createQuery("select m.username, m.age from Member  m");

// setParameter
// 이름 기준
Member singleResult1 = em.createQuery("select m from Member m where m.username = :username", Member.class)
    .setParameter("username", "member1")
    .getSingleResult();

// 위치 기준
TypedQuery<Member> query6 = em.createQuery("select m from Member m where m.username = ?1", Member.class);
query6.setParameter(1, "member1");
Member singleResult2 = query6.getSingleResult();
```

- #### TypeQuery는 반환 타입이 명확할 때 사용한다.

- #### 명확한 타입 정보를 제시해야 한다.

- #### Query는 반환 타입이 명확하지 않을 때 사용한다.

- #### 위의 코드에서 String과 int 타입을 반환하므로 반환 타입을 지정하기 어려워서 Query로 반환한다.

- #### Query.getResultList()는 결과가 하나 이상일 때, 리스트를 반환한다.

- #### 결과가 없으면 빈 리스트를 반환하여 NullPointException 걱정 없다.

- #### Query.getSingleResult()는 결과가 정확히 하나일 때 반환한다.

- #### 결과가 없으면 javax.persistence.NoResultException이 발생한다.

- #### Spring data JPA로 null이므로 optional을 반환하게 된다. Try catch문을 작성하여 오류를 탐지해야 한다.

- #### 결과가 둘 이상이면 javax.persistence.NonUniqueResultException이 발생한다.

- #### Query.setParameter()는 파라미터를 매핑하는 역할을 한다. 보통 이름 기준으로 호출한다.

- #### 파라미터에 ?를 사용하여 위치 기준으로 매핑할 수 있다. 하지만 원하는 위치 중간에 다른 값이 들어오면 순서가 섞이므로 원하는 값이 반환되지 않을 수 있다. 위치 기준 파라미터는 사용하지 않는 것을 권장한다.



### Projection



- #### 프로젝션을 통해 대상, 엔티티, 임베디드 타입, 스칼라 타입을 반환할 수 있다.

  - #### SELECT m FROM Member m -> 엔티티 프로젝션

  - #### SELECT m.team FROM Member m -> 엔티티 프로젝션

  - #### SELECT m.address FROM Member m -> 임베디드 타입 프로젝션

  - #### SELECT m.username, m.age FROM Member m -> 스칼라 타입 프로젝션

- #### 중복은 DISTINCT 키워드로 제거할 수 있다.

- #### 조회 대상의 외래키로 다른 엔티티를 조회할 때 SQL 쿼리와 비슷하게 JPQL을 작성해야 한다.

  ```java
  "select t from Member m join m.team t"
  ```

  - #### 위 쿼리처럼 작성하면 SQL문이 어떻게 실행되는지 바로 파악 가능하다.

- #### 프로젝션으로 한번에 여러 값 타입을 조회할 때 3가지 방법을 이용할 수 있다.

  - #### Query 타입으로 조회

  ```java
  Query query3 = em.createQuery("select m.username, m.age from Member  m");
  ```

  - #### Object[] 타입으로 조회

  ```java
  List<Object[]> resultList2 = em.createQuery("select distinct m.username, m.age from Member m")
      .getResultList();
  Object[] result4 = resultList2.get(0);
  System.out.println("username = " + result4[0]);
  System.out.println("age = " + result4[1]);
  ```

  - #### New 명령어로 조회

  ```java
  // DTO를 생성하여 필요한 값만 조회한다.
  // JPQL에 작성할 때 패키지 명을 포함한 전체 클래스 명을 입력해야 한다.
  // DTO에 반드시 순서와 타입이 일치하는 생성자가 필요하다.
  
  List<MemberDTO> resultList3 = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
      .getResultList();
  MemberDTO memberDTO = resultList3.get(0);
  System.out.println("username = " + memberDTO.getUsername());
  System.out.println("age = " + memberDTO.getAge());
  ```



### Paging API



```java
List<Member> resultList4 = em.createQuery("select m from Member m order by m.age desc", Member.class)
    .setFirstResult(1)
    .setMaxResults(10)
    .getResultList();
```

- #### 페이징 기법은 어디서부터 어디까지 조회할 것인지를 말한다.

- #### SetFirstResult(int startPosition)은 조회를 시작하는 위치를 말하며 0부터 시작한다. 1 이상일 경우 offset이 붙는다.

- #### SetMaxResult(int maxResult)는 조회할 데이터의 수를 말한다.

- #### 페이징은 SQL 방언마다 다르게 전송된다.

- #### 위 코드를 참고하면 페이징 시 Member m 뒤에 order by m.age desc로 역순으로 정렬 후 조회한다.



### JOIN



```java
// 내부 조인
"SELECT m FROM Member m [INNER] JOIN m.team t"
String query = "select m from Member m inner join m.team t";
List<Member> result = em.createQuery(query, Member.class)
    .getResultList();
System.out.println("result = " + result);
    
// 외부 조인
"SELECT m FROM Member m LEFT [OUTER] JOIN m.team t"
String query2 = "select m from Member m left outer join m.team t";
List<Member> result2 = em.createQuery(query2, Member.class)
    .getResultList();
System.out.println("result2 = " + result2);    

// 세타 조인
"select count(m) from Member m, Team t where m.username = t.name"
String query3 = "select m from Member m, Team t where m.username = t.name";
List<Member> result3 = em.createQuery(query3, Member.class)
    .getResultList();
System.out.println("result3 = " + result3);
System.out.println("result3.size() = " + result3.size());
```

- #### JPQL은 3가지 조인을 지원한다.

  - #### 내부 조인 :  Member과 연관있는 Team만 t로 조인한다. Member에 Team이 없으면 데이터 오지 않는다.

  - #### 외부 조인 : Member이 있고 Team이 없어도 Team은  null로 Member만 조회한다.

  - #### 세타 조인 : 연관 없어도 조인하는 기법이다. 선택된 두 엔티티를 곱해서 쭉 세운 후 where절로 뽑는다.

- #### ON 절은 JPA2.1부터 지원하며 조인 대상을 필터링하고 연관관계 없는 외부 조인을 가능하게 한다.

  - #### 회원과 팀을 조인하면서 팀 이름이 A인 팀만 조인한다.

    ```java
    String query4 = "select m from Member m left join m.team t on t.name = 'teamA'";
    List<Member> result4 = em.createQuery(query4, Member.class)
        .getResultList();
    ```

  - #### 회원의 이름과 팀의 이름이 같은 대상 외부 조인

    ```java
    String query5 = "select m from Member m join Team t on m.username = t.name";
    List<Member> result5 = em.createQuery(query5, Member.class)
        .getResultList();
    ```



### Sub Query



```java
String query6 = "select (select avg(m1.age) from Member m1) as avgAge from m join Team t on m.username = t.name";

"select m from Member m where m.age > (select avg(m2.age) from Member m2)";

"select m from Member m where (select count(o) from Order o where m = o.member) > 0";
```

- #### 서브쿼리는 이미 있는 쿼리 내부에 쿼리를 또 작성하는 것이다.

- #### 서브쿼리는 여러 함수를 지원한다.

  - #### [NOT] EXISTS (subquery): 서브쿼리에 결과가 존재하면 참이다

    ```java
    // 팀A 소속인 회원
    "select m from Member m where exists (select t from m.team t where t.name = ‘팀A')"
    ```

    

  - #### {ALL | ANY | SOME} (subquery) 조건도 사용 가능하다.

    - #### ALL 모두 만족하면 참이다.

    ```java
    // 전체 상품 각각의 재고보다 주문량이 많은 주문들
    // 여기서 ALL은 o.orderAmount > ALL (select p.stockAmount from Product p) 에 해당된다.
    "select o from Order o where o.orderAmount > ALL (select p.stockAmount from Product p)"
    ```

    

    - #### ANY, SOME: 같은 의미, 조건을 하나라도 만족하면 참이다.

    ```java
    // 어떤 팀이든 팀에 소속된 회원
    // 여기서 ANY는 m.team = ANY (select t from Team t)" 에 해당된다.
    "select m from Member m where m.team = ANY (select t from Team t)"
    ```

    

  - #### [NOT] IN (subquery): 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참이다.

- #### JPA는 WHERE, HAVING 절에서만 서브 쿼리가 가능하다.

- #### 물론 SELECT 절도 하이버네이트에서 지원하여 가능하다.

- #### FROM 절의 서브쿼리는 현재 JPQL에서 불가능하여 조인으로 풀 수 있으면 풀어서 해결하거 Native SQL을 사용한다.

  ```java
  // FROM 절 서브쿼리 불가
  "select mm from (select m.age from Member m) as mm"
  ```




### JPQL Type

```java
// 문자
// 문자는 그대로 list에 포함하여 가져온다
String query7 = "select m.username, 'HELLO', TRUE from Member m";

// ENUM
// 패키지명을 포함해야 한다
String query7 = "select m.username, 'HELLO', TRUE from Member m " + "where m.type = jpql.MemberType.ADMIN";

// ENUM
// 파라미터 바인딩으로 패키지명 대체 가능하다
String query7 = "select m.username, 'HELLO', TRUE from Member m " + "where m.type = :userType";
List<Object[]> result7 = em.createQuery(query7)
    .setParameter("userType", MemberType.ADMIN)
    .getResultList();

// is not null
String query8 = "select m.username, 'HELLO', TRUE from Member m " + "where m.username is not null";

// between
String query9 = "select m.username, 'HELLO', TRUE from Member m " + "where m.age between 0 and 10";
```

- #### 문자 : 'HELLO'로 표현하여 그대로 문자를 가져올 수 있다. ''를 사용하면 '도 문자로 가져올 수 있다. 예를 들어, 'SHE''S'를 하면 'SHE'S'가 반환된다.

- #### 숫자 : 10L(Long), 10D(Double), 10F(Float)으로 숫자 자료형을 표현 할 수 있다.

- #### Boolean : TRUE, FALSE 로 반환 가능하다.

- #### ENUM : ENUM 변수명과 패키지명을 모두 입력해야 한다. 패키지명을 생략하려면 파라미터 바인딩을 이용해야 한다. QueryDSL을 이용하면 패키지를 input하여 사용할 수 있으므로 하드코딩을 하여 복잡하지 않게 ENUM을 사용할 수 있다.

- #### 엔티티 타입 : Type(m) = Member를 하여 상속 관계에서 사용한다. 이 코드는 엔티티 타입 정보가 Member인 객체만 사용한다는 뜻이다.

- #### JPQL은 SQL과 문법이 같은 식으로 EXISTS, IN, AND, OR, NOT, =, >, >=, <, <=, <>, BETWEEN, LIKE, IS NULL 과 같은 문법을 사용할 수 있다.



### CASE



```java
// 기본 CASE 식
String query =
		"select " +
			"case when m.age <= 10 then '학생요금' " +
			"     when m.age >= 60 then '경로요금' " +
			"     else '일반요금' " +
			"end " +
		"from Member m";

// 단순 CASE 식
String query = 
		"select " +
			"case t.name " +
			"	  when '팀A' then '인센티브110%' " +
			"	  when '팀B' then '인센티브120%' " +
			"	  else '인센티브105%' " +
			"end " +
		"from Team t";

// COALESCE
String query2 = "select coalesce(m.username, '이름 없는 회원') as username from Member m";

// NULLIF
String query3 = "select nullif(m.username, 'member1') as username from Member m";
```

- #### JPQL에서 기본, 단순 CASE 식으로 SQL의 CASE를 표현하는 것이 가능하다.

- #### 위 코드의 COALESCE는 회원 이름이 없으면 '이름 없는 회원'을 반환하게 하는 것이다.

- #### 위 코드의 NULLIF는 회원 이름이 member1이면 null을 반환하고 아니면 본인 이름을 반환하게 하는 것이다. Member1의 이름을 숨기고 싶을 때 사용한다.

- #### CASE, COALESCE, NULLIF는 JPA에서 제공하는 표준 함수이므로 모든 데이터베이스에 적용될 수 있다.



### JPQL Basic Function



```java
// CONCAT
String query = "select concat('a', 'b') from Member m";

// CONCAT
// ','는 '||'로 대체 가능하다
String query = "select 'a' || 'b' from Member m";

// SUBSTRING
String query = "select substring(m.username, 2, 2) from Member m";

// LOCATE
String query = "select locate('de', 'abcdegf') from Member m";

// SIZE
String query = "select size(t.members) from Team t";

// INDEX
String query = "select index(t.members) from Team t";

// 사용자 정의 함수 호출
String query = "select function('group_concat', m.username) from Member m";

// 사용자 정의 함수 호출
// hibernate에서는 직관적으로 쿼리를 풀이할 수 있다
String query = "select group_concat(m.username) from Member m";
```

```java
// 사용자 정의 함수 호출을 위한 Dialect 생성
public class MyH2Dialect extends H2Dialect {

    public MyH2Dialect(){
        // registerFunction으로 함수 등록
        // StandardSQLFunction을 들어가서 참조후 사용한다.
        registerFunction("group_concat", new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));
    }
}
```

```xml
<!--persistence.xml-->
<!--Dialect 사용자 정의한 것으로 바꾼다-->
<property name="hibernate.dialect" value="dialect.MyH2Dialect"/>
```

- #### CONCAT : 문자를 합칠 때 사용한다.

- #### SUBSTRING : 문자열에서 필요한 값을 가져올 때 사용한다.

- #### TRIM : 공백을 제거할 때 사용한다.

- #### LOWER, UPPER : 소문자, 대문자로 바꿀 때 사용한다.

- #### LENGTH : 문자열 길이가 필요할 때 사용한다.

- #### LOCATE : 첫 번째 문자부터 1로 하여 앞의 문자열이 시작하는 부분의 인덱스를 뒷 문자열에서 찾는다. Integer 형식으로 반환한다.

- #### ABS, SQRT, MOD : 절댓값, 제곱근, 나머지를 구할 때 사용한다.

- #### SIZE : 컬렉션으 크기를 알아볼 때 사용한다(연관관계 주인이 아닌 상대편에서 사용한다).

- #### INDEX : 리스트의 값 타입이 collection일 때 사용 가능하며 컬렉션 위치값을 구할 때 사용한다. 하지만 리스트 중간에 값이 없어지면 데이터가 null이 발생할 수 있으므로 사용하는 것을 권장하지 않는다. @OrderColumn을 사용하는 리스트일 경우에만 사용 가능하다.

- #### 사용자 정의 함수로 사용자가 직접 함수를 정의할 수 있다. 자신이 사용하는 데이터 베이스의 방언을 상속받은 뒤 persistence.xml의 방언 값을 생성한 클래스명으로 바꾼다. 위 코드의 사용자 정의 함수에서 registerFunction은 함수를 등록하는 것이며 standardSQLFunction은 이 함수를 찾아서 참조한다는 뜻이다.



## JPQL Path Expression



>select m.username -> 상태 필드
>  from Member m
>    join m.team t -> 단일 값 연관 필드
>    join m.orders o -> 컬렉션 값 연관 필드
>where t.name = '팀A'

```java
// 상태 필드
String query = "select m.username, m.age from Member m";

// 단일 값 연관 경로
String query = "select o.member from Order o";

// 컬렉션 값 연관 경로
String query = "select t.members from Team t";

// 컬렉션 값 연관 경로
// FROM 절을 이용한 명시적 조인 - 탐색 가능
String query = "select m.username from Team t join t.members m";

// 명시적 조인
String query = "select m from Member m join m.team t";

// 묵시적 조인
String query = "select m.team from Member m";

// 묵시적 조인 2번
String query = "select o.member.team from Order o";

// 컬렉션 값 연관 경로로 더 탐색이 불가능
String query = "select t.members.username from Team t";
```

```sql
# 상태 필드
select m.username, m.age from Member m

# 단일 값 연관 경로
# 묵시적 내부 조인 발생
select m.*
  from Orders o
  inner join Member m on o.member_id = m.id
```



- #### 경로 표현식은 .을 찍어 객체 그래프를 탐색하는 것이다.

- #### 상태 필드(state field) : 단순히 값을 저장하기 위한 필드이다. 예를 들어, m.username이 있다.

- #### 연관 필드(association field) : 연관관계를 위한 필드이다.

  - #### 단일 값 연관 필드 : @ManyToOne, @OneToOne, 대상이 엔티티인 경우이다. 예를 들어, m.team이 있다.

  - #### 컬렉션 값 연관 필드 : @OneToMany, @ManyToMany, 대상이 컬렉션인 경우이다. 예를 들어, m.orders가 있다.

- #### 상태 필드는 경로 탐색의 끝으로 더 이상 탐색이 불가능하다. 즉, m.username에서 .으로 더 들어갈 수 없다.

- #### 단일 값 연관 경로는 묵시적 내부 조인(inner join)이 발생하며 더 탐색이 가능하다. 즉, 내부 조인 후 프로젝션에 나열하는 것이다. 

- #### 컬렉션 값 연관 경로는 묵시적 내부 조인이 발생하며 탐색을 더 이상 하지 못한다.  컬렉션을 지정하는 형식이므로 뒤에 탐색을 이어갈 수 없다. 탐색을 가능하게 하려면 FROM 절에서 명시적 조인을 통해 별칭을 얻어서 별칭을 통해 탐색하면 된다.

- #### 컬렉션 값 연관 경로에서 1 : N 일 때 컬렉션에서 여러개의 값 중 어떤 값을 선택하여 가져올지 난감해지는 문제가 발생한다.

- #### 묵시적 내부 조인은 무존건 피해야 한다. 나중에 유지보수할 때 어디서 조인 쿼리가 실행되었는지 알기 힘들다. 또한 성능 튜닝에 큰 영향을 미치기 때문에 사용하면 안됀다.

- #### 명시적 조인으로 직접 조인해야 성능과 유지보수 능력을 향상 시킬 수 있다.

- #### 명시적 조인은 join 키워드를 직접 사용하는 것이다. 명시적 조인을 이용하여 어떠한 조인이 발생하는지 알 수 있다.

- #### 묵시적 조인은 경로 표현식에 의해 묵시적으로 SQL에 조인이 발생하는 것으로 내부 조인만 가능하다.

- #### 경로 탐색은 주로 SELECT, WHERE 절에서 사용하지만 묵시적 조인으로 인해 SQL의 FROM(JOIN)절에 영향을 줄 수 있으므로 주의해야 한다.

- #### 가급적이면 명시적 조인을 이용하며 SQL 튜닝을 효율적으로 하기 위해 JPQL을 SQL과 비슷하게 작성하는 것이 좋다.



## Fetch Join



### About Fetch Join

- #### 페치 조인은 SQL에는 없는 조인으로 JPQL 전용 기능이다.

- #### 페치 조인은 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회하는 기능이다.

- #### 페치 조인은 ::= [LEFT[OUTER]|INNER] JOIN FETCH 조인경로 형식으로 작성한다.



![페치조인](https://user-images.githubusercontent.com/79822924/144725088-83fe8eb5-ef44-4be6-9183-45956f64ae27.png)

![페치조인2](https://user-images.githubusercontent.com/79822924/144725089-4fa96136-ab27-4bee-a962-ffb99d6fbd7f.png)

![페치조인3](https://user-images.githubusercontent.com/79822924/144725094-5916f8e1-ddbf-4991-9a96-94890cb977ec.png)

```java
// N + 1 문제 발생
String query = "select m.team from Member m";

// fetch join으로 N + 1 문제 해결
String query = "select m from Member m join fetch m.team";

```

```sql
# "select m from Member m join fetch m.team";
SELECT M.*, T.* FROM MEMBER M INNER JOIN TEAM T ON M.TEAM_ID=T.ID
```



- #### 위 코드에서 Team이 100개가 연관되어 있고 모든 Team을 조회한다면 Member을 포함해서 100 + 1번의 쿼리가 전송된다. 즉, N + 1 문제가 발생한다.

- #### N + 1 문제를 해결하기 위해 페치 조인을 이용하면 연관된 Team에 대한 데이터를 쿼리 하나로 조인하여 가져온다. 즉, N + 1 문제를 해결하여 쿼리 1개만 전송하게 된다.

- #### 위 코드는 Member을 조회할 때 Team도 동적으로 같이 가져온다는 뜻이다. 또한, 적용된 SQL을 보면 Member와 Team 중 teamId가 같은 데이터를 모두 조인하여 나열했다는 뜻이다.

- #### 페치 조인은 즉시로딩하여 데이터를 가져오는 것과 비슷하게 동작하지만 쿼리로 사용자가 원하는 객체 그래프가 한번에 조회하는 것으로 직접 명시적으로 동적인 타이밍에 정해주는 것이다. 물론, 즉시로딩도 N + 1 문제가 있지만 즉시 조인하는 면에서 페치 조인과 비슷하긴 하다.

- #### 페치 조인은 조인하여 바로 가져왔으므로 프록시가 아닌 실제 엔티티를 반환한다.

- #### 페치 조인은 지연로딩보다 우선순위를 가지게 되어 지연로딩이어도 조인이 일어난다.



### Collection Fetch Join



![컬렉션 페치 조인](https://user-images.githubusercontent.com/79822924/144725099-3eeb3770-97c2-4391-9538-f2d72b35a964.png)

![컬렉션 페치 조인2](https://user-images.githubusercontent.com/79822924/144725105-282ceb2f-36b8-4ae1-bded-9a92116765a3.png)

![컬렉션 페치 조인3](https://user-images.githubusercontent.com/79822924/144725108-de02a7e9-c324-4c85-82cb-2b39694c3ab5.png)

```java
// 컬렉션 페치 조인
String query = "select t from Team t join fetch t.members";
// 데이터 뻥튀기 발생
/* 출력문
team = teamA|members=2
->  member = Member{id=4, username='member1', age=0}
->  member = Member{id=5, username='member2', age=0}
team = teamA|members=2
->  member = Member{id=4, username='member1', age=0}
->  member = Member{id=5, username='member2', age=0}
*/
```

```sql
# "select t from Team t join fetch t.members";
SELECT T.*, M.* FROM TEAM T INNER JOIN MEMBER M ON T.ID=M.TEAM_ID
```

- #### 컬렉션 페치 조인은 주로 1 : N 관계에서 나타나는데 데이터 뻥튀기가 발생한다. 즉, 위 그림과 같이 '팀A'에 소속된 회원이 2명이므로 리스트에 2개의 데이터가 같은 내용으로 들어가게 된다.

- #### 데이터 뻥튀기는 JPA 입장에서 '팀A'에 몇명의 회원이 소속되는지 모르므로 해결하지 못하고 중복하여 데이터를 가져오는 것이다.

- #### 리스트에는 2개의 데이터가 들어가지만 같은 엔티티 객체이므로 1개의 영속성 컨텍스트를 사용한다. 즉, 리스트에 들어가는 같은 데이터는 주소값이 동일하다.



![distinct 페치 조인](https://user-images.githubusercontent.com/79822924/144725157-5df3e3f2-8c66-46a9-a2c8-f46144cf4836.png)

```java
// DISTINCT로 데이터 뻥튀기 제거
String query = "select distinct t from Team t join fetch t.members";
// 데이터 뻥튀기 해결
/* 출력문
team = teamA|members=2
->  member = Member{id=4, username='member1', age=0}
->  member = Member{id=5, username='member2', age=0}
team = teamB|members=1
->  member = Member{id=6, username='member3', age=0}
team = teamB|members=1
->  member = Member{id=6, username='member3', age=0}
*/
```

- #### DISTINCT는 SQL에 존재하는 키워드로 중복을 제거하는 것이다. 하지만 SQL의 DISTINCT 기능은 완전히 동일한 레코드만 중복으로 간주하여 제거하기 때문에 데이터 뻥튀기를 해결하지 못한다.

- #### JPQL의 DISTINCT은 2가지 기능을 제공한다.

  - ####  SQL에 DISTINCT 키워드를 추가한다.

  - #### 어플리케이션에서 엔티티 중복을 제거한다. 즉, 데이터베이스가 아닌 어플리케이션 영역에서 똑같은 엔티티가 있으면 지우는 것으로 데이터 뻥튀기를 해결할 수 있다.

- #### DISTINCT로 같은 식별자(엔티티)를 가진 레코드를 삭제한다. 즉, 사용자는 JPQL에 DISTINCT를 써주는 것만으로 데이터 뻥튀기를 해결할 수 있다.



### Fetch Join & General Join Difference



```java
// 일반 조인
String query = "select t from Team t join t.members m";
// 데이터 뻥튀기
/* 출력문
team = teamA|members=2
->  member = Member{id=4, username='member1', age=0}
->  member = Member{id=5, username='member2', age=0}
team = teamA|members=2
->  member = Member{id=4, username='member1', age=0}
->  member = Member{id=5, username='member2', age=0}
team = teamB|members=1
->  member = Member{id=6, username='member3', age=0}
*/
```

```sql
# "select t from Team t join t.members m";
SELECT T.* FROM TEAM T INNER JOIN MEMBER M ON T.ID=M.TEAM_ID
```

- #### 일반 조인은 실행시 연관된 엔티티를 함께 조회하지 않는다. 즉, 위 코드를 보면 조인만 했지 영속성 컨텍스트에 Member을 가져온 것이 아니므로 Member을 조회하는 추가 쿼리가 전송된다. 위 코드에서는 Team 가져오는 쿼리 1개, team.getMembers() 하여 members를 접근할 때 members를 가져오는 쿼리 1개로 총 2개 쿼리로 구성된다.

- #### 일반 조인에서도 1 : N 경우 데이터 뻥튀기가 발생한다.

- #### JPQL은 결과를 반환할 때 연관관계를 고려하지 않는다. 단지 SELECT 절에 지정한 엔티티만 조회할 뿐이다.

- #### 페치 조인은 연관관계까지 모두 가져온다. 즉, 페치 조인으로 N + 1 문제를 거의 모두 해결할 수 있다.



### Fetch Join Limitation



```java
// 페치 조인 대상에는 별칭 부여 불가
// 이 코드에서 m 부여 불가
String query = "select t from Team t join fetch t.members m";

// 1 : 1, N : 1 관계에서는 페이징 API 가능
// but 1 : N 에서도 간단한 페이징이 허용되는 것으로 보이지만 사용하지 않는 것을 권장
String  query = "select m from Member m join fetch m.team t";

// 기본적인 N + 1 문제 batch_size로 해결
String query = "select t from Team t";
```

```java
// @BatchSize를 이용하여 N + 1 해결
@BatchSize(size = 100)
@OneToMany(mappedBy = "team")
private List<Member> members = new ArrayList<>();
```

```xml
<!--persistence.xml에 global setting으로 batch_size 설정-->
<property name="hibernate.default_batch_fetch_size" value="100"/>
```

- #### 페치 조인 대상에는 별칭을 줄 수 없다. 물론 하이버네이트에서는 가능하지만 가급적이면 사용하지 않는다.

- #### JPA 조인은 연관된 것을 모두 가져오는 것을 전제로 설계되었다. 즉, 별칭을 사용하여 특정 데이터만 가져오면 나머지 데이터는 조인 했는데 누락되어 오류 발생 위험이 높아지는 것이다. 페치 조인이 한 쿼리에 연관되어 몇번 사용할 때에 별칭을 사용하기도 하지만 권장하지 않는다. 특정 데이터 몇 개만 조회하고 싶다면 따로 조회 쿼리를 생성해야 한다.

- #### 둘 이상의 컬렉션은 페치 조인 할 수 없다. 즉, 1 : N : N이 되어 데이터 뻥튀기 및 조회 문제 발생 확률이 높다.

- #### 컬렉션을 페치 조인하면 페이징 API(setFirstResult, setMaxResults)를 사용할 수 없다. 1 : N의 경우 엔티티 데이터 뻥튀기로 데이터가 2개 이상일 때 페이지 1개만 가져오면 여러개 중 1개만 조회하는 것으로 나머지 데이터는 조회하지 않아서 데이터가 1개만 존재하는 것 처럼 보일 수 있다.

- #### 1 : 1, N : 1은 같은 단일 값 연관 필드들은 페치 조인해도 페이징이 가능하다. 데이터 뻥튀기가 일어나지 않기 때문에 가능하다.

- #### 지연 로딩으로 N + 1이 발생할 경우 batch size로 해결한다. 즉, batch size는 하위 엔티티를 로딩할때 한번에 상위 엔티티 ID를 지정한 숫자만큼 in Query로 로딩한다. 컬렉션에 @BatchSize를 부여해도 되며 실무에서는 대부분 global setting으로 설정한다.

- #### 페치 조인은 객체 그래프를 유지할 때 사용하면 효과적이다.

- #### 여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 낼 때 페치 조인보다는 일반 조인을 사용한 후 DTO로 반환하는 것이 효과적이다.

- #### 조인하여 데이터를 조회하는 방법에는 3가지가 있다.

  - #### 페치 조인으로 엔티티를 조회한다.

  - #### 페치 조인을 어플리케이션에서 DTO로 변환한다.

  - #### 처음부터 쿼리 전송할 때 DTO로 new로 반환한다.



## JPQL Polymorphism Query



### TYPE & TREAT

![다형성 쿼리](https://user-images.githubusercontent.com/79822924/144725163-c3bbe38e-4c18-42fa-81dd-edb2b0dbb91a.png)

```java
// TYPE
// Item 중 Book, Movie를 조회
String query = "select i from Item i where type(i) IN (Book, Movie)";
    
// TREAT
// 저자가 'kim'인 Item 조회
String query = "select i from Item i where treat(i as Book).auther = ‘kim’";
```

```sql
# 현재 Single_Table 전략이다

# "select i from Item i where type(i) IN (Book, Movie);
select i from i where i.DTYPE in (‘B’, ‘M’)

# "select i from Item i where treat(i as Book).auther = ‘kim’"
select i.* from Item i where i.DTYPE = ‘B’ and i.auther = ‘kim’
```

- #### TYPE을 이용하여 조회 대상을 특정 자식으로 한정하여 조회할 수 있다.

- #### TREAT는 자바의 타입 캐스팅과 유사하다. 즉, 다운 캐스팅을 사용하는 것과 유사한 방식이다.

- #### 상속 구조에서 부모 타입을 특정 자식 타입으로 다룰 때 사용한다.

- #### FROM, WHERE, SELECT에서 사용 가능하다. 하이버네이트에서 지원한다.



## Direct Entity Usage



### Primary Key



```java
// 엔티티 직접 사용으로 기본키 값으로 비교
// 두 코드 모두 동일한 SQL 전송
String query = "select count(m.id) from Member m";
String query = "select count(m) from Member m";

// 두 코드 모두 동일한 SQL 전송
String jpql = "select m from Member m where m = :member";
List resultList = em.createQuery(jpql)
    .setParameter("member", member)
    .getResultList();
String jpql = "select m from Member m where m.id = :memberId";
List resultList = em.createQuery(jpql)
    .setParameter("memberId", memberId)
    .getResultList();
```

```sql
# "select count(m.id) from Member m";
# "select count(m) from Member m";
select count(m.id) as cnt from Member m

# "select m from Member m where m = :member";
# "select m from Member m where m.id = :memberId";
select m.* from Member m where m.id=?
```



- #### JPQL에서 엔티티를 직접 사용하면 SQL에서 해당 엔티티의 기본키 값을 사용한다.



### Foreign Key



```java
// 외래키 값으로 비교
// 두 코드 모두 동일한 SQL 전송
String qlString = "select m from Member m where m.team = :team";
List resultList = em.createQuery(qlString)
    .setParameter("team", team)
    .getResultList();
String qlString = "select m from Member m where m.team.id = :teamId";
List resultList = em.createQuery(qlString)
    .setParameter("teamId", teamId)
    .getResultList();
```

```sql
# "select m from Member m where m.team = :team";
# "select m from Member m where m.team.id = :teamId";
select m.* from Member m where m.team_id=?
```

- #### 연관된 엔티티로 데이터 조회시 엔티티를 직접 사용하면 해당 엔티티의 외래키 값으로 비교한다.



## Named Query



```java
@Entity
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)
public class Member {
}
```

```java
List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
    .setParameter("username", "member1")
    .getResultList();
```

```xml
<!--persistence.xml-->
<persistence-unit name="jpabook" >
	<mapping-file>META-INF/ormMember.xml</mapping-file>
    
<!--ormMember.xml-->
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm" version="2.1">
	<named-query name="Member.findByUsername">
		<query><![CDATA[
			select m
			from Member m
			where m.username = :username
		]]></query>
	</named-query>
	<named-query name="Member.count">
		<query>select count(m) from Member m</query>
	</named-query>
</entity-mappings>
```

- #### 네임드 쿼리는 미리 정의해서 이름을 부여하고 사용하는 JPQL로 정적 쿼리로 사용한다.

- #### 어노테이션, XML로 미리 정의하는 것이 가능하다.

- #### 어플리케이션 로딩 시점에 초기화 후 재사용하는 것이 가능하다. 즉, 로딩 시점에 하이버네이트, JPA가 이 JPQL을 미리 파싱하고 캐시에 넣어둔다.

- #### 어플리케이션 로딩 시점에 쿼리를 검증하는 것이 가능하다. 즉, 미리 로딩 시점에 하이버네이트, JPA가 쿼리를 체크 후 문법에 오류가 있으면 오류를 발생시킨다.

- #### XML이 항상 우선권을 가지게 되며 어플리케이션 운영 환경에 따라 다른 XML을 배포할 수 있다.



## JPQL Bulk



```java
// flush 자동 호출한다
int resultCount = em.createQuery("update Member m set m.age = 20")
    .executeUpdate();

System.out.println("resultCount = " + resultCount);
// 4 출력

// 여기서는 나이가 모두 0 으로 나온다
// 벌크는 영속성을 무시하고 날리기 때문이다
// 즉 이 출력문을 호출하기 전에 영속성 컨텍스트를 초기화해야 한다.
System.out.println("member1.getAge() = " + member1.getAge());
System.out.println("member2.getAge() = " + member2.getAge());
System.out.println("member3.getAge() = " + member3.getAge());
// 모두 0 출력

// 정상 작 동
em.clear();
Member findMember = em.find(Member.class, member1.getId());

System.out.println("findMember = " + findMember.getAge());
// 20 출력

// 재고가 10개 미만인 상품을 리스트로 조회한다.
// 상품 엔티티의 가격을 10% 증가한다.
String qlString = "update Product p " +
    			  "set p.price = p.price * 1.1 " +
    			  "where p.stockAmount < :stockAmount";
int resultCount = em.createQuery(qlString)
    .setParameter("stockAmount", 10)
    .executeUpdate();
```

- #### 벌크 연산은 SQL의 UPDATE, DELETE 쿼리를 일괄 처리한다.

- #### INSERT(insert into ... select)를 하이버네이트에서 지원한다. 즉, select해서 INSERT한다는 뜻이다.

- #### 벌크 연산은 쿼리 한 번으로 여러 테이블 레코드를 변경한다(엔티티).

- #### ExecuteUpdate()는 결과로 영향 받은 엔티티의 수를 반환한다.

- #### 변경 감지 기능으로 실행할 때 발생하는 너무 많은 SQL 실행을 방지한다.

- #### 벌크 연산도 JPQL을 날리는 것이므로 자동으로 flush()를 호출한다.

- #### 벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리를 전송하기 때문에 주의해서 사용해야 한다. 즉, 데이터를 조회하여 사용하는 도중에 벌크 연산 쿼리가 전송되면 어플리케이션 영속성 컨텍스트에 있는 데이터와 데이터베이스에 있는 데이터가 일치하지 않을 수 있다.

- #### 벌크 연산 주의사항을 방지하는 방법에는 2가지가 있다.

  - #### 벌크 연산을 먼저 실행한다. 즉, 가장 먼저 벌크 연산을 사용하면 영속성 컨텍스트에 아무것도 없다.

  - #### 벌크 연산 수행 후 영속성 컨텍스트를 초기화한다. 즉, 벌크 연산 후 바로 영속성 컨텍스트를 clear()을 사용하여 초기화한다. 이 방법을 거의 필수적으로 사용한다.



Image 출처 : [김영한 ORM 표준 JPA](https://www.inflearn.com/course/ORM-JPA-Basic/dashboard)
