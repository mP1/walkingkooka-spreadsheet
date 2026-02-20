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
import walkingkooka.CanBeEmptyTesting;
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterProviders;
import walkingkooka.currency.CurrencyContext;
import walkingkooka.currency.CurrencyContexts;
import walkingkooka.environment.AuditInfo;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.storage.FakeHasUserDirectories;
import walkingkooka.storage.HasUserDirectories;
import walkingkooka.storage.StoragePath;
import walkingkooka.text.CharSequences;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.tree.text.WordWrap;
import walkingkooka.validation.Validator;
import walkingkooka.validation.provider.ValidatorSelector;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetMetadataTestCase<T extends SpreadsheetMetadata> implements CanBeEmptyTesting,
    ClassTesting2<SpreadsheetMetadata>,
    ConverterTesting,
    HashCodeEqualsDefinedTesting2<SpreadsheetMetadata>,
    JsonNodeMarshallingTesting<SpreadsheetMetadata>,
    HateosResourceTesting<SpreadsheetMetadata, SpreadsheetId>,
    ThrowableTesting,
    ToStringTesting<SpreadsheetMetadata>,
    TreePrintableTesting,
    SpreadsheetEnvironmentContextTesting {

    private final static Function<ValidatorSelector, Validator<SpreadsheetExpressionReference, SpreadsheetValidatorContext>> VALIDATOR_SELECTOR_TO_VALIDATOR = (final ValidatorSelector selector) -> {
        throw new UnsupportedOperationException();
    };

    final static BiFunction<Object, SpreadsheetExpressionReference, SpreadsheetExpressionEvaluationContext> VALUE_N_CELL_TO_SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT = (final Object value,
                                                                                                                                                                         final SpreadsheetExpressionReference cellOrLabel) -> {
        throw new UnsupportedOperationException();
    };

    final static ConverterProvider CONVERTER_PROVIDER = ConverterProviders.fake();

    final static Optional<StoragePath> CURRENT_WORKING_DIRECTORY = Optional.of(
        StoragePath.parse("/current1/working2/directory3")
    );

    final static Optional<StoragePath> HOME_DIRECTORY = Optional.of(
        StoragePath.parse("/home/user")
    );

    final static HasUserDirectories HAS_USER_DIRECTORIES = new FakeHasUserDirectories() {

        @Override
        public Optional<StoragePath> currentWorkingDirectory() {
            return CURRENT_WORKING_DIRECTORY;
        }

        @Override
        public Optional<StoragePath> homeDirectory() {
            return HOME_DIRECTORY;
        }
    };

    final static SpreadsheetLabelNameResolver LABEL_NAME_RESOLVER = SpreadsheetLabelNameResolvers.fake();

    final static LineEnding LINE_ENDING = LineEnding.NL;

    final static LocaleContext LOCALE_CONTEXT = LocaleContexts.jre(
        Locale.forLanguageTag("EN-AU")
    );

    final static CurrencyContext CURRENCY_CONTEXT = CurrencyContexts.jre(
        Currency.getInstance("AUD"),
        (Currency from, Currency to) -> {
            throw new UnsupportedOperationException();
        },
        LOCALE_CONTEXT
    );

    final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    SpreadsheetMetadataTestCase() {
        super();
    }

    // isEmpty..........................................................................................................

    @Test
    public final void testIsEmpty() {
        final SpreadsheetMetadata metadata = this.createObject();

        this.isEmptyAndCheck(
            metadata,
            metadata.value().isEmpty()
        );
    }

    // get..............................................................................................................

    @Test
    public final void testGetNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .get(null)
        );
    }

    @Test
    public final void testGetUnknown() {
        this.getAndCheck(
            this.createObject(),
            SpreadsheetMetadataPropertyName.ROUNDING_MODE
        );
    }

    @Test
    public final void testGetUnknownDefaultsToDefault() {
        final Integer value = 123;

        final SpreadsheetMetadata metadata = this.createObject();

        final SpreadsheetMetadataPropertyName<Integer> unknown = SpreadsheetMetadataPropertyName.PRECISION;
        this.getAndCheck(
            metadata,
            unknown
        );

        this.getAndCheck(
            metadata.setDefaults(
                SpreadsheetMetadata.EMPTY.set(
                    unknown,
                    value
                )
            ),
            unknown,
            value
        );
    }

    final <TT> void getAndCheck(final SpreadsheetMetadata metadata,
                                final SpreadsheetMetadataPropertyName<TT> propertyName) {
        this.getAndCheck(
            metadata,
            propertyName,
            Optional.empty()
        );
    }

    final <TT> void getAndCheck(final SpreadsheetMetadata metadata,
                                final SpreadsheetMetadataPropertyName<TT> propertyName,
                                final TT value) {
        this.getAndCheck(
            metadata,
            propertyName,
            Optional.of(value)
        );
    }

    final <TT> void getAndCheck(final SpreadsheetMetadata metadata,
                                final SpreadsheetMetadataPropertyName<TT> propertyName,
                                final Optional<TT> value) {
        this.checkEquals(
            value,
            metadata.get(propertyName),
            () -> metadata + " get " + propertyName
        );
    }

    // getOrFail........................................................................................................

    @Test
    public final void testGetOrFailFails() {
        final SpreadsheetMetadataPropertyName<DecimalNumberSymbols> propertyName = SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS;

        final SpreadsheetMetadataPropertyValueException thrown = assertThrows(
            SpreadsheetMetadataPropertyValueException.class,
            () -> this.createObject()
                .getOrFail(propertyName)
        );

        this.checkMessage(
            thrown,
            "Metadata " + propertyName.value() + "=null, Missing"
        );
        this.checkEquals(
            propertyName,
            thrown.name(),
            "property name"
        );
        this.checkEquals(
            null,
            thrown.value(),
            "property value"
        );
    }

    // getIgnoringDefaults..............................................................................................

    @Test
    public final void testGetIgnoringDefaultsNullPropertyNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .getIgnoringDefaults(null)
        );
    }

    // getIgnoresDefaults...............................................................................................

    @Test
    public final void testGetIgnoresDefaults() {
        final SpreadsheetMetadataPropertyName<Locale> propertyName = SpreadsheetMetadataPropertyName.LOCALE;
        final Locale value = Locale.ENGLISH;

        final SpreadsheetMetadata metadata = this.createObject()
            .setDefaults(
                SpreadsheetMetadata.EMPTY.set(
                    propertyName,
                    value
                )
            );
        this.getAndCheck(
            metadata,
            propertyName,
            value
        );
    }

    // effectiveStyle...................................................................................................

    @Test
    public final void testEffectiveStyleNotNull() {
        this.checkNotEquals(
            this.createObject().effectiveStyle(),
            null
        );
    }

    final void effectiveStyleAndCheck(final SpreadsheetMetadata metadata,
                                      final TextStyle expected) {
        final TextStyle effectiveStyle = metadata.effectiveStyle();

        this.checkEquals(
            expected,
            effectiveStyle,
            () -> "effectiveStyle of " + metadata
        );

        assertSame(
            effectiveStyle,
            metadata.effectiveStyle(),
            () -> "effectiveStyle not cached of " + metadata
        );
    }

    // set..............................................................................................................

    @Test
    public final void testSetNullPropertyNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .set(null, "value")
        );
    }

    @Test
    public final void testSetNullPropertyValueFails() {
        assertThrows(
            SpreadsheetMetadataPropertyValueException.class,
            () -> this.createObject()
                .set(
                    SpreadsheetMetadataPropertyName.AUDIT_INFO,
                    null
                )
        );
    }

    @Test
    public final void testSetInvalidPropertyValueFails() {
        assertThrows(
            SpreadsheetMetadataPropertyValueException.class, () -> {
                final SpreadsheetMetadataPropertyName<?> propertyName = SpreadsheetMetadataPropertyName.AUDIT_INFO;
                this.createObject()
                    .set(
                        propertyName,
                        Cast.to("invalid-expected-EmailAddress")
                    );
            });
    }

    final <TT> SpreadsheetMetadata setAndCheck(final SpreadsheetMetadata metadata,
                                               final SpreadsheetMetadataPropertyName<TT> propertyName,
                                               final TT value,
                                               final String expected) {
        final SpreadsheetMetadata set = metadata.set(propertyName, value);
        this.checkEquals(
            expected,
            set.toString(),
            () -> "set " + propertyName + " = " + CharSequences.quoteIfChars(value) + "\n" + metadata
        );
        return set;
    }

    // remove...........................................................................................................

    @Test
    public final void testRemoveNullFails() {
        assertThrows(NullPointerException.class, () -> this.createObject().remove(null));
    }

    @Test
    public final void testRemoveUnknown() {
        final SpreadsheetMetadata metadata = this.createObject();
        assertSame(
            metadata,
            metadata.remove(SpreadsheetMetadataPropertyName.ROUNDING_MODE)
        );
    }

    final SpreadsheetMetadata removeAndCheck(final SpreadsheetMetadata metadata,
                                             final SpreadsheetMetadataPropertyName<?> propertyName,
                                             final SpreadsheetMetadata expected) {
        final SpreadsheetMetadata removed = metadata.remove(propertyName);
        this.checkEquals(expected,
            removed,
            () -> metadata + " remove " + propertyName);
        return removed;
    }

    // getEffectiveStyleProperty.......................................................................................

    @Test
    public final void testGetEffectiveStylePropertyNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .getEffectiveStyleProperty(null)
        );
    }

    @Test
    public final void testGetEffectiveStylePropertyAbsent() {
        this.getEffectiveStylePropertyAndCheck(
            this.createObject(),
            TextStylePropertyName.WORD_WRAP,
            null
        );
    }

    @Test
    public final void testGetEffectiveStylePresentInDefault() {
        final TextStylePropertyName<WordWrap> textStylePropertyName = TextStylePropertyName.WORD_WRAP;
        final WordWrap wordWrap = WordWrap.BREAK_WORD;

        this.getEffectiveStylePropertyAndCheck(this.createObject()
                .setDefaults(
                    SpreadsheetMetadata.EMPTY.set(
                        SpreadsheetMetadataPropertyName.STYLE,
                        TextStyle.EMPTY.set(textStylePropertyName, wordWrap)
                    )
                ),
            textStylePropertyName,
            wordWrap
        );
    }

    final <TT> void getEffectiveStylePropertyAndCheck(final SpreadsheetMetadata metadata,
                                                      final TextStylePropertyName<TT> property,
                                                      final TT expected) {
        this.checkEquals(
            Optional.ofNullable(expected),
            metadata.getEffectiveStyleProperty(property),
            () -> metadata + " getEffectiveStyleProperty " + property
        );
    }

    // getEffectiveStyleOrFail..........................................................................................

    @Test
    public final void testGetEffectiveStylePropertyOrFailNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .getEffectiveStylePropertyOrFail(null)
        );
    }

    @Test
    public final void testGetEffectiveStyleOrFailAbsent() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createObject().getEffectiveStylePropertyOrFail(TextStylePropertyName.WORD_WRAP)
        );
    }

    @Test
    public final void testGetEffectiveStylePropertyOrFailPresentInDefault() {
        final TextStylePropertyName<WordWrap> textStylePropertyName = TextStylePropertyName.WORD_WRAP;
        final WordWrap wordWrap = WordWrap.BREAK_WORD;

        this.getEffectiveStylePropertyOrFailAndCheck(
            this.createObject()
                .setDefaults(
                    SpreadsheetMetadata.EMPTY.set(
                        SpreadsheetMetadataPropertyName.STYLE,
                        TextStyle.EMPTY.set(textStylePropertyName, wordWrap)
                    )
                ),
            textStylePropertyName,
            wordWrap
        );
    }

    final <TT> void getEffectiveStylePropertyOrFailAndCheck(final SpreadsheetMetadata metadata,
                                                            final TextStylePropertyName<TT> property,
                                                            final TT expected) {
        this.checkEquals(
            expected,
            metadata.getEffectiveStylePropertyOrFail(property),
            () -> metadata + " getEffectiveStyleOrFailProperty " + property
        );
    }

    // NameToColor......................................................................................................

    @Test
    public final void testNameToColor() {
        this.nameToColorAndCheck(this.createObject(), SpreadsheetColorName.with("unknown"), null);
    }

    final void nameToColorAndCheck(final SpreadsheetMetadata metadata,
                                   final SpreadsheetColorName name,
                                   final Color color) {
        final Function<SpreadsheetColorName, Optional<Color>> nameToColor = metadata.nameToColor();
        this.checkEquals(
            Optional.ofNullable(color),
            nameToColor.apply(name),
            () -> name + " to color " + metadata
        );
    }

    // NumberToColor....................................................................................................

    @Test
    public final void testNumberToColor() {
        this.numberToColorAndCheck(this.createObject(), 99, null);
    }

    final void numberToColorAndCheck(final SpreadsheetMetadata metadata,
                                     final int number,
                                     final Color color) {
        final Function<Integer, Optional<Color>> numberToColor = metadata.numberToColor();
        this.checkEquals(
            Optional.ofNullable(color),
            numberToColor.apply(number),
            () -> number + " to color " + metadata
        );
    }

    // NumberToColorName................................................................................................

    @Test
    public final void testNumberToColorName() {
        this.numberToColorNameAndCheck(
            this.createObject(),
            99,
            null
        );
    }

    final void numberToColorNameAndCheck(final SpreadsheetMetadata metadata,
                                         final int number,
                                         final SpreadsheetColorName colorName) {
        final Function<Integer, Optional<SpreadsheetColorName>> numberToColorName = metadata.numberToColorName();
        this.checkEquals(
            Optional.ofNullable(colorName),
            numberToColorName.apply(number),
            () -> number + " to color " + metadata
        );
    }

    // SpreadsheetEnvironmentContext....................................................................................

    @Test
    public void testSpreadsheetEnvironmentContextWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .spreadsheetEnvironmentContext(null)
        );
    }

    // expressionConverter........................................................................................................

    @Test
    public final void testExpressionConverterWithNullConverterSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .converter(
                    null,
                    ConverterProviders.fake(),
                    PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public final void testFindConverterWithNullConverterProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .converter(
                    SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                    null,
                    PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public final void testFindConverterWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .converter(
                    SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                    ConverterProviders.fake(),
                    null
                )
        );
    }


    @Test
    public final void testFindConverterRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> this.createObject()
                .converter(
                    SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                    ConverterProviders.fake(),
                    PROVIDER_CONTEXT
                )
        );
        checkMessage(
            thrown,
            "Metadata missing: findConverter"
        );
    }

    @Test
    public final void testFormulaConverterWithNullConverterProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .converter(
                    SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
                    null,
                    PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public final void testFormulaConverterWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .converter(
                    SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
                    ConverterProviders.fake(),
                    null
                )
        );
    }


    @Test
    public final void testFormulaConverterRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> this.createObject()
                .converter(
                    SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
                    ConverterProviders.fake(),
                    PROVIDER_CONTEXT
                )
        );
        checkMessage(
            thrown,
            "Metadata missing: formulaConverter"
        );
    }

    // DateTimeContext..................................................................................................

    @Test
    public final void testDateTimeContextWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .dateTimeContext(
                    null,
                    LocalDateTime::now,
                    LOCALE_CONTEXT
                )
        );
    }

    @Test
    public final void testDateTimeContextWithNullNowFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .dateTimeContext(
                    SpreadsheetMetadata.NO_CELL,
                    null,
                    LOCALE_CONTEXT
                )
        );
    }

    @Test
    public final void testDateTimeContextWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .dateTimeContext(
                    SpreadsheetMetadata.NO_CELL,
                    LocalDateTime::now,
                    null
                )
        );
    }

    @Test
    public final void testDateTimeContextRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> this.createObject()
                .dateTimeContext(
                    SpreadsheetMetadata.NO_CELL,
                    LocalDateTime::now,
                    LOCALE_CONTEXT
                )
        );
        checkMessage(
            thrown,
            "Metadata missing: defaultYear, locale, twoDigitYear"
        );
    }

    // DecimalNumberContext.............................................................................................

    @Test
    public final void testDecimalNumberContextWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .decimalNumberContext(
                    null,
                    LOCALE_CONTEXT
                )
        );
    }

    @Test
    public final void testDecimalNumberContextWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .decimalNumberContext(
                    SpreadsheetMetadata.NO_CELL,
                    null
                )
        );
    }

    @Test
    public final void testDecimalNumberContextRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> this.createObject()
                .decimalNumberContext(
                    SpreadsheetMetadata.NO_CELL,
                    LOCALE_CONTEXT
                )
        );
        checkMessage(
            thrown,
            "Metadata missing: decimalNumberDigitCount, locale, precision, roundingMode"
        );
    }

    // HasFormatter.....................................................................................................

    @Test
    public final void testHasFormatterRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> this.createObject()
                .spreadsheetFormatter(
                    SpreadsheetFormatterProviders.fake(),
                    PROVIDER_CONTEXT
                )
        );
        checkMessage(
            thrown,
            "Metadata missing: dateFormatter, dateTimeFormatter, errorFormatter, numberFormatter, textFormatter, timeFormatter"
        );
    }

    // HasMathContext...................................................................................................

    @Test
    public final void testHasMathContextRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> this.createObject().mathContext()
        );
        checkMessage(
            thrown,
            "Metadata missing: precision, roundingMode");
    }

    // spreadsheetValidatorContext......................................................................................

    @Test
    public final void testSpreadsheetValidatorContextWithCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .spreadsheetValidatorContext(
                    null,
                    VALIDATOR_SELECTOR_TO_VALIDATOR,
                    VALUE_N_CELL_TO_SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                    HAS_USER_DIRECTORIES,
                    INDENTATION,
                    LABEL_NAME_RESOLVER,
                    LINE_ENDING,
                    CONVERTER_PROVIDER,
                    CURRENCY_CONTEXT,
                    LOCALE_CONTEXT,
                    PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public final void testSpreadsheetValidatorContextWithValidatorSelectorToValidatorFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .spreadsheetValidatorContext(
                    SpreadsheetSelection.A1,
                    null,
                    VALUE_N_CELL_TO_SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                    HAS_USER_DIRECTORIES,
                    INDENTATION,
                    LABEL_NAME_RESOLVER,
                    LINE_ENDING,
                    CONVERTER_PROVIDER,
                    CURRENCY_CONTEXT,
                    LOCALE_CONTEXT,
                    PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public final void testSpreadsheetValidatorContextWithSpreadsheetCellReferenceToSpreadsheetExpressionEvaluationContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .spreadsheetValidatorContext(
                    SpreadsheetSelection.A1,
                    VALIDATOR_SELECTOR_TO_VALIDATOR,
                    null,
                    HAS_USER_DIRECTORIES,
                    INDENTATION,
                    LABEL_NAME_RESOLVER,
                    LINE_ENDING,
                    CONVERTER_PROVIDER,
                    CURRENCY_CONTEXT,
                    LOCALE_CONTEXT,
                    PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public final void testSpreadsheetValidatorContextWithNullHasUserDirectoriesFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .spreadsheetValidatorContext(
                    SpreadsheetSelection.A1,
                    VALIDATOR_SELECTOR_TO_VALIDATOR,
                    VALUE_N_CELL_TO_SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                    null,
                    INDENTATION,
                    LABEL_NAME_RESOLVER,
                    LINE_ENDING,
                    CONVERTER_PROVIDER,
                    CURRENCY_CONTEXT,
                    LOCALE_CONTEXT,
                    PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public final void testSpreadsheetValidatorContextWithNullIndentationFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .spreadsheetValidatorContext(
                    SpreadsheetSelection.A1,
                    VALIDATOR_SELECTOR_TO_VALIDATOR,
                    VALUE_N_CELL_TO_SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                    HAS_USER_DIRECTORIES,
                    null,
                    LABEL_NAME_RESOLVER,
                    LINE_ENDING,
                    CONVERTER_PROVIDER,
                    CURRENCY_CONTEXT,
                    LOCALE_CONTEXT,
                    PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public final void testSpreadsheetValidatorContextWithLabelNameResolverFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .spreadsheetValidatorContext(
                    SpreadsheetSelection.A1,
                    VALIDATOR_SELECTOR_TO_VALIDATOR,
                    VALUE_N_CELL_TO_SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                    HAS_USER_DIRECTORIES,
                    INDENTATION,
                    null,
                    LINE_ENDING,
                    CONVERTER_PROVIDER,
                    CURRENCY_CONTEXT,
                    LOCALE_CONTEXT,
                    PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public final void testSpreadsheetValidatorContextWithLineEndingFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .spreadsheetValidatorContext(
                    SpreadsheetSelection.A1,
                    VALIDATOR_SELECTOR_TO_VALIDATOR,
                    VALUE_N_CELL_TO_SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                    HAS_USER_DIRECTORIES,
                    INDENTATION,
                    LABEL_NAME_RESOLVER,
                    null,
                    CONVERTER_PROVIDER,
                    CURRENCY_CONTEXT,
                    LOCALE_CONTEXT,
                    PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public final void testSpreadsheetValidatorContextWithConverterProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .spreadsheetValidatorContext(
                    SpreadsheetSelection.A1,
                    VALIDATOR_SELECTOR_TO_VALIDATOR,
                    VALUE_N_CELL_TO_SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                    HAS_USER_DIRECTORIES,
                    INDENTATION,
                    LABEL_NAME_RESOLVER,
                    LINE_ENDING,
                    null,
                    CURRENCY_CONTEXT,
                    LOCALE_CONTEXT,
                    PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public final void testSpreadsheetValidatorContextWithCurrencyContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .spreadsheetValidatorContext(
                    SpreadsheetSelection.A1,
                    VALIDATOR_SELECTOR_TO_VALIDATOR,
                    VALUE_N_CELL_TO_SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                    HAS_USER_DIRECTORIES,
                    INDENTATION,
                    LABEL_NAME_RESOLVER,
                    LINE_ENDING,
                    CONVERTER_PROVIDER,
                    null,
                    LOCALE_CONTEXT,
                    PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public final void testSpreadsheetValidatorContextWithLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .spreadsheetValidatorContext(
                    SpreadsheetSelection.A1,
                    VALIDATOR_SELECTOR_TO_VALIDATOR,
                    VALUE_N_CELL_TO_SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                    HAS_USER_DIRECTORIES,
                    INDENTATION,
                    LABEL_NAME_RESOLVER,
                    LINE_ENDING,
                    CONVERTER_PROVIDER,
                    CURRENCY_CONTEXT,
                    null,
                    PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public final void testSpreadsheetValidatorContextWithProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .spreadsheetValidatorContext(
                    SpreadsheetSelection.A1,
                    VALIDATOR_SELECTOR_TO_VALIDATOR,
                    VALUE_N_CELL_TO_SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                    HAS_USER_DIRECTORIES,
                    INDENTATION,
                    LABEL_NAME_RESOLVER,
                    LINE_ENDING,
                    CONVERTER_PROVIDER,
                    CURRENCY_CONTEXT,
                    LOCALE_CONTEXT,
                    null
                )
        );
    }

    // setDefaults......................................................................................................

    @Test
    public final void testDefaultsNotNull() {
        final SpreadsheetMetadata metadata = this.createObject();
        this.checkNotEquals(
            null,
            metadata.defaults()
        );
    }

    @Test
    public final void testSetDefaultsNullFails() {
        final SpreadsheetMetadata metadata = this.createObject();
        assertThrows(
            NullPointerException.class,
            () -> metadata.setDefaults(null)
        );
    }

    @Test
    public final void testSetDefaultsSame() {
        final SpreadsheetMetadata metadata = this.createObject();
        assertSame(
            metadata,
            metadata.setDefaults(metadata.defaults())
        );
    }

    @Test
    public final void testSetDefaultsEmpty() {
        final SpreadsheetMetadata metadata = this.createObject();
        assertSame(
            metadata,
            metadata.setDefaults(SpreadsheetMetadata.EMPTY)
        );
    }

    @Test
    public final void testSetDefaultsNotEmpty() {
        final SpreadsheetMetadata metadata = this.createObject();
        final SpreadsheetMetadata notEmpty = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);

        final SpreadsheetMetadata withDefaults = metadata.setDefaults(notEmpty);
        assertNotSame(metadata, withDefaults);
        this.checkDefaults(withDefaults, notEmpty);
    }

    @Test
    public final void testSetDefaultsIncludesAuditFails() {
        this.setDefaultsWithInvalidFails(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            AuditInfo.with(
                EmailAddress.parse("creator@example.com"),
                LocalDateTime.MIN,
                EmailAddress.parse("modified@example.com"),
                LocalDateTime.MAX
            )
        );
    }

    @Test
    public final void testSetDefaultsIncludesSpreadsheetIdFails() {
        this.setDefaultsWithInvalidFails(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            SpreadsheetId.with(123)
        );
    }

    private <TT> void setDefaultsWithInvalidFails(final SpreadsheetMetadataPropertyName<TT> property,
                                                  final TT value) {
        final SpreadsheetMetadata metadata = this.createObject();
        final SpreadsheetMetadata defaults = SpreadsheetMetadata.EMPTY.set(property, value);

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> metadata.setDefaults(defaults)
        );
        this.checkEquals(
            "Defaults includes invalid default values: " +
                property,
            thrown.getMessage(),
            () -> "defaults with " + defaults
        );
    }

    final void checkDefaults(final SpreadsheetMetadata metadata,
                             final SpreadsheetMetadata defaults) {
        if (null == defaults || defaults == SpreadsheetMetadata.EMPTY) {
            assertSame(null, metadata.defaults, "defaults");
        } else {
            assertSame(defaults, metadata.defaults, "defaults");
            this.checkEquals(
                false,
                metadata.defaults.isEmpty(),
                () -> "defaults should not be an empty SpreadsheetMetadata, " + metadata.defaults
            );
        }
    }

    // json.............................................................................................................

    @Test
    public final void testRoundtripWithDefaults() {
        final SpreadsheetMetadata metadata = this.createObject();
        final SpreadsheetMetadata notEmptyDefaults = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);
        final SpreadsheetMetadata withDefaults = metadata.setDefaults(notEmptyDefaults);

        this.marshallRoundTripTwiceAndCheck(withDefaults);
    }

    @Override
    public final SpreadsheetMetadata unmarshall(final JsonNode from,
                                                final JsonNodeUnmarshallContext context) {
        return SpreadsheetMetadata.unmarshall(from, context);
    }

    @Override
    public final SpreadsheetMetadata createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // missingRequiredProperties........................................................................................

    @Test
    public final void testMissingRequiredPropertiesReadOnly() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createObject()
                .missingRequiredProperties()
                .add(SpreadsheetMetadataPropertyName.VIEWPORT_HOME)
        );
    }

    final void missingRequiredPropertiesAndCheck(final SpreadsheetMetadata metadata,
                                                 final SpreadsheetMetadataPropertyName<?>... missing) {
        this.checkEquals(
            Sets.of(missing),
            metadata.missingRequiredProperties(),
            () -> "" + metadata
        );
    }

    // HateosResourceTesting.............................................................................................

    @Override
    public final SpreadsheetMetadata createHateosResource() {
        return this.createObject();
    }

    // class...........................................................................................................

    @Override
    public final Class<SpreadsheetMetadata> type() {
        return Cast.to(this.metadataType());
    }

    abstract Class<T> metadataType();

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
