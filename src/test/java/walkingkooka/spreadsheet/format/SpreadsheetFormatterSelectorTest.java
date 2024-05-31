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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatterSelectorTest implements ClassTesting2<SpreadsheetFormatterSelector>,
        HashCodeEqualsDefinedTesting2<SpreadsheetFormatterSelector>,
        ToStringTesting<SpreadsheetFormatterSelector> {

    private final static SpreadsheetFormatterName NAME = SpreadsheetFormatterName.with("text-format");

    private final static String TEXT = "@@";

    @Test
    public void testWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormatterSelector.with(
                        null,
                        TEXT
                )
        );
    }

    @Test
    public void testWithNullTextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormatterSelector.with(
                        NAME,
                        null
                )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterSelector.with(
                NAME,
                TEXT
        );

        this.checkEquals(NAME, selector.name(), "name");
        this.checkEquals(TEXT, selector.text(), "text");
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(
                SpreadsheetFormatterSelector.with(
                        SpreadsheetFormatterName.with("different"),
                        TEXT
                )
        );
    }

    @Test
    public void testEqualsDifferentText() {
        this.checkNotEquals(
                SpreadsheetFormatterSelector.with(
                        NAME,
                        "different"
                )
        );
    }

    @Override
    public SpreadsheetFormatterSelector createObject() {
        return SpreadsheetFormatterSelector.with(
                NAME,
                TEXT
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetFormatterSelector.with(
                        NAME,
                        TEXT
                ),
                "text-format \"@@\""
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetFormatterSelector> type() {
        return SpreadsheetFormatterSelector.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
