package com.zslin.business.mini.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 分享图片工具类
 */
@Component
public class ShareImageTools {

    @Autowired
    private QrTools qrTools;

    public BufferedImage createImage(String proTitle, String price, String nickname, String remark,
                            String proImgUrl, String headUrl, String page, String scene) {
        try {
            int proHeight = 460; //+280
            int allHeight = 0;
            int conHeight = 280;
            //产品图片
            BufferedImage proImg = zoomImage(new URL(proImgUrl).openStream(), 800);
//            proHeight = proImg.getHeight()>460?460:proImg.getHeight();
            proHeight = proImg.getHeight();
            if(proHeight>460) {allHeight = proHeight;}
            else {allHeight = proHeight + conHeight;}

            int conStartY = allHeight - conHeight; //内容起始Y轴

            String fontName = "微软雅黑";
            BufferedImage bi = new BufferedImage(800, allHeight, BufferedImage.TYPE_INT_BGR);

            Graphics g = bi.getGraphics();
            g.setColor(new Color(255,255,255));
            g.fillRect(0,0,bi.getWidth(),allHeight); //设置上部份背景色


            //System.out.println("--------->"+proImg.getHeight());

            g.drawImage(proImg, 0,0,null);

            //背景白色，透明度150，0-255
            g.setColor(new Color(255,255,255, 220));
            g.fillRect(0,allHeight-conHeight,bi.getWidth(),conHeight); //设置下部份背景色
            //g.dispose();

            g.setColor(new Color(0,0,0, 100));
            g.drawLine(0, allHeight-conHeight, bi.getWidth(), allHeight-conHeight);

            //小程序码
            BufferedImage qr = zoomImage(qrTools.getQrB(page, scene), 190);
//            BufferedImage qr = qrTools.getQrB(page, scene); 530-460=70
            g.drawImage(qr, 580,conStartY+70,null);

            //满山晴字样
            BufferedImage imgIcon = ImageIO.read(new ClassPathResource("logo-msq.png").getInputStream());
            g.drawImage(imgIcon, 30,conStartY+20,null);

            //头像
            BufferedImage srcImg = null;//ImageIO.read(new URL(headUrl)); //读取图片
            try {
                srcImg = ImageIO.read(new URL(headUrl)); //读取图片
            } catch (Exception e) {
                //默认图片
                srcImg = ImageIO.read(new URL("http://qiniu.qswkx.com/logo-144.jpg")); //读取图片
            }

            BufferedImage newImage=new BufferedImage(90, 90,BufferedImage.TYPE_INT_RGB);
            newImage.createGraphics().drawImage(srcImg.getScaledInstance(90, 90, Image.SCALE_SMOOTH), 0, 0, null);

            g.drawImage(newImage, 30,conStartY+170,null); //630-460=170

            Font font = new Font(fontName, Font.PLAIN, 34);
            g.setColor(new Color(39,39,39));
            g.setFont(font);
            g.drawString(proTitle, 150, conStartY+55); //写字是从下往上的占高度 //515-460=55

            g.setColor(new Color(255, 0, 0));
            g.setFont(new Font(fontName, Font.PLAIN, 50));
            g.drawString(price, 40, conStartY+130); //590-460=130

            g.setColor(new Color(255, 0, 0));
            g.setFont(new Font(fontName, Font.PLAIN, 20));
            g.drawString("起", 40+(buildLen(price)), conStartY+130); //590-460=130

            g.setColor(new Color(100,100,100));
            g.setFont(font);
            g.drawString(nickname, 135, conStartY+210); //670-460=210

            g.setColor(new Color(126,126,126));
            g.setFont(new Font(fontName, Font.PLAIN, 26));
            g.drawString(remark, 135, conStartY+250); //710-460=250

            g.dispose();

//            ImageIO.write(bi, "jpg", new File("D:/temp/test/share.jpg"));
            return bi;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** 生成“起”字的起始位置 */
    private int buildLen(String price) {
        int flag = 32;
        if(price.contains(".")) {flag = 28;}
        return price.length()*flag;
    }

    /*public BufferedImage createImage(String proTitle, String price, String nickname, String remark,
                                     String proImgUrl, String headUrl, String page, String scene) {
        try {
            int proHeight = 460; //+280
            //产品图片
            BufferedImage proImg = zoomImage(new URL(proImgUrl).openStream(), 800);
            proHeight = proImg.getHeight()>460?460:proImg.getHeight();

            String fontName = "微软雅黑";
            BufferedImage bi = new BufferedImage(800, proHeight+280, BufferedImage.TYPE_INT_BGR);

            Graphics g = bi.getGraphics();
            g.setColor(new Color(255,255,255));
            g.fillRect(0,0,bi.getWidth(),proHeight); //设置上部份背景色


            //System.out.println("--------->"+proImg.getHeight());

            g.drawImage(proImg, 0,0,null);


            g.setColor(new Color(255,255,255));
            g.fillRect(0,proHeight,bi.getWidth(),280); //设置下部份背景色
            //g.dispose();


            //小程序码
            BufferedImage qr = zoomImage(qrTools.getQrB(page, scene), 190);
//            BufferedImage qr = qrTools.getQrB(page, scene); 530-460=70
            g.drawImage(qr, 580,proHeight+70,null);

            //满山晴字样
            BufferedImage imgIcon = ImageIO.read(new ClassPathResource("logo-msq.png").getInputStream());
            g.drawImage(imgIcon, 30,proHeight+20,null);

            //头像
            BufferedImage srcImg = ImageIO.read(new URL(headUrl)); //读取图片
            BufferedImage newImage=new BufferedImage(90, 90,BufferedImage.TYPE_INT_RGB);
            newImage.createGraphics().drawImage(srcImg.getScaledInstance(90, 90, Image.SCALE_SMOOTH), 0, 0, null);

            g.drawImage(newImage, 30,proHeight+170,null); //630-460=170

            Font font = new Font(fontName, Font.PLAIN, 34);
            g.setColor(new Color(39,39,39));
            g.setFont(font);
            g.drawString(proTitle, 150, proHeight+55); //写字是从下往上的占高度 //515-460=55

            g.setColor(new Color(255, 0, 0));
            g.setFont(new Font(fontName, Font.PLAIN, 50));
            g.drawString(price, 40, proHeight+130); //590-460=130

            g.setColor(new Color(100,100,100));
            g.setFont(font);
            g.drawString(nickname, 135, proHeight+210); //670-460=210

            g.setColor(new Color(126,126,126));
            g.setFont(new Font(fontName, Font.PLAIN, 26));
            g.drawString(remark, 135, proHeight+250); //710-460=250

            g.dispose();

//            ImageIO.write(bi, "jpg", new File("D:/temp/test/share.jpg"));
            return bi;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    private BufferedImage zoomImage(InputStream is, int w) throws Exception {
        BufferedImage srcImg = ImageIO.read(is); //读取图片
        return zoomImage(srcImg, w);
    }

    private BufferedImage zoomImage(BufferedImage srcImg, int w) {
        int sw = srcImg.getWidth();
        int sh = srcImg.getHeight();

        int dh = (int)(w*sh/sw); //设置新高度

        BufferedImage newImage=new BufferedImage(w, dh,BufferedImage.TYPE_INT_RGB);
        newImage.createGraphics().drawImage(srcImg.getScaledInstance(w, dh, Image.SCALE_SMOOTH), 0, 0, null);

        return newImage;
    }
}
