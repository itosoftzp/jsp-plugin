package org.joget.tutorial;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.tutorial.model.Item;
import org.json.JSONException;
import org.json.JSONObject;

public class PurchaseRequestWS extends DefaultApplicationPlugin {
    
    @Override
    public String getName() {
        return "this is Webservice processtools to POST data plugin";
    }
    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getLabel() {
        return "WS-PT plugin";
    }

    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "properties/StoreDataWS.json");
    }

    @Override
    public String getDescription() {
        return "this is plugin at process tools to post data";
    }


    @Override
    public String getVersion() {
        return "2.0.0";
    }

    @Override
    public Object execute(Map arg0) {
        // dapatkan WS URL dari properti
        String WSURL = getPropertyString("url");

        // dapatkan nilai parent ID dari properti
        String parentId = getPropertyString("parentId");

        LogUtil.info(this.getClassName(), "WSURL : " + WSURL);
        LogUtil.info(this.getClassName(), "parentId : " + parentId);

        if (parentId != null && !"".equals(parentId)) {
            // dapatkan data purchase Item dari database berdasarkan parent ID
            List<Item> listItems = getPurchaseItems(parentId);
            // Loop data purchase item, panggil Web Service
            for(Item item : listItems) {
            callWebService(item, WSURL);
            }
        }
        return null; 
    }

    
    
    // get the data
    private List<Item> getPurchaseItems(String parentId){
            List<Item> list = new ArrayList();

            Connection con = null;
            try {
                DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
                con = ds.getConnection();
                String query;
                query = "select id,dateCreated,dateModified,createdBy,createdByName,modifiedBy, modifiedByName,c_namaProduk,c_kuantitas,c_alur,c_skuProduk,c_tanggal,  c_id_flow,  c_id_req_item_keluar,c_gudang,c_qty_balance,c_qty_sebelum,c_qty_sesudah,c_qty_count from app_fd_flow_stock where id=?";
                if(!con.isClosed()){
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setObject(1, parentId);

                    ResultSet rs = ps.executeQuery();
                    
                    // looping hasil query 
                    while (rs.next()){
                        Item item = new Item();
                        item.setid(rs.getString("id"));
                        item.setdateCreated(rs.getString("dateCreated"));
                        item.setdateModified(rs.getString("dateModified"));
                        item.setcreatedBy(rs.getString("createdBy"));
                        item.setcreatedByName(rs.getString("createdByName"));
                        item.setmodifiedBy(rs.getString("modifiedBy"));
                        item.setmodifiedByName(rs.getString("modifiedByName"));
                        item.setc_namaProduk(rs.getString("c_namaProduk"));
                        item.setc_kuantitas(rs.getString("c_kuantitas"));
                        item.setc_alur(rs.getString("c_alur"));
                        item.setc_skuProduk(rs.getString("c_skuProduk"));
                        item.setc_tanggal(rs.getString("c_tanggal"));
                        item.setc_id_flow(rs.getString("c_id_flow"));
                        item.setc_id_req_item_keluar(rs.getString("c_id_req_item_keluar"));
                        item.setc_gudang(rs.getString("c_gudang"));
                        item.setc_qty_balance(rs.getString("c_qty_balance"));
                        item.setc_qty_sebelum(rs.getString("c_qty_sebelum"));
                        item.setc_qty_sesudah(rs.getString("c_qty_sesudah"));
                        item.setc_qty_count(rs.getString("c_qty_count"));
                        list.add(item);
                    }
                }
            } catch (Exception e) {
                LogUtil.error(this.getClassName(), e, e.getMessage());
            } finally{
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException ex) {
                        LogUtil.error(this.getClassName(), ex, ex.getMessage());
                    }
                }
            }
            return list;
    }

    private boolean callWebService(Item item, String WSURL) {
        boolean result = false;
        // Prepare parameters
        JSONObject param = new JSONObject();
        try {
            param.put("id", item.getid());                  
            param.put("dateCreated", item.getdateCreated());         
            param.put("dateModified", item.getdateModified());        
            param.put("createdBy", item.getcreatedBy());           
            param.put("createdByName", item.getcreatedByName());       
            param.put("modifiedBy", item.getmodifiedBy());          
            param.put("modifiedByName", item.getmodifiedByName());      
            param.put("c_namaProduk", item.getc_namaProduk());        
            param.put("c_kuantitas", item.getc_kuantitas());         
            param.put("c_alur", item.getc_alur());              
            param.put("c_skuProduk", item.getc_skuProduk());         
            param.put("c_tanggal", item.getc_tanggal());           
            param.put("c_id_flow", item.getc_id_flow());           
            param.put("c_id_req_item_keluar", item.getc_id_req_item_keluar());
            param.put("c_gudang", item.getc_gudang());            
            param.put("c_qty_balance", item.getc_qty_balance());       
            param.put("c_qty_sebelum", item.getc_qty_sebelum());       
            param.put("c_qty_sesudah", item.getc_qty_sesudah());       
            param.put("c_qty_count", item.getc_qty_count());
        } catch (JSONException ex) {
            LogUtil.error(this.getClassName(), ex, ex.getMessage());
        }
    
        // Call web service
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost post = new HttpPost(WSURL);
    
            StringEntity input = new StringEntity(param.toString());
            input.setContentType("application/json");
            post.setEntity(input);
    
            CloseableHttpResponse response = client.execute(post);
    
            // Read the response from the web service
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                StringBuilder resultInquiry = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    resultInquiry.append(line);
                }
                // Print the result of the web service call
                LogUtil.info(this.getClassName(), "print hasil call ws : " + resultInquiry.toString());
            }
        } catch (UnsupportedEncodingException ex) {
            LogUtil.error(this.getClassName(), ex, ex.getMessage());
        } catch (IOException ex) {
            LogUtil.error(this.getClassName(), ex, ex.getLocalizedMessage());
        }
    
        return result;
    }

}
