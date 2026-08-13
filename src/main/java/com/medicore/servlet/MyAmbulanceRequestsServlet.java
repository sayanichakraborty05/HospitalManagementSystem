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

@WebServlet("/MyAmbulanceRequestsServlet")
public class MyAmbulanceRequestsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("patientId") == null) {
            response.getWriter().println("<p class='form-hint'>Log in to see your ambulance requests.</p>");
            return;
        }

        int patientId = (Integer) session.getAttribute("patientId");
        String sql = "SELECT id, pickup_location, condition_text, status, created_at FROM ambulance_requests "
                + "WHERE patient_id = ? ORDER BY created_at DESC LIMIT 5";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder out = new StringBuilder();
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    String status = rs.getString("status");
                    String badgeClass = "requested".equals(status) ? "badge-warn"
                            : "completed".equals(status) ? "badge-ok" : "badge-danger";

                    out.append("<div class='med-card' style='flex-direction:column;align-items:flex-start;gap:4px'>")
                       .append("<div style='display:flex;justify-content:space-between;width:100%'>")
                       .append("<strong>Request #AMB").append(rs.getInt("id")).append("</strong>")
                       .append("<span class='badge ").append(badgeClass).append("'>").append(status).append("</span>")
                       .append("</div>")
                       .append("<div class='med-meta'>Pickup: ").append(rs.getString("pickup_location")).append("</div>")
                       .append("<div class='med-meta'>Condition: ").append(rs.getString("condition_text") == null ? "-" : rs.getString("condition_text")).append("</div>")
                       .append("</div>");
                }
                if (!any) out.append("<p class='form-hint'>No ambulance requests yet.</p>");
                response.getWriter().println(out.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<p class='form-hint'>Error loading requests.</p>");
        }
    }
}