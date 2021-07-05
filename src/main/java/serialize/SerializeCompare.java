package serialize;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerializeCompare {

    private static RuntimeSchema<TrieNode> schema = RuntimeSchema.createFrom(TrieNode.class);

    public static void main(String[] args) {
        TrieNode node = new TrieNode();

        for (int x = 0; x < 10; x++) {
            objectOutputTream(node);
            protostuffDemo(node);
        }
    }

    public static void objectOutputTream(TrieNode node) {
        long startAt = System.currentTimeMillis();

        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(node);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        System.out.println("object write cost:" + (System.currentTimeMillis() - startAt) + ",size:" + byteArrayOutputStream.toByteArray().length);

        startAt = System.currentTimeMillis();

        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            TrieNode trieNode = (TrieNode)objectInputStream.readObject();
            System.out.println("Hi, trieNode " + trieNode);



        } catch (Exception e) {

        } finally {

        }

        System.out.println("object read cost: " + (System.currentTimeMillis() - startAt));
    }

    public static void protostuffDemo(TrieNode node) {
        long startAt = System.currentTimeMillis();

        /**
         *序列化
         */
        TrieNode crab = new TrieNode();
        crab.childrenV2 = new HashMap<>();
        crab.value = "";

//参数三缓冲器
        byte[] bytes = ProtostuffIOUtil.toByteArray(crab,schema,LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

        System.out.println("puf write cost:" + (System.currentTimeMillis() - startAt) + ", size:" + bytes.length);
        startAt = System.currentTimeMillis();
/**
 *反序列化
 */
// 空对象
        TrieNode newCrab = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes,newCrab,schema);
        System.out.println("Hi, My name is " + newCrab);

        System.out.println("puf read cost:" + (System.currentTimeMillis() - startAt));
    }


}

class TrieNode implements Serializable {
    String value; // 节点字符.


    Map<String, Integer> childrenV2 = new HashMap<>();

    {
        for (int x = 0; x < 144440; x++) {
            childrenV2.put(String.valueOf(x), x);
        }
    }

    double logProb = -0.7447569649747722;
    double logBow = -12.506378183937674;

    @Override
    public String toString() {
        return "TrieNode{" +
                "value='" + value + '\'' +
                ", childrenV2=" + childrenV2.size() +
                ", logProb=" + logProb +
                ", logBow=" + logBow +
                '}';
    }
}