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

@WebServlet("/StaffLoginServlet")
public class StaffLoginServlet extends HttpServlet {

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(
            "<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'></head>"
            + "<body style='padding:60px 24px'><div class='card card-pad' style='max-width:480px;margin:0 auto'>"
            + "<h2>Login failed</h2>"
            + "<p>" + message + "</p>"
            + "<a href='login.html' class='btn btn-primary'>Try again</a>"
            + "</div></body></html>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String role     = request.getParameter("role");
        String userId    = request.getParameter("userId"); // email
        String password  = request.getParameter("password");

        String sql = "SELECT staff_id, full_name, role, status FROM staff WHERE email = ? AND password_hash = ? AND role = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, password);
            ps.setString(3, role);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (!"approved".equals(rs.getString("status"))) {
                        sendError(response, "Your account is still pending admin approval.");
                        return;
                    }
                    HttpSession session = request.getSession();
                    session.setAttribute("staffId", rs.getInt("staff_id"));
                    session.setAttribute("staffName", rs.getString("full_name"));
                    session.setAttribute("staffRole", rs.getString("role"));

                    if ("admin".equals(role)) {
                        response.sendRedirect("admin-dashboard.html");
                    } else if ("doctor".equals(role)) {
                        response.sendRedirect("doctor-dashboard.html");
                    } else {
                        response.sendRedirect("index_1.html");
                    }
                } else {
                    sendError(response, "Incorrect email, password, or role.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            sendError(response, e.getMessage());
        }
    }
}