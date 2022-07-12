package ohCahe;

import org.caffinitas.ohc.CacheSerializer;
import org.caffinitas.ohc.Eviction;
import org.caffinitas.ohc.OHCache;
import org.caffinitas.ohc.OHCacheBuilder;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * 模拟使用堆外内存存储一个大对象
 */
public class OHCacheDemo {
    public static CacheSerializer<String> stringCacheSerializer = new CacheSerializer<String>() {
        @Override
        public void serialize(String value, ByteBuffer buf) {
            buf.put(value.getBytes());
        }

        @Override
        public String deserialize(ByteBuffer buf) {
            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);
            return new String(bytes);
        }

        @Override
        public int serializedSize(String value) {
            return value.getBytes().length;
        }
    };

    public static CacheSerializer<Float[]> bigCacheSerializer = new CacheSerializer<Float[]>() {
        @Override
        public void serialize(Float[] value, ByteBuffer buf) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(value);
                buf.put(byteArrayOutputStream.toByteArray());

                objectOutputStream.close();
                byteArrayOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Float[] deserialize(ByteBuffer buf) {
            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);
            try {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                Float[] big = (Float[]) objectInputStream.readObject();
                objectInputStream.close();
                byteArrayInputStream.close();
                return big;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public int serializedSize(Float[] value) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(value);
                int length = byteArrayOutputStream.toByteArray().length;

                objectOutputStream.close();
                byteArrayOutputStream.close();
                return length;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        }
    };



    public static void main(String[] args) {
        OHCache<String, Float[]> ohCache = OHCacheBuilder.<String, Float[]>newBuilder()
                .keySerializer(stringCacheSerializer)
                .valueSerializer(bigCacheSerializer)
                //单位是byte，需要自己计算大小, 设置成20M
                .capacity(167772160)
                //数据不过期
                .eviction(Eviction.NONE)
                .throwOOME(true)
                .build();

        Float[] floats = new Float[768];
        floats[0] = 1.0f;
        floats[1] = 2.0f;
        floats[2] = 3.0f;


        ohCache.put("007", floats);
//        ohCache.get("007").setName("008");

        System.out.println(ohCache.get("007")[0]);
    }
}


class Big implements Serializable {
    private String name;
    private Float[] bytes = new Float[768];

    {
        bytes[0] = 1.0f;
        bytes[1] = 2.0f;
        bytes[2] = 3.0f;
    }

    public Big(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Big{" +
                "name='" + name + '\'' +
                "bytes[0]" + bytes[0] +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }
}