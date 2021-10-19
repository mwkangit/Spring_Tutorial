package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception {

        // 1번 아이디의 값을 가져온다는 뜻
        Book book = em.find(Book.class, 1L);

        // TX (transaction)
        // 업데이트 코드 JPA가 자동으로 생성하여 실행한다
        book.setName("asdfasdf");

        // 변경감지 == dirty checking -> 이 메커니즘으로 JPA를 통해 원하는 것으로 업데이트 가능
        // TX commmit

    }
    
}
