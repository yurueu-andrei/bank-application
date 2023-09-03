package ru.clevertec.bank.servlet;

import ru.clevertec.bank.config.ApplicationConfig;
import ru.clevertec.bank.dto.UserRequestDto;
import ru.clevertec.bank.dto.UserResponseDto;
import ru.clevertec.bank.exception.BadRequestException;
import ru.clevertec.bank.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Controller for Users with <b>CRUD</b> operations
 *
 * @author Andrei Yuryeu
 */
@WebServlet("/users/*")
@Setter
public class UserServlet extends HttpServlet {

    private UserService userService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) {
        this.userService = ApplicationConfig.getUserService();
        this.objectMapper = ApplicationConfig.getObjectMapper();
    }

    /**
     * Retrieves user information based on the provided URI:
     * - If the URI contains an ID, it returns the corresponding user as a UserResponseDto.
     * - If the URI does not contain an ID, it lists users based on optional "page" and "size" query parameters.
     *
     * @param req  The HttpServletRequest object representing the HTTP request.
     * @param resp The HttpServletResponse object representing the HTTP response.
     * @throws IOException         If there is an issue with reading or writing data.
     * @throws BadRequestException If the HTTP request format is invalid.
     * @see UserResponseDto
     */
    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {
        PrintWriter writer = resp.getWriter();
        String[] uriElements = req.getRequestURI().split("/");
        if (uriElements.length == 3) {
            validateRequestURIWithId(req);
            var userDto = userService.findById(Long.valueOf(uriElements[2]));
            writer.print(objectMapper.writeValueAsString(userDto));
        } else if (uriElements.length == 2) {
            String page = req.getParameter("page");
            String size = req.getParameter("size");
            int limit = size == null ? 20 : Integer.parseInt(size);
            int offset = page == null ? 0 : Integer.parseInt(page) * limit;
            List<UserResponseDto> userDtos = userService.findAll(limit, offset);
            writer.print(objectMapper.writeValueAsString(userDtos));
        } else {
            throw new BadRequestException("Invalid HTTP request format");
        }
        writer.close();
    }

    /**
     * Retrieves user information based on the provided URI:
     * - If the URI contains an ID, it returns the corresponding user as a UserResponseDto.
     * - If the URI does not contain an ID, it lists users based on optional "page" and "size" query parameters.
     *
     * @param req  The HttpServletRequest object representing the HTTP request.
     * @param resp The HttpServletResponse object representing the HTTP response.
     * @throws IOException         If there is an issue with reading or writing data.
     * @throws BadRequestException If the HTTP request format is invalid.
     * @see UserResponseDto
     */
    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {
        if (req.getRequestURI().split("/").length == 2) {
            UserRequestDto userRequestDto;
            try {
                userRequestDto = objectMapper.readValue(req.getInputStream(), UserRequestDto.class);
            } catch (IOException e) {
                throw new BadRequestException("Invalid request body content");
            }
            var userResponseDto = userService.save(userRequestDto);
            resp.sendRedirect(req.getContextPath() + req.getServletPath() + "/" + userResponseDto.id());
        } else {
            throw new BadRequestException("Invalid HTTP request format");
        }
    }

    /**
     * Updates user information based on the provided URI and JSON request body.
     * It redirects to the updated user's URI upon success.
     *
     * @param req  The HttpServletRequest object representing the HTTP request.
     * @param resp The HttpServletResponse object representing the HTTP response.
     * @throws IOException         If there is an issue with reading or writing data.
     * @throws BadRequestException If the request body content is invalid or if the HTTP request format is invalid.
     */
    @Override
    protected void doPut(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {
        validateRequestURIWithId(req);
        String[] partsOfURI = req.getRequestURI().split("/");
        String id = partsOfURI[2];
        UserRequestDto userRequestDto;
        try {
            userRequestDto = objectMapper.readValue(req.getInputStream(), UserRequestDto.class);
        } catch (IOException e) {
            throw new BadRequestException("Invalid request body content");
        }
        userService.update(Long.parseLong(id), userRequestDto);
        resp.sendRedirect(req.getContextPath() + req.getServletPath() + "/" + id);
    }

    /**
     * Deletes a user based on the provided user ID in the URI. If the deletion is successful,
     * it sets the response status to 204 (No Content).
     *
     * @param req  The HttpServletRequest object representing the HTTP request.
     * @param resp The HttpServletResponse object representing the HTTP response.
     * @throws IOException         If there is an issue with writing data.
     * @throws BadRequestException If the HTTP request format is invalid.
     */
    @Override
    protected void doDelete(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {
        PrintWriter writer = resp.getWriter();
        validateRequestURIWithId(req);
        String[] partsOfURI = req.getRequestURI().split("/");
        boolean result = userService.delete(Long.parseLong(partsOfURI[2]));
        resp.setStatus(204);
        writer.print(result);
        writer.close();
    }

    /**
     * Validates the HTTP request to ensure it follows the expected format.
     *
     * @param request The HttpServletRequest object representing the HTTP request.
     * @throws BadRequestException If the HTTP request format is invalid or the account number doesn't match the expected pattern.
     */
    private void validateRequestURIWithId(HttpServletRequest request) {
        String[] parts = request.getRequestURI().split("/");
        if (!(parts.length == 3 && parts[2].matches("^[1-9][0-9]*$"))) {
            throw new BadRequestException("Only entity plural name and its ID must be set in this request");
        }
    }
}
