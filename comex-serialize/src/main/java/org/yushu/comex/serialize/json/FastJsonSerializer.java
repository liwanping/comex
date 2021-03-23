package org.yushu.comex.serialize.json;

import org.yushu.comex.serialize.SerializeType;
import org.yushu.comex.serialize.Serializer;

/**
 * @author: frank.li
 * @date: 2021/3/23
 */
public class FastJsonSerializer implements Serializer {

    @Override
    public byte type() {
        return SerializeType.JSON.getCode();
    }

    @Override
    public byte[] serialize(Object object) throws Exception {
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> type) throws Exception {
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes) throws Exception {
        return null;
    }
}
