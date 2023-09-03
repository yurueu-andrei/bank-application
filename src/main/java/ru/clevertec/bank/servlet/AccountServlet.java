package ru.clevertec.bank.servlet;

import ru.clevertec.bank.config.ApplicationConfig;
import ru.clevertec.bank.dto.AccountRequestDto;
import ru.clevertec.bank.dto.AccountResponseDto;
import ru.clevertec.bank.exception.BadRequestException;
import ru.clevertec.bank.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Controller for Accounts with <b>CRUD</b> operations and balance-manipulation operations.
 *
 * @author Andrei Yuryeu
 */
@WebServlet("/accounts/*")
public class AccountServlet extends HttpServlet {

    private AccountService accountService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        this.objectMapper = ApplicationConfig.getObjectMapper();
        this.accountService = ApplicationConfig.getAccountService();
    }

    /**
     * Retrieves account information based on the provided URI:
     * - If the URI contains an ID, it returns the corresponding account as an AccountDto.
     * - If the URI does not contain an ID, it lists accounts based on optional "page" and "size" query parameters.
     *
     * @param req  The HttpServletRequest object representing the HTTP request.
     * @param resp The HttpServletResponse object representing the HTTP response.
     * @throws IOException         If there is an issue with reading or writing data.
     * @throws BadRequestException If the HTTP request format is invalid.
     * @see AccountResponseDto
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
            var accountDto = accountService.findById(Long.valueOf(uriElements[2]));
            writer.print(objectMapper.writeValueAsString(accountDto));
        } else if (uriElements.length == 2) {
            String page = req.getParameter("page");
            String size = req.getParameter("size");
            int limit = size == null ? 20 : Integer.parseInt(size);
            int offset = page == null ? 0 : Integer.parseInt(page) * limit;
            List<AccountResponseDto> accountDtos = accountService.findAll(limit, offset);
            writer.print(objectMapper.writeValueAsString(accountDtos));
        } else {
            throw new BadRequestException("Invalid HTTP request format");
        }
        writer.close();
    }

    /**
     * Creates a new account using the provided JSON request body. Upon successful creation,
     * it redirects to the newly created account's URI.
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
            AccountRequestDto accountRequestDto;
            try {
                accountRequestDto = objectMapper.readValue(req.getInputStream(), AccountRequestDto.class);
            } catch (IOException e) {
                throw new BadRequestException("Invalid request body content");
            }
            var accountResponseDto = accountService.save(accountRequestDto);
            resp.sendRedirect(req.getContextPath() + req.getServletPath() + "/" + accountResponseDto.id());
        } else {
            throw new BadRequestException("Invalid HTTP request format");
        }
    }

    /**
     * Performs account transactions, such as withdrawals, refills, or transfers, based on the URI and query parameters.
     * The transaction type is determined by the path segments in the URI.
     *
     * @param req  The HttpServletRequest object representing the HTTP request.
     * @param resp The HttpServletResponse object representing the HTTP response.
     * @throws BadRequestException If the HTTP request format is invalid.
     */
    @Override
    protected void doPut(
            HttpServletRequest req,
            HttpServletResponse resp
    ) {
        String[] partsOfURI = req.getRequestURI().split("/");
        validatePutRequest(partsOfURI);
        String amount = req.getParameter("amount");
        String number = partsOfURI[2];
        String transactionType = partsOfURI[3];
        switch (transactionType) {
            case "withdraw" -> accountService.withdraw(
                    number,
                    new BigDecimal(amount));
            case "deposit" -> accountService.deposit(
                    number,
                    new BigDecimal(amount));
            case "transfer" -> {
                String receiverNumber = partsOfURI[4];
                accountService.transfer(
                        number,
                        receiverNumber,
                        new BigDecimal(amount));
            }
        }
    }

    /**
     * Deletes an account based on the provided account ID in the URI. If the deletion is successful,
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
        boolean result = accountService.delete(Long.valueOf(partsOfURI[2]));
        resp.setStatus(204);
        writer.print(result);
        writer.close();
    }

    private void validatePutRequest(String[] partsOfURI) {
        if (partsOfURI.length < 4 || partsOfURI.length > 5) {
            throw new BadRequestException("Invalid HTTP request format");
        }

        String transactionType = partsOfURI[3];
        if (partsOfURI.length == 4 && !("withdraw".equals(transactionType) || "deposit".equals(transactionType))) {
            throw new BadRequestException("Invalid HTTP request format");
        }

        if (partsOfURI.length == 5 && !("transfer".equals(transactionType) && !partsOfURI[2].equals(partsOfURI[4]))) {
            throw new BadRequestException("Invalid HTTP request format");
        }
    }

    /**
     * Validates the HTTP request to ensure it follows the expected format.
     *
     * @param request The HttpServletRequest object representing the HTTP request.
     * @throws BadRequestException If the HTTP request format is invalid or the account number
     *                             doesn't match the expected pattern.
     */
    private void validateRequestURIWithId(HttpServletRequest request) {
        String[] parts = request.getRequestURI().split("/");
        if (!(parts.length == 3 && parts[2].matches("^[1-9][0-9]*$"))) {
            throw new BadRequestException("Only entity plural name and its ID must be set in this request");
        }
    }
}
