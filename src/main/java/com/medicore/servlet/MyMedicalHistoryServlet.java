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

@WebServlet("/MyMedicalHistoryServlet")
public class MyMedicalHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("patientId") == null) {
            response.getWriter().println("<tr><td colspan='4'>Log in to see your medical history.</td></tr>");
            return;
        }

        int patientId = (Integer) session.getAttribute("patientId");
        String sql = "SELECT mh.visit_date, s.full_name AS doctor_name, mh.diagnosis, mh.notes "
                + "FROM medical_history mh LEFT JOIN staff s ON s.staff_id = mh.doctor_id "
                + "WHERE mh.patient_id = ? ORDER BY mh.visit_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder rows = new StringBuilder();
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    rows.append("<tr><td class='mono'>").append(rs.getDate("visit_date"))
                        .append("</td><td>").append(rs.getString("doctor_name") == null ? "-" : rs.getString("doctor_name"))
                        .append("</td><td>").append(rs.getString("diagnosis"))
                        .append("</td><td>").append(rs.getString("notes") == null ? "-" : rs.getString("notes"))
                        .append("</td></tr>");
                }
                if (!any) rows.append("<tr><td colspan='4'>No visit records yet.</td></tr>");
                response.getWriter().println(rows.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<tr><td colspan='4'>Error loading history.</td></tr>");
        }
    }
}