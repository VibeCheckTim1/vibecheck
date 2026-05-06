package hr.tvz.vibecheck.exception.framework;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorEntryTest {

    @Test
    void record_shouldStoreCodeAndMessage() {
        ErrorEntry entry = new ErrorEntry(404, "Not found");

        assertThat(entry.code()).isEqualTo(404);
        assertThat(entry.message()).isEqualTo("Not found");
    }

    @Test
    void record_equality_shouldBeValueBased() {
        ErrorEntry entry1 = new ErrorEntry(400, "Bad request");
        ErrorEntry entry2 = new ErrorEntry(400, "Bad request");

        assertThat(entry1).isEqualTo(entry2);
        assertThat(entry1.hashCode()).hasSameHashCodeAs(entry2.hashCode());
    }

    @Test
    void record_toString_shouldContainFields() {
        ErrorEntry entry = new ErrorEntry(500, "Server error");

        String str = entry.toString();

        assertThat(str).contains("500").contains("Server error");
    }

    @Test
    void record_withNullMessage_shouldAllowNull() {
        ErrorEntry entry = new ErrorEntry(200, null);

        assertThat(entry.code()).isEqualTo(200);
        assertThat(entry.message()).isNull();
    }
}
