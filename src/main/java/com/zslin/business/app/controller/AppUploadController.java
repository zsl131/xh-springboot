package com.zslin.business.app.controller;

import com.zslin.business.dao.ICustomerDao;
import com.zslin.business.dao.IOrdersProductDao;
import com.zslin.business.mini.dao.IImageWallDao;
import com.zslin.business.mini.model.ImageWall;
import com.zslin.business.model.Customer;
import com.zslin.core.common.NormalTools;
import com.zslin.core.controller.dto.UploadResult;
import com.zslin.core.qiniu.tools.MyFileTools;
import com.zslin.core.qiniu.tools.QiniuConfigTools;
import com.zslin.core.qiniu.tools.QiniuTools;
import com.zslin.core.tools.ConfigTools;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by zsl on 2018/7/12.
 */
@RestController
@RequestMapping(value = "api/app/upload")
@Slf4j
public class AppUploadController {

    @Autowired
    private ConfigTools configTools;

    @Autowired
    private QiniuConfigTools qiniuConfigTools;

    @Autowired
    private QiniuTools qiniuTools;

    @Autowired
    private IImageWallDao imageWallDao;

    @Autowired
    private ICustomerDao customerDao;

    @Autowired
    private IOrdersProductDao ordersProductDao;

    /**
     * 文件上传的通用方法
     * @param files
     * @param customId ticket和objType不能为空
     * @return
     */
    @RequestMapping(value = "normal")
    public UploadResult normalUpload(@RequestParam("files") MultipartFile[] files, Integer customId) {
        UploadResult result = upload(files, customId);
        return result;
    }

    /**
     * 上传产品售后信息的照片
     * @param files
     * @return
     */
    @RequestMapping(value = "productException")
    public UploadResult productException(@RequestParam("files") MultipartFile[] files) {
        UploadResult result = new UploadResult(0);
        if(files!=null) {
            for(MultipartFile file : files) {
                BufferedOutputStream bw = null;
                try {
                    //System.out.println("---->"+extra);
                    //log.info("上传参数：{}。", customId);
//                    UploadParam param = UploadParamsTools.buildParams(extra); //参数DTO对象

                    String fileName = file.getOriginalFilename();
                    String fileType = NormalTools.getFileType(fileName); //文件类型

                    String type = "";//1-图片；2-视频；
                    if(MyFileTools.isImageFile(fileName)) {type = "1";}
                    else if(MyFileTools.isVideoFile(fileName)) {type = "2"; }

                    //上传到七牛
                    String key = "Product_Exception_"+UUID.randomUUID().toString() + fileType.toLowerCase();
                    //System.out.println("------>"+key);
//                        m.setUrl(qiniuConfigTools.getQiniuConfig().getUrl() + "/" + key);
                    if("1".equals(type)) { //图片
                        File outFile = new File(configTools.getFilePath("/app/productException") + File.separator + "temp" + File.separator + UUID.randomUUID().toString() + fileType);
                        FileUtils.copyInputStreamToFile(file.getInputStream(), outFile);

                        Thumbnails.of(outFile).size(1000, 1000).toFile(outFile);

                        qiniuTools.upload(FileUtils.openInputStream(outFile), key);
                        outFile.delete(); //传到七牛就删除本地

                        result.add(qiniuConfigTools.getQiniuConfig().getUrl()+key);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    private UploadResult upload( MultipartFile[] files, Integer customId) {
        UploadResult result = new UploadResult(0);
        if(files!=null) {
            for(MultipartFile file : files) {
                BufferedOutputStream bw = null;
                try {
                    //System.out.println("---->"+extra);
                    //log.info("上传参数：{}。", customId);
//                    UploadParam param = UploadParamsTools.buildParams(extra); //参数DTO对象

                    String fileName = file.getOriginalFilename();
                    String fileType = NormalTools.getFileType(fileName); //文件类型

                    String type = "";//1-图片；2-视频；
                    if(MyFileTools.isImageFile(fileName)) {type = "1";}
                    else if(MyFileTools.isVideoFile(fileName)) {type = "2"; }

                    //上传到七牛
                    String key = "ImageWall_"+UUID.randomUUID().toString() + fileType.toLowerCase();
                    //System.out.println("------>"+key);
//                        m.setUrl(qiniuConfigTools.getQiniuConfig().getUrl() + "/" + key);
                    if("1".equals(type)) { //图片
                        File outFile = new File(configTools.getFilePath("/app/imageWall") + File.separator + "temp" + File.separator + UUID.randomUUID().toString() + fileType);
                        FileUtils.copyInputStreamToFile(file.getInputStream(), outFile);

                        Thumbnails.of(outFile).size(800, 800).toFile(outFile);

                        qiniuTools.upload(FileUtils.openInputStream(outFile), key);
                        outFile.delete(); //传到七牛就删除本地
                    } else if("2".equals(type)) { //是视频
                        //String key = System.currentTimeMillis() + fileType.toLowerCase();
                        qiniuTools.upload(file.getInputStream(), key);
                    }

                    Customer customer = customerDao.findOne(customId);
                    log.info(customer.toString());

                    if(customer!=null) {
                        ImageWall wall = new ImageWall();
                        wall.setCreateDay(NormalTools.curDate());
                        wall.setCreateLong(System.currentTimeMillis());
                        wall.setCreateTime(NormalTools.curDatetime());
                        wall.setCustomId(customId);
                        wall.setCustomNickname(customer.getNickname());
                        wall.setCustomOpenid(customer.getOpenid());
                        wall.setCustomUnionid(customer.getUnionid());
                        wall.setFileType(type);
                        wall.setHeadImgUrl(customer.getHeadImgUrl());
                        wall.setStatus("0");
                        wall.setUrl(qiniuConfigTools.getQiniuConfig().getUrl() + key);
                        imageWallDao.save(wall);
                        result.add(wall.getId(), wall.getUrl());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }
}