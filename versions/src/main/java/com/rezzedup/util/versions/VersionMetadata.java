/*
 * Copyright © 2022, RezzedUp <https://github.com/RezzedUp/Versions>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.versions;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class VersionMetadata implements Versions.Comparable<VersionMetadata>
{
    private static final Pattern DIGITS = Pattern.compile("\\d+");
    
    static final VersionMetadata EMPTY = new VersionMetadata(List.of());
    
    static VersionMetadata of(Collection<String> identifiers)
    {
        if (identifiers.isEmpty()) { return VersionMetadata.EMPTY; }
        
        // TODO: validate identifiers
        return new VersionMetadata(List.copyOf(identifiers));
    }
    
    private final List<String> identifiers;
    
    VersionMetadata(List<String> identifiers)
    {
        this.identifiers = List.copyOf(identifiers);
    }
    
    public Builder toBuilder()
    {
        return new Builder();
    }
    
    public List<String> identifiers()
    {
        return identifiers;
    }
    
    public boolean isEmpty()
    {
        return identifiers.isEmpty();
    }
    
    public boolean isPresent()
    {
        return !identifiers.isEmpty();
    }
    
    // 11.4:    Precedence for two pre-release versions with the same major, minor, and patch version
    //          MUST be determined by comparing each dot separated identifier from left to right until
    //          a difference is found as follows:
    @Override
    public int compareTo(VersionMetadata o)
    {
        if (identifiers.equals(o.identifiers)) { return 0; }
        
        for (int i = 0; i < identifiers.size(); i++)
        {
            String left = identifiers.get(i);
            
            // 11.4.4:  A larger set of pre-release fields has a higher precedence than a smaller set,
            //          if all of the preceding identifiers are equal.
            // (Right has fewer identifiers, so left takes precedence)
            if (o.identifiers.size() <= i) { return 1; }
            
            String right = o.identifiers.get(i);
            
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
        if (o.identifiers.size() > identifiers.size()) { return -1; }
        
        // Equal
        return 0;
    }
    
    @Override
    public String toString()
    {
        return String.join(".", identifiers);
    }
    
    @Override
    public boolean equals(@NullOr Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        VersionMetadata meta = (VersionMetadata) o;
        return identifiers.equals(meta.identifiers);
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(identifiers);
    }
    
    public class Builder
    {
        private final List<String> identifiers;
        
        private Builder()
        {
            this.identifiers = new ArrayList<>(VersionMetadata.this.identifiers);
        }
        
        public Builder then(String identifier)
        {
            identifiers.add(identifier);
            return this;
        }
        
        public Builder then(int number)
        {
            identifiers.add(String.valueOf(number));
            return this;
        }
        
        public VersionMetadata build()
        {
            return VersionMetadata.of(identifiers);
        }
    }
}
