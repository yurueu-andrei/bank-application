package ru.clevertec.bank.servlet;

import ru.clevertec.bank.dto.UserResponseDto;
import ru.clevertec.bank.exception.BadRequestException;
import ru.clevertec.bank.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserServlet userController;

    @Nested
    class doGet {

        @Test
        public void doGetWithValidURI_shouldReturnUser() throws IOException {
            //given
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);

            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);

            UserResponseDto userDto = new UserResponseDto(3L, "Ivan", "Kozlov", LocalDate.of(1998, 8, 1));

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            userController.setObjectMapper(mapper);

            //when
            when(userService.findById(3L)).thenReturn(userDto);
            when(request.getRequestURI()).thenReturn("/users/3");
            userController.doGet(request, response);

            //then
            verify(response).getWriter();
            verify(userService).findById(3L);

            String responseContent = stringWriter.toString();
            String expectedContent = "{\"id\":3,\"name\":\"Ivan\",\"surname\":\"Kozlov\",\"birthdate\":[1998,8,1]}";
            Assertions.assertEquals(expectedContent, responseContent);
        }

        @Test
        public void doGetWithValidURIAndParameters_shouldReturnEmptyArray() throws IOException {
            //given
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);

            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            userController.setObjectMapper(mapper);

            List<UserResponseDto> userDtos = new ArrayList<>();

            //when
            when(request.getRequestURI()).thenReturn("/users");
            when(request.getParameter("page")).thenReturn("2");
            when(request.getParameter("size")).thenReturn("2");
            when(userService.findAll(2, 4)).thenReturn(userDtos);
            userController.doGet(request, response);

            //then
            String responseContent = stringWriter.toString();
            String expectedContent = "[]";
            Assertions.assertEquals(expectedContent, responseContent);
        }

        @Test
        public void doGetWithInvalidURI_shouldThrowException() throws IOException {
            //given
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);

            //when
            when(request.getRequestURI()).thenReturn("/invalid/uri");
            when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

            //then
            Assertions.assertThrows(BadRequestException.class, () -> userController.doGet(request, response));
        }
    }
}
