/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.joget.jsp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONObject;

/**
 *
 * @author 12
 */
public class GetSisaPreform extends DefaultApplicationPlugin implements PluginWebSupport {

    @Override
    public Object execute(Map map) {
        return null;
    }

    @Override
    public String getName() {
        return "WSQury Sisa Preform";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "Web service to query sisa preform untuk produksi";
    }

    @Override
    public String getLabel() {
        return getName();
    }

    @Override
    public String getClassName() {
        return getName();
    }

    @Override
    public String getPropertyOptions() {
        return null;
    }

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection connection = null;
        PreparedStatement ps = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        ResultSet rs = null;
        try {
            connection = ds.getConnection();
            String query = "select c_total_pre_form from app_fd_pre_form where c_nama_pre_form =?";
            ps = connection.prepareStatement(query);
            ps.setString(1, request.getParameter("preform"));
            rs = ps.executeQuery();

            JSONObject jsObject = new JSONObject();
            while (rs.next()) {
                jsObject.put("total_pre_form", rs.getString("c_total_pre_form"));
                response.setContentType("application/json");
                response.getWriter().write(jsObject.toString());
            }
        } catch (SQLException ex) {
            Logger.getLogger(GetDetailProduk.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    LogUtil.error(getClass().getName(), ex, ex.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    LogUtil.error(getClass().getName(), ex, ex.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    LogUtil.error(getClass().getName(), ex, ex.getMessage());
                }
            }
        }
    }
    
}
