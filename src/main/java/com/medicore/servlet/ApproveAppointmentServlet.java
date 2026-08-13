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

@WebServlet("/ApproveAppointmentServlet")
public class ApproveAppointmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"doctor".equals(session.getAttribute("staffRole"))) {
            response.sendRedirect("login.html");
            return;
        }

        String approvingDoctorName = (String) session.getAttribute("staffName");

        long appointmentId = Long.parseLong(request.getParameter("appointmentId"));
        double fee = Double.parseDouble(request.getParameter("fee"));
        String confirmedDate = request.getParameter("confirmedDate");
        String confirmedTime = request.getParameter("confirmedTime");

        String sql = "UPDATE appointments SET status = 'confirmed', fee = ?, appt_date = ?, appt_time = ?, doctor_name = ? "
                + "WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, fee);
            ps.setString(2, confirmedDate);
            ps.setString(3, confirmedTime);
            ps.setString(4, approvingDoctorName);
            ps.setLong(5, appointmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.sendRedirect("doctor-dashboard.html");
    }
}