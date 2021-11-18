package jpabook.jpashop_basic;

import jpabook.jpashop_basic.domain.Order;
import jpabook.jpashop_basic.domain.OrderItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // transaction 가져오기
        tx.begin(); // transaction 실행 시작

        try{

            Order order = new Order();
            em.persist(order);
//            order.addOrderItem(new OrderItem()); // 이 방법말고 아래 방법을 이용하면 주인을 이용하는 것이므로 가능하다.
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            em.persist(orderItem);

            tx.commit();
        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
