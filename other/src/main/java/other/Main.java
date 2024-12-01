package other;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Stuff stuff = new Stuff();
        stuff.run();
        stuff.execute();
        int result  = stuff.add(3, 5);
        System.out.printf("result is: %d", result);
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("cmd.exe /c dir");  // 在这里执行你要监控的命令
    }

}
