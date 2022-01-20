package com.rezzedup.util.versions;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

public class VersionTests
{
    @Test
    public void testVersionSource()
    {
        Version ver = Version.of(1, 0, 0);
        
        Assertions.assertThat((VersionSource) () -> ver)
            .isInstanceOf(VersionSource.class)
            .matches(source -> source.version() == ver)
            .matches(source -> Comparator.comparing(VersionSource::version).compare(source, Version::zero) > 0);
    }
}
