package com.medicore.servlet;
import com.medicore.db.DBConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/MedicineListServlet")
public class MedicineListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String sql = "SELECT code, name, price, stock FROM medicines WHERE code IS NOT NULL ORDER BY medicine_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder out = new StringBuilder();
            while (rs.next()) {
                
                out.append(rs.getString("code")).append("~")
                   .append(rs.getString("name")).append("~")
                   .append(rs.getDouble("price")).append("~")
                   .append(rs.getInt("stock")).append("\n");
            }
            response.getWriter().print(out.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
