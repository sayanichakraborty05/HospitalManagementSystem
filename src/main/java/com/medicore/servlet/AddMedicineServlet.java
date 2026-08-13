package com.medicore.servlet;
import com.medicore.db.DBConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AddMedicineServlet")
public class AddMedicineServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String name=request.getParameter("name");
        double price=Double.parseDouble(request.getParameter("price"));
        int stock=Integer.parseInt(request.getParameter("stock"));

        try{

            Connection con=DBConnection.getConnection();

            PreparedStatement ps=con.prepareStatement(
            "insert into medicines(name,price,stock) values(?,?,?)");

            ps.setString(1,name);
            ps.setDouble(2,price);
            ps.setInt(3,stock);

            ps.executeUpdate();

            response.sendRedirect("admin-pharmacy.html");

        }catch(Exception e){
            e.printStackTrace();
        }

    }

}