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
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.validation.provider.ValidatorSelector;

import java.math.RoundingMode;
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
        final SpreadsheetMetadataPropertyName<AuditInfo> propertyName = SpreadsheetMetadataPropertyName.AUDIT_INFO;
        final AuditInfo auditInfo = AuditInfo.with(
            EmailAddress.parse("created@example.com"),
            LocalDateTime.MIN,
            EmailAddress.parse("modified@example.com"),
            LocalDateTime.MAX
        );

        this.setAndCheck(
            SpreadsheetMetadata.EMPTY,
            propertyName,
            auditInfo,
            "{\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"created@example.com\",\n" +
                "    \"createdTimestamp\": \"-999999999-01-01T00:00\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"+999999999-12-31T23:59:59.999999999\"\n" +
                "  }\n" +
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
        this.missingRequiredPropertiesAndCheck(
            SpreadsheetMetadata.EMPTY,
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            SpreadsheetMetadataPropertyName.LOCALE
        );
    }

    @Test
    public void testMissingPropertiesIgnoresDefaults() {
        final SpreadsheetMetadata defaults = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP);

        this.missingRequiredPropertiesAndCheck(
            SpreadsheetMetadata.EMPTY.setDefaults(defaults),
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            SpreadsheetMetadataPropertyName.LOCALE
        );
    }

    // HasExpressionNumberContext.......................................................................................

    @Test
    public void testExpressionNumberContextAllRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetMetadata.EMPTY.expressionNumberContext(
                SpreadsheetMetadata.NO_CELL,
                LOCALE_CONTEXT
            )
        );
        this.checkEquals(
            "Metadata missing: decimalNumberDigitCount, expressionNumberKind, locale, precision, roundingMode",
            thrown.getMessage(),
            "message"
        );
    }

    // SpreadsheetEnvironmentContext....................................................................................

    @Test
    public void testSpreadsheetEnvironmentContext() {
        this.environmentValueAndCheck(
            SpreadsheetMetadata.EMPTY.spreadsheetEnvironmentContext(
                EnvironmentContexts.empty(
                    LineEnding.NL,
                    LOCALE_CONTEXT.locale(),
                    LocalDateTime::now,
                    EnvironmentContext.ANONYMOUS
                )
            ),
            EnvironmentValueName.with("metadata." + SpreadsheetMetadataPropertyName.AUDIT_INFO)
        );
    }

    // HasJsonNodeUnmarshallContext.....................................................................................

    @Test
    public void testUnmarshallContextAllRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            SpreadsheetMetadata.EMPTY::jsonNodeUnmarshallContext
        );
        this.checkEquals(
            "Metadata missing: expressionNumberKind, precision, roundingMode",
            thrown.getMessage(),
            "message"
        );
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
            "Metadata missing: dateParser, dateTimeParser, numberParser, timeParser",
            thrown.getMessage(),
            "message"
        );
    }

    // HasParserContext.....................................................................................

    @Test
    public void testSpreadsheetParserContextAllRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetMetadata.EMPTY.spreadsheetParserContext(
                SpreadsheetMetadata.NO_CELL,
                LOCALE_CONTEXT,
                LocalDateTime::now
            )
        );
        this.checkEquals(
            "Metadata missing: decimalNumberDigitCount, defaultYear, expressionNumberKind, locale, precision, roundingMode, twoDigitYear, valueSeparator",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testSpreadsheetParserContextAllRequiredPropertiesAbsentFails2() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetMetadata.EMPTY
                .spreadsheetParserContext(
                    SpreadsheetMetadata.NO_CELL,
                    LOCALE_CONTEXT,
                    LocalDateTime::now
                )
        );
        this.checkEquals(
            "Metadata missing: decimalNumberDigitCount, defaultYear, expressionNumberKind, locale, precision, roundingMode, twoDigitYear, valueSeparator",
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
                (final ValidatorSelector validatorSelector) -> {
                    throw new UnsupportedOperationException();
                },
                (final Object value,
                 final SpreadsheetExpressionReference cellOrLabel) -> {
                    throw new UnsupportedOperationException();
                },
                LABEL_NAME_RESOLVER,
                CONVERTER_PROVIDER,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
        this.checkEquals(
            "Metadata missing: dateTimeOffset, decimalNumberDigitCount, defaultYear, expressionNumberKind, locale, precision, roundingMode, twoDigitYear, validationConverter, valueSeparator",
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
