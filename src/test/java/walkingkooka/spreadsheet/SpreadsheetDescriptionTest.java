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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CharSequences;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetDescriptionTest implements ClassTesting2<SpreadsheetDescription>,
    HashCodeEqualsDefinedTesting2<SpreadsheetDescription>,
    ToStringTesting<SpreadsheetDescription> {

    private final static String TEXT = "description #1";

    @Test
    public void testWithNullValueFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetDescription.with(null));
    }

    @Test
    public void testWithEmptyValueFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetDescription.with(""));
    }

    @Test
    public void testWithWhitespaceValueFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetDescription.with("   "));
    }

    @Test
    public void testWith() {
        final SpreadsheetDescription description = SpreadsheetDescription.with(TEXT);
        this.checkValue(description, TEXT);
    }

    // equals...............................................................................................

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(SpreadsheetDescription.with("different"));
    }

    @Test
    public void testEqualsDifferentCase() {
        this.checkNotEquals(SpreadsheetDescription.with(TEXT.toUpperCase()));
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createObject(),
            CharSequences.quote(TEXT).toString());
    }

    @Override
    public SpreadsheetDescription createObject() {
        return SpreadsheetDescription.with(TEXT);
    }

    private void checkValue(final SpreadsheetDescription description, final String value) {
        this.checkEquals(value, description.value(), "value");
    }

    @Override
    public Class<SpreadsheetDescription> type() {
        return SpreadsheetDescription.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
