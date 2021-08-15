package com.company;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        scrape(args[0], Integer.valueOf(args[1]));
    }

    private static void toCSV(ArrayList<HashMap<String, String>> list) {
        List<String> headers = list.stream().flatMap(map -> map.keySet().stream()).distinct().collect(Collectors.toList());
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < headers.size(); i++) {
            sb.append(headers.get(i));
            sb.append(i == headers.size()-1 ? "\n" : ",");
        }
        for (HashMap<String, String> map : list) {
            for (int i = 0; i < headers.size(); i++) {
                sb.append(map.get(headers.get(i)));
                sb.append(i == headers.size()-1 ? "\n" : ",");
            }
        }
        try {
            FileUtils.writeStringToFile(new File("jdData.csv"), sb.toString(),"utf-8");
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void scrape(String url, Integer pages) {
        Integer pageNumber = 1;
        ArrayList<HashMap<String, String>> ret = new ArrayList<>();
        while (pageNumber < pages) {
            String link = url+"/page-" + pageNumber.toString();
            String body = Requests.get(link);
            Pattern pattern = Pattern.compile("#Cards start here(.*?)#Cards Ends Here", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(body);
            ArrayList<String> allMatches = new ArrayList<>();
            while (matcher.find()) {
                String match = matcher.group();
                allMatches.add(match);
            }
            for (String match : allMatches) {
                HashMap<String, String> temp = new HashMap<>();
                temp.put("Name", getName(match));
                temp.put("Phone", getPhone(match));
                temp.put("Rating", getRating(match));
                temp.put("Address", getAddress(match));
                temp.put("Location", getLocation(match));
                temp.put("City", getCity(match));
                temp.put("Image", getImage(match));
                ret.add(temp);
            }
            pageNumber++;
        }
        toCSV(ret);
    }

    private static String getName(String match) {
        String name = getMatch("title=\"(.*?)\">",match);
        return name.replace("title=\"","").replace("\">","");
    }

    private static String getImage(String match) {
        String imageUrl = getMatch("data-src=\"(.*?)fit=around", match);
        imageUrl = imageUrl.replace("data-src=\"","").replace("fit=around","");
        return imageUrl.substring(0,imageUrl.length()-1);
    }

    private static String getAddress(String match) {
        String address = getMatch("<span class=\"cont_fl_addr\">(.*?)</span>", match);
        address = address.replace("<span class=\"cont_fl_addr\">","").replace("</span>","");
        return address;
    }

    private static String getLocation(String match) {
        String location = getName(match);
        return location.split(" in ")[1].split(", ")[0];
    }

    private static String getCity(String match) {
        String location = getName(match);
        return location.split(" in ")[1].split(", ")[1];
    }

    private static String getRating(String match) {
         String rating = getMatch("<span class=\"exrt_count\">(.*?)</span>",match);
         rating = rating.replace("<span class=\"exrt_count\"> ","").replace("</span>","");
         return rating;
    }

    private static String getMatch(String patternStr, String match) {
        String ret = "Not Found";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(match);
        if (matcher.find()) {
            ret = matcher.group();;
        }
        return ret;
    }

    private static String getPhone(String match) {
        String phone = getMatch("<i class=\"res_contactic resultimg\"></i><span><a>(.*?)</a></span>",match);
        phone = phone.replace("<span class=\"","");
        phone = phone.replace("\"></span><span class=\"","");
        phone = phone.replace("\"></span>'","");
        phone = phone.replace("mobilesv icon-dc","");
        phone = phone.replace("mobilesv icon-fe","");
        phone = phone.replace("mobilesv icon-hg","");
        phone = phone.replace("mobilesv icon-ba","");
        phone = phone.replace("mobilesv icon-ji","9");
        phone = phone.replace("mobilesv icon-yz","1");
        phone = phone.replace("mobilesv icon-rq","5");
        phone = phone.replace("mobilesv icon-wx","2");
        phone = phone.replace("mobilesv icon-lk","8");
        phone = phone.replace("mobilesv icon-acb","0");
        phone = phone.replace("mobilesv icon-ts","4");
        phone = phone.replace("mobilesv icon-nm","7");
        phone = phone.replace("mobilesv icon-vu","3");
        phone = phone.replace("mobilesv icon-po","6");
        phone = phone.replace("<b>","").replace("\"","").replace("</span>","").replace("/","").replace("<","").replace(">","").replace("b","");
        phone = phone.replace("i class=res_contactic resultimgispana","").replace("a","");
        return phone;
    };
}
