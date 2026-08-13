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

@WebServlet("/PatientStatsServlet")
public class PatientStatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("patientId") == null) {
            response.getWriter().print("-|0|0|-");
            return;
        }

        int patientId = (Integer) session.getAttribute("patientId");
        String bloodGroup = "-";
        int apptCount = 0;
        int rxCount = 0;
        String lastVisit = "-";

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT blood_group FROM patients WHERE patient_id = ?")) {
                ps.setInt(1, patientId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getString("blood_group") != null) {
                        bloodGroup = rs.getString("blood_group");
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM appointments WHERE patient_id = ? AND appt_date >= CURDATE()")) {
                ps.setInt(1, patientId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) apptCount = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM prescriptions WHERE patient_id = ? AND status = 'active'")) {
                ps.setInt(1, patientId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) rxCount = rs.getInt(1);
                }
            }

            // "Last visit" = shobcheye recent confirmed appointment jeta already past
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT MAX(appt_date) AS last_date FROM appointments "
                    + "WHERE patient_id = ? AND status = 'confirmed' AND appt_date < CURDATE()")) {
                ps.setInt(1, patientId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getDate("last_date") != null) {
                        lastVisit = rs.getDate("last_date").toString();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.getWriter().print(bloodGroup + "|" + apptCount + "|" + rxCount + "|" + lastVisit);
    }
}