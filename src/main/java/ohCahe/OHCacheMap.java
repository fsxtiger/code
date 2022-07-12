package ohCahe;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import org.caffinitas.ohc.CacheSerializer;
import org.caffinitas.ohc.Eviction;
import org.caffinitas.ohc.OHCache;
import org.caffinitas.ohc.OHCacheBuilder;

import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class FloatWrapper {
    String name = "fsx";

    @Override
    public String toString() {
        return "FloatWrapper{" +
                "name='" + name + '\'' +
                '}';
    }
}

public class OHCacheMap<K, V> extends AbstractMap<K, V> {
    private OHCache<K, V> ohCache;
    private static Map<Class, CacheSerializer> clazzToCacheSerializer = new HashMap<>();

    static {
        clazzToCacheSerializer.put(String.class, new StringSerializer());
        clazzToCacheSerializer.put(Integer.class, new IntSerializer());
        clazzToCacheSerializer.put(Double.class, new DoubleSerializer());
        clazzToCacheSerializer.put(Float.class, new FloatSerializer());
        clazzToCacheSerializer.put(Short.class, new ShortSerializer());
        clazzToCacheSerializer.put(Character.class, new Charserializer());
    }

    private <E> CacheSerializer<E> getCacheSerializer(Class<E> clazz) {
        if (clazzToCacheSerializer.containsKey(clazz)) {
            return clazzToCacheSerializer.get(clazz);
        }
        return new ObjectSerializer<>(RuntimeSchema.createFrom(clazz));
    }

    public OHCacheMap(int capacity, Eviction eviction, Boolean throwOOM, Class<K> keyClazz, Class<V> valueClazz) {

        this.ohCache = OHCacheBuilder.<K, V>newBuilder()
                .keySerializer(getCacheSerializer(keyClazz))
                .valueSerializer(getCacheSerializer(valueClazz))
                //单位是byte，需要自己计算大小, 设置成20M
                .capacity(capacity)
                //数据不过期
                .eviction(Eviction.NONE)
                .throwOOME(true)
                .build();
    }

    public OHCacheMap(int capacity, Eviction eviction, Boolean throwOOM, CacheSerializer<K> keySerializer, CacheSerializer<V> valueSerializer) {
        this.ohCache = OHCacheBuilder.<K, V>newBuilder()
                .keySerializer(keySerializer)
                .valueSerializer(valueSerializer)
                //单位是byte，需要自己计算大小, 设置成20M
                .capacity(capacity)
                //数据不过期
                .eviction(Eviction.NONE)
                .throwOOME(true)
                .build();
    }


    @Override
    public V put(K key, V value) {
        V oldValue = this.ohCache.get(key);
        this.ohCache.put(key, value);

        return oldValue;
    }

    @Override
    public V get(Object key) {
        return this.ohCache.get((K) key);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.ohCache.containsKey((K) key);
    }

    @Override
    public boolean isEmpty() {
        return this.ohCache.size() < 1;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }



    public static void main(String[] args) {
        OHCacheMap map = new OHCacheMap(167772160, Eviction.NONE, true, String.class, FloatWrapper.class);
        map.put("abc", new FloatWrapper());

        System.out.println(map.get("abc"));
    }
}

class StringSerializer implements CacheSerializer<String> {
    @Override
    public void serialize(String s, ByteBuffer byteBuffer) {
        byteBuffer.put(s.getBytes());
    }

    @Override
    public String deserialize(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        return new String(bytes);
    }

    @Override
    public int serializedSize(String s) {
        return s.getBytes().length;
    }
}

class LongSerializer implements CacheSerializer<Long> {
    @Override
    public void serialize(Long l, ByteBuffer byteBuffer) {
        byteBuffer.putLong(l);
    }

    @Override
    public Long deserialize(ByteBuffer byteBuffer) {
        return byteBuffer.getLong();
    }

    @Override
    public int serializedSize(Long l) {
        return Long.BYTES;
    }
}

class IntSerializer implements CacheSerializer<Integer> {
    @Override
    public void serialize(Integer i, ByteBuffer byteBuffer) {
        byteBuffer.putInt(i);
    }

    @Override
    public Integer deserialize(ByteBuffer byteBuffer) {
        return byteBuffer.getInt();
    }

    @Override
    public int serializedSize(Integer l) {
        return Integer.BYTES;
    }
}

class DoubleSerializer implements CacheSerializer<Double> {
    @Override
    public void serialize(Double d, ByteBuffer byteBuffer) {
        byteBuffer.putDouble(d);
    }

    @Override
    public Double deserialize(ByteBuffer byteBuffer) {
        return byteBuffer.getDouble();
    }

    @Override
    public int serializedSize(Double l) {
        return Double.BYTES;
    }
}

class FloatSerializer implements CacheSerializer<Float> {
    @Override
    public void serialize(Float f, ByteBuffer byteBuffer) {
        byteBuffer.putFloat(f);
    }

    @Override
    public Float deserialize(ByteBuffer byteBuffer) {
        return byteBuffer.getFloat();
    }

    @Override
    public int serializedSize(Float f) {
        return Float.BYTES;
    }
}

class ShortSerializer implements CacheSerializer<Short> {
    @Override
    public void serialize(Short s, ByteBuffer byteBuffer) {
        byteBuffer.putShort(s);
    }

    @Override
    public Short deserialize(ByteBuffer byteBuffer) {
        return byteBuffer.getShort();
    }

    @Override
    public int serializedSize(Short s) {
        return Short.BYTES;
    }
}

class Charserializer implements CacheSerializer<Character> {
    @Override
    public void serialize(Character c, ByteBuffer byteBuffer) {
        byteBuffer.putChar(c);
    }

    @Override
    public Character deserialize(ByteBuffer byteBuffer) {
        return byteBuffer.getChar();
    }

    @Override
    public int serializedSize(Character ch) {
        return Character.BYTES;
    }
}

class ObjectSerializer<T> implements CacheSerializer<T> {
    private RuntimeSchema<T> schema ;

    public ObjectSerializer(RuntimeSchema<T> schema) {
        this.schema = schema;
    }

    @Override
    public void serialize(T t, ByteBuffer byteBuffer) {

        byte[] bytes = ProtostuffIOUtil.toByteArray(t, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        byteBuffer.put(bytes);
    }

    @Override
    public T deserialize(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);

        T newCrab = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes,newCrab, schema);

        return newCrab;
    }

    @Override
    public int serializedSize(T t) {
        return ProtostuffIOUtil.toByteArray(t, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE)).length;

    }
}

