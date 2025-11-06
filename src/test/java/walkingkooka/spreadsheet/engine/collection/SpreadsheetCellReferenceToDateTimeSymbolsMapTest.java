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
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionMaps;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.text.DateFormatSymbols;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellReferenceToDateTimeSymbolsMapTest implements MapTesting2<SpreadsheetCellReferenceToDateTimeSymbolsMap, SpreadsheetCellReference, Optional<DateTimeSymbols>>,
    ClassTesting2<SpreadsheetCellReferenceToDateTimeSymbolsMap>,
    JsonNodeMarshallingTesting<SpreadsheetCellReferenceToDateTimeSymbolsMap>,
    HasUrlFragmentTesting {

    private final static SpreadsheetCellReference KEY1 = SpreadsheetCellReference.A1;

    private final static Optional<DateTimeSymbols> VALUE1 = Optional.of(
        DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(Locale.ENGLISH)
        )
    );

    private final static SpreadsheetCellReference KEY2 = SpreadsheetCellReference.parseCell("A2");

    private final static Optional<DateTimeSymbols> VALUE2 = Optional.empty();

    private final static Map<SpreadsheetCellReference, Optional<DateTimeSymbols>> MAP = Maps.of(
        KEY1,
        VALUE1,
        KEY2,
        VALUE2
    );

    @Test
    public void testWithNullMapFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellReferenceToDateTimeSymbolsMap.with(null)
        );
    }

    @Test
    public void testWithIncludesNullSpreadsheetCellFails() {
        final Map<SpreadsheetCellReference, Optional<DateTimeSymbols>> map = SpreadsheetSelectionMaps.cell();
        map.put(KEY1, VALUE1);
        map.put(KEY2, null);

        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellReferenceToDateTimeSymbolsMap.with(map)
        );
    }

    @Test
    public void testWithSpreadsheetCellReferenceToDateTimeSymbolsMapDoesntWrap() {
        final SpreadsheetCellReferenceToDateTimeSymbolsMap map = this.createMap();

        assertSame(
            map,
            SpreadsheetCellReferenceToDateTimeSymbolsMap.with(map)
        );
    }

    @Test
    public void testWithMap() {
        final SpreadsheetCellReferenceToDateTimeSymbolsMap map = this.createMap();

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
        final Iterator<Map.Entry<SpreadsheetCellReference, Optional<DateTimeSymbols>>> iterator = this.createMap()
            .entrySet()
            .iterator();
        iterator.next();

        assertThrows(
            UnsupportedOperationException.class,
            () -> iterator.remove()
        );
    }

    @Override
    public SpreadsheetCellReferenceToDateTimeSymbolsMap createMap() {
        return SpreadsheetCellReferenceToDateTimeSymbolsMap.with(MAP);
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
                "  \"A1\": {\n" +
                "    \"ampms\": [\n" +
                "      \"AM\",\n" +
                "      \"PM\"\n" +
                "    ],\n" +
                "    \"monthNames\": [\n" +
                "      \"January\",\n" +
                "      \"February\",\n" +
                "      \"March\",\n" +
                "      \"April\",\n" +
                "      \"May\",\n" +
                "      \"June\",\n" +
                "      \"July\",\n" +
                "      \"August\",\n" +
                "      \"September\",\n" +
                "      \"October\",\n" +
                "      \"November\",\n" +
                "      \"December\"\n" +
                "    ],\n" +
                "    \"monthNameAbbreviations\": [\n" +
                "      \"Jan\",\n" +
                "      \"Feb\",\n" +
                "      \"Mar\",\n" +
                "      \"Apr\",\n" +
                "      \"May\",\n" +
                "      \"Jun\",\n" +
                "      \"Jul\",\n" +
                "      \"Aug\",\n" +
                "      \"Sep\",\n" +
                "      \"Oct\",\n" +
                "      \"Nov\",\n" +
                "      \"Dec\"\n" +
                "    ],\n" +
                "    \"weekDayNames\": [\n" +
                "      \"Sunday\",\n" +
                "      \"Monday\",\n" +
                "      \"Tuesday\",\n" +
                "      \"Wednesday\",\n" +
                "      \"Thursday\",\n" +
                "      \"Friday\",\n" +
                "      \"Saturday\"\n" +
                "    ],\n" +
                "    \"weekDayNameAbbreviations\": [\n" +
                "      \"Sun\",\n" +
                "      \"Mon\",\n" +
                "      \"Tue\",\n" +
                "      \"Wed\",\n" +
                "      \"Thu\",\n" +
                "      \"Fri\",\n" +
                "      \"Sat\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"A2\": null\n" +
                "}"
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
                "{\n" +
                "  \"A1\": {\n" +
                "    \"ampms\": [\n" +
                "      \"AM\",\n" +
                "      \"PM\"\n" +
                "    ],\n" +
                "    \"monthNames\": [\n" +
                "      \"January\",\n" +
                "      \"February\",\n" +
                "      \"March\",\n" +
                "      \"April\",\n" +
                "      \"May\",\n" +
                "      \"June\",\n" +
                "      \"July\",\n" +
                "      \"August\",\n" +
                "      \"September\",\n" +
                "      \"October\",\n" +
                "      \"November\",\n" +
                "      \"December\"\n" +
                "    ],\n" +
                "    \"monthNameAbbreviations\": [\n" +
                "      \"Jan\",\n" +
                "      \"Feb\",\n" +
                "      \"Mar\",\n" +
                "      \"Apr\",\n" +
                "      \"May\",\n" +
                "      \"Jun\",\n" +
                "      \"Jul\",\n" +
                "      \"Aug\",\n" +
                "      \"Sep\",\n" +
                "      \"Oct\",\n" +
                "      \"Nov\",\n" +
                "      \"Dec\"\n" +
                "    ],\n" +
                "    \"weekDayNames\": [\n" +
                "      \"Sunday\",\n" +
                "      \"Monday\",\n" +
                "      \"Tuesday\",\n" +
                "      \"Wednesday\",\n" +
                "      \"Thursday\",\n" +
                "      \"Friday\",\n" +
                "      \"Saturday\"\n" +
                "    ],\n" +
                "    \"weekDayNameAbbreviations\": [\n" +
                "      \"Sun\",\n" +
                "      \"Mon\",\n" +
                "      \"Tue\",\n" +
                "      \"Wed\",\n" +
                "      \"Thu\",\n" +
                "      \"Fri\",\n" +
                "      \"Sat\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"A2\": null\n" +
                "}",
            this.createMap()
        );
    }

    @Override
    public SpreadsheetCellReferenceToDateTimeSymbolsMap unmarshall(final JsonNode json,
                                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellReferenceToDateTimeSymbolsMap.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetCellReferenceToDateTimeSymbolsMap createJsonNodeMarshallingValue() {
        return this.createMap();
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testUrlFragment() {
        this.urlFragmentAndCheck(
            this.createMap(),
            "%7B%0A%20%20%22A1%22:%20%7B%0A%20%20%20%20%22ampms%22:%20[%0A%20%20%20%20%20%20%22AM%22,%0A%20%20%20%20%20%20%22PM%22%0A%20%20%20%20],%0A%20%20%20%20%22monthNames%22:%20[%0A%20%20%20%20%20%20%22January%22,%0A%20%20%20%20%20%20%22February%22,%0A%20%20%20%20%20%20%22March%22,%0A%20%20%20%20%20%20%22April%22,%0A%20%20%20%20%20%20%22May%22,%0A%20%20%20%20%20%20%22June%22,%0A%20%20%20%20%20%20%22July%22,%0A%20%20%20%20%20%20%22August%22,%0A%20%20%20%20%20%20%22September%22,%0A%20%20%20%20%20%20%22October%22,%0A%20%20%20%20%20%20%22November%22,%0A%20%20%20%20%20%20%22December%22%0A%20%20%20%20],%0A%20%20%20%20%22monthNameAbbreviations%22:%20[%0A%20%20%20%20%20%20%22Jan%22,%0A%20%20%20%20%20%20%22Feb%22,%0A%20%20%20%20%20%20%22Mar%22,%0A%20%20%20%20%20%20%22Apr%22,%0A%20%20%20%20%20%20%22May%22,%0A%20%20%20%20%20%20%22Jun%22,%0A%20%20%20%20%20%20%22Jul%22,%0A%20%20%20%20%20%20%22Aug%22,%0A%20%20%20%20%20%20%22Sep%22,%0A%20%20%20%20%20%20%22Oct%22,%0A%20%20%20%20%20%20%22Nov%22,%0A%20%20%20%20%20%20%22Dec%22%0A%20%20%20%20],%0A%20%20%20%20%22weekDayNames%22:%20[%0A%20%20%20%20%20%20%22Sunday%22,%0A%20%20%20%20%20%20%22Monday%22,%0A%20%20%20%20%20%20%22Tuesday%22,%0A%20%20%20%20%20%20%22Wednesday%22,%0A%20%20%20%20%20%20%22Thursday%22,%0A%20%20%20%20%20%20%22Friday%22,%0A%20%20%20%20%20%20%22Saturday%22%0A%20%20%20%20],%0A%20%20%20%20%22weekDayNameAbbreviations%22:%20[%0A%20%20%20%20%20%20%22Sun%22,%0A%20%20%20%20%20%20%22Mon%22,%0A%20%20%20%20%20%20%22Tue%22,%0A%20%20%20%20%20%20%22Wed%22,%0A%20%20%20%20%20%20%22Thu%22,%0A%20%20%20%20%20%20%22Fri%22,%0A%20%20%20%20%20%20%22Sat%22%0A%20%20%20%20]%0A%20%20%7D,%0A%20%20%22A2%22:%20null%0A%7D"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetCellReferenceToDateTimeSymbolsMap> type() {
        return SpreadsheetCellReferenceToDateTimeSymbolsMap.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
