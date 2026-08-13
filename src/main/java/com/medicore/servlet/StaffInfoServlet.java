package com.medicore.servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/StaffInfoServlet")
public class StaffInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        if (session != null && session.getAttribute("staffName") != null) {
            response.getWriter().print(session.getAttribute("staffName"));
        } else {
            response.getWriter().print("Guest");
        }
    }
}