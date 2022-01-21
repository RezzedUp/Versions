/*
 * Copyright © 2022, RezzedUp <https://github.com/RezzedUp/Versions>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.versions;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.regex.Pattern;

public final class Versions
{
    private Versions() {}
    
    static final Pattern DOT_SEPARATOR = Pattern.compile("\\.");
    
    static final Pattern VALID_PRE_RELEASE_PATTERN =
        Pattern.compile("(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*");
    
    static final Pattern VALID_BUILD_METADATA_PATTERN =
        Pattern.compile("[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*");
    
    static final Pattern PARTIAL_SEMVER_PATTERN =
        Pattern.compile(
            "(?<major>0|[1-9]\\d*)(?:\\.(?<minor>0|[1-9]\\d*))?(?:\\.(?<patch>0|[1-9]\\d*))?" +
            "(?:-(?<prerelease>" + VALID_PRE_RELEASE_PATTERN + "))?" +
            "(?:\\+(?<buildmetadata>" + VALID_BUILD_METADATA_PATTERN + "))?"
        );
    
    static final Pattern VALID_SEMVER_PATTERN =
        Pattern.compile(
            "(?<major>0|[1-9]\\d*)\\.(?<minor>0|[1-9]\\d*)\\.(?<patch>0|[1-9]\\d*)" +
            "(?:-(?<prerelease>" + VALID_PRE_RELEASE_PATTERN + "))?" +
            "(?:\\+(?<buildmetadata>" + VALID_BUILD_METADATA_PATTERN + "))?"
        );
    
    static int onlyIfPositive(int number, String name)
    {
        if (number >= 0) { return number; }
        throw new IllegalArgumentException(name + " must be positive: " + number);
    }
    
    static boolean isNullOrEmpty(@NullOr String string)
    {
        return string == null || string.isEmpty();
    }
    
    static String emptyIfNull(@NullOr String string)
    {
        return (string == null) ? "" : string;
    }
    
    static @NullOr String onlyIfMatchesPattern(@NullOr String input, Pattern pattern, String name)
    {
        // Empty may as well be null, as far as Version is concerned.
        if (isNullOrEmpty(input)) { return null; }
        if (pattern.matcher(input).matches()) { return input; }
        
        throw new IllegalArgumentException(
            name + " must match pattern: `" + pattern + "` but received invalid input: \"" + input + "\""
        );
    }
    
    static int intOrZero(@NullOr String text)
    {
        return (text == null) ? 0 : Integer.parseInt(text);
    }
    
    public interface Comparable<T extends Comparable<T>> extends java.lang.Comparable<T>
    {
        default boolean greaterThan(T o)
        {
            return compareTo(o) > 0;
        }
        
        default boolean greaterThanOrEqualTo(T o)
        {
            return compareTo(o) >= 0;
        }
        
        default boolean equalTo(T version)
        {
            return compareTo(version) == 0;
        }
        
        default boolean lessThan(T o)
        {
            return compareTo(o) < 0;
        }
        
        default boolean lessThanOrEqualTo(T o)
        {
            return compareTo(o) <= 0;
        }
    }
}
