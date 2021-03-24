package org.yushu.comex.serialize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author Hanson on 16/5/31.
 */
public class SerializerSelector {

    private static final SerializerSelector instance = new SerializerSelector();
    private final Serializer[] serializers = new Serializer[Byte.MAX_VALUE];

    private SerializerSelector() {
        ServiceLoader<Serializer> serializerServiceLoader = ServiceLoader.load(Serializer.class);
        for (Serializer serializer : serializerServiceLoader) {
            serializers[serializer.type()] = serializer;
        }
    }

    public static SerializerSelector getInstance() {
        return instance;
    }

    /**
     * 选择一个序列化类型对应的序列化器
     *
     * @param serializeType 序列化类型
     * @return 序列化器
     */
    public Serializer select(byte serializeType) {
        return serializers[serializeType];
    }
}
