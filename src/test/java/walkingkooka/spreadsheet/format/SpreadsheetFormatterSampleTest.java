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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatterSampleTest implements HashCodeEqualsDefinedTesting2<SpreadsheetFormatterSample<String>>,
        ClassTesting<SpreadsheetFormatterSample<String>>,
        ToStringTesting<SpreadsheetFormatterSample<String>>,
        TreePrintableTesting {

    private final static String LABEL = "Label123";

    private final static SpreadsheetFormatterSelector SELECTOR = SpreadsheetFormatterSelector.with(
            SpreadsheetFormatterName.TEXT_FORMAT_PATTERN,
            " @"
    );

    private final static String VALUE = "Value123";

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
    public void testWith() {
        final SpreadsheetFormatterSample<?> sample = SpreadsheetFormatterSample.with(
                LABEL,
                SELECTOR,
                VALUE
        );
        this.checkEquals(LABEL, sample.label(), "label");
        this.checkEquals(SELECTOR, sample.selector(), "selector");
        this.checkEquals(VALUE, sample.value(), "value");
    }

    @Test
    public void testWithNullValue() {
        final String value = null;
        final SpreadsheetFormatterSample<?> sample = SpreadsheetFormatterSample.with(
                LABEL,
                SELECTOR,
                value
        );
        this.checkEquals(LABEL, sample.label(), "label");
        this.checkEquals(SELECTOR, sample.selector(), "selector");
        this.checkEquals(value, sample.value(), "value");
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
                        "different"
                )
        );
    }

    @Override
    public SpreadsheetFormatterSample<String> createObject() {
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
                "Label123 text-format-pattern  @ \"Value123\""
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                this.createObject(),
                "Label123\n" +
                        "  text-format-pattern\n" +
                        "    \" @\"\n" +
                        "  Value123\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterSample<String>> type() {
        return Cast.to(SpreadsheetFormatterSample.class);
    }


    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
