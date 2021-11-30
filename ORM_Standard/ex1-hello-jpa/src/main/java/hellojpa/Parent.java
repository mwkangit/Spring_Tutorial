package hellojpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// 영속성 전이 CASCADE 설명 용 엔티티이다
@Entity
public class Parent {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

//    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    // 부모 삭제하면 그 객체와 연관된 자식 모두 삭제한다
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Child> childList = new ArrayList<>();

    public void addChild(Child child){
        childList.add(child);
        child.setParent(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Child> getChildList() {
        return childList;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
    }
}
