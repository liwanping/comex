package org.yushu.comex.serialize;

/**
 * @author: frank.li
 * @date: 2021/3/23
 */
public interface Serializer {

    /**
     * serializer id. It should be unique from the exist ids {@link SerializeType#getCode()}
     *
     * @return serializer name
     */
    byte type();

    /**
     * serialize java object to array of bytes
     * @param object java object
     * @return array of bytes
     */
    byte[] serialize(Object object);

    /**
     * convert array of bytes to java object
     * @param bytes array of bytes
     * @param classOfT concrete class type to convert
     * @return java object
     */
    <T> T deserialize(byte[] bytes, Class<T> classOfT);
}
