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
import walkingkooka.ToStringTesting;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.locale.LocaleContext;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.text.TextStylePropertyName;

import java.math.MathContext;
import java.util.Currency;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetMetadataPropertyNameTestCase<N extends SpreadsheetMetadataPropertyName<V>, V> implements ClassTesting<N>,
    TypeNameTesting<N>,
    ToStringTesting<N> {

    final static SpreadsheetLabelNameResolver LABEL_NAME_RESOLVER = SpreadsheetLabelNameResolvers.fake();

    SpreadsheetMetadataPropertyNameTestCase() {
        super();
    }

    @Test
    public final void testUrlFragment() {
        final N name = this.createName();

        this.checkEquals(
            UrlFragment.parse(name.value()),
            name.urlFragment(),
            () -> name + " urlFragment"
        );
    }

    @Test
    public final void testTextStylePropertyNameClashFree() {
        final String property = this.createName().value();

        this.checkEquals(
            false,
            TextStylePropertyName.VALUES
                .stream()
                .anyMatch(p -> p.value().equals(property))
        );
    }

    @Test
    public final void testSpreadsheetMetadataJsonRoundtrip() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(this.createName(), this.propertyValue());

        final JsonNode node = JsonNodeMarshallContexts.basic()
            .marshall(metadata);
        this.checkEquals(
            metadata,
            JsonNodeUnmarshallContexts.basic(
                (String cc) -> Optional.ofNullable(
                    Currency.getInstance(cc)
                ),
                ExpressionNumberKind.DOUBLE,
                MathContext.DECIMAL32
            ).unmarshall(node, SpreadsheetMetadata.class)
        );
    }

    @Test
    public final void testCheckValueWithNullFails() {
        this.checkValueFails(
            null,
            "Metadata " + this.createName() + "=null, Missing value"
        );
    }

    @Test
    public final void testCheckValueWithInvalidFails() {
        this.checkValueFails(
            this,
            "Metadata " + this.createName() + "=" + CharSequences.quoteIfChars(this) + ", Expected " + this.propertyValueType());
    }

    @Test
    public final void testCheckValueWithInvalidValueFails2() {
        final StringBuilder value = new StringBuilder("123abc");

        // sample:
        // Metadata decimal-separator="123abc", Expected Character symbol, not control character, whitespace, letter or digit
        this.checkValueFails(
            value,
            "Metadata " + this.createName() + "=" + CharSequences.quoteIfChars(value) + ", Expected " + this.propertyValueType()
        );
    }

    @Test
    public final void testCheckValue() {
        this.checkValue(this.propertyValue());
    }

    final void checkValue(final Object value) {
        this.createName().checkValue(value);
    }

    final void checkValueFails(final Object value, final String message) {
        final SpreadsheetMetadataPropertyName<?> propertyName = this.createName();

        final SpreadsheetMetadataPropertyValueException thrown = assertThrows(
            SpreadsheetMetadataPropertyValueException.class,
            () -> propertyName.checkValue(value)
        );
        this.checkSpreadsheetMetadataPropertyValueException(thrown, message, propertyName, value);

        final SpreadsheetMetadataPropertyValueException thrown2 = assertThrows(
            SpreadsheetMetadataPropertyValueException.class,
            () -> propertyName.checkValue(value)
        );
        this.checkSpreadsheetMetadataPropertyValueException(thrown2, message, propertyName, value);
    }

    private void checkSpreadsheetMetadataPropertyValueException(final SpreadsheetMetadataPropertyValueException thrown,
                                                                final String message,
                                                                final SpreadsheetMetadataPropertyName<?> propertyName,
                                                                final Object value) {
        if (null != message) {
            this.checkEquals(
                message,
                thrown.getMessage(),
                "message"
            );
        }
        this.checkEquals(
            propertyName,
            thrown.name(),
            "propertyName"
        );
        this.checkEquals(
            value,
            thrown.value(),
            "value"
        );
    }

    // extractLocaleAwareValue...............................................................................................

    final void extractLocaleValueAwareAndCheck(final LocaleContext context,
                                               final V value) {
        final N propertyName = this.createName();
        this.checkEquals(
            Optional.ofNullable(value),
            propertyName.extractLocaleAwareValue(context),
            propertyName + " extractLocaleAwareValue for locale " + context.locale()
        );
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Test
    public final void testParseUrlFragmentSaveValue() {
        final SpreadsheetMetadataPropertyName<V> propertyName = this.createName();

        if (false == propertyName instanceof SpreadsheetMetadataPropertyNameAuditInfo &&
            false == propertyName instanceof SpreadsheetMetadataPropertyNameSpreadsheetId &&
            false == propertyName instanceof SpreadsheetMetadataPropertyNameStyle &&
            false == propertyName instanceof SpreadsheetMetadataPropertyNameViewportHome &&
            false == propertyName instanceof SpreadsheetMetadataPropertyNameViewportSelection) {
            final String text;

            final V value = this.propertyValue();
            if (value instanceof HasUrlFragment) {
                final HasUrlFragment has = (HasUrlFragment) value;
                text = has.urlFragment().value();
            } else {
                if (value instanceof HasText) {
                    final HasText has = (HasText) value;
                    text = has.text();
                } else {
                    text = String.valueOf(value);
                }
            }

            this.parseUrlFragmentSaveValueAndCheck(
                propertyName,
                text,
                value
            );
        }
    }

    final void parseUrlFragmentSaveValueAndCheck(final SpreadsheetMetadataPropertyName<V> propertyName,
                                                 final String urlFragment,
                                                 final V value) {
        this.checkEquals(
            value,
            propertyName.parseUrlFragmentSaveValue(urlFragment)
        );
    }

    // isConverterSelector...................................................................................

    @Test
    public final void testIsConverterSelector() {
        final N propertyName = this.createName();

        this.checkEquals(
            this.propertyValue() instanceof ConverterSelector,
            propertyName.isConverterSelector(),
            propertyName::toString
        );
    }

    // isSpreadsheetFormatterSelector...................................................................................

    @Test
    public final void testIsSpreadsheetFormatterSelector() {
        final N propertyName = this.createName();

        this.checkEquals(
            this.propertyValue() instanceof SpreadsheetFormatterSelector,
            propertyName.isSpreadsheetFormatterSelector(),
            propertyName::toString
        );
    }


    // isSpreadsheetParserSelector...................................................................................

    @Test
    public final void testIsSpreadsheetParserSelector() {
        final N propertyName = this.createName();

        this.checkEquals(
            this.propertyValue() instanceof SpreadsheetParserSelector,
            propertyName.isSpreadsheetParserSelector(),
            propertyName::toString
        );
    }

    // NameTesting......................................................................................................

    abstract N createName();

    abstract V propertyValue();

    abstract String propertyValueType();

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetMetadataPropertyName.class.getSimpleName();
    }

    @Override
    public final String typeNameSuffix() {
        return "";
    }
}
