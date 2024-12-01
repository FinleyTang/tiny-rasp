package other;

public class Main {

    public static void main(String[] args) {
        Stuff stuff = new Stuff();
        stuff.run();
        stuff.execute();
        int result  = stuff.add(3, 5);
        System.out.printf("result is: %d", result);
    }

}
