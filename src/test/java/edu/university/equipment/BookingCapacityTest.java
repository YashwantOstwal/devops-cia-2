package edu.university.equipment;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Map;

class BookingCapacityTest {
    private BookingService service;

    @BeforeEach
    void setUp() { service = new BookingService(); }

    @Test
    void exactAvailableQuantityIsAllowed() {
        Booking booking = service.create("STU1001", "PROJECTOR01", 4);
        assertEquals("CONFIRMED", booking.getStatus());
        assertEquals(0, service.getEquipment("PROJECTOR01").getAvailableQuantity());
    }

    @Test
    void oneOverAvailableIsRejectedWithoutInventoryOrBookingChange() {
        assertThrows(IllegalStateException.class,
                () -> service.create("STU1001", "PROJECTOR01", 5));
        assertEquals(4, service.getEquipment("PROJECTOR01").getAvailableQuantity());
        assertEquals(0, bookingCount());
    }

    @Test
    void rejectedBookingDoesNotAffectExistingBooking() {
        Booking first = service.create("STU1001", "PROJECTOR01", 2);
        assertThrows(IllegalStateException.class,
                () -> service.create("STU1002", "PROJECTOR01", 3));
        assertEquals(2, service.getEquipment("PROJECTOR01").getAvailableQuantity());
        assertEquals("CONFIRMED", service.getBooking(first.getId()).getStatus());
        assertEquals(1, bookingCount());
    }

    private int bookingCount() {
        return ((Map<?, ?>) ReflectionTestUtils.getField(service, "bookings")).size();
    }
}
