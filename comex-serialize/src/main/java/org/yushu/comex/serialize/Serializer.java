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
     * @throws Exception serialization exception
     */
    byte[] serialize(Object object) throws Exception;

    /**
     * convert array of bytes to java object
     * @param bytes array of bytes
     * @param type concrete class type to convert
     * @return java object
     * @throws Exception serialization exception
     */
    Object deserialize(byte[] bytes, Class<?> type) throws Exception;

    /**
     * convert array of bytes to java object. use serializer to decide which type to convert
     * @param bytes array of bytes
     * @return java object
     * @throws Exception serialization exception
     */
    Object deserialize(byte[] bytes) throws Exception;
}
