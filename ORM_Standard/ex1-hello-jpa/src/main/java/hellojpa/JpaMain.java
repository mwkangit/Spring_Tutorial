package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // transaction 가져오기
        tx.begin(); // transaction 실행 시작

        // 오류 발생 위치를 알기 위해 try catch 사용한다. 이게 정석 but 없어도 spring이 알아서 다 해준다
        try {
            // 생성하는 코드
//            Memb er member = new Member();
//            member.setId(2L);
//            member.setName("HelloB");
//            em.persist(member);

            // 수정하는 코드
//            Member findMember = em.find(Member.class, 1L);
//            System.out.println("findMember.id = " + findMember.getId());
//            System.out.println("findMember.name = " + findMember.getName());
//            findMember.setName("HelloJPA"); // commit 안해도 바뀐다. JPA를 통해 엔티티를 가져오면 JPA가 관리하기 시작한다. Transaction 시 다시 확인한 후 수정된 것을 쿼리로 날린다. 즉, 테이블은 가져오는 것이 아니다.

            // 삭제하는 코드
//            Member findMember = em.find(Member.class, 1L);
//            em.remove(findMember);

            // JPQL
//            List<Member> result = em.createQuery("select m from Member as m", Member.class)
//                    .setFirstResult(5)
//                    .setMaxResults(8)
//                    .getResultList();// table을 대상으로 코드를 짠게 아니다. Member객체를 대상으로 쿼리를 만든 것이다. 즉, m은 Member 엔티티를 선택하는 것이다. JPQL은 객체지향 쿼리이며 방언에 맞춰서 각 DB에 맞게 쿼리 날린다.
            // setFirstResult(), setMaxResults()은 페이징 쿼리로 5번부터 8개 가져오라는 뜻이다.
//            for (Member member : result) {
//                System.out.println("member.name = " + member.getName());
//            }

//======
            // 비영속 상태
//            Member member = new Member();
//            member.setId(160L);
//            member.setName("B");

            // 영속 상태
//            System.out.println("=== BEFORE ===");
//            em.persist(member);
//            System.out.println("=== AFTER ===");
//
//            Member findMember = em.find(Member.class, 101L); // 첫 조회이므로 쿼리 날라간다? 이미 영속성이므로 캐시1에서 탐지가능
//            Member findMember2 = em.find(Member.class, 101L); // 두 번째 조회이므로 캐시1에서 찾는다
//
//            System.out.println("findMember.id = " + findMember.getId()); // 쿼리 날리지 않고 캐시1에서 탐지하여 출력한다.
//            System.out.println("findMember.name = " + findMember.getName());
//            System.out.println(findMember == findMember2); // 같은 트랜잭션에 있어서 인스턴스로 인식한다.
//======
            // persistence.xml batch_size는 영속 컨테이너의 버퍼 저장 크기로 그만큼씩 쿼리로 한번에 보낸다. But 실전에 그렇게 한번에 보내는 경우는 없다.
            // 변경 감지(dirty checking)
//            Member member = em.find(Member.class, 150L);
//            member.setName("ZZZZZ");
            // 여기서 persist 안해도 된다. collection처럼 사용 가능하다. 알아서 update 쿼리 나간다.
//======
            // flush는 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송한다 (commit 시점에도 호출되지만 이 경우 강제 전송이다)
//            Member member = new Member(200L, "member200");
//            em.persist(member);
//
//            em.flush(); // insert query 바로 나간다
            // 1차 캐시나 쓰기 지연 저장소에서 삭제되는 것이 아닌 db에 반영해주는 것이다.
            // jpql은 실행 시 바로 쿼리가 날라간다. 영속성 컨테이너에 있는 것을 모두 db에 반영하여 jpql 실패 확률을 줄이는 것이다.
            // em.setFlushMode(FLushModeType.AUTO) 는 커밋이나 쿼리 실행시 플러시하는 것이고 FlushModeType.COMMIT은 커밋 할 때에만 flush가 실행된는 것이다. default auto.
//======
            // detach 준영속 상태
//            em.find(Member.class, 150L);
//            member.setName("AAAAA");
//
//            em.detach(member); // 영속성에서 관리하지 않게되어 update 쿼리가 날라가지 않는다.
//            em.clear(); // 영속성 컨택스트 모두 삭제.
//            em.close(); // 영속성 컨택스트 닫아버린다.
//======
            //  N : 1 연관관계
            // Team과 Member의 연관관계에 대해 배운다.

            // JoinColumn으로 매핑 후 코드
//            Team team = new Team();
//            team.setName("TeamA");
//
//            em.persist(team);
//
//            Member member = new Member();
//            member.setUsername("member1");
//            member.setTeam(team);
//            em.persist(member);

            // 전혀 객체지향스럽지 않고 계속 db에 쿼리로 물어봐야 한다.
//            Member findMember = em.find(Member.class, member.getId());
//            Long findTeamId = findMember.getTeamId();
//            Team findTeam = em.find(Team.class, findTeamId);

//            em.flush();
//            em.clear(); // 이 두 단계 하면 아래에서 쿼리문 나가는 것을 볼 수 있다

//            Member findMember = em.find(Member.class, member.getId());
//            Team findTeam = findMember.getTeam();
//            System.out.println("findTeam.getName() = " + findTeam.getName());
//
//            // 그냥 set으로 외래키 업데이트 가능하다
//            Team teamB = new Team();
//            teamB.setName("TeamB");
//            em.persist(teamB);
//
//            findMember.setTeam(teamB);
//======
            // 양방향 연관관계

            // 주인이 Member이므로 Member.Team에 team을 주입하여 persist하면 db의 Team, Member에 정상적으로 올라간다. Member이 주인이므로 Team.members는 JPA가 사용하지 않는다.
//            Team team = new Team();
//            team.setName("TeamA");
//
//            em.persist(team);
//
//            Member member = new Member();
//            member.setUsername("member1");
////            member.changeTeam(team); // Team 쪽에 값을 추가하는 메소드를 넣어도 된다. 둘 중 한 곳에만 메소드를 생성하는 것이 권장된다.
//            em.persist(member);
//
//            team.addMember(member);
//
////            team.getMembers().add(member); // flush, clear 없으면 members에 아무거도 없으므로 미리 넣어준다. 1차 캐시에서 find() 하기 때문이다. 이 코드를 지우고 주인 객체에 set할때 연관관계를 매핑해도 된다.
//
////            em.flush();
////            em.clear(); // 이거 해줘야 이후에 db에서 깔끔하게 find한 후 sout가능하다. db에서 다시 find하기 전에는 team의 list에 member이 추가되어 있지 않다.
//
//            Member findMember = em.find(Member.class, member.getId());
//            List<Member> members = findMember.getTeam().getMembers(); // members를 가져오는 시점에도 쿼리를 한번 날려서 외래키와 연관된 members에 값 넣어준다. 즉, team.getMember().add(member); 안해도 된다(flush, clear 있는 상태). 하지만 없다면 db에서 받아온게 아니므로 연결관계가 형성되어 있지 않다. 즉, team.getMember().add(member); 해야 한다.
//
//            for (Member m : members) {
//                System.out.println("m = " + m.getUsername());
//            }
//======
//            // 1 : N 연관관계
//            Member member = new Member();
//            member.setUsername("member1");
//
//            em.persist(member);
//
//            Team team = new Team();
//            team.setName("TeamA");
//            // 이 부분부터 애매하다.
//            team.getMembers().add(member);
//
//            em.persist(team);
//======
            // 상속관계 매핑
            // 조인전략에 대한 상속 관계 매핑이다. 즉, 4개의 테이블 만든 후 상속을 통해 각각 Item과 동일한 기본키를 매핑하는지 각 값이 잘 조인 되는지 알아본다.

//            Movie movie = new Movie();
//            movie.setDirector("kang");
//            movie.setActor("eeee");
//            movie.setName("바람과함께사라지다");
//            movie.setPrice(10000);
//
//            em.persist(movie);

//            em.flush();
//            em.clear();
//
//            // 데이터베이스에서 조회할 때 Movie로 inner join 해서 날라오는 것을 알 수 있다.
//            Movie findMovie = em.find(Movie.class, movie.getId());
//======
            // MappedSuperClass 활용

//            Member member = new Member();
//            member.setUsername("user1");
//            member.setCreatedBy("kim");
//            member.setCreatedDate(LocalDateTime.now());
//
//            em.persist(member);
//======
            // Proxy 객체 target 생성 과정
//            Member member = new Member();
//            member.setUsername("hello");
//
//            em.persist(member);
//
//            em.flush();
//            em.clear();

            // find()시 team Id를 outer join하여 엔티티 전체를 가져온다.
//            Member findMember = em.find(Member.class, member.getId());
            // reference()는 속성을 사용하고자 할 때 쿼리를 날려서 엔티티를 가져온다.
//            Member findMember = em.getReference(Member.class, member.getId());
//            System.out.println("findMember id = " + findMember.getId());
//            System.out.println("findMember username = " + findMember.getUsername());
//======
            // Proxy 객체는 target 지정되어도 proxy 객체로 같은 getClass() 아니어서 == 아닌 instanceOf 사용한다.
//            Team team = new Team();
//            team.setName("abc");
//            em.persist(team);
//
//            Member member1 = new Member();
//            member1.setUsername("member1");
//            member1.setTeam(team);
//            em.persist(member1);
//
//            Member member2 = new Member();
//            member2.setUsername("member1");
//            em.persist(member2);
//
//            em.flush();
//            em.clear();
//
//            Member m1 = em.find(Member.class, member1.getId());
////            Member m2 = em.find(Member.class, member1.getId());
//            Member m3 = em.getReference(Member.class, member1.getId());
//            System.out.println(m1.getClass());
//            System.out.println(m3.getClass());
//
//            // getClass()시 같은 Member 엔티티의 객체이므로 true가 나온다.
////            System.out.println("m1 == m2 : " + (m1.getClass() == m2.getClass()));
//
//            // proxy객체는 실제 Class를 참조하는 가짜 객체이므로 같은 엔티티가 아니어서 false가 반환된다.
//            // proxy든지 실제든지 m1 == m3이면 무조건 true로 하는 것이 jpa 기본 메커니즘이다.
//            System.out.println("m1 == m3 : " + (m1.getClass() == m3.getClass())); // true
//            System.out.println("m1 == m3 : " + (m1 == m3)); // true
//======
            // 즉시로딩, 지연로딩
            // fetch LAZY를 선택하여 특정 객체만 불러오는 프록시 객체 생성한다.

            // LAZY일 때에는 프록시 생성하여 지연로딩, EAGER일 때에는 즉시로딩하여 프록시 생성 안하고 연관된 모든 데이터 가져온다.

//            Team team = new Team();
//            team.setName("abc");
//            em.persist(team);
//
//            Team team2 = new Team();
//            team2.setName("def");
//            em.persist(team2);
//
//            Member member1 = new Member();
//            member1.setUsername("member1");
//            member1.setTeam(team);
//            em.persist(member1);
//
//            Member member2 = new Member();
//            member2.setUsername("member2");
//            member2.setTeam(team2);
//            em.persist(member2);
//
//            em.flush();
//            em.clear();

//            Member m1 = em.find(Member.class, member1.getId());

            // JPQL로 쿼리를 생성할 경우 쿼리가 2번 나간다. 쿼리가 Member 한번, Team 한번 나간다. 즉, N+1번 나간다. 쿼리에 따라 Member만 가져온 뒤 연관된 Team도 가져오는 쿼리 요청한다. Member 2개 Team 2개라면 Member을 위한 쿼리 1번, Team 각각 불러오기 위한 쿼리 2번 날라간다. 1은 처음 나가는 원하는 쿼리이며 N은 연관된 추가 쿼리들이다.
            // LAZY로 잡으면 추가 쿼리 없다. 프록시다. N+1 없다.
//            List<Member> members = em.createQuery("select m from Member m", Member.class)
//                    .getResultList();
            // fetch join. Member와 Team 한번에 다 가져왔다.
//            List<Member> members = em.createQuery("select m from Member m join fetch m.team", Member.class)
//                            .getResultList();
//======
            // 영속성 전이 : CASCADE
            // CASCADE는 어떤 엔티티에 연관된 다른 엔티티를 자동으로 persist 하여 영속성 컨테이너에 등록한다.

            // Child persist를 Parent보다 뒤에해도 된다.

//            Child child1 = new Child();
//            Child child2 = new Child();
//
//            Parent parent = new Parent();
//            parent.addChild(child1);
//            parent.addChild(child2);

            // CASCADE 안했을 때에는 이렇게 일일이 해줘야 했다.
//            em.persist(parent);
//            em.persist(child1);
//            em.persist(child2);

            // CASCADE.ALL인 경우 이렇게만 해줘도 Child가 persist 된다. 즉, 그 엔티티에 저장된 엔티티들 모두 영속성 컨텍스트로 저장한다.
//            em.persist(parent);
//======
            // 고아 객체
            // 부모에 orphanRemoval = true 하여 부모 지워지면 자식도 지워지게 한다.
            // 즉, 부모 collection에서 빠지면 지워진다.

//            Child child1 = new Child();
//            Child child2 = new Child();
//
//            Parent parent = new Parent();
//            parent.addChild(child1);
//            parent.addChild(child2);
//
////            em.persist(parent);
//
//            // 부모 삭제하면 자식도 모두 삭제한다
//            em.persist(parent);
//            em.persist(child1);
//            em.persist(child2);
//
//
//            em.flush();
//            em.clear();
//
//            Parent findParent = em.find(Parent.class, parent.getId());
//            // child1 삭제한다.
////            findParent.getChildList().remove(0);
//
//            em.remove(findParent);
//======
            // @Embeddable

//            Member member = new Member();
//            member.setUsername("hello");
//            member.setHomeAddress(new Address("city", "street", "zipcode"));
//            member.setWorkPeriod(new Period());

            // 값 타입 공유 시 데이터 수정하면 공유 중인 다른 엔티티에도 영향을 미친다

//            Address address = new Address("city", "street", "10000");
//
//            Member member1 = new Member();
//            member1.setUsername("member1");
//            member1.setHomeAddress(address);
//            em.persist(member1);
//
//
//            // 참조하여 객체 공유하게 된다
////            Member member2 = new Member();
////            member2.setUsername("member2");
////            member2.setHomeAddress(address);
////            em.persist(member2);
//
//            // 복사본을 만든 뒤 주입하자
//            Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());
//            Member member2 = new Member();
//            member2.setUsername("member2");
//            member2.setHomeAddress(copyAddress);
//            em.persist(member2);
//
//            // 불변 객체 값을 바꾸고 싶다면 통으로 새로 만들어야 한다
//            Address newAddress = new Address("NewCity", address.getStreet(), address.getZipcode());
//            member1.setHomeAddress(newAddress);
//
//            // 첫 번째 멤버만 바꾼다고 생각하고 개발자가 실수하는 케이스이다.
//            member1.getHomeAddress().setCity("newCity");
//======
            // 값 타입 컬렉션
            // @ElementCollection으로 값 타입 컬렉션임을 알리고 @CollectionTable로 외래키를 매핑한다.

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity", "street", "10000"));
            em.persist(member);

            // 여기서부터 컬렉션 챙긴다.
//            member.getFavoriteFoods().add("치킨");
//            member.getFavoriteFoods().add("족발");
//            member.getFavoriteFoods().add("피자");

//            member.getAddressHistory().add(new Address("old1", "street", "10000"));
//            member.getAddressHistory().add(new Address("old2", "street", "10000"));

            // 1 : N 관계로 만든 값 타입 컬렉션
            // 1 : N 은 외래키 주인이 반대편이므로 update쿼리 두번 나간다.
//            member.getAddressHistory().add(new AddressEntity("old1", "street", "10000"));
//            member.getAddressHistory().add(new AddressEntity("old2", "street", "10000"));

            // 값 타입 컬렉션은 persist하지 않고 정의 되어있는 엔티티가 persist되면 자동으로 저장된다.
            // 즉, 생명주기가 Member 엔티티에 소속된 것이다.
//            em.persist(member);
//
            em.flush();
            em.clear();

            // 여기서부터 값 타입 컬렉션 조회를 해본다.
            // 값 타입 컬렉션은 지연로딩을 지원한다
//            Member findMember = em.find(Member.class, member.getId());

            // 이때부터 값 타입 컬렉션에 쿼리 날라간다.
//            List<Address> addressHistory = findMember.getAddressHistory();
//            for (Address address : addressHistory) {
//                System.out.println("address = " + address.getCity());
//            }
//
//            Set<String> favoriteFoods = findMember.getFavoriteFoods();
//            for (String favoriteFood : favoriteFoods) {
//                System.out.println("favoriteFood = " + favoriteFood);
//            }

            // 여기서부터 값 타입 컬렉션 수정을 해본다

            // homeCity -> newCity
            // sideEffect 때문에 값 타입은 immutable(불변 객체) 해야 한다.
            // findMember.getHomeAddress().setCity(); 이거 안됀다.

            // 값 타입은 통으로 바꿔줘야 한다.
            // 이건 값 타입에 대한 내용이다. 전 단계에서도 한 것이다.
//            Address a = findMember.getHomeAddress();
//            findMember.setHomeAddress(new Address("newCity", a.getStreet(), a.getZipcode()));

            // 값 타입 컬렉션 수정
            // FAVORITE_FOOD
            // 치킨 -> 한식
//            findMember.getFavoriteFoods().remove("치킨");
//            findMember.getFavoriteFoods().add("한식");

            // ADDRESSHISTORY
            // old1 -> new1
            // 쿼리에서는 old1을 지우는 것이 아닌 리스트에서 특정 멤버에 대한 기록을 모두 지운 후 다시 하나하나 insert하는 쿼리를 보낸다.
//            findMember.getAddressHistory().remove(new Address("old1", "street", "10000"));
//            findMember.getAddressHistory().add(new Address("newCity1", "street", "10000"));
//======
            // JPQL 기본 알아보기

//            List<Member> result = em.createQuery(
//                    "select m from Member m where m.username like '%kim%'",
//                            Member.class
//                    ).getResultList();
//
//            for (Member member : result) {
//                System.out.println("member = " + member);
//            }

            // Criteria 기본
            // 동적 쿼리 대안

//            CriteriaBuilder cb = em.getCriteriaBuilder();
//            CriteriaQuery<Member> query = cb.createQuery(Member.class);
//
//            Root<Member> m = query.from(Member.class);
//
//            CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "kim"));
//            List<Member> resultList = em.createQuery(cq)
//                    .getResultList();
//======

            tx.commit();
        } catch (Exception e){
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}
