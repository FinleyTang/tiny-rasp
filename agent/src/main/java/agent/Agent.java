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

                if ("java/lang/ProcessBuilder".equals(s)) {
                    System.out.println("Captured ProcessBuilder class");

                    try {
                        ClassPool pool = ClassPool.getDefault();
                        CtClass processBuilderClass = pool.get("java.lang.ProcessBuilder");
                        CtMethod startMethod = processBuilderClass.getDeclaredMethod("start");

                        // 插桩，打印方法调用的日志
//                        startMethod.insertBefore("{ System.out.println(\"ProcessBuilder start method called\"); }");
                        // 插桩，打印命令和启动日志
                        startMethod.insertBefore("{ " +
                                "System.out.println(\"ProcessBuilder command \" );" +
                                "new Exception(\"Call stack:\").printStackTrace();" +
                                "}");
                        byte[] byteCode = processBuilderClass.toBytecode();
                        processBuilderClass.detach();
                        return byteCode;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                return null;
            }
        });
    }



}

