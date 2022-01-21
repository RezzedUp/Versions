/*
 * Copyright © 2022, RezzedUp <https://github.com/RezzedUp/Versions>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.versions;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionTests
{
    @Test
    public void testVersionSource()
    {
        Version ver = Version.of(1, 0, 0);
        
        assertThat((VersionSource) () -> ver)
            .matches(source -> source.version() == ver)
            .matches(source -> Comparator.comparing(VersionSource::version).compare(source, Version::zero) > 0);
    }
}
