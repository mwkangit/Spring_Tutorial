package jpql;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // transaction 가져오기
        tx.begin(); // transaction 실행 시작

        // 오류 발생 위치를 알기 위해 try catch 사용한다. 이게 정석 but 없어도 spring이 알아서 다 해준다
        try {
//======
            //  JPQL 기본
//            for(int i = 0 ; i < 100 ; i++){
//                Member member = new Member();
//                member.setUsername("member" + i);
//                member.setAge(i);
//                em.persist(member);
//            }
//
//
//            // TypeQuery 반환 타입이 명확한 경우
//            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
//            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
//
//            // Query 반환 타입이 명확하지 않은 경우
//            Query query3 = em.createQuery("select m.username, m.age from Member  m");
//
//            // getResultList() 결과가 여러개일 경우 리스트로 반환
//            List<Member> resultList = query1.getResultList();
//
//            for (Member member1 : resultList) {
//                System.out.println("member1 = " + member1);
//            }
//
//            // getSingleResult() 결과가 하나일 경우 객체로 반환
//            // 이때 데이터 없을 경우 오류 발생하는데 그것을 방지하기 위해 Spring Data JPA 이용 -> 결과 없으면 null이나 optional 반환한다
////            Member result = query1.getSingleResult();
////            System.out.println("result = " + result);
//
//            // parameter binding
//            // 이름 기준
//            TypedQuery<Member> query4 = em.createQuery("select m from Member m where m.username = :username", Member.class);
//            query4.setParameter("username", "member1");
//            Member singleResult = query4.getSingleResult();
//            System.out.println("singleResult = " + singleResult);
//            // 위치 기준
//            TypedQuery<Member> query6 = em.createQuery("select m from Member m where m.username = ?1", Member.class);
//            query6.setParameter(1, "member1");
//            Member singleResult2 = query6.getSingleResult();
//            System.out.println("singleResult2 = " + singleResult2);
//
//            // 보통 메소트 체인으로 엮어서 사용한다
//            Member singleResult1 = em.createQuery("select m from Member m where m.username = :username", Member.class)
//                    .setParameter("username", "member1")
//                    .getSingleResult();
//            System.out.println("singleResult1 = " + singleResult1);
//
//            // 프로젝션
//            em.flush();
//            em.clear();
//
//            List<Member> result2 = em.createQuery("select m from Member m", Member.class)
//                    .getResultList();
//            // 영속성으로 관리하므로 update 쿼리가 나가는 것을 알 수 있다
//            Member findMember = result2.get(0);
//            findMember.setAge(20);
//            // 조회 대상의 외래키로 다른 엔티티를 조회하고 싶을 때 join 쿼리가 나가게 된다. 가능한한 sql과 비슷한 쿼리로 jpql을 작성해야한다.
//            // 이렇게 조회하면 안됀다.
////            List<Team> result3 = em.createQuery("select m.team from Member m", Team.class)
////                            .getResultList();
//            // 이렇게 조회해야 한다.
//            List<Member> result3 = em.createQuery("select m from Member m", Member.class)
//                            .getResultList();
//            // 스칼라 값도 가져올 수 있으며 distinct로 중복을 제거할 수 있다.
////            em.createQuery("select distinct m.username, m.age from Member m")
////                    .getResultList();
//            // 여러 타입으로 받는 경우 조회 방법
//            // Object 타입으로 받는 방법
////            List resultList2 = em.createQuery("select distinct m.username, m.age from Member m")
////                    .getResultList();
////            Object o = resultList2.get(0);
////            Object[] result4 = (Object[]) o;
////            System.out.println("username = " + result4[0]);
////            System.out.println("age = " + result4[1]);
//            // 위 과정은 type casting으로 코드를 줄일 수 있다.
//            // 이게 Query 방법으로 가져오는 것이다
//            List<Object[]> resultList2 = em.createQuery("select distinct m.username, m.age from Member m")
//                    .getResultList();
//            Object[] result4 = resultList2.get(0);
//            System.out.println("username = " + result4[0]);
//            System.out.println("age = " + result4[1]);
//            // new 명령어로 조회
//            List<MemberDTO> resultList3 = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
//                    .getResultList();
//            MemberDTO memberDTO = resultList3.get(0);
//            System.out.println("username = " + memberDTO.getUsername());
//            System.out.println("age = " + memberDTO.getAge());
//
//            // 페이징 기법 API
//            // Dialect(방언) 다르게 하면 다른 페이징 쿼리 나간다
//            List<Member> resultList4 = em.createQuery("select m from Member m order by m.age desc", Member.class)
//                    .setFirstResult(1)
//                    .setMaxResults(10)
//                    .getResultList();
//            System.out.println("resultList.size = " + resultList4.size());
//            for (Member member1 : resultList4) {
//                System.out.println("member1 = " + member1);
//            }
//======
            // Join 기법
//            Team team = new Team();
//            team.setName("teamA");
//            em.persist(team);
//
//            Member member = new Member();
//            member.setUsername("member1");
//            member.setAge(10);
//            member.setTeam(team);
//            member.setType(MemberType.ADMIN);
//            em.persist(member);
//
//            em.flush();
//            em.clear();
//
//            // 내부 조인 inner join
//            String query = "select m from Member m inner join m.team t";
//            List<Member> result = em.createQuery(query, Member.class)
//                    .getResultList();
//            System.out.println("result = " + result);
//
//            // 외부 조인 left outer join
//            String query2 = "select m from Member m left outer join m.team t";
//            List<Member> result2 = em.createQuery(query2, Member.class)
//                    .getResultList();
//            System.out.println("result2 = " + result2);
//
//            // 세타 조인 기준 이상하게 막 조인하는 방식이다
//            String query3 = "select m from Member m, Team t where m.username = t.name";
//            List<Member> result3 = em.createQuery(query3, Member.class)
//                    .getResultList();
//            System.out.println("result3 = " + result3);
//            System.out.println("result3.size() = " + result3.size());
//
//            // on 절 사용 (JPA 2.1부터 지원)
//            // 조인 대상 필터링
//            String query4 = "select m from Member m left join m.team t on t.name = 'teamA'";
//            List<Member> result4 = em.createQuery(query4, Member.class)
//                    .getResultList();
//            System.out.println("result4 = " + result4);
//            System.out.println("result4.size() = " + result4.size());
//            // 연관관계 없는 엔티티 외부 조인
//            String query5 = "select m from Member m join Team t on m.username = t.name";
//            List<Member> result5 = em.createQuery(query5, Member.class)
//                    .getResultList();
//            System.out.println("result5 = " + result5);
//            System.out.println("result5.size() = " + result5.size());
////======
//            // 서브 쿼리
//
//            // select 절도 서브쿼리 가능하다 (하이버네이트 지원)
//            String query6 = "select (select avg(m1.age) from Member m1) as avgAge from m join Team t on m.username = t.name";
////======
//            // JPQL 타입 표현
//            // 이렇게 하면 'HELLO', TRUE가 바로 1, 2번 인덱스로 모든 레코드마다 들어간다.
////            String query7 = "select m.username, 'HELLO', TRUE from Member m";
//            // ENUM 타입 시 패키지명 모두 포함
////            String query7 = "select m.username, 'HELLO', TRUE from Member m "
////                    + "where m.type = jpql.MemberType.ADMIN";
//            // 파라미터 바인딩으로 ENUM 패키지명을 대체할 수 있다
//            String query7 = "select m.username, 'HELLO', TRUE from Member m "
//                    + "where m.type = :userType";
//            List<Object[]> result7 = em.createQuery(query7)
//                    .setParameter("userType", MemberType.ADMIN)
//                    .getResultList();
//            // is not null
//            String query8 = "select m.username, 'HELLO', TRUE from Member m "
//                    + "where m.username is not null";
//            List<Object[]> result8 = em.createQuery(query8)
//                    .getResultList();
//            // between
//            String query9 = "select m.username, 'HELLO', TRUE from Member m "
//                    + "where m.age between 0 and 10";
//            List<Object[]> result9 = em.createQuery(query9)
//                    .getResultList();
//            for (Object[] objects : result9) {
//                System.out.println("objects[0] = " + objects[0]);
//                System.out.println("objects[1] = " + objects[1]);
//                System.out.println("objects[2] = " + objects[2]);
//            }
//======
            // JPQL 조건식 CASE 식
//            Team team = new Team();
//            team.setName("teamA");
//            em.persist(team);
//
//            Member member = new Member();
//            member.setUsername("member1");
//            member.setAge(10);
//            member.setTeam(team);
//            member.setType(MemberType.ADMIN);
//            team.getMembers().add(member);
//            em.persist(member);
//
//            Team team2 = new Team();
//            team2.setName("teamB");
//            em.persist(team2);
//
//            Member member2 = new Member();
//            member.setUsername("member2");
//            member.setAge(20);
//            member.setTeam(team2);
//            member.setType(MemberType.ADMIN);
//            team2.getMembers().add(member2);
//            em.persist(member2);
//
//            em.flush();
//            em.clear();

            // 기본 CASE 식
//            String query =
//                    "select " +
//                            "case when m.age <= 10 then '학생요금' " +
//                            "     when m.age >= 60 then '경로요금' " +
//                            "     else '일반요금' " +
//                            "end " +
//                    "from Member m";
//            List<String> result = em.createQuery(query, String.class)
//                    .getResultList();
//            for (String s : result) {
//                System.out.println("s = " + s);
//            }
//            // COALESCE 조건식
//            // as 식을 붙일 수 있다
//            String query2 = "select coalesce(m.username, '이름 없는 회원') as username from Member m";
//            List<String> result2 = em.createQuery(query2, String.class)
//                    .getResultList();
//            for (String s : result2) {
//                System.out.println("s = " + s);
//            }
//            // NULLIF 조건식
//            String query3 = "select nullif(m.username, 'member1') as username from Member m";
//            List<String> result3 = em.createQuery(query3, String.class)
//                    .getResultList();
//            for (String s : result3) {
//                System.out.println("s = " + s);
//            }

            // JPQL 기본 함수

            // CONCAT
//            String query = "select concat('a', 'b') from Member m";
            // CONCAT의 ','는 || 로도 대체 가능하다
//            String query = "select 'a' || 'b' from Member m";
            //SUBSTRING
            //////// 오류 - 2번째 값 null 나온다
//            String query = "select substring(m.username, 2, 2) from Member m";
            // LOCATE
//            String query = "select locate('de', 'abcdegf') from Member m";
            // SIZE
            //////// 오류 - Integer 타입이 아닌 Object 타입으로 넘어온다
            //////// 오류 - 쿼리문 자체가 이상하게 날라간다
            // 연관관계 주인 상대편이 사용가능
//            String query = "select size(t.members) from Team t";
            // INDEX
//            @OrderColumn
//            String query = "select index(t.members) from Team t";
            // 사용자 정의 함수 호출
            // group_concat
            //////// 오류 - 쿼리문은 제대로 나가지만 결과에 2개의 값이 아닌 1개의 값이 출력된다
//            String query = "select function('group_concat', m.username) from Member m";
            //hibernate에선 직관적으로 쿼리를 풀이할 수 있다
//            String query = "select group_concat(m.username) from Member m";

//            String query = "select group_concat(m.username) from Member m";
//
//            // Locate시 Integer 타입으로 설정해야 한다.
//            List<String> result = em.createQuery(query, String.class)
//                    .getResultList();
////            System.out.println("result = " + result);
//            for (String s : result) {
//                System.out.println("s = " + s);
//            }
//======
            // JPQL 경로 표현식 '.'
//
//            Team team = new Team();
//            em.persist(team);
//
//
//            Member member1 = new Member();
//            member1.setUsername("관리자1");
//            member1.setTeam(team);
//            em.persist(member1);
//
//            Member member2 = new Member();
//            member2.setUsername("관리자2");
//            member2.setTeam(team);
//            em.persist(member2);
//
//            em.flush();
//            em.clear();
//
////            String query = "select t.members from Team t";
//            // FROM 절에서 명시적 조인을 통해 컬렉션 값 연관 경로에서 탑색 가능하게 할 수 있다
//            String query = "select m.username from Team t join t.members m";
//
//
//            List<String> result = em.createQuery(query, String.class)
//                    .getResultList();
//            System.out.println("result = " + result);
//            for (Integer o : result) {
//                System.out.println("o = " + o);
//            }
//======
            // Fetch Join

            // member1,2는 team1에 속하고 member3은 team2에 속하고 member4는 소속이 없으며 team3는 소속된 멤버가 없다.
            // join은 inner join 이므로 member1,2 와 소속 team1, member3고 소속 team2를 반환한다.
            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Team teamC = new Team();
            teamC.setName("teamC");
            em.persist(teamC);

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("member3");
            member3.setTeam(teamB);
            em.persist(member3);

            Member member4 = new Member();
            member4.setUsername("member4");
            em.persist(member4);

//            em.flush();
//            em.clear();

            // 여기는 N : 1 fetch join 이다

            // member1, teamA(SQL) - teamA가 영속성에 없으므로 데이터베이스에서 가져온다
            // member2, teamA(1차캐시) - 영속성에 있으므로 바로 사용한다.
            // member3, teamB(SQL) - teamB가 영속성에 없으므로 데이터베이스에서 가져온다.
            // Member 조회까지 합하면 총 쿼리 3번 나갔다.
            // 회원 100명이고 모두 팀 다를때 -> 회원 조회 쿼리 1개 + 각 팀 조회 쿼리 N개, 즉, N + 1 쿼리 나간다.
//            String query = "select m from Member m";

            // join fetch로 명시적 조인을 하여 한번에 원하는 엔티티를 모두 가져와서 N + 1 방지 가능하다.
            // 한번에 조인하여 쿼리가 1번만 나간다.
            // left join fetch 하면 outer join 된다.
            // Team 객체들은 이미 조인해서 가져와서 프록시가 아닌 실제 엔티티를 가져온다.
//            String query = "select m from Member m join fetch m.team";
//            List<Member> result = em.createQuery(query, Member.class)
//                            .getResultList();
//
//            for (Member member : result) {
//                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
//            }

            // 여기는 컬렉션 fetch join 이다
            // 1 : N 이나 컬렉션 fetch join을 말한다

//            String query = "select t from Team t join fetch t.members";
//            List<Team> result = em.createQuery(query, Team.class)
//                    .getResultList();
//
//            for (Team team : result) {
//                System.out.println("team = " + team.getName() + "|members=" + team.getMembers().size());
//                for(Member member : team.getMembers()){
//                    System.out.println("->  member = " + member);
//                }
//            }

            // 여기는 Distinct로 컬렉션 fetch join의 중복 엔티티 문제를 해결한다.

//            String query = "select distinct t from Team t join fetch t.members";
//            List<Team> result = em.createQuery(query, Team.class)
//                    .getResultList();
//
//            for (Team team : result) {
//                System.out.println("team = " + team.getName() + "|members=" + team.getMembers().size());
//                for(Member member : team.getMembers()){
//                    System.out.println("->  member = " + member);
//                }
//            }

            // 여기는 fetch join 과 일반 join의 차이에 대해 말한다.
            // 일반 join을 하면 member을 사용할 때 조인만 하고 아직 데이터를 가지고 온 것이 아니므로 추가 쿼리가 나간다.
            // 물론 이 때도 1 : N 데이터 뻥튀기는 일어난다.

//            String query = "select t from Team t join t.members m";
//            List<Team> result = em.createQuery(query, Team.class)
//                    .getResultList();
//
//            for (Team team : result) {
//                System.out.println("team = " + team.getName() + "|members=" + team.getMembers().size());
//                for(Member member : team.getMembers()){
//                    System.out.println("->  member = " + member);
//                }
//            }
//======
            // fetch join 한계

            // fetch join은 페이징 API 적용하면 데이터 잘못 뽑으므로 페이징 API 사용하면 안됀다.
//            String query = "select t from Team t join fetch t.members m";
            // 이렇게 하면 1 : N 이 아닌 N : 1 이므로 데이터 뻥튀기 없어서 괜찮다
//            String  query = "select m from Member m join fetch m.team t";
            //
//            String query = "select t from Team t";
//
//
//            List<Team> result = em.createQuery(query, Team.class)
//                    .setFirstResult(0)
//                    .setMaxResults(2)
//                    .getResultList();
//
//            for (Team team : result) {
//                System.out.println("team = " + team.getName() + "|members=" + team.getMembers().size());
//                for(Member member : team.getMembers()){
//                    System.out.println("->  member = " + member);
//                }
//            }
//======
            // 엔티티 직접 사용

            // 기본키
//            String query = "select m from Member m where m = :member";
            // 외래키
//            String query = "select m from Member m where m.team = :team";
//
//
//            List<Member> result = em.createQuery(query, Member.class)
//                    .setParameter("team", teamA)
//                    .getResultList();
//
////            System.out.println("findMember = " + findMember);
//
//            for (Member member : result) {
//                System.out.println("member = " + member);
//            }
//======
            // Named 쿼리

            // Member 엔티티의 username으로 조회하는 네임드 쿼리 사용해본다
//            List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
//                    .setParameter("username", "member1")
//                    .getResultList();
//
//            for (Member member : resultList) {
//                System.out.println("member = " + member);
//            }
//======
            // 벌크 연산

            // flush 자동 호출한다
            int resultCount = em.createQuery("update Member m set m.age = 20")
                    .executeUpdate();

            System.out.println("resultCount = " + resultCount);

            // 여기서는 나이가 모두 0 으로 나온다
            // 벌크는 영속성을 무시하고 날리기 때문이다
            // 즉 이 출력문을 호출하기 전에 영속성 컨텍스트를 초기화해야 한다.
//            System.out.println("member1.getAge() = " + member1.getAge());
//            System.out.println("member2.getAge() = " + member2.getAge());
//            System.out.println("member3.getAge() = " + member3.getAge());

            // 정상 작 동
            em.clear();
            Member findMember = em.find(Member.class, member1.getId());

            System.out.println("findMember = " + findMember.getAge());


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
