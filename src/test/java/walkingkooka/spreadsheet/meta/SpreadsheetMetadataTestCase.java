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
import walkingkooka.convert.provider.ConverterProviders;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.tree.text.WordWrap;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetMetadataTestCase<T extends SpreadsheetMetadata> implements CanBeEmptyTesting<SpreadsheetMetadata>,
        ClassTesting2<SpreadsheetMetadata>,
        ConverterTesting,
        HashCodeEqualsDefinedTesting2<SpreadsheetMetadata>,
        JsonNodeMarshallingTesting<SpreadsheetMetadata>,
        HateosResourceTesting<SpreadsheetMetadata, SpreadsheetId>,
        ThrowableTesting,
        ToStringTesting<SpreadsheetMetadata>,
        TreePrintableTesting {

    final static SpreadsheetLabelNameResolver LABEL_NAME_RESOLVER = SpreadsheetLabelNameResolvers.fake();

    SpreadsheetMetadataTestCase() {
        super();
    }

    // isEmpty...........................................................................................................

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
        assertThrows(NullPointerException.class, () -> this.createObject().get(null));
    }

    @Test
    public final void testGetUnknown() {
        this.getAndCheck(
                this.createObject(),
                SpreadsheetMetadataPropertyName.MODIFIED_BY
        );
    }

    @Test
    public final void testGetUnknownDefaultsToDefault() {
        final String value = "!!!";

        final SpreadsheetMetadata metadata = this.createObject();

        final SpreadsheetMetadataPropertyName<String> unknown = SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL;
        this.getAndCheck(
                metadata,
                unknown
        );

        this.getAndCheck(
                metadata.setDefaults(
                        SpreadsheetMetadata.EMPTY.set(unknown, value)
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
        final SpreadsheetMetadataPropertyName<String> propertyName = SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL;

        final SpreadsheetMetadataPropertyValueException thrown = assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> this.createObject().getOrFail(propertyName));

        this.checkMessage(thrown, "Required property missing, but got null for " + CharSequences.quote(propertyName.value()));
        this.checkEquals(propertyName, thrown.name(), "property name");
        this.checkEquals(null, thrown.value(), "property value");
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
        assertThrows(NullPointerException.class, () -> this.createObject().set(null, "value"));
    }

    @Test
    public final void testSetNullPropertyValueFails() {
        assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> this.createObject().set(SpreadsheetMetadataPropertyName.CREATOR, null));
    }

    @Test
    public final void testSetInvalidPropertyValueFails() {
        assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> {
            final SpreadsheetMetadataPropertyName<?> propertyName = SpreadsheetMetadataPropertyName.CREATOR;
            this.createObject().set(propertyName, Cast.to("invalid-expected-EmailAddress"));
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
        assertSame(metadata, metadata.remove(SpreadsheetMetadataPropertyName.MODIFIED_BY));
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
        assertThrows(NullPointerException.class, () -> this.createObject().getEffectiveStyleProperty(null));
    }

    @Test
    public final void testGetEffectiveStylePropertyAbsent() {
        this.getEffectiveStylePropertyAndCheck(this.createObject(), TextStylePropertyName.WORD_WRAP, null);
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

    // getEffectiveStyleOrFail.................................................................................................

    @Test
    public final void testGetEffectiveStylePropertyOrFailNullFails() {
        assertThrows(NullPointerException.class, () -> this.createObject().getEffectiveStylePropertyOrFail(null));
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

        this.getEffectiveStylePropertyOrFailAndCheck(this.createObject()
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
        this.checkEquals(Optional.ofNullable(color),
                nameToColor.apply(name),
                () -> name + " to color " + metadata);
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
        this.checkEquals(Optional.ofNullable(color),
                numberToColor.apply(number),
                () -> number + " to color " + metadata);
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

    // EnvironmentContext...............................................................................................

    @Test
    public void testEnvironmentContextCached() {
        final SpreadsheetMetadata metadata = this.createObject();

        assertSame(
                metadata.environmentContext(),
                metadata.environmentContext(),
                () -> "EnvironmentContext not cached, new instance created each time."
        );
    }

    // expressionConverter........................................................................................................

    @Test
    public final void testExpressionConverterWithNullConverterProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createObject()
                        .expressionConverter(
                                null
                        )
        );
    }


    @Test
    public final void testExpressionConverterRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> this.createObject()
                        .expressionConverter(
                                ConverterProviders.fake()
                        )
        );
        checkMessage(
                thrown,
                "Required properties \"expression-converter\" missing."
        );
    }

    // HasDateTimeContext...............................................................................................

    @Test
    public final void testHasDateTimeContextRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> this.createObject().dateTimeContext(LocalDateTime::now)
        );
        checkMessage(thrown,
                "Required properties \"default-year\", \"locale\", \"two-digit-year\" missing.");
    }

    // HasDecimalNumberContext..........................................................................................

    @Test
    public final void testHasDecimalNumberContextRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> this.createObject().decimalNumberContext());
        checkMessage(thrown,
                "Required properties \"currency-symbol\", \"decimal-separator\", \"exponent-symbol\", \"group-separator\", \"locale\", \"negative-sign\", \"percentage-symbol\", \"positive-sign\", \"precision\", \"rounding-mode\" missing.");
    }

    // HasFormatter.....................................................................................................

    @Test
    public final void testHasFormatterRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> this.createObject()
                        .formatter(SpreadsheetFormatterProviders.fake())
        );
        checkMessage(
                thrown,
                "Required properties \"date-formatter\", \"date-time-formatter\", \"number-formatter\", \"text-formatter\", \"time-formatter\" missing."
        );
    }

    // HasMathContext...................................................................................................

    @Test
    public final void testHasMathContextRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> this.createObject().mathContext());
        checkMessage(thrown,
                "Required properties \"precision\", \"rounding-mode\" missing.");
    }

    // setDefaults......................................................................................................

    @Test
    public final void testDefaultsNotNull() {
        final SpreadsheetMetadata metadata = this.createObject();
        this.checkNotEquals(null, metadata.defaults());
    }

    @Test
    public final void testSetDefaultsNullFails() {
        final SpreadsheetMetadata metadata = this.createObject();
        assertThrows(NullPointerException.class, () -> metadata.setDefaults(null));
    }

    @Test
    public final void testSetDefaultsSame() {
        final SpreadsheetMetadata metadata = this.createObject();
        assertSame(metadata, metadata.setDefaults(metadata.defaults()));
    }

    @Test
    public final void testSetDefaultsEmpty() {
        final SpreadsheetMetadata metadata = this.createObject();
        assertSame(metadata, metadata.setDefaults(SpreadsheetMetadata.EMPTY));
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
    public final void testSetDefaultsIncludesCreatorFails() {
        this.setDefaultsWithInvalidFails(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("creator@example.com"));
    }

    @Test
    public final void testSetDefaultsIncludesCreateDateTimeFails() {
        this.setDefaultsWithInvalidFails(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.now());
    }

    @Test
    public final void testSetDefaultsIncludesModifiedByFails() {
        this.setDefaultsWithInvalidFails(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("modified@example.com"));
    }

    @Test
    public final void testSetDefaultsIncludesModifiedDateTimeFails() {
        this.setDefaultsWithInvalidFails(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.now());
    }

    @Test
    public final void testSetDefaultsIncludesSpreadsheetIdFails() {
        this.setDefaultsWithInvalidFails(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(123));
    }

    private <TT> void setDefaultsWithInvalidFails(final SpreadsheetMetadataPropertyName<TT> property,
                                                  final TT value) {
        final SpreadsheetMetadata metadata = this.createObject();
        final SpreadsheetMetadata defaults = SpreadsheetMetadata.EMPTY.set(property, value);

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> metadata.setDefaults(defaults));
        this.checkEquals("Defaults includes invalid default values: " + property, thrown.getMessage(), () -> "defaults with " + defaults);
    }

    @Test
    public final void testSetDefaultsSeveralInvalidsFails() {
        final SpreadsheetMetadata metadata = this.createObject();
        final SpreadsheetMetadata defaults = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("creator@example.com"))
                .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.now());

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> metadata.setDefaults(defaults));
        this.checkEquals("Defaults includes invalid default values: " + SpreadsheetMetadataPropertyName.CREATE_DATE_TIME + ", " + SpreadsheetMetadataPropertyName.CREATOR,
                thrown.getMessage(),
                () -> "defaults with " + defaults);
    }

    @Test
    public final void testSetDefaultWithDefaultFails() {
        final SpreadsheetMetadata metadata = this.createObject();

        final SpreadsheetMetadata defaults = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("creator@example.com"))
                .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.now())
                .setDefaults(SpreadsheetMetadata.EMPTY
                        .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "$UAD")
                );

        assertThrows(IllegalArgumentException.class, () -> metadata.setDefaults(defaults));
    }

    final void checkDefaults(final SpreadsheetMetadata metadata,
                             final SpreadsheetMetadata defaults) {
        if (null == defaults || defaults == SpreadsheetMetadata.EMPTY) {
            assertSame(null, metadata.defaults, "defaults");
        } else {
            assertSame(defaults, metadata.defaults, "defaults");
            this.checkEquals(false, metadata.defaults.isEmpty(), () -> "defaults should not be an empty SpreadsheetMetadata, " + metadata.defaults);
        }
    }

    @Test
    public final void testRoundtripWithDefaults() {
        final SpreadsheetMetadata metadata = this.createObject();
        final SpreadsheetMetadata notEmptyDefaults = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);
        final SpreadsheetMetadata withDefaults = metadata.setDefaults(notEmptyDefaults);

        this.marshallRoundTripTwiceAndCheck(withDefaults);
    }

    // missingRequiredProperties........................................................................................

    @Test
    public final void testMissingRequiredPropertiesReadOnly() {
        assertThrows(UnsupportedOperationException.class,
                () -> this.createObject().missingRequiredProperties().add(SpreadsheetMetadataPropertyName.VIEWPORT));
    }

    final void missingRequiredPropertiesAndCheck(final SpreadsheetMetadata metadata,
                                                 final SpreadsheetMetadataPropertyName<?>... missing) {
        this.checkEquals(Sets.of(missing),
                metadata.missingRequiredProperties(),
                () -> "" + metadata);
    }

    // ClassTesting.....................................................................................................

    @Override
    public final Class<SpreadsheetMetadata> type() {
        return Cast.to(this.metadataType());
    }

    abstract Class<T> metadataType();

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public final SpreadsheetMetadata unmarshall(final JsonNode from,
                                                final JsonNodeUnmarshallContext context) {
        return SpreadsheetMetadata.unmarshall(from, context);
    }

    @Override
    public final SpreadsheetMetadata createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // HateosResourceTesting.............................................................................................

    @Override
    public final SpreadsheetMetadata createHateosResource() {
        return this.createObject();
    }

    // CanBeEmptyTesting................................................................................................

    @Override
    public final SpreadsheetMetadata createCanBeEmpty() {
        return this.createObject();
    }
}
