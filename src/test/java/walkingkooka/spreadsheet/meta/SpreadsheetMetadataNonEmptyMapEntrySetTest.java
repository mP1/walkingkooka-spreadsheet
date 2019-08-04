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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.iterator.IteratorTesting;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.SetTesting;
import walkingkooka.net.email.EmailAddress;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataNonEmptyMapEntrySetTest implements SetTesting<SpreadsheetMetadataNonEmptyMapEntrySet, Entry<SpreadsheetMetadataPropertyName<?>, Object>>,
        IteratorTesting {

    @Test
    public void testWithInvalidPropertyFails() {
        assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> {
            SpreadsheetMetadataNonEmptyMapEntrySet.with(Maps.of(SpreadsheetMetadataPropertyName.CREATOR, null));
        });
    }

    @Test
    public void testEmpty() {
        assertSame(SpreadsheetMetadataNonEmptyMapEntrySet.EMPTY, SpreadsheetMetadataNonEmptyMapEntrySet.with(Maps.empty()));
    }

    @Test
    public void testSize() {
        this.sizeAndCheck(this.createSet(), 2);
    }

    @Test
    public void testAddFails() {
        this.addFails(this.createSet(), Maps.entry(this.property1(), this.value1()));
    }

    @Test
    public void testIteratorRemoveFails() {
        final Iterator<?> iterator = this.createSet().iterator();
        iterator.next();

        assertThrows(UnsupportedOperationException.class, () -> {
            iterator.remove();
        });
    }

    @Test
    public void testToString() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        this.toStringAndCheck(this.createSet(), map.entrySet().toString());
    }

    @Override
    public SpreadsheetMetadataNonEmptyMapEntrySet createSet() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.ordered();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());
        return SpreadsheetMetadataNonEmptyMapEntrySet.with(map);
    }

    private SpreadsheetMetadataPropertyName<?> property1() {
        return SpreadsheetMetadataPropertyName.CREATE_DATE_TIME;
    }

    private LocalDateTime value1() {
        return LocalDateTime.of(2000, 1, 2, 12, 58, 59);
    }

    private SpreadsheetMetadataPropertyName<?> property2() {
        return SpreadsheetMetadataPropertyName.CREATOR;
    }

    private EmailAddress value2() {
        return EmailAddress.parse("user@example.com");
    }

    @Override
    public Class<SpreadsheetMetadataNonEmptyMapEntrySet> type() {
        return SpreadsheetMetadataNonEmptyMapEntrySet.class;
    }
}
