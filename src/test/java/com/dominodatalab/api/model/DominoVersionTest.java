package com.dominodatalab.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Unit")
class DominoVersionTest {

    @Test
    void pojo() {
        // Arrange
        DominoVersion version = DominoVersion.builder().version("1.0").buildId("123").commitId("commit").build();

        // Act
        String toString = version.toString();
        int hashCode = version.hashCode();

        // Assert
        assertEquals(1871293790, hashCode);
        assertEquals("DominoVersion(buildId=123, buildUrl=null, commitId=commit, commitUrl=null, timestamp=null, version=1.0)", toString);
    }
}