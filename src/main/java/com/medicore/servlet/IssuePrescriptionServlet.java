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

@WebServlet("/IssuePrescriptionServlet")
public class IssuePrescriptionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || !"doctor".equals(session.getAttribute("staffRole"))) {
            response.getWriter().print("ERROR: Not logged in as doctor");
            return;
        }

        int doctorId = (Integer) session.getAttribute("staffId");
        int patientId = Integer.parseInt(request.getParameter("patientId"));
        String appointmentIdStr = request.getParameter("appointmentId");
        String validTill = request.getParameter("validTill");
        String itemsData = request.getParameter("itemsData");

        try (Connection conn = DBConnection.getConnection()) {

            String prescSql = "INSERT INTO prescriptions (patient_id, doctor_id, appointment_id, issued_date, valid_till, status) "
                    + "VALUES (?, ?, ?, CURDATE(), ?, 'active')";
            long prescriptionId;
            try (PreparedStatement ps = conn.prepareStatement(prescSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, patientId);
                ps.setInt(2, doctorId);
                if (appointmentIdStr != null && !appointmentIdStr.isEmpty()) {
                    ps.setInt(3, Integer.parseInt(appointmentIdStr));
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                ps.setString(4, validTill);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    prescriptionId = keys.getLong(1);
                }
            }

            String itemSql = "INSERT INTO prescription_items (prescription_id, medicine_name, dosage, duration) "
                    + "VALUES (?, ?, ?, ?)";
            String[] items = itemsData.split("\\|");
            try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                for (String item : items) {
                    String[] parts = item.split("~");
                    if (parts.length < 3) continue;
                    ps.setLong(1, prescriptionId);
                    ps.setString(2, parts[0]);
                    ps.setString(3, parts[1]);
                    ps.setString(4, parts[2]);
                    ps.executeUpdate();
                }
            }

            response.getWriter().print("OK");

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().print("ERROR: " + e.getMessage());
        }
    }
}