package com.medicore.servlet;
import com.medicore.db.DBConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/PatientRegisterServlet")
public class PatientRegisterServlet extends HttpServlet {

    private void sendResult(HttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("text/html");
        response.getWriter().println(
            "<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'></head>"
            + "<body style='padding:60px 24px'><div class='card card-pad' style='max-width:480px;margin:0 auto'>"
            + "<h2>" + (success ? "Registration successful!" : "Registration failed") + "</h2>"
            + "<p>" + message + "</p>"
            + "<a href='" + (success ? "patient-login.html" : "patient-register.html") + "' class='btn btn-primary'>"
            + (success ? "Log in" : "Try again") + "</a>"
            + "</div></body></html>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName  = request.getParameter("fullName");
        String dob       = request.getParameter("dob");
        String gender    = request.getParameter("gender");
        String bloodGroup= request.getParameter("bloodGroup");
        String email     = request.getParameter("email");
        String phone     = request.getParameter("phone");
        String pincode   = request.getParameter("pincode");
        String address   = request.getParameter("address");
        String emergency = request.getParameter("emergencyContact");
        String password  = request.getParameter("password");

        String sql = "INSERT INTO patients "
                + "(full_name, dob, gender, blood_group, email, phone, pincode, address, emergency_contact, password_hash) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, dob);
            ps.setString(3, gender);
            ps.setString(4, bloodGroup);
            ps.setString(5, email);
            ps.setString(6, phone);
            ps.setString(7, pincode);
            ps.setString(8, address);
            ps.setString(9, emergency);
            ps.setString(10, password);

            ps.executeUpdate();

            sendResult(response, true, "Welcome, " + fullName + "! Your account has been created.");

        } catch (SQLException e) {
            e.printStackTrace();
            sendResult(response, false, e.getMessage());
        }
    }
}