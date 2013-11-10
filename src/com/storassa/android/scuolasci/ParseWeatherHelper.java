package com.storassa.android.scuolasci;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ParseWeatherHelper {

   String page;
   double minSnow = 0, maxSnow = 0;
   String lastSnow = "";

   public ParseWeatherHelper(String _page) {
      page = _page;

      try {
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory
               .newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.parse(new InputSource(
               new ByteArrayInputStream(page.getBytes("utf-8"))));

         doc.getDocumentElement().normalize();

         NodeList minList = doc.getElementsByTagName("lower_snow_depth");
         NodeList maxList = doc.getElementsByTagName("upper_snow_depth");
         NodeList lastList = doc.getElementsByTagName("last_snow_date");

         minSnow = Double.parseDouble(minList.item(0).getFirstChild()
               .getTextContent());
         maxSnow = Double.parseDouble(maxList.item(0).getFirstChild()
               .getTextContent());
         lastSnow = lastList.item(0).getFirstChild().getTextContent();

         // for (int i = 0; i < nList.getLength(); i++) {
         // Node nNode = nList.item(i);
         // if (reachedSnowReport(nNode)) {
         // String min = ((Element) nNode).getElementsByTagName(
         // "LOWER_SNOW_DEPTH").item(0).getTextContent();
         // String max = ((Element) nNode).getElementsByTagName(
         // "UPPER_SNOW_DEPTH").item(0).getTextContent();
         //
         // minSnow = Double.parseDouble(min);
         // maxSnow = Double.parseDouble(max);
         // }
         // }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public double getMinSnow() {
      return minSnow;
   }

   public double getMaxSnow() {
      return maxSnow;
   }

   public String getLastSnow() {
      return lastSnow;
   }

   public static void main(String[] args) {
      ParseWeatherHelper helper = new ParseWeatherHelper(args[0]);
      System.out.println("Max: " + helper.getMaxSnow());
      System.out.println("Min: " + helper.getMinSnow());
   }
}
