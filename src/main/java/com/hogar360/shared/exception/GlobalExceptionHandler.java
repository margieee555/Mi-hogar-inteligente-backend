package com.hogar360.shared.exception;

import com.hogar360.auth.domain.exception.InvalidCredentialsException;
import com.hogar360.auth.domain.exception.InvalidGoogleTokenException;
import com.hogar360.auth.domain.exception.InvalidRefreshTokenException;
import com.hogar360.auth.domain.exception.UserAlreadyExistsException;
import com.hogar360.finance.domain.exception.HouseholdRequiredException;
import com.hogar360.finance.domain.exception.TransactionNotFoundException;
import com.hogar360.household.domain.exception.HouseholdAccessDeniedException;
import com.hogar360.household.domain.exception.HouseholdNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.hogar360.task.domain.exception.TaskHouseholdRequiredException;
import com.hogar360.task.domain.exception.TaskNotFoundException;
import com.hogar360.inventory.domain.exception.InventoryHouseholdRequiredException;
import com.hogar360.inventory.domain.exception.InventoryItemNotFoundException;
import com.hogar360.shopping.domain.exception.ShoppingHouseholdRequiredException;
import com.hogar360.shopping.domain.exception.ShoppingItemNotFoundException;

import com.hogar360.household.domain.exception.AlreadyInHouseholdException;
import com.hogar360.household.domain.exception.InvitationExpiredException;
import com.hogar360.household.domain.exception.InvitationNotFoundException;
import com.hogar360.household.domain.exception.InvitationNotPendingException;
import com.hogar360.household.domain.exception.MemberNotFoundException;
import java.util.List;
import java.util.Locale;

/**
 * Traduce excepciones a respuestas ApiError. Cada error lleva un "code" estable y un
 * "message" en el idioma de la petición (Accept-Language); si falta la traducción, se usa
 * el texto original de la excepción.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserExists(UserAlreadyExistsException ex) {
        return build(HttpStatus.CONFLICT, "Conflict", "error.auth.userAlreadyExists", ex.getMessage(), ex.getEmail());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", "error.auth.invalidCredentials", ex.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", "error.auth.invalidRefreshToken", ex.getMessage());
    }

    @ExceptionHandler(InvalidGoogleTokenException.class)
    public ResponseEntity<ApiError> handleInvalidGoogleToken(InvalidGoogleTokenException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", "error.auth.invalidGoogleToken", ex.getMessage());
    }

    @ExceptionHandler(HouseholdNotFoundException.class)
    public ResponseEntity<ApiError> handleHouseholdNotFound(HouseholdNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found", "error.household.notFound", ex.getMessage());
    }

    @ExceptionHandler(HouseholdAccessDeniedException.class)
    public ResponseEntity<ApiError> handleHouseholdAccessDenied(HouseholdAccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "Forbidden", "error.household.accessDenied", ex.getMessage());
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ApiError> handleTransactionNotFound(TransactionNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found", "error.finance.transactionNotFound",
                ex.getMessage(), ex.getTransactionId());
    }

    @ExceptionHandler(HouseholdRequiredException.class)
    public ResponseEntity<ApiError> handleHouseholdRequired(HouseholdRequiredException ex) {
        return build(HttpStatus.CONFLICT, "Conflict", "error.household.required", ex.getMessage());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiError> handleTaskNotFound(TaskNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found", "error.task.notFound",
                ex.getMessage(), ex.getTaskId());
    }

    @ExceptionHandler(TaskHouseholdRequiredException.class)
    public ResponseEntity<ApiError> handleTaskHouseholdRequired(TaskHouseholdRequiredException ex) {
        return build(HttpStatus.CONFLICT, "Conflict", "error.household.required", ex.getMessage());
    }
    
    @ExceptionHandler(InventoryItemNotFoundException.class)
    public ResponseEntity<ApiError> handleInventoryItemNotFound(InventoryItemNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found", "error.inventory.notFound",
                ex.getMessage(), ex.getItemId());
    }

    @ExceptionHandler(InventoryHouseholdRequiredException.class)
    public ResponseEntity<ApiError> handleInventoryHouseholdRequired(InventoryHouseholdRequiredException ex) {
        return build(HttpStatus.CONFLICT, "Conflict", "error.household.required", ex.getMessage());
    }

        @ExceptionHandler(ShoppingItemNotFoundException.class)
    public ResponseEntity<ApiError> handleShoppingItemNotFound(ShoppingItemNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found", "error.shopping.notFound",
                ex.getMessage(), ex.getItemId());
    }

    @ExceptionHandler(ShoppingHouseholdRequiredException.class)
    public ResponseEntity<ApiError> handleShoppingHouseholdRequired(ShoppingHouseholdRequiredException ex) {
        return build(HttpStatus.CONFLICT, "Conflict", "error.household.required", ex.getMessage());
    }
    
    @ExceptionHandler(InvitationNotFoundException.class)
    public ResponseEntity<ApiError> handleInvitationNotFound(InvitationNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found", "error.invitation.notFound", ex.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiError> handleMemberNotFound(MemberNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found", "error.household.member.notFound", ex.getMessage());
    }

    @ExceptionHandler(InvitationNotPendingException.class)
    public ResponseEntity<ApiError> handleInvitationNotPending(InvitationNotPendingException ex) {
        return build(HttpStatus.CONFLICT, "Conflict", "error.invitation.notPending", ex.getMessage());
    }

    @ExceptionHandler(InvitationExpiredException.class)
    public ResponseEntity<ApiError> handleInvitationExpired(InvitationExpiredException ex) {
        return build(HttpStatus.CONFLICT, "Conflict", "error.invitation.expired", ex.getMessage());
    }

    @ExceptionHandler(AlreadyInHouseholdException.class)
    public ResponseEntity<ApiError> handleAlreadyInHousehold(AlreadyInHouseholdException ex) {
        return build(HttpStatus.CONFLICT, "Conflict", "error.household.alreadyMember", ex.getMessage());
    }

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ApiError> handleDomainValidation(DomainValidationException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getCode(), ex.getMessage(), ex.getArgs());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", "error.badRequest", ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", "error.badRequest.body",
                "El cuerpo de la petición no es válido o contiene valores no permitidos");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", "error.badRequest.param",
                "El valor no es válido para el parámetro '" + ex.getName() + "'", ex.getValue(), ex.getName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        // Cada FieldError trae códigos como "Size.registerRequest.password": si hay traducción se usa,
        // si no, queda el mensaje original de la anotación.
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + messageSource.getMessage(f, locale))
                .toList();
        String message = messageSource.getMessage("error.validation.failed", null,
                "Uno o más campos no son válidos", locale);
        ApiError error = ApiError.of(HttpStatus.BAD_REQUEST.value(), "Validation Failed",
                "error.validation.failed", message, details);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        // Errores estándar de Spring MVC (404, 405, 415, parámetro faltante...) conservan su código 4xx
        if (ex instanceof ErrorResponse errorResponse && errorResponse.getStatusCode().is4xxClientError()) {
            HttpStatusCode status = errorResponse.getStatusCode();
            HttpStatus resolved = HttpStatus.resolve(status.value());
            String reason = resolved != null ? resolved.getReasonPhrase() : "Error";
            String detail = errorResponse.getBody().getDetail();
            return build(status, reason, "error.http." + status.value(),
                    detail != null ? detail : "La solicitud no es válida");
        }

        log.error("Error inesperado", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "error.unexpected",
                "Ocurrió un error inesperado");
    }

    private ResponseEntity<ApiError> build(HttpStatusCode status, String error, String code,
                                           String fallback, Object... args) {
        String message = messageSource.getMessage(code, args, fallback, LocaleContextHolder.getLocale());
        return ResponseEntity.status(status).body(ApiError.of(status.value(), error, code, message));
    }
}