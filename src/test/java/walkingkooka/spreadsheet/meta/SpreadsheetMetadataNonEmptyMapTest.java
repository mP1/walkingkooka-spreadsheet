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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.MapTesting2;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObjectNode;
import walkingkooka.tree.json.marshall.FromJsonNodeContext;
import walkingkooka.tree.json.marshall.FromJsonNodeContexts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataNonEmptyMapTest implements MapTesting2<SpreadsheetMetadataNonEmptyMap, SpreadsheetMetadataPropertyName<?>, Object> {

    @Test
    public void testWithInvalidPropertyFails() {
        assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> {
            SpreadsheetMetadataNonEmptyMap.with(Maps.of(SpreadsheetMetadataPropertyName.CREATOR, null));
        });
    }

    @Test
    public void testWithMapCopied() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> from = Maps.sorted();
        from.put(this.property1(), this.value1());
        from.put(this.property2(), this.value2());

        final SpreadsheetMetadataNonEmptyMap map = SpreadsheetMetadataNonEmptyMap.with(from);

        from.clear();
        this.sizeAndCheck(map, 2);
    }

    @Test
    public void testGet() {
        this.getAndCheck(this.property1(), this.value1());
    }

    @Test
    public void testGetUnknown() {
        this.getAndCheckAbsent(SpreadsheetMetadataPropertyName.MODIFIED_BY);
    }

    @Test
    public void testSize() {
        this.sizeAndCheck(this.createMap(), 2);
    }

    @Test
    public void testPutFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.createMap().put(this.property1(), this.value1());
        });
    }

    @Test
    public void testKeySet() {
        final List<SpreadsheetMetadataPropertyName<?>> keys = Lists.array();

        for (SpreadsheetMetadataPropertyName<?> key : this.createMap().keySet()) {
            keys.add(key);
        }

        assertEquals(Lists.of(this.property2(), this.property1()), keys);
    }

    @Test
    public void testFromEmptyJsonObject() {
        assertSame(SpreadsheetMetadataNonEmptyMap.EMPTY,
                SpreadsheetMetadataNonEmptyMap.fromJsonNode(JsonNode.object(), FromJsonNodeContexts.basic()));
    }

    @Test
    public void testToString() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        this.toStringAndCheck(this.createMap(), map.toString());
    }

    @Override
    public SpreadsheetMetadataNonEmptyMap createMap() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.ordered();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());
        return SpreadsheetMetadataNonEmptyMap.with(map);
    }

    private SpreadsheetMetadataPropertyName<?> property1() {
        return SpreadsheetMetadataPropertyName.CREATOR;
    }

    private EmailAddress value1() {
        return EmailAddress.parse("user@example.com");
    }

    private SpreadsheetMetadataPropertyName<?> property2() {
        return SpreadsheetMetadataPropertyName.CREATE_DATE_TIME;
    }

    private LocalDateTime value2() {
        return LocalDateTime.of(2000, 1, 2, 12, 58, 59);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataNonEmptyMap> type() {
        return SpreadsheetMetadataNonEmptyMap.class;
    }
}
