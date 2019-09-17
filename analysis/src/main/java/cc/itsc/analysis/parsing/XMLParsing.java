package cc.itsc.analysis.parsing;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/9/17 21:02
 */
public class XMLParsing {


    public static void main(String[] args) {
        // 解析books.xml文件
        // 创建SAXReader的对象reader
        SAXReader reader = new SAXReader();
        try {
            // 通过reader对象的read方法加载xml文件,获取Document对象。
            Document document = reader.read(new File("E:\\0_JavaProject\\1_OpenHub\\UpPlan7\\analysis\\src\\main\\resources\\profile\\ProfileConfiguration.xml"));
            // 通过document对象获取根节点
            Element profiles = document.getRootElement();
            // 通过element对象的elementIterator方法获取迭代器
            Iterator itp = profiles.elementIterator();
            // 遍历迭代器，获取根节点中的信息
            while (itp.hasNext()) {
                System.out.println("=====第一个节点=====");
                Element item = (Element) itp.next();
                if ("original".equals(item.getName()) || "transfer".equals(item.getName())) {
                    Iterator profileIter = item.elementIterator();
                    while (profileIter.hasNext()) {
                        Element profile = (Element) profileIter.next();
                        // 获取属性名以及 属性值
                        List<Attribute> profileAttrs = profile.attributes();
                        for (Attribute attr : profileAttrs) {
                            System.out.println("属性名：" + attr.getName() + "--属性值："
                                    + attr.getValue());
                        }
                        Iterator itt = profile.elementIterator();
                        while (itt.hasNext()) {
                            Element profileChild = (Element) itt.next();
                            System.out.println("节点名：" + profileChild.getName() + "--节点值：" + profileChild.getStringValue());
                        }
                        System.out.println("=====结束遍历=====");
                    }
                }
            }

        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
