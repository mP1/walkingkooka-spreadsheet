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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCoordinates;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.BorderStyle;
import walkingkooka.tree.text.FontFamily;
import walkingkooka.tree.text.FontSize;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextAlign;
import walkingkooka.tree.text.TextJustify;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.tree.text.WordWrap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataTest implements ClassTesting2<SpreadsheetMetadata>,
        HashCodeEqualsDefinedTesting2<SpreadsheetMetadata>,
        JsonNodeMarshallingTesting<SpreadsheetMetadata>,
        ToStringTesting<SpreadsheetMetadata> {

    @Test
    public void testWithNullFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetMetadata.with(null));
    }

    @Test
    public void testWithInvalidPropertyFails() {
        assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.CREATOR, null)));
    }

    @Test
    public void testWithSpreadsheetMetadataMapNullValueFails() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(123), SpreadsheetMetadataPropertyName.SPREADSHEET_NAME, null);
        final SpreadsheetMetadataPropertyValueException thrown = assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> SpreadsheetMetadata.with(map));
        assertEquals("Missing value, but got null for \"spreadsheet-name\"", thrown.getMessage(), "message");
    }

    @Test
    public void testWithSpreadsheetMetadataMap() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.of(this.property1(), this.value1());
        final SpreadsheetMetadataNonEmptyMap metadataMap = SpreadsheetMetadataNonEmptyMap.with(map);

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
    public void testMaxNumberColorConstant() {
        assertEquals(SpreadsheetMetadataPropertyNameNumberedColor.MAX_NUMBER, SpreadsheetMetadata.MAX_NUMBER_COLOR);
    }

    @Test
    public void testEmpty() {
        assertSame(SpreadsheetMetadataNonEmptyMap.EMPTY, SpreadsheetMetadataNonEmptyMap.with(Maps.empty()));
    }

    @Test
    public void testValue() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.of(this.property1(), this.value1());

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.with(map);
        assertEquals(SpreadsheetMetadataNonEmptyMap.class, metadata.value().getClass(), () -> "" + metadata);
    }

    @Test
    public void testSetEditCellAbsolute() {
        this.setAndCheck(SpreadsheetMetadataPropertyName.EDIT_CELL);
    }

    @Test
    public void testSetViewportCellAbsolute() {
        this.setAndCheck(SpreadsheetMetadataPropertyName.VIEWPORT_CELL);
    }

    private void setAndCheck(final SpreadsheetMetadataPropertyName<SpreadsheetCellReference> property) {
        this.setAndCheck(property,
                SpreadsheetCellReference.parseCellReference("$B$99"));
    }

    @Test
    public void testSetEditRangeAbsolute() {
        this.setAndCheck(SpreadsheetMetadataPropertyName.EDIT_RANGE,
                SpreadsheetRange.parseRange("$B$2:$C$3"));
    }

    private <T extends SpreadsheetExpressionReference<T>> void setAndCheck(final SpreadsheetMetadataPropertyName<T> property,
                                                                           final T value) {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                .set(property, value);
        assertEquals(value.toRelative(), metadata.getOrFail(property));
    }

    // NON_LOCALE_DEFAULTS..............................................................................................

    @Test
    public void testNonLocaleDefaults() {
        final SpreadsheetMetadata nonLocaleDefaults = SpreadsheetMetadata.NON_LOCALE_DEFAULTS;

        assertNotEquals(SpreadsheetMetadata.EMPTY, nonLocaleDefaults);
        assertNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.DATETIME_OFFSET));

        final Length<?> borderWidth = Length.pixel(1.0);
        final Color borderColor = Color.BLACK;
        final BorderStyle borderStyle = BorderStyle.SOLID;
        final Length<?> none = Length.none();

        final TextStyle style = TextStyle.EMPTY
                .set(TextStylePropertyName.BORDER_LEFT_WIDTH, borderWidth)
                .set(TextStylePropertyName.BORDER_LEFT_COLOR, borderColor)
                .set(TextStylePropertyName.BORDER_LEFT_STYLE, borderStyle)

                .set(TextStylePropertyName.BORDER_TOP_WIDTH, borderWidth)
                .set(TextStylePropertyName.BORDER_TOP_COLOR, borderColor)
                .set(TextStylePropertyName.BORDER_TOP_STYLE, borderStyle)

                .set(TextStylePropertyName.BORDER_RIGHT_WIDTH, borderWidth)
                .set(TextStylePropertyName.BORDER_RIGHT_COLOR, borderColor)
                .set(TextStylePropertyName.BORDER_RIGHT_STYLE, borderStyle)

                .set(TextStylePropertyName.BORDER_BOTTOM_WIDTH, borderWidth)
                .set(TextStylePropertyName.BORDER_BOTTOM_COLOR, borderColor)
                .set(TextStylePropertyName.BORDER_BOTTOM_STYLE, borderStyle)

                .set(TextStylePropertyName.PADDING_LEFT, none)
                .set(TextStylePropertyName.PADDING_TOP, none)
                .set(TextStylePropertyName.PADDING_BOTTOM, none)
                .set(TextStylePropertyName.PADDING_RIGHT, none)

                .set(TextStylePropertyName.BACKGROUND_COLOR, Color.WHITE)
                .set(TextStylePropertyName.COLOR, Color.BLACK)

                .set(TextStylePropertyName.FONT_FAMILY, FontFamily.with("MS Sans Serif"))
                .set(TextStylePropertyName.FONT_SIZE, FontSize.with(11))
                .set(TextStylePropertyName.TEXT_ALIGN, TextAlign.LEFT)
                .set(TextStylePropertyName.TEXT_JUSTIFY, TextJustify.NONE)
                .set(TextStylePropertyName.WORD_WRAP, WordWrap.NORMAL)

                .set(TextStylePropertyName.HEIGHT, Length.pixel(30.0))
                .set(TextStylePropertyName.WIDTH, Length.pixel(100.0));
        assertEquals(Optional.of(style), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.STYLE));

        assertNotEquals(Optional.of(ExpressionNumberKind.DEFAULT), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND));
        assertNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.PRECISION));
        assertNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.ROUNDING_MODE));
        assertNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR));
        assertNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.WIDTH));

        assertEquals(Optional.of(SpreadsheetCellReference.parseCellReference("A1")), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.VIEWPORT_CELL));
        assertEquals(Optional.of(SpreadsheetCoordinates.with(0, 0)), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.VIEWPORT_COORDINATES));
    }

    // loadFromLocale...................................................................................................

    @Test
    public void testLoadFromLocaleNullFails() {
        assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> SpreadsheetMetadata.EMPTY.loadFromLocale());
    }

    @Test
    public void testLoadFromLocale() {
        assertEquals(SpreadsheetMetadata.EMPTY
                        .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "¤")
                        .set(SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN, SpreadsheetPattern.parseDateFormatPattern("dddd, mmmm d, yyyy"))
                        .set(SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERNS, SpreadsheetPattern.parseDateParsePatterns("dddd, mmmm d, yyyy;mmmm d, yyyy;mmm d, yyyy;m/d/yy"))
                        .set(SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN, SpreadsheetPattern.parseDateTimeFormatPattern("dddd, mmmm d, yyyy \\a\\t H:mm:ss AM/PM"))
                        .set(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERNS, SpreadsheetPattern.parseDateTimeParsePatterns("dddd, mmmm d, yyyy \\a\\t H:mm:ss AM/PM;dddd, mmmm d, yyyy \\a\\t H:mm:ss AM/PM;dddd, mmmm d, yyyy, H:mm:ss AM/PM;dddd, mmmm d, yyyy, H:mm AM/PM;mmmm d, yyyy \\a\\t H:mm:ss AM/PM;mmmm d, yyyy \\a\\t H:mm:ss AM/PM;mmmm d, yyyy, H:mm:ss AM/PM;mmmm d, yyyy, H:mm AM/PM;mmm d, yyyy, H:mm:ss AM/PM;mmm d, yyyy, H:mm:ss AM/PM;mmm d, yyyy, H:mm:ss AM/PM;mmm d, yyyy, H:mm AM/PM;m/d/yy, H:mm:ss AM/PM;m/d/yy, H:mm:ss AM/PM;m/d/yy, H:mm:ss AM/PM;m/d/yy, H:mm AM/PM"))
                        .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '.')
                        .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, "E")
                        .set(SpreadsheetMetadataPropertyName.GROUPING_SEPARATOR, ',')
                        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
                        .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, '-')
                        .set(SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN, SpreadsheetPattern.parseNumberFormatPattern("#,##0.###"))
                        .set(SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERNS, SpreadsheetPattern.parseNumberParsePatterns("#,##0.###;#,##0"))
                        .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, '%')
                        .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, '+')
                        .set(SpreadsheetMetadataPropertyName.TIME_FORMAT_PATTERN, SpreadsheetPattern.parseTimeFormatPattern("H:mm:ss AM/PM"))
                        .set(SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERNS, SpreadsheetPattern.parseTimeParsePatterns("H:mm:ss AM/PM;H:mm:ss AM/PM;H:mm:ss AM/PM;H:mm AM/PM"))
                        .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, ','),
                SpreadsheetMetadata.EMPTY.set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH).loadFromLocale());
    }

    // converter........................................................................................................

    @Test
    public void testNonLocaleDefaultsConverter() {
        SpreadsheetMetadata.NON_LOCALE_DEFAULTS
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-AU"))
                .loadFromLocale()
                .set(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetPattern.parseTextFormatPattern("@"))
                .converter();
    }

    // HasJsonNodeMarshallContext.......................................................................................

    @Test
    public void testJsonNodeMarshallContext() {
        final SpreadsheetMetadata metadata = this.createObject();
        final JsonNodeMarshallContext marshallContext = metadata.jsonNodeMarshallContext();
        final JsonNodeMarshallContext marshallContext2 = JsonNodeMarshallContexts.basic();

        final BigDecimal bigDecimal = BigDecimal.valueOf(1.25);
        assertEquals(marshallContext.marshall(bigDecimal), marshallContext2.marshall(bigDecimal), () -> "" + bigDecimal);

        final LocalDateTime localDateTime = LocalDateTime.now();

        assertEquals(marshallContext.marshall(localDateTime), marshallContext2.marshall(localDateTime), () -> "" + localDateTime);

        assertEquals(marshallContext.marshall(metadata), marshallContext2.marshall(metadata), () -> "" + metadata);
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
        assertSame(SpreadsheetMetadata.EMPTY,
                SpreadsheetMetadata.unmarshall(JsonNode.object(), this.unmarshallContext()));
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

    @SuppressWarnings("SameReturnValue")
    private SpreadsheetMetadataPropertyName<?> property1() {
        return SpreadsheetMetadataPropertyName.CREATE_DATE_TIME;
    }

    private LocalDateTime value1() {
        return LocalDateTime.of(2000, 1, 2, 12, 58, 59);
    }

    @SuppressWarnings("SameReturnValue")
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

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetMetadata unmarshall(final JsonNode from,
                                          final JsonNodeUnmarshallContext context) {
        return SpreadsheetMetadata.unmarshall(from, context);
    }

    @Override
    public SpreadsheetMetadata createJsonNodeMappingValue() {
        return this.createObject();
    }
}
