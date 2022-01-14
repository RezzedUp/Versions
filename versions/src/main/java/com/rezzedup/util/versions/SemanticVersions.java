/*
 * Copyright © 2022, RezzedUp <https://github.com/RezzedUp/Versions>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.versions;

import java.util.regex.Pattern;

final class SemanticVersions
{
    private SemanticVersions() { throw new UnsupportedOperationException(); }
    
    static final Pattern VALID_PRE_RELEASE_PATTERN =
        Pattern.compile("(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*");
    
    static final Pattern VALID_BUILD_METADATA_PATTERN =
        Pattern.compile("[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*");
    
    static final Pattern VALID_SEMVER_PATTERN =
        Pattern.compile(
            "(?<major>0|[1-9]\\d*)\\.(?<minor>0|[1-9]\\d*)\\.(?<patch>0|[1-9]\\d*)" +
            "(?:-(?<prerelease>" + VALID_PRE_RELEASE_PATTERN + "))?" +
            "(?:\\+(?<buildmetadata>" + VALID_BUILD_METADATA_PATTERN + "))?"
        );
    
    static final Pattern PARTIAL_SEMVER_PATTERN =
        Pattern.compile(
            "(?<major>0|[1-9]\\d*)(?:\\.(?<minor>0|[1-9]\\d*))?(?:\\.(?<patch>0|[1-9]\\d*))?" +
            "(?:-(?<prerelease>" + VALID_PRE_RELEASE_PATTERN + "))?" +
            "(?:\\+(?<buildmetadata>" + VALID_BUILD_METADATA_PATTERN + "))?"
        );
    
    static final Pattern DOT_SEPARATOR = Pattern.compile("\\.");
    
    static final Pattern DIGITS = Pattern.compile("\\d+");
    
    // 11.4:    Precedence for two pre-release versions with the same major, minor, and patch version
    //          MUST be determined by comparing each dot separated identifier from left to right until
    //          a difference is found as follows:
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
}
