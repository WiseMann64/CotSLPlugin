package me.wisemann64.soulland.system.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandParser {

    public static Map<String,String> parse(String arg) {
        while (arg.contains("  ")) arg = arg.replace("  "," ");
        Map<String, String> a = new HashMap<>();
        String[] ss = arg.split(" ");
        String key = "";
        boolean readingKey = false;
        boolean readingQuote = false;
        List<String> quoted = new ArrayList<>();
        for (String s : ss) {
            if (!readingKey) if (s.charAt(0) == '-') {
                key = s.substring(1);
                readingKey = true;
                continue;
            }
            if (readingKey) {
                if (s.charAt(0) != '"') {
                    a.put(key,s);
                    readingKey = false;
                    continue;
                }
                if (s.charAt(s.length()-1) == '"') {
                    a.put(key,s.substring(1,s.length()-1));
                    readingKey = false;
                    continue;
                }
                readingQuote = true;
                readingKey = false;
                quoted = new ArrayList<>();
                quoted.add(s.substring(1));
                continue;
            }
            if (readingQuote) {
                if (s.charAt(s.length()-1) != '"') {
                    quoted.add(s);
                    continue;
                }
                quoted.add(s.substring(0,s.length()-1));
                readingQuote = false;
                StringBuilder t = new StringBuilder(quoted.get(0));
                for (int i = 1; i < quoted.size(); i++) t.append(" ").append(quoted.get(i));
                a.put(key,t.toString());
            }
        }
        return a;
    }

    public static String appender(String[] args) {
        if (args.length == 0) return "";
        if (args.length == 1) return args[0];
        StringBuilder a = new StringBuilder(args[0]);
        for (int i = 1 ; i < args.length ; i++) a.append(" ").append(args[i]);
        return a.toString();
    }
}
