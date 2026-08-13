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

@WebServlet("/PharmacyOrderServlet")
public class PharmacyOrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerName  = request.getParameter("customerName");
        String customerEmail = request.getParameter("customerEmail");
        String itemsStr       = request.getParameter("items");
        String orderMode       = request.getParameter("orderMode");
        double subtotal         = Double.parseDouble(request.getParameter("subtotal"));
        double deliveryCharge   = Double.parseDouble(request.getParameter("deliveryCharge"));
        double total             = Double.parseDouble(request.getParameter("total"));
        String pincode           = request.getParameter("pincode");

        HttpSession session = request.getSession(false);
        Integer patientId = (session != null) ? (Integer) session.getAttribute("patientId") : null;

        response.setContentType("text/plain");

        try (Connection conn = DBConnection.getConnection()) {

            Integer zoneId = null;
            if (pincode != null && !pincode.isEmpty()) {
                try (PreparedStatement psZone = conn.prepareStatement(
                        "SELECT zone_id FROM delivery_zones WHERE pincode = ?")) {
                    psZone.setString(1, pincode);
                    try (ResultSet rs = psZone.executeQuery()) {
                        if (rs.next()) zoneId = rs.getInt("zone_id");
                    }
                }
            }

            String orderSql = "INSERT INTO orders (patient_id, customer_name, customer_email, order_mode, "
                    + "zone_id, delivery_charge, subtotal, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            long orderId;
            try (PreparedStatement psOrder = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                if (patientId != null) psOrder.setInt(1, patientId); else psOrder.setNull(1, Types.INTEGER);
                psOrder.setString(2, customerName);
                psOrder.setString(3, customerEmail);
                psOrder.setString(4, orderMode);
                if (zoneId != null) psOrder.setInt(5, zoneId); else psOrder.setNull(5, Types.INTEGER);
                psOrder.setDouble(6, deliveryCharge);
                psOrder.setDouble(7, subtotal);
                psOrder.setDouble(8, total);
                psOrder.executeUpdate();

                try (ResultSet keys = psOrder.getGeneratedKeys()) {
                    keys.next();
                    orderId = keys.getLong(1);
                }
            }

            String itemSql = "INSERT INTO order_items (order_id, medicine_id, quantity, price_each) "
                    + "SELECT ?, medicine_id, ?, price FROM medicines WHERE code = ?";
            String stockSql = "UPDATE medicines SET stock = stock - ? WHERE code = ?";

            String[] items = itemsStr.split(",");
            for (String item : items) {
                String[] parts = item.split(":");
                String medCode = parts[0];
                int qty = Integer.parseInt(parts[1]);

                try (PreparedStatement psItem = conn.prepareStatement(itemSql)) {
                    psItem.setLong(1, orderId);
                    psItem.setInt(2, qty);
                    psItem.setString(3, medCode);
                    psItem.executeUpdate();
                }
                try (PreparedStatement psStock = conn.prepareStatement(stockSql)) {
                    psStock.setInt(1, qty);
                    psStock.setString(2, medCode);
                    psStock.executeUpdate();
                }
            }

            response.getWriter().print(orderId);

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("ERROR: " + e.getMessage());
        }
    }
}