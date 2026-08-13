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

@WebServlet("/AmbulanceServlet")
public class AmbulanceServlet extends HttpServlet {

    private void sendResult(HttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(
            "<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'></head>"
            + "<body style='padding:60px 24px'><div class='card card-pad' style='max-width:480px;margin:0 auto'>"
            + "<h2>" + (success ? "Ambulance requested!" : "Request failed") + "</h2>"
            + "<p>" + message + "</p>"
            + "<a href='patient-dashboard.html' class='btn btn-primary'>Back to dashboard</a>"
            + "</div></body></html>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name       = request.getParameter("name");
        String phone       = request.getParameter("phone");
        String location    = request.getParameter("location");
        String condition   = request.getParameter("condition");

        HttpSession session = request.getSession(false);
        Integer patientId = (session != null) ? (Integer) session.getAttribute("patientId") : null;

        String sql = "INSERT INTO ambulance_requests (patient_id, caller_name, phone, pickup_location, condition_text) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (patientId != null) ps.setInt(1, patientId); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, name);
            ps.setString(3, phone);
            ps.setString(4, location);
            ps.setString(5, condition);

            ps.executeUpdate();

            sendResult(response, true,
                "Our team will call " + name + " at " + phone + " shortly. Pickup location: " + location);

        } catch (SQLException e) {
            e.printStackTrace();
            sendResult(response, false, e.getMessage());
        }
    }
}