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

@WebServlet("/MyPrescriptionsServlet")
public class MyPrescriptionsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("patientId") == null) {
            response.getWriter().println("<tr><td colspan='3'>Please log in to see your prescriptions.</td></tr>");
            return;
        }

        int patientId = (Integer) session.getAttribute("patientId");
        String sql = "SELECT pi.medicine_name, pi.dosage, p.status FROM prescriptions p "
                + "JOIN prescription_items pi ON pi.prescription_id = p.prescription_id "
                + "WHERE p.patient_id = ? ORDER BY p.issued_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder rows = new StringBuilder();
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    String status = rs.getString("status");
                    String badgeClass = "active".equals(status) ? "badge-ok" : "badge-warn";
                    rows.append("<tr><td>").append(rs.getString("medicine_name"))
                        .append("</td><td>").append(rs.getString("dosage"))
                        .append("</td><td><span class='badge ").append(badgeClass).append("'>")
                        .append(status).append("</span></td></tr>");
                }
                if (!any) rows.append("<tr><td colspan='3'>No active prescriptions yet.</td></tr>");
                response.getWriter().println(rows.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<tr><td colspan='3'>Error loading prescriptions.</td></tr>");
        }
    }
}