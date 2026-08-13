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
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/PatientLoginServlet")
public class PatientLoginServlet extends HttpServlet {

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/html");
        response.getWriter().println(
            "<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'></head>"
            + "<body style='padding:60px 24px'><div class='card card-pad' style='max-width:480px;margin:0 auto'>"
            + "<h2>Login failed</h2>"
            + "<p>" + message + "</p>"
            + "<a href='patient-login.html' class='btn btn-primary'>Try again</a>"
            + "</div></body></html>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email    = request.getParameter("email");
        String password = request.getParameter("password");

        String sql = "SELECT patient_id, full_name FROM patients WHERE email = ? AND password_hash = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HttpSession session = request.getSession();
                    session.setAttribute("patientId", rs.getInt("patient_id"));
                    session.setAttribute("patientName", rs.getString("full_name"));
                    response.sendRedirect("patient-dashboard.html");
                } else {
                    sendError(response, "Incorrect email or password.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            sendError(response, e.getMessage());
        }
    }
}