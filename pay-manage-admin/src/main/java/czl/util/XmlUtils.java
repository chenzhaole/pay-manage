package czl.util;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlUtils {

      /* java对象转换为xml文件
       * @param xmlPath  xml文件路径
       * @param load    java对象.Class
       * @return    xml文件的String
       * @throws JAXBException
      */
     public static String beanToXml(Object obj,Class<?> load) throws JAXBException {
         JAXBContext context = JAXBContext.newInstance(load);
         Marshaller marshaller = context.createMarshaller();
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
         marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
         //是否省略xml头信息（<?xml version="1.0" encoding="gb2312" standalone="yes"?>）
         marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
         StringWriter writer = new StringWriter();
         marshaller.marshal(obj,writer);
         return writer.toString();

     }

        /**
           * xml文件配置转换为对象
           * @param xml  xml字符串
           * @param load  java对象.Class
           * @return    java对象
           * @throws JAXBException
           * @throws IOException
           */
     public static Object xmlToBean(String xml,Class<?> load) throws JAXBException, IOException {
                JAXBContext context = JAXBContext.newInstance(load);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                //是否省略xml头信息（<?xml version="1.0" encoding="gb2312" standalone="yes"?>）
                //unmarshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
                StringReader reader = new StringReader(xml);
                Object object = unmarshaller.unmarshal(reader);
                return object;
     }
}
