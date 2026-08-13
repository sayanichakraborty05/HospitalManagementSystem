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

@WebServlet("/ContactServlet")
public class ContactServlet extends HttpServlet {

    private void sendResult(HttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("text/html");
        response.getWriter().println(
            "<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'></head>"
            + "<body style='padding:60px 24px'><div class='card card-pad' style='max-width:480px;margin:0 auto'>"
            + "<h2>" + (success ? "Message sent!" : "Send failed") + "</h2>"
            + "<p>" + message + "</p>"
            + "<a href='index_1.html' class='btn btn-primary'>Back to home</a>"
            + "</div></body></html>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name    = request.getParameter("name");
        String email    = request.getParameter("email");
        String message  = request.getParameter("message");

        String sql = "INSERT INTO contact_messages (name, email, message) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, message);

            ps.executeUpdate();

            sendResult(response, true, "Thanks, " + name + " — we'll get back to you soon.");

        } catch (SQLException e) {
            e.printStackTrace();
            sendResult(response, false, e.getMessage());
        }
    }
}