
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

package walkingkooka.spreadsheet.engine.collection;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.map.MapTesting2;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMapTest implements MapTesting2<SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap, SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>>,
    ClassTesting2<SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap>,
    JsonNodeMarshallingTesting<SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap>,
    HasUrlFragmentTesting {

    private final static SpreadsheetCellReference KEY1 = SpreadsheetCellReference.A1;

    private final static Optional<SpreadsheetFormatterSelector> VALUE1 = Optional.of(
        SpreadsheetFormatterSelector.parse("hello-formatter")
    );

    private final static SpreadsheetCellReference KEY2 = SpreadsheetCellReference.parseCell("A2");

    private final static Optional<SpreadsheetFormatterSelector> VALUE2 = Optional.empty();

    private final static Map<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>> MAP = Maps.of(
        KEY1,
        VALUE1,
        KEY2,
        VALUE2
    );

    @Test
    public void testWithNullMapFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap.with(null)
        );
    }

    @Test
    public void testWithIncludesNullSpreadsheetCellFails() {
        final Map<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>> map = Maps.sorted(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
        map.put(KEY1, VALUE1);
        map.put(KEY2, null);

        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap.with(map)
        );
    }

    @Test
    public void testWithSpreadsheetCellReferenceToSpreadsheetFormatterSelectorMapDoesntWrap() {
        final SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap map = this.createMap();

        assertSame(
            map,
            SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap.with(map)
        );
    }

    @Test
    public void testWithMap() {
        final SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap map = this.createMap();

        this.checkEquals(
            MAP,
            map
        );
    }

    @Test
    public void testGetWithNonSpreadsheetCellReferenceFails() {
        this.getAndCheckAbsent(
            KEY1.toString()
        );
    }

    @Test
    public void testGetWithUnknownSpreadsheetCellReferenceFails() {
        this.getAndCheckAbsent(
            SpreadsheetSelection.parseCell("Z99")
        );
    }

    @Test
    public void testSize() {
        this.sizeAndCheck(
            this.createMap(),
            MAP.size()
        );
    }

    @Test
    public void testGet() {
        this.getAndCheck(
            KEY1,
            VALUE1
        );
    }

    @Test
    public void testGet2() {
        this.getAndCheck(
            KEY2,
            VALUE2
        );
    }

    @Test
    public void testGetDifferentSpreadsheetCellReferenceKind() {
        final SpreadsheetCellReference reference = KEY1.toAbsolute();
        this.checkNotEquals(
            KEY1,
            reference
        );

        this.getAndCheck(
            reference,
            VALUE1
        );
    }

    @Test
    public void testPutFails() {
        this.putFails(
            this.createMap(),
            KEY1,
            VALUE1
        );
    }

    @Test
    public void testRemoveFails() {
        this.removeFails(
            this.createMap(),
            KEY1
        );
    }

    @Test
    public void testIteratorRemoveFails() {
        final Iterator<Map.Entry<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>>> iterator = this.createMap()
            .entrySet()
            .iterator();
        iterator.next();

        assertThrows(
            UnsupportedOperationException.class,
            () -> iterator.remove()
        );
    }

    @Override
    public SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap createMap() {
        return SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap.with(MAP);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createMap(),
            MAP.toString()
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createMap(),
                "{\n" +
                "  \"A1\": \"hello-formatter\",\n" +
                "  \"A2\": null\n" +
                "}"
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
            "{\n" +
                "  \"A1\": \"hello-formatter\",\n" +
                "  \"A2\": null\n" +
                "}",
            this.createMap()
        );
    }

    @Override
    public SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap unmarshall(final JsonNode json,
                                                                        final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap createJsonNodeMarshallingValue() {
        return this.createMap();
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testUrlFragment() {
        this.urlFragmentAndCheck(
            this.createMap(),
            "%7B%0A%20%20%22A1%22:%20%22hello-formatter%22,%0A%20%20%22A2%22:%20null%0A%7D"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap> type() {
        return SpreadsheetCellReferenceToSpreadsheetFormatterSelectorMap.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
