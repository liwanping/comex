package org.yushu.comex.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;

/**
 * Kryo Utils
 * 
 * @author kongming.lrq
 */
public class KryoUtils {

    private static final ThreadLocal<Kryo> KRYOS = new ThreadLocal<Kryo>() {
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            kryo.setReferences(false);
            return kryo;
        }
    };

    private KryoUtils() {
    }

    /**
     * @return
     */
    public static Kryo getKryo() {
        return KRYOS.get();
    }
}
