package com.zslin.test.tools;

import com.zslin.core.common.NormalTools;
import com.zslin.test.dto.JDBCObj;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsl on 2019/4/1.
 */
public class JDBCHandleTools {

    private List<String> reallyNos ;
    public JDBCHandleTools() {
        reallyNos = new ArrayList<>();
        reallyNos.add("202007061943461665");
        reallyNos.add("20200706125829868462");
        reallyNos.add("202007111010071249744");
        reallyNos.add("202007111000261249623");
        reallyNos.add("2020071419190615878");
        reallyNos.add("2020071409364515976");
        reallyNos.add("2020071409314915607");
        reallyNos.add("2020071408174915568");
        reallyNos.add("20200801131111302599");
    }

    public void handleProTitle() throws Exception {
//        JDBCObj local = new JDBCObj("localhost:3306", "zz_mall", "root", "123");
        JDBCObj local = new JDBCObj("122.14.215.131:13316", "z_mall", "root", "ynzslzsl131**");
        Connection localCon = getConn(local);
        Statement localState = localCon.createStatement();
        String sql = "select * FROM business_orders o WHERE o.pro_titles is NULL";
        ResultSet localRs = localState.executeQuery(sql);
        int count = 0;
        while(localRs.next()) {
            String ordersNo = localRs.getString("orders_no");
            Float money = localRs.getFloat("back_money");

            System.out.println("订单号："+ordersNo+"-->退款："+money);
            String titles = queryTitle(ordersNo, localCon);
            updateTitle(ordersNo, titles, localCon);
            count ++;
            //count += check(serverCon, phone, money);
        }
        System.out.println("退款数量：【"+count+"】");
    }

    private void updateTitle(String ordersNo, String titles, Connection con) throws Exception {
        Statement localState = con.createStatement();
        String sql = "update business_orders o SET o.pro_titles='"+titles+"' WHERE o.orders_no='"+ordersNo+"'";
        localState.executeUpdate(sql);
    }

    private String queryTitle(String ordersNo,Connection con) throws Exception {
        Statement localState = con.createStatement();
        String sql = "select * FROM business_orders_product o WHERE o.orders_no='"+ordersNo+"'";
        ResultSet localRs = localState.executeQuery(sql);
        StringBuffer sb = new StringBuffer();
        while(localRs.next()) {
            String title = localRs.getString("pro_title");
            sb.append(title).append("|");
            //count += check(serverCon, phone, money);
        }
        return sb.toString();
    }

    public void run() throws Exception {
        JDBCObj local = new JDBCObj("localhost:3306", "zz_mall", "root", "123");
//        JDBCObj local = new JDBCObj("122.14.215.131:13316", "z_mall", "root", "ynzslzsl131**");
//        JDBCObj server = new JDBCObj("123.58.6.13:13396", "hlx", "root", "ynzslzsl**");
//        Connection serverCon = getConn(server);

        Connection localCon = getConn(local);
        Statement localState = localCon.createStatement();
        String sql = "select * FROM business_orders_product o WHERE o.back_money>0";
        ResultSet localRs = localState.executeQuery(sql);
        int count = 0;
        while(localRs.next()) {
            String ordersNo = localRs.getString("orders_no");
            Float money = localRs.getFloat("back_money");
            Integer ordersId = localRs.getInt("orders_id");
            String [] agentInfo = queryAgent(localCon, ordersNo);

            String agentName = agentInfo[0];
            String agentOpenid = agentInfo[1];
            String agentPhone = agentInfo[2];
//            String agentName = localRs.getString("agent_name");
//            String agentOpenid = localRs.getString("agent_openid");
//            String agentPhone = localRs.getString("agent_phone");

            Integer proId = localRs.getInt("pro_id");
            String proTitle = localRs.getString("pro_title");

            System.out.println("订单号："+ordersNo+"-->退款："+money+",订单ID："+ordersId+"," +
                    "agentName:"+agentName+",agentOpenid:"+agentOpenid+",agentPhone:"+agentPhone+"," +
                    "proId:"+proId+",proTitle:"+proTitle);
            if(!reallyNos.contains(ordersNo)) {
                count++;
                insert(localCon, agentName, agentOpenid, agentPhone, money, ordersId, ordersNo,proId, proTitle);
            }
            //count += check(serverCon, phone, money);
        }
        System.out.println("退款数量：【"+count+"】");
    }

    private void insert(Connection con, String agentName, String agentOpenid, String agentPhone, Float backMoney,
                        Integer ordersId, String ordersNo, Integer proId, String proTitle) throws Exception {
        Statement state = con.createStatement();
        String sql = "insert into business_refund_record (agent_name, agent_openid, agent_phone, back_money," +
                "create_day, create_long, create_time, opt_name, opt_user_id,opt_username,orders_id," +
                "orders_no, orders_pro_id, orders_pro_title,reason,status) values ('"+agentName+"', '"+agentOpenid+"'," +
                "'"+agentPhone+"', "+backMoney+", '"+ NormalTools.curDate() +"', "+System.currentTimeMillis()+"," +
                "'"+NormalTools.curDatetime()+"', '钟述林', 1,'root', "+ordersId+", '"+ordersNo+"', "+proId+", '"+proTitle+"'," +
                "'坏果包赔', '0')";
        state.executeUpdate(sql);
    }

    private String[] queryAgent(Connection localCon, String ordersNo) throws Exception {
        Statement localState = localCon.createStatement();
        String sql = "select * FROM business_orders o WHERE o.orders_no='"+ordersNo+"'";
        ResultSet localRs = localState.executeQuery(sql);
        String[] res = new String[3];
        while(localRs.next()) {
            String agentName = localRs.getString("agent_name");
            String agentOpenid = localRs.getString("agent_openid");
            String agentPhone = localRs.getString("agent_phone");
            res[0] = agentName;
            res[1] = agentOpenid;
            res[2] = agentPhone;
        }
        return res;
    }

    private int check(Connection con, String phone, Integer money) throws Exception {
//        JDBCObj server = new JDBCObj("localhost:3306", "hlx", "root", "123");
//        Connection con = getConn(server);
        Statement sm = con.createStatement();
        String sql = "select * FROM t_wallet WHERE phone='"+phone+"'";
        ResultSet rs = sm.executeQuery(sql);
        int count = 0;
        while(rs.next()) {
            int m = rs.getInt("money");
            if(m!=money) {
                count ++;
                System.out.println(phone + "--> 本地：" + money + "--> 服务器：" + rs.getInt("money"));
            }
        }
        return count;
    }

    private Connection getConn(JDBCObj obj) {
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            Class.forName(driver);
            Connection con = DriverManager.getConnection(obj.getUrl(), obj.getUser(), obj.getPwd());
            return con;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
