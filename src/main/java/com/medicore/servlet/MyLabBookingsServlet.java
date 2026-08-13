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

@WebServlet("/MyLabBookingsServlet")
public class MyLabBookingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("patientId") == null) {
            response.getWriter().println("<p class='form-hint'>Log in to see your lab test bookings.</p>");
            return;
        }

        int patientId = (Integer) session.getAttribute("patientId");
        String bookingSql = "SELECT booking_id, booking_date, mode, total_amount FROM lab_bookings "
                + "WHERE patient_id = ? ORDER BY booking_id DESC";
        String itemsSql = "SELECT lt.name FROM lab_booking_items lbi "
                + "JOIN lab_tests lt ON lt.test_id = lbi.test_id WHERE lbi.booking_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psBooking = conn.prepareStatement(bookingSql)) {

            psBooking.setInt(1, patientId);
            try (ResultSet rs = psBooking.executeQuery()) {
                StringBuilder out = new StringBuilder();
                boolean any = false;

                while (rs.next()) {
                    any = true;
                    long bookingId = rs.getLong("booking_id");

                    java.util.List<String> testNames = new java.util.ArrayList<>();
                    try (PreparedStatement psItems = conn.prepareStatement(itemsSql)) {
                        psItems.setLong(1, bookingId);
                        try (ResultSet rsItems = psItems.executeQuery()) {
                            while (rsItems.next()) testNames.add(rsItems.getString("name"));
                        }
                    }

                    out.append("<div class='med-card' style='flex-direction:column;align-items:flex-start;gap:4px'>")
                       .append("<div style='display:flex;justify-content:space-between;width:100%'>")
                       .append("<strong>Booking #LB").append(bookingId).append("</strong>")
                       .append("<span class='mono'>Rs. ").append(rs.getDouble("total_amount")).append("</span>")
                       .append("</div>")
                       .append("<div class='med-meta'>").append(rs.getDate("booking_date"))
                       .append(" - ").append("home_collection".equals(rs.getString("mode")) ? "Home sample collection" : "Walk-in")
                       .append("</div>")
                       .append("<div class='med-meta'>").append(String.join(", ", testNames)).append("</div>")
                       .append("</div>");
                }

                if (!any) out.append("<p class='form-hint'>No lab tests booked yet.</p>");
                response.getWriter().println(out.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<p class='form-hint'>Error loading bookings.</p>");
        }
    }
}