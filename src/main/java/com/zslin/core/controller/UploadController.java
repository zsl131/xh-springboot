package com.zslin.core.controller;

import com.zslin.business.dao.IMediumDao;
import com.zslin.business.dao.IProductDao;
import com.zslin.business.model.Medium;
import com.zslin.core.common.NormalTools;
import com.zslin.core.controller.dto.UploadParam;
import com.zslin.core.controller.dto.UploadResult;
import com.zslin.core.controller.tools.UploadParamsTools;
import com.zslin.core.qiniu.dto.MyPutRet;
import com.zslin.core.qiniu.model.QiniuConfig;
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

import java.io.*;
import java.util.UUID;

/**
 * Created by zsl on 2018/7/12.
 */
@RestController
@RequestMapping(value = "api/upload")
@Slf4j
public class UploadController {

    @Autowired
    private ConfigTools configTools;

    @Autowired
    private IMediumDao mediumDao;

    @Autowired
    private QiniuConfigTools qiniuConfigTools;

    @Autowired
    private QiniuTools qiniuTools;

    @Autowired
    private IProductDao productDao;

    //private static final String PATH_PRE = "/wangeditor/images";
    //private static final String UPLOAD_PATH_PRE = "/publicFile/upload";

    /**
     * 文件上传的通用方法
     * @param files
     * @param extra ticket和objType不能为空
     * @return
     */
    @RequestMapping(value = "normal")
    public UploadResult normalUpload(@RequestParam("files") MultipartFile[] files, String extra) {
        //log.info(extra);
        UploadResult result = upload(files, extra);
        return result;
    }

    private UploadResult upload( MultipartFile[] files, String extra) {
        UploadResult result = new UploadResult(0);
        QiniuConfig qiniuConfig = qiniuConfigTools.getQiniuConfig();
        if(files!=null) {
            for(MultipartFile file : files) {
                BufferedOutputStream bw = null;
                try {
                    //System.out.println("---->"+extra);
                    //log.info("上传参数：{}。", extra);
                    UploadParam param = UploadParamsTools.buildParams(extra); //参数DTO对象
                    //log.info(param.toString());
                    boolean isEditor = param.isEditor();
                    String objType = param.getObjClassName(); //上传的文件归属对象类型
                    String fileName = file.getOriginalFilename();
                    String fileType = NormalTools.getFileType(fileName); //文件类型
//                    System.out.println("========fileName::"+fileName);
                    //System.out.println("==============>"+param);
                    Medium m = new Medium();
                    m.setObjClassName(objType);
                    m.setTicket(param.getTicket());
                    m.setOrderNo(param.getOrderNo());
                    m.setObjId(param.getObjId());
                    m.setIsFirst(param.getIsFirst());
                    m.setType(MyFileTools.getFileEngType(fileName));
                    m.setRootUrl(qiniuConfig.getUrl());

                    if("1".equals(param.getTarget())) { //上传到本地
                        File outFile = new File(configTools.getFilePath(param.getPath()) + File.separator + NormalTools.getNow("yyyyMMdd") + File.separator + objType+ "_"+UUID.randomUUID().toString() + fileType);
                        String uploadPath = outFile.getAbsolutePath().replace(configTools.getFilePath(), File.separator).replaceAll("\\\\", "/");
                        FileUtils.copyInputStreamToFile(file.getInputStream(), outFile);
                        try {
                            if(NormalTools.isImageFile(fileName)) {
                                Thumbnails.of(outFile).size(param.getWidth(), param.getHeight()).toFile(outFile); //图片压缩
                            }
                        } catch (Exception e) {
                        }
                        m.setFileSize(outFile.length());
//                        m.setUrl(uploadPath);
                    } else { //上传到七牛
                        String key = objType+"_"+UUID.randomUUID().toString() + fileType.toLowerCase();
                        //System.out.println("------>"+key);
//                        m.setUrl(qiniuConfigTools.getQiniuConfig().getUrl() + "/" + key);
                        InputStream is = file.getInputStream();
                        File outFile = new File(configTools.getFilePath("/temp") + File.separator + key);
                        if(NormalTools.isImageFile(fileName)) {
                            try {
                                Thumbnails.of(file.getInputStream()).size(param.getWidth(), param.getHeight()).toFile(outFile); //图片压缩
                                is = new FileInputStream(outFile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        MyPutRet mpr = qiniuTools.upload(is, key);
                        if(NormalTools.isImageFile(fileName)) {
                            outFile.delete(); //上传到七牛后，将临时文件删除
                        }
                        m.setQiniuKey(key);
                        m.setFileSize(outFile.length());
                    }
                    m.setCreateLong(System.currentTimeMillis());
                    mediumDao.save(m); //保存
                    String filePath = m.getRootUrl() + (m.getRootUrl().endsWith("/")?"":"/") + m.getQiniuKey();
                    if(isEditor) {
                        result.add(filePath);
                    } else {
                        result.add(m.getId(), filePath);
                    }
                    updateProductHeadimg(param, filePath);
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

    /**
     * 修改产品的头像图片信息
     * @param param
     * @param imgPath
     */
    private void updateProductHeadimg(UploadParam param, String imgPath) {
        if("Product".equalsIgnoreCase(param.getObjClassName()) && param.getOrderNo()==1) {
            productDao.updateHeadimgUrl(imgPath, param.getObjId());
        }
    }
}