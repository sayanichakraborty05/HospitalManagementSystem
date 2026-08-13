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

@WebServlet("/AdminRecentPatientsServlet")
public class AdminRecentPatientsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || !"admin".equals(session.getAttribute("staffRole"))) {
            response.getWriter().println("<tr><td colspan='3'>Please log in as admin.</td></tr>");
            return;
        }

        String sql = "SELECT full_name, created_at FROM patients ORDER BY created_at DESC LIMIT 5";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder rows = new StringBuilder();
            boolean any = false;
            while (rs.next()) {
                any = true;
                rows.append("<tr>")
                    .append("<td>").append(rs.getString("full_name")).append("</td>")
                    .append("<td class='mono'>").append(rs.getTimestamp("created_at")).append("</td>")
                    .append("<td><span class='badge badge-ok'>Active</span></td>")
                    .append("</tr>");
            }
            if (!any) rows.append("<tr><td colspan='3'>No patients registered yet.</td></tr>");
            response.getWriter().println(rows.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<tr><td colspan='3'>Error loading patients.</td></tr>");
        }
    }
}