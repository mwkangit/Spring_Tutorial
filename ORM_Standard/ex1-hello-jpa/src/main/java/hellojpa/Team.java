package hellojpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long Id;

    private  String name;

    // Member 입장 N : 1 경우
    @OneToMany(mappedBy = "team") // 나는 Member의 team 객체에 의해 관리가 되고 있다. 조회는 가능하지만 값을 아무리 넣어도 업데이트는 불가하다. 외래키를 업데이트 할 수 있는 주인이 아니다.
    private List<Member> members = new ArrayList<>();

    // Team 입장 1 : N 경우 - Team의 리스트를 갱신하여 Member 테이블의 Team_ID 값 갱신한다.
//    @OneToMany
//    @JoinColumn(name = "TEAM_ID")
//    private List<Member> members = new ArrayList<>();

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

//    public void addMember(Member member){ // 연관관계로 변수값 세팅해준다
//        member.setTeam(this);
//        members.add(member);
//    }
}
