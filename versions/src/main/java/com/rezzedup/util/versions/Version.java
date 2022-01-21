/*
 * Copyright © 2022, RezzedUp <https://github.com/RezzedUp/Versions>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.versions;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class Version implements Versions.Comparable<Version>
{
    /** Zero-version constant **/
    static final Version ZERO = new Version(VersionCore.ZERO, VersionMetadata.EMPTY, VersionMetadata.EMPTY);
    
    public static VersionCore core(int major, int minor, int patch)
    {
        return VersionCore.of(major, minor, patch);
    }
    
    public static VersionMetadata meta(Collection<String> identifiers)
    {
        return VersionMetadata.of(identifiers);
    }
    
    public static VersionMetadata meta(String meta)
    {
        if (meta.isEmpty()) { return VersionMetadata.EMPTY; }
        return meta(List.of(Versions.DOT_SEPARATOR.split(meta)));
    }
    
    public static Version of(VersionCore core, VersionMetadata prerelease, VersionMetadata build)
    {
        if (core == VersionCore.ZERO && prerelease == VersionMetadata.EMPTY && build == VersionMetadata.EMPTY)
        {
            return ZERO;
        }
        
        Objects.requireNonNull(core, "core");
        Objects.requireNonNull(prerelease, "prerelease");
        Objects.requireNonNull(build, "build");
        
        return new Version(core, prerelease, build);
    }
    
    public static Version of(int major, int minor, int patch, String prerelease, String build)
    {
        return Version.of(core(major, minor, patch), meta(prerelease), meta(build));
    }
    
    public static Version of(int major, int minor, int patch, String prerelease)
    {
        return Version.of(major, minor, patch, prerelease, "");
    }
    
    public static Version of(int major, int minor, int patch)
    {
        return Version.of(major, minor, patch, "", "");
    }
    
    public static Version of(int major, int minor)
    {
        return Version.of(major, minor, 0, "", "");
    }
    
    public static Version of(int major)
    {
        return Version.of(major, 0, 0, "", "");
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
        
        int major = Versions.intOrZero(matcher.group("major"));
        int minor = Versions.intOrZero(matcher.group("minor"));
        int patch = Versions.intOrZero(matcher.group("patch"));
        String prerelease = Versions.emptyIfNull(matcher.group("prerelease"));
        String build = Versions.emptyIfNull(matcher.group("buildmetadata"));
        
        return Version.of(major, minor, patch, prerelease, build);
    }
    
    public static Version parseOrThrow(String input)
    {
        return parseOrThrow(Versions.PARTIAL_SEMVER_PATTERN, input);
    }
    
    public static Version parseStrictOrThrow(String input)
    {
        return parseOrThrow(Versions.VALID_SEMVER_PATTERN, input);
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
    
    public static Version zero() { return ZERO; }
    
    public static Builder builder() { return ZERO.toBuilder(); }
    
    private final VersionCore core;
    private final VersionMetadata prerelease;
    private final VersionMetadata build;
    
    private Version(VersionCore core, VersionMetadata prerelease, VersionMetadata build)
    {
        this.core = core;
        this.prerelease = prerelease;
        this.build = build;
    }
    
    public VersionCore core() { return core; }
    
    public int major() { return core.major(); }
    
    public int minor() { return core.minor(); }
    
    public int patch() { return core.patch(); }
    
    public VersionMetadata prerelease() { return prerelease; }
    
    public VersionMetadata build() { return build; }
    
    public boolean hasMetadata() { return prerelease.isPresent() || build.isPresent(); }
    
    @Override
    public int compareTo(Version o)
    {
        int diff = core.compareTo(o.core);
        return (diff != 0) ? diff : prerelease.compareTo(o.prerelease);
    }
    
    public Builder toBuilder()
    {
        return new Builder();
    }
    
    @Override
    public String toString()
    {
        String ver = core.toString();
        if (!prerelease.isEmpty()) { ver += "-" + prerelease; }
        if (!build.isEmpty()) { ver += "+" + build; }
        return ver;
    }
    
    @Override
    public boolean equals(@NullOr Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Version version = (Version) o;
        return Objects.equals(core, version.core)
            && Objects.equals(prerelease, version.prerelease)
            && Objects.equals(build, version.build);
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(core, prerelease, build);
    }
    
    public final class Builder
    {
        private VersionCore.Builder core;
        private String prerelease;
        private String build;
        
        private Builder()
        {
            this.core = Version.this.core.toBuilder();
            this.prerelease = Version.this.prerelease.toString();
            this.build = Version.this.build.toString();
        }
        
        public Builder major(int major)
        {
            core.major(major);
            return this;
        }
        
        public Builder minor(int minor)
        {
            core.major(minor);
            return this;
        }
        
        public Builder patch(int patch)
        {
            core.patch(patch);
            return this;
        }
        
        public Builder core(VersionCore core)
        {
            this.core = core.toBuilder();
            return this;
        }
        
        public Builder prerelease(@NullOr String prerelease)
        {
            this.prerelease = Versions.emptyIfNull(Versions.onlyIfMatchesPattern(prerelease, Versions.VALID_PRE_RELEASE_PATTERN, "prerelease"));
            return this;
        }
        
        public Builder build(@NullOr String build)
        {
            this.build = Versions.emptyIfNull(Versions.onlyIfMatchesPattern(build, Versions.VALID_BUILD_METADATA_PATTERN, "build"));
            return this;
        }
        
        public Version build()
        {
            return Version.of(core.build(), meta(prerelease), meta(build));
        }
    }
    
}
