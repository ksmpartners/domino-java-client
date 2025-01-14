package com.dominodatalab.api.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Unit")
class DominoVersionTest {

    @Test
    void testPojo() {
        // Arrange
        DominoVersion version = DominoVersion.builder().version("1.0").buildId("123").commitId("commit").build();

        // Act
        String toString = version.toString();
        int hashCode = version.hashCode();

        // Assert
        Assertions.assertEquals(1871293790, hashCode);
        Assertions.assertEquals("DominoVersion(buildId=123, buildUrl=null, commitId=commit, commitUrl=null, timestamp=null, version=1.0)", toString);
    }
}