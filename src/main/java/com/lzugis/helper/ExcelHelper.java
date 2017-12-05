package com.lzugis.helper;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.util.List;
import java.util.Map;

public class ExcelHelper {

    public void write2ExcelFile(String xlsPath, String[] header, List data){
        //第一步，创建一个workbook对应一个excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        //第二部，在workbook中创建一个sheet对应excel中的sheet
        HSSFSheet sheet = workbook.createSheet("poi_data");
        //第三部，在sheet表中添加表头第0行，老版本的poi对sheet的行列有限制
        HSSFRow row = sheet.createRow(0);

        //创建头
        for(int i=0;i<header.length;i++){
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(header[i]);
        }
        //填充数据
        for(int i=0;i<data.size();i++){
            HSSFRow rowD = sheet.createRow(i + 1);
            Map<String, Object> _row = (Map)data.get(i);
            int index = 0;
            for (Map.Entry entry : _row.entrySet()) {
                rowD.createCell(index).setCellValue(entry.getValue().toString());
                index++;
            }
        }
        //将文件保存到指定的位置
        try {
            FileOutputStream fos = new FileOutputStream(xlsPath);
            workbook.write(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void convertCsv2Excel(String csvPath, String xlsPath){
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Sheet1");
        BufferedReader r = null;
        try{
            r = new BufferedReader(new FileReader(csvPath));
            int i = 0;
            while (true){
                String ln = r.readLine();
                if (ln == null)
                    break;
                HSSFRow row = sheet.createRow((short) i++);
                int j = 0;
                for (CSVTokenizer it = new CSVTokenizer(ln); it.hasMoreTokens();){
                    String val = it.nextToken();
                    HSSFCell cell = row.createCell((short) j++);
                    cell.setCellValue(val);
                }
            }
            r.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        //输出文件
        FileOutputStream fileOut = null;
        try
        {
            fileOut = new FileOutputStream(xlsPath);
            wb.write(fileOut);
            fileOut.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
}
