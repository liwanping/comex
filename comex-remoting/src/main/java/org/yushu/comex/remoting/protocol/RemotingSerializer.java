package org.yushu.comex.remoting.protocol;

import org.yushu.comex.remoting.protocol.RemotingCommand;
import org.yushu.comex.serialize.SerializeType;
import org.yushu.comex.serialize.SerializerSelector;

import java.nio.ByteBuffer;

/**
 * @author: frank.li
 * @date: 2021/3/24
 */
public class RemotingSerializer {

    public static RemotingCommand decode(final byte[] array) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        return decode(byteBuffer);
    }

    public static RemotingCommand decode(final ByteBuffer byteBuffer) {
        int length = byteBuffer.limit();
        int oriHeaderLen = byteBuffer.getInt();
        int headerLength = getHeaderLength(oriHeaderLen);

        byte[] headerData = new byte[headerLength];
        byteBuffer.get(headerData);

        RemotingCommand cmd = headerDecode(headerData, getProtocolType(oriHeaderLen));

        int bodyLength = length - 4 - headerLength;
        byte[] bodyData = null;
        if (bodyLength > 0) {
            bodyData = new byte[bodyLength];
            byteBuffer.get(bodyData);
        }
        cmd.setBody(bodyData);
        return cmd;
    }

    public static ByteBuffer encodeHeader(RemotingCommand cmd) {

        int bodyLength = 0;
        if (cmd.getBody() != null) {
            bodyLength = cmd.getBody().length;
        }

        // 1> header length size
        int length = 4;

        // 2> header data length
        byte[] headerData;
        headerData = headerEncode(cmd);

        length += headerData.length;

        // 3> body data length
        length += bodyLength;

        ByteBuffer result = ByteBuffer.allocate(4 + length - bodyLength);

        // length
        result.putInt(length);

        // header length
        result.put(markProtocolType(headerData.length, cmd.getSerializeType()));

        // header data
        result.put(headerData);

        result.flip();

        return result;
    }

    private static int getHeaderLength(int length) {
        return length & 0xFFFFFF;
    }

    private static SerializeType getProtocolType(int source) {
        return SerializeType.valueOf((byte) ((source >> 24) & 0xFF));
    }

    public static byte[] markProtocolType(int source, SerializeType type) {
        byte[] result = new byte[4];
        result[0] = type.getCode();
        result[1] = (byte) ((source >> 16) & 0xFF);
        result[2] = (byte) ((source >> 8) & 0xFF);
        result[3] = (byte) (source & 0xFF);
        return result;
    }

    private static byte[] headerEncode(RemotingCommand cmd) {
        return SerializerSelector.getInstance()
                .select(cmd.getSerializeType().getCode())
                .serialize(cmd);
    }

    private static RemotingCommand headerDecode(byte[] headerData, SerializeType type) {
        RemotingCommand result = SerializerSelector.getInstance().select(type.getCode())
                .deserialize(headerData, RemotingCommand.class);
        result.setSerializeType(type);
        return result;
    }
}
