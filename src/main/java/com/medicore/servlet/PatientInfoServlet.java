package com.medicore.servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/PatientInfoServlet")
public class PatientInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        response.setContentType("text/plain");

        if (session != null && session.getAttribute("patientName") != null) {
            response.getWriter().print(session.getAttribute("patientName"));
        } else {
            response.getWriter().print("Guest");
        }
    }
}