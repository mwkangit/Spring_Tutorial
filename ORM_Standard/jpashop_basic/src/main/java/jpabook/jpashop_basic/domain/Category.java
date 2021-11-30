package jpabook.jpashop_basic.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
public class Category extends BaseEntity{

    @Id @GeneratedValue
    private Long id;

    private String name;

    // 상위 카테고리이다
    // 셀프로 매핑하는 것도 jpa 가능하다
    // 자식은 많지만 부모는 1개일 수 있으므로
//여기 모르겠다
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "PARENT_ID")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();
//여기까지 모르겠다

    // 내가 조인 해야하는 것은 얘고 반대쪽에서 조인해야하는 것은 쟤야 라는 뜻이다.
    @ManyToMany
    @JoinTable(name = "CATEGORY_ITEM",
        joinColumns = @JoinColumn(name = "CATEGORY_ID"),
        inverseJoinColumns = @JoinColumn(name = "ITEM_ID"))
    private List<Item> items = new ArrayList<>();



}
