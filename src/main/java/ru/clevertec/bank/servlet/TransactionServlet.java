package ru.clevertec.bank.servlet;

import ru.clevertec.bank.config.ApplicationConfig;
import ru.clevertec.bank.dto.TransactionResponseDto;
import ru.clevertec.bank.exception.BadRequestException;
import ru.clevertec.bank.service.TransactionService;
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
 * Controller for Banks with doGet method
 *
 * @author Andrei Yuryeu
 */
@WebServlet("/transactions/*")
public class TransactionServlet extends HttpServlet {

    private TransactionService transactionService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) {
        this.transactionService = ApplicationConfig.getTransactionService();
        this.objectMapper = ApplicationConfig.getObjectMapper();
    }

    /**
     * Retrieves transaction information based on the provided URI:
     * - If the URI contains an ID, it returns the corresponding transaction as a TransactionResponseDto.
     * - If the URI does not contain an ID, it lists transactions based on optional "page" and "size" query parameters.
     *
     * @param req  The HttpServletRequest object representing the HTTP request.
     * @param resp The HttpServletResponse object representing the HTTP response.
     * @throws IOException         If there is an issue with reading or writing data.
     * @throws BadRequestException If the HTTP request format is invalid.
     * @see TransactionResponseDto
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
            var userDto = transactionService.findById(Long.valueOf(uriElements[2]));
            writer.print(objectMapper.writeValueAsString(userDto));
        } else if (uriElements.length == 2) {
            String page = req.getParameter("page");
            String size = req.getParameter("size");
            int limit = size == null ? 20 : Integer.parseInt(size);
            int offset = page == null ? 0 : Integer.parseInt(page) * limit;
            List<TransactionResponseDto> transactionDtos = transactionService.findAll(limit, offset);
            writer.print(objectMapper.writeValueAsString(transactionDtos));
        } else {
            throw new BadRequestException("Invalid HTTP request format");
        }
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
