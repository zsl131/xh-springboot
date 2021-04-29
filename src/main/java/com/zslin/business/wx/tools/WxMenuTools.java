package com.zslin.business.wx.tools;

import com.alibaba.fastjson.JSONObject;
import com.zslin.business.wx.dao.IWxMenuDao;
import com.zslin.business.wx.dto.WxMenuDto;
import com.zslin.business.wx.model.WxMenu;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WxMenuTools {

    @Autowired
    private IWxMenuDao wxMenuDao;

    @Autowired
    private WxConfigTools wxConfigTools;

    @Autowired
    private WxAccessTokenTools wxAccessTokenTools;

    public void publishMenu() {
        String json = createMenuJson();
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+wxAccessTokenTools.getAccessToken();
        JSONObject jsonObj = WeixinUtil.httpRequest(url, "POST", json);
        String code = JsonTools.getJsonParam(jsonObj.toString(), "errcode");
        if(!"0".equals(code)) {
            throw new BusinessException(code, JsonTools.getJsonParam(jsonObj.toJSONString(), "errmsg"));
        }
    }

    /**
     * 生成微信菜单的JSON数据
     * @return
     */
    public String createMenuJson() {
        StringBuffer sb = new StringBuffer("{\"button\":[");
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<WxMenu> parents = wxMenuDao.findRoot("1", sort);
        int temp = 0;
        for(WxMenu p : parents) {
            sb.append(createSinglMenuJson(p));
            temp++;
            if(temp<parents.size()) {sb.append(",");}
        }
        sb.append("]}");
        return sb.toString();
    }

    private String createSinglMenuJson(WxMenu menu) {
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        StringBuffer sb = new StringBuffer("{");
        List<WxMenu> suns = wxMenuDao.findByPidAndStatus(menu.getId(), "1", sort);
        sb.append("\"name\":\"").append(menu.getName()).append("\"");
        if(suns==null || suns.size()<=0) {
            //无子菜单
            sb.append(createMenu(menu));
        } else {
            sb.append(",\"sub_button\":[");
            int temp = 0;
            for(WxMenu sun : suns) {
                sb.append("{");
                sb.append("\"name\":\"").append(sun.getName()).append("\"");
                sb.append(createMenu(sun));
                sb.append("}");
                temp ++;
                if(temp<suns.size()) {sb.append(",");}
            }
            sb.append("]");
        }
        sb.append("}");
        return sb.toString();
    }

    private String createMenu(WxMenu menu) {
        StringBuffer sb = new StringBuffer();
        String type = menu.getType();
        sb.append(",\"type\":\"").append(type).append("\"");
        if("view".equalsIgnoreCase(type)) {
            String url = menu.getUrl();
            if(!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://")) { //如果不是http链接
                if(!url.startsWith("/")) {url = "/"+url;}
                url = wxConfigTools.getWxConfig().getUrl()+url;
            }
            sb.append(",\"url\":\"").append(url).append("\"");
        } else if("click".equalsIgnoreCase(type)) {
            sb.append(",\"key\":\"").append(menu.getOptKey()).append("\"");
        } else if("miniprogram".equalsIgnoreCase(type)) {
            sb.append(",\"appid\":\"").append(menu.getAppid()).append("\"");
            sb.append(",\"url\":\"").append(menu.getPagePath()).append("\"");
            sb.append(",\"pagepath\":\"").append(menu.getPagePath()).append("\"");
        }
        return sb.toString();
    }

    public void buildWxMenuOrderNo() {
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<WxMenu> root = wxMenuDao.findRoot(sort);
        Integer index = 1;
        for(WxMenu r : root) {
            wxMenuDao.updateOrderNo(index++, r.getId());
            buildWxMenuOrderNo(r.getId(), sort);
        }
    }

    private void buildWxMenuOrderNo(Integer pid, Sort sort) {
        List<WxMenu> list = wxMenuDao.findByPid(pid, sort);
        if(list!=null && list.size()>0) {
            int index = 1;
            for(WxMenu m : list) {
                wxMenuDao.updateOrderNo(index++, m.getId());
                buildWxMenuOrderNo(m.getId(), sort);
            }
        }
    }

    /**
     * 生成分类数据的树结构数据
     * @return
     */
    public List<WxMenuDto> buildTree() {
        List<WxMenuDto> result = new ArrayList<>();
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<WxMenu> rootList = wxMenuDao.findRoot(sort);
        for(WxMenu m : rootList) {
            List<WxMenu> children = wxMenuDao.findByPid(m.getId(), sort);
            result.add(new WxMenuDto(m, children));
        }
        return result;
    }
}
