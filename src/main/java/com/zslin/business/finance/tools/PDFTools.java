package com.zslin.business.finance.tools;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.zslin.business.finance.model.FinanceDetail;
import com.zslin.business.finance.model.FinanceRecord;
import com.zslin.business.finance.model.FinanceTicket;
import com.zslin.core.tools.ConfigTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by zsl on 2019/1/5.
 */
@Component
public class PDFTools {

    @Autowired
    private ConfigTools configTools;

    private static int TABLE_HEIGHT = 30; //表格高度
    private static int MAX_DETAIL_LEN = 6; //最多6条数据
    private float chapWidth = 0f, chapHeight = 0f;

    public void buildPDF(OutputStream os, FinanceRecord record, List<FinanceDetail> detailList, List<FinanceTicket> ticketList) {
        String ticketNo = record.getTicketNo();
        try {
            //设置纸张大小
            Document document = new Document(PageSize.A5.rotate());
            //建立一个书写器，与document对象关联
            PdfWriter writer = PdfWriter.getInstance(document, os);

            document.open();

            writePage(writer, ticketNo);
//            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("123456789", getFont(10)), document.left(), document.top()+2, 0);
            //将章节二纵向显示
            document.setPageSize(PageSize.A5.rotate());
            chapWidth = PageSize.A5.getWidth();
            chapHeight = PageSize.A5.getHeight();
            Chapter chap = new Chapter(1);
            chap.add(buildHeadParagraph(record.getFlag(), ticketNo)); //标题
            chap.add(buildBlankP()); //空行
            chap.add(buildNameDateP(record.getRecordDate())); //名称日期
            chap.add(buildBlankP()); //空行
            chap.add(buildTable(record, detailList)); //数据表格

            document.add(chap);

//            writePage(writer, document, ticketNo);

            for(FinanceTicket ticket:ticketList) {
                document.add(buildImageChap(ticket));
                //writePage(writer, document, ticketNo);
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writePage(PdfWriter writer, String ticketNo) {
        /*PdfContentByte headAndFootPdfContent = writer.getDirectContent();
        headAndFootPdfContent.saveState();
        headAndFootPdfContent.beginText();
        headAndFootPdfContent.setFontAndSize(getBaseFont(), 8);
//        headAndFootPdfContent.setColor
        //文档页头信息设置  
        float x = document.top(-20);
        float x1 = document.top(-5);
        //页头信息中间  
        headAndFootPdfContent.showTextAligned(PdfContentByte.ALIGN_LEFT, "编号："+ticketNo, (document.right() + document.left()) / 2, x, 0);
//            headAndFootPdfContent.showTextAligned(Element.ALIGN_CENTER, "123456789", document.left(), document.top()+2, 0);
        headAndFootPdfContent.endText();
        headAndFootPdfContent.restoreState();*/

        writer.setPageEvent(new PdfPageEventHelper() {

            @Override
            public void onStartPage(PdfWriter pdfWriter, Document document) {
                PdfContentByte headAndFootPdfContent = pdfWriter.getDirectContent();
                headAndFootPdfContent.saveState();
                headAndFootPdfContent.beginText();
                headAndFootPdfContent.setFontAndSize(getBaseFont(), 8);
                //文档页头信息设置  
                float x = document.top(-20);
                //页头信息中间  
                headAndFootPdfContent.showTextAligned(PdfContentByte.ALIGN_LEFT, "编号："+ticketNo, document.left(), x, 0);
//            headAndFootPdfContent.showTextAligned(Element.ALIGN_CENTER, "123456789", document.left(), document.top()+2, 0);
                headAndFootPdfContent.endText();
                headAndFootPdfContent.restoreState();
            }

            @Override
            public void onEndPage(PdfWriter pdfWriter, Document document) {

            }
        });
    }

    private Chapter buildImageChap(FinanceTicket ticket) {
        try {
            Chapter chap = new Chapter(1);

            Image image = Image.getInstance(configTools.getFilePath()+ticket.getPicUrl());
            float w = image.getWidth(), h = image.getHeight();
            float scalePercentage = (chapHeight / w) * 90.0f;
            image.scalePercent(scalePercentage, scalePercentage);
//            System.out.println("width::"+w+", Height::"+h+", CW:"+chapWidth+", CH:"+chapHeight+", scale:"+scalePercentage);
            image.setAlignment(Image.MIDDLE);
            image.setPaddingTop(-100);
            chap.add(image);
            return chap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private PdfPTable buildTable(FinanceRecord record, List<FinanceDetail> detailList) {
//        float [] widths = {56f,220,57.5f,52f,28,52f,49f};
        float [] widths = {56f,210,57.5f,42f,39, 29,53f,28f};
        float totalWidth = 0f;
        for(float w : widths) {totalWidth += w;}
        PdfPTable table = new PdfPTable(widths);
        table.setTotalWidth(totalWidth);
        table.setLockedWidth(true);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        String [] header = {"日期", "摘要（简要说明）", "会计科目", "经办人", "单价(元)", "数量", "金额(元)", "附单(张)"};
        for(String h : header) {
            PdfPCell pdfCell = new PdfPCell(); //表格的单元格
            pdfCell.setMinimumHeight(TABLE_HEIGHT);
            pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            Paragraph paragraph = new Paragraph(h, getFont(12));
            pdfCell.setPhrase(paragraph);
            table.addCell(pdfCell);
        }
        Integer totalCount = 0;
        Float totalMoney = 0f;
        int len = 0;
        for(FinanceDetail fd : detailList) {
            len ++;
            Float total = fd.getAmount(); //小计金额
            Integer count = fd.getTicketCount(); //单据张数
            String [] data = {rebuildDate(fd.getRecordDate()), fd.getTitle(), fd.getCateName(), fd.getHandleName(),
                    formatMoney(fd.getPrice()), fd.getCount()+"", formatMoney(total), count+""};
            totalCount += count; totalMoney += total;

            int index = 0;
            for(String h : data) {
                PdfPCell pdfCell = new PdfPCell(); //表格的单元格
                pdfCell.setMinimumHeight(TABLE_HEIGHT);
                if(index==1) {
                    pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                } else {
                    pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                Paragraph paragraph ;
                if(index == 6) {
                    Font f = getFont(12);
                    f.setStyle("bold");
                    paragraph = new Paragraph(h, f);
                } else {
                    paragraph = new Paragraph(h, getFont(12));
                }
                pdfCell.setPhrase(paragraph);
                table.addCell(pdfCell);
                index ++;
            }
        }

        for(int i=0;i<(MAX_DETAIL_LEN-len);i++) {
            String [] data = {"", "", "", "", "", "", "", ""};

            int index = 0;
            for(String h : data) {
                PdfPCell pdfCell = new PdfPCell(); //表格的单元格
                pdfCell.setMinimumHeight(TABLE_HEIGHT);
                if(index==1) {
                    pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                } else {
                    pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                Paragraph paragraph ;
                if(index == 6) {
                    Font f = getFont(12);
                    f.setStyle("bold");
                    paragraph = new Paragraph(h, f);
                } else {
                    paragraph = new Paragraph(h, getFont(12));
                }
                pdfCell.setPhrase(paragraph);
                table.addCell(pdfCell);
                index ++;
            }
        }

        table.addCell(buildHeji()); //合计
        table.addCell(buildTotalMoney(totalMoney)); //大写金额
        table.addCell(buildTotalMoneyNum(totalMoney)); //小写金额
        table.addCell(buildTotalCount(totalCount)); //总单据张数
        table.addCell(buildApply(record.getRecordName())); //申请人
        table.addCell(buildVerify(record.getVerifyName())); //审核人
        table.addCell(buildAdmin("")); //主管
        return table;
    }

    private PdfPCell buildAdmin(String adminName) {
        PdfPCell pdfCell = new PdfPCell(); //表格的单元格
        pdfCell.setMinimumHeight(TABLE_HEIGHT);
        pdfCell.setRowspan(1);
        pdfCell.setColspan(2);
        pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Paragraph p = new Paragraph();
        Chunk c = new Chunk("主管：", getFont(12));
        Chunk c2 = new Chunk(adminName, getFont(12));
        p.add(c);
        p.add(c2);
        pdfCell.setPhrase(p);
        return pdfCell;
    }

    private PdfPCell buildApply(String applyName) {
        PdfPCell pdfCell = new PdfPCell(); //表格的单元格
        pdfCell.setMinimumHeight(TABLE_HEIGHT);
        pdfCell.setRowspan(1);
        pdfCell.setColspan(2);
        pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Paragraph p = new Paragraph();
        Chunk c = new Chunk("申请人：", getFont(12));
        Chunk c2 = new Chunk(applyName, getFont(12));
        p.add(c);
        p.add(c2);
        pdfCell.setPhrase(p);
        return pdfCell;
    }

    private PdfPCell buildVerify(String verifyName) {
        PdfPCell pdfCell = new PdfPCell(); //表格的单元格
        pdfCell.setMinimumHeight(TABLE_HEIGHT);
        pdfCell.setRowspan(1);
        pdfCell.setColspan(3);
        pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Paragraph p = new Paragraph();
        Chunk c = new Chunk("审核人：", getFont(12));
        Chunk c2 = new Chunk(verifyName, getFont(12));
        p.add(c);
        p.add(c2);
        pdfCell.setPhrase(p);
        return pdfCell;
    }

    private PdfPCell buildTotalCount(int count) {
        PdfPCell pdfCell = new PdfPCell(); //表格的单元格
        pdfCell.setMinimumHeight(TABLE_HEIGHT);
        pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Paragraph paragraph = new Paragraph(count+"", getFont(12));
        pdfCell.setPhrase(paragraph);
        return pdfCell;
    }

    private PdfPCell buildTotalMoneyNum(float totalMoney) {
        PdfPCell pdfCell = new PdfPCell(); //表格的单元格
        pdfCell.setMinimumHeight(TABLE_HEIGHT);
        pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Font f = getFont(12);
        f.setStyle("bold");
        Paragraph paragraph = new Paragraph(formatMoney(totalMoney), f);
        pdfCell.setPhrase(paragraph);
        return pdfCell;
    }

    private PdfPCell buildTotalMoney(float totalMoney) {
        totalMoney = Float.parseFloat(formatMoney(totalMoney)); //转换成2位小数
        PdfPCell pdfCell = new PdfPCell(); //表格的单元格
        pdfCell.setRowspan(1);
        pdfCell.setColspan(5);
        pdfCell.setMinimumHeight(TABLE_HEIGHT);
        pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Paragraph p = new Paragraph();
        Chunk c = new Chunk("（大写）", getFont(12));
        Chunk c2 = new Chunk(MoneyTools.digitUppercase(totalMoney), getFont(12));
        p.add(c);
        p.add(c2);
        pdfCell.setPhrase(p);
        return pdfCell;
    }

    private PdfPCell buildHeji() {
        PdfPCell pdfCell = new PdfPCell(); //表格的单元格
        pdfCell.setMinimumHeight(TABLE_HEIGHT);
        pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Paragraph paragraph = new Paragraph("合计", getFont(12));
        pdfCell.setPhrase(paragraph);
        return pdfCell;
    }

    private String formatMoney(float d) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(d);
    }

    private String rebuildDate(String recordDate) {
        StringBuffer sb = new StringBuffer();
        sb.append(recordDate.substring(0, 4)).append(".").append(recordDate.substring(4, 6)).
                append(".").append(recordDate.substring(6, 8));
        return sb.toString();
    }

    private Paragraph buildBlankP() {
        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_LEFT);
        Chunk c = new Chunk("  ", getFont(12));
        p.add(c);
        return p;
    }

    private Paragraph buildNameDateP(String recordDate) {
        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_LEFT);
        Chunk c = new Chunk("公司名称：知满农产品（昭通）有限公司", getFont(12));
        Chunk cb = new Chunk("                                                                 ", getFont(12));
        Chunk cd = new Chunk(buildDate(recordDate), getFont(12));
        p.add(c);
        p.add(cb);
        p.add(cd);
        return p;
    }

    private String buildDate(String recordDate) {
        StringBuffer sb = new StringBuffer();
        String spe = "    ";
        sb.append(recordDate.substring(0, 4)).append(spe).append("年")
                .append(spe).append(recordDate.substring(4, 6)).append(spe).append("月")
                .append(spe).append(recordDate.substring(6, 8)).append(spe).append("日");
        return sb.toString();
    }

    private Paragraph buildHeadParagraph(String titleFlag, String ticketNo) {
        Paragraph titleP = new Paragraph();
        titleP.setAlignment(Element.ALIGN_RIGHT);
        Font titleFont = new Font(getBaseFont(), 24);
//            titleFont.setStyle("bold");
        titleFont.setStyle("underline");
        String titleStr = "1".equals(titleFlag)?"  收 益 入 帐 单  ":"  费 用 报 销 单  ";
        Chunk titleC = new Chunk(titleStr, titleFont);
        Chunk black = new Chunk("                    ", getFont(16));
        Chunk ticketC = new Chunk("编号：", getFont(12));
        Font f2 = getFont(16);
        f2.setStyle("underline");
        Chunk ticketC2 = new Chunk("  "+ticketNo+"  ", f2);

        Chunk ticketC3 = new Chunk("号", getFont(12));

        titleP.add(titleC);
        titleP.add(black);
        titleP.add(ticketC);
        titleP.add(ticketC2);
        titleP.add(ticketC3);
        return titleP;
    }

    private BaseFont getBaseFont(){
        try {
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            return bfChinese;
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Font getFont(int size) {
        try {
            BaseFont bfChinese = getBaseFont();
            Font font = new Font(bfChinese, size);
            return font;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
