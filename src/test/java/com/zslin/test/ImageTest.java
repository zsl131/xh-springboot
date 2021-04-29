package com.zslin.test;

import com.zslin.business.mini.tools.ShareImageTools;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "zsl")
public class ImageTest {

    @Autowired
    private ShareImageTools shareImageTools;

    @Test
    public void test04() throws Exception {
        String proTitle = "绥江半边红李子";
        String price = "￥ 88.8";
        String nickname = "好友想攀登的胖子";
        String remark = "请君移驾品鉴";
        String fontName = "微软雅黑";
        String proImgUrl = "https://msq-file.zslin.com/112.jpg";
//        String headUrl = "https://zz-specialty.zslin.com/oIguM5UvbfNglnWYj7W7_aBkS-3w.jpg";
        String headUrl = "";
        BufferedImage bi = shareImageTools.createImage(proTitle, price, nickname, remark, proImgUrl, headUrl, "", "123");
        ImageIO.write(bi, "jpg", new File("D:/temp/test/share3.jpg"));
    }

    @Test
    public void test03() throws Exception {
        String proTitle = "绥江半边红李子";
        String price = "￥ 88";
        String nickname = "好友想攀登的胖子";
        String remark = "请君移驾品鉴";
        String fontName = "微软雅黑";
        String proImgUrl = "https://zz-specialty.zslin.com/Product_a2d5d791-55a8-472f-80c6-e49bdcf7c964.jpg";
        String headUrl = "https://zz-specialty.zslin.com/oIguM5UvbfNglnWYj7W7_aBkS-3w.jpg";
        BufferedImage bi = new BufferedImage(800, 900, BufferedImage.TYPE_INT_BGR);
        File file = new File("D:/temp/test/bottom.jpg");
        try {
            if(file.exists()) {
                file.delete();
                file.createNewFile();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
//        writeImage(bi, "jpg", file);
        Graphics g = bi.getGraphics();
        g.setColor(new Color(255,255,255));
        g.fillRect(0,0,bi.getWidth(),620); //设置上部份背景色


//        BufferedImage proImg = zoomImage(new FileInputStream(new File("D:/temp/test/pro.jpg")), 800);
        BufferedImage proImg = zoomImage(new URL(proImgUrl).openStream(), 800);
        g.drawImage(proImg, 0,0,null);


        g.setColor(new Color(233,233,233));
        g.fillRect(0,620,bi.getWidth(),280); //设置下部份背景色
        //g.dispose();



        BufferedImage qr = zoomImage(new URL(proImgUrl).openStream(), 190);
        g.drawImage(qr, 590,690,null);

       /* ///小程序码
//        ImageIcon imgIcon = new ImageIcon("D:/temp/test/123.jpg");
        ImageIcon imgIcon = new ImageIcon(new URL("https://zz-specialty.zslin.com/Product_a2d5d791-55a8-472f-80c6-e49bdcf7c964.jpg"));
        //得到Image对象。
        Image img = imgIcon.getImage();
        //将小图片绘到大图片上。
        //5,300 .表示你的小图片在大图片上的位置。
        g.drawImage(img,590,690,null);*/
        //g.dispose();

        //满山晴字样
        BufferedImage imgIcon = ImageIO.read(new ClassPathResource("logo-msq.png").getInputStream());
        g.drawImage(imgIcon, 30,640,null);

        /*//满山晴字样
        ImageIcon imgIcon2 = new ImageIcon("D:/temp/test/logo.png");
        //得到Image对象。
        Image img2 = imgIcon2.getImage();
        //将小图片绘到大图片上。
        //5,300 .表示你的小图片在大图片上的位置。
        g.drawImage(img2,30,640,null);*/


        BufferedImage srcImg = ImageIO.read(new URL(headUrl)); //读取图片
        BufferedImage newImage=new BufferedImage(90, 90,BufferedImage.TYPE_INT_RGB);
        newImage.createGraphics().drawImage(srcImg.getScaledInstance(90, 90, Image.SCALE_SMOOTH), 0, 0, null);

        g.drawImage(newImage, 30,790,null);

        Font font = new Font(fontName, Font.PLAIN, 34);
        g.setColor(new Color(39,39,39));
        g.setFont(font);
        g.drawString(proTitle, 150, 675); //写字是从下往上的占高度

        g.setColor(new Color(255, 0, 0));
        g.setFont(new Font(fontName, Font.PLAIN, 50));
        g.drawString(price, 40, 750);

        g.setColor(new Color(100,100,100));
        g.setFont(font);
        g.drawString(nickname, 135, 830);

        g.setColor(new Color(126,126,126));
        g.setFont(new Font(fontName, Font.PLAIN, 26));
        g.drawString(remark, 135, 870);


        g.dispose();
        ImageIO.write(bi, "jpg", file);

        /*//设置颜色。
        g.setColor(Color.BLACK);


        //最后一个参数用来设置字体的大小
        Font f = new Font("宋体",Font.PLAIN,25);
        Color mycolor = Color.red;//new Color(0, 0, 255);
        g.setColor(mycolor);
        g.setFont(f);

        //10,20 表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。
        g.drawString(username,100,135);

        g.dispose();


        OutputStream os;

        //os = new FileOutputStream("d:/union.jpg");
        String shareFileName = "\\upload\\" + System.currentTimeMillis() + ".jpg";
        os = new FileOutputStream(shareFileName);
        //创键编码器，用于编码内存中的图象数据。
        JPEGImageEncoder en = JPEGCodec.createJPEGEncoder(os);
        en.encode(buffImg);

        is.close();
        os.close();*/


        System.out.println("绘图成功");
    }

    private BufferedImage zoomImage(InputStream is, int w) throws Exception {
        /*File srcFile = new File(src);
        File destFile = new File(dest);*/

        BufferedImage srcImg = ImageIO.read(is); //读取图片
        int sw = srcImg.getWidth();
        int sh = srcImg.getHeight();

        //System.out.println(sw+"========"+sh);

        int dh = (int)(w*sh/sw); //设置新高度

        //System.out.println(w+"----------"+dh);


        BufferedImage newImage=new BufferedImage(w, dh,BufferedImage.TYPE_INT_RGB);
        newImage.createGraphics().drawImage(srcImg.getScaledInstance(w, dh, Image.SCALE_SMOOTH), 0, 0, null);

        return newImage;
    }

    @Test
    public void test01() {
        BufferedImage bi = new BufferedImage(800, 1137, BufferedImage.TYPE_INT_BGR);
        File file = new File("D:/temp/test.jpg");
        try {
            if(file.exists()) {
                file.delete();
                file.createNewFile();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        writeImage(bi, "jpg", file);
        System.out.println("绘图成功");
    }

    private boolean writeImage(BufferedImage bi, String picType, File file) {
        Graphics g = bi.getGraphics();
        g.setColor(new Color(255,255,255));
        g.fillRect(0,0,bi.getWidth(),bi.getHeight()); //设置背景色
        g.dispose();
        boolean val = false;
        try {
            val = ImageIO.write(bi, picType, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return val;
    }

    /** 绽放照片 */
    @Test
    public void test02() throws Exception {
        zoomImage("D:/temp/pro.jpg", "D:/temp/pro-1.jpg", 800);
        zoomImage("D:/temp/pro.jpg", "D:/temp/pro-2.jpg", 300);
    }

    private void zoomImage(String src,String dest,int w) throws Exception {
        File srcFile = new File(src);
        File destFile = new File(dest);

        BufferedImage srcImg = ImageIO.read(srcFile); //读取图片
        int sw = srcImg.getWidth();
        int sh = srcImg.getHeight();

        System.out.println(sw+"========"+sh);

        int dh = (int)(w*sh/sw); //设置新高度

        System.out.println(w+"----------"+dh);


        BufferedImage newImage=new BufferedImage(w, dh,BufferedImage.TYPE_INT_RGB);
        newImage.createGraphics().drawImage(srcImg.getScaledInstance(w, dh, Image.SCALE_SMOOTH), 0, 0, null);

        ImageIO.write(newImage, dest.substring(dest.lastIndexOf(".")+1), destFile);
    }
}
