package com.sys.admin.common.mapper;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.util.Assert;

import com.sys.common.util.Reflections;
import com.sys.common.util.ThrowableUtils;
import com.thoughtworks.xstream.XStream;

/**
 * 使用Jaxb2.0实现XML<->Java Object的Mapper.
 * 
 * 在创建时需要设定所有需要序列化的Root对象的Class.
 * 特别支持Root对象是Collection的情形.
 * 
 */
@SuppressWarnings("rawtypes")
public class XmlMapper {

	private static ConcurrentMap<Class, JAXBContext> jaxbContexts = new ConcurrentHashMap<Class, JAXBContext>();

	/**
	 * Java Object->Xml without encoding.
	 */
	public static String toXml(Object root) {
		Class clazz = Reflections.getUserClass(root);
		return toXml(root, clazz, null);
	}

    public static String obj2Xml(Object root) {
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.aliasSystemAttribute(null, "class");
        return xstream.toXML(root);
    }

	/**
	 * Java Object->Xml with encoding.
	 */
	public static String toXml(Object root, String encoding) {
		Class clazz = Reflections.getUserClass(root);
		return toXml(root, clazz, encoding);
	}

	/**
	 * Java Object->Xml with encoding.
	 */
	public static String toXml(Object root, Class clazz, String encoding) {
		try {
			StringWriter writer = new StringWriter();
			createMarshaller(clazz, encoding).marshal(root, writer);
			return writer.toString();
		} catch (JAXBException e) {
			throw ThrowableUtils.unchecked(e);
		}
	}

	/**
	 * Java Collection->Xml without encoding, 特别支持Root Element是Collection的情形.
	 */
	public static String toXml(Collection<?> root, String rootName, Class clazz) {
		return toXml(root, rootName, clazz, null);
	}

	/**
	 * Java Collection->Xml with encoding, 特别支持Root Element是Collection的情形.
	 */
	public static String toXml(Collection<?> root, String rootName, Class clazz, String encoding) {
		try {
			CollectionWrapper wrapper = new CollectionWrapper();
			wrapper.collection = root;

			JAXBElement<CollectionWrapper> wrapperElement = new JAXBElement<CollectionWrapper>(new QName(rootName),
					CollectionWrapper.class, wrapper);

			StringWriter writer = new StringWriter();
			createMarshaller(clazz, encoding).marshal(wrapperElement, writer);

			return writer.toString();
		} catch (JAXBException e) {
			throw ThrowableUtils.unchecked(e);
		}
	}

	/**
	 * Xml->Java Object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String xml, Class<T> clazz) {
		try {
			StringReader reader = new StringReader(xml);
			return (T) createUnmarshaller(clazz).unmarshal(reader);
		} catch (JAXBException e) {
			throw ThrowableUtils.unchecked(e);
		}
	}

    public static Map<String, Object> Xml2Map(String xml) {
        try {
            Document doc = DocumentHelper.parseText(xml);
            return Dom2Map(doc);
        } catch (DocumentException e) {
            throw ThrowableUtils.unchecked(e);
        }
    }

    public static Map<String, Object> Dom2Map(Document doc) {
		if (null != doc) {
			return Element2Map(doc.getRootElement(), new HashMap<String, Object>());
		}
		return null;
	}


    public static Map<String, Object> Element2Map(Element element, Map<String, Object> map) {
        Map<String, Object> childrenMap = new HashMap<String, Object>();

        Iterator attrIt = element.attributeIterator();
        while (attrIt.hasNext()) {
            Attribute a = (Attribute) attrIt.next();
            if ("xsi:nil".equals(a.getQualifiedName()) && "true".equals(a.getText())) {
                putInMap(map, element.getQualifiedName(), null);
                return map;
            }
            putInMap(childrenMap, a.getQualifiedName(), a.getText());
        }

        Iterator eleIt = element.elementIterator();
        while (eleIt.hasNext()) {
            Element e = (Element) eleIt.next();
            Element2Map(e, childrenMap);
        }
        if (element.isTextOnly()) {
            if (childrenMap.size() == 0) {
                putInMap(map, element.getQualifiedName(), element.getText());
                return map;
            } else {
                putInMap(childrenMap, element.getQualifiedName(), element.getText());
            }
        }
        if (element.isRootElement()) {
            return childrenMap;
        }

        putInMap(map, element.getQualifiedName(), childrenMap);
        return map;
    }

    private static void putInMap(Map<String, Object> map, String key, Object value) {
        if (map.containsKey(key)) {
            List mapList;
            Object obj = map.get(key);
            if (!ArrayList.class.equals(obj.getClass())) {
                mapList = new ArrayList();
                mapList.add(obj);
                mapList.add(value);
            } else {
                mapList = (List) obj;
                mapList.add(value);
            }
            map.put(key, mapList);
        } else {
            map.put(key, value);
        }
    }

	/**
	 * 创建Marshaller并设定encoding(可为null).
	 * 线程不安全，需要每次创建或pooling。
	 */
	public static Marshaller createMarshaller(Class clazz, String encoding) {
		try {
			JAXBContext jaxbContext = getJaxbContext(clazz);

			Marshaller marshaller = jaxbContext.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			if (StringUtils.isNotBlank(encoding)) {
				marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
			}

			return marshaller;
		} catch (JAXBException e) {
			throw ThrowableUtils.unchecked(e);
		}
	}

	/**
	 * 创建UnMarshaller.
	 * 线程不安全，需要每次创建或pooling。
	 */
	public static Unmarshaller createUnmarshaller(Class clazz) {
		try {
			JAXBContext jaxbContext = getJaxbContext(clazz);
			return jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw ThrowableUtils.unchecked(e);
		}
	}

	protected static JAXBContext getJaxbContext(Class clazz) {
		Assert.notNull(clazz, "'clazz' must not be null");
		JAXBContext jaxbContext = jaxbContexts.get(clazz);
		if (jaxbContext == null) {
			try {
				jaxbContext = JAXBContext.newInstance(clazz, CollectionWrapper.class);
				jaxbContexts.putIfAbsent(clazz, jaxbContext);
			} catch (JAXBException ex) {
				throw new HttpMessageConversionException("Could not instantiate JAXBContext for class [" + clazz
						+ "]: " + ex.getMessage(), ex);
			}
		}
		return jaxbContext;
	}

	/**
	 * 封装Root Element 是 Collection的情况.
	 */
	public static class CollectionWrapper {

		@XmlAnyElement
		protected Collection<?> collection;
	}
}
