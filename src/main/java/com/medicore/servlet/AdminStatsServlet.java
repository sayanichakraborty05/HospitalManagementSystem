package com.medicore.servlet;
import com.medicore.db.DBConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;

@WebServlet("/AdminStatsServlet")
public class AdminStatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || !"admin".equals(session.getAttribute("staffRole"))) {
            response.getWriter().print("0|0|0|0");
            return;
        }

        int patients = 0, doctors = 0, apptToday = 0, lowStock = 0;

        try (Connection conn = DBConnection.getConnection()) {

            try (Statement st = conn.createStatement()) {
                ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM patients");
                if (rs.next()) patients = rs.getInt(1);

                rs = st.executeQuery("SELECT COUNT(*) FROM staff WHERE role='doctor' AND status='approved'");
                if (rs.next()) doctors = rs.getInt(1);

                rs = st.executeQuery("SELECT COUNT(*) FROM appointments WHERE appt_date = CURDATE()");
                if (rs.next()) apptToday = rs.getInt(1);

                rs = st.executeQuery("SELECT COUNT(*) FROM medicines WHERE stock <= 10");
                if (rs.next()) lowStock = rs.getInt(1);
            }

            response.getWriter().print(patients + "|" + doctors + "|" + apptToday + "|" + lowStock);

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().print("0|0|0|0");
        }
    }
}