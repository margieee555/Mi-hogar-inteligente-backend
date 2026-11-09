package com.hogar360.shared.exception;

import com.hogar360.auth.domain.exception.InvalidCredentialsException;
import com.hogar360.auth.domain.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerI18nTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        handler = new GlobalExceptionHandler(messageSource);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void sinIdiomaEspecificado_respondeEnEspañolConElCodigo() {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("es"));

        ResponseEntity<ApiError> response = handler.handleInvalidCredentials(new InvalidCredentialsException());

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody().code()).isEqualTo("error.auth.invalidCredentials");
        assertThat(response.getBody().message()).isEqualTo("Correo o contraseña incorrectos");
    }

    @Test
    void conIdiomaIngles_respondeEnIngles() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        ResponseEntity<ApiError> response = handler.handleInvalidCredentials(new InvalidCredentialsException());

        assertThat(response.getBody().code()).isEqualTo("error.auth.invalidCredentials");
        assertThat(response.getBody().message()).isEqualTo("Incorrect email or password");
    }

    @Test
    void conIdiomaNoSoportado_usaEspañol() {
        LocaleContextHolder.setLocale(Locale.FRENCH);

        ResponseEntity<ApiError> response = handler.handleInvalidCredentials(new InvalidCredentialsException());

        assertThat(response.getBody().message()).isEqualTo("Correo o contraseña incorrectos");
    }

    @Test
    void mensajeConArgumento_insertaElDato() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        ResponseEntity<ApiError> response = handler.handleUserExists(new UserAlreadyExistsException("ana@mail.com"));

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody().message())
                .isEqualTo("A user is already registered with the email: ana@mail.com");
    }

    @Test
    void errorDeDominio_usaSuCodigoYSusArgumentos() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        ResponseEntity<ApiError> response = handler.handleDomainValidation(
                new DomainValidationException("error.finance.description.tooLong",
                        "La descripción no puede superar 255 caracteres", 255));

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().code()).isEqualTo("error.finance.description.tooLong");
        assertThat(response.getBody().message()).isEqualTo("The description cannot exceed 255 characters");
    }

    @Test
    void codigoSinTraduccion_usaElMensajeOriginal() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        ResponseEntity<ApiError> response = handler.handleDomainValidation(
                new DomainValidationException("error.inventado", "Texto original"));

        assertThat(response.getBody().message()).isEqualTo("Texto original");
    }

    @Test
    void validacionDeCampos_traduceCadaCampo() throws Exception {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        BeanPropertyBindingResult binding = new BeanPropertyBindingResult(new Object(), "registerRequest");
        binding.addError(new FieldError("registerRequest", "password", null, false,
                new String[]{"Size.registerRequest.password", "Size.password", "Size"},
                new Object[]{}, "La contraseña debe tener al menos 8 caracteres"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                new MethodParameter(Object.class.getMethod("toString"), -1), binding);

        ResponseEntity<ApiError> response = handler.handleValidation(ex);

        assertThat(response.getBody().code()).isEqualTo("error.validation.failed");
        assertThat(response.getBody().message()).isEqualTo("One or more fields are not valid");
        assertThat(response.getBody().details())
                .containsExactly("password: The password must be at least 8 characters long");
    }
}
