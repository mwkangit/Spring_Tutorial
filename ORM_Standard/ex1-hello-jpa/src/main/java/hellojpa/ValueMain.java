package hellojpa;

public class ValueMain {
    public static void main(String[] args) {
        Integer a = new Integer(50);
        Integer b = a;

        a = 30;

        System.out.println("a = " + a);
        System.out.println("b = " + b);

        String s1 = "abc";
        String s2 = s1;

        s1 += "def";

        System.out.println("s1 = " + s1);
        System.out.println("s2 = " + s2);
        
        Node n1 = new Node(1,33);
        Node n2 = n1;
        
        n1.weight = 50;

        // 클래스는 항상 얕은 복사 된다
        System.out.println("n1.weight = " + n1.weight);
        System.out.println("n2.weight = " + n2.weight);

        Address address1 = new Address("city", "street", "10000");
        Address address2 = new Address("city", "street", "10000");
        // == 비교이므로 false
        System.out.println("address1.equals(address2) = " + address1.equals(address2));
        // equals Address 엔티티에 override 후 사용
        System.out.println("address1.equals(address2) = " + address1.equals(address2));


    }
    
    static class Node{
        int number;
        int weight;
        
        public Node(int number, int weight){
            this.number = number;
            this.weight = weight;
        }
    }
}
