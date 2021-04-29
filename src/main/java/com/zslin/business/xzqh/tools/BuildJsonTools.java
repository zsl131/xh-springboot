package com.zslin.business.xzqh.tools;

import com.zslin.business.xzqh.dto.DivisionDto;
import com.zslin.business.xzqh.dto.DivisionExcelDto;
import com.zslin.business.xzqh.dto.DivisionResultDto;
import com.zslin.business.xzqh.dto.DivisionSingleDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成行政区划JSON数据的工具类
 */
public class BuildJsonTools {

    public static List<DivisionResultDto> buildDivisionJson(String fileName) {
        List<DivisionResultDto> result = new ArrayList<>();
        List<DivisionExcelDto> list = buildList(fileName);
        String provinceCode = "", cityCode = "";
        DivisionDto cityDto = null;
        DivisionResultDto provinceDto = null;
        System.out.println("-------->"+list.get(list.size()-1));
        for(DivisionExcelDto dto : list) {
             if(!cityCode.equals(dto.getCityCode())) { //如果是另一个市
                if(cityDto!=null) {provinceDto.add(cityDto);}
                cityDto = new DivisionDto(dto.getCity(), dto.getCityCode());
            }
            if(!provinceCode.equals(dto.getProvinceCode())) { //另一个省
                //provinceCode = dto.getProvinceCode(); cityCode = dto.getCityCode();
                if(provinceDto!=null) {result.add(provinceDto);}
                provinceDto = new DivisionResultDto(dto.getProvince(), dto.getProvinceCode());
                //cityDto = new DivisionDto(dto.getCity(), dto.getCityCode());
            }
            //provinceDto = new DivisionResultDto(dto.getProvince(), dto.getProvinceCode());
            //cityDto = new DivisionDto(dto.getCity(), dto.getCityCode());
            cityDto.add(new DivisionSingleDto(dto.getCounty(), dto.getCountyCode()));
            provinceCode = dto.getProvinceCode(); cityCode = dto.getCityCode();
            //sb.append("");
        }
        if(cityDto!=null && provinceDto!=null) {provinceDto.add(cityDto); result.add(provinceDto);}

        return result;
    }

    public static List<DivisionExcelDto> buildList(String fileName) {
        List<DivisionExcelDto> result = new ArrayList<>();
        try {
            String path = ClassLoader.getSystemResource(fileName).getPath();
            FileInputStream fis = new FileInputStream(new File(path));
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            for(int i=1;i<=sheet.getLastRowNum();i++) {
                Row row = sheet.getRow(i);
                result.add(buildDto(row));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static DivisionExcelDto buildDto(Row row) {
        DivisionExcelDto dto = new DivisionExcelDto();
        dto.setProvince(getCellStringValue(row, 0));
        dto.setProvinceCode(getCellStringValue(row, 1));
        dto.setCity(getCellStringValue(row, 2));
        dto.setCityCode(getCellStringValue(row, 3));
        dto.setCounty(getCellStringValue(row, 4));
        dto.setCountyCode(getCellStringValue(row, 5));
        return dto;
    }

    private static String getCellStringValue(Row row, Integer cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if(cell!=null) {
            if(cell.getCellType()==CellType.NUMERIC) {
                return removeDot(cell.getNumericCellValue());
            } else {
                return cell.getStringCellValue();
            }
        }
        else {return "";}
    }

    private static String removeDot(Double value) {
        try {
            int d = (int)Math.floor(value);
            return d+"";
        } catch (Exception e) {
            return value+"";
        }
    }
}
