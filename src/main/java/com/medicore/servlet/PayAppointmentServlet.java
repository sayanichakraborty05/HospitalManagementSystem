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
import java.sql.SQLException;

@WebServlet("/PayAppointmentServlet")
public class PayAppointmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("patientId") == null) {
            response.getWriter().print("ERROR: Not logged in");
            return;
        }

        long apptId = Long.parseLong(request.getParameter("apptId"));
        int patientId = (Integer) session.getAttribute("patientId");
        String paymentMethod = request.getParameter("paymentMethod"); // cash / upi / card

        String newStatus = "cash".equals(paymentMethod) ? "pay_at_hospital" : "paid";
        String billToken = "BILL-" + apptId + "-" + System.currentTimeMillis() % 100000;

        String sql = "UPDATE appointments SET payment_status = ?, bill_token = ? "
                + "WHERE appointment_id = ? AND patient_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setString(2, billToken);
            ps.setLong(3, apptId);
            ps.setInt(4, patientId);
            ps.executeUpdate();

            response.getWriter().print(billToken);

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().print("ERROR: " + e.getMessage());
        }
    }
}