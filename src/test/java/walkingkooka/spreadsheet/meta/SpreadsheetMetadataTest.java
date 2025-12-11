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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterAliasSet;
import walkingkooka.convert.provider.ConverterProviders;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.AuditInfo;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextTesting;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.provider.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterAliasSet;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterAliasSet;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserAliasSet;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.patch.PatchableTesting;
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
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorAliasSet;
import walkingkooka.validation.provider.ValidatorProviders;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataTest implements ClassTesting2<SpreadsheetMetadata>,
    HashCodeEqualsDefinedTesting2<SpreadsheetMetadata>,
    HasUrlFragmentTesting,
    JsonNodeMarshallingTesting<SpreadsheetMetadata>,
    LocaleContextTesting,
    PatchableTesting<SpreadsheetMetadata>,
    ToStringTesting<SpreadsheetMetadata> {

    private final static LocaleContext LOCALE_CONTEXT = LocaleContexts.fake();

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    // NON_LOCALE_DEFAULTS..............................................................................................

    @Test
    public void testNonLocaleDefaults() {
        final SpreadsheetMetadata nonLocaleDefaults = SpreadsheetMetadata.NON_LOCALE_DEFAULTS;

        this.checkNotEquals(SpreadsheetMetadata.EMPTY, nonLocaleDefaults);
        this.checkNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET));

        final Length<?> none = Length.none();

        final TextStyle style = TextStyle.EMPTY
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

        this.checkEquals(
            Optional.of(style),
            nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.STYLE)
        );

        TextStyle withoutBorders = style;
        for (TextStylePropertyName<?> property : style.value().keySet()) {
            if (property.value()
                .toLowerCase()
                .contains("border")) {
                withoutBorders = withoutBorders.remove(property);
            }
        }

        this.checkEquals(
            withoutBorders,
            style
        );

        this.checkEquals(
            false,
            style.toString()
                .contains("border"),
            "border properties should not be present\n" + style
        );

        this.checkNotEquals(
            Optional.empty(),
            nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH)
        );

        this.checkNotEquals(
            Sets.empty(),
            SpreadsheetColorName.DEFAULTS.stream()
                .filter(n -> false == nonLocaleDefaults.defaults()
                    .get(n.spreadsheetMetadataPropertyName()).isPresent())
                .collect(Collectors.toSet()),
            () -> "missing named color defaults"
        );

        this.checkNotEquals(
            Sets.empty(),
            IntStream.range(SpreadsheetColors.MIN, SpreadsheetColors.MAX + 1)
                .mapToObj(SpreadsheetMetadataPropertyName::numberedColor)
                .filter(n -> false == nonLocaleDefaults.defaults().get(n).isPresent())
                .collect(Collectors.toSet()),
            () -> "missing numbered color defaults"
        );

        this.checkNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.DEFAULT_YEAR));
        this.checkNotEquals(Optional.of(ExpressionNumberKind.DEFAULT), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND));
        this.checkEquals(
            Optional.of(
                DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT
            ),
            nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT)
        );
        this.checkEquals(
            Optional.of(false),
            nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.HIDE_ZERO_VALUES)
        );
        this.checkNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.PRECISION));
        this.checkNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.ROUNDING_MODE));
        this.checkNotEquals(Optional.empty(), nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR));

        this.checkEquals(
            Optional.of(SpreadsheetSelection.A1),
            nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.VIEWPORT_HOME)
        );
    }

    // loadFromLocale...................................................................................................

    @Test
    public void testLoadFromLocaleWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.loadFromLocale(null)
        );
    }

    @Test
    public void testLoadFromLocale() {
        final Locale locale = Locale.ENGLISH;

        this.checkEquals(
            SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("dddd, mmmm d, yyyy").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("dddd, mmmm d, yyyy;dddd, mmmm d, yy;dddd, mmmm d;mmmm d, yyyy;mmmm d, yy;mmmm d;mmm d, yyyy;mmm d, yy;mmm d;m/d/yy;m/d/yyyy;m/d").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("dddd, mmmm d, yyyy \\a\\t h:mm:ss AM/PM").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("dddd, mmmm d, yyyy \\a\\t h:mm:ss AM/PM;dddd, mmmm d, yy \\a\\t h:mm:ss AM/PM;dddd, mmmm d, yy \\a\\t h:mm:ss;dddd, mmmm d, yy \\a\\t h:mm AM/PM;dddd, mmmm d, yyyy \\a\\t h:mm:ss.0 AM/PM;dddd, mmmm d, yyyy \\a\\t h:mm:ss.0;dddd, mmmm d, yyyy \\a\\t h:mm:ss;dddd, mmmm d, yyyy \\a\\t h:mm AM/PM;dddd, mmmm d, yyyy \\a\\t h:mm;dddd, mmmm d, yyyy, h:mm:ss AM/PM;dddd, mmmm d, yy, h:mm:ss AM/PM;dddd, mmmm d, yy, h:mm:ss;dddd, mmmm d, yy, h:mm AM/PM;dddd, mmmm d, yyyy, h:mm:ss.0 AM/PM;dddd, mmmm d, yyyy, h:mm:ss.0;dddd, mmmm d, yyyy, h:mm:ss;dddd, mmmm d, yyyy, h:mm AM/PM;dddd, mmmm d, yyyy, h:mm;dddd, mmmm d, yy, h:mm;mmmm d, yyyy \\a\\t h:mm:ss AM/PM;mmmm d, yy \\a\\t h:mm:ss AM/PM;mmmm d, yy \\a\\t h:mm:ss;mmmm d, yy \\a\\t h:mm AM/PM;mmmm d, yyyy \\a\\t h:mm:ss.0 AM/PM;mmmm d, yyyy \\a\\t h:mm:ss.0;mmmm d, yyyy \\a\\t h:mm:ss;mmmm d, yyyy \\a\\t h:mm AM/PM;mmmm d, yyyy \\a\\t h:mm;mmmm d, yyyy, h:mm:ss AM/PM;mmmm d, yy, h:mm:ss AM/PM;mmmm d, yy, h:mm:ss;mmmm d, yy, h:mm AM/PM;mmmm d, yyyy, h:mm:ss.0 AM/PM;mmmm d, yyyy, h:mm:ss.0;mmmm d, yyyy, h:mm:ss;mmmm d, yyyy, h:mm AM/PM;mmmm d, yyyy, h:mm;mmmm d, yy, h:mm;mmm d, yyyy, h:mm:ss AM/PM;mmm d, yy, h:mm:ss AM/PM;mmm d, yy, h:mm:ss;mmm d, yy, h:mm AM/PM;mmm d, yyyy, h:mm:ss.0 AM/PM;mmm d, yyyy, h:mm:ss.0;mmm d, yyyy, h:mm:ss;mmm d, yyyy, h:mm AM/PM;mmm d, yyyy, h:mm;mmm d, yy, h:mm;m/d/yy, h:mm:ss AM/PM;m/d/yy, h:mm:ss;m/d/yy, h:mm AM/PM;m/d/yyyy, h:mm:ss AM/PM;m/d/yyyy, h:mm:ss.0 AM/PM;m/d/yyyy, h:mm:ss.0;m/d/yyyy, h:mm:ss;m/d/yyyy, h:mm AM/PM;m/d/yy, h:mm:ss.0;m/d/yy, h:mm;m/d/yyyy, h:mm").spreadsheetParserSelector())
                .set(
                    SpreadsheetMetadataPropertyName.DATE_TIME_SYMBOLS,
                    DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(locale)
                    )
                ).set(
                    SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS,
                    DecimalNumberSymbols.with(
                        '-', // negativeSign
                        '+', // positiveSign
                        '0', // zeroDigit
                        "¤", // currencySymbol
                        '.', // decimalSeparator
                        "E", // exponent
                        ',', // groupSeparator
                        "\u221e",
                        '.',
                        "NaN",
                        '%', // percentSymbols
                        '\u2030' // permillSymbol
                    )
                ).set(SpreadsheetMetadataPropertyName.LOCALE, locale)
                .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("#,##0.###").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("#,##0.###;#,##0").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.TIME_FORMATTER, SpreadsheetPattern.parseTimeFormatPattern("h:mm:ss AM/PM").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("h:mm:ss AM/PM;h:mm:ss;h:mm:ss.0;h:mm AM/PM;h:mm").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, ','),
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                locale
            ).loadFromLocale(
                LocaleContexts.jre(locale)
            )
        );
    }

    // setOrRemove......................................................................................................

    @Test
    public void testSetOrRemoveNullValue() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP);

        this.checkEquals(
            metadata,
            metadata.setOrRemove(SpreadsheetMetadataPropertyName.ROUNDING_MODE, null)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP)
        );
    }

    @Test
    public void testSetOrRemoveNonNullValue() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP);

        this.checkEquals(
            metadata.set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_DOWN),
            metadata.setOrRemove(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_DOWN)
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
        final SpreadsheetMetadata metadata = this.metadata()
            .set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                SpreadsheetId.with(1)
            ).set(
                SpreadsheetMetadataPropertyName.VIEWPORT_HOME,
                SpreadsheetSelection.A1
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
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            AuditInfo.with(
                EmailAddress.parse("different@example.com"),
                LocalDateTime.MIN,
                EmailAddress.parse("different2@example.com"),
                LocalDateTime.MAX
            )
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
    public void testShouldViewsRefreshSameDifferentHome() {
        final SpreadsheetId id = SpreadsheetId.with(1);

        final SpreadsheetMetadata metadata = this.metadata()
            .set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, id)
            .set(
                SpreadsheetMetadataPropertyName.VIEWPORT_HOME,
                SpreadsheetSelection.A1
            );
        final SpreadsheetMetadata different = metadata.set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            id
        ).set(
            SpreadsheetMetadataPropertyName.VIEWPORT_HOME,
            SpreadsheetSelection.parseCell("B2")
        );

        this.checkNotEquals(
            metadata,
            different
        );

        this.shouldViewRefreshAndCheck(
            different,
            metadata,
            true
        );
    }

    @Test
    public void testShouldViewsRefreshSameDifferentSelection() {
        final SpreadsheetMetadata metadata = this.metadata();
        final SpreadsheetMetadata different = metadata.set(
            SpreadsheetMetadataPropertyName.VIEWPORT_SELECTION,
            SpreadsheetSelection.parseCell("Z99")
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
    public void testShouldViewsRefreshSameIdPresentDifferentDecimalNumberSymbol() {
        final SpreadsheetMetadata metadata = this.metadata()
            .set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                SpreadsheetId.with(1)
            );

        this.shouldViewRefreshAndCheck(
            metadata.set(
                SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS,
                DecimalNumberSymbols.fromDecimalFormatSymbols(
                    '+',
                    new DecimalFormatSymbols(Locale.FRANCE)
                )
            ),
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

    // Converter........................................................................................................

    @Test
    public void testConverterWithNullConverterSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.converter(
                null,
                ConverterProviders.fake(),
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testConverterWithNullConverterProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.converter(
                SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testConverterWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.converter(
                SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
                ConverterProviders.fake(),
                null
            )
        );
    }

    @Test
    public void testConverterWithMissingPropertyFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetMetadata.EMPTY.converter(
                SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
                ConverterProviders.fake(),
                PROVIDER_CONTEXT
            )
        );
        this.checkEquals(
            "Metadata missing: formulaConverter",
            thrown.getMessage()
        );
    }

    @Test
    public void testConverter() {
        final Locale locale = Locale.forLanguageTag("EN-AU");

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.NON_LOCALE_DEFAULTS
            .set(
                SpreadsheetMetadataPropertyName.LOCALE,
                locale
            ).loadFromLocale(
                LocaleContexts.jre(locale)
            );

        final Converter<SpreadsheetConverterContext> converter = metadata.converter(
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
            SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                (final ProviderContext p) -> metadata.dateTimeConverter(
                    spreadsheetFormatterProvider(),
                    spreadsheetParserProvider(),
                    p
                )
            ),
            PROVIDER_CONTEXT
        );
        this.checkNotEquals(
            null,
            converter
        );
    }

    @Test
    public void testConverterToStringPrefixedByPropertyNameWithFormulaConverter() {
        final Locale locale = Locale.forLanguageTag("EN-AU");

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.NON_LOCALE_DEFAULTS
            .set(
                SpreadsheetMetadataPropertyName.LOCALE,
                locale
            ).loadFromLocale(
                LocaleContexts.jre(locale)
            );

        final Converter<SpreadsheetConverterContext> converter = metadata.converter(
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
            SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                (final ProviderContext p) -> metadata.dateTimeConverter(
                    spreadsheetFormatterProvider(),
                    spreadsheetParserProvider(),
                    p
                )
            ),
            PROVIDER_CONTEXT
        );

        this.toStringAndCheck(
            converter,
            "formulaConverter: collection(text, number, date-time, basic, spreadsheet-value, boolean, error-throwing, color, expression, environment, json, locale, template, net)"
        );
    }

    @Test
    public void testConverterToStringPrefixedByPropertyNameWithFormatterConverter() {
        final Locale locale = Locale.forLanguageTag("EN-AU");

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.NON_LOCALE_DEFAULTS
            .set(
                SpreadsheetMetadataPropertyName.LOCALE,
                locale
            ).loadFromLocale(
                LocaleContexts.jre(locale)
            );

        final Converter<SpreadsheetConverterContext> converter = metadata.converter(
            SpreadsheetMetadataPropertyName.FORMATTING_CONVERTER,
            SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                (final ProviderContext p) -> metadata.dateTimeConverter(
                    spreadsheetFormatterProvider(),
                    spreadsheetParserProvider(),
                    p
                )
            ),
            PROVIDER_CONTEXT
        );

        this.toStringAndCheck(
            converter,
            "formattingConverter: collection(text, number, date-time, basic, spreadsheet-value, boolean, error-throwing, color, expression, environment, locale, plugins, spreadsheet-metadata, style, text-node, template, net)"
        );
    }

    // DateTimeConverter................................................................................................

    @Test
    public void testDateTimeConverterWithNullSpreadsheetFormatterProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.dateTimeConverter(
                null,
                SpreadsheetParserProviders.fake(),
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testDateTimeConverterWithNullSpreadsheetParserProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.dateTimeConverter(
                SpreadsheetFormatterProviders.fake(),
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testDateTimeConverterWithMissingPropertyFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetMetadata.EMPTY.dateTimeConverter(
                SpreadsheetFormatterProviders.fake(),
                SpreadsheetParserProviders.fake(),
                PROVIDER_CONTEXT
            )
        );
        this.checkEquals(
            "Metadata missing: dateFormatter, dateParser, dateTimeFormatter, dateTimeParser, timeFormatter, timeParser",
            thrown.getMessage()
        );
    }

    @Test
    public void testDateTimeConverter() {
        final Locale locale = Locale.forLanguageTag("EN-AU");

        final Converter<SpreadsheetConverterContext> converter = SpreadsheetMetadata.NON_LOCALE_DEFAULTS
            .set(
                SpreadsheetMetadataPropertyName.LOCALE,
                locale
            ).loadFromLocale(
                LocaleContexts.jre(locale)
            ).dateTimeConverter(
                spreadsheetFormatterProvider(),
                spreadsheetParserProvider(),
                PROVIDER_CONTEXT
            );
        this.checkNotEquals(
            null,
            converter
        );
    }
    
    // ExpressionFunctionProvider.......................................................................................

    @Test
    public void testExpressionFunctionProviderWithNullPropertyNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.expressionFunctionProvider(
                null,
                ExpressionFunctionProviders.fake()
            )
        );
    }

    @Test
    public void testExpressionFunctionProviderWithNullExpressionFunctionProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.expressionFunctionProvider(
                SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS,
                null
            )
        );
    }

    @Test
    public void testExpressionFunctionProviderMissingRequiredPropertiesFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetMetadata.EMPTY.expressionFunctionProvider(
                SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS,
                ExpressionFunctionProviders.fake()
            )
        );
        this.checkEquals(
            "Metadata missing: formulaFunctions",
            thrown.getMessage(),
            "message"
        );
    }

    // spreadsheetConverterContext......................................................................................

    @Test
    public void testSpreadsheetConverterContextWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetConverterContext(
                null,
                SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                SpreadsheetLabelNameResolvers.fake(),
                ConverterProviders.fake(),
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testSpreadsheetConverterContextWithNullValidationReferenceFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetConverterContext(
                SpreadsheetMetadata.NO_CELL,
                null,
                SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                SpreadsheetLabelNameResolvers.fake(),
                ConverterProviders.fake(),
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testSpreadsheetConverterContextWithNullPropertyNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetConverterContext(
                SpreadsheetMetadata.NO_CELL,
                SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                null,
                SpreadsheetLabelNameResolvers.fake(),
                ConverterProviders.fake(),
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testSpreadsheetConverterContextWithNullSpreadsheetLabelNameResolverFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetConverterContext(
                SpreadsheetMetadata.NO_CELL,
                SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                null,
                ConverterProviders.fake(),
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testSpreadsheetConverterContextWithNullConverterProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetConverterContext(
                SpreadsheetMetadata.NO_CELL,
                SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                SpreadsheetLabelNameResolvers.fake(),
                null,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testSpreadsheetConverterContextWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetConverterContext(
                SpreadsheetMetadata.NO_CELL,
                SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                SpreadsheetLabelNameResolvers.fake(),
                ConverterProviders.fake(),
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testSpreadsheetConverterContextWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetConverterContext(
                SpreadsheetMetadata.NO_CELL,
                SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                SpreadsheetLabelNameResolvers.fake(),
                ConverterProviders.fake(),
                LOCALE_CONTEXT,
                null
            )
        );
    }

    @Test
    public void testSpreadsheetConverterContextWithMissingRequiredPropertiesFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetConverterContext(
                SpreadsheetMetadata.NO_CELL,
                SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                SpreadsheetLabelNameResolvers.fake(),
                ConverterProviders.fake(),
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );

        this.checkEquals(
            "Metadata missing: dateTimeOffset, defaultYear, expressionNumberKind, findConverter, generalNumberFormatDigitCount, locale, precision, roundingMode, twoDigitYear, valueSeparator",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testSpreadsheetConverterContextAndCellDateTimeSymbolsDecimalNumberSymbols() {
        final Locale locale = Locale.forLanguageTag("EN-AU");

        final char positiveSign = '*';

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY
        ).setDateTimeSymbols(
            Optional.of(
                DateTimeSymbols.fromDateFormatSymbols(
                    new DateFormatSymbols(locale)
                ).setMonthNames(
                    Lists.of("Jan*", "Feb*", "Mar*", "Apr*", "May*", "Jun*", "Jul*", "Aug*", "Sep*", "Oct*", "Nov*", "Dec*")
                )
            )
        ).setDecimalNumberSymbols(
            Optional.of(
                DecimalNumberSymbols.fromDecimalFormatSymbols(
                    positiveSign,
                    new DecimalFormatSymbols(locale)
                )
            )
        );

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            locale
        ).loadFromLocale(
            LocaleContexts.jre(locale)
        ).set(
            SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET,
            Converters.EXCEL_1900_DATE_SYSTEM_OFFSET
        ).set(
            SpreadsheetMetadataPropertyName.DEFAULT_YEAR,
            1950
        ).set(
            SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND,
            ExpressionNumberKind.BIG_DECIMAL
        ).set(
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
            ConverterSelector.parse("simple")
        ).set(
            SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT
        ).set(
            SpreadsheetMetadataPropertyName.PRECISION,
            10
        ).set(
            SpreadsheetMetadataPropertyName.ROUNDING_MODE,
            RoundingMode.HALF_UP
        ).set(
            SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR,
            50
        );

        final SpreadsheetConverterContext context = metadata.spreadsheetConverterContext(
            Optional.of(cell),
            SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
            SpreadsheetLabelNameResolvers.fake(),
            ConverterProviders.converters(),
            LOCALE_CONTEXT,
            PROVIDER_CONTEXT
        );

        this.checkEquals(
            positiveSign,
            context.positiveSign(),
            "positiveSign"
        );
        this.checkEquals(
            "Jan*",
            context.monthName(0),
            "monthName 0"
        );
    }

    // SpreadsheetProvider..............................................................................................

    @Test
    public void testSpreadsheetProviderWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetProvider(null)
        );
    }

    @Test
    public void testSpreadsheetProviderWithMissingRequiredPropertiesFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetProvider(
                spreadsheetProvider()
            )
        );

        this.checkEquals(
            "Metadata missing: comparators, converters, exporters, formHandlers, formatters, functions, importers, parsers, validators",
            thrown.getMessage()
        );
    }

    @Test
    public void testSpreadsheetProvider() {
        this.checkNotEquals(
            null,
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.COMPARATORS,
                SpreadsheetComparatorAliasSet.parse("comparator-1")
            ).set(
                SpreadsheetMetadataPropertyName.CONVERTERS,
                ConverterAliasSet.parse("converter-1")
            ).set(
                SpreadsheetMetadataPropertyName.EXPORTERS,
                SpreadsheetExporterAliasSet.parse("exporter-1")
            ).set(
                SpreadsheetMetadataPropertyName.FORM_HANDLERS,
                FormHandlerAliasSet.parse("form-handler-1")
            ).set(
                SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS,
                SpreadsheetExpressionFunctions.parseAliasSet("function-1")
            ).set(
                SpreadsheetMetadataPropertyName.FUNCTIONS,
                SpreadsheetExpressionFunctions.parseAliasSet("function-1")
            ).set(
                SpreadsheetMetadataPropertyName.FORMATTERS,
                SpreadsheetFormatterAliasSet.parse("formatter-1")
            ).set(
                SpreadsheetMetadataPropertyName.IMPORTERS,
                SpreadsheetImporterAliasSet.parse("importer-1")
            ).set(
                SpreadsheetMetadataPropertyName.PARSERS,
                SpreadsheetParserAliasSet.parse("parser-1")
            ).set(
                SpreadsheetMetadataPropertyName.VALIDATORS,
                ValidatorAliasSet.parse("validator-1")
            ).spreadsheetProvider(
                this.spreadsheetProvider()
            )
        );
    }

    private SpreadsheetProvider spreadsheetProvider() {
        final ExpressionFunction<Object, SpreadsheetExpressionEvaluationContext> function1 = new FakeExpressionFunction<>() {
            @Override
            public Optional<ExpressionFunctionName> name() {
                return Optional.of(
                    SpreadsheetExpressionFunctions.name("function-1")
                );
            }

            @Override
            public Object apply(final List<Object> parameters,
                                final SpreadsheetExpressionEvaluationContext context) {
                return "Hello";
            }
        };

        return SpreadsheetProviders.basic(
            ConverterProviders.converters(),
            ExpressionFunctionProviders.basic(
                Url.parseAbsolute("https://example.com/"),
                CaseSensitivity.INSENSITIVE,
                Sets.of(
                    function1
                )
            ),
            SpreadsheetComparatorProviders.spreadsheetComparators(),
            SpreadsheetExporterProviders.spreadsheetExport(),
            SpreadsheetFormatterProviders.spreadsheetFormatters(),
            FormHandlerProviders.validation(),
            SpreadsheetImporterProviders.spreadsheetImport(),
            SpreadsheetParserProviders.spreadsheetParsePattern(
                SpreadsheetFormatterProviders.spreadsheetFormatters()
            ),
            ValidatorProviders.validators()
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
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"creator@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  },\n" +
                "  \"hideZeroValues\": true\n" +
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
                .setNull(
                    JsonPropertyName.with(
                        SpreadsheetMetadataPropertyName.ROUNDING_MODE.value()
                    )
                )
        );
    }

    @Test
    public void testPatchRemoveUnknownProperty2() {
        this.patchAndCheck(
            SpreadsheetMetadata.EMPTY
                .set(
                    SpreadsheetMetadataPropertyName.ROUNDING_MODE,
                    RoundingMode.HALF_UP
                ),
            JsonNode.object()
                .setNull(
                    JsonPropertyName.with(
                        SpreadsheetMetadataPropertyName.PRECISION.value()
                    )
                )
        );
    }

    @Test
    public void testPatchSetProperty() {
        final SpreadsheetMetadataPropertyName<RoundingMode> property = SpreadsheetMetadataPropertyName.ROUNDING_MODE;
        final RoundingMode roundingMode = RoundingMode.HALF_UP;

        final SpreadsheetMetadata before = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.BIG_DECIMAL)
            .set(SpreadsheetMetadataPropertyName.PRECISION, 1)
            .set(property, roundingMode);

        this.patchAndCheck(
            before,
            JsonNode.object()
                .set(
                    JsonPropertyName.with(
                        property.value()
                    ),
                    marshall(roundingMode)
                ),
            before.set(
                property,
                roundingMode
            )
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
        final SpreadsheetMetadataPropertyName<RoundingMode> property = SpreadsheetMetadataPropertyName.ROUNDING_MODE;
        final RoundingMode roundingMode = RoundingMode.HALF_UP;

        final SpreadsheetMetadata before = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.BIG_DECIMAL)
            .set(SpreadsheetMetadataPropertyName.PRECISION, 1)
            .set(property, RoundingMode.HALF_DOWN);

        this.patchAndCheck(
            before,
            JsonNode.object()
                .set(
                    JsonPropertyName.with(
                        property.value()
                    ),
                    marshall(roundingMode)),
            before.set(
                property,
                roundingMode
            )
        );
    }

    @Test
    public void testPatchSetReplaceAndRemove() {
        final SpreadsheetMetadata before = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.BIG_DECIMAL)
            .set(SpreadsheetMetadataPropertyName.PRECISION, 1)
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP);

        this.patchAndCheck(
            before,
            JsonNode.object()
                .set(JsonPropertyName.with(
                        SpreadsheetMetadataPropertyName.PRECISION.value()
                    ),
                    marshall(2)
                ).setNull(
                    JsonPropertyName.with(
                        SpreadsheetMetadataPropertyName.ROUNDING_MODE.value()
                    )
                ),
            before.set(SpreadsheetMetadataPropertyName.PRECISION, 2)
                .remove(SpreadsheetMetadataPropertyName.ROUNDING_MODE)
        );
    }

    // urlFragment......................................................................................................

    @Test
    public void testUrlFragmentSpreadsheetId() {
        this.urlFragmentAndCheck(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            "spreadsheetId"
        );
    }

    @Test
    public void testUrlFragmentSpreadsheetName() {
        this.urlFragmentAndCheck(
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
            "spreadsheetName"
        );
    }

    @Test
    public void testUrlFragmentSpreadsheetDateFormatPattern() {
        this.urlFragmentAndCheck(
            SpreadsheetMetadataPropertyName.DATE_FORMATTER,
            "dateFormatter"
        );
    }

    @Test
    public void testUrlFragmentSpreadsheetDateTimeFormatPattern() {
        this.urlFragmentAndCheck(
            SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER,
            "dateTimeFormatter"
        );
    }

    @Test
    public void testUrlFragmentSpreadsheetDateTimeParsePattern() {
        this.urlFragmentAndCheck(
            SpreadsheetMetadataPropertyName.DATE_TIME_PARSER,
            "dateTimeParser"
        );
    }

    @Test
    public void testUrlFragmentSpreadsheetDateTimeOffset() {
        this.urlFragmentAndCheck(
            SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET,
            "dateTimeOffset"
        );
    }

    @Test
    public void testUrlFragmentSpreadsheetStyle() {
        this.urlFragmentAndCheck(
            SpreadsheetMetadataPropertyName.STYLE,
            "style"
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
    private SpreadsheetMetadataPropertyName<AuditInfo> property1() {
        return SpreadsheetMetadataPropertyName.AUDIT_INFO;
    }

    private AuditInfo value1() {
        return AuditInfo.with(
            EmailAddress.parse("creator@example.com"),
            LocalDateTime.of(1999, 12, 31, 12, 58, 59),
            EmailAddress.parse("modified@example.com"),
            LocalDateTime.of(2000, 1, 2, 12, 58, 59)
        );
    }

    @SuppressWarnings("SameReturnValue")
    private SpreadsheetMetadataPropertyName<Boolean> property2() {
        return SpreadsheetMetadataPropertyName.HIDE_ZERO_VALUES;
    }

    private Boolean value2() {
        return true;
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

    private static SpreadsheetFormatterProvider spreadsheetFormatterProvider() {
        return SpreadsheetFormatterProviders.spreadsheetFormatters();
    }

    private static SpreadsheetParserProvider spreadsheetParserProvider() {
        return SpreadsheetParserProviders.spreadsheetParsePattern(
            spreadsheetFormatterProvider()
        );
    }
}
