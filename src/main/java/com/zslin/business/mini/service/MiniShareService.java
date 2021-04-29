package com.zslin.business.mini.service;

import com.zslin.business.dao.IMediumDao;
import com.zslin.business.dao.IProductDao;
import com.zslin.business.mini.tools.ShareImageTools;
import com.zslin.business.model.Medium;
import com.zslin.business.model.Product;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.qiniu.model.QiniuConfig;
import com.zslin.core.qiniu.tools.QiniuConfigTools;
import com.zslin.core.qiniu.tools.QiniuTools;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.ConfigTools;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 小程序分享
 */
@Service
public class MiniShareService {

    @Autowired
    private ShareImageTools shareImageTools;

    @Autowired
    private ConfigTools configTools;

    @Autowired
    private IProductDao productDao;

    @Autowired
    private QiniuConfigTools qiniuConfigTools;

    @Autowired
    private QiniuTools qiniuTools;

    @Autowired
    private IMediumDao mediumDao;

    private String buildProImg(Integer objId) {
        try {
            Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
            List<Medium> list = mediumDao.findByObjClassNameAndObjId("Product", objId, sort);
            int random = (int)(Math.random()*list.size());
            Medium m = list.get(random);
            return m.getRootUrl()+m.getQiniuKey();
        } catch (Exception e) {
            return null;
        }
    }

    public JsonResult share(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        Integer id = JsonTools.getParamInteger(params, "proId");
        Product pro = productDao.findOne(id); //获取对应产品
        String page = JsonTools.getJsonParam(params, "page");
        String scene = id+"_"+customDto.getCustomId(); //生成scene数据
        String proImg = buildProImg(id);
        if(proImg==null) {proImg = pro.getHeadImgUrl();}
        BufferedImage bi = shareImageTools.createImage(pro.getTitle(), "￥ "+pro.getPrice(), "好友"+customDto.getNickname(),
                "山里有味，满山晴浓", proImg, customDto.getHeadImgUrl(), page, scene);
        if(bi==null) { //说明生成失败
            return JsonResult.success("生成失败").set("flag", "0").set("url", "");
        } else {
            try {
                String key = "share_"+ UUID.randomUUID().toString() +".jpg";
                File outFile = new File(configTools.getFilePath("share") + File.separator + key);
                ImageIO.write(bi, "jpg", outFile);

                qiniuTools.upload(new FileInputStream(outFile), key);

                QiniuConfig qiniuConfig = qiniuConfigTools.getQiniuConfig();
                String url = qiniuConfig.getUrl() + key;
                outFile.delete(); //上传到七牛后删除本地图片文件
                return JsonResult.success("生成成功").set("flag", "1").set("url", url);
            } catch (IOException e) {
                e.printStackTrace();
                return JsonResult.success("生成失败"+e.getMessage()).set("flag", "0").set("url", "");
            }
        }
    }
}
