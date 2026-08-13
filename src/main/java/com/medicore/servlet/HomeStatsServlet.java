package com.medicore.servlet;

import com.medicore.db.DBConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

@WebServlet("/HomeStatsServlet")
public class HomeStatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        int doctorCount = 0, apptTodayCount = 0, lowStockCount = 0;

        try (Connection conn = DBConnection.getConnection()) {

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM staff WHERE role='doctor' AND status='approved'")) {
                if (rs.next()) doctorCount = rs.getInt(1);
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM appointments WHERE appt_date = CURDATE() AND status='confirmed'")) {
                if (rs.next()) apptTodayCount = rs.getInt(1);
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM medicines WHERE stock <= 10")) {
                if (rs.next()) lowStockCount = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.getWriter().print(doctorCount + "|" + apptTodayCount + "|" + lowStockCount);
    }
}