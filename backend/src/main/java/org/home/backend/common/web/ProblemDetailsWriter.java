package org.home.backend.common.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

@Component
public class ProblemDetailsWriter {

    private final ObjectMapper objectMapper;

    public ProblemDetailsWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void write(HttpServletResponse response, int status, String title, String detail, String instance)
            throws IOException {
        HttpStatus httpStatus = HttpStatus.valueOf(status);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(httpStatus, detail);
        problem.setTitle(title);
        problem.setInstance(URI.create(instance));

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
