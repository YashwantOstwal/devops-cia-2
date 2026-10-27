package edu.university.equipment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BorrowerValidationTest {
    @Autowired private MockMvc mvc;
    @Autowired private BookingService service;

    @Test
    void validBorrowerIsAccepted() throws Exception {
        mvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON)
                .content("{\"borrowerId\":\"STU2001\",\"equipmentId\":\"TABLET01\",\"quantity\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
        assertEquals(7, service.getEquipment("TABLET01").getAvailableQuantity());
        assertEquals(1, bookingCount());
    }

    @Test
    void emptyBorrowerIsRejected() throws Exception {
        assertRejected("{\"borrowerId\":\"\",\"equipmentId\":\"TABLET01\",\"quantity\":1}");
    }

    @Test
    void whitespaceOnlyBorrowerIsRejected() throws Exception {
        assertRejected("{\"borrowerId\":\"   ",\"equipmentId\":\"TABLET01\",\"quantity\":1}");
    }

    @Test
    void missingBorrowerIsRejected() throws Exception {
        assertRejected("{\"equipmentId\":\"TABLET01\",\"quantity\":1}");
    }

    private void assertRejected(String request) throws Exception {
        mvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
        assertEquals(8, service.getEquipment("TABLET01").getAvailableQuantity());
        assertEquals(0, bookingCount());
    }

    private int bookingCount() {
        return ((Map<?, ?>) ReflectionTestUtils.getField(service, "bookings")).size();
    }
}
