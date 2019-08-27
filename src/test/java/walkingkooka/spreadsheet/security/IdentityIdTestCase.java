/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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
 */

package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;
import walkingkooka.compare.ComparableTesting;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.map.JsonNodeMappingTesting;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class IdentityIdTestCase<I extends IdentityId & Comparable<I>> implements ClassTesting2<I>,
        ComparableTesting<I>,
        JsonNodeMappingTesting<I>,
        ToStringTesting<I> {

    IdentityIdTestCase() {
        super();
    }

    @Test
    public final void testCreate() {
        final Long value = 123L;
        final I id = this.createId(value);
        assertEquals(value, id.value(), "value");
    }

    @Test
    public final void testArraySort() {
        final I id1 = this.createId(1);
        final I id2 = this.createId(2);
        final I id3 = this.createId(3);

        this.compareToArraySortAndCheck(id2, id3, id1,
                id1, id2, id3);
    }

    @Test
    public final void testToJson() {
        this.toJsonNodeAndCheck(this.createId(123L), this.toJsonNodeContext().toJsonNode(123L));
    }

    @Test
    public final void testToJsonRoundtrip() {
        this.toJsonNodeRoundTripTwiceAndCheck(this.createId(123L));
    }

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    abstract I createId(long value);

    @Override
    public I createJsonNodeMappingValue() {
        return this.createId(1L);
    }
}
