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

@WebServlet("/StaffRegisterServlet")
public class StaffRegisterServlet extends HttpServlet {

    private void sendResult(HttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(
            "<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'></head>"
            + "<body style='padding:60px 24px'><div class='card card-pad' style='max-width:480px;margin:0 auto'>"
            + "<h2>" + (success ? "Request submitted!" : "Request failed") + "</h2>"
            + "<p>" + message + "</p>"
            + "<a href='" + (success ? "index_1.html" : "register.html") + "' class='btn btn-primary'>"
            + (success ? "Back to home" : "Try again") + "</a>"
            + "</div></body></html>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name       = request.getParameter("name");
        String role        = request.getParameter("role");
        String department  = request.getParameter("department");
        String email        = request.getParameter("email");
        String password     = request.getParameter("password");

        String sql = "INSERT INTO staff (full_name, role, department, email, password_hash, status) "
                + "VALUES (?, ?, ?, ?, ?, 'pending')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, role);
            ps.setString(3, department);
            ps.setString(4, email);
            ps.setString(5, password);

            ps.executeUpdate();

            sendResult(response, true,
                name + ", your request has been sent to the admin. You'll be able to log in once approved.");

        } catch (SQLException e) {
            e.printStackTrace();
            sendResult(response, false, e.getMessage());
        }
    }
}