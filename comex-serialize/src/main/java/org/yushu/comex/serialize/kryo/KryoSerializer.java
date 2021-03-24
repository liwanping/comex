package org.yushu.comex.serialize.kryo;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.yushu.comex.serialize.SerializeType;
import org.yushu.comex.serialize.Serializer;

/**
 * @author Hanson on 16/7/6.
 */
public class KryoSerializer implements Serializer {

    private static final int OUTPUT_BUFFER_INIT_SIZE = 8192;
    private static final int OUTPUT_BUFFER_MAX_SIZE = 102400;

    @Override
    public byte type() {
        return SerializeType.KRYO.getCode();
    }

    @Override
    public byte[] serialize(Object object) {
        Output output = new Output(OUTPUT_BUFFER_INIT_SIZE, OUTPUT_BUFFER_MAX_SIZE);
        KryoUtils.getKryo().writeClassAndObject(output, object);
        byte[] bytes = output.toBytes();
        output.close();
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classOfT) {
        Input input = new Input(bytes);
        return (T)KryoUtils.getKryo().readClassAndObject(input);
    }
}
