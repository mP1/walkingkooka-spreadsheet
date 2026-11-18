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

package walkingkooka.spreadsheet.compare;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.color.Color;
import walkingkooka.color.compare.ColorComparators;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorName;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetComparatorOutputTextStylePropertyNameTest implements SpreadsheetComparatorTesting2<SpreadsheetComparatorOutputTextStylePropertyName<Color>, Color>,
    HashCodeEqualsDefinedTesting2<SpreadsheetComparatorOutputTextStylePropertyName<Color>>,
    ToStringTesting {

    private final static TextStylePropertyName<Color> TEXT_STYLE_PROPERTY_NAME = TextStylePropertyName.COLOR;

    private final static Comparator<Color> COMPARATOR = ColorComparators.red();

    @Test
    public void testWithNullTextStylePropertyNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetComparatorOutputTextStylePropertyName.with(
                null,
                COMPARATOR
            )
        );
    }

    @Test
    public void testWithNullComparatorFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetComparatorOutputTextStylePropertyName.with(
                TEXT_STYLE_PROPERTY_NAME,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetComparatorOutputTextStylePropertyName<Color> comparator = SpreadsheetComparatorOutputTextStylePropertyName.with(
            TEXT_STYLE_PROPERTY_NAME,
            COMPARATOR
        );

        this.nameAndCheck(
            comparator,
            SpreadsheetComparatorName.with("color")
        );
    }

    @Test
    public void testCompareLess() {
        this.compareAndCheckLess(
            Color.parse("#1FF"),
            Color.parse("#2FF")
        );
    }

    @Override
    public SpreadsheetComparatorOutputTextStylePropertyName<Color> createComparator() {
        return SpreadsheetComparatorOutputTextStylePropertyName.with(
            TEXT_STYLE_PROPERTY_NAME,
            COMPARATOR
        );
    }

    @Override
    public SpreadsheetComparatorContext createContext() {
        return new FakeSpreadsheetComparatorContext();
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentTextStylePropertyName() {
        this.checkNotEquals(
            SpreadsheetComparatorOutputTextStylePropertyName.with(
                TextStylePropertyName.BACKGROUND_COLOR,
                COMPARATOR
            )
        );
    }

    @Test
    public void testEqualsDifferentComparator() {
        this.checkNotEquals(
            SpreadsheetComparatorOutputTextStylePropertyName.with(
                TEXT_STYLE_PROPERTY_NAME,
                ColorComparators.green()
            )
        );
    }

    @Override
    public SpreadsheetComparatorOutputTextStylePropertyName<Color> createObject() {
        return this.createComparator();
    }

    // toString.........................................................................................................

    @Test
    public void tesToString() {
        this.toStringAndCheck(
            this.createComparator(),
            TEXT_STYLE_PROPERTY_NAME + " " + COMPARATOR
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetComparatorOutputTextStylePropertyName<Color>> type() {
        return Cast.to(
            SpreadsheetComparatorOutputTextStylePropertyName.class
        );
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
