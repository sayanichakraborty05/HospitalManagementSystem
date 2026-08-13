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

@WebServlet("/MyPatientsServlet")
public class MyPatientsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || !"doctor".equals(session.getAttribute("staffRole"))) {
            response.getWriter().println("<tr><td colspan='3'>Please log in as a doctor.</td></tr>");
            return;
        }

        String doctorName = (String) session.getAttribute("staffName");

        String sql = "SELECT a.appointment_id, p.patient_id, p.full_name, a.appt_date "
                + "FROM appointments a "
                + "JOIN patients p ON p.patient_id = a.patient_id "
                + "LEFT JOIN prescriptions pr ON pr.appointment_id = a.appointment_id "
                + "WHERE a.doctor_name = ? AND a.status = 'confirmed' AND pr.prescription_id IS NULL "
                + "ORDER BY a.appt_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, doctorName);
            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder rows = new StringBuilder();
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    int patientId = rs.getInt("patient_id");
                    long appointmentId = rs.getLong("appointment_id");
                    String patientName = rs.getString("full_name");

                    rows.append("<tr>")
                        .append("<td>").append(patientName).append("</td>")
                        .append("<td class='mono'>").append(rs.getDate("appt_date")).append("</td>")
                        .append("<td><a class='btn btn-primary btn-sm' href='issue-prescription.html?patientId=")
                        .append(patientId)
                        .append("&appointmentId=").append(appointmentId)
                        .append("&patientName=").append(java.net.URLEncoder.encode(patientName, "UTF-8"))
                        .append("'>Issue Prescription</a></td>")
                        .append("</tr>");
                }
                if (!any) rows.append("<tr><td colspan='3'>No pending prescriptions to issue.</td></tr>");
                response.getWriter().println(rows.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<tr><td colspan='3'>Error loading patients.</td></tr>");
        }
    }
}