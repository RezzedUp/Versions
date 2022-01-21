/*
 * Copyright © 2022, RezzedUp <https://github.com/RezzedUp/Versions>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.versions;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;

public final class VersionCore implements Versions.Comparable<VersionCore>
{
    static final VersionCore ZERO = new VersionCore(0, 0, 0);
    
    static VersionCore of(int major, int minor, int patch)
    {
        if (major == 0 && minor == 0 && patch == 0) { return VersionCore.ZERO; }
        
        return new VersionCore(
            Versions.onlyIfPositive(major, "major"),
            Versions.onlyIfPositive(minor, "minor"),
            Versions.onlyIfPositive(patch, "patch")
        );
    }
    
    private final int major;
    private final int minor;
    private final int patch;
    
    VersionCore(int major, int minor, int patch)
    {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
    
    public Builder toBuilder()
    {
        return new Builder(this);
    }
    
    public int major() { return major; }
    
    public int minor() { return minor; }
    
    public int patch() { return patch; }
    
    // >= 1.2.3
    public boolean atLeast(int major, int minor, int patch)
    {
        return compareTo(major, minor, patch) >= 0;
    }
    
    // >= 1.2.*
    public boolean atLeast(int major, int minor)
    {
        return atLeast(major, minor, 0);
    }
    
    // >= 1.*.*
    public boolean atLeast(int major)
    {
        return atLeast(major, 0, 0);
    }
    
    // <= 1.2.3
    public boolean atMost(int major, int minor, int patch)
    {
        return compareTo(major, minor, patch) <= 0;
    }
    
    // <= 1.2.*
    public boolean atMost(int major, int minor)
    {
        return atMost(major, minor, Integer.MAX_VALUE);
    }
    
    // <= 1.*.*
    public boolean atMost(int major)
    {
        return atMost(major, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    // == 1.2.3
    public boolean is(int major, int minor, int patch)
    {
        return compareTo(major, minor, patch) == 0;
    }
    
    // == 1.2.*
    public boolean isAny(int major, int minor)
    {
        return atLeast(major, minor) && atMost(major, minor);
    }
    
    // == 1.*.*
    public boolean isAny(int major)
    {
        return atLeast(major) && atMost(major);
    }
    
    @SuppressWarnings("RedundantIfStatement") // included for clarity
    private int compareTo(int major, int minor, int patch)
    {
        int diffMajor = this.major - major;
        if (diffMajor != 0) { return diffMajor; }
        
        int diffMinor = this.minor - minor;
        if (diffMinor != 0) { return diffMinor; }
        
        int diffPatch = this.patch - patch;
        if (diffPatch != 0) { return diffPatch; }
        
        return 0;
    }
    
    @Override
    public int compareTo(VersionCore o)
    {
        return compareTo(o.major, o.minor, o.patch);
    }
    
    @Override
    public String toString()
    {
        return major + "." + minor + "." + patch;
    }
    
    @Override
    public boolean equals(@NullOr Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        VersionCore core = (VersionCore) o;
        return major == core.major && minor == core.minor && patch == core.patch;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(major, minor, patch);
    }
    
    public final class Builder
    {
        private int major;
        private int minor;
        private int patch;
        
        private Builder(VersionCore core)
        {
            this.major = VersionCore.this.major;
            this.minor = VersionCore.this.minor;
            this.patch = VersionCore.this.patch;
        }
        
        public Builder major(int major)
        {
            this.major = Versions.onlyIfPositive(major, "major");
            return this;
        }
        
        public Builder minor(int minor)
        {
            this.minor = Versions.onlyIfPositive(minor, "minor");
            return this;
        }
        
        public Builder patch(int patch)
        {
            this.patch = Versions.onlyIfPositive(patch, "patch");
            return this;
        }
        
        public VersionCore build()
        {
            return VersionCore.of(major, minor, patch);
        }
    }
}
