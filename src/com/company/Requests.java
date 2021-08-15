package com.company;

import org.jsoup.*;
import org.jsoup.nodes.Document;

public class Requests {
    public static String get(String url) {
        Document page = new Document("null");
        try {
            page = Jsoup.connect(url).get();
        } catch (Exception ex) {
            ex.printStackTrace();
        };
        return page.body().toString();
    };
}
