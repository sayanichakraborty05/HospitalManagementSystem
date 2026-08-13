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

@WebServlet("/FeedbackServlet")
public class FeedbackServlet extends HttpServlet {

    private void sendResult(HttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(
            "<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'></head>"
            + "<body style='padding:60px 24px'><div class='card card-pad' style='max-width:480px;margin:0 auto'>"
            + "<h2>" + (success ? "Thank you!" : "Submit failed") + "</h2>"
            + "<p>" + message + "</p>"
            + "<a href='patient-dashboard.html' class='btn btn-primary'>Back to dashboard</a>"
            + "</div></body></html>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name       = request.getParameter("name");
        String department = request.getParameter("department");
        String rating      = request.getParameter("rating");
        String comments    = request.getParameter("comments");

        String sql = "INSERT INTO feedback (name, department, rating, comments) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, department);
            ps.setInt(3, Integer.parseInt(rating));
            ps.setString(4, comments);

            ps.executeUpdate();

            sendResult(response, true, "Your feedback has been received, " + name + ".");

        } catch (SQLException e) {
            e.printStackTrace();
            sendResult(response, false, e.getMessage());
        }
    }
}