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
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.naming.NameTesting2;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.ConstantsTesting;
import walkingkooka.reflect.FieldAttributes;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.text.CaseSensitivity;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

final public class SpreadsheetColorNameTest implements ClassTesting2<SpreadsheetColorName>,
    NameTesting2<SpreadsheetColorName, SpreadsheetColorName>,
    ConstantsTesting<SpreadsheetColorName> {

    @Test
    public void testEqualsDifferentCase() {
        this.checkEquals(
            SpreadsheetColorName.with("RED"),
            SpreadsheetColorName.with("red")
        );
    }

    // Comparator ......................................................................................................

    @Test
    public void testSort2() {
        final SpreadsheetColorName red = SpreadsheetColorName.with("red");
        final SpreadsheetColorName blue = SpreadsheetColorName.with("blue");
        final SpreadsheetColorName green = SpreadsheetColorName.with("green");
        final SpreadsheetColorName yellow = SpreadsheetColorName.with("YELLOW");

        this.compareToArraySortAndCheck(
            yellow,
            green,
            red,
            blue,
            blue,
            green,
            red,
            yellow
        );
    }

    @Override
    public SpreadsheetColorName createName(final String name) {
        return SpreadsheetColorName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.INSENSITIVE;
    }

    @Override
    public String nameText() {
        return "straw";
    }

    @Override
    public String differentNameText() {
        return "different";
    }

    @Override
    public String nameTextLess() {
        return "aqua";
    }

    @Override
    public int minLength() {
        return 1;
    }

    @Override
    public int maxLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String possibleValidChars(final int position) {
        return ASCII_LETTERS;
    }

    @Override
    public String possibleInvalidChars(final int position) {
        return CONTROL + BYTE_NON_ASCII + ASCII_DIGITS;
    }

    // spreadsheetMetadataPropertyName..................................................................................

    @Test
    public void testSpreadsheetMetadataPropertyName() {
        this.checkEquals(
            SpreadsheetColorName.BLACK.spreadsheetMetadataPropertyName(),
            SpreadsheetMetadataPropertyName.namedColor(SpreadsheetColorName.BLACK)
        );
    }

    // ConstantTesting..................................................................................................

    @Test
    public void testWithBlack() {
        assertSame(
            SpreadsheetColorName.BLACK,
            SpreadsheetColorName.with(SpreadsheetColorName.BLACK.value())
        );
    }

    @Test
    public void testWithBLACK() {
        assertNotSame(
            SpreadsheetColorName.BLACK,
            SpreadsheetColorName.with("BLACK")
        );
    }

    @Test
    public void testWithEachConstants() {
        for (final SpreadsheetColorName color : SpreadsheetColorName.DEFAULTS) {
            assertSame(
                color,
                SpreadsheetColorName.with(color.value())
            );
        }
    }

    @Test
    public void testWithEachConstantFields() {
        final Set<SpreadsheetColorName> constants = Arrays.stream(SpreadsheetColorName.class.getDeclaredFields())
            .filter(FieldAttributes.STATIC::is)
            .filter(f -> f.getType() == SpreadsheetColorName.class)
            .map(f -> {
                try {
                    return SpreadsheetColorName.class.cast(f.get(null));
                } catch (final Exception cause) {
                    throw new Error(cause);
                }
            }).collect(Collectors.toCollection(SortedSets::tree));

        for (final SpreadsheetColorName color : constants) {
            assertSame(
                color,
                SpreadsheetColorName.with(color.value())
            );
        }
    }

    @Override
    public Set<SpreadsheetColorName> intentionalDuplicateConstants() {
        return Sets.empty();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetColorName> type() {
        return SpreadsheetColorName.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
