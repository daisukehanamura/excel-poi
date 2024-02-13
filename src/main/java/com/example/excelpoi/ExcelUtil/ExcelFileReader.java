package com.example.excelpoi.ExcelUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class ExcelFileReader {

    /*
     * Excelファイルのシートを読み込む処理
     */
    public Sheet readExcel(MultipartFile multipartFile, String sheetName)
            throws IOException {

        // バイナリを取得する
        byte[] bytes = multipartFile.getBytes();
        // バイナリからストリームへ変換
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        // エクセルをワークブックインスタンスへ
        Workbook wb = WorkbookFactory.create(inputStream);
        // シートを取得
        Sheet sheet = wb.getSheet(sheetName);

        return sheet;
    }

    /*
     * Excelファイルをエンティティに置き換え
     */
    public <T> List<Object> readFromXml(Sheet sheet, String xmlFile, Class<T> clazz)
            throws IOException, SAXException, ParserConfigurationException, InstantiationException, 
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, 
            NoSuchMethodException, SecurityException {

        // XMLファイルを読み込み
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        NodeList nodes = doc.getElementsByTagName("definitions");

        // 定義体に基づいてエンティティリストを作成
        List<Object> entities = new ArrayList<>();

        for (Row row : sheet) {
            // 1行目はヘッダーなのでスキップ
            if (row.getRowNum() == 0) {
                continue;
            }

            // エンティティのインスタンスを作成
            Object entity = (Object) clazz.getDeclaredConstructor().newInstance();
            int i = 0;
            for (Cell cell : row) {
                String propertyValue = cell.getStringCellValue();
                Element element = (Element) nodes.item(i);
                String propertyName = element.getFirstChild().getNodeName();
                if (propertyName != null) {
                    // T型で定義したエンティティに値を格納したい
                    BeanUtils.copyProperties(entity, propertyName, propertyValue);
                }
                i++;
            }
            entities.add(entity);
        }

        return entities;
    }
}