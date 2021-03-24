package org.yushu.comex.serialize.json;

import com.alibaba.fastjson.JSON;
import org.yushu.comex.serialize.SerializeType;
import org.yushu.comex.serialize.Serializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author: frank.li
 * @date: 2021/3/23
 */
public class FastJsonSerializer implements Serializer {

    private final static Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

    @Override
    public byte type() {
        return SerializeType.JSON.getCode();
    }

    @Override
    public byte[] serialize(Object object) {
        String json = JSON.toJSONString(object);
        if (json != null) {
            return json.getBytes(CHARSET_UTF8);
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classOfT) {
        String json = new String(bytes, CHARSET_UTF8);
        return JSON.parseObject(json, classOfT);
    }
}
