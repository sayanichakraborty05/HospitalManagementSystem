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

@WebServlet("/AdminOrderServlet")
public class AdminOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || !"admin".equals(session.getAttribute("staffRole"))) {
            response.getWriter().println("<tr><td colspan='6'>Please log in as admin.</td></tr>");
            return;
        }

        String sql = "SELECT order_id, customer_name, order_mode, total, status, created_at "
                + "FROM orders ORDER BY created_at DESC LIMIT 30";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder rows = new StringBuilder();
            boolean any = false;
            while (rs.next()) {
                any = true;
                long orderId = rs.getLong("order_id");
                String currentStatus = rs.getString("status");

                rows.append("<tr>")
                    .append("<td>MC").append(orderId).append("</td>")
                    .append("<td>").append(rs.getString("customer_name")).append("</td>")
                    .append("<td>").append(rs.getString("order_mode")).append("</td>")
                    .append("<td class='mono'>Rs. ").append(rs.getDouble("total")).append("</td>")
                    .append("<td>").append(currentStatus).append("</td>")
                    .append("<td>")
                    .append("<form style='display:flex;gap:6px' method='POST' action='UpdateOrderStatusServlet'>")
                    .append("<input type='hidden' name='orderId' value='").append(orderId).append("'>")
                    .append("<select name='newStatus'>")
                    .append(statusOption("confirmed", currentStatus))
                    .append(statusOption("preparing", currentStatus))
                    .append(statusOption("out_for_delivery", currentStatus))
                    .append(statusOption("delivered", currentStatus))
                    .append(statusOption("ready_for_pickup", currentStatus))
                    .append("</select>")
                    .append("<button type='submit' class='btn btn-primary btn-sm'>Update</button>")
                    .append("</form>")
                    .append("</td>")
                    .append("</tr>");
            }
            if (!any) rows.append("<tr><td colspan='6'>No orders yet.</td></tr>");
            response.getWriter().println(rows.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<tr><td colspan='6'>Error loading orders.</td></tr>");
        }
    }

    private String statusOption(String value, String current) {
        String selected = value.equals(current) ? " selected" : "";
        return "<option value='" + value + "'" + selected + ">" + value + "</option>";
    }
}