package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

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
            // 1 : N 연관관계
            Member member = new Member();
            member.setUsername("member1");

            em.persist(member);

            Team team = new Team();
            team.setName("TeamA");
            // 이 부분부터 애매하다.
            team.getMembers().add(member);

            em.persist(team);

            tx.commit();
        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
