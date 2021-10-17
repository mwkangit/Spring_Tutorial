package hello.core.singleton;

public class StatefulService {

    private int price; // 상태를 유지하는 필드

    public void order(String name, int price){
        System.out.println("name = " + name + "price = " + price);
        this.price = price; // 여기가 문제!

        // 사실 price를 오래 존재하는 변수로 두지말고 이 order 메서드에서 price를 반환하면 된다.

    }

    public int getPrice(){
        return price;
    }



}
