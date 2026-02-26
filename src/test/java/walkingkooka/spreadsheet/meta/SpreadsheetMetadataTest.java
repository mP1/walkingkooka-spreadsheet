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
import walkingkooka.currency.CurrencyContext;
import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.currency.FakeCurrencyContext;
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
import walkingkooka.props.HasPropertiesTesting;
import walkingkooka.props.Properties;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.color.SpreadsheetColors;
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
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.FakeHasUserDirectories;
import walkingkooka.storage.HasUserDirectories;
import walkingkooka.storage.StoragePath;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
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
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataTest implements ClassTesting2<SpreadsheetMetadata>,
    HashCodeEqualsDefinedTesting2<SpreadsheetMetadata>,
    HasPropertiesTesting,
    HasUrlFragmentTesting,
    JsonNodeMarshallingTesting<SpreadsheetMetadata>,
    LocaleContextTesting,
    PatchableTesting<SpreadsheetMetadata>,
    ToStringTesting<SpreadsheetMetadata> {

    private final static Optional<StoragePath> CURRENT_WORKING_DIRECTORY = Optional.of(
        StoragePath.parse("/current1/working2/directory3")
    );

    private final static Optional<StoragePath> HOME_DIRECTORY = Optional.of(
        StoragePath.parse("/home/user123")
    );

    private final static HasUserDirectories HAS_USER_DIRECTORIES = new FakeHasUserDirectories() {

        @Override
        public Optional<StoragePath> currentWorkingDirectory() {
            return CURRENT_WORKING_DIRECTORY;
        }

        @Override
        public Optional<StoragePath> homeDirectory() {
            return HOME_DIRECTORY;
        }
    };

    private final static Indentation INDENTATION = Indentation.SPACES2;

    private final static LineEnding LINE_ENDING = LineEnding.NL;

    private final static Locale LOCALE = Locale.forLanguageTag("en-AU");

    private final static LocaleContext LOCALE_CONTEXT = LocaleContexts.jre(LOCALE);

    private final static CurrencyContext CURRENCY_CONTEXT = new FakeCurrencyContext() {

        @Override
        public Optional<Currency> currencyForCurrencyCode(final String currencyCode) {
            return Optional.of(
                Currency.getInstance(currencyCode)
            );
        }

        @Override
        public Optional<Currency> currencyForLocale(final Locale locale) {
            return Optional.of(
                Currency.getInstance(locale)
            );
        }
    };

    private final static CurrencyLocaleContext CURRENCY_LOCALE_CONTEXT = CURRENCY_CONTEXT.setLocaleContext(LOCALE_CONTEXT);

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
            nonLocaleDefaults.get(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT)
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
        this.checkEquals(
            SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY, Currency.getInstance(LOCALE))
                .set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("dddd, d mmmm yyyy").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("dddd, d mmmm yyyy;dddd, d mmmm yy;dddd, d mmmm;d mmmm yyyy;d mmmm yy;d mmmm;d mmm yyyy;d mmm yy;d mmm;d/m/yy;d/m/yyyy;d/m").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("dddd, d mmmm yyyy \\a\\t h:mm:ss AM/PM").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("dddd, d mmmm yyyy \\a\\t h:mm:ss AM/PM;dddd, d mmmm yy \\a\\t h:mm:ss AM/PM;dddd, d mmmm yy \\a\\t h:mm:ss;dddd, d mmmm yy \\a\\t h:mm AM/PM;dddd, d mmmm yyyy \\a\\t h:mm:ss.0 AM/PM;dddd, d mmmm yyyy \\a\\t h:mm:ss.0;dddd, d mmmm yyyy \\a\\t h:mm:ss;dddd, d mmmm yyyy \\a\\t h:mm AM/PM;dddd, d mmmm yyyy \\a\\t h:mm;dddd, d mmmm yyyy, h:mm:ss AM/PM;dddd, d mmmm yy, h:mm:ss AM/PM;dddd, d mmmm yy, h:mm:ss;dddd, d mmmm yy, h:mm AM/PM;dddd, d mmmm yyyy, h:mm:ss.0 AM/PM;dddd, d mmmm yyyy, h:mm:ss.0;dddd, d mmmm yyyy, h:mm:ss;dddd, d mmmm yyyy, h:mm AM/PM;dddd, d mmmm yyyy, h:mm;dddd, d mmmm yy, h:mm;d mmmm yyyy \\a\\t h:mm:ss AM/PM;d mmmm yy \\a\\t h:mm:ss AM/PM;d mmmm yy \\a\\t h:mm:ss;d mmmm yy \\a\\t h:mm AM/PM;d mmmm yyyy \\a\\t h:mm:ss.0 AM/PM;d mmmm yyyy \\a\\t h:mm:ss.0;d mmmm yyyy \\a\\t h:mm:ss;d mmmm yyyy \\a\\t h:mm AM/PM;d mmmm yyyy \\a\\t h:mm;d mmmm yyyy, h:mm:ss AM/PM;d mmmm yy, h:mm:ss AM/PM;d mmmm yy, h:mm:ss;d mmmm yy, h:mm AM/PM;d mmmm yyyy, h:mm:ss.0 AM/PM;d mmmm yyyy, h:mm:ss.0;d mmmm yyyy, h:mm:ss;d mmmm yyyy, h:mm AM/PM;d mmmm yyyy, h:mm;d mmmm yy, h:mm;d mmm yyyy, h:mm:ss AM/PM;d mmm yy, h:mm:ss AM/PM;d mmm yy, h:mm:ss;d mmm yy, h:mm AM/PM;d mmm yyyy, h:mm:ss.0 AM/PM;d mmm yyyy, h:mm:ss.0;d mmm yyyy, h:mm:ss;d mmm yyyy, h:mm AM/PM;d mmm yyyy, h:mm;d mmm yy, h:mm;d/m/yy, h:mm:ss AM/PM;d/m/yy, h:mm:ss;d/m/yy, h:mm AM/PM;d/m/yyyy, h:mm:ss AM/PM;d/m/yyyy, h:mm:ss.0 AM/PM;d/m/yyyy, h:mm:ss.0;d/m/yyyy, h:mm:ss;d/m/yyyy, h:mm AM/PM;d/m/yy, h:mm:ss.0;d/m/yy, h:mm;d/m/yyyy, h:mm").spreadsheetParserSelector())
                .set(
                    SpreadsheetMetadataPropertyName.DATE_TIME_SYMBOLS,
                    DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(LOCALE)
                    )
                ).set(
                    SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS,
                    DecimalNumberSymbols.with(
                        '-', // negativeSign
                        '+', // positiveSign
                        '0', // zeroDigit
                        "$", // currencySymbol
                        '.', // decimalSeparator
                        "e", // exponent
                        ',', // groupSeparator
                        "\u221e",
                        '.',
                        "NaN",
                        '%', // percentSymbols
                        '\u2030' // permillSymbol
                    )
                ).set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
                .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("#,##0.###").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("#,##0.###;#,##0").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.TIME_FORMATTER, SpreadsheetPattern.parseTimeFormatPattern("h:mm:ss AM/PM").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("h:mm:ss AM/PM;h:mm:ss;h:mm:ss.0;h:mm AM/PM;h:mm").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, ','),
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                LOCALE
            ).loadFromLocale(
                CURRENCY_LOCALE_CONTEXT
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
                CURRENCY_CONTEXT.setLocaleContext(
                    LocaleContexts.jre(locale)
                )
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
                CURRENCY_CONTEXT.setLocaleContext(
                    LocaleContexts.jre(locale)
                )
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
            "formulaConverter: collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, json, locale, template, net)"
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
                CURRENCY_CONTEXT.setLocaleContext(
                    LocaleContexts.jre(locale)
                )
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
            "formattingConverter: collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, locale, plugins, spreadsheet-metadata, style, text-node, template, net)"
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
                CURRENCY_CONTEXT.setLocaleContext(
                    LocaleContexts.jre(locale)
                )
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
                HAS_USER_DIRECTORIES,
                INDENTATION,
                SpreadsheetLabelNameResolvers.fake(),
                LINE_ENDING,
                ConverterProviders.fake(),
                CURRENCY_LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testSpreadsheetConverterContextWithNullIndentationFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetConverterContext(
                SpreadsheetMetadata.NO_CELL,
                SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                HAS_USER_DIRECTORIES,
                null,
                SpreadsheetLabelNameResolvers.fake(),
                LINE_ENDING,
                ConverterProviders.fake(),
                CURRENCY_LOCALE_CONTEXT,
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
                HAS_USER_DIRECTORIES,
                INDENTATION,
                SpreadsheetLabelNameResolvers.fake(),
                LINE_ENDING,
                ConverterProviders.fake(),
                CURRENCY_LOCALE_CONTEXT,
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
                HAS_USER_DIRECTORIES,
                INDENTATION,
                SpreadsheetLabelNameResolvers.fake(),
                LINE_ENDING,
                ConverterProviders.fake(),
                CURRENCY_LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testSpreadsheetConverterContextWithNullHasUserDirectoriesFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetConverterContext(
                SpreadsheetMetadata.NO_CELL,
                SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                null,
                INDENTATION,
                SpreadsheetLabelNameResolvers.fake(),
                LINE_ENDING,
                ConverterProviders.fake(),
                CURRENCY_LOCALE_CONTEXT,
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
                HAS_USER_DIRECTORIES,
                INDENTATION,
                null,
                LINE_ENDING,
                ConverterProviders.fake(),
                CURRENCY_LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testSpreadsheetConverterContextWithNullLineEndingFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetConverterContext(
                SpreadsheetMetadata.NO_CELL,
                SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                HAS_USER_DIRECTORIES,
                INDENTATION,
                SpreadsheetLabelNameResolvers.fake(),
                null,
                ConverterProviders.fake(),
                CURRENCY_LOCALE_CONTEXT,
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
                HAS_USER_DIRECTORIES,
                INDENTATION,
                SpreadsheetLabelNameResolvers.fake(),
                LINE_ENDING,
                null,
                CURRENCY_LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testSpreadsheetConverterContextWithNullCurrencyLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetConverterContext(
                SpreadsheetMetadata.NO_CELL,
                SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                HAS_USER_DIRECTORIES,
                INDENTATION,
                SpreadsheetLabelNameResolvers.fake(),
                LINE_ENDING,
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
                HAS_USER_DIRECTORIES,
                INDENTATION,
                SpreadsheetLabelNameResolvers.fake(),
                LINE_ENDING,
                ConverterProviders.fake(),
                CURRENCY_LOCALE_CONTEXT,
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
                HAS_USER_DIRECTORIES,
                INDENTATION,
                SpreadsheetLabelNameResolvers.fake(),
                LINE_ENDING,
                ConverterProviders.fake(),
                CURRENCY_LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );

        this.checkEquals(
            "Metadata missing: dateTimeOffset, decimalNumberDigitCount, defaultYear, expressionNumberKind, findConverter, locale, precision, roundingMode, twoDigitYear, valueSeparator",
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
            CURRENCY_CONTEXT.setLocaleContext(
                LocaleContexts.jre(locale)
            )
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
            SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT,
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
            HAS_USER_DIRECTORIES,
            INDENTATION,
            SpreadsheetLabelNameResolvers.fake(),
            LINE_ENDING,
            ConverterProviders.converters(),
            CURRENCY_LOCALE_CONTEXT,
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
            (String cc) -> Optional.ofNullable(
                Currency.getInstance(cc)
            ),
            (String lt) -> Optional.of(
                Locale.forLanguageTag(lt)
            ),
            ExpressionNumberKind.BIG_DECIMAL,
            MathContext.UNLIMITED
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

    // hashCode/equals..................................................................................................

    @Override
    public SpreadsheetMetadata createObject() {
        return this.metadata();
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetMetadata unmarshall(final JsonNode from,
                                          final JsonNodeUnmarshallContext context) {
        return SpreadsheetMetadata.unmarshall(from, context);
    }

    @Override
    public SpreadsheetMetadata createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // HasProperties....................................................................................................

    @Test
    public void testPropertiesWhenEmpty() {
        this.propertiesAndCheck(
            SpreadsheetMetadata.EMPTY,
            Properties.EMPTY
        );
    }

    @Test
    public void testPropertiesWithAuditInfo() {
        this.propertiesAndCheck(
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.create(
                    EmailAddress.parse("user@example.com"),
                    LocalDateTime.MIN
                )
            ),
            "auditInfo.createdBy=user@example.com\r\n" +
                "auditInfo.createdTimestamp=-999999999-01-01T00:00\r\n" +
                "auditInfo.modifiedBy=user@example.com\r\n" +
                "auditInfo.modifiedTimestamp=-999999999-01-01T00:00\r\n"
        );
    }

    @Test
    public void testPropertiesWithLocale() {
        this.propertiesAndCheck(
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                Locale.forLanguageTag("en-AU")
            ),
            "locale=en-AU\r\n"
        );
    }

    @Test
    public void testPropertiesWithTextStyle() {
        this.propertiesAndCheck(
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.STYLE,
                TextStyle.EMPTY.set(
                    TextStylePropertyName.COLOR,
                    Color.BLACK
                ).set(
                    TextStylePropertyName.TEXT_ALIGN,
                    TextAlign.LEFT
                )
            ),
            "style.color=black\r\n" +
                "style.text-align=LEFT\r\n"
        );
    }

    @Test
    public void testProperties() {
        this.propertiesAndCheck(
            SpreadsheetMetadataTesting.METADATA_EN_AU,
            "auditInfo.createdBy=user@example.com\r\n" +
                "auditInfo.createdTimestamp=1999-12-31T12:58\r\n" +
                "auditInfo.modifiedBy=user@example.com\r\n" +
                "auditInfo.modifiedTimestamp=1999-12-31T12:58\r\n" +
                "autoHideScrollbars=false\r\n" +
                "cellCharacterWidth=1\r\n" +
                "color1=black\r\n" +
                "color2=white\r\n" +
                "colorBlack=1\r\n" +
                "colorWhite=2\r\n" +
                "comparators=date, date-time, day-of-month, day-of-week, hour-of-am-pm, hour-of-day, minute-of-hour, month-of-year, nano-of-second, number, seconds-of-minute, text, text-case-insensitive, time, year\r\n" +
                "converters=basic, boolean, boolean-to-text, collection, collection-to, collection-to-list, color, color-to-color, color-to-number, date-time, date-time-symbols, decimal-number-symbols, environment, error-throwing, error-to-error, error-to-number, expression, form-and-validation, format-pattern-to-string, has-formatter-selector, has-host-address, has-parser-selector, has-properties, has-spreadsheet-selection, has-style, has-text-node, has-validator-selector, json, json-to, locale, locale-to-text, net, null-to-number, number, number-to-color, number-to-number, number-to-text, optional-to, plugins, spreadsheet-cell-set, spreadsheet-metadata, spreadsheet-selection-to-spreadsheet-selection, spreadsheet-selection-to-text, spreadsheet-value, storage, storage-path-json-to-class, storage-path-properties-to-class, storage-path-txt-to-class, storage-value-info-list-to-text, style, system, template, text, text-node, text-to-boolean-list, text-to-color, text-to-csv-string-list, text-to-date-list, text-to-date-time-list, text-to-email-address, text-to-environment-value-name, text-to-error, text-to-expression, text-to-flag, text-to-form-name, text-to-has-host-address, text-to-host-address, text-to-json, text-to-line-ending, text-to-locale, text-to-number-list, text-to-object, text-to-spreadsheet-color-name, text-to-spreadsheet-formatter-selector, text-to-spreadsheet-id, text-to-spreadsheet-metadata, text-to-spreadsheet-metadata-color, text-to-spreadsheet-metadata-property-name, text-to-spreadsheet-name, text-to-spreadsheet-selection, text-to-spreadsheet-text, text-to-storage-path, text-to-string-list, text-to-template-value-name, text-to-text, text-to-text-node, text-to-text-style, text-to-text-style-property-name, text-to-time-list, text-to-url, text-to-url-fragment, text-to-url-query-string, text-to-validation-error, text-to-validator-selector, text-to-value-type, text-to-zone-offset, to-boolean, to-json-node, to-json-text, to-number, to-string, to-styleable, to-validation-checkbox, to-validation-choice, to-validation-choice-list, to-validation-error-list, url, url-to-hyperlink, url-to-image\r\n" +
                "currency=AUD\r\n" +
                "dateFormatter=date yyyy/mm/dd\r\n" +
                "dateParser=date yyyy/mm/dd\r\n" +
                "dateTimeFormatter=date-time yyyy/mm/dd hh:mm\r\n" +
                "dateTimeOffset=-25569\r\n" +
                "dateTimeParser=date-time yyyy/mm/dd hh:mm\r\n" +
                "dateTimeSymbols=\"am,pm\",\"January,February,March,April,May,June,July,August,September,October,November,December\",\"Jan.,Feb.,Mar.,Apr.,May,Jun.,Jul.,Aug.,Sep.,Oct.,Nov.,Dec.\",\"Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday\",\"Sun.,Mon.,Tue.,Wed.,Thu.,Fri.,Sat.\"\r\n" +
                "decimalNumberDigitCount=8\r\n" +
                "decimalNumberSymbols=-,+,0,$,.,e,\",\",\\u221e,.,NaN,%,\\u2030\r\n" +
                "defaultFormHandler=basic\r\n" +
                "defaultYear=2000\r\n" +
                "errorFormatter=badge-error text @\r\n" +
                "exporters=collection, empty, json\r\n" +
                "expressionNumberKind=BIG_DECIMAL\r\n" +
                "findConverter=collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, locale, spreadsheet-metadata, style, text-node, template, net)\r\n" +
                "findFunctions=\r\n" +
                "formHandlers=\r\n" +
                "formatters=accounting, automatic, badge-error, collection, currency, date, date-time, default-text, expression, full-date, full-date-time, full-time, general, hyperlinking, long-date, long-date-time, long-time, medium-date, medium-date-time, medium-time, number, percent, scientific, short-date, short-date-time, short-time, text, time\r\n" +
                "formattingConverter=collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, locale, plugins, style, text-node, template, net)\r\n" +
                "formattingFunctions=\r\n" +
                "formulaConverter=collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, locale, template, net, json)\r\n" +
                "formulaFunctions=\r\n" +
                "functions=\r\n" +
                "importers=collection, empty, json\r\n" +
                "locale=en-AU\r\n" +
                "numberFormatter=number 0.#;0.#;0\r\n" +
                "numberParser=number 0.#;0.#;0\r\n" +
                "parsers=date, date-time, general, number, time, whole-number\r\n" +
                "plugins=\r\n" +
                "precision=7\r\n" +
                "roundingMode=HALF_UP\r\n" +
                "scriptingConverter=collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, json, locale, plugins, spreadsheet-metadata, storage, style, text-node, text-to-line-ending, template, net)\r\n" +
                "scriptingFunctions=\r\n" +
                "showFormulaEditor=true\r\n" +
                "showFormulas=false\r\n" +
                "showGridLines=true\r\n" +
                "showHeadings=true\r\n" +
                "sortComparators=date,datetime,day-of-month,day-of-year,hour-of-ampm,hour-of-day,minute-of-hour,month-of-year,nano-of-second,number,seconds-of-minute,text,text-case-insensitive,time,year\r\n" +
                "sortConverter=collection(text, boolean, number, date-time, basic, spreadsheet-value, locale)\r\n" +
                "style.height=50px\r\n" +
                "style.width=100px\r\n" +
                "textFormatter=text @\r\n" +
                "timeFormatter=time hh:mm:ss\r\n" +
                "timeParser=time hh:mm:ss\r\n" +
                "twoDigitYear=50\r\n" +
                "validationConverter=collection(text, boolean, number, date-time, basic, spreadsheet-value, environment, error-throwing, expression, form-and-validation, locale, plugins, template, json)\r\n" +
                "validationFunctions=\r\n" +
                "validationValidators=absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\r\n" +
                "validators=absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\r\n" +
                "valueSeparator=,\r\n"
        );
    }

    // fromProperties...................................................................................................

    @Test
    public void testFromPropertiesWithNullPropertiesFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.fromProperties(
                null,
                CURRENCY_LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testFromPropertiesWithNullCurrencyLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadata.fromProperties(
                Properties.EMPTY,
                null
            )
        );
    }

    @Test
    public void testFromPropertiesAuditInfo() {
        this.fromPropertiesAndCheck(
            "auditInfo.createdBy=created-by@example.com\n" +
                "auditInfo.createdTimestamp=1999-12-31T12:58:59\n" +
                "auditInfo.modifiedBy=modified-by@example.com\n" +
                "auditInfo.modifiedTimestamp=2000-01-02T12:58:59",
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.with(
                    EmailAddress.parse("created-by@example.com"),
                    LocalDateTime.of(
                        1999,
                        12,
                        31,
                        12,
                        58,
                        59
                    ),
                    EmailAddress.parse("modified-by@example.com"),
                    LocalDateTime.of(
                        2000,
                        1,
                        2,
                        12,
                        58,
                        59
                    )
                )
            )
        );
    }

    @Test
    public void testFromPropertiesExpressionNumberKind() {
        this.fromPropertiesAndCheck(
            "expressionNumberKind=BIG_DECIMAL",
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND,
                ExpressionNumberKind.BIG_DECIMAL
            )
        );
    }

    @Test
    public void testFromPropertiesLocale() {
        this.fromPropertiesAndCheck(
            "locale=en-AU",
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                LOCALE
            )
        );
    }

    @Test
    public void testFromPropertiesAllProperties() {
        this.fromPropertiesAndCheck(
            "auditInfo.createdBy=user@example.com\r\n" +
                "auditInfo.createdTimestamp=1999-12-31T12:58\r\n" +
                "auditInfo.modifiedBy=user@example.com\r\n" +
                "auditInfo.modifiedTimestamp=1999-12-31T12:58\r\n" +
                "autoHideScrollbars=false\r\n" +
                "cellCharacterWidth=1\r\n" +
                "color1=black\r\n" +
                "color2=white\r\n" +
                "colorBlack=1\r\n" +
                "colorWhite=2\r\n" +
                "comparators=date, date-time, day-of-month, day-of-week, hour-of-am-pm, hour-of-day, minute-of-hour, month-of-year, nano-of-second, number, seconds-of-minute, text, text-case-insensitive, time, year\r\n" +
                "converters=basic, boolean, boolean-to-text, collection, collection-to, collection-to-list, color, color-to-color, color-to-number, date-time, date-time-symbols, decimal-number-symbols, environment, error-throwing, error-to-error, error-to-number, expression, form-and-validation, format-pattern-to-string, has-formatter-selector, has-host-address, has-parser-selector, has-properties, has-spreadsheet-selection, has-style, has-text-node, has-validator-selector, json, json-to, locale, locale-to-text, net, null-to-number, number, number-to-color, number-to-number, number-to-text, optional-to, plugins, spreadsheet-cell-set, spreadsheet-metadata, spreadsheet-selection-to-spreadsheet-selection, spreadsheet-selection-to-text, spreadsheet-value, storage, storage-path-json-to-class, storage-path-properties-to-class, storage-path-txt-to-class, storage-value-info-list-to-text, style, system, template, text, text-node, text-to-boolean-list, text-to-color, text-to-csv-string-list, text-to-date-list, text-to-date-time-list, text-to-email-address, text-to-environment-value-name, text-to-error, text-to-expression, text-to-flag, text-to-form-name, text-to-has-host-address, text-to-host-address, text-to-json, text-to-line-ending, text-to-locale, text-to-number-list, text-to-object, text-to-spreadsheet-color-name, text-to-spreadsheet-formatter-selector, text-to-spreadsheet-id, text-to-spreadsheet-metadata, text-to-spreadsheet-metadata-color, text-to-spreadsheet-metadata-property-name, text-to-spreadsheet-name, text-to-spreadsheet-selection, text-to-spreadsheet-text, text-to-storage-path, text-to-string-list, text-to-template-value-name, text-to-text, text-to-text-node, text-to-text-style, text-to-text-style-property-name, text-to-time-list, text-to-url, text-to-url-fragment, text-to-url-query-string, text-to-validation-error, text-to-validator-selector, text-to-value-type, text-to-zone-offset, to-boolean, to-json-node, to-json-text, to-number, to-string, to-styleable, to-validation-checkbox, to-validation-choice, to-validation-choice-list, to-validation-error-list, url, url-to-hyperlink, url-to-image\r\n" +
                "currency=AUD\r\n" +
                "dateFormatter=date yyyy/mm/dd\r\n" +
                "dateParser=date yyyy/mm/dd\r\n" +
                "dateTimeFormatter=date-time yyyy/mm/dd hh:mm\r\n" +
                "dateTimeOffset=-25569\r\n" +
                "dateTimeParser=date-time yyyy/mm/dd hh:mm\r\n" +
                "dateTimeSymbols=\"am,pm\",\"January,February,March,April,May,June,July,August,September,October,November,December\",\"Jan.,Feb.,Mar.,Apr.,May,Jun.,Jul.,Aug.,Sep.,Oct.,Nov.,Dec.\",\"Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday\",\"Sun.,Mon.,Tue.,Wed.,Thu.,Fri.,Sat.\"\r\n" +
                "decimalNumberDigitCount=8\r\n" +
                "decimalNumberSymbols=-,+,0,$,.,e,\",\",\\u221e,.,NaN,%,\\u2030\r\n" +
                "defaultFormHandler=basic\r\n" +
                "defaultYear=2000\r\n" +
                "errorFormatter=badge-error text @\r\n" +
                "exporters=collection, empty, json\r\n" +
                "expressionNumberKind=BIG_DECIMAL\r\n" +
                "findConverter=collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, locale, spreadsheet-metadata, style, text-node, template, net)\r\n" +
                "findFunctions=\r\n" +
                "formHandlers=\r\n" +
                "formatters=accounting, automatic, badge-error, collection, currency, date, date-time, default-text, expression, full-date, full-date-time, full-time, general, hyperlinking, long-date, long-date-time, long-time, medium-date, medium-date-time, medium-time, number, percent, scientific, short-date, short-date-time, short-time, text, time\r\n" +
                "formattingConverter=collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, locale, plugins, style, text-node, template, net)\r\n" +
                "formattingFunctions=\r\n" +
                "formulaConverter=collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, locale, template, net, json)\r\n" +
                "formulaFunctions=\r\n" +
                "functions=\r\n" +
                "importers=collection, empty, json\r\n" +
                "locale=en-AU\r\n" +
                "numberFormatter=number 0.#;0.#;0\r\n" +
                "numberParser=number 0.#;0.#;0\r\n" +
                "parsers=date, date-time, general, number, time, whole-number\r\n" +
                "plugins=\r\n" +
                "precision=7\r\n" +
                "roundingMode=HALF_UP\r\n" +
                "scriptingConverter=collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, json, locale, plugins, spreadsheet-metadata, storage, style, text-node, text-to-line-ending, template, net)\r\n" +
                "scriptingFunctions=\r\n" +
                "showFormulaEditor=true\r\n" +
                "showFormulas=false\r\n" +
                "showGridLines=true\r\n" +
                "showHeadings=true\r\n" +
                "sortComparators=date,datetime,day-of-month,day-of-year,hour-of-ampm,hour-of-day,minute-of-hour,month-of-year,nano-of-second,number,seconds-of-minute,text,text-case-insensitive,time,year\r\n" +
                "sortConverter=collection(text, boolean, number, date-time, basic, spreadsheet-value, locale)\r\n" +
                "style.height=50px\r\n" +
                "style.width=100px\r\n" +
                "textFormatter=text @\r\n" +
                "timeFormatter=time hh:mm:ss\r\n" +
                "timeParser=time hh:mm:ss\r\n" +
                "twoDigitYear=50\r\n" +
                "validationConverter=collection(text, boolean, number, date-time, basic, spreadsheet-value, environment, error-throwing, expression, form-and-validation, locale, plugins, template, json)\r\n" +
                "validationFunctions=\r\n" +
                "validationValidators=absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\r\n" +
                "validators=absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\r\n" +
                "valueSeparator=,\r\n",
            SpreadsheetMetadataTesting.METADATA_EN_AU
        );
    }

    @Test
    public void testFromPropertiesAndPropertiesRoundtrip() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadataTesting.METADATA_EN_AU;

        this.fromPropertiesAndCheck(
            metadata.properties(),
            metadata
        );
    }

    private void fromPropertiesAndCheck(final String properties,
                                        final SpreadsheetMetadata expected) {
        this.fromPropertiesAndCheck(
            properties,
            CURRENCY_LOCALE_CONTEXT,
            expected
        );
    }

    private void fromPropertiesAndCheck(final String properties,
                                        final CurrencyLocaleContext context,
                                        final SpreadsheetMetadata expected) {
        this.fromPropertiesAndCheck(
            Properties.parse(properties),
            context,
            expected
        );
    }

    private void fromPropertiesAndCheck(final Properties properties,
                                        final SpreadsheetMetadata expected) {
        this.fromPropertiesAndCheck(
            properties,
            CURRENCY_LOCALE_CONTEXT,
            expected
        );
    }

    private void fromPropertiesAndCheck(final Properties properties,
                                        final CurrencyLocaleContext context,
                                        final SpreadsheetMetadata expected) {
        this.checkEquals(
            expected,
            SpreadsheetMetadata.fromProperties(
                properties,
                context
            )
        );
    }

    // helpers..........................................................................................................

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

    // helper...........................................................................................................

    private JsonNode marshall(final Object value) {
        return JsonNodeMarshallContexts.basic()
            .marshall(value);
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
