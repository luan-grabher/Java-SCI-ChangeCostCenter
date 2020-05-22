package testPack;

public class testMain {


    public static void main(String[] args) {
        String s = " replace this :rep";
        Integer n = null;
        
        s = s.replaceFirst(":rep", n + "");
        
        System.out.println(s);
    }
    
}
