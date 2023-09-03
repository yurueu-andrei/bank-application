package ru.clevertec.bank.servlet;

import ru.clevertec.bank.config.ApplicationConfig;
import ru.clevertec.bank.dto.BankRequestDto;
import ru.clevertec.bank.dto.BankResponseDto;
import ru.clevertec.bank.exception.BadRequestException;
import ru.clevertec.bank.service.BankService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Controller for Banks with <b>CRUD</b> operations
 *
 * @author Andrei Yuryeu
 */
@WebServlet("/banks/*")
public class BankServlet extends HttpServlet {

    private BankService bankService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) {
        this.bankService = ApplicationConfig.getBankService();
        this.objectMapper = ApplicationConfig.getObjectMapper();
    }

    /**
     * Retrieves bank information based on the provided URI:
     * - If the URI contains an ID, it returns the corresponding bank as a BankDto.
     * - If the URI does not contain an ID, it lists banks based on optional "page" and "size" query parameters.
     *
     * @param req  The HttpServletRequest object representing the HTTP request.
     * @param resp The HttpServletResponse object representing the HTTP response.
     * @throws IOException         If there is an issue with reading or writing data.
     * @throws BadRequestException If the HTTP request format is invalid.
     * @see BankResponseDto
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
            var bankDto = bankService.findById(Long.valueOf(uriElements[2]));
            writer.print(objectMapper.writeValueAsString(bankDto));
        } else if (uriElements.length == 2) {
            String page = req.getParameter("page");
            String size = req.getParameter("size");
            int limit = size == null ? 20 : Integer.parseInt(size);
            int offset = page == null ? 0 : Integer.parseInt(page) * limit;
            List<BankResponseDto> bankDtos = bankService.findAll(limit, offset);
            writer.print(objectMapper.writeValueAsString(bankDtos));
        } else {
            throw new BadRequestException("Invalid HTTP request format");
        }
        writer.close();
    }

    /**
     * Creates a new bank using the provided JSON request body. Upon successful creation,
     * it redirects to the newly created bank's URI.
     *
     * @param req  The HttpServletRequest object representing the HTTP request.
     * @param resp The HttpServletResponse object representing the HTTP response.
     * @throws IOException         If there is an issue with reading or writing data.
     * @throws BadRequestException If the request body content is invalid or if the HTTP request format is invalid.
     */
    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {
        if (req.getRequestURI().split("/").length == 2) {
            BankRequestDto bankRequestDto;
            try {
                bankRequestDto = objectMapper.readValue(req.getInputStream(), BankRequestDto.class);
            } catch (IOException e) {
                throw new BadRequestException("Invalid request body content");
            }
            var bankResponseDto = bankService.save(bankRequestDto);
            resp.sendRedirect(req.getContextPath() + req.getServletPath() + "/" + bankResponseDto.id());
        } else {
            throw new BadRequestException("Invalid HTTP request format");
        }
    }

    /**
     * Updates bank information based on the provided URI and JSON request body.
     * It redirects to the updated bank's URI upon success.
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
        BankRequestDto bankRequestDto;
        try {
            bankRequestDto = objectMapper.readValue(req.getInputStream(), BankRequestDto.class);
        } catch (IOException e) {
            throw new BadRequestException("Invalid request body content");
        }
        bankService.update(Long.parseLong(id), bankRequestDto);
        resp.sendRedirect(req.getContextPath() + req.getServletPath() + "/" + id);
    }

    /**
     * Deletes a bank based on the provided bank ID in the URI. If the deletion is successful,
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
        boolean result = bankService.delete(Long.parseLong(partsOfURI[2]));
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
