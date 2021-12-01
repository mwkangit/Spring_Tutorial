# Advanced Mapping



## Inheritance Mapping



- #### 관계형 데이터베이스는 상속 관계가 없다.

- #### 슈퍼타입 서브타입 관계라는 모델링 기법이 객체 상속과 유사하다.

- #### 관계형 데이터베이스에는 논리, 물리 모델이 존재한다.

- #### 상속 매핑을 할 때 @DiscriminatorColumn(name = "DTYPE")은 구별자를 넣을 컬럼의 이름을 지정한다. Default 값은 DTYPE이다.

- #### @DiscriminatorValue("XXX")는 각 자식 객체에 대한 이름을 부여하는 것이다. 이름을 부여하여 명확하게 구분하는 것이 좋다. Default 값은 엔티티명이다.

- #### @Inheritance(strategy = InheritanceType.XXX)는 상속 전략을 정하는 것이다. Default 값은 SINGLE_TABLE이다.

- #### 객체 입장에서는 어떠한 전략을 사용해도 변하지 않는다.

- #### 어노테이션만 수정하여 상속 매핑 전략을 선택할 수 있다. JPA의 좋은 전략이다.

![관계형논리모델](https://user-images.githubusercontent.com/79822924/144243601-6d143e76-9367-44a5-96ed-651ef66b235d.png)

![객체상속관계](https://user-images.githubusercontent.com/79822924/144243625-0f397548-fb50-45ea-b8d9-51c32d8fb90b.png)


## Join Strategy



![조인전략객체](https://user-images.githubusercontent.com/79822924/144243680-09e35aed-0333-40f7-90a4-97683eb770e1.png)

![조인전략디비](https://user-images.githubusercontent.com/79822924/144243703-6240457c-5c59-4c83-bd3c-e5087e5e6b6f.png)

- #### 조인 전략은 @Inheritance(strategy = InheritanceType.JOINED)를 사용한다.

- #### 각각 테이블로 변환하여 구성한다.

- #### 조인 전략을 사용하기 전에 데이터베이스에 원하는 형식으로 데이터를 저장하기 위해 h2 데이터베이스 버전을 1.4.200에서 1.4.199로 낮추거나 하이버네이트 버전을 5.3.10에서 5.4.13으로 올려야 한다.

- #### 부모와 자식에 대한 테이블을 데이터를 나눠서 모두 만든 후 조인으로 데이터를 가져온다.

- #### 즉, 위의 그림에서 Album으로 값이 들어오면 name, price는 Item테이블로 들어가며 artist는 Album 테이블로 들어간다.

- #### 조인 전략을 전택하면 이와 같이 Insert 쿼리가 2번 나가게 된다.

- #### 기본키 값은 동일하게 부여되어 사용되어 구분가능하다.

- #### 사실 조인 전략에서는 자식 테이블 모두 쿼리로 조회하면 값 전체가 나오므로 @DiscriminatorColumn 없어도 된다.

- #### 조인 전략에는 장점이 있다.

  - #### 테이블이 정규화된 모델이다.

  - #### 외래키 참조 무결성 제약조건이 활용가능하다. 예를 들어, 주문 테이블에서 Item들을 조회하고 싶을 때 Item 테이블만 접근하면 된다. 또한, 가격 정산 시 Item 테이블만 접근하면 된다.

  - #### 저장공간을 효율적으로 활용할 수 있다.

- #### 조인 전략에는 단점이 있다.

  - #### 조회 시 조인 쿼리를 많이 사용하여 성능 저하가 일어날 수 있다.

  - #### 조회 쿼리가 복잡하게 이루어진다.

  - #### 데이터 저장시 Insert 쿼리가 2번 호출된다.



## Single Table Strategy



![단일테이블전략객체](https://user-images.githubusercontent.com/79822924/144243746-a33b0f0a-0b91-4f5e-9a95-a7427c9fca8a.png)

![단일테이블전략디비](https://user-images.githubusercontent.com/79822924/144243776-4fc6f5d8-3fbe-466c-9563-a38e78158391.png)

- #### 단일 테이블 전략은 @Inheritance(strategy = InheritanceType.SINGLE_TABLE)을 사용한다.

- #### 통합 테이블로 변환하여 구성한다.

- #### 논리 모델을 하나의 테이블로 합친 것이다.

- #### @DiscriminatorColumn이 반드시 필요하다.

- #### DTYPE으로 각 엔티티를 구분한다.

- #### Insert 및 조회 쿼리가 1번 발생해서 성능이 좋고 단순하다.

- #### 단일 테이블 전략에는 장점이 있다.

  - #### 조인이 필요 없으므로 일반적으로 조회 성능이 빠르다.

  - #### 조회 쿼리가 단순하다.

- #### 단일 테이블 전략에는 단점이 있다.

  - #### 자식 엔티티가 매핑한 컬럼은 모두 null을 허용한다. 즉, 데이터 무결성 입장에서 애매하다.

  - #### 단일 테이블에 모든 것을 저장하므로 테이블이 커질 수 있다. 상황에 따라서 조회 성능이 오히려 느려질 수 있다.



## Table Per Class Strategy



![클래스마다테이블객체](https://user-images.githubusercontent.com/79822924/144243831-ec3cd8ef-6dbd-4259-926c-4e9d4c4e9c4a.png)

![클래스마다테이블디비](https://user-images.githubusercontent.com/79822924/144243804-850aef61-210b-4fa1-b283-d3b376190d21.png)

- #### 구현 클래스마다 테이블 전략은 @Inheritance(strategy = InheritanceType.SINGLE_TABLE)을 사용한다.

- #### 서브타입 테이블로 변환하여 구성한다.

- #### 부모는 class가 아닌 abstract class로 생성한다.

- #### 부모 테이블은 생성하지 않고 자식 테이블만 따로 생성한다.

- #### 부모 테이블의 속성이 모두 자식 테이블로 들어간다.

- #### Insert 쿼리 1개 생성된다.

- #### 각 테이블에 대한 코드, 쿼리가 필요하다. 즉, 새로운 타입이 추가될 때 어렵다.

- #### 이 전략은 DBA와 ORM 전문가 둘다 추천하지 않는다.

- #### 구현 클래스마다 테이블 전략은 장점이 있다.

  - #### 서브 타입을 명확하게 구분해서 처리할 때 효과적이다.

  - #### not null 제약조건을 사용 가능하다.

- #### 구현 클래스마다 테이블 전략은 단점이 있다.

  - #### 여러 자식 테이블을 함께 조회할 때 성능이 느리다. 즉, 기본키 값으로 원하는 데이터를 찾을 때 UNION SQL로 모든 자식 테이블을 접근해야 한다.

  - #### 자식 테이블을 통합해서 쿼리하기 어렵다.



## @MappedSuperclass



![MappedSuperclass1](https://user-images.githubusercontent.com/79822924/144243861-d6d65966-13b2-44a5-abaa-0ab4ded2261d.png)

![MappedSuperclass2](https://user-images.githubusercontent.com/79822924/144243881-7de6c108-e5bb-4e98-8039-432fa294b3c1.png)

```java
@MappedSuperclass
public abstract class BaseEntity {

    // 만든 사람, 만든 일시, 수정한 사람, 수정한 일시에 대한 속성이 Member, Team에 공통으로 있다고 할 때 부모 클래스 사용한다.
    @Column(name = "INSERT_MEMBER")
    private String createdBy;
    private LocalDateTime createdDate;
    @Column(name = "UPDATE_MEMBER")
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
}
```



- #### 상속관계 매핑과 관련없다.

- #### 엔티티가 아니므로 테이블과 매핑하지 않는다.

- #### 공통 속성을 묶어서 사용한다.

- #### 매핑 정보만 받는 부모클래스, 슈퍼클래스이다.

- #### 부모 클래스를 상속 받는 자식 클래스에 매핑 정보만 제공한다.

- #### 조회, 검색 불가하다. 즉, 위 코드에서 em.find(BaseEntity) 불가하다.

- #### 직접 생성해서 사용할 일이 없으므로 추상 클래스 권장한다. 즉, abstract class를 사용한다.

- #### 테이블과 관계 없고, 단순히 엔티티가 공통으로 사용하는 매핑정보를 모으는 역할을 한다.

- #### 주로 등록일, 수정일, 등록자, 수정자 같은 전체 엔티티에서 공통으로 적용하는 정보를 모을 때 사용한다.

- #### 참고로 @Entity 클래스는 엔티티나 @MappedSuperclass로 지정한 클래스만 상속 가능하다.

- #### JPA에서 날짜 데이터에 대한 것은 자동으로 받아올 수 있다. 즉, 코드로 적어주지 않아도 된다. 예를 들어, Admin의 로그인 된 세션 정보를 읽어와서 이름을 넣어줄 수 있다. 즉, JPA의 이벤트 기능으로 해결하거나 어노테이션으로 해결할 수 있다.

- #### 실무에서 처음에 상속으로 객체 지향적으로 설계 후 문제 예상 후 다른 것으로 바꾸는 방식을 취한다.

- #### 위 사진처럼 Item만 단독으로 테이블에 저장할 일 있으면 class 혹은 abstract class를 사용한다.

- #### 상속은 데이터가 적을 때에는 잘 작동하지만 데이터가 억 단위, 파티셔닝 때에는 복잡하다. 테이블을 단순하게 운영해야 한다.



# Managing Proxy & Association



## Proxy



```java
public void printUser(String memberId) {
Member member = em.find(Member.class, memberId);
Team team = member.getTeam();
System.out.println("회원 이름: " + member.getUsername());
}
```

- #### Member 엔티티만 사용할 경우 연관된 Team에 대한 엔티티도 쿼리로 가져오면 낭비가 일어난다.

- #### Find() 메소드는 연관된 엔티티까지 조회하는 기능을 한다.



```java
Member member = em.getReference(Member.class, memberId);
member.getUsername();
```

![proxy](https://user-images.githubusercontent.com/79822924/144243929-64f4cb21-2d05-435e-b200-086e1fb44c0b.png)

- #### GetReference()는 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체를 조회한다.

- #### 프록시는 실제 클래스를 상속 받아서 만들어진다. Hibernate가 지원해준다.

- #### 실제 클래스와 겉 모양이 같으며 사용자 입장에서 진짜와 프록시를 구분하지 않고 사용할 수 있다.

- #### 프록시 객체는 실제 객체에 대한 참조를 보관하여 사용시 참조한다. 즉, 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메소드를 호출한다.

- #### 위 사진과 같이 프록시 객체가 참조가 이루어지지 않았을 경우 우선 영속성 컨텍스트를 확인한다. 영속성 컨텍스트에 원하는 엔티티가 없다면 데이터베이스에 엔티티를 요청한 후 영속성 컨텍스트에 저장하여 참조를 시작한다. 한번 프록시의 target으로 지정되면 같은 정보에 대한 쿼리를 날리지 않는다.

- #### 프록시 객체는 처음 사용할 때 한 번만 초기화하며 프록시 객체는 초기화 해도 실제 엔티티로 바뀌는 것이 아닌 참조만 하여 실제 엔티티에 접근하기만 하는 것이다. 즉, 한번 프록시 객체는 계속 hibernate에서 관리하는 프록시 객체이다.

- #### 프록시 객체는 원본 엔티티를 상속받으므로 타입 체크시 주의해야 한다. 실제값 비교 비즈니스 메소드를 만든 후 프록시 객체와 실제 객체를 비교할 때 ==를 사용하면 서로 class 주소가 다르므로 false가 발생한다. 즉, ==대신 instanceOf()를 사용하여 인스턴스로 비교해야 true가 발생한다.

- #### JPA는 한 트랜잭션에서 실제 엔티티나 프록시 객체가 같은 객체를 이용하면 == 시 무조건 true를 반환한다. 즉, JPA의 기본 메커니즘은 한 트랜잭션에서 같은 객체는 같은 참조를 가진다고 한다.

- #### 프록시 객체를 먼저 생성 후 find()로 동일한 객체를 생성하면 이후에 생성된 객체들은 모두 첫 프록시 객체와 같은 값을 가진다. 즉, 먼저 만들어진 객체의 타입을 따라가게 된다. 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 그 엔티티를 따라간다는 뜻이다.

- #### 아직 target이 정해지지 않은 프록시 객체를 detach()하여 준영속 상태로 만들면 쿼리를 날리짐 못하므로 target 지정하는 것이 불가능 해져서 org.hibernate.LazyInitializationException 예외가 발생한다.

- #### PersistenceUnitUtil.isLoaded(Object entity)로 프록시 인스턴스의 초기화 여부를 확인 할 수 있다.

- #### Entity.getClass().getUsername()을 출력했을 때 javasist나 HibernateProxy가 나오면 프록시 객체이다(예전 spring에서는 CGLIB가 프록시 객체를 뜻했다).

- #### JPA 표준에는 강제 초기화가 없다.



## EAGER, LAZY



```java
@Entity
public class Member {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name = "USERNAME")
	private String name;
	
	@ManyToOne( fetch = FetchType.LAZY )
	@JoinColumn(name = "TEAM_ID")
	private Team team;
}
```

![Lazy](https://user-images.githubusercontent.com/79822924/144243967-9ce8a871-cefe-428b-8cb7-cedba8e2807f.png)

- #### FetchType.Lazy는 지연로딩으로 엔티티를 프록시로 조회를 한다는 뜻이다. 즉, 위 코드에서 find() 시 Member 엔티티만 조회하며 Team 엔티티는 프록시로 생성해 놓는 것이다.

- #### 실제 Team을 사용하는 시점에 최기화하여 데이터베이스에서 조회한다. 즉, Team 엔티티는 이미 프록시로 만들어져 있으며 프록시 객체를 사용하여 메소드를 호출하거나 변수를 사용할 때 쿼리가 전송된다.



```java
@Entity
public class Member {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name = "USERNAME")
	private String name;
	
	@ManyToOne( fetch = FetchType.EAGER )
	@JoinColumn(name = "TEAM_ID")
	private Team team;
}
```

![EAGER](https://user-images.githubusercontent.com/79822924/144243985-572b3f6b-203e-4fcb-8292-dfccb1993a2e.png)

- #### FetchType.EAGER은 즉시로딩으로 연관된 엔티티를 모두 함께 조회한다는 뜻이다. 즉, 위 코드에서 find() 시 Member 엔티티와 Team 엔티티 모두 쿼리가 전송되어 조회된다.



- #### 가급적 지연 로딩만 사용하는 것이 좋다. 즉시로딩으로 하면 DBA에 너무 많은 로직(조인)이 발생할 수 있다.

- #### 즉시로딩은 JQPL에서 N+1 문제를 일으킨다. 즉, 원하는 엔티티를 조회한 후 연관된 엔티티에 대한 객체를 하나하나 쿼리를 전송하여 가져온다. 1은 처음 조회하고자 하는 쿼리이며 N은 연관되어 전송된는 추가 쿼리이다.

- #### 즉, 즉시로딩은 상상하지 못한 쿼리가 나가므로 지연로딩으로 모든 관계를 설정하는 것이 실무에 좋다.

- #### @ManyToOne, @OneToMany는 default로 LAZY가 설정되어 있다.

- #### @OneToMany, @ManyToMany는 default로 EAGER가 설정되어 있다.



## CASCADE



```java
@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
```

![cascade](https://user-images.githubusercontent.com/79822924/144244014-9fb3dddb-116f-4c82-93c3-3d4b7668cc13.png)

- #### Cascade는 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고 싶을 때 사용한다.

- #### 즉, cascade로 된 엔티티 객체 내부에 연관된 다른 엔티티 객체 모두 영속성 컨텍스트로 저장할 수 있다. Persist() 시 모두 한꺼번에 영속성 컨텍스트로 저장할 수 있다.

- #### Cascade(영속성 전이)는 연관관계 매핑과는 관련이 없으며 한번에 영속화하는 편리함만 제공할 뿐이다.

- #### Cascade는 하나의 부모가 자식들을 관리할 때 의미가 있다. 예를 들어, 첨부 파일은 하나의 게시물에서만 관리하므로 cascade에 어울린다. 하지만 파일을 여러 엔티티에서 관리하면 cascade를 사용하면 안됀다.

- #### Cascade에는 여러 종류가 있다.

  - #### ALL : 모두 적용하는 것으로 lifecycle 다 맞춘 공동체 연관일 때 사용한다.

  - #### PERSIST : 저장할 때에만 적용한다.

  - #### REMOVE : 삭제할 때에만 적용한다.

  - #### MERGE : 병합할 때에만 적용한다.

  - #### REFRESH : REFRESH할 때에만 적용한다.

  - #### DETACH : DETACH할 때에만 적용한다.



## Orphan



```java
@OneToMany(mappedBy = "parent", orphanRemoval = true)
```

- #### 고아 객체는 부모에서 자식 객체를 관리한다는 뜻이다.

- #### Cascade와 차이점은 부모에서 자식만 삭제하는 것이 가능하다는 것이다.

- #### 부모가 지워지면 자식이 고아가 되므로 자식도 모두 삭제한다.



```java
Parent parent1 = em.find(Parent.class, id);
parent1.getChildren().remove(0);
```

- #### 부모 list에서 child를 삭제하면 그 child는 데이터베이스에서도 삭제된다. 즉, 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 판단하여 삭제하는 것이다.

- #### 고아 객체 선언은 참조하는 곳이 하나일 때 사용해야 한다.

- #### 고아 객체 선언은 엔티티가 개인 소유할 때 사용해야 한다.

- #### @OneToOne, @OneToMany일 경우에만 사용 가능하다.

- #### 부모가 사라지면 자식도 사라지는 것은 CascadeType.REMOVE와 비슷하게 작동한다.



## Cascade + Orphan



```java
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
```

- #### 부모 엔티티의 경우 스스로 생명주기를 관리하여 persist()로 영속화 remove로 제거할 수 있다. 즉, lifecycle을 엔티티 매니저를 통해 관리하는 것이다.

- #### 자식 엔티티의 경우 cascade, orphan 옵션이 모두 활성화 되면 부모 엔티티를 통해서 lifecycle을 관리할 수 있다.

- #### 도메인 주도 설계(DDD)의 Aggregate Root 개념을 구현할 때 유용하다. Repository는 Aggregate Root로 컨택하고 나머지 비즈니스는 repository로 만들지 않는다. 즉, Aggregate Root를 통해서 모든 lifecycle을 관리하는 것이다.



# Value Type



## Basic Value Type

- #### 값 타입은 3가지로 분류할 수 있다.

  - #### 기본값 타입 : 자바 기본 타입(int, double, ...), 래퍼 클래스(Integer, Long, ...), String 등이 있다.

  - #### 임베디드 타입 : embedded type, 복합 값 타입으로 좌표 등을 표현할 때 유용하다.

  - #### 컬렉션 값 타입 : collection value type으로 collection에 기본값 타입을 넣는다.

- #### 기본값 타입은 생명주기를 엔티티에 의존하며 공유하면 side effect가 많아서 공유하면 안됀다.

- #### 자바의 기본 타입은 절대 공유하면 안됀다.

- ####  Int, double 같은 기본 타입은 값 타입시 독립이므로 안전하며 절대 공유하면 안됀다.

- #### 기본 타입은 항상 깊은 복사를 한다.

- #### Integer 같은 래퍼 클래스나 String 같은 특수한 클래스는 공유 가능한 객체이지만 변경하면 안됀다. String 클래스는 깊은 복사가 되는 것처럼 보이지만 새로운 메모리를 할당하여 새 객체를 생성한다. 하지만 참조를 하는 것이지만 변경 자체가 일어날 수 없으므로 안전하다.

- #### 참고로 클래스는 모두 얕은 복사가 일어난다.



## Embedded Type



- #### 임베디드 타입은 새로운 값 타입을 직접 정의할 수 있다.

- #### 내장 타입이며 엔티티가 아닌 값 타입이다.

- #### 주로 추적 불가한 기본 값 타입을 모아서 만들어서 복합 값 타입이라고도 부른다.



![embedded전](https://user-images.githubusercontent.com/79822924/144244083-9dc5d81b-60ec-488f-9277-502a39cd70ca.png)

![embedded후](https://user-images.githubusercontent.com/79822924/144244099-4cd6ab69-e0cc-45c6-b4a4-1c49543bc81a.png)

- #### 임베디드 타입은 값 타입을 묶어서 엔티티에 전달할 수 있다.

- #### @Embeddable은 값 타입을 정의하는 곳에 표시하여 사용할 수 있다.

- #### @Embedded는 값 타입을 사용하는 곳에 표시하여 사용할 수 있다.

- #### @Embeddable이 붙은 값 타입 객체는 기본 생성자가 필수이다.

- #### 임베디드 타입에는 장점들이 있다.

  - #### 시스템 전체에서 재사용이 가능하다.

  - #### 클래스 내부에서 응집도가 높다.

  - #### 해당 값 타입만 사용하는 의미있는 메소드를 만들 수 있다.

  - #### 임베디드 타입을 포함한 모든 값 타입은 값 타입을 소유한 엔티티에 생명주기를 의존한다. 즉, 엔티티가 삭제되면 같이 삭제되고 엔티티가 생성될 때 값이 들어와서 생성된다.

- #### 값 타입은 공유 시 데이터를 수정하면 공유 중인 다른 엔티티에도 영향을 미친다. 서로 영향을 미치면 안돼므로 복사본을 만든 후 각 엔티티에 따로 설정해야 한다.

- #### 임베디드 타입은 엔티티의 값일 뿐이므로 사용하기 전과 후에 매핑하는 테이블은 동일하다.

- #### 임베디드 타입으로 객체와 테이블을 아주 세밀하게(fine-grained) 매핑하는 것이 가능하다. 예를 들어, 근무 시작, 근무 끝을 근무기간으로 만들 수 있다.

- #### 임베디드 타입으로 용어, 코드를 공통화하여 잘 설계한 ORM 어플리케이션을 만들 수 있다. 잘 설계한 ORM 어플리케이션은 매핑한 테이블의 수보다 클래스의 수가 더 많다.

- #### 임베디드 타입은 임베디드 타입을 값 타입으로 저장할 수 있다.

- #### 임베디드 타입의 값이 null이면 매핑한 컬럼 값은 모두 null이다.

- #### 임베디드는 테이블을 생성하지 않으며 비즈니스 메소드를 임베디드 클래스 내에 생성할 수 있다.

- #### UML에서 임베디드 클래스처럼 따로 생성되어 있는 것을 StereoType 이라고 부른다.



```java
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city",
            column = @Column(name = "WORK_CITY")),
            @AttributeOverride(name = "street",
            column = @Column(name = "WORK_STREET")),
            @AttributeOverride(name = "zipcode",
            column = @Column(name = "WORK_ZIPCODE"))
    })
    private Address workAddress;
```

- #### 한 엔티티에서 같은 값 타입을 중복하여 사용할 때 컬럼명이 중복되어 오류가 난다.

- #### @AttributeOverrides, @AttributeOverride를 사용하여 컬럼명 속성을 재정의하여 중복하여 값 타입을 사용할 수 있다.



## Immutable Object



![불변객체 오류](https://user-images.githubusercontent.com/79822924/144244149-bb707f2e-4f24-454f-954f-e7db55d8c5f0.png)

- #### 같은 주소를 참조할 때 값을 바꾸면 양쪽 엔티티에 적용되는 부작용이 발생한다.

- #### 이러한 side effect 버그는 잡아내기 힘들다.

- #### 만약 공유가 목적이었다면 값 타입이 아닌 엔티티로 생성하여 사용해야 한다.



```java
Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());
Member member2 = new Member();
member2.setHomeAddress(copyAddress);
```

- #### 값 타입을 따로 저장하여 사용하고 싶으면 복사본을 만든 후 주입해야 한다.

- #### 임베디드 타입은 객체 타입이므로 복사 시 참조 값을 공유하게 된다.

- #### 참조 공유를 방지하기 위해 값 타입은 불변 객체로 설계해야 한다.

- #### 생성 시점 이후 절대 값을 변경할 수 없는 객체를 만들기 위해 생성자로만 값을 설정하고 수정자를 생성하지 않는다.

- #### Integer, String은 자바가 제공하는 대표적인 불변 객체이다.



```java
Address newAddress = new Address("NewCity", address.getStreet(), address.getZipcode());
member1.setHomeAddress(newAddress);
```

- #### 불변 객체의 값을 바꾸고 싶다면 통으로 새로운 객체를 생성 후 저장해야 한다.



```java
int a = 10;
int b = 10;
// a == b true

Address c = new Address("서울시");
Address d = new Address("서울시");
// c == d false
// c.equals(d) true // equals와 hashcode를 override 해야 한다
```

- #### 값 타임은 인스턴스가 달라도 그 안에 값이 같으면 같은 것으로 봐야 한다.

- #### 동일성(identity) 비교란 인스턴스의 참조 값을 비교하는 것으로 ==를 사용한다.

- #### 동등성(equivalence) 비교란 인스턴스의 값을 비교하는 것으로 equals()를 사용한다.

- #### 값 타입은 동등성 비교를 이용하여 비교한다. Equals() 메소드를 적절하게 재정의 하여 사용한다. Equals()는 default로 ==비교를 하므로 override하여 기준을 바꿔야 한다. Equals()를 구현하면 hashcode도 그것에 맞게 구현해야 한다. Hashcode를 구현하면 hashmap, collection을 효율적으로 이용 가능하다.



## Value Type Collection



![값타입컬렉션1](https://user-images.githubusercontent.com/79822924/144244207-7cdc9123-be4d-4df3-86e6-f06758d38d74.png)

![값타입컬렉션2](https://user-images.githubusercontent.com/79822924/144244225-eef766cd-da21-40e9-bd79-dc397cf5589a.png)

- #### 값 타입 컬렉션은 값 타입을 컬렉션에 담아서 사용하는 것이다.

- #### 값 타입으로 묶어서 모든 속성을 기본키로 활용할 수 있다.

- #### 값 타입 컬렉션은 값 타입을 하나 이상 저장할 때 사용한다.

- #### 데이터베이스는 컬렉션을 같은 테이블에 저장할 수 없으므로 컬렉션을 저장하기 위한 별도의 테이블을 생성한다. 별도의 테이블을 만든 후 소속된 엔티티에 1 : N 연관관계를 매핑한다.



```java
	@ElementCollection
    @CollectionTable(name = "FAVORITE_FOOD", joinColumns =
    @JoinColumn(name = "MEMBER_ID")
    )
    @Column(name = "FOOD_NAME") // 값이 하나이고 정의한 것이 아니므로 컬럼명을 지정한다.
    private Set<String> favoriteFoods = new HashSet<>();

    // Column명을 embedded 타입 그냥 사용하면 된다.
//    @ElementCollection
//    @CollectionTable(name = "ADDRESS", joinColumns =
//    @JoinColumn(name = "MEMBER_ID")
//    )
//    private List<Address> addressHistory = new ArrayList<>();

//     값 타입 컬렉션 대신 1 : N 관계 엔티티를 사용한다.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MEMBER_ID")
    private List<AddressEntity> addressHistory = new ArrayList<>();
```

- #### @ElementCollection은 값 타입 컬렉션의 매핑을 알리는 어노테이션이다.

- #### @CollectionTable은 값 타입 컬렉션을 위한 테이블을 생성하는 것이다. @JoinColumn을 사용하여 소속된 엔티티의 기본기를 외래키로 매핑할 수 있다.

- #### 값 타입 테이블은 컬렉션 수만큼 레코드가 저장되어 소속된 엔티티의 기본키가 연속하여 사용되고 리스트의 길이만큼 레코드가 저장된다.

- #### 값 타입 컬렉션은 소속된 엔티티가 persist()하면 자동으로 함께 영속성으로 관리된다. 즉, 소속된 엔티티의 생명주기에 의존하는 것이다.

- #### 값 타입 컬렉션은 모두 지연 로딩을 지원한다.

- #### 값 타입 컬렉션은 영속성 전이(Cascade)와 고아 객체 제거 기능을 필수로 가진다.



```java
findMember.getAddressHistory().remove(new Address("old1", "street", "10000"));
findMember.getAddressHistory().add(new Address("newCity1", "street", "10000"));
```

- #### 클래스로 된 값 타입 컬렉션 수정 시 remove 할 때 객체가 equals()로 비교하기 때문에 위와 같은 로직으로 수행해야 한다. 물론 객체 엔티티에 equals(), hashcode를 override 해야 한다.

- #### 값 타입은 엔티티와 다르게 식별자 개념이 없어서 값 변경 시 추적이 불가하다.

- #### 값 타입 컬렉션에 변경 사항이 발생하면 주인 엔티티와 연관된 모든 데이터를 삭제하고 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다. 즉, 초기화 후 변경된 내용을 다시 저장하는 것으로 이런 로직은 사용하면 안됀다.

- #### 값 타입 컬렉션을 매핑하는 테이블은 각 컬럼에 기본키를 할 속성이 없으므로 모든 값을 묶어서 기본키로 사용해야 한다. 즉, null 입력하면 안돼며 중복 저장도 허용해서는 안됀다.



```java
@Entity
@Table(name = "ADDRESS")
public class AddressEntity {

    public AddressEntity() {
    }

    public AddressEntity(String city, String street, String zipcode)
    {
        this.address = new Address(city, street, zipcode);
    }

    public AddressEntity(Address address){
        this.address = address;
    }

    @Id
    @GeneratedValue
    private Long id;

    private Address address;
}
```



- #### 대안으로 실무에서는 상황에 따라 값 타입 컬렉션 대신 1 : N 관계를 고려하여 사용한다. 즉, 엔티티로 래핑하여 값 타입으로 승급하는 것이다. 고유의 기본키를 사용한다.

- #### 1 : N 관계를 위한 엔티티를 만들고 엔티티에 값 타입을 적용하는 것이다.

- #### 엔티티를 매핑하여 영속성 전이(Cascade)와 고아객체 제거를 사용하여 값 타입 컬렉션처럼 사용할 수 있다.

- #### 값 타입 컬렉션은 매우 단순할 때 이용해야 한다. 예를 들어, select box 에 치킨, 피자가 있고 둘 다 고를 수 있을 때 사용한다.

- #### 식별자가 필요하고 지속해서 값을 추적하고 변경해야 한다면 그것은 값 타입이 아닌 엔티티로 관리해야 한다.





# Additional Contents



- #### 연관관계 매핑 시 컬럼명을 @Column(name = "PARENT")로 설정하지 않아도 @JoinColumn(name = "parent_id")로 자동으로 매핑 가능하다.

- #### Equals(), hashcode 만들 때 getters를 허용해야 한다. 프록시로 equals()를 실행할 때 getter을 통해 쿼리를 전송하고 데이터를 받아온다.



Image 출처 : [김영한 ORM 표준 JPA](https://www.inflearn.com/course/ORM-JPA-Basic/dashboard)

