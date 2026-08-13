package com.medicore.servlet;
import com.medicore.db.DBConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

@WebServlet("/AppointmentServlet")
public class AppointmentServlet extends HttpServlet {

    private void sendResult(HttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(
            "<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'></head>"
            + "<body style='padding:60px 24px'><div class='card card-pad' style='max-width:480px;margin:0 auto'>"
            + "<h2>" + (success ? "Appointment booked!" : "Booking failed") + "</h2>"
            + "<p>" + message + "</p>"
            + "<a href='" + (success ? "patient-dashboard.html" : "appointment.html") + "' class='btn btn-primary'>"
            + (success ? "Go to dashboard" : "Try again") + "</a>"
            + "</div></body></html>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String patientName = request.getParameter("patientName");
        String department  = request.getParameter("department");
        String doctor       = request.getParameter("doctor");
        String date          = request.getParameter("date");
        String time          = request.getParameter("time");
        String reason         = request.getParameter("reason");

        HttpSession session = request.getSession(false);
        Integer patientId = (session != null) ? (Integer) session.getAttribute("patientId") : null;

        String sql = "INSERT INTO appointments (patient_id, patient_name, department, doctor_name, appt_date, appt_time, reason) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (patientId != null) ps.setInt(1, patientId); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, patientName);
            ps.setString(3, department);
            ps.setString(4, (doctor == null || doctor.isEmpty()) ? "Any available" : doctor);
            ps.setString(5, date);
            ps.setString(6, time);
            ps.setString(7, reason);

            ps.executeUpdate();

            sendResult(response, true,
                patientName + ", your appointment in " + department + " on " + date + " at " + time + " is confirmed.");

        } catch (SQLException e) {
            e.printStackTrace();
            sendResult(response, false, e.getMessage());
        }
    }
}