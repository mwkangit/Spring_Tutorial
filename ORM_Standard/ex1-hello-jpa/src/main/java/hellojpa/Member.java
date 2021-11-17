package hellojpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
//@SequenceGenerator(name = "member_seq_generator",
//        sequenceName = "membrer_seq",
//        initialValue = 1, allocationSize = 1)
public class Member {

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE,
//            generator = "member_seq_generator")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME") // username이라는 객체명을 사용하고 싶은데 db에는 name이라는 column으로 매핑한다.
    private String username;

//    @Column(name = "TEAM_ID")
//    private Long teamId;


    // N : 1 경우
    // Member와 Team이 1 : N 인지 N : 1인지 관계 알려줘야 한다.
//    @ManyToOne
//    @JoinColumn(name = "TEAM_ID")
//    private Team team;

    // 1 : N 양방향 경우 - 양방향 아닐 때에는 Team 객체 없다.
//    @ManyToOne
//    @JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)
//    private Team team;

    // 1 : 1 경우 - Member을 주인으로 설정하는 경우
    @OneToOne
    @JoinColumn(name = "LOCKER_ID")
    private Locker locker;

    // N : M 경우 - 이렇게 하면 단방향이다
//    @ManyToMany
//    @JoinTable(name = "MEMBER_PRODUCT")
//    private List<Product> products = new ArrayList<>();

    // N : M 을 1 : N, N : 1 로 풀어 사용하기
    @OneToMany(mappedBy = "member")
    private List<MemberProduct> memberProducts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //    public void changeTeam(Team team) {
//        this.team = team;
//        team.getMembers().add(this); // 현재 이 객체를 team의 members에 추가한다.
//    }
    

    //
//    private Integer age; // 가장 적절한 숫자타입 만들어서 부여한다.
//
//    @Enumerated(EnumType.STRING) // db에는 enum 타입이 없어서 @Enumerated를 붙여준다.
//    private RoleType roleType;
//
//    @Temporal(TemporalType.TIMESTAMP) // 타입에 Date, Time, TimeStamp가 있다. TimeStamp는 둘다 사용한 것이다.
//    private Date createdDate;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date lastModifiedDate;
//
//    @Lob // Clob, Blob? VARCHAR을 넘는 큰 컨텐츠를 입력하고 싶으면 @Lob을 사용한다. // @Lob 시 자료형이 문자타입이면 clob 실행한다.
//    private String description;

    public Member() {
    }

}
