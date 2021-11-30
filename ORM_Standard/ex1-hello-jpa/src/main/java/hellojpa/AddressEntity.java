package hellojpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

// 값 타입 컬렉션 대신 1 : N 관계 엔티티 생성한다.
@Entity
@Table(name = "ADDRESS")
public class AddressEntity {

    public AddressEntity() {
    }

    public AddressEntity(String city, String street, String zipcode) {
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
