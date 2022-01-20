/*
 * Copyright © 2022, RezzedUp <https://github.com/RezzedUp/Versions>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.versions;

import pl.tlinkowski.annotation.basic.NullOr;
import pl.tlinkowski.annotation.basic.VisibleForTesting;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class Version implements Comparable<Version>
{
    private static final Pattern VALID_PRE_RELEASE_PATTERN =
        Pattern.compile("(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*");
    
    private static final Pattern VALID_BUILD_METADATA_PATTERN =
        Pattern.compile("[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*");
    
    private static final Pattern VALID_SEMVER_PATTERN =
        Pattern.compile(
            "(?<major>0|[1-9]\\d*)\\.(?<minor>0|[1-9]\\d*)\\.(?<patch>0|[1-9]\\d*)" +
            "(?:-(?<prerelease>" + VALID_PRE_RELEASE_PATTERN + "))?" +
            "(?:\\+(?<buildmetadata>" + VALID_BUILD_METADATA_PATTERN + "))?"
        );
    
    private static final Pattern PARTIAL_SEMVER_PATTERN =
        Pattern.compile(
            "(?<major>0|[1-9]\\d*)(?:\\.(?<minor>0|[1-9]\\d*))?(?:\\.(?<patch>0|[1-9]\\d*))?" +
            "(?:-(?<prerelease>" + VALID_PRE_RELEASE_PATTERN + "))?" +
            "(?:\\+(?<buildmetadata>" + VALID_BUILD_METADATA_PATTERN + "))?"
        );
    
    private static final Pattern DOT_SEPARATOR = Pattern.compile("\\.");
    
    private static final Pattern DIGITS = Pattern.compile("\\d+");
    
    /** Zero-version constant **/
    private static final Version ZERO = new Version(0, 0, 0, null, null);
    
    private static int onlyIfPositive(int number, String name)
    {
        if (number >= 0) { return number; }
        throw new IllegalArgumentException(name + " must be positive: " + number);
    }
    
    private static boolean isNullOrEmpty(@NullOr String string)
    {
        return string == null || string.isEmpty();
    }
    
    private static @NullOr String onlyIfMatchesPattern(@NullOr String input, Pattern pattern, String name)
    {
        // Empty may as well be null, as far as Version is concerned.
        if (isNullOrEmpty(input)) { return null; }
        if (pattern.matcher(input).matches()) { return input; }
        
        throw new IllegalArgumentException(
            name + " must match pattern: `" + pattern + "` but received invalid input: \"" + input + "\""
        );
    }
    
    private static int intOrZero(@NullOr String text)
    {
        return (text == null) ? 0 : Integer.parseInt(text);
    }
    
    public static Version of(int major, int minor, int patch, @NullOr String prerelease, @NullOr String build)
    {
        if (major == 0 && minor == 0 && patch == 0 && isNullOrEmpty(prerelease) && isNullOrEmpty(build))
        {
            return ZERO;
        }
        
        return new Version(
            onlyIfPositive(major, "major"),
            onlyIfPositive(minor, "minor"),
            onlyIfPositive(patch, "patch"),
            onlyIfMatchesPattern(prerelease, VALID_PRE_RELEASE_PATTERN, "prerelease"),
            onlyIfMatchesPattern(build, VALID_BUILD_METADATA_PATTERN, "build")
        );
    }
    
    public static Version of(int major, int minor, int patch, @NullOr String prerelease)
    {
        return Version.of(major, minor, patch, prerelease, null);
    }
    
    public static Version of(int major, int minor, int patch)
    {
        return Version.of(major, minor, patch, null, null);
    }
    
    public static Version of(int major, int minor)
    {
        return Version.of(major, minor, 0, null, null);
    }
    
    public static Version of(int major)
    {
        return Version.of(major, 0, 0, null, null);
    }
    
    private static Version parseOrThrow(Pattern pattern, String input)
    {
        Objects.requireNonNull(input, "input");
        Matcher matcher = pattern.matcher(input);
        
        if (!matcher.matches())
        {
            throw new IllegalArgumentException(
                "Version must match pattern: `" + pattern + "` but received invalid input: \"" + input + "\""
            );
        }
        
        int major = intOrZero(matcher.group("major"));
        int minor = intOrZero(matcher.group("minor"));
        int patch = intOrZero(matcher.group("patch"));
        @NullOr String prerelease = matcher.group("prerelease");
        @NullOr String build = matcher.group("buildmetadata");
        
        return Version.of(major, minor, patch, prerelease, build);
    }
    
    public static Version parseOrThrow(String input)
    {
        return parseOrThrow(PARTIAL_SEMVER_PATTERN, input);
    }
    
    public static Version parseStrictOrThrow(String input)
    {
        return parseOrThrow(VALID_SEMVER_PATTERN, input);
    }
    
    public static Optional<Version> parse(String input)
    {
        try { return Optional.of(parseOrThrow(input)); }
        catch (IllegalArgumentException ignored) { return Optional.empty(); }
    }
    
    public static Optional<Version> parseStrict(String input)
    {
        try { return Optional.of(parseStrictOrThrow(input)); }
        catch (IllegalArgumentException ignored) { return Optional.empty(); }
    }
    
    public static Builder builder() { return new Builder(); }
    
    public static Version zero() { return ZERO; }
    
    private final int major;
    private final int minor;
    private final int patch;
    private final @NullOr String prerelease;
    private final @NullOr String build;
    
    private Version(int major, int minor, int patch, @NullOr String prerelease, @NullOr String build)
    {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.prerelease = prerelease;
        this.build = build;
    }
    
    public int major() { return major; }
    
    public int minor() { return minor; }
    
    public int patch() { return patch; }
    
    public Optional<String> prerelease() { return Optional.ofNullable(prerelease); }
    
    public Optional<String> build() { return Optional.ofNullable(build); }
    
    @Override
    public int compareTo(Version o)
    {
        int diffMajor = major - o.major;
        if (diffMajor != 0) { return diffMajor; }
        
        int diffMinor = minor - o.minor;
        if (diffMinor != 0) { return diffMinor; }
        
        int diffPatch = patch - o.patch;
        if (diffPatch != 0) { return diffPatch; }
        
        if (prerelease == null)
        {
            if (o.prerelease != null) { return 1; }
        }
        else
        {
            if (o.prerelease == null) { return -1; }
            return comparePrereleaseStrings(prerelease, o.prerelease);
        }
        
        return 0;
    }
    
    // 11.4:    Precedence for two pre-release versions with the same major, minor, and patch version
    //          MUST be determined by comparing each dot separated identifier from left to right until
    //          a difference is found as follows:
    @VisibleForTesting
    static int comparePrereleaseStrings(String leftPrerelease, String rightPrerelease)
    {
        if (leftPrerelease.equals(rightPrerelease)) { return 0; }
        
        String[] leftIdentifiers = DOT_SEPARATOR.split(leftPrerelease);
        String[] rightIdentifiers = DOT_SEPARATOR.split(rightPrerelease);
        
        for (int i = 0; i < leftIdentifiers.length; i++)
        {
            String left = leftIdentifiers[i];
            
            // 11.4.4:  A larger set of pre-release fields has a higher precedence than a smaller set,
            //          if all of the preceding identifiers are equal.
            // (Right has fewer identifiers, so left takes precedence)
            if (rightIdentifiers.length <= i) { return 1; }
            
            String right = rightIdentifiers[i];
            
            boolean leftIsNumeric = DIGITS.matcher(left).matches();
            boolean rightIsNumeric = DIGITS.matcher(right).matches();
            
            // 11.4.1: Identifiers consisting of only digits are compared numerically.
            if (leftIsNumeric && rightIsNumeric)
            {
                int diff = Integer.parseInt(left) - Integer.parseInt(right);
                
                if (diff == 0) { continue; }
                else { return diff; }
            }
            // 11.4.3: Numeric identifiers always have lower precedence than non-numeric identifiers.
            else if (leftIsNumeric) { return -1; }
            else if (rightIsNumeric) { return 1; }
            
            // 11.4.2: Identifiers with letters or hyphens are compared lexically in ASCII sort order.
            int diff = left.compareTo(right);
            if (diff != 0) { return diff; }
        }
        
        // Right has more segments, which takes precedence over left
        if (rightIdentifiers.length > leftIdentifiers.length) { return -1; }
        
        // Equal
        return 0;
    }
    
    // Like equals, but disregards build metadata
    public boolean equivalentTo(Version version)
    {
        return compareTo(version) == 0;
    }
    
    public Builder toBuilder()
    {
        return new Builder(this);
    }
    
    @Override
    public String toString()
    {
        String ver = major + "." + minor + "." + patch;
        if (prerelease != null) { ver += "-" + prerelease; }
        if (build != null) { ver += "+" + build; }
        return ver;
    }
    
    @Override
    public boolean equals(@NullOr Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Version version = (Version) o;
        return major == version.major
            && minor == version.minor
            && patch == version.patch
            && Objects.equals(prerelease, version.prerelease)
            && Objects.equals(build, version.build);
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(major, minor, patch, prerelease, build);
    }
    
    public static final class Builder
    {
        private int major;
        private int minor;
        private int patch;
        private @NullOr String prerelease;
        private @NullOr String build;
        
        private Builder() {}
        
        private Builder(Version existing)
        {
            this.major = existing.major;
            this.minor = existing.minor;
            this.patch = existing.patch;
            this.prerelease = existing.prerelease;
            this.build = existing.build;
        }
        
        public Builder major(int major)
        {
            this.major = onlyIfPositive(major, "major");
            return this;
        }
        
        public Builder minor(int minor)
        {
            this.minor = onlyIfPositive(minor, "minor");
            return this;
        }
        
        public Builder patch(int patch)
        {
            this.patch = onlyIfPositive(patch, "patch");
            return this;
        }
        
        public Builder prerelease(@NullOr String prerelease)
        {
            this.prerelease = onlyIfMatchesPattern(prerelease, VALID_PRE_RELEASE_PATTERN, "prerelease");
            return this;
        }
        
        public Builder build(@NullOr String build)
        {
            this.build = onlyIfMatchesPattern(build, VALID_BUILD_METADATA_PATTERN, "build");
            return this;
        }
        
        public Version build()
        {
            return Version.of(major, minor, patch, prerelease, build);
        }
    }
}
