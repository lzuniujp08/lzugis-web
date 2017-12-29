package com.lzugis.helper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.lzugis.services.model.Capital;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class KmlHelper {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        KmlHelper kmlHelper  = new KmlHelper();
        String filePath = "D:/capital.kml";
        kmlHelper.generateKml(filePath);
        System.out.println(System.currentTimeMillis()-start);
    }

    public void generateKml(String filePath) {
        try {
            List<Capital> list = readCsvData();
            Element root = DocumentHelper.createElement("kml");  //根节点是kml
            Document doc = DocumentHelper.createDocument(root);
            //给根节点kml添加子节点  Document
            Element documentElement = root.addElement("Document");
            documentElement.addElement("name").addText("省会城市"); //添加name节点

            Element Style = DocumentHelper.createElement("Style");
            Style.addAttribute("id", "myStyle");
            documentElement.add(Style);

            Element IconStyle = DocumentHelper.createElement("IconStyle");
            Style.add(IconStyle);

            Element Icon = DocumentHelper.createElement("Icon");
            Icon.addElement("href").addText("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1614210093,3146638674&fm=27&gp=0.jpg");
            IconStyle.add(Icon);

            Element LineStyle = DocumentHelper.createElement("LineStyle");
            Style.add(LineStyle);
            LineStyle.addElement("color").addText("ff000000");
            LineStyle.addElement("width").addText("4");

            Element PolyStyle = DocumentHelper.createElement("PolyStyle");
            Style.add(PolyStyle);
            PolyStyle.addElement("color").addText("b2ff4d4d");
            PolyStyle.addElement("outline").addText("2");

            Element LabelStyle = DocumentHelper.createElement("LabelStyle");
            Style.add(LabelStyle);
            LabelStyle.addElement("color").addText("ffffffff");


            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                Capital capital = list.get(i);
                Element Placemark = DocumentHelper.createElement("Placemark");
                documentElement.add(Placemark);

                Placemark.addElement("name").addText(capital.getName());

                Placemark.addElement("styleUrl").addText("#myStyle");

                /*点*/
                Element point = DocumentHelper.createElement("Point");
                Placemark.add(point);

                String coordinates = capital.getLon() + "," + capital.getLat()+",0";
                sb.append(coordinates);
                if(i!=list.size()-1)sb.append("\r\n");
                point.addElement("coordinates").addText(coordinates);
            }

            /*线*/
            Element PlacemarkLine = DocumentHelper.createElement("Placemark");
            documentElement.add(PlacemarkLine);

            PlacemarkLine.addElement("name").addText("line");

            PlacemarkLine.addElement("styleUrl").addText("#myStyle");

            Element LineString = DocumentHelper.createElement("LineString");
            PlacemarkLine.add(LineString);

            LineString.addElement("altitudeMode").addText("relativeToGround");
            LineString.addElement("coordinates").addText(sb.toString());

            /*面*/
            Element PlacemarkPolygon = DocumentHelper.createElement("Placemark");
            documentElement.add(PlacemarkPolygon);

            PlacemarkPolygon.addElement("name").addText("polygon");

            PlacemarkPolygon.addElement("styleUrl").addText("#myStyle");

            Element Polygon = DocumentHelper.createElement("Polygon");
            PlacemarkPolygon.add(Polygon);

            Element outerBoundaryIs = DocumentHelper.createElement("outerBoundaryIs");
            PlacemarkPolygon.add(outerBoundaryIs);
            Element LinearRing = DocumentHelper.createElement("LinearRing");
            outerBoundaryIs.add(LinearRing);

            LinearRing.addElement("coordinates").addText("116.240466,39.859252,0 116.240041,39.8592,0 116.236872,39.85798,0 116.240466,39.859252,0");

            //将kml写出本地
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("utf-8");//设置编码格式
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(filePath), format);

            xmlWriter.write(doc);

            xmlWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Capital> readCsvData(){
        String csvfile = "D:\\lzugis\\code\\lzugis\\data\\xls\\capital.csv";
        List result = new ArrayList();
        try {
            File csv = new File(csvfile);  // CSV文件路径
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(csv));
            String line = "";
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] lineData = line.split(",");
                int id = Integer.parseInt(lineData[0]);
                String name=lineData[1];
                double lon = Double.parseDouble(lineData[2]),
                        lat = Double.parseDouble(lineData[3]);
                Capital capital = new Capital(id, name, lon, lat);
                result.add(capital);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
