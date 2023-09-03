package ru.clevertec.bank.servlet;

import ru.clevertec.bank.config.ApplicationConfig;
import ru.clevertec.bank.exception.BadRequestException;
import ru.clevertec.bank.exception.EntityNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION_TYPE;
import static jakarta.servlet.RequestDispatcher.ERROR_MESSAGE;
import static jakarta.servlet.RequestDispatcher.ERROR_REQUEST_URI;

@WebServlet("/error")
public class ExceptionHandlerServlet extends HttpServlet {

    private ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) {
        this.mapper = ApplicationConfig.getObjectMapper();
    }

    /**
     * Handles GET requests to the "/error" endpoint.
     * Invokes the handleException method to generate and send an appropriate error response.
     *
     * @param req  The HTTP request object.
     * @param resp The HTTP response object.
     * @throws IOException If an I/O error occurs while handling the request.
     */
    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {
        handleException(resp, req);
    }

    /**
     * Handles POST requests to the "/error" endpoint.
     * Invokes the handleException method to generate and send an appropriate error response.
     *
     * @param req  The HTTP request object.
     * @param resp The HTTP response object.
     * @throws IOException If an I/O error occurs while handling the request.
     */
    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {
        handleException(resp, req);
    }

    /**
     * Handles PUT requests to the "/error" endpoint.
     * Invokes the handleException method to generate and send an appropriate error response.
     *
     * @param req  The HTTP request object.
     * @param resp The HTTP response object.
     * @throws IOException If an I/O error occurs while handling the request.
     */
    @Override
    protected void doPut(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {
        handleException(resp, req);
    }

    /**
     * Handles DELETE requests to the "/error" endpoint.
     * Invokes the handleException method to generate and send an appropriate error response.
     *
     * @param req  The HTTP request object.
     * @param resp The HTTP response object.
     * @throws IOException If an I/O error occurs while handling the request.
     */
    @Override
    protected void doDelete(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {
        handleException(resp, req);
    }

    /**
     * Generates and sends an error response based on the exception type and HTTP status code.
     *
     * @param response The HTTP response object.
     * @param request  The HTTP request object containing information about the exception.
     * @throws IOException If an I/O error occurs while sending the error response.
     */
    private void handleException(HttpServletResponse response, HttpServletRequest request) throws IOException {
        try (PrintWriter writer = response.getWriter()) {
            Map<String, Object> details = new HashMap<>();
            details.put("URI", request.getAttribute(ERROR_REQUEST_URI));
            details.put("info", request.getAttribute(ERROR_MESSAGE));
            String responseValue = mapper
                    .writeValueAsString(new ApiCallDetailedError("Something gone wrong", details));
            Class<?> clazz = (Class<?>) request.getAttribute(ERROR_EXCEPTION_TYPE);
            if (Objects.equals(clazz, BadRequestException.class)) {
                response.setStatus(400);
            } else if (Objects.equals(clazz, EntityNotFoundException.class)) {
                response.setStatus(404);
            } else {
                response.setStatus(500);
            }
            writer.print(responseValue);
            writer.flush();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ApiCallDetailedError {
        private String message;
        private Map<String, Object> details;
    }
}
