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

package walkingkooka.spreadsheet.format.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.TextNode;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatterSampleTest implements HashCodeEqualsDefinedTesting2<SpreadsheetFormatterSample>,
    ClassTesting<SpreadsheetFormatterSample>,
    ToStringTesting<SpreadsheetFormatterSample>,
    TreePrintableTesting,
    JsonNodeMarshallingTesting<SpreadsheetFormatterSample> {

    private final static String LABEL = "Label123";

    private final static SpreadsheetFormatterSelector SELECTOR = SpreadsheetFormatterSelector.with(
        SpreadsheetFormatterName.TEXT,
        " @"
    );

    private final static TextNode VALUE = TextNode.text("Value123");

    // with.............................................................................................................

    @Test
    public void testWithNullLabelFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterSample.with(
                null,
                SELECTOR,
                VALUE
            )
        );
    }

    @Test
    public void testWithEmptyLabelFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetFormatterSample.with(
                "",
                SELECTOR,
                VALUE
            )
        );
    }

    @Test
    public void testWithNullSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterSample.with(
                LABEL,
                null,
                VALUE
            )
        );
    }

    @Test
    public void testWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterSample.with(
                LABEL,
                SELECTOR,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetFormatterSample sample = SpreadsheetFormatterSample.with(
            LABEL,
            SELECTOR,
            VALUE
        );
        this.checkEquals(LABEL, sample.label(), "label");
        this.checkEquals(SELECTOR, sample.selector(), "selector");
        this.checkEquals(VALUE, sample.value(), "value");
    }

    // setSelector......................................................................................................

    @Test
    public void testSetSelectorWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterSample.with(
                LABEL,
                SELECTOR,
                VALUE
            ).setSelector(null)
        );
    }

    @Test
    public void testSetSelectorSame() {
        final SpreadsheetFormatterSample sample = SpreadsheetFormatterSample.with(
            LABEL,
            SELECTOR,
            VALUE
        );
        assertSame(
            sample,
            sample.setSelector(SELECTOR)
        );
    }

    @Test
    public void testSetSelectorDifferent() {
        final SpreadsheetFormatterSample sample = SpreadsheetFormatterSample.with(
            LABEL,
            SELECTOR,
            VALUE
        );

        final SpreadsheetFormatterSelector differentSelector = SpreadsheetFormatterSelector.parse("different");
        final SpreadsheetFormatterSample different = sample.setSelector(differentSelector);

        assertNotSame(
            sample,
            different
        );

        this.checkEquals(LABEL, different.label(), "label");
        this.checkEquals(differentSelector, different.selector(), "selector");
        this.checkEquals(VALUE, different.value(), "value");

        this.checkEquals(LABEL, sample.label(), "label");
        this.checkEquals(SELECTOR, sample.selector(), "selector");
        this.checkEquals(VALUE, sample.value(), "value");
    }

    // setValue.........................................................................................................

    @Test
    public void testSetValueWithNullFails() {
        final SpreadsheetFormatterSample sample = SpreadsheetFormatterSample.with(
            LABEL,
            SELECTOR,
            VALUE
        );
        assertThrows(
            NullPointerException.class,
            () -> sample.setValue(null)
        );
    }

    @Test
    public void testSetValueSame() {
        final SpreadsheetFormatterSample sample = SpreadsheetFormatterSample.with(
            LABEL,
            SELECTOR,
            VALUE
        );
        assertSame(
            sample,
            sample.setValue(VALUE)
        );
    }

    @Test
    public void testSetValueDifferent() {
        final SpreadsheetFormatterSample sample = SpreadsheetFormatterSample.with(
            LABEL,
            SELECTOR,
            VALUE
        );

        final TextNode differentValue = TextNode.text("different");
        final SpreadsheetFormatterSample different = sample.setValue(differentValue);

        assertNotSame(
            sample,
            different
        );

        this.checkEquals(LABEL, different.label(), "label");
        this.checkEquals(SELECTOR, different.selector(), "selector");
        this.checkEquals(differentValue, different.value(), "value");

        this.checkEquals(LABEL, sample.label(), "label");
        this.checkEquals(SELECTOR, sample.selector(), "selector");
        this.checkEquals(VALUE, sample.value(), "value");
    }

    // hashcode/equals..................................................................................................

    @Test
    public void testEqualsDifferentLabel() {
        this.checkNotEquals(
            SpreadsheetFormatterSample.with(
                "different",
                SELECTOR,
                VALUE
            )
        );
    }

    @Test
    public void testEqualsDifferentSelector() {
        this.checkNotEquals(
            SpreadsheetFormatterSample.with(
                LABEL,
                SpreadsheetFormatterSelector.parse("different"),
                VALUE
            )
        );
    }

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(
            SpreadsheetFormatterSample.with(
                LABEL,
                SELECTOR,
                TextNode.text("different")
            )
        );
    }

    @Override
    public SpreadsheetFormatterSample createObject() {
        return SpreadsheetFormatterSample.with(
            LABEL,
            SELECTOR,
            VALUE
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createObject(),
            "Label123 text  @ \"Value123\""
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createObject(),
            "Label123\n" +
                "  text\n" +
                "    \" @\"\n" +
                "  Text \"Value123\"\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            "{\n" +
                "  \"label\": \"Label123\",\n" +
                "  \"selector\": \"text  @\",\n" +
                "  \"value\": {\n" +
                "    \"type\": \"text\",\n" +
                "    \"value\": \"Value123\"\n" +
                "  }\n" +
                "}"
        );
    }

    @Test
    public void testMarshallUnmarshall() {
        this.marshallRoundTripTwiceAndCheck(
            this.createJsonNodeMarshallingValue()
        );
    }

    @Override
    public SpreadsheetFormatterSample unmarshall(final JsonNode json,
                                                 final JsonNodeUnmarshallContext context) {
        return Cast.to(
            SpreadsheetFormatterSample.unmarshall(
                json,
                context
            )
        );
    }

    @Override
    public SpreadsheetFormatterSample createJsonNodeMarshallingValue() {
        return SpreadsheetFormatterSample.with(
            LABEL,
            SELECTOR,
            VALUE
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterSample> type() {
        return SpreadsheetFormatterSample.class;
    }


    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
