package io.codelens.tools.thorntail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    @DisplayName("Should convert to kebab case")
    void test_should_convert_to_kebab_case() {
        assertAll(
                () -> assertEquals("generate-some-kebab-case", Utils.toKebabCase("generateSomeKebabCase")),
                () -> assertEquals("this123-is-ok-as-well", Utils.toKebabCase("this123IsOkAsWell")),
                () -> assertEquals("so-this", Utils.toKebabCase("SoThis"))
        );
    }
    
}