package co.harbor.calendly.controller.helper;

import co.harbor.calendly.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import static co.harbor.calendly.constants.Constants.*;

@Slf4j
public class ErrorHelper {
    public static ResponseEntity<Response> handleDataIntegrityViolationException(DataIntegrityViolationException violationException, String violationMsg) {
        log.error("Data integrity violation exception {}", violationException.getMessage(), violationException);
        return ResponseEntity.badRequest().body(new Response(violationMsg, ERROR));
    }

}
