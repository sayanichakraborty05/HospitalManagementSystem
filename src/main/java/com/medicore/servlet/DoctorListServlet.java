package com.medicore.servlet;
import com.medicore.db.DBConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/DoctorListServlet")
public class DoctorListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String sql = "SELECT full_name, department FROM staff WHERE role = 'doctor' AND status = 'approved' ORDER BY department, full_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder out = new StringBuilder();
            while (rs.next()) {

                out.append(rs.getString("department")).append("|").append(rs.getString("full_name")).append("\n");
            }
            response.getWriter().print(out.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}