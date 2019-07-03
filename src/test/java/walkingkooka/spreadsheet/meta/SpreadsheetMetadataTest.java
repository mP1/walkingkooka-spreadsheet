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
import walkingkooka.collect.map.Maps;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataTest implements ClassTesting2<SpreadsheetMetadata>,
        HashCodeEqualsDefinedTesting<SpreadsheetMetadata>,
        HasJsonNodeTesting<SpreadsheetMetadata>,
        ToStringTesting<SpreadsheetMetadata> {

    @Test
    public void testWithNullFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetMetadata.with(null);
        });
    }

    @Test
    public void testWithInvalidPropertyFails() {
        assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> {
            SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.CREATOR, null));
        });
    }

    @Test
    public void testWithSpreadsheetMetadataMap() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.of(this.property1(), this.value1());
        final SpreadsheetMetadataMap metadataMap = SpreadsheetMetadataMap.with(map);

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.with(metadataMap);
        assertSame(metadataMap, metadata.value(), "value");
    }

    @Test
    public void testWithMapCopied() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        final Map<SpreadsheetMetadataPropertyName<?>, Object> copy = Maps.sorted();
        copy.putAll(map);

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.with(map);

        map.clear();
        assertEquals(copy, metadata.value(), "value");
    }

    @Test
    public void testEmpty() {
        assertSame(SpreadsheetMetadataMap.EMPTY, SpreadsheetMetadataMap.with(Maps.empty()));
    }

    @Test
    public void testValue() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.of(this.property1(), this.value1());

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.with(map);
        assertEquals(SpreadsheetMetadataMap.class, metadata.value().getClass(), () -> "" + metadata);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.of(this.property1(),
                this.value1(),
                this.property2(),
                this.value2());

        this.toStringAndCheck(SpreadsheetMetadata.with(map), map.toString());
    }

    @Test
    public void testFromEmptyJsonObject() {
        assertSame(SpreadsheetMetadata.EMPTY, SpreadsheetMetadata.fromJsonNode(JsonNode.object()));
    }

    @Override
    public SpreadsheetMetadata createObject() {
        return this.metadata();
    }

    private SpreadsheetMetadata metadata() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.ordered();
        map.put(this.property1(), this.value1());
        return SpreadsheetMetadata.with(map);
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

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadata> type() {
        return SpreadsheetMetadata.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // HasJsonNodeTesting................................................................................................

    @Override
    public SpreadsheetMetadata fromJsonNode(final JsonNode from) {
        return SpreadsheetMetadata.fromJsonNode(from);
    }

    @Override
    public SpreadsheetMetadata createHasJsonNode() {
        return this.createObject();
    }
}
