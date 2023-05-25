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
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.net.UrlFragment;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.patch.PatchableTesting;
import walkingkooka.tree.text.BorderStyle;
import walkingkooka.tree.text.FontFamily;
import walkingkooka.tree.text.FontSize;
import walkingkooka.tree.text.FontStyle;
import walkingkooka.tree.text.FontVariant;
import walkingkooka.tree.text.Hyphens;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextAlign;
import walkingkooka.tree.text.TextJustify;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.tree.text.VerticalAlign;
import walkingkooka.tree.text.WordBreak;
import walkingkooka.tree.text.WordWrap;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataTest implements ClassTesting2<SpreadsheetMetadata>,
        HashCodeEqualsDefinedTesting2<SpreadsheetMetadata>,
        JsonNodeMarshallingTesting<SpreadsheetMetadata>,
        PatchableTesting<SpreadsheetMetadata>,
        ToStringTesting<SpreadsheetMetadata> {

    @Test
    public void testMaxNumberColorConstant() {
        this.checkEquals(SpreadsheetMetadataPropertyNameNumberedColor.MAX_NUMBER, SpreadsheetMetadata.MAX_NUMBER_COLOR);
    }

    @Test
    public void testSwappablePropertiesConstants() {
        assertArrayEquals(
                SpreadsheetMetadataPropertyName.CONSTANTS.values()
                        .stream()
                        .filter(c -> c instanceof SpreadsheetMetadataPropertyNameCharacter)
                        .toArray(),
                SpreadsheetMetadata.SWAPPABLE_PROPERTIES
        );
    }

    // NON_LOCALE_DEFAULTS..............................................................................................

    @Test
    public void testNonLocaleDefaults() {
        final SpreadsheetMetadata nonLocaleDefaults = SpreadsheetMetadata.NON_LOCALE_DEFAULTS;

        this.checkNotEquals(SpreadsheetMetadata.EMPTY, nonLocaleDefaults);
        this.checkNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.DATETIME_OFFSET));

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

                .set(TextStylePropertyName.MARGIN_LEFT, none)
                .set(TextStylePropertyName.MARGIN_TOP, none)
                .set(TextStylePropertyName.MARGIN_BOTTOM, none)
                .set(TextStylePropertyName.MARGIN_RIGHT, none)

                .set(TextStylePropertyName.PADDING_LEFT, none)
                .set(TextStylePropertyName.PADDING_TOP, none)
                .set(TextStylePropertyName.PADDING_BOTTOM, none)
                .set(TextStylePropertyName.PADDING_RIGHT, none)

                .set(TextStylePropertyName.BACKGROUND_COLOR, Color.WHITE)
                .set(TextStylePropertyName.COLOR, Color.BLACK)

                .set(TextStylePropertyName.FONT_FAMILY, FontFamily.with("MS Sans Serif"))
                .set(TextStylePropertyName.FONT_SIZE, FontSize.with(11))
                .set(TextStylePropertyName.FONT_STYLE, FontStyle.NORMAL)
                .set(TextStylePropertyName.FONT_VARIANT, FontVariant.NORMAL)
                .set(TextStylePropertyName.HYPHENS, Hyphens.NONE)
                .set(TextStylePropertyName.TEXT_ALIGN, TextAlign.LEFT)
                .set(TextStylePropertyName.TEXT_JUSTIFY, TextJustify.NONE)
                .set(TextStylePropertyName.VERTICAL_ALIGN, VerticalAlign.TOP)
                .set(TextStylePropertyName.WORD_BREAK, WordBreak.NORMAL)
                .set(TextStylePropertyName.WORD_WRAP, WordWrap.NORMAL)

                .set(TextStylePropertyName.HEIGHT, Length.pixel(30.0))
                .set(TextStylePropertyName.WIDTH, Length.pixel(100.0));
        this.checkEquals(Optional.of(style), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.STYLE));

        this.checkNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH));

        this.checkNotEquals(
                Sets.empty(),
                SpreadsheetColorName.DEFAULTS.stream()
                        .filter(n -> false == nonLocaleDefaults.defaults()
                                .get(n.spreadsheetMetadataPropertyName()).isPresent())
                        .collect(Collectors.toSet()),
                () -> "missing color defaults"
        );

        this.checkNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.DEFAULT_YEAR));
        this.checkNotEquals(Optional.of(ExpressionNumberKind.DEFAULT), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND));
        this.checkNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.PRECISION));
        this.checkNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.ROUNDING_MODE));
        this.checkNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR));

        this.checkEquals(
                Optional.of(
                        SpreadsheetSelection.A1
                ),
                nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.VIEWPORT_CELL)
        );
    }

    // loadFromLocale...................................................................................................

    @Test
    public void testLoadFromLocaleNullFails() {
        assertThrows(
                SpreadsheetMetadataPropertyValueException.class,
                SpreadsheetMetadata.EMPTY::loadFromLocale
        );
    }

    @Test
    public void testLoadFromLocale() {
        this.checkEquals(SpreadsheetMetadata.EMPTY
                        .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "¤")
                        .set(SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN, SpreadsheetPattern.parseDateFormatPattern("dddd, mmmm d, yyyy"))
                        .set(SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERN, SpreadsheetPattern.parseDateParsePattern("dddd, mmmm d, yyyy;dddd, mmmm d, yy;dddd, mmmm d;mmmm d, yyyy;mmmm d, yy;mmmm d;mmm d, yyyy;mmm d, yy;mmm d;m/d/yy;m/d/yyyy;m/d"))
                        .set(SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN, SpreadsheetPattern.parseDateTimeFormatPattern("dddd, mmmm d, yyyy \\a\\t h:mm:ss AM/PM"))
                        .set(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERN, SpreadsheetPattern.parseDateTimeParsePattern("dddd, mmmm d, yyyy \\a\\t h:mm:ss AM/PM;dddd, mmmm d, yy \\a\\t h:mm:ss AM/PM;dddd, mmmm d, yy \\a\\t h:mm:ss;dddd, mmmm d, yy \\a\\t h:mm AM/PM;dddd, mmmm d, yyyy \\a\\t h:mm:ss.0 AM/PM;dddd, mmmm d, yyyy \\a\\t h:mm:ss.0;dddd, mmmm d, yyyy \\a\\t h:mm:ss;dddd, mmmm d, yyyy \\a\\t h:mm AM/PM;dddd, mmmm d, yyyy \\a\\t h:mm;dddd, mmmm d, yyyy, h:mm:ss AM/PM;dddd, mmmm d, yy, h:mm:ss AM/PM;dddd, mmmm d, yy, h:mm:ss;dddd, mmmm d, yy, h:mm AM/PM;dddd, mmmm d, yyyy, h:mm:ss.0 AM/PM;dddd, mmmm d, yyyy, h:mm:ss.0;dddd, mmmm d, yyyy, h:mm:ss;dddd, mmmm d, yyyy, h:mm AM/PM;dddd, mmmm d, yyyy, h:mm;dddd, mmmm d, yy, h:mm;mmmm d, yyyy \\a\\t h:mm:ss AM/PM;mmmm d, yy \\a\\t h:mm:ss AM/PM;mmmm d, yy \\a\\t h:mm:ss;mmmm d, yy \\a\\t h:mm AM/PM;mmmm d, yyyy \\a\\t h:mm:ss.0 AM/PM;mmmm d, yyyy \\a\\t h:mm:ss.0;mmmm d, yyyy \\a\\t h:mm:ss;mmmm d, yyyy \\a\\t h:mm AM/PM;mmmm d, yyyy \\a\\t h:mm;mmmm d, yyyy, h:mm:ss AM/PM;mmmm d, yy, h:mm:ss AM/PM;mmmm d, yy, h:mm:ss;mmmm d, yy, h:mm AM/PM;mmmm d, yyyy, h:mm:ss.0 AM/PM;mmmm d, yyyy, h:mm:ss.0;mmmm d, yyyy, h:mm:ss;mmmm d, yyyy, h:mm AM/PM;mmmm d, yyyy, h:mm;mmmm d, yy, h:mm;mmm d, yyyy, h:mm:ss AM/PM;mmm d, yy, h:mm:ss AM/PM;mmm d, yy, h:mm:ss;mmm d, yy, h:mm AM/PM;mmm d, yyyy, h:mm:ss.0 AM/PM;mmm d, yyyy, h:mm:ss.0;mmm d, yyyy, h:mm:ss;mmm d, yyyy, h:mm AM/PM;mmm d, yyyy, h:mm;mmm d, yy, h:mm;m/d/yy, h:mm:ss AM/PM;m/d/yy, h:mm:ss;m/d/yy, h:mm AM/PM;m/d/yyyy, h:mm:ss AM/PM;m/d/yyyy, h:mm:ss.0 AM/PM;m/d/yyyy, h:mm:ss.0;m/d/yyyy, h:mm:ss;m/d/yyyy, h:mm AM/PM;m/d/yy, h:mm:ss.0;m/d/yy, h:mm;m/d/yyyy, h:mm"))
                        .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '.')
                        .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, "E")
                        .set(SpreadsheetMetadataPropertyName.GROUPING_SEPARATOR, ',')
                        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
                        .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, '-')
                        .set(SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN, SpreadsheetPattern.parseNumberFormatPattern("#,##0.###"))
                        .set(SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERN, SpreadsheetPattern.parseNumberParsePattern("#,##0.###;#,##0"))
                        .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, '%')
                        .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, '+')
                        .set(SpreadsheetMetadataPropertyName.TIME_FORMAT_PATTERN, SpreadsheetPattern.parseTimeFormatPattern("h:mm:ss AM/PM"))
                        .set(SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERN, SpreadsheetPattern.parseTimeParsePattern("h:mm:ss AM/PM;h:mm:ss;h:mm:ss.0;h:mm AM/PM;h:mm"))
                        .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, ','),
                SpreadsheetMetadata.EMPTY.set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH).loadFromLocale());
    }

    // general........................................................................................................

    @Test
    public void testNonLocaleDefaultsConverter() {
        SpreadsheetMetadata.NON_LOCALE_DEFAULTS
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-AU"))
                .loadFromLocale()
                .set(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetPattern.parseTextFormatPattern("@"))
                .converter();
    }

    // setOrRemove......................................................................................................

    @Test
    public void testSetOrRemoveNullValue() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "AUD");

        this.checkEquals(
                metadata,
                metadata.set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '.')
                        .setOrRemove(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, null)
        );
    }

    @Test
    public void testSetOrRemoveNonNullValue() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "AUD");

        this.checkEquals(
                metadata.set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '.'),
                metadata.setOrRemove(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '.')
        );
    }

    // shouldViewsRefresh...............................................................................................

    @Test
    public void testShouldViewsRefreshSameIdMissing() {
        this.shouldViewRefreshAndCheck(
                SpreadsheetMetadata.EMPTY,
                this.metadata(),
                false
        );
    }

    @Test
    public void testShouldViewsRefreshSameIdPresent() {
        final SpreadsheetMetadata metadata = this.metadata().set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                SpreadsheetId.with(1)
        );

        this.checkNotEquals(
                Optional.empty(),
                metadata.id()
        );

        this.shouldViewRefreshAndCheck(
                metadata,
                metadata,
                false
        );
    }

    @Test
    public void testShouldViewsRefreshSameDifferentCreator() {
        final SpreadsheetMetadata metadata = this.metadata();
        final SpreadsheetMetadata different = metadata.set(
                SpreadsheetMetadataPropertyName.CREATOR,
                EmailAddress.parse("different@example.com")
        );

        this.checkNotEquals(
                metadata,
                different
        );

        this.shouldViewRefreshAndCheck(
                different,
                metadata,
                false
        );
    }

    @Test
    public void testShouldViewsRefreshSameDifferentCreateDateTime() {
        final SpreadsheetMetadata metadata = this.metadata();
        final SpreadsheetMetadata different = metadata.set(
                SpreadsheetMetadataPropertyName.CREATE_DATE_TIME,
                LocalDateTime.now().plusDays(1)
        );

        this.checkNotEquals(
                metadata,
                different
        );

        this.shouldViewRefreshAndCheck(
                different,
                metadata,
                false
        );
    }

    @Test
    public void testShouldViewsRefreshSameDifferentModified() {
        final SpreadsheetMetadata metadata = this.metadata();
        final SpreadsheetMetadata different = metadata.set(
                SpreadsheetMetadataPropertyName.MODIFIED_BY,
                EmailAddress.parse("different@example.com")
        );

        this.checkNotEquals(
                metadata,
                different
        );

        this.shouldViewRefreshAndCheck(
                different,
                metadata,
                false
        );
    }

    @Test
    public void testShouldViewsRefreshSameDifferentModifiedDateTime() {
        final SpreadsheetMetadata metadata = this.metadata();
        final SpreadsheetMetadata different = metadata.set(
                SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME,
                LocalDateTime.now().plusDays(1)
        );

        this.checkNotEquals(
                metadata,
                different
        );

        this.shouldViewRefreshAndCheck(
                different,
                metadata,
                false
        );
    }

    @Test
    public void testShouldViewsRefreshSameDifferentName() {
        final SpreadsheetMetadata metadata = this.metadata();
        final SpreadsheetMetadata different = metadata.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                SpreadsheetName.with("Different")
        );

        this.checkNotEquals(
                metadata,
                different
        );

        this.shouldViewRefreshAndCheck(
                different,
                metadata,
                false
        );
    }

    @Test
    public void testShouldViewsRefreshSameDifferentSelection() {
        final SpreadsheetMetadata metadata = this.metadata();
        final SpreadsheetMetadata different = metadata.set(
                SpreadsheetMetadataPropertyName.SELECTION,
                SpreadsheetSelection.parseColumn("A")
                        .setDefaultAnchor()
        );

        this.checkNotEquals(
                metadata,
                different
        );

        this.shouldViewRefreshAndCheck(
                different,
                metadata,
                false
        );
    }

    @Test
    public void testShouldViewsRefreshSameIdPresentDifferentCurrencySymbol() {
        final SpreadsheetMetadata metadata = this.metadata()
                .set(
                        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                        SpreadsheetId.with(1)
                );

        this.shouldViewRefreshAndCheck(
                metadata.set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "Diff"),
                metadata,
                true
        );
    }

    @Test
    public void testShouldViewsRefreshSameIdPresentDifferentId() {
        final SpreadsheetMetadata metadata = this.metadata();

        this.shouldViewRefreshAndCheck(
                metadata.set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(9999)),
                metadata,
                true
        );
    }

    @Test
    public void testShouldViewsRefreshSameIdPresentDifferentLocale() {
        final SpreadsheetMetadata metadata = this.metadata()
                .set(
                        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                        SpreadsheetId.with(1)
                );

        this.shouldViewRefreshAndCheck(
                metadata.set(SpreadsheetMetadataPropertyName.LOCALE, Locale.FRANCE),
                metadata,
                true
        );
    }

    @Test
    public void testShouldViewsRefreshSameIdPresentDifferentStyle() {
        final SpreadsheetMetadata metadata = this.metadata()
                .set(
                        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                        SpreadsheetId.with(1)
                ).set(
                        SpreadsheetMetadataPropertyName.STYLE,
                        TextStyle.EMPTY.set(
                                TextStylePropertyName.COLOR,
                                Color.parse("#000000")
                        )
                );

        this.shouldViewRefreshAndCheck(
                metadata.set(
                        SpreadsheetMetadataPropertyName.STYLE,
                        TextStyle.EMPTY.set(
                                TextStylePropertyName.COLOR,
                                Color.parse("#123456")
                        )
                ),
                metadata,
                true
        );
    }

    private void shouldViewRefreshAndCheck(final SpreadsheetMetadata metadata,
                                           final SpreadsheetMetadata previous,
                                           final boolean expected) {
        if (expected) {
            this.checkNotEquals(
                    metadata,
                    previous
            );
        }

        this.checkEquals(
                expected,
                metadata.shouldViewRefresh(previous),
                () -> metadata + " shouldViewsRefresh " + previous
        );
    }

    // HasJsonNodeMarshallContext.......................................................................................

    @Test
    public void testMarshallContext() {
        final SpreadsheetMetadata metadata = this.createObject();
        final JsonNodeMarshallContext marshallContext = metadata.jsonNodeMarshallContext();
        final JsonNodeMarshallContext marshallContext2 = JsonNodeMarshallContexts.basic();

        final BigDecimal bigDecimal = BigDecimal.valueOf(1.25);
        this.checkEquals(marshallContext.marshall(bigDecimal), marshallContext2.marshall(bigDecimal), () -> "" + bigDecimal);

        final LocalDateTime localDateTime = LocalDateTime.now();

        this.checkEquals(marshallContext.marshall(localDateTime), marshallContext2.marshall(localDateTime), () -> "" + localDateTime);

        this.checkEquals(marshallContext.marshall(metadata), marshallContext2.marshall(metadata), () -> "" + metadata);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadata.EMPTY
                        .set(this.property1(), this.value1())
                        .set(this.property2(), this.value2()),
                "{\n" +
                        "  \"create-date-time\": \"2000-01-02T12:58:59\",\n" +
                        "  \"creator\": \"user@example.com\"\n" +
                        "}"
        );
    }

    @Test
    public void testFromEmptyJsonObject() {
        assertSame(SpreadsheetMetadata.EMPTY,
                SpreadsheetMetadata.unmarshall(JsonNode.object(), this.unmarshallContext()));
    }

    // patch............................................................................................................

    @Test
    public void testPatchEmptyObjectFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetMetadata.EMPTY.patch(
                        JsonNode.object(),
                        JsonNodeUnmarshallContexts.fake()
                )
        );
        this.checkEquals(
                "Empty patch",
                thrown.getMessage()
        );
    }

    @Test
    public void testPatchRemoveUnknownProperty() {
        this.patchAndCheck(
                SpreadsheetMetadata.EMPTY,
                JsonNode.object()
                        .set(JsonPropertyName.with(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL.value()), JsonNode.nullNode())
        );
    }

    @Test
    public void testPatchRemoveUnknownProperty2() {
        this.patchAndCheck(
                SpreadsheetMetadata.EMPTY
                        .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "AUD"),
                JsonNode.object()
                        .set(JsonPropertyName.with(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR.value()), JsonNode.nullNode())
        );
    }

    @Test
    public void testPatchSetProperty() {
        final SpreadsheetMetadata before = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.BIG_DECIMAL)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 1)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP);

        this.patchAndCheck(
                before,
                JsonNode.object()
                        .set(JsonPropertyName.with(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL.value()), marshall("AUD")),
                before
                        .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "AUD")
        );
    }

    @Test
    public void testPatchSetProperty2() {
        final SpreadsheetMetadata before = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.BIG_DECIMAL)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 1)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP);

        this.patchAndCheck(
                before.set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "AUD"),
                JsonNode.object()
                        .set(JsonPropertyName.with(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR.value()), marshall('.')),
                before.set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "AUD")
                        .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '.')
        );
    }

    @Test
    public void testPatchSetStyleProperty() {
        final SpreadsheetMetadata before = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.BIG_DECIMAL)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 1)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP);

        final TextStyle style = TextStyle.EMPTY
                .set(TextStylePropertyName.COLOR, Color.BLACK);

        this.patchAndCheck(
                before,
                JsonNode.object()
                        .set(JsonPropertyName.with(SpreadsheetMetadataPropertyName.STYLE.value()), marshall(style)),
                before
                        .set(SpreadsheetMetadataPropertyName.STYLE, style)
        );
    }

    @Test
    public void testPatchSetStyleProperty2() {
        final TextStyle styleBefore = TextStyle.EMPTY
                .set(TextStylePropertyName.BACKGROUND_COLOR, Color.BLACK);

        final SpreadsheetMetadata before = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.BIG_DECIMAL)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 1)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP)
                .set(SpreadsheetMetadataPropertyName.STYLE, styleBefore);

        final TextStyle style = TextStyle.EMPTY
                .set(TextStylePropertyName.COLOR, Color.WHITE);

        this.patchAndCheck(
                before,
                JsonNode.object()
                        .set(JsonPropertyName.with(SpreadsheetMetadataPropertyName.STYLE.value()), marshall(style)),
                before.set(
                        SpreadsheetMetadataPropertyName.STYLE,
                        styleBefore.set(TextStylePropertyName.COLOR, Color.WHITE)
                )
        );
    }

    @Test
    public void testPatchReplaceProperty() {
        final SpreadsheetMetadata before = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.BIG_DECIMAL)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 1)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP);

        this.patchAndCheck(
                before
                        .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "AUD"),
                JsonNode.object()
                        .set(JsonPropertyName.with(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL.value()), marshall("NZD")),
                before
                        .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "NZD")
        );
    }

    @Test
    public void testPatchSetReplaceAndRemove() {
        final SpreadsheetMetadata before = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.BIG_DECIMAL)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 1)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP)
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "AUD")
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '.')
                .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, '%');

        this.patchAndCheck(
                before,
                JsonNode.object()
                        .set(JsonPropertyName.with(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL.value()), marshall("NZD"))
                        .set(JsonPropertyName.with(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR.value()), marshall('/'))
                        .set(JsonPropertyName.with(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL.value()), JsonNode.nullNode()),
                before
                        .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "NZD")
                        .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '/')
                        .remove(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL)
        );
    }

    // urlFragment......................................................................................................

    @Test
    public void testUrlFragmentSpreadsheetId() {
        this.urlFragmentAndCheck(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                "/spreadsheet-id"
        );
    }

    @Test
    public void testUrlFragmentSpreadsheetName() {
        this.urlFragmentAndCheck(
                SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                "/spreadsheet-name"
        );
    }

    @Test
    public void testUrlFragmentSpreadsheetDateFormatPattern() {
        this.urlFragmentAndCheck(
                SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN,
                "/pattern/date-format"
        );
    }

    @Test
    public void testUrlFragmentSpreadsheetDateTimeFormatPattern() {
        this.urlFragmentAndCheck(
                SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN,
                "/pattern/date-time-format"
        );
    }

    @Test
    public void testUrlFragmentSpreadsheetDateTimeParsePattern() {
        this.urlFragmentAndCheck(
                SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERN,
                "/pattern/date-time-parse"
        );
    }

    @Test
    public void testUrlFragmentSpreadsheetDateTimeOffset() {
        this.urlFragmentAndCheck(
                SpreadsheetMetadataPropertyName.DATETIME_OFFSET,
                "/date-time-offset"
        );
    }

    @Test
    public void testUrlFragmentSpreadsheetStyle() {
        this.urlFragmentAndCheck(
                SpreadsheetMetadataPropertyName.STYLE,
                "/style"
        );
    }

    private void urlFragmentAndCheck(final SpreadsheetMetadataPropertyName<?> propertyName,
                                     final String urlFragment) {
        this.checkEquals(
                UrlFragment.parse(urlFragment),
                propertyName.urlFragment(),
                () -> propertyName + " urlFragment"
        );
    }

    // json.............................................................................................................

    private JsonNode marshall(final Object value) {
        return JsonNodeMarshallContexts.basic()
                .marshall(value);
    }

    // helpers..........................................................................................................

    @Override
    public SpreadsheetMetadata createObject() {
        return this.metadata();
    }

    private SpreadsheetMetadata metadata() {
        return SpreadsheetMetadataNonEmpty.with(
                Maps.of(this.property1(), this.value1()),
                null
        );
    }

    @SuppressWarnings("SameReturnValue")
    private SpreadsheetMetadataPropertyName<LocalDateTime> property1() {
        return SpreadsheetMetadataPropertyName.CREATE_DATE_TIME;
    }

    private LocalDateTime value1() {
        return LocalDateTime.of(2000, 1, 2, 12, 58, 59);
    }

    @SuppressWarnings("SameReturnValue")
    private SpreadsheetMetadataPropertyName<EmailAddress> property2() {
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
    public SpreadsheetMetadata createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // PatchableTesting.................................................................................................

    @Override
    public SpreadsheetMetadata createPatchable() {
        return this.createObject();
    }

    @Override
    public JsonNode createPatch() {
        return JsonNode.object();
    }

    @Override
    public JsonNodeUnmarshallContext createPatchContext() {
        return JsonNodeUnmarshallContexts.basic(
                ExpressionNumberKind.BIG_DECIMAL,
                MathContext.UNLIMITED
        );
    }
}
