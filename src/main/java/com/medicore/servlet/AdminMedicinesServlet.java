package com.medicore.servlet;
import com.medicore.db.DBConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;

@WebServlet("/AdminMedicinesServlet")
public class AdminMedicinesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || !"admin".equals(session.getAttribute("staffRole"))) {
            response.getWriter().println("<tr><td colspan='5'>Please log in as admin.</td></tr>");
            return;
        }

        String sql = "SELECT medicine_id, code, name, price, stock FROM medicines ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder rows = new StringBuilder();
            while (rs.next()) {
                int stock = rs.getInt("stock");
                String badgeClass = stock == 0 ? "badge-danger" : stock <= 10 ? "badge-warn" : "badge-ok";
                String badgeText = stock == 0 ? "Out of stock" : stock <= 10 ? "Low" : "OK";

                rows.append("<tr>")
                    .append("<td>").append(rs.getString("name")).append("</td>")
                    .append("<td class='mono'>Rs. ").append(rs.getDouble("price")).append("</td>")
                    .append("<td class='mono'>").append(stock).append("</td>")
                    .append("<td><span class='badge ").append(badgeClass).append("'>").append(badgeText).append("</span></td>")
                    .append("<td>")
                    .append("<form style='display:flex;gap:6px' method='POST' action='UpdateStockServlet'>")
                    .append("<input type='hidden' name='medicineId' value='").append(rs.getInt("medicine_id")).append("'>")
                    .append("<input type='number' name='newStock' value='").append(stock).append("' min='0' style='width:80px'>")
                    .append("<button type='submit' class='btn btn-primary btn-sm'>Update</button>")
                    .append("</form>")
                    .append("</td>")
                    .append("</tr>");
            }
            response.getWriter().println(rows.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<tr><td colspan='5'>Error loading medicines.</td></tr>");
        }
    }
}