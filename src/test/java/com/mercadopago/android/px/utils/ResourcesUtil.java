package com.mercadopago.android.px.utils;

import java.io.InputStream;
import java.util.Scanner;

public final class ResourcesUtil {

    private ResourcesUtil() {
    }

    public static String getStringResource(final String fileName) {
        String resource;
        try {
            final InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName);
            final Scanner scanner = new Scanner(is).useDelimiter("\\A");
            resource = scanner.hasNext() ? scanner.next() : "";
        } catch (final Exception e) {
            resource = "";
        }
        return resource;
    }
}
