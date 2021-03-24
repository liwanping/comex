package org.yushu.comex.serialize;

import org.yushu.comex.serialize.SerializeType;
import org.yushu.comex.serialize.SerializerSelector;

/**
 * @author: frank.li
 * @date: 2021/3/24
 */
public class SerializeUtils {

    public static final String SERIALIZE_TYPE_PROPERTY = "serialize.type";

    private static SerializeType serializeType = SerializeType.JSON;

    static {
        final String protocol = System.getProperty(SERIALIZE_TYPE_PROPERTY);
        if (!isBlank(protocol)) {
            try {
                serializeType = SerializeType.valueOf(protocol);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("parser specified protocol error. protocol=" + protocol, e);
            }
        }
    }

    public static byte[] serialize(Object object) {
        return SerializerSelector.getInstance()
                .select(serializeType.getCode())
                .serialize(object);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> classOfT) {
        return SerializerSelector.getInstance()
                .select(serializeType.getCode())
                .deserialize(bytes, classOfT);
    }

    public static SerializeType getSerializeType() {
        return serializeType;
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
