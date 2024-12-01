package agent;
import javassist.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader classLoader, String s, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
//                System.out.println("Class: " + s);
                // 拦截 Runtime.exec()  似乎没有用到
                if ("java/lang/Runtime".equals(s)) {
                    System.out.println("Captured Runtime class");

                    try {
                        ClassPool pool = ClassPool.getDefault();
                        CtClass runtimeClass = pool.get("java.lang.Runtime");

                        // 获取 exec(String) 方法
                        CtMethod execMethod = runtimeClass.getDeclaredMethod("exec", new CtClass[] {pool.get("java.lang.String")});

                        // 插桩，打印命令信息
                        execMethod.insertBefore("{ System.out.println(\"Runtime exec command: \" + $1); }");

                        byte[] byteCode = runtimeClass.toBytecode();
                        runtimeClass.detach();
                        return byteCode;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

//                if ("java/lang/ProcessBuilder".equals(s)) {
//                    System.out.println("Captured ProcessBuilder class");
//
//                    try {
//                        ClassPool pool = ClassPool.getDefault();
//                        CtClass processBuilderClass = pool.get("java.lang.ProcessBuilder");
//
//                        // 获取 start() 方法
//                        CtMethod startMethod = processBuilderClass.getDeclaredMethod("start");
//
//                        // 插入代码来打印命令
//                        startMethod.insertBefore("{ " +
//                                "System.out.println(\"ProcessBuilder command: \" + this.command().toString());" +  // 使用 toString() 打印命令列表
//                                "new Exception(\"Call stack:\").printStackTrace();" +
//                                "}");
//
//                        byte[] byteCode = processBuilderClass.toBytecode();
//                        processBuilderClass.detach();
//                        return byteCode;
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }

                if ("java/io/ObjectInputStream".equals(s)) {
                    System.out.println("Class: " + s);

                    try {
                        ClassPool pool = ClassPool.getDefault();
                        System.out.println("flag01");

                        CtClass ctClass = pool.get("java.io.ObjectInputStream");
                        System.out.println("flag02");

                        // More specific method resolution
                        // 获取 readObject 方法
                        CtMethod[] methods = ctClass.getDeclaredMethods();
                        for (CtMethod method : methods) {
                            System.out.println(method.getName());
                        }
                        CtMethod readObjectMethod = ctClass.getDeclaredMethod("readObject");
//                        CtMethod readObjectMethod = ctClass.getDeclaredMethod("readObject", new CtClass[] { pool.get("java.io.ObjectInputStream") });

                        System.out.println("flag03");

                        // 插桩：打印反序列化堆栈信息与输入数据类型
                        // 插桩：打印反序列化堆栈信息与输入数据类型
                        // 插桩代码：反序列化前打印信息
                        String insertCode =
                                "System.out.println(\"Deserialization started...\");" +
                                        "System.out.println(\"Current Thread: \" + Thread.currentThread().getName());" +
                                        "System.out.println(\"Calling method: \" + $0.getClass().getName() + \".readObject\");" +  // 打印调用的类与方法
                                        "System.out.println(\"Stack Trace:\");" +
                                        "new Exception(\"Call Stack:\").printStackTrace();" +  // 打印堆栈信息
                                        "System.out.println(\"Deserializing Object\");";  // 简化目标对象类型打印

                        readObjectMethod.insertBefore(insertCode);
                        byte[] byteCode = ctClass.toBytecode();
                        ctClass.detach();
                        return byteCode;
                    } catch (NotFoundException e) {
                        System.err.println("Class or method not found: " + e.getMessage());
                        e.printStackTrace();
                    } catch (Exception e) {
                        System.err.println("Unexpected error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                return null;
            }
        });
    }



}

