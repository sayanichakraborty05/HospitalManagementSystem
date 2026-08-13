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

@WebServlet("/AppointmentDetailsServlet")
public class AppointmentDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("patientId") == null) {
            response.getWriter().println("<p>Please log in.</p>");
            return;
        }

        long apptId = Long.parseLong(request.getParameter("apptId"));
        int patientId = (Integer) session.getAttribute("patientId");

        String sql = "SELECT doctor_name, department, appt_date, appt_time, fee, payment_status "
                + "FROM appointments WHERE appointment_id = ? AND patient_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, apptId);
            ps.setInt(2, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if ("paid".equals(rs.getString("payment_status"))) {
                        response.getWriter().println("<p>This appointment has already been paid for.</p>");
                        return;
                    }
                    response.getWriter().println(
                        "<h3>" + rs.getString("department") + " with " + rs.getString("doctor_name") + "</h3>"
                        + "<p class='form-hint'>" + rs.getDate("appt_date") + " at " + rs.getTime("appt_time") + "</p>"
                        + "<p style='font-size:1.6rem;color:var(--teal-900)' class='mono'>Rs. " + rs.getDouble("fee") + "</p>"
                    );
                } else {
                    response.getWriter().println("<p>Appointment not found.</p>");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<p>Error loading appointment.</p>");
        }
    }
}