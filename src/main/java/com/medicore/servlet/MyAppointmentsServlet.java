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

@WebServlet("/MyAppointmentsServlet")
public class MyAppointmentsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("patientId") == null) {
            response.getWriter().println("<tr><td colspan='7'>Please log in to see your appointments.</td></tr>");
            return;
        }

        int patientId = (Integer) session.getAttribute("patientId");
        String sql = "SELECT appointment_id, appt_date, doctor_name, department, status, fee, payment_status, bill_token "
                + "FROM appointments WHERE patient_id = ? ORDER BY appt_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder rows = new StringBuilder();
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    long apptId = rs.getLong("appointment_id");
                    String status = rs.getString("status");

                    String doctorName = rs.getString("doctor_name");
                    if (doctorName == null || doctorName.trim().isEmpty()) doctorName = "Any available";

                    double fee = rs.getDouble("fee");
                    boolean hasFee = !rs.wasNull();
                    String feeText = hasFee ? ("Rs. " + fee) : "-";

                    String paymentStatus = rs.getString("payment_status");
                    if (paymentStatus == null) paymentStatus = "unpaid";

                    String actionCell;
                    if ("confirmed".equals(status) && "unpaid".equals(paymentStatus)) {
                        actionCell = "<a class='btn btn-gold btn-sm' href='pay-appointment.html?apptId=" + apptId + "'>Pay Now</a>";
                    } else if ("paid".equals(paymentStatus)) {
                        String token = rs.getString("bill_token");
                        actionCell = "<span class='badge badge-ok'>Paid" + (token != null ? " - " + token : "") + "</span>";
                    } else if ("pay_at_hospital".equals(paymentStatus)) {
                        String token = rs.getString("bill_token");
                        actionCell = "<span class='badge badge-warn'>Pay at hospital" + (token != null ? " - " + token : "") + "</span>";
                    } else {
                        actionCell = "-";
                    }

                    rows.append("<tr><td class='mono'>").append(rs.getDate("appt_date"))
                        .append("</td><td>").append(doctorName)
                        .append("</td><td>").append(rs.getString("department"))
                        .append("</td><td><span class='badge badge-ok'>").append(status).append("</span>")
                        .append("</td><td class='mono'>").append(feeText)
                        .append("</td><td>").append(paymentStatus)
                        .append("</td><td>").append(actionCell)
                        .append("</td></tr>");
                }
                if (!any) rows.append("<tr><td colspan='7'>No upcoming appointments yet.</td></tr>");
                response.getWriter().println(rows.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<tr><td colspan='7'>Error loading appointments.</td></tr>");
        }
    }
}