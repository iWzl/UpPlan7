package cc.itsc.analysis.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ReflectBeanTest {
    public static void main(String[] args) {
        ExampleBean order = new ExampleBean();
        order.setId(1);
        order.setName("LeoWang");
        try {
            HashMap addMap = new HashMap();
            HashMap addValMap = new HashMap();
            addMap.put("age", Class.forName("java.lang.Integer"));
            addValMap.put("age",22);
            Object obj2 = new ReflectUtil().dynamicClass(order, addMap, addValMap);
            System.out.println(obj2.toString());
            // 通过反射查看所有方法名
            Class clazz = obj2.getClass();
            Method[] methods = clazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                System.out.println(methods[i].getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
