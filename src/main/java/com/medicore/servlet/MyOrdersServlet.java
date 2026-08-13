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

@WebServlet("/MyOrdersServlet")
public class MyOrdersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("patientId") == null) {
            response.getWriter().println("<p class='form-hint'>Log in to see your past orders.</p>");
            return;
        }

        int patientId = (Integer) session.getAttribute("patientId");
        String sql = "SELECT order_id, order_mode, total, status, created_at FROM orders "
                + "WHERE patient_id = ? ORDER BY created_at DESC LIMIT 10";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder rows = new StringBuilder();
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    rows.append("<div class='med-card'><span>Order #MC").append(rs.getLong("order_id"))
                        .append(" - ").append(rs.getString("order_mode"))
                        .append(" - <span class='badge badge-ok'>").append(rs.getString("status")).append("</span></span>")
                        .append("<span class='mono'>Rs. ").append(rs.getDouble("total")).append("</span></div>");
                }
                if (!any) rows.append("<p class='form-hint'>No past orders yet.</p>");
                response.getWriter().println(rows.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<p class='form-hint'>Error loading orders.</p>");
        }
    }
}