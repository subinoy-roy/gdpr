package com.roy.gdprspring.logging;

import java.io.*;

@SuppressWarnings("unchecked")
public class DeepCopyUtil {
    public static <T> T[] deepCopy(T[] array) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(array);
            out.flush();
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);
            return (T[]) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
