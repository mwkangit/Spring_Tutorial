# JPA Background



- #### SQL로 데이터베이스 로직에 CRUD 방식을 적용하기에는 너무 많은 코드가 필요하다. 또한, 데이터베이스 스키마 수정 시 유지보수를 해결하기 쉽지 않다.

- #### 모든 객체를 미리 로딩할 수 없어서 계층 분할이 어려웠다. 또한, 쿼리를 하나의 인스턴스에 너무 많이 날리게 되어 불필요한 지연이 발생한다.

- #### Collection에 저장하는 방식으로 데이터베이스에 의존적이지 않은 객체 지향형으로 데이터를 관리하기 위해 Hibernate가 탄생했다. 

- #### Hibernate를 자바 표준으로 인정하고 발전하여 JPA 가 탄생했다.



![JPA Logic](/media/mwkang/Klevv/Spring 일지/ORM/11.14/JPA Logic.png)

- #### JPA는 트랜잭션 내에서 이루어지며 동일한 ㅡ랜잭션에서 조회한 엔티티는 같음을 보장한다.

- #### 1차 캐시는 영속성 컨테이너를 운영하는 것이다. 즉, 한번 사용한 쿼리를 중복하여 사용하지 않도록 캐시를 운영하여 바로 이용 가능하게 한다.

- #### 쓰기 지연을 사용하여 트랜잭션 커밋 시 컨테이너에 모아놓은 쿼리를 한번에 데이터베이스에 전송할 수 있다.

- #### 지연로딩은 객체가 실제 사용될 때 로딩되는 것이며 즉시로딩은 JOIN SQL로 한번에 연관된 객체까지 미리 조회하는 것이다.



# JPA 설정



```xml
<!--persistence.xml-->

<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.2"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">
    <persistence-unit name="hello">
        <properties>
            <!-- 필수 속성 -->
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
            <property name="javax.persistence.jdbc.user" value="sa"/>
            <property name="javax.persistence.jdbc.password" value=""/>
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/ORM"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
            <!-- 옵션 -->
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
            <property name="hibernate.use_sql_comments" value="true"/>
            <property name="hibernate.jdbc.batch_size" value="10"/>
            <property name="hibernate.hbm2ddl.auto" value="create" />
        </properties>
    </persistence-unit>
</persistence>
```

```xml
<!--pom.xml-->

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>jpa-basic</groupId>
    <artifactId>ex1-hello-jpa</artifactId>
    <version>1.0.0</version>

    <properties>
        <maven.compiler.source>15</maven.compiler.source>
        <maven.compiler.target>15</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- JPA 하이버네이트 -->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-entitymanager</artifactId>
            <version>5.3.10.Final</version>
        </dependency>
        <!-- H2 데이터베이스 -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>1.4.200</version>
        </dependency>
        <!-- java 11 -->
        <dependency>
            <groupId>javax.xml.bind</groupId>
            <artifactId>jaxb-api</artifactId>
            <version>2.3.0</version>
        </dependency>
    </dependencies>

</project>
```

- #### Pom.xml을 이용하여 JPA 버전, 사용할 데이터베이스 연동, 자바 버전을 설정할 수 있다.

- #### Persistence.xml은  resource 디렉토리의 META-INF 내부에 작성한다. 데이터베이스 방언과 JPA 속성을 설정할 수 있다.



# JPA 구동 방식



![JPA 구동 방식](/media/mwkang/Klevv/Spring 일지/ORM/11.14/JPA 구동 방식.png)

- #### EntityManagerFactory는 하나만 생성하여 어플리케이션 전체에 공유한다.

- #### 엔티티 매니저는 쓰레드간 공유하면 안돼며 JPA의 모든 데이터 변경은 트랜잭션 안에서 실행되어야 한다.

- #### 어플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 필요한 SQL이 필요하다. 이러한 SQL을 지원한는 것이 JPQL이다.

- #### JPQL은 SQL을 추상화한 것으로 객체 지향 쿼리 언어이다.

- #### 단, SQL은 DB 테이블을 대상으로 쿼리하지만 JPQL은 엔티티 객체를 대상으로 쿼리한다. 즉, DB SQL에 의존하지 않게 할 수 있다.



# 영속성 관리



## 영속성 컨텍스트



![엔티티 매니저 팩토리와 엔티티 매니저](/media/mwkang/Klevv/Spring 일지/ORM/11.14/엔티티 매니저 팩토리와 엔티티 매니저.png)

- #### 엔티티 매니저 팩토리는 유일하며 각 사용자마다 엔티티 매니저를 할당한다. 즉, 각 트랜잭션 마다 엔티티 매니저를 할당한다.

- #### 엔티티 매니저를 통해 영속성 컨텍스트에 접근 가능하다.

- #### 영속성 컨텍스트는 논리적인 개념으로 눈에 보이지 않는다.



![엔티티의 생명주기](/media/mwkang/Klevv/Spring 일지/ORM/11.14/엔티티의 생명주기.png)

- #### 비영속(new/transient)는 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태로 메모리에서 사용하는 변수의 개념으로 생각할 수 있다.

- #### 영속(managed)는 영속성 컨텍스트에서 관리되는 상태이다. Persist()를 통해 영속성 컨텍스트로 등록할 수 있다.

- #### 준영속(detached)은 영속성 컨텍스트에 저장되었다가 분리된 상태이다. Detach() 로 영속성 컨텍스트에서 분리할 수 있다.

- #### 삭제(removed)는 삭제된 상태이다. Remove() 로 객체를 삭제할 수 있다.



## 영속성 컨텍스트의 이점



![1차 캐시 이점](/media/mwkang/Klevv/Spring 일지/ORM/11.14/1차 캐시 이점.png)

- #### 한 트랜잭션에서 영속성 컨텍스트에 등록시 1차 캐시에 등록되며 다시 특정 객체를 사용하고자하면 1차 캐시에서 먼저 객체를 탐색하여 사용한다.

- #### Persist(), find() 등으로 1차 캐시에 객체를 등록하는 것이 가능하다.

- #### 1차 캐시로 데이터베이스에 접근하는 경우의 감소로 성능이 향상될 수 있다.



```java
Member a = em.find(Member.class, "member1");
Member b = em.find(Member.class, "member1");
System.out.println(a == b); // true
```



- #### 1차 캐시로 반복 가능한 읽기(REPEATABLE READ) 등급의 트랜잭션 격리 수준을 데이터베이스가 아닌 어플리케이션 차원에서 제공하여 영속 엔티티의 동일성을 보장할 수 있다.



![쓰기지연](/media/mwkang/Klevv/Spring 일지/ORM/11.14/쓰기지연.png)

- #### 영속성 컨텍스트는 쓰기 지연 저장소에 SQL 쿼리를  저장한다.

- #### Persist() 시점에는 1차 캐시, 쓰기 지연 저장소에 정보를 저장하는 것으로 아직 데이터베이스에 전송하지 않는다.

- #### Flush()로 쓰기 지연 저장소의 SQL을 데이터베이스에 전송하고 commit()으로 데이터베이스에 저장한다. Commit()으로 flush()를 모두 해결할 수 있다.

- #### Commit() 후에도 트랜잭션이 종료되지 않았으면 영속성 컨텍스트는 삭제되지 않고 그대로 남아있다.

- #### 모든 과정은 동일한 트랜잭션에서 이루어져야 한다.



![변경 감지](/media/mwkang/Klevv/Spring 일지/ORM/11.14/변경 감지.png)

- #### 스냅샷은 엔티티가 영속성 엔티티에 최초로 들어온 시점의 상태이다.

- #### Flush()가 발생하면 현재 엔티티를 스냅샷과 비교하여 변경된 것에 대한 update 쿼리를 쓰기 지연 저장소에 저장한다. 하나하나 비교하여 다른 것을 찾아내는 것으로 변경 감지(Dirty Checking)이라고 한다.

- #### Update 쿼리가 반영된 후 commit()으로 데이터베이스에 저장된다.



```java
Member memberA = em.find(Member.class, “memberA");
em.remove(memberA); //엔티티 삭제
```

- #### 이미 저장된 엔티티를 조회 후 삭제하면  commit() 시점에 데이터베이스에 반영된다.

- #### Flush()는 영속성 컨텍스트의 변경내용을 데이터 베이스에 반영하는 것이다.

- #### Flush()는 변경 감지, 수정된 엔티티 쓰기 지연  저장소에 등록, 쓰기 지연 저장소의 쿼리를 데이터베이스에 전송(등록, 수정, 삭제 쿼리)할 때 발생한다.

- #### Flush()는 직접 호출할 수도 있지만 트랜잭션 commit(), JPQL 쿼리 실생 시점에 자동으로 호출 된다.



```java
em.setFlushMode(FlushModeType.COMMIT);
```

- #### FlushModeType.AUTO는 default 값으로 commit이나 쿼리를 실행할 때 자동으로 flush()되도록 한다.

- #### FlushModeType.COMMIT은 commit()할 때에만 flush()가 발생하게 하는 것이다.

- #### Flush()는 영속성 컨텍스트를 비우지 않으며 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화 할 뿐이다.

- #### Commit() 직전에 반드시 flush()를 실행해야 한다.

- #### Em.detach(entity)를 통해 특정 엔티티를 준영속 상태로 만들 수 있다.

- #### Em.clear()를 통해 영속성 컨텍스트를 완전히 초기화 할 수 있다.

- #### Em.close()를 통해 영속성 컨텍스트를 종료할 수 있다.



# Entity Mapping



## Entity

- #### @Entity가 붙은 클래스는 JPA가 관리하며 엔티티라고 부른다.

- #### Reflection, proxy를 위해 기본 생성자는 public이나 protected로 필수적으로 만들어야 한다.

- #### Final 클래스, enum, interface, inner 클래스에는 적용할 수 없으며 DB에 저장할 필드에 final 키워드를 사용할 수 없다.

- #### Name 속성으로 JPA에서 사용할 이름을 지정할 수 있다. Default 값은 클래스 이름을 그대로 사용하는 것이다.



## Table

- #### @Table은 엔티티와 매핑할 테이블을 지정한다.

- #### Name 속성은 매핑할 테이블 이름을 지정한다. Default 값은 엔티티 이름을 사용한다.

- #### Catalog 속성은 데이터베이스 catalog에 매핑한다.

- #### Schema 데이터베이스 schema에 매핑한다.

- #### UniqueConstraints(DDL) 속성은 DDL 생성 시에 유니크 제약 조건을 생성하는 것이다.



## Database Schema Auto Creation



```xml
<property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>

<property name="hibernate.hbm2ddl.auto" value="create" />
```

- #### DDL을 어플리케이션 실행 시점에 자동 생성하는 것이 가능하다.

- #### 이러한 방식으로 생성한 DDL은 개발 장비에서만 사용하며 운영에는 사용하면 안됀다.

- #### Persistence.xml에서 DB 방언을 선택할 수 있으며 데이터베이스 자동 생성 속성을 지정할 수 있다.

- #### DDL 자동 생성 방식에는 여러 속성이 존재한다.

  - #### Create 속성은 기존 테이블을 삭제후 다시 생성하는 것이다(DROP + CREATE).

  - #### Create-drop 속성은 create와 같으나 종료시점에 테이블을 drop한다.

  - #### Update 속성은 변경부분만 반영이 되지만 운영 DB에는 사용하면 안됀다(Column 삭제는 불가하다).

  - #### Validate 속성은 엔티티와 테이블이 정상 매핑되었는지만 확인한다. 즉, column 관계가 모두 일치하는지 확인하는 것이다.

  - #### None 속성은 아무것도 사용하지 않겠다는 것이다.

- #### 보통 개발 초기 단계에는 local로 create, update로 시작하며 테스트 서버에서는 여러 사람이 사용하므로 update, validate를 사용한다. 그리고 스테이징과 운영 서버는 validate, none을 사용한다.



```java
@Column(nullable = false, length = 10)
```

- #### 제약조건을 추가할 수 있다. 회원 명은 필수이며 10자를 넘으면 안돼는 조건이다.

```java
@Table(uniqueConstraints = {@UniqueConstraint(name = "NAME_AGE_UNIQUE", columnNames = {"NAME", "AGE"})})
```

- #### 유니크 제약조건을 추가하는 것이 가능하다.

- #### 즉, DDL 생성 기능은 DDL을 자동 생성할 때만 사용되고 JPA의 실행 로직에는 영향을 주지 않는다.



## Field & Column Mapping



```java
	@Id
	private Long id;
	
	@Column(name = "name")
	private String username;

	private Integer age; // 가장 적절한 숫자타입 만들어서 부여한다.

    @Enumerated(EnumType.STRING) // db에는 enum 타입이 없어서 @Enumerated를 붙여준다.
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP) // 타입에 Date, Time, TimeStamp가 있다. TimeStamp는 둘다 사용한 것이다.
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob  Clob, Blob 사용 가능하다. VARCHAR을 넘는 큰 컨텐츠를 입력하고 싶으면 @Lob을 사용한다. // @Lob 시 자료형이 문자타입이면 clob 실행한다.
//    private String description;
```

- #### 기본 매핑 어노테이션에는 여러 종류가 존재한다.

  - #### @Column 어노테이션은 컬럼에 매핑하는 것이다.

  - #### @Temporal 어노테이션은 날짜 타입에 매핑하는 것이다.

  - #### @Enumerated 어노테이션은 enum 타입에 매핑하는 것이다

  - #### @Lob 어노테이션은 BLOB, CLOB을 매핑한다.

  - #### @Transient 어노테이션은 특정 필드를 컴럼에 매핑하지 않는 것으로 매핑을 무시하는 것이다. 즉, DB와 상관없는 변수를 memory에서만 사용하는 것이다.

- #### @Column 어노테이션에는 여러 속성이 존재한다.

  - #### Name 속성은 필드와 매핑할 테이블의 컬럼 이름을 지정할 수 있다. Default 값은 객체의 필드 이름이다.

  - #### Insertable, Updatable 속성은 등록, 변경 가능 여부를 결정한다. Default 값은 TRUE이다.

  - #### Nullable(DDL) 속성은 null 값의 허용 여부를 설정한다. False로 설정하면 DDL 생성 시에 not null 제약조건이 붙는다.

  - #### Unique(DDL) 속성은 @Table의 uniqueConstraints와 같지만 한 컬럼에 간단히 유니크 제약조건을 걸 때 사용한다. 보통 table에 사용해야 정상적인 실행 콘솔 확인이 가능하다.

  - #### ColumnDefinition(DDL) 속성은 데이터베이스 컬럼 정보를 직접 줄 수 있다. Default 값은 필드의 자바 타입과 방언 정보를 사용한다. varchar(100) default 'EMPTY'와 같은 정보를 부여할 수 있다.

  - #### Length(DDL) 속성은 문자 길이 제약조건, String 타입에만 사용한다. Default 값은 255이다.

  - #### Precision, scale(DDL) 속성은 BigDecimal 타입과 같은 아주 큰 숫자나 소수 사용시 사용하며 BigInteger도 사용 가능하다. Precision은 소수점을 포함한 전체 자릿수를, scale은 소수의 자릿수이다. 참고로 double, float 타입에는 적용되지 않는다.
  
- #### @Enumerated에는 하나의 속성이 있다.

  - #### Value 속성은 EnumType.ORDINAL, EnumType.STRING이 있으며 ORDINAL은 enum 순서를 데이터베이스에 저장하며 STRING은 enum 이름을 데이터베이스에 저장한다. Default 값은 ORDINAL이다. ORDINAL은 수정 시 값의 순서에 따라 변화할 수 있으므로 위험하다. STRING을 사용하는 것이 좋다.

- #### @Temporal은 날짜 타입(java.util.Date, java.util.Calendar)을 매핑할 때 사용한다. 참고로 LocalDate(연 월), LocalDateTime(연 월 시)을 사용할 때에는 생략 가능하다.

  - #### Value 속성은 TemporalType.DATE, TemporalType.TIME, TemporalType.TIMESTAMP 가 있는데 DATE는 날짜, 데이터베이스 date 타입과 매핑하고 TIME은 시간, 데이터베이스 time 타입과 매핑하고 TIMESTAMP는 날짜와 시간, 데이터베이스 timestamp 타입과 매핑한다.

- #### @Lob은 데이터베이스 BLOB, CLOB 타입과 매핑한다. @Lob에는 지정할 수 있는 속성이 없으며 매핑하는 필드 타입이 문자면 CLOB 매핑, 나머지는 BLOB 매핑을 한다.

  - #### CLOB : String, char[], java.sql.CLOB

  - #### BLOB : byte[], java.sql.BLOB

- #### @Transient는 필드에 매핑하지 않고 데이터베이스에 저장 및 조회 기능을 사용하지 않는 메모리상에서만 임시로 어떤 값을 보관하고 싶을 때 사용한다.

  - #### @Transient

    #### private Integer temp;



## Primary Key Mapping



```java
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long id;
```

- #### 기본키 직접 할당 시 @Id만 사용한다.

- #### 기본키 자동 생성 시 @GeneratedValue와 속성을 이용한다.

  - #### IDENTITY : 데이터베이스에 위임한다. 주로 MYSQL 에 사용한다.

  - #### SEQUENCE : 데이터베이스 시퀀스 오브젝트를 사용한다. 주로 ORACLE에 사용하며 @SequenceGenerator이 필요하다.

  - ####  TABLE : 키 생성용 테이블을 사용하며 모든 DB에서 사용 가능하다. @TableGenerator이 필요하다.

  - #### AUTO : 방언에 따라 자동으로 지정한다. @GeneratedValue의 default 값이다.



```java
@Entity
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
}
```

- #### IDENTITY는 기본키 생성을 데이터베이스에 위임하는 것으로 MYSQL, PostgreSQL, SQL Server, DB2에서 사용하며 MYSQL의 AUTO_INCREMENT로 볼 수 있다.

- #### 쿼리에 NULL값 주면 데이터베이스에서 알아서 기본키를 계산하여 생성한다.

- #### 영속성 관리를 하려면 기본키가 있어야 하지만 IDENTITY는 기본키가 데이터베이스에 도달해서 만들어진다. 즉, 영속성 관리를 위해 persist 시점에 바로 쿼리가 데이터베이스로 전송된다.

- #### IDENTITY는 쿼리를 즉시 전송하여 지연쓰기 전략이 불가능하다.



```java
@Entity
@SequenceGenerator(
name = “MEMBER_SEQ_GENERATOR",
sequenceName = “MEMBER_SEQ", //매핑할 데이터베이스 시퀀스 이름
initialValue = 1, allocationSize = 1)
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
	private Long id;
}
```

- #### SEQUENCE는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트로 ORACLE, PostgreSQL, DB2, H2 Database에서 사용한다.

- #### 시퀀스는 String, Integer이 아닌 Long으로 사용하는 것이 좋다.

- #### 시퀀스도 IDENTITY와 동일하게 초기에 기본키 값이 없어서 영속성으로 관리하지 못한다. 성능 최적화를 위해 첫 쿼리 전송 시 할당한 사이즈 만큼 시퀀스를 가져온 후 사이즈 만큼의 시퀀스를 이용한다. 만약 사이즈를 모두 사용했다면 쿼리가 다시 전송되어 다음 사이즈만큼 시퀀스를 가져와서 사용한다. 즉, 미리 데이터베이스에 할당 사이즈 만큼 시쿤스를 생성하는 것이다.

- #### 테이블마나 시퀀스를 따로 관리하려면 @SequenceGenerator로 매핑해야 한다.

- #### @SequenceGenerator에는 여러 속성이 존재한다.

  - #### Name 속성은 식별자 생성기 이름을 부여하는 것으로 필수이다.

  - #### SequenceName 속성은 데이터베이스에 등록되어 있는 시퀀스 이름이다. Default  값은 hibernate_sequence이다.

  - #### InitialValue 속성은 DDL 생성 시에만 사용하며 시퀀스 DDL을 생성할 때 처음 1 시작하는 수를 지정한다. Default 값은 1이다.

  - #### AllocationSize 속성은 시퀀스 한 번 호출에 증가하는 수로 성능 최적화에 사용한다. 데이터베이스 시퀀스 값이 하나씩 증가하도록 설정되어 있으면 이 값을 반드시 1로 설정해야 한다. Default 값은 50이다.

  - #### Catalog, Schema 속성은 데이터베이스 catalog, schema 이름에 매핑한다.



```java
@Entity
@TableGenerator(
name = "MEMBER_SEQ_GENERATOR",
table = "MY_SEQUENCES",
pkColumnValue = “MEMBER_SEQ", allocationSize = 1)
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "MEMBER_SEQ_GENERATOR")
	private Long id;
}
```

- #### TABLE은 키 생성 전용 테이블을 생성하여 데이터베이스 시퀀스를 흉내내는 전략이다.

- #### 모든 데이터베이스에 적용 가능한 것이 장점이지만 테이블을 직접 사용하여 Lock이 가능하고 최적화가 불가능하여 성능이 다른 전략에 비해 좋지 않을 수 있다.

- #### TABLE은 실제 시퀀스 테이블을 생성한 후 next-value로 기본키 값을 가져온다.

- #### @TableGenerator에는 여러 속성이 존재한다.

  - #### Name 속성은 식별자 이름을 지정하며 필수이다.

  - #### Table 속성은 키 생성 테이블명을 부여한다. Default 값은 hibernate_sequences이다.

  - #### PkColumnName 속성은 시퀀스 컬럼명을 부여한다. Default 값은 sequence_name이다.

  - #### ValueColumnNa 속성은 시퀀스 값 컬럼명이다. Default 값은 next_val이다.

  - #### PkColumnValue 속성은 키로 사용할 값의 이름을 부여한다. Default 값은 엔티티 이름이다.

  - #### InitialValue 속성은 초기 값으로 마지막으로 생성된 값이 기준이다. Default 값은 0이다.

  - #### AllocationSize 속성은 시퀀스 한 번 호출에 증가하는 수 이다. Default 값은 50이다. 여러 서버가 한 데이터베이스를 이용해도 미리 할당 크기를 정해놨으므로 오류가 발생하지 않는다.

  - #### Catalog, Schema 속성은 데이터베이스 catalog, schema 이름을 지정한다.

  - #### UniqueConstraint 속성은 유니크 제약 조건을 지정한다.

- #### 기본키 제약조건은 NULL이 아니어야 하며 유일하고 변하면 안됀다.

- #### 비즈니스를 키로 가져오는 것은 매우 위험한 행동으로 generatedvalue, random 값과 같은 비즈니스와 관련없는 값을 사용해야 한다. 예를 들어, 주민등록번호는 기본키로 사용하면 안됀다. 다른 테이블에서 외래키로 사용하다가 모두 바뀌는 일이 발생할 수 있다.

- #### 권장하는 규격은 Long형 + 대체키 + 키 생성전략을 사용하는 것이다.



# 연관관계 매핑

- #### 객체를 테이블에 맞추어 데이터 중심으로 모델링하면 협력 관계를 만들 수 없다.

- #### 테이블은 외래키로 조인을 사용해서 연관된 테이블을 찾는다.

- #### 객체는 참조를 사용해서 연관되 객체를 찾는다.



## 단방향 연관관계



![단방향 연관관계](/media/mwkang/Klevv/Spring 일지/ORM/11.14/단방향 연관관계.png)

```java
@Entity
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;
    
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;
}
```

- #### Member 엔티티에 Team 엔티티의 Id가 외래키로 있고 N : 1 경우 Member이 연관관계 주인이 되어 @ManyToOne 어노테이션으로 외래키를 매핑한다.

- #### Find()를 사용하면 연관된 속성도 조회할 수 있다.

- #### 연관관계 수정 시 수정할 엔티티를 미리 persist 후 collection 방식으로 수정하면 된다.



## 양방향 연관관계



![양방향 연관관계](/media/mwkang/Klevv/Spring 일지/ORM/11.14/양방향 연관관계.png)

![양방향 연관관계2](/media/mwkang/Klevv/Spring 일지/ORM/11.14/양방향 연관관계2.png)

```java
@Entity
public class Team{
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	
	@OneToMany(mappedBy = "team")
	List<Member> members = new ArrayList<Member>();
}
```

- #### Team 엔티티 기준 1 : N 이면서 Member의 team 변수와 매핑 되었다는 뜻이다.

- #### ArrayList로 객체를 생성해야 add할 때 NullPoint가 발생하지 않는다.

- #### 양방향 매핑은 엔티티가 양쪽에서 참조하여 사용 가능해야할 때 사용한다.

- #### 테이블은 사실상 외래키로 연결되어 join하면 되기 때문에 외래키 생성 시 이미 양방향이라고 할 수 있다.



## 연관관계 주인과 mappedBy



- #### 객체의 양방향 관계는 사실 양방향 관계가 아니라 서로 다른 단방향 관계 2개이다.

- #### 테이블은 외래키 하나로 두테이블을 join할 수 있어서 외래키 하나로 양방향 관계가 성립한다.

- #### 연관관계의 주인은 외래키가 있는 엔티티로 해야 한다.

- #### 연관관계의 주인만이 외래키를 관리할 수 있다(등록, 수정).

- #### 주인이 아닌쪽은 읽기만 가능하다.

- #### MappedBy는 어떠한 것의 주인이 아닌 뜻으로 주인은 mappedBy 속성을 사용하지 않는다. 즉, 주인이 아니면 mappedBy 속성으로 주인을 지정한다.



```java
Team team = new Team();
team.setName("TeamA");
em.persist(team);

Member member = new Member();
member.setName("member1");

team.getMembers().add(member);
member.setTeam(team); 
em.persist(member);
```

- #### 양방향 매핑시 연관관계의 주인에 값을 입력해야 한다.

- #### 순수한 객체 관계를 고려하면 항상 양쪽 모두 값을 입력해야 한다.



```java
public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this); // 현재 이 객체를 team의 members에 추가한다.
    }
```

- #### 연관관계 편의 메소드를 엔티티에 생성하여 양쪽에 값을 설정할 수 있다.

- #### 양방향 매핑 시 toString(), lombok, JSON으로 무한 루프 및 오류가 발생할 수 있다.

  - #### toString(), lombok :  양쪽의 엔티티가 각각 상대방의 toString() 혹은 lombok 메소드를 호출하여 무한 루프에 빠질 수 있다.

  - #### JSON : 양방향 연관관계일 경우 엔티티를 JSON으로 임의로 바꿔버리면 무한 루프에 빠진다. Controller은 엔티티를 절대 반환하면 안됀다. 엔티티가 중간에 변경되면 API 스펙이 변하게 된다. 엔티티 반환 시 단순하게 값만 있는 DTO로 변환 후 반환한다.

- #### 단방향 매핑으로 이미 연관관계는 매핑된 것이다.

- #### 양방향 매핑은 반대 방향으로 조회 기능만 추가되는 것 뿐이다.

- #### 처음 개발 할 때에는 단방향으로 생성한다.

- #### JPQL에서 역방향으로 탐색할 일이 많아질 경우 양방향 매핑을 생성한다.

- #### 비즈니스 로직을 기준으로 연관관계의 주인을 선택하면 안돼고 연관관계의 주인은 반드시 외래키의 위치를 기준으로 정해야 한다. 주인이 아닌 쪽에 객체를 설정할 일이 있다면 연관관계 편의 메소드를 사용한다.



# 다양한 연관관계 매핑



## N : 1



![N 대 1 단방향](/media/mwkang/Klevv/Spring 일지/ORM/11.14/N 대 1 단방향.png)

![N 대 1 양방향](/media/mwkang/Klevv/Spring 일지/ORM/11.14/N 대 1 양방향.png)

- #### N : 1 은 가장 많이 사용하는 연관관계 구조이다.

- #### 양방향 연관관계 시 외래키가 있는 곳을 주인으로 한다.



## 1 : N 



![1 대 N 단방향](/media/mwkang/Klevv/Spring 일지/ORM/11.14/1 대 N 단방향.png)

![1 대 N 양방향](/media/mwkang/Klevv/Spring 일지/ORM/11.14/1 대 N 양방향.png)

- #### 1 : N 연관관계에서 외래키가 상대 엔티티에 있어도 1이 연관관계 주인이 된다.

- #### 테이블의 1 : N 관계는 항상 N 엔티티에 외래키가 있다.

- #### 객체와 테이블의 차이 때문에 반대편 테이블의 외래키를 관리하는 특이한 구조로 억지로 맞추는 듯한 느낌이 발생한다.

- #### @JoinColumn 어노테이션을 반드시 사용해야 한다. @JoinColumn이 없다면 @JoinTable 방식으로 중간에 테이블 하나를 추가해야 한다. 즉, @JoinTable의 default로 주인엔티티_상대엔티티 테이블을 생성하여 외래키를 받는 구조로 생성한다.

- #### MappedBy는 사용하지 않는다.

- #### 위의 구조에서는 member 리스트를 업데이트하면 Member테이블의 TEAM_ID(외래키)를 업데이트 하는 것이다.

- #### 만약 테이블에서 N 엔티티에 외래키가 없고 1 엔티티에 외래키가 있으면 insert 시 memberID와 중복 가능성이 있다.

- #### 1 : N 연관관계는 외래키가 다른 테이블에 있으므로 추가로 UPDATE SQL을 실행해야하고 상대의 값을 업데이트  하기 때문에 유지보수가 매우 좋지 않다. 즉, 1 : N보다 N : 1 연관관계를 사용하는게 유지보수에 좋다.

- #### 1 : N 양방향 매핑은 공식적으로 존재하지 않는다. 임의로 설정하는 구조이다.

- #### 1 : N 양방향 매핑 사용 시 @JoinColumn(insertable = false, updatable = false)를 사용하여 읽기 전용으로 조회만 가능하게 만든다.

  - #### Insertable = false : insert가 불가하게 하는 것이다.

  - #### Updatable = false : update가 불가하게 하는 것이다.

- #### 즉, 1 : N 양방향 매핑은 읽기 전용 필드를 사용해서 양방향처럼 사용하는 방법이다.



## 1 : 1



![1 대 1 단방향](/media/mwkang/Klevv/Spring 일지/ORM/11.14/1 대 1 단방향.png)

![1 대 1 양방향](/media/mwkang/Klevv/Spring 일지/ORM/11.14/1 대 1 양방향.png)

- #### 1 : 1 관계는 반대도 1 : 1 관계이다.

- #### 주 테이블이나 대상 테이블 중에 아무 곳에나 외래키를 선택해도 되지만 많이 사용되는 주 테이블에 외래키를 설정하는 것이 편하다.

- #### 외래키에 데이터베이스 유니크(UNI) 제약조건을 추가해야 데이터베이스 입장에서 관리가 쉽다.

- #### N : 1 처럼 외래키가 있는 곳이 연관관계 주인이며 반대편은 mappedBy로 매핑한다.

- #### 1 : 1 연관관계에서 외래키 가진 테이블과 객체 엔티티가 다르면 단방향 관계를 JPA가 지원하지 않는다. 양방향 관계는 성립하며 주 테이블을 가질 때의 양방향 연관관계와 동일한 구조이다.

- #### DBA 와 개발자 입장에서 외래키 설정 위치에 따라 입장이 다르다.

  - #### DBA 입장 : Member이 여러개의 Locker을 가질 수 있다면 연관관계가 1 : N이 된다. 그러므로 1 : 1 연관관계를 1 : N으로 수정 시 Locker에 외래키가 있었던 것이 좋다.

  - #### Developer : 성능 측면으로 Member 조회 경우가 많다면 Member 조회로 Locker까지 조회할 수 있기 때문에 Locker 유무 로직 활용 시 효율적이다. 즉, 객체 지향형 입장에서는 Member에 외래키가 있었던 것이 좋다.

- #### 주 테이블에 외래키를 설정할 때의 장점, 단점이 있다.

  - #### 장점 : 주 테이블만 조회해도 대상 테이블에 데이터가 있는지 확인 가능하다.

  - #### 단점 : 값이 없으면 외래키에 NULL을 허용해야 하므로 DBA입장에서 치명적이다.

- #### 대상 테이블에 외래키를 설정할 때의 장점, 단점이 있다.

  - #### 장점 : 주 테이블과 대상 테이블을 1 : 1 관계에서 1 : N 관계로 변경 시 테이블 구조를 유지하기 쉽다.

  - #### 단점 : 프록시 기능의 한계로 지연 로딩으로 설정해도 항상 즉시 로딩된다. 원래 NULL이면 NULL저장하고 아니면 포록시를 넣지만 이미 쿼리를 전송하여 즉시 로딩으로 설정되어 지연 로딩을 쓸모 없어진다. 그리고 반드시 1 : 1 양방향 관계로 생성해야 한다. Developer 입장에서 신경쓸 사항이 많다.



## N : M



![N 대 M 연관관계 객체](/media/mwkang/Klevv/Spring 일지/ORM/11.14/N 대 M 연관관계 객체.png)

![N 대 M 연관관계 테이블](/media/mwkang/Klevv/Spring 일지/ORM/11.14/N 대 M 연관관계 테이블.png)

![N 대 M to 1 대  N, N 대 1 객체](/media/mwkang/Klevv/Spring 일지/ORM/11.14/N 대 M to 1 대  N, N 대 1 객체.png)

![N 대 M to 1 대 N, N 대 1 테이블](/media/mwkang/Klevv/Spring 일지/ORM/11.14/N 대 M to 1 대 N, N 대 1 테이블.png)

```java
@ManyToMany
@JoinTable(name = "MEMBER_PRODUCT")
private List<Product> products = new ArrayList<>();
```

- #### 관계형 데이터베이스는 정규화된 테이블 2개로 N : M 관계를 표현할 수 없어서 연결 테이블을 추가하여 1 : N, N : 1 관계로 풀어내야 한다.

- #### 객체는 컬렉션을 사용해서 객체 2개로 N : M 관계를 매핑하는 것이 가능하다.

- #### @ManyToMany 어노테이션을 설정 후 @JoinTable로 연결 테이블을 지정하여 단방향, 양방향 관계를 설정해줄 수 있다.

- #### 하지만 연결 테이블에 다른 속성을 추가할 수 없는 단점이 있다. 즉, 연결 테이블에 매핑 정보인 외래키만 들어가고 추가로 다른 정보는 들어갈 수 없어서 사용할 때 힘들다.

- #### N : M 관계는 실무에서 절대 사용하면 안돼는 관계 구조이다.

- #### N : M 관계를 구현해야 한다면 연결 테이블용 엔티티를 추가하는 것이 좋다. 즉, 클래스를 하나 더 만들어서 양쪽의 외래키를 설정한 후 추가 정보를 설정할 수 있다. JPA에서 2개 기본키를 묶서엇 만들면 composite ID가 필요하므로 대체키로 의미없는 기본키를 만들어서 관리하는 것이 좋다.



## @JoinColumn & @ManyToOne & @OnetoMany



- #### @JoinColumn은 외래키를 매핑할 때 사용하며 여러 속성이 존재한다.

  - #### Name : 매핑할 외래키 이름을 설정할 수 있다. Default 값은 필드명 + _ + 참조하는 테이블의 기본키 컬럼명 이다.

  - #### ReferencedColumnName : 외래키가 참조하는 대상 테이블의 컬럼명을 설정할 수 있다. Default 값은 참조하는 테이블의 기본키 컬럼명이다.

  - #### ForeignKey(DDL) : 외래키 제약조건을 직접 지정할 수 있다. 이 속성은 테이블을 생성할 때만 사용한다.

  - #### Unique, nullable, insertable, updatable, columnDefinition, table : @Column의 속성과 같다.

- #### @ManyToOne은 N : 1 관계 매핑 시 이용하며 여러 속성이 존재하고 연관관계 주인이므로 mappedBy 속성이 없다.

  - #### Optional : false로 설정하면 연관된 엔티티가 항상 있어야 한다. Default 값은 TRUE이다.

  - #### Fetch : 글로벌 패치 전략을 설정한다. @Many가 붙은 어노테이션은 Default 값이 FetchType.EAGER이며 @ONE이 붙은 어노테이션은 Default 값이 FetchType.LAZY이다. 즉시 로딩, 지연 로딩을 설정 가능하다.

  - #### Cascade : 영속성 전이 기능을 사용한다.

  - #### TargetEntity : 연관된 엔티티의 타입 정보를 설정한다. 이 기능은 거의 사용하지 않으며 컬렉션을 사용해도 제네릭으로 타입 정보를 알 수 있다.

- #### @OneToMany는 1 : N 관계 매핑 시 이용하며 여러 속성이 존재한다.

  - #### MappedBy : 연관관계의 주인 필드를 선택하여 매핑한다.

  - #### Fetch : 글로벌 패치 전략을 설정한다. @Many가 붙은 어노테이션은 Default 값이 FetchType.EAGER이며 @ONE이 붙은 어노테이션은 Default 값이 FetchType.LAZY이다. 즉시 로딩, 지연 로딩을 설정 가능하다.

  - #### Cascade : 영속성 전이 기능을 사용한다.

  - #### TargetEntity : 연관된 엔티티의 타입 정보를 설정한다. 이 기능은 거의 사용하지 않으며 컬렉션을 사용해도 제네릭으로 타입 정보를 알 수 있다.



Image 출처 : [김영한 ORM 표준 JPA](https://www.inflearn.com/course/ORM-JPA-Basic/dashboard)

