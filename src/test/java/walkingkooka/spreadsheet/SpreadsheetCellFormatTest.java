/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.spreadsheetformat.SpreadsheetTextFormatter;
import walkingkooka.text.spreadsheetformat.SpreadsheetTextFormatters;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.MemberVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellFormatTest extends ClassTestCase<SpreadsheetCellFormat>
        implements HashCodeEqualsDefinedTesting<SpreadsheetCellFormat>,
        HasJsonNodeTesting<SpreadsheetCellFormat>,
        ToStringTesting<SpreadsheetCellFormat> {

    private final static String PATTERN = "abc123";
    private final static Optional<SpreadsheetTextFormatter<?>> FORMATTER = Optional.of(SpreadsheetTextFormatters.fake());

    @Test
    public void testWithNullpatternFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetCellFormat.with(null, FORMATTER);
        });
    }

    @Test
    public void testWithNullFormatterFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetCellFormat.with(PATTERN, null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetCellFormat format = this.createObject();
        this.check(format, PATTERN, FORMATTER);
    }

    @Test
    public void testWithEmptyPattern() {
        this.createAndCheck("", FORMATTER);
    }

    private void createAndCheck(final String pattern,
                                final Optional<SpreadsheetTextFormatter<?>> formatter) {
        final SpreadsheetCellFormat format = SpreadsheetCellFormat.with(pattern, formatter);
        this.check(format, pattern, formatter);
    }

    // setPattern...........................................................

    @Test
    public void testSetPatternNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setPattern(null);
        });
    }

    @Test
    public void testSetPatternSame() {
        final SpreadsheetCellFormat format = this.createObject();
        assertSame(format, format.setPattern(PATTERN));
    }

    @Test
    public void testSetPatternDifferentClearsFormatter() {
        final String differentPattern = "different";
        final SpreadsheetCellFormat format = this.createObject();
        final SpreadsheetCellFormat different = format.setPattern(differentPattern);
        assertNotSame(format, different);
        this.check(different, differentPattern, SpreadsheetCellFormat.NO_FORMATTER);
    }

    // setFormatter...........................................................

    @Test
    public void testSetFormatterNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setFormatter(null);
        });
    }

    @Test
    public void testSetFormatterSame() {
        final SpreadsheetCellFormat format = this.createObject();
        assertSame(format, format.setFormatter(FORMATTER));
    }

    @Test
    public void testSetFormatterDifferent() {
        this.setFormatterDifferentAndCheck(Optional.of(SpreadsheetTextFormatters.general()));
    }

    @Test
    public void testSetFormatterDifferentWithout() {
        this.setFormatterDifferentAndCheck(SpreadsheetCellFormat.NO_FORMATTER);
    }

    private void setFormatterDifferentAndCheck(final Optional<SpreadsheetTextFormatter<?>> differentFormatter) {
        final SpreadsheetCellFormat format = this.createObject();
        final SpreadsheetCellFormat different = format.setFormatter(differentFormatter);
        assertNotSame(format, different);
        this.check(different, PATTERN, differentFormatter);
    }

    private void check(final SpreadsheetCellFormat format,
                       final String pattern,
                       final Optional<SpreadsheetTextFormatter<?>> formatter) {
        assertEquals(pattern, format.pattern(), "pattern");
        assertEquals(formatter, format.formatter(), "formatter");
    }

    // equals..................................................................................

    @Test
    public void testEqualsBothNoFormatter() {
        this.checkEqualsAndHashCode(this.withoutFormatter(), this.withoutFormatter());
    }

    @Test
    public void testEqualsDifferentText() {
        this.checkNotEquals(SpreadsheetCellFormat.with("different", FORMATTER));
    }

    @Test
    public void testEqualsDifferentFormatter() {
        this.checkNotEquals(SpreadsheetCellFormat.with(PATTERN, Optional.of(SpreadsheetTextFormatters.general())));
    }

    @Test
    public void tesEqualstDifferentNoFormatter() {
        this.checkNotEquals(this.withoutFormatter());
    }

    // HasJsonNode......................................................................................

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(this.createObject(), JsonNode.string(PATTERN));
    }

    // toString................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createObject(),
                CharSequences.quote(PATTERN) + " " + FORMATTER.get());
    }

    @Test
    public void testToStringWithoutFormatter() {
        assertEquals(CharSequences.quote(PATTERN).toString(), SpreadsheetCellFormat.with(PATTERN, SpreadsheetCellFormat.NO_FORMATTER).toString());
    }

    @Override
    public SpreadsheetCellFormat createObject() {
        return SpreadsheetCellFormat.with(PATTERN, FORMATTER);
    }
    
    private SpreadsheetCellFormat withoutFormatter() {
        return SpreadsheetCellFormat.with(PATTERN, SpreadsheetCellFormat.NO_FORMATTER);
    }

    @Override
    public Class<SpreadsheetCellFormat> type() {
        return SpreadsheetCellFormat.class;
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
