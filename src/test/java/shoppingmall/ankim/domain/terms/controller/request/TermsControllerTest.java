package shoppingmall.ankim.domain.terms.controller.request;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import shoppingmall.ankim.domain.email.controller.MailController;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(MailController.class)
@AutoConfigureMockMvc(addFilters = false)
class TermsControllerTest {

    @Test
    void getJoinTerms() {
    }
}