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
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.text.TextStyle;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

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
        final SpreadsheetMetadataPropertyName<EmailAddress> propertyName = SpreadsheetMetadataPropertyName.CREATOR;
        final EmailAddress email = EmailAddress.parse("user@example.com");

        this.setAndCheck(
                SpreadsheetMetadata.EMPTY,
                propertyName,
                email,
                "{\n" +
                        "  \"creator\": \"user@example.com\"\n" +
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
                SpreadsheetMetadataPropertyName.CREATOR,
                SpreadsheetMetadataPropertyName.CREATE_DATE_TIME,
                SpreadsheetMetadataPropertyName.LOCALE,
                SpreadsheetMetadataPropertyName.MODIFIED_BY,
                SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME);
    }

    @Test
    public void testMissingPropertiesIgnoresDefaults() {
        final SpreadsheetMetadata defaults = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "$");

        this.missingRequiredPropertiesAndCheck(SpreadsheetMetadata.EMPTY.setDefaults(defaults),
                SpreadsheetMetadataPropertyName.CREATOR,
                SpreadsheetMetadataPropertyName.CREATE_DATE_TIME,
                SpreadsheetMetadataPropertyName.LOCALE,
                SpreadsheetMetadataPropertyName.MODIFIED_BY,
                SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME);
    }

    // EnvironmentContext...............................................................................................

    @Test
    public void testEnvironmentContext() {
        final EnvironmentContext context = SpreadsheetMetadata.EMPTY.environmentContext();
        this.checkEquals(
                Optional.empty(),
                context.environmentValue(
                        EnvironmentValueName.with("metadata." + SpreadsheetMetadataPropertyName.CREATOR)
                )
        );
    }

    // HasExpressionNumberContext.......................................................................................

    @Test
    public void testExpressionNumberContextAllRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                SpreadsheetMetadata.EMPTY::expressionNumberContext
        );
        this.checkEquals("Required properties \"expression-number-kind\", \"precision\", \"rounding-mode\" missing.",
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
        this.checkEquals("Required properties \"expression-number-kind\", \"precision\", \"rounding-mode\" missing.",
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
                "Required properties \"date-parser\", \"date-time-parser\", \"number-parser\", \"time-parser\" missing.",
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
                "Required properties \"currency-symbol\", \"decimal-separator\", \"exponent-symbol\", \"expression-number-kind\", \"group-separator\", \"locale\", \"negative-sign\", \"percentage-symbol\", \"positive-sign\", \"precision\", \"rounding-mode\", \"two-digit-year\", \"value-separator\" missing.",
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
                "Required properties \"decimal-separator\", \"exponent-symbol\", \"expression-number-kind\", \"group-separator\", \"locale\", \"negative-sign\", \"percentage-symbol\", \"positive-sign\", \"precision\", \"rounding-mode\", \"two-digit-year\", \"value-separator\" missing.",
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
