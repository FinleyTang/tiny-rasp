package other;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Stuff stuff = new Stuff();
        stuff.run();
        stuff.execute();
        int result  = stuff.add(3, 5);
        System.out.printf("result is: %d", result);
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("cmd.exe /c dir");  // 在这里执行你要监控的命令

        try {
            // 创建一个对象并将其序列化
            MyObject myObject = new MyObject("test", 123);
            FileOutputStream fos = new FileOutputStream("myobject.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(myObject);
            oos.close();
            System.out.println("Object serialized");

            // 从文件中读取并反序列化
            FileInputStream fis = new FileInputStream("myobject.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            MyObject deserializedObject = (MyObject) ois.readObject();
            ois.close();
            System.out.println("Object deserialized: " + deserializedObject);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
