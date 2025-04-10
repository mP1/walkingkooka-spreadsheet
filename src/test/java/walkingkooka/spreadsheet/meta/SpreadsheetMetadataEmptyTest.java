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
import walkingkooka.Cast;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.text.TextStyle;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataEmptyTest extends SpreadsheetMetadataTestCase<SpreadsheetMetadataEmpty> {

    @Test
    public void testCreated() {
        final SpreadsheetMetadata empty = SpreadsheetMetadata.EMPTY;
        this.checkDefaults(empty, null);
    }

    @Test
    public void testValue() {
        assertSame(SpreadsheetMetadata.EMPTY.value(), SpreadsheetMetadata.EMPTY.value());
    }

    // effectiveStyle...................................................................................................

    @Test
    public void testEffectiveStyle() {
        this.effectiveStyleAndCheck(
                SpreadsheetMetadata.EMPTY,
                TextStyle.EMPTY
        );
    }

    // set..............................................................................................................

    @Test
    public void testSet() {
        final SpreadsheetMetadataPropertyName<EmailAddress> propertyName = SpreadsheetMetadataPropertyName.CREATED_BY;
        final EmailAddress email = EmailAddress.parse("user@example.com");

        this.setAndCheck(
                SpreadsheetMetadata.EMPTY,
                propertyName,
                email,
                "{\n" +
                        "  \"created-by\": \"user@example.com\"\n" +
                        "}"
        );
    }

    // HateosResourceTesting............................................................................................

    @Test
    public void testHateosLinkIdMissingIdFails() {
        assertThrows(IllegalStateException.class, () -> SpreadsheetMetadataEmpty.instance().hateosLinkId());
    }

    // SpreadsheetMetadataVisitor.......................................................................................

    @Test
    public void testAccept() {
        SpreadsheetMetadata.EMPTY.accept(new SpreadsheetMetadataVisitor() {
        });
    }

    // missingRequiredProperties.........................................................................................

    @Test
    public void testMissingProperties() {
        this.missingRequiredPropertiesAndCheck(SpreadsheetMetadata.EMPTY,
                SpreadsheetMetadataPropertyName.CREATED_BY,
                SpreadsheetMetadataPropertyName.CREATED_TIMESTAMP,
                SpreadsheetMetadataPropertyName.LOCALE,
                SpreadsheetMetadataPropertyName.MODIFIED_BY,
                SpreadsheetMetadataPropertyName.MODIFIED_TIMESTAMP);
    }

    @Test
    public void testMissingPropertiesIgnoresDefaults() {
        final SpreadsheetMetadata defaults = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "$");

        this.missingRequiredPropertiesAndCheck(SpreadsheetMetadata.EMPTY.setDefaults(defaults),
                SpreadsheetMetadataPropertyName.CREATED_BY,
                SpreadsheetMetadataPropertyName.CREATED_TIMESTAMP,
                SpreadsheetMetadataPropertyName.LOCALE,
                SpreadsheetMetadataPropertyName.MODIFIED_BY,
                SpreadsheetMetadataPropertyName.MODIFIED_TIMESTAMP);
    }

    // EnvironmentContext...............................................................................................

    @Test
    public void testEnvironmentContext() {
        this.environmentValueAndCheck(
                SpreadsheetMetadata.EMPTY.environmentContext(
                        EnvironmentContexts.empty(
                                LocalDateTime::now,
                                EnvironmentContext.ANONYMOUS
                        )
                ),
                EnvironmentValueName.with("metadata." + SpreadsheetMetadataPropertyName.CREATED_BY)
        );
    }

    // HasExpressionNumberContext.......................................................................................

    @Test
    public void testExpressionNumberContextAllRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                SpreadsheetMetadata.EMPTY::expressionNumberContext
        );
        this.checkEquals("Metadata missing: expression-number-kind, precision, rounding-mode",
                thrown.getMessage(),
                "message");
    }

    // HasJsonNodeUnmarshallContext.....................................................................................

    @Test
    public void testUnmarshallContextAllRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                SpreadsheetMetadata.EMPTY::jsonNodeUnmarshallContext
        );
        this.checkEquals("Metadata missing: expression-number-kind, precision, rounding-mode",
                thrown.getMessage(),
                "message");
    }

    // HasParser........................................................................................................

    @Test
    public void testSpreadsheetParserAllRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> SpreadsheetMetadata.EMPTY.spreadsheetParser(
                        SpreadsheetParserProviders.fake(),
                        PROVIDER_CONTEXT
                )
        );
        this.checkEquals(
                "Metadata missing: date-parser, date-time-parser, number-parser, time-parser",
                thrown.getMessage(),
                "message"
        );
    }

    // HasParserContext.....................................................................................

    @Test
    public void testSpreadsheetParserContextAllRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> SpreadsheetMetadata.EMPTY.spreadsheetParserContext(LocalDateTime::now)
        );
        this.checkEquals(
                "Metadata missing: currency-symbol, decimal-separator, exponent-symbol, expression-number-kind, group-separator, locale, negative-sign, percentage-symbol, positive-sign, precision, rounding-mode, two-digit-year, value-separator",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testSpreadsheetParserContextAllRequiredPropertiesAbsentFails2() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> SpreadsheetMetadata.EMPTY
                        .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "AUD")
                        .spreadsheetParserContext(LocalDateTime::now)
        );
        this.checkEquals(
                "Metadata missing: decimal-separator, exponent-symbol, expression-number-kind, group-separator, locale, negative-sign, percentage-symbol, positive-sign, precision, rounding-mode, two-digit-year, value-separator",
                thrown.getMessage(),
                "message"
        );
    }

    // SpreadsheetValidatorContext......................................................................................

    @Test
    public void testSpreadsheetValidatorContextMissingPropertiesFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> SpreadsheetMetadata.EMPTY.spreadsheetValidatorContext(
                        SpreadsheetSelection.A1,
                        (final Object value,
                         final SpreadsheetCellReference cell) -> {
                            throw new UnsupportedOperationException();
                        },
                        LABEL_NAME_RESOLVER,
                        CONVERTER_PROVIDER,
                        PROVIDER_CONTEXT
                )
        );
        this.checkEquals(
                "Metadata missing: formula-converter",
                thrown.getMessage(),
                "message"
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadata.EMPTY, "{}");
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Test
    public void testFromEmptyJsonObject() {
        assertSame(SpreadsheetMetadata.EMPTY, SpreadsheetMetadata.unmarshall(JsonNode.object(), this.unmarshallContext()));
    }

    @Test
    public void testMarshallWithDefaults() {
        final SpreadsheetMetadata defaultNotEmpty = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);
        final JsonNodeMarshallContext context = JsonNodeMarshallContexts.basic();

        this.checkEquals(JsonNode.object()
                        .set(SpreadsheetMetadata.DEFAULTS, context.marshall(defaultNotEmpty)),
                context.marshall(SpreadsheetMetadata.EMPTY.setDefaults(defaultNotEmpty)));
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                SpreadsheetMetadata.EMPTY,
                ""
        );
    }

    // helper...........................................................................................................

    @Override
    public SpreadsheetMetadataEmpty createObject() {
        return Cast.to(SpreadsheetMetadata.EMPTY);
    }

    @Override
    Class<SpreadsheetMetadataEmpty> metadataType() {
        return SpreadsheetMetadataEmpty.class;
    }
}
