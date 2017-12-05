package com.lzugis.web.servlet;

import com.lzugis.helper.CommonConfig;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.swing.JMapFrame;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/9/10.
 */
@WebServlet(description = "wms services", urlPatterns =  {"/wms"}, loadOnStartup=1)
public class WmsServiceServlet extends HttpServlet {
    private static MapContent map = null;

    public WmsServiceServlet() {
        super();
        try{
            ImageIO.scanForPlugins();
            String shpPath = "", sldPath = "";
            shpPath = CommonConfig.getVal("wms.shp");
            sldPath = CommonConfig.getVal("wms.sld");
            map = new MapContent();
            this.addShapeLayer(shpPath, sldPath);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String LAYERS = request.getParameter("LAYERS"),
                WIDTH = request.getParameter("WIDTH"),
                HEIGHT = request.getParameter("HEIGHT"),
                BBOX = request.getParameter("BBOX");

        int _w = Integer.parseInt(WIDTH),
                _h = Integer.parseInt(HEIGHT);

        String[] BBOXS = BBOX.split(",");
        double[] _bbox = new double[]{
                Double.parseDouble(BBOXS[0]),
                Double.parseDouble(BBOXS[1]),
                Double.parseDouble(BBOXS[2]),
                Double.parseDouble(BBOXS[3])
        };
        Map paras = new HashMap();
        paras.put("bbox", _bbox);
        paras.put("width", _w);
        paras.put("height", _h);
        this.getMapContent(paras, response);
    }

    private void addShapeLayer(String shpPath, String sldPath){
        try {
            File file = new File(shpPath);
            ShapefileDataStore shpDataStore = null;
            shpDataStore = new ShapefileDataStore(file.toURL());
            //设置编码
            Charset charset = Charset.forName("GBK");
            shpDataStore.setCharset(charset);
            String typeName = shpDataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = null;
            featureSource = shpDataStore.getFeatureSource(typeName);

            Style style = SLD.createSimpleStyle(featureSource.getSchema());
            if (sldPath != "") {
                //SLD的方式
                File sldFile = new File(sldPath);
                StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
                SLDParser stylereader = new SLDParser(styleFactory, sldFile.toURI().toURL());
                Style[] stylearray = stylereader.readXML();
                style = stylearray[0];
            } else {
                SLD.setPolyColour(style, Color.RED);
            }

            Layer layer = new FeatureLayer(featureSource, style);
            map.addLayer(layer);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void getMapContent(Map paras, HttpServletResponse response){
        try{
            double[] bbox = (double[]) paras.get("bbox");
            double x1 = bbox[0], y1 = bbox[1],
                    x2 = bbox[2], y2 = bbox[3];
            int width = (Integer) paras.get("width"),
                    height=(Integer) paras.get("height");

            // 设置输出范围
            CoordinateReferenceSystem crs = CRS.decode("EPSG:3857");
            ReferencedEnvelope mapArea = new ReferencedEnvelope(x1, x2, y1, y2, crs);
            // 初始化渲染器
            StreamingRenderer sr = new StreamingRenderer();
            sr.setMapContent(map);
            // 初始化输出图像
            BufferedImage bi = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.getGraphics();
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Rectangle rect = new Rectangle(0, 0, width, height);
            // 绘制地图
            sr.paint((Graphics2D) g, rect, mapArea);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            boolean flag = ImageIO.write(bi, "png", out);
            byte[] wmsByte = out.toByteArray();

            OutputStream os = response.getOutputStream();
            InputStream is = new ByteArrayInputStream(wmsByte);
            try {
                int count = 0;
                byte[] buffer = new byte[1024 * 1024];
                while ((count = is.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                }
                os.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                os.close();
                is.close();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        long start = System.currentTimeMillis();

        WmsServiceServlet shp2img = new WmsServiceServlet();

        String shpPath = "D:/bj_grid/GeoServer 2.11.0/data_dir/bj_grid/layer_lines.shp";
        String sldPath = "D:/bj_grid/GeoServer 2.11.0/data_dir/styles/layer_lines.sld";
        shp2img.addShapeLayer(shpPath, sldPath);
        JMapFrame.showMap(map);
        System.out.println("共耗时"+(System.currentTimeMillis() - start)+"ms");
    }
}