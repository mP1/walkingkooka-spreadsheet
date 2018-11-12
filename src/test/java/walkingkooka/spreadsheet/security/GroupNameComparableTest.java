/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package walkingkooka.spreadsheet.security;

import org.junit.Test;
import walkingkooka.compare.ComparableTestCase;

public final class GroupNameComparableTest extends ComparableTestCase<GroupName> {

    @Test
    public void testDifferentCasel() {
        this.checkNotEquals(GroupName.with("ABC"));
    }

    @Test
    public void testLess() {
        this.compareToAndCheckLess(GroupName.with("xyz"));
    }

    @Test
    public void testMore() {
        this.compareToAndCheckMore(GroupName.with("XYZ"));
    }

    @Override
    protected GroupName createComparable() {
        return GroupName.with("abc");
    }
}
