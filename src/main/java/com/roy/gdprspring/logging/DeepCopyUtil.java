package com.roy.gdprspring.logging;

import java.io.*;

/**
 * Utility class for performing deep copy operations.
 */
@SuppressWarnings("unchecked")
public class DeepCopyUtil {

    /**
     * Creates a deep copy of the given array.
     *
     * @param array the array to be deep copied
     * @param <T> the type of the elements in the array
     * @return a deep copy of the given array, or null if an error occurs
     */
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