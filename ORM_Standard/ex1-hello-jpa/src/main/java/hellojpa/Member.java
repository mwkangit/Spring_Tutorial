package hellojpa;

import javax.persistence.*;
import java.util.*;

@Entity
//@SequenceGenerator(name = "member_seq_generator",
//        sequenceName = "membrer_seq",
//        initialValue = 1, allocationSize = 1)
public class Member extends BaseEntity{

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


    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    // N : 1 경우
    // Member와 Team이 1 : N 인지 N : 1인지 관계 알려줘야 한다.
    @ManyToOne // @...One의 default fetch는 EAGER 이다.
//    @ManyToOne(fetch = FetchType.LAZY) // 프록시 객체를 이용하여 Team에 대한 정보는 필요할 때에만 조회한다.
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    // 1 : N 양방향 경우 - 양방향 아닐 때에는 Team 객체 없다.
//    @ManyToOne
//    @JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)
//    private Team team;

    // 1 : 1 경우 - Member을 주인으로 설정하는 경우
//    @OneToOne
//    @JoinColumn(name = "LOCKER_ID")
//    private Locker locker;

    // N : M 경우 - 이렇게 하면 단방향이다
//    @ManyToMany
//    @JoinTable(name = "MEMBER_PRODUCT")
//    private List<Product> products = new ArrayList<>();

    // N : M 을 1 : N, N : 1 로 풀어 사용하기
//    @OneToMany(mappedBy = "member")
//    private List<MemberProduct> memberProducts = new ArrayList<>();

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
//==
    // @Embedded 타입 생성
    // 기간
//    @Embedded
//    private Period workPeriod;

    // 주소
//    @Embedded
//    private Address homeAddress;

//    @Embedded
//    private Address homeAddress;

    // @Embedded 같은 클래스 2번 사용하고 싶을 때
//    @AttributeOverride()로 컬럼명을 다르게 해준다
//    @Embedded
//    @AttributeOverrides({
//            @AttributeOverride(name = "city",
//            column = @Column(name = "WORK_CITY")),
//            @AttributeOverride(name = "street",
//            column = @Column(name = "WORK_STREET")),
//            @AttributeOverride(name = "zipcode",
//            column = @Column(name = "WORK_ZIPCODE"))
//    })
//    private Address workAddress;

//    public Period getWorkPeriod() {
//        return workPeriod;
//    }
//
//    public void setWorkPeriod(Period workPeriod) {
//        this.workPeriod = workPeriod;
//    }
//
//    public Address getHomeAddress() {
//        return homeAddress;
//    }
//
//    public void setHomeAddress(Address homeAddress) {
//        this.homeAddress = homeAddress;
//    }
//==
    // 값 타입 컬렉션

    @Embedded
    private Address homeAddress;

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

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Set<String> getFavoriteFoods() {
        return favoriteFoods;
    }

    public void setFavoriteFoods(Set<String> favoriteFoods) {
        this.favoriteFoods = favoriteFoods;
    }

    public List<AddressEntity> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<AddressEntity> addressHistory) {
        this.addressHistory = addressHistory;
    }

    public Member() {
    }

}
