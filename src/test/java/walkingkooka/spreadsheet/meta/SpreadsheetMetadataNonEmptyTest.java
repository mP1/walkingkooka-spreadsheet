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
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterAliasSet;
import walkingkooka.convert.provider.ConverterProviders;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextTesting;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.datetime.HasNow;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextTesting;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.PluginNameSet;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.color.SpreadsheetColors;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.provider.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetCellQuery;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterSelector;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterTesting;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.DateSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DateTimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.NumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.TimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterAliasSet;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterSelector;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.text.CharSequences;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.convert.ExpressionNumberConverters;
import walkingkooka.tree.expression.convert.FakeExpressionNumberConverterContext;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.FontFamily;
import walkingkooka.tree.text.FontSize;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.tree.text.WordWrap;
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.form.provider.FormHandlerSelector;
import walkingkooka.validation.provider.ValidatorAliasSet;
import walkingkooka.validation.provider.ValidatorProviders;
import walkingkooka.validation.provider.ValidatorSelector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataNonEmptyTest extends SpreadsheetMetadataTestCase<SpreadsheetMetadataNonEmpty>
    implements ConverterTesting,
    DateTimeContextTesting,
    DecimalNumberContextTesting,
    SpreadsheetFormatterTesting {
    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    private final static int DEFAULT_YEAR = 1900;

    private final static int DECIMAL_NUMBER_DIGIT_COUNT = DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT - 1;

    private final static Locale LOCALE = Locale.ENGLISH;

    private final static HasNow NOW = LocalDateTime::now;

    private final static SpreadsheetFormatterProvider SPREADSHEET_FORMATTER_PROVIDER = SpreadsheetFormatterProviders.spreadsheetFormatters();

    private final static SpreadsheetParserProvider SPREADSHEET_PARSER_PROVIDER = SpreadsheetParserProviders.spreadsheetParsePattern(
        SPREADSHEET_FORMATTER_PROVIDER
    );

    @Test
    public void testId() {
        final SpreadsheetId id = SpreadsheetId.with(123);
        final SpreadsheetMetadata metadata = this.createSpreadsheetMetadata(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, id));
        this.checkEquals(Optional.of(id), metadata.id(), "id");
    }

    @Test
    public void testName() {
        final SpreadsheetName name = SpreadsheetName.with("Title123");
        final SpreadsheetMetadata metadata = this.createSpreadsheetMetadata(
            Maps.of(
                SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                name
            )
        );
        this.checkEquals(
            Optional.of(name),
            metadata.name(),
            "name"
        );
    }

    // get..............................................................................................................

    @Test
    public void testGet() {
        this.getAndCheck(this.createSpreadsheetMetadata(),
            this.property1(),
            this.value1());
    }

    @Test
    public void testGet2() {
        this.getAndCheck(this.createSpreadsheetMetadata(),
            this.property2(),
            this.value2());
    }

    @Test
    public void testGetActualNotDefault() {
        final SpreadsheetMetadataPropertyName<AuditInfo> propertyName = SpreadsheetMetadataPropertyName.AUDIT_INFO;
        final AuditInfo auditInfo = AuditInfo.with(
            EmailAddress.parse("created@example.com"),
            LocalDateTime.MIN,
            EmailAddress.parse("modified@example.com"),
            LocalDateTime.MAX
        );

        final SpreadsheetMetadata notEmpty = SpreadsheetMetadataNonEmpty.with(
            Maps.of(propertyName, auditInfo),
            SpreadsheetMetadata.EMPTY
        );
        this.getAndCheck(
            notEmpty,
            propertyName,
            auditInfo
        );
    }

    // getOrFail........................................................................................................

    @Test
    public void testGetOrFailPresent() {
        final SpreadsheetMetadataPropertyName<AuditInfo> propertyName = SpreadsheetMetadataPropertyName.AUDIT_INFO;
        final AuditInfo auditInfo = AuditInfo.with(
            EmailAddress.parse("created@example.com"),
            LocalDateTime.MIN,
            EmailAddress.parse("modified@example.com"),
            LocalDateTime.MAX
        );

        final SpreadsheetMetadata metadata = SpreadsheetMetadataNonEmpty.with(
            Maps.of(
                propertyName,
                auditInfo
            ),
            SpreadsheetMetadata.EMPTY
        );
        this.checkEquals(
            auditInfo,
            metadata.getOrFail(propertyName),
            () -> "getOrFail " + propertyName + " in " + metadata
        );
    }

    // effectiveStyle...................................................................................................

    @Test
    public void testEffectiveStyleMissingDefaults() {
        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.BLACK
        );

        this.effectiveStyleAndCheck(
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.STYLE,
                style
            ),
            style
        );
    }

    @Test
    public void testEffectiveStyleOnlyDefaults() {
        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.BLACK
        );

        this.effectiveStyleAndCheck(
            SpreadsheetMetadata.EMPTY.setDefaults(
                SpreadsheetMetadata.EMPTY.set(
                    SpreadsheetMetadataPropertyName.STYLE,
                    style
                )
            ),
            style
        );
    }

    @Test
    public void testEffectiveStyleMixed() {
        final Length<?> width = Length.pixel(12.0);
        final Length<?> height = Length.pixel(34.0);

        this.effectiveStyleAndCheck(
            SpreadsheetMetadata.EMPTY
                .set(
                    SpreadsheetMetadataPropertyName.STYLE,
                    TextStyle.EMPTY.set(
                        TextStylePropertyName.WIDTH,
                        width
                    )
                ).setDefaults(
                    SpreadsheetMetadata.EMPTY.set(
                        SpreadsheetMetadataPropertyName.STYLE,
                        TextStyle.EMPTY.set(
                            TextStylePropertyName.HEIGHT,
                            height
                        )
                    )
                ),
            TextStyle.EMPTY.set(
                TextStylePropertyName.WIDTH,
                width
            ).set(
                TextStylePropertyName.HEIGHT,
                height
            )
        );
    }

    @Test
    public void testEffectiveStyleDefaultLowerPriority() {
        final Length<?> width = Length.pixel(12.0);
        final Length<?> height = Length.pixel(34.0);

        this.effectiveStyleAndCheck(
            SpreadsheetMetadata.EMPTY
                .set(
                    SpreadsheetMetadataPropertyName.STYLE,
                    TextStyle.EMPTY.set(
                        TextStylePropertyName.WIDTH,
                        width
                    )
                ).setDefaults(
                    SpreadsheetMetadata.EMPTY.set(
                        SpreadsheetMetadataPropertyName.STYLE,
                        TextStyle.EMPTY.set(
                            TextStylePropertyName.HEIGHT,
                            height
                        ).set(
                            TextStylePropertyName.WIDTH,
                            Length.pixel(999.0) // ignored
                        )
                    )
                ),
            TextStyle.EMPTY.set(
                TextStylePropertyName.WIDTH,
                width
            ).set(
                TextStylePropertyName.HEIGHT,
                height
            )
        );
    }

    @Test
    public void testEffectiveStyleDefaultLowerPriority2() {
        final Length<?> width = Length.pixel(12.0);
        final Length<?> height = Length.pixel(34.0);
        final Color color = Color.parse("#123");
        final Color bgColor = Color.parse("#456");

        this.effectiveStyleAndCheck(
            SpreadsheetMetadata.EMPTY
                .set(
                    SpreadsheetMetadataPropertyName.STYLE,
                    TextStyle.EMPTY.set(
                        TextStylePropertyName.WIDTH,
                        width
                    ).set(
                        TextStylePropertyName.COLOR,
                        color
                    )
                ).setDefaults(
                    SpreadsheetMetadata.EMPTY.set(
                        SpreadsheetMetadataPropertyName.STYLE,
                        TextStyle.EMPTY.set(
                            TextStylePropertyName.HEIGHT,
                            height
                        ).set(
                            TextStylePropertyName.WIDTH,
                            Length.pixel(999.0) // ignored
                        ).set(
                            TextStylePropertyName.BACKGROUND_COLOR,
                            bgColor
                        ).set(
                            TextStylePropertyName.COLOR,
                            Color.parse("#999")
                        )
                    )
                ),
            TextStyle.EMPTY.set(
                TextStylePropertyName.WIDTH,
                width
            ).set(
                TextStylePropertyName.HEIGHT,
                height
            ).set(
                TextStylePropertyName.COLOR,
                color
            ).set(
                TextStylePropertyName.BACKGROUND_COLOR,
                bgColor
            )
        );
    }

    // set..............................................................................................................

    @Test
    public void testSetExistingPropertyAndValue() {
        this.setAndCheck(this.createSpreadsheetMetadata(),
            this.property1(),
            this.value1());
    }

    @Test
    public void testSetExistingPropertyAndValue2() {
        this.setAndCheck(this.createSpreadsheetMetadata(),
            this.property2(),
            this.value2());
    }

    @Test
    public void testSetPropertyValueSameDefaultValue() {
        final SpreadsheetMetadataPropertyName<RoundingMode> roundingMode = SpreadsheetMetadataPropertyName.ROUNDING_MODE;
        final RoundingMode same = RoundingMode.HALF_UP;

        this.setAndCheck(
            SpreadsheetMetadataNonEmpty.with(
                Maps.empty(),
                SpreadsheetMetadataNonEmpty.with(
                    Maps.of(
                        roundingMode, same
                    ),
                    null
                )
            ),
            roundingMode,
            same,
            "{\n" +
                "  \"roundingMode\": \"HALF_UP\",\n" +
                "  \"_defaults\": {\n" +
                "    \"roundingMode\": \"HALF_UP\"\n" +
                "  }\n" +
                "}"
        );
    }

    @Test
    public void testSetReplacePropertyAndValue() {
        final SpreadsheetMetadataPropertyName<AuditInfo> auditInfo = this.property1();
        final AuditInfo value1 = this.value1();

        final SpreadsheetMetadataPropertyName<Boolean> hideIfZero = this.property2();
        final Boolean value2 = this.value2();

        final AuditInfo different = AuditInfo.with(
            EmailAddress.parse("different@example.com"),
            LocalDateTime.MIN,
            EmailAddress.parse("different@example.com"),
            LocalDateTime.MAX
        );
        assertNotSame(different, value1);

        this.setAndCheck(
            this.createSpreadsheetMetadata(
                auditInfo, value1,
                hideIfZero, value2
            ),
            auditInfo,
            different,
            "{\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"different@example.com\",\n" +
                "    \"createdTimestamp\": \"-999999999-01-01T00:00\",\n" +
                "    \"modifiedBy\": \"different@example.com\",\n" +
                "    \"modifiedTimestamp\": \"+999999999-12-31T23:59:59.999999999\"\n" +
                "  },\n" +
                "  \"hideZeroValues\": true\n" +
                "}"
        );
    }

    @Test
    public void testSetReplacePropertyAndValue2() {
        final SpreadsheetMetadataPropertyName<AuditInfo> auditInfo = this.property1();
        final AuditInfo value1 = this.value1();

        final SpreadsheetMetadataPropertyName<Boolean> hideIfZero = this.property2();
        final Boolean value2 = this.value2();

        final Boolean different = !value2;
        assertNotSame(different, value2);

        this.setAndCheck(
            this.createSpreadsheetMetadata(
                auditInfo,
                value1,
                hideIfZero,
                value2
            ),
            hideIfZero,
            different,
            "{\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"created@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  },\n" +
                "  \"hideZeroValues\": false\n" +
                "}"
        );
    }

    @Test
    public void testSetNewPropertyAndValue() {
        final SpreadsheetMetadataPropertyName<AuditInfo> auditInfo = this.property1();
        final AuditInfo value1 = this.value1();

        final SpreadsheetMetadataPropertyName<Boolean> hideIfZero = this.property2();
        final Boolean value2 = this.value2();

        final SpreadsheetMetadataPropertyName<Integer> precision = this.property3();
        final Integer value3 = this.value3();

        this.setAndCheck(
            this.createSpreadsheetMetadata(
                auditInfo,
                value1,
                hideIfZero,
                value2
            ),
            precision,
            value3,
            "{\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"created@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  },\n" +
                "  \"hideZeroValues\": true,\n" +
                "  \"precision\": 10\n" +
                "}"
        );
    }

    private <T> void setAndCheck(final SpreadsheetMetadata metadata,
                                 final SpreadsheetMetadataPropertyName<T> propertyName,
                                 final T value) {
        assertSame(metadata,
            metadata.set(propertyName, value),
            () -> metadata + " set " + propertyName + " and " + CharSequences.quoteIfChars(value));
    }

    // remove...........................................................................................................

    @Test
    public void testRemove() {
        final SpreadsheetMetadataPropertyName<AuditInfo> property1 = this.property1();

        final SpreadsheetMetadataPropertyName<Boolean> property2 = this.property2();
        final Boolean value2 = this.value2();

        this.removeAndCheck(
            this.createSpreadsheetMetadata(
                property1,
                this.value1(),
                property2,
                value2
            ),
            property1,
            this.createSpreadsheetMetadata(property2, value2)
        );
    }

    @Test
    public void testRemove2() {
        final SpreadsheetMetadataPropertyName<AuditInfo> property1 = this.property1();
        final AuditInfo value1 = this.value1();

        final SpreadsheetMetadataPropertyName<Boolean> property2 = this.property2();

        this.removeAndCheck(
            this.createSpreadsheetMetadata(
                property1,
                value1,
                property2,
                this.value2()
            ),
            property2,
            this.createSpreadsheetMetadata(property1, value1)
        );
    }

    @Test
    public void testRemoveBecomesEmpty() {
        final SpreadsheetMetadataPropertyName<AuditInfo> property1 = this.property1();
        final AuditInfo value1 = this.value1();

        this.removeAndCheck(
            this.createSpreadsheetMetadata(property1, value1),
            property1,
            SpreadsheetMetadata.EMPTY
        );
    }

    // set & remove ...................................................................................................

    @Test
    public void testSetSetRemoveRemove() {
        //set
        final SpreadsheetMetadataPropertyName<AuditInfo> property1 = this.property1();
        final AuditInfo value1 = this.value1();
        final SpreadsheetMetadata metadata1 = this.setAndCheck(
            SpreadsheetMetadata.EMPTY,
            property1,
            value1,
            "{\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"created@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  }\n" +
                "}"
        );

        //set
        final SpreadsheetMetadataPropertyName<Boolean> property2 = this.property2();
        final Boolean value2 = this.value2();
        final SpreadsheetMetadata metadata2 = this.setAndCheck(
            metadata1,
            property2,
            value2,
            "{\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"created@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  },\n" +
                "  \"hideZeroValues\": true\n" +
                "}"
        );

        // remove1
        final SpreadsheetMetadata metadata3 = this.removeAndCheck(metadata2,
            property1,
            this.createSpreadsheetMetadata(property2, value2));

        this.removeAndCheck(metadata3,
            property2,
            SpreadsheetMetadata.EMPTY);
    }

    @Test
    public void testSetSetRemoveSet() {
        //set
        final SpreadsheetMetadataPropertyName<AuditInfo> property1 = this.property1();
        final AuditInfo value1 = this.value1();
        final SpreadsheetMetadata metadata1 = this.setAndCheck(
            SpreadsheetMetadata.EMPTY,
            property1,
            value1,
            "{\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"created@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  }\n" +
                "}"
        );

        //set
        final SpreadsheetMetadataPropertyName<Boolean> property2 = this.property2();
        final Boolean value2 = this.value2();
        final SpreadsheetMetadata metadata2 = this.setAndCheck(
            metadata1,
            property2,
            value2,
            "{\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"created@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  },\n" +
                "  \"hideZeroValues\": true\n" +
                "}"
        );

        // remove1
        final SpreadsheetMetadata metadata3 = this.removeAndCheck(metadata2,
            property1,
            this.createSpreadsheetMetadata(property2, value2));


        //set property1 again
        this.setAndCheck(metadata3,
            property1,
            value1,
            "{\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"created@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  },\n" +
                "  \"hideZeroValues\": true\n" +
                "}"
        );
    }

    // getEffectiveStyleProperty........................................................................................

    @Test
    public void testGetEffectiveStylePropertyEmptyDefaults() {
        final TextStylePropertyName<WordWrap> textStylePropertyName = TextStylePropertyName.WORD_WRAP;
        final WordWrap wordWrap = WordWrap.BREAK_WORD;

        this.getEffectiveStylePropertyAndCheck(
            this.createObject()
                .set(
                    SpreadsheetMetadataPropertyName.STYLE,
                    TextStyle.EMPTY.set(textStylePropertyName, wordWrap)
                )
                .setDefaults(
                    SpreadsheetMetadata.EMPTY.set(
                        SpreadsheetMetadataPropertyName.STYLE, TextStyle.EMPTY
                    )
                ),
            textStylePropertyName,
            wordWrap
        );
    }

    @Test
    public void testGetEffectiveStylePropertyIgnoresDefaults() {
        final TextStylePropertyName<WordWrap> textStylePropertyName = TextStylePropertyName.WORD_WRAP;
        final WordWrap wordWrap = WordWrap.BREAK_WORD;

        this.getEffectiveStylePropertyAndCheck(
            this.createObject()
                .set(
                    SpreadsheetMetadataPropertyName.STYLE,
                    TextStyle.EMPTY.set(textStylePropertyName, wordWrap)
                )
                .setDefaults(
                    SpreadsheetMetadata.EMPTY.set(
                        SpreadsheetMetadataPropertyName.STYLE,
                        TextStyle.EMPTY.set(textStylePropertyName, WordWrap.NORMAL)
                    )
                ),
            textStylePropertyName,
            wordWrap
        );
    }

    // getEffectiveStylePropertyOrFail..................................................................................

    @Test
    public void testGetEffectiveStylePropertyOrFailEmptyDefaults() {
        final TextStylePropertyName<WordWrap> textStylePropertyName = TextStylePropertyName.WORD_WRAP;
        final WordWrap wordWrap = WordWrap.BREAK_WORD;

        this.getEffectiveStylePropertyOrFailAndCheck(
            this.createObject()
                .set(
                    SpreadsheetMetadataPropertyName.STYLE,
                    TextStyle.EMPTY.set(textStylePropertyName, wordWrap)
                )
                .setDefaults(
                    SpreadsheetMetadata.EMPTY.set(
                        SpreadsheetMetadataPropertyName.STYLE, TextStyle.EMPTY
                    )
                ),
            textStylePropertyName,
            wordWrap
        );
    }

    @Test
    public void testGetEffectiveStylePropertyOrFailIgnoresDefaults() {
        final TextStylePropertyName<WordWrap> textStylePropertyName = TextStylePropertyName.WORD_WRAP;
        final WordWrap wordWrap = WordWrap.BREAK_WORD;

        this.getEffectiveStylePropertyOrFailAndCheck(
            this.createObject()
                .set(
                    SpreadsheetMetadataPropertyName.STYLE,
                    TextStyle.EMPTY.set(textStylePropertyName, wordWrap)
                )
                .setDefaults(
                    SpreadsheetMetadata.EMPTY.set(
                        SpreadsheetMetadataPropertyName.STYLE,
                        TextStyle.EMPTY.set(textStylePropertyName, WordWrap.NORMAL)
                    )
                ),
            textStylePropertyName,
            wordWrap
        );
    }

    // NameToColor......................................................................................................

    @Test
    public void testNameToColor2() {
        final Color color1 = Color.fromRgb(0x111);
        final SpreadsheetColorName name1 = SpreadsheetColorName.with("title");

        final Color color2 = Color.fromRgb(0x222);
        final SpreadsheetColorName name2 = SpreadsheetColorName.with("that");

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
            .set(SpreadsheetMetadataPropertyName.numberedColor(2), color1)
            .set(SpreadsheetMetadataPropertyName.numberedColor(4), color2)
            .set(SpreadsheetMetadataPropertyName.namedColor(name1), 2)
            .set(SpreadsheetMetadataPropertyName.namedColor(name2), 4)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE);

        Stream.of(name1, name2, SpreadsheetColorName.with("unknown"))
            .forEach(n -> this.nameToColorAndCheck(metadata,
                n,
                name1 == n ? color1 :
                    name2 == n ? color2 :
                        null));
    }

    @Test
    public void testNameToColorBlack() {
        final Color black = Color.BLACK;
        final SpreadsheetColorName blackName = SpreadsheetColorName.BLACK;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
            .set(SpreadsheetMetadataPropertyName.numberedColor(1), black)
            .set(SpreadsheetMetadataPropertyName.namedColor(blackName), 1)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE);

        this.nameToColorAndCheck(
            metadata,
            blackName,
            black
        );
    }

    @Test
    public void testNameToColorRed() {
        final Color red = Color.parse("#f00");
        final SpreadsheetColorName redName = SpreadsheetColorName.RED;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
            .set(SpreadsheetMetadataPropertyName.numberedColor(12), red)
            .set(SpreadsheetMetadataPropertyName.namedColor(redName), 12)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE);

        this.nameToColorAndCheck(
            metadata,
            redName,
            red
        );
    }

    @Test
    public void testNameToColorUsesDefaults() {
        final Color red = Color.parse("#f00");
        final SpreadsheetColorName redName = SpreadsheetColorName.RED;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .setDefaults(
                SpreadsheetMetadata.EMPTY
                    .set(SpreadsheetMetadataPropertyName.numberedColor(23), red)
                    .set(SpreadsheetMetadataPropertyName.namedColor(redName), 23)
            );

        this.nameToColorAndCheck(
            metadata,
            redName,
            red
        );
    }

    @Test
    public void testNameToColorUsesDefaults2() {
        final Color red = Color.parse("#f00");
        final SpreadsheetColorName redName = SpreadsheetColorName.RED;
        final int number = 12;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.numberedColor(number), red)
            .setDefaults(
                SpreadsheetMetadata.EMPTY
                    .set(SpreadsheetMetadataPropertyName.namedColor(redName), number)
            );

        this.numberToColorAndCheck(
            metadata,
            number,
            red
        );
        this.nameToColorAndCheck(
            metadata,
            redName,
            red
        );
    }

    @Test
    public void testNameToColorIgnoresDefaults() {
        final Color red = Color.parse("#f00");
        final SpreadsheetColorName redName = SpreadsheetColorName.RED;
        final int number = 23;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.numberedColor(number), red)
            .setDefaults(
                SpreadsheetMetadata.EMPTY
                    .set(SpreadsheetMetadataPropertyName.numberedColor(number), Color.parse("#999"))
                    .set(SpreadsheetMetadataPropertyName.namedColor(redName), number)
            );

        this.numberToColorAndCheck(
            metadata,
            number,
            red
        );
        this.nameToColorAndCheck(
            metadata,
            redName,
            red
        );
    }

    @Test
    public void testNameToColorDifferentCase() {
        final Color color1 = Color.fromRgb(0x111);
        final SpreadsheetColorName name1 = SpreadsheetColorName.with("title");

        final Color color2 = Color.fromRgb(0x222);
        final SpreadsheetColorName name2 = SpreadsheetColorName.with("that");

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
            .set(SpreadsheetMetadataPropertyName.numberedColor(2), color1)
            .set(SpreadsheetMetadataPropertyName.numberedColor(4), color2)
            .set(SpreadsheetMetadataPropertyName.namedColor(name1), 2)
            .set(SpreadsheetMetadataPropertyName.namedColor(name2), 4)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE);

        this.nameToColorAndCheck(
            metadata,
            SpreadsheetColorName.with(
                name1.value()
                    .toUpperCase()
            ),
            color1
        );

        this.nameToColorAndCheck(
            metadata,
            name2,
            color2
        );
    }

    @Test
    public void testNameToColorCached() {
        final SpreadsheetMetadata metadata = this.createSpreadsheetMetadata();
        assertSame(metadata.nameToColor(), metadata.nameToColor());
    }

    // NumberToColor....................................................................................................

    @Test
    public void testNumberToColor2() {
        final Color color1 = Color.fromRgb(0x111);
        final int number1 = 1;

        final Color color7 = Color.fromRgb(0x777);
        final int number7 = 7;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
            .set(SpreadsheetMetadataPropertyName.numberedColor(number1), color1)
            .set(SpreadsheetMetadataPropertyName.numberedColor(number7), color7)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("#0.0").spreadsheetFormatterSelector());

        for (int i = SpreadsheetColors.MIN; i < 10; i++) {
            this.numberToColorAndCheck(metadata,
                i,
                number1 == i ? color1 :
                    number7 == i ? color7 :
                        null);
        }
    }

    @Test
    public void testNumberToColorDefaults() {
        final Color color = Color.parse("#123456");
        final int number = 23;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .setDefaults(
                SpreadsheetMetadata.EMPTY
                    .set(
                        SpreadsheetMetadataPropertyName.numberedColor(number),
                        color
                    )
            );

        this.numberToColorAndCheck(
            metadata,
            number,
            color
        );
    }

    @Test
    public void testNumberToColorIgnoresDefaults() {
        final Color color = Color.parse("#123456");
        final int number = 23;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.numberedColor(number), color)
            .setDefaults(
                SpreadsheetMetadata.EMPTY
                    .set(
                        SpreadsheetMetadataPropertyName.numberedColor(number),
                        Color.parse("#999")
                    )
            );

        this.numberToColorAndCheck(
            metadata,
            number,
            color
        );
    }

    @Test
    public void testNumberToColorCached() {
        final SpreadsheetMetadata metadata = this.createSpreadsheetMetadata();
        assertSame(metadata.numberToColor(), metadata.numberToColor());
    }

    // NumberToColorName................................................................................................

    @Test
    public void testNumberToColorName2() {
        final SpreadsheetColorName colorName1 = SpreadsheetColorName.CYAN;
        final Color color1 = Color.fromRgb(0x111);
        final int number1 = 1;

        final SpreadsheetColorName colorName7 = SpreadsheetColorName.YELLOW;
        final Color color7 = Color.fromRgb(0x777);
        final int number7 = 7;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
            .set(SpreadsheetMetadataPropertyName.numberedColor(number1), color1)
            .set(SpreadsheetMetadataPropertyName.namedColor(colorName1), number1)
            .set(SpreadsheetMetadataPropertyName.numberedColor(number7), color7)
            .set(SpreadsheetMetadataPropertyName.namedColor(colorName7), number7)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("#0.0").spreadsheetFormatterSelector());

        this.numberToColorNameAndCheck(
            metadata,
            number1,
            colorName1
        );

        this.numberToColorNameAndCheck(
            metadata,
            number7,
            colorName7
        );
    }

    @Test
    public void testNumberToColorNameDefaults() {
        final SpreadsheetColorName colorName = SpreadsheetColorName.CYAN;
        final Color color = Color.parse("#123456");
        final int number = 4;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .setDefaults(
                SpreadsheetMetadata.EMPTY
                    .set(
                        SpreadsheetMetadataPropertyName.numberedColor(number),
                        color
                    ).set(
                        SpreadsheetMetadataPropertyName.namedColor(colorName),
                        number
                    )
            );

        this.numberToColorNameAndCheck(
            metadata,
            number,
            colorName
        );
    }

    @Test
    public void testNumberToColorNameIgnoresDefaults() {
        final SpreadsheetColorName colorName = SpreadsheetColorName.CYAN;
        final Color color = Color.parse("#123456");
        final int number = 4;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.numberedColor(number), color)
            .set(SpreadsheetMetadataPropertyName.namedColor(colorName), number)
            .setDefaults(
                SpreadsheetMetadata.EMPTY
                    .set(
                        SpreadsheetMetadataPropertyName.numberedColor(number),
                        Color.parse("#999")
                    )
            );

        this.numberToColorNameAndCheck(
            metadata,
            number,
            colorName
        );
    }

    @Test
    public void testNumberToColorNameCached() {
        final SpreadsheetMetadata metadata = this.createSpreadsheetMetadata();
        assertSame(
            metadata.numberToColorName(),
            metadata.numberToColorName()
        );
    }

    // setDefaults......................................................................................................

    @Test
    public void testSetDefaultsCycleFails() {
        final SpreadsheetMetadata metadata = this.createObject();
        assertThrows(IllegalArgumentException.class, () -> metadata.setDefaults(metadata));
    }

    @Test
    public void testSetDefaultsCycleFails1() {
        final SpreadsheetMetadata metadata = this.createObject();
        final SpreadsheetMetadata notEmpty = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE);

        final SpreadsheetMetadata withDefaults = metadata.setDefaults(notEmpty);
        assertThrows(IllegalArgumentException.class, () -> withDefaults.setDefaults(withDefaults));
    }

    // HateosResource...................................................................................................

    @Test
    public void testHateosLinkId() {
        this.hateosLinkIdAndCheck(
            SpreadsheetMetadataNonEmpty.with(
                Maps.of(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(0x12347f)
                ),
                SpreadsheetMetadata.EMPTY),
            "12347f");
    }

    @Test
    public void testHateosLinkIdMissingIdFails() {
        assertThrows(IllegalStateException.class, () -> this.createSpreadsheetMetadata().hateosLinkId());
    }

    // HasConverter.....................................................................................................

    @Test
    public void testConverterExpressionNumberToString() {
        this.converterConvertAndCheck(
            EXPRESSION_NUMBER_KIND.create(123.5),
            "123.5");
    }

    @Test
    public void testConverterBigDecimalToString() {
        this.converterConvertAndCheck(
            BigDecimal.valueOf(123.5),
            "123.5"
        );
    }

    @Test
    public void testConverterBigIntegerToString() {
        this.converterConvertAndCheck(
            BigInteger.valueOf(123),
            "123"
        );
    }

    @Test
    public void testConverterByteToString() {
        this.converterConvertAndCheck(
            (byte) 123,
            "123"
        );
    }

    @Test
    public void testConverterShortToString() {
        this.converterConvertAndCheck(
            (short) 123,
            "123"
        );
    }

    @Test
    public void testConverterIntegerToString() {
        this.converterConvertAndCheck(
            123,
            "123"
        );
    }

    @Test
    public void testConverterLongToString() {
        this.converterConvertAndCheck(
            123L,
            "123"
        );
    }

    @Test
    public void testConverterFloatToString() {
        this.converterConvertAndCheck(
            123.5f,
            "123.5"
        );
    }

    @Test
    public void testConverterDoubleToString() {
        this.converterConvertAndCheck(
            123.5,
            "123.5"
        );
    }

    @Test
    public void testConverterStringToExpressionNumber() {
        this.converterConvertAndCheck(
            "123.500",
            EXPRESSION_NUMBER_KIND.create(123.5)
        );
    }

    @Test
    public void testConverterStringToBigDecimal() {
        this.converterConvertAndCheck(
            "123.500",
            BigDecimal.valueOf(123.5)
        );
    }

    @Test
    public void testConverterStringToBigInteger() {
        this.converterConvertAndCheck(
            "123.000",
            BigInteger.valueOf(123)
        );
    }

    @Test
    public void testConverterStringToByte() {
        this.converterConvertAndCheck(
            "123.000",
            (byte) 123
        );
    }

    @Test
    public void testConverterStringToShort() {
        this.converterConvertAndCheck(
            "123.000",
            (short) 123
        );
    }

    @Test
    public void testConverterStringToInteger() {
        this.converterConvertAndCheck(
            "123.000",
            123
        );
    }

    @Test
    public void testConverterStringToLong() {
        this.converterConvertAndCheck(
            "123.000",
            123L
        );
    }

    @Test
    public void testConverterStringToFloat() {
        this.converterConvertAndCheck(
            "123.500",
            123.5f
        );
    }

    @Test
    public void testConverterStringToDouble() {
        this.converterConvertAndCheck(
            "123.500",
            123.5
        );
    }

    @Test
    public void testConverterDateToString() {
        this.converterConvertAndCheck(
            "Date 2000 12 31",
            LocalDate.of(2000, 12, 31)
        );
    }

    @Test
    public void testConverterStringToDate() {
        this.converterConvertAndCheck(
            LocalDate.of(2000, 12, 31),
            "Date 2000 12 31"
        );
    }

    @Test
    public void testConverterDateTimeToString() {
        this.converterConvertAndCheck(
            "DateTime 2000 12",
            LocalDateTime.of(2000, 1, 1, 12, 0, 0)
        );
    }

    @Test
    public void testConverterStringToDateTime() {
        this.converterConvertAndCheck(
            LocalDateTime.of(2000, 1, 1, 12, 0, 0),
            "DateTime 2000 12"
        );
    }

    @Test
    public void testConverterStringToString() {
        final String text = "abc123";
        this.converterConvertAndCheck(text, text);
    }

    @Test
    public void testConverterTimeToString() {
        this.converterConvertAndCheck(
            "Time 59 12",
            LocalTime.of(12, 0, 59)
        );
    }

    @Test
    public void testConverterStringToTime() {
        this.converterConvertAndCheck(
            LocalTime.of(12, 58, 59),
            "Time 59 12"
        );
    }

    private void converterConvertAndCheck(final Object value,
                                          final Object expected) {
        final SpreadsheetMetadataPropertyName<ConverterSelector> converterSelector = SpreadsheetMetadataPropertyName.FORMULA_CONVERTER;
        final SpreadsheetMetadata metadata = this.createSpreadsheetMetadataWithConverter(converterSelector);

        final Converter<SpreadsheetConverterContext> converter = metadata.converter(
            converterSelector,
            SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                (final ProviderContext p) -> metadata.dateTimeConverter(
                    SPREADSHEET_FORMATTER_PROVIDER,
                    SPREADSHEET_PARSER_PROVIDER,
                    p
                )
            ),
            PROVIDER_CONTEXT
        );

        final Locale locale = LOCALE;

        this.convertAndCheck3(
            value,
            expected,
            converter,
            SpreadsheetConverterContexts.basic(
                Optional.of(metadata),
                SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                converter,
                LABEL_NAME_RESOLVER,
                JsonNodeConverterContexts.basic(
                    ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ConverterContexts.basic(
                            false, // canNumbersHaveGroupSeparator
                            Converters.JAVA_EPOCH_OFFSET, // dateOffset
                            LINE_ENDING,
                            ',', // valueSeparator
                            Converters.fake(),
                            DateTimeContexts.basic(
                                DateTimeSymbols.fromDateFormatSymbols(
                                    new DateFormatSymbols(locale)
                                ),
                                locale,
                                DEFAULT_YEAR,
                                20,
                                NOW
                            ),
                            DecimalNumberContexts.american(MathContext.DECIMAL32)
                        ),
                        metadata.expressionNumberKind()
                    ),
                    JsonNodeMarshallUnmarshallContexts.fake()
                ),
                LocaleContexts.jre(locale)
            )
        );
    }

    private SpreadsheetMetadata createSpreadsheetMetadataWithConverter(final SpreadsheetMetadataPropertyName<ConverterSelector> converterSelector) {
        return SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
            .set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("\"Date\" yyyy mm dd").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("\"Date\" yyyy mm dd").spreadsheetParserSelector())
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("\"DateTime\" yyyy hh").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("\"DateTime\" yyyy hh").spreadsheetParserSelector())
            .set(
                SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT,
                DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT
            ).set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, DEFAULT_YEAR)
            .set(converterSelector, ConverterSelector.parse("collection(text, number, date-time, basic, spreadsheet-value)"))
            .set(SpreadsheetMetadataPropertyName.ERROR_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("\"Error\" @").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("\"Number\" 00.000").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("\"Number\" 00.000").spreadsheetParserSelector())
            .set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("\"Text\" @").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.TIME_FORMATTER, SpreadsheetPattern.parseTimeFormatPattern("\"Time\" ss hh").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("\"Time\" ss hh").spreadsheetParserSelector())
            .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 20);
    }

    private void convertAndCheck3(final Object value,
                                  final Object expected,
                                  final Converter<SpreadsheetConverterContext> converter,
                                  final SpreadsheetConverterContext context) {
        this.convertAndCheck(
            converter,
            value,
            Cast.to(expected.getClass()),
            context,
            expected
        );
    }

    private final static char VALUE_SEPARATOR = '\'';

    private final static DateTimeSymbols DATE_TIME_SYMBOLS = DateTimeSymbols.fromDateFormatSymbols(
        new DateFormatSymbols(LOCALE)
    );

    private final static DecimalNumberSymbols DECIMAL_NUMBER_SYMBOLS = DecimalNumberSymbols.fromDecimalFormatSymbols(
        '+',
        new DecimalFormatSymbols(
            Locale.forLanguageTag("EN-AU")
        )
    ).setCurrencySymbol("$AUD");

    // HasDateTimeContext...............................................................................................

    @Test
    public void testDateTimeContextMissingDateTimeSymbols() {
        Arrays.stream(Locale.getAvailableLocales())
            .forEach(l -> {
                    final int twoDigitYear = 49;
                    final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                        .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, DEFAULT_YEAR)
                        .set(SpreadsheetMetadataPropertyName.LOCALE, l)
                        .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, twoDigitYear);

                    final DateFormatSymbols symbols = DateFormatSymbols.getInstance(l);
                    final DateTimeContext context = metadata.dateTimeContext(
                        SpreadsheetMetadata.NO_CELL,
                        NOW,
                        LOCALE_CONTEXT
                    );
                    this.amPmAndCheck(context, 13, symbols.getAmPmStrings()[1]);
                    this.monthNameAndCheck(context, 2, symbols.getMonths()[2]);
                    this.monthNameAbbreviationAndCheck(context, 3, symbols.getShortMonths()[3]);
                    this.twoDigitYearAndCheck(context, twoDigitYear);
                    this.weekDayNameAndCheck(context, 1, symbols.getWeekdays()[2]);
                    this.weekDayNameAbbreviationAndCheck(context, 3, symbols.getShortWeekdays()[4]);

                }
            );
    }

    @Test
    public void testDateTimeContextWithDateTimeSymbols() {
        final Locale locale = Locale.forLanguageTag("FR");

        final DateTimeSymbols dateTimeSymbols = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(locale)
        );

        final int twoDigitYear = 49;
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, DEFAULT_YEAR)
            .set(SpreadsheetMetadataPropertyName.LOCALE, locale)
            .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, twoDigitYear);

        final DateTimeContext context = metadata.dateTimeContext(
            SpreadsheetMetadata.NO_CELL,
            NOW,
            LOCALE_CONTEXT
        );

        this.checkEquals(
            dateTimeSymbols,
            context.dateTimeSymbols(),
            "dateTimeSymbols"
        );
    }

    @Test
    public void testDateTimeContextWithSpreadsheetCellWithDateTimeSymbols() {
        final Locale locale = Locale.FRANCE;

        this.checkNotEquals(
            locale,
            LOCALE
        );

        final DateTimeSymbols dateTimeSymbols = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(locale)
        );

        final int twoDigitYear = 49;
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, DEFAULT_YEAR)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, twoDigitYear);

        final DateTimeContext context = metadata.dateTimeContext(
            Optional.of(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                    .setDateTimeSymbols(
                        Optional.of(dateTimeSymbols)
                    )
            ),
            NOW,
            LOCALE_CONTEXT
        );

        this.checkEquals(
            dateTimeSymbols,
            context.dateTimeSymbols(),
            "dateTimeSymbols"
        );
    }

    @Test
    public void testDateTimeContextWithSpreadsheetCellWithDateTimeSymbolsAndLocale() {
        final Locale locale = Locale.FRANCE;

        this.checkNotEquals(
            locale,
            LOCALE
        );

        final DateTimeSymbols dateTimeSymbols = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(locale)
        );

        final int twoDigitYear = 49;
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, DEFAULT_YEAR)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, twoDigitYear);

        final DateTimeContext context = metadata.dateTimeContext(
            Optional.of(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                    .setDateTimeSymbols(
                        Optional.of(dateTimeSymbols)
                    ).setLocale(
                        Optional.of(
                            Locale.GERMANY
                        )
                    )
            ),
            NOW,
            LOCALE_CONTEXT
        );

        this.checkEquals(
            dateTimeSymbols,
            context.dateTimeSymbols(),
            "dateTimeSymbols"
        );
    }

    @Test
    public void testDateTimeContextWithSpreadsheetCellWithLocale() {
        final Locale locale = Locale.FRANCE;

        this.checkNotEquals(
            locale,
            LOCALE
        );

        final DateTimeSymbols dateTimeSymbols = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(locale)
        );

        final int twoDigitYear = 49;
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, DEFAULT_YEAR)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, twoDigitYear);

        final DateTimeContext context = metadata.dateTimeContext(
            Optional.of(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                    .setLocale(
                        Optional.of(locale)
                    )
            ),
            NOW,
            LOCALE_CONTEXT
        );

        this.checkEquals(
            dateTimeSymbols,
            context.dateTimeSymbols(),
            "dateTimeSymbols"
        );
    }

    // DecimalNumberContext.............................................................................................

    @Test
    public void testDecimalNumberContextSomeRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetMetadata.EMPTY.decimalNumberContext(
                SpreadsheetMetadata.NO_CELL,
                LOCALE_CONTEXT
            )
        );
        this.checkEquals(
            "Metadata missing: decimalNumberDigitCount, locale, precision, roundingMode",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testDecimalNumberContextPropertiesPresent() {
        final Locale locale = Locale.forLanguageTag("EN-AU");

        Lists.of(MathContext.DECIMAL32, MathContext.DECIMAL64, MathContext.DECIMAL128, MathContext.UNLIMITED)
            .forEach(mc -> {
                    final int precision = mc.getPrecision();
                    final RoundingMode roundingMode = mc.getRoundingMode();

                    this.checkEquals(
                        DECIMAL_NUMBER_SYMBOLS,
                        SpreadsheetMetadata.EMPTY
                            .set(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS, DECIMAL_NUMBER_SYMBOLS)
                            .set(SpreadsheetMetadataPropertyName.LOCALE, locale)
                            .set(SpreadsheetMetadataPropertyName.PRECISION, precision)
                            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, roundingMode)
                            .getOrFail(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS)
                    );
                }
            );
    }

    @Test
    public void testDecimalNumberContextWithSpreadsheetCellWithDecimalNumberSymbols() {
        final Locale locale = Locale.FRANCE;

        this.checkNotEquals(
            locale,
            LOCALE
        );

        final DecimalNumberSymbols decimalNumberSymbols = DecimalNumberSymbols.fromDecimalFormatSymbols(
            '+',
            new DecimalFormatSymbols(locale)
        );

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT, DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT)
            .set(SpreadsheetMetadataPropertyName.PRECISION, 7)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP);

        final DecimalNumberContext context = metadata.decimalNumberContext(
            Optional.of(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                    .setDecimalNumberSymbols(
                        Optional.of(decimalNumberSymbols)
                    )
            ),
            LOCALE_CONTEXT
        );

        this.checkEquals(
            decimalNumberSymbols,
            context.decimalNumberSymbols(),
            "decimalNumberSymbols"
        );
    }

    @Test
    public void testDecimalNumberContextWithSpreadsheetCellWithDecimalNumberSymbolsAndLocale() {
        final Locale locale = Locale.FRANCE;

        this.checkNotEquals(
            locale,
            LOCALE
        );

        final DecimalNumberSymbols decimalNumberSymbols = DecimalNumberSymbols.fromDecimalFormatSymbols(
            '+',
            new DecimalFormatSymbols(locale)
        );

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT, DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT)
            .set(SpreadsheetMetadataPropertyName.PRECISION, 7)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP);

        final DecimalNumberContext context = metadata.decimalNumberContext(
            Optional.of(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                    .setDecimalNumberSymbols(
                        Optional.of(decimalNumberSymbols)
                    ).setLocale(
                        Optional.of(
                            Locale.GERMANY
                        )
                    )
            ),
            LOCALE_CONTEXT
        );

        this.checkEquals(
            decimalNumberSymbols,
            context.decimalNumberSymbols(),
            "decimalNumberSymbols"
        );
    }

    @Test
    public void testDecimalNumberContextWithSpreadsheetCellWithLocale() {
        final Locale locale = Locale.FRANCE;

        this.checkNotEquals(
            locale,
            LOCALE
        );

        final DecimalNumberSymbols decimalNumberSymbols = DecimalNumberSymbols.fromDecimalFormatSymbols(
            '+',
            new DecimalFormatSymbols(locale)
        );

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT, DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT)
            .set(SpreadsheetMetadataPropertyName.PRECISION, 7)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP);

        final DecimalNumberContext context = metadata.decimalNumberContext(
            Optional.of(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                    .setLocale(
                        Optional.of(locale)
                    )
            ),
            LOCALE_CONTEXT
        );

        this.checkEquals(
            decimalNumberSymbols,
            context.decimalNumberSymbols(),
            "decimalNumberSymbols"
        );
    }

    // ExpressionNumberContext..........................................................................................

    @Test
    public void testExpressionNumberContextSomeRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.DOUBLE)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 5)
                .expressionNumberContext(
                    SpreadsheetMetadata.NO_CELL,
                    LOCALE_CONTEXT
                )
        );
        this.checkEquals(
            "Metadata missing: decimalNumberDigitCount, locale, roundingMode",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testExpressionNumberContextSomeRequiredPropertiesAbsentFails2() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.DOUBLE)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.CEILING)
                .expressionNumberContext(
                    SpreadsheetMetadata.NO_CELL,
                    LOCALE_CONTEXT
                )
        );
        this.checkEquals(
            "Metadata missing: decimalNumberDigitCount, locale, precision",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testExpressionNumberContext() {
        final ExpressionNumberKind kind = ExpressionNumberKind.DOUBLE;

        final ExpressionNumberContext context = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS, DECIMAL_NUMBER_SYMBOLS)
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, kind)
            .set(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT, DECIMAL_NUMBER_DIGIT_COUNT)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.PRECISION, 16)
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.CEILING)
            .expressionNumberContext(
                SpreadsheetMetadata.NO_CELL,
                LOCALE_CONTEXT
            );
        this.checkEquals(kind, context.expressionNumberKind(), "expressionNumberKind");
        this.checkNotEquals(null, context.mathContext(), "mathContext");
    }

    // SpreadsheetEnvironmentContext....................................................................................

    @Test
    public void testSpreadsheetEnvironmentContext() {
        final SpreadsheetMetadataPropertyName<Integer> propertyName = SpreadsheetMetadataPropertyName.PRECISION;
        int precision = 123;

        this.environmentValueAndCheck(
            SpreadsheetMetadata.EMPTY
                .set(propertyName, precision)
                .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
                .spreadsheetEnvironmentContext(
                    EnvironmentContexts.map(
                        EnvironmentContexts.empty(
                            LineEnding.NL,
                            LOCALE_CONTEXT.locale(),
                            NOW,
                            EnvironmentContext.ANONYMOUS
                        )
                    )
                ),
            EnvironmentValueName.with(
                propertyName.value(),
                propertyName.type()
            ),
            precision
        );
    }

    // SpreadsheetFormatter.............................................................................................

    @Test
    public void testSpreadsheetFormatterFormatDate() {
        this.formatAndCheck2(LocalDate.of(2000, 12, 31), "Date 31122000");
    }

    @Test
    public void testSpreadsheetFormatterFormatDateTime() {
        this.formatAndCheck2(LocalDateTime.of(2000, 12, 31, 12, 58, 59), "DateTime 31122000 125859");
    }

    @Test
    public void testSpreadsheetFormatterFormatNumber() {
        this.formatAndCheck2(125.5, "Number 125.500");
    }

    @Test
    public void testSpreadsheetFormatterFormatText() {
        this.formatAndCheck2("abc123", "Text abc123");
    }

    @Test
    public void testSpreadsheetFormatterFormatTime() {
        this.formatAndCheck2(LocalTime.of(12, 58, 59), "Time 125859");
    }

    private void formatAndCheck2(final Object value,
                                 final String text) {
        this.formatAndCheck(
            this.createSpreadsheetMetadataWithFormatter()
                .spreadsheetFormatter(
                    SPREADSHEET_FORMATTER_PROVIDER,
                    PROVIDER_CONTEXT
                ),
            value,
            new FakeSpreadsheetFormatterContext() {
                @Override
                public boolean canConvert(final Object value,
                                          final Class<?> target) {
                    return this.convert(value, target).isLeft();
                }

                @Override
                public <T> Either<T, String> convert(final Object value,
                                                     final Class<T> target) {
                    return Converters.collection(
                        Lists.of(
                            Converters.simple(),
                            ExpressionNumberConverters.toNumberOrExpressionNumber(
                                Converters.numberToNumber()
                            ),
                            Converters.localDateToLocalDateTime(),
                            Converters.localTimeToLocalDateTime()
                        )
                    ).convert(
                        value,
                        target,
                        new FakeExpressionNumberConverterContext() {
                            @Override
                            public ExpressionNumberKind expressionNumberKind() {
                                return ExpressionNumberKind.BIG_DECIMAL;
                            }
                        }
                    );
                }

                @Override
                public char decimalSeparator() {
                    return this.decimalNumberContext.decimalSeparator();
                }

                @Override
                public char groupSeparator() {
                    return this.decimalNumberContext.groupSeparator();
                }

                @Override
                public char negativeSign() {
                    return this.decimalNumberContext.negativeSign();
                }

                @Override
                public char positiveSign() {
                    return this.decimalNumberContext.positiveSign();
                }

                @Override
                public MathContext mathContext() {
                    return this.decimalNumberContext.mathContext();
                }

                @Override
                public char zeroDigit() {
                    return this.decimalNumberContext.zeroDigit();
                }

                private final DecimalNumberContext decimalNumberContext = DecimalNumberContexts.american(MathContext.UNLIMITED);
            },
            SpreadsheetText.with(text));
    }

    private SpreadsheetMetadata createSpreadsheetMetadataWithFormatter() {
        return SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("\"Date\" ddmmyyyy").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("\"DateTime\" ddmmyyyy hhmmss").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.ERROR_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("\"Error\" @").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("\"Number\" #.000").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("\"Text\" @").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.TIME_FORMATTER, SpreadsheetPattern.parseTimeFormatPattern("\"Time\" hhmmss").spreadsheetFormatterSelector());
    }

    // spreadsheetFormatterContext......................................................................................

    @Test
    public void testSpreadsheetFormatterContext() {
        final SpreadsheetMetadata metadata = this.createSpreadsheetMetadataWithConverter(SpreadsheetMetadataPropertyName.FORMATTING_CONVERTER)
            .set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 10)
            .set(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS, DECIMAL_NUMBER_SYMBOLS)
            .set(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT, DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.PRECISION, 10)
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.DOWN)
            .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, ',');

        this.checkNotEquals(
            null,
            metadata.spreadsheetFormatterContext(
                SpreadsheetMetadata.NO_CELL,
                (final Optional<Object> value) -> {
                    throw new UnsupportedOperationException();
                },
                LABEL_NAME_RESOLVER,
                LINE_ENDING,
                LOCALE_CONTEXT,
                SpreadsheetProviders.basic(
                    SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                        (ProviderContext p) -> metadata.dateTimeConverter(
                            SPREADSHEET_FORMATTER_PROVIDER,
                            SPREADSHEET_PARSER_PROVIDER,
                            p
                        )
                    ),
                    ExpressionFunctionProviders.fake(),
                    SpreadsheetComparatorProviders.fake(),
                    SpreadsheetExporterProviders.fake(),
                    SPREADSHEET_FORMATTER_PROVIDER,
                    FormHandlerProviders.fake(),
                    SpreadsheetImporterProviders.fake(),
                    SpreadsheetParserProviders.fake(),
                    ValidatorProviders.fake()
                ),
                PROVIDER_CONTEXT
            )
        );
    }

    // SpreadsheetFormatterProviderSamplesContext.......................................................................

    @Test
    public void testSpreadsheetFormatterProviderSamplesContext() {
        final SpreadsheetMetadata metadata = this.createSpreadsheetMetadataWithConverter(SpreadsheetMetadataPropertyName.FORMATTING_CONVERTER)
            .set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 10)
            .set(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS, DECIMAL_NUMBER_SYMBOLS)
            .set(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT, DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.PRECISION, 10)
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.DOWN)
            .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, ',');

        this.checkNotEquals(
            null,
            metadata.spreadsheetFormatterProviderSamplesContext(
                SpreadsheetMetadata.NO_CELL,
                (final Optional<Object> v) -> {
                    throw new UnsupportedOperationException();
                },
                LABEL_NAME_RESOLVER,
                LINE_ENDING,
                LOCALE_CONTEXT,
                SpreadsheetProviders.basic(
                    SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                        (ProviderContext p) -> metadata.dateTimeConverter(
                            SPREADSHEET_FORMATTER_PROVIDER,
                            SPREADSHEET_PARSER_PROVIDER,
                            p
                        )
                    ),
                    ExpressionFunctionProviders.fake(),
                    SpreadsheetComparatorProviders.fake(),
                    SpreadsheetExporterProviders.fake(),
                    SPREADSHEET_FORMATTER_PROVIDER,
                    FormHandlerProviders.fake(),
                    SpreadsheetImporterProviders.fake(),
                    SpreadsheetParserProviders.fake(),
                    ValidatorProviders.fake()
                ),
                PROVIDER_CONTEXT
            )
        );
    }

    // HasJsonNodeUnmarshallContext.......................................................................................

    @Test
    public void testUnmarshallContextSomeRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.DOUBLE)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 5)
                .jsonNodeUnmarshallContext()
        );
        this.checkEquals(
            "Metadata missing: roundingMode",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testUnmarshallContextSomeRequiredPropertiesAbsentFails2() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.DOUBLE)
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.CEILING)
            .jsonNodeUnmarshallContext());
        this.checkEquals(
            "Metadata missing: precision",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testUnmarshallContext() {
        final ExpressionNumberKind kind = ExpressionNumberKind.DOUBLE;

        final JsonNodeUnmarshallContext context = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, kind)
            .set(SpreadsheetMetadataPropertyName.PRECISION, 5)
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.CEILING)
            .jsonNodeUnmarshallContext();
        this.checkEquals(kind, context.expressionNumberKind(), "expressionNumberKind");
        this.checkNotEquals(null, context.mathContext(), "mathContext");
    }

    @Test
    public void testUnmarshallContextUnmarshall() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.DOUBLE)
            .set(SpreadsheetMetadataPropertyName.PRECISION, 5)
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.CEILING);

        final JsonNodeUnmarshallContext context = metadata.jsonNodeUnmarshallContext();
        final JsonNodeMarshallContext marshallContext = metadata.jsonNodeMarshallContext();

        final BigDecimal bigDecimal = BigDecimal.valueOf(1.5);
        this.checkEquals(bigDecimal, context.unmarshallWithType(marshallContext.marshallWithType(bigDecimal)), () -> "roundtrip json " + bigDecimal);

        final LocalDateTime localDateTime = LocalDateTime.now();
        this.checkEquals(localDateTime, context.unmarshallWithType(marshallContext.marshallWithType(localDateTime)), () -> "roundtrip json " + localDateTime);

        this.checkEquals(metadata, context.unmarshallWithType(marshallContext.marshallWithType(metadata)), () -> "roundtrip json " + metadata);
    }

    // HasMathContext...................................................................................................

    @Test
    public void testHasMathContextRequiredPropertiesAbsentFails2() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetMetadataNonEmpty.with(
                Maps.of(
                    SpreadsheetMetadataPropertyName.PRECISION, 1),
                SpreadsheetMetadata.EMPTY
            ).mathContext()
        );
        this.checkMessage(thrown, "Metadata missing: roundingMode");
    }

    @Test
    public void testMathContext() {
        final int precision = 11;

        Arrays.stream(RoundingMode.values()).forEach(r -> {
            final MathContext mathContext = SpreadsheetMetadataNonEmpty.with(
                    Maps.of(
                        SpreadsheetMetadataPropertyName.PRECISION, precision,
                        SpreadsheetMetadataPropertyName.ROUNDING_MODE, r
                    ),
                    SpreadsheetMetadata.EMPTY
                )
                .mathContext();
            this.checkEquals(precision, mathContext.getPrecision(), "precision");
            this.checkEquals(r, mathContext.getRoundingMode(), "roundingMode");
        });
    }

    @Test
    public void testMathContextCached() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadataNonEmpty.with(
            Maps.of(
                SpreadsheetMetadataPropertyName.PRECISION, 16,
                SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.FLOOR
            ),
            SpreadsheetMetadata.EMPTY
        );
        assertSame(metadata.mathContext(), metadata.mathContext());
    }

    // HasParser........................................................................................................

    @Test
    public void testSpreadsheetParserMissingProperties() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(
                SpreadsheetMetadataPropertyName.DATE_PARSER,
                SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd").spreadsheetParserSelector()
            );
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> metadata.spreadsheetParser(
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
        this.checkEquals(
            "Metadata missing: dateTimeParser, numberParser, timeParser",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testSpreadsheetParserAndParseDate() {
        this.metadataSpreadsheetParserParseAndCheck(
            "2000/12/31",
            (t, c) -> t.cast(DateSpreadsheetFormulaParserToken.class).toLocalDate(c),
            LocalDate.of(2000, 12, 31)
        );
    }

    @Test
    public void testSpreadsheetParserAndParseDateTime() {
        this.metadataSpreadsheetParserParseAndCheck(
            "2000/12/31 15:58",
            (t, c) -> t.cast(DateTimeSpreadsheetFormulaParserToken.class).toLocalDateTime(c),
            LocalDateTime.of(
                LocalDate.of(2000, 12, 31),
                LocalTime.of(15, 58)
            )
        );
    }

    @Test
    public void testSpreadsheetParserAndParseNumber() {
        this.metadataSpreadsheetParserParseAndCheck(
            "1.5",
            (t, c) -> t.cast(NumberSpreadsheetFormulaParserToken.class).toNumber(c),
            EXPRESSION_NUMBER_KIND.create(1.5)
        );
    }

    @Test
    public void testSpreadsheetParserAndParseTime() {
        this.metadataSpreadsheetParserParseAndCheck(
            "15:58",
            (t, c) -> t.cast(TimeSpreadsheetFormulaParserToken.class).toLocalTime(),
            LocalTime.of(15, 58)
        );
    }

    private SpreadsheetMetadata metadataWithSpreadsheetParser() {
        return SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd").spreadsheetParserSelector())
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm").spreadsheetParserSelector())
            .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("#.#").spreadsheetParserSelector())
            .set(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("hh:mm").spreadsheetParserSelector());
    }

    private <T> void metadataSpreadsheetParserParseAndCheck(final String text,
                                                            final BiFunction<ParserToken, ExpressionEvaluationContext, T> valueExtractor,
                                                            final T expected) {
        final TextCursor cursor = TextCursors.charSequence(text);

        final ParserToken token = this.metadataWithSpreadsheetParser()
            .spreadsheetParser(
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
            ).parse(
                cursor,
                this.metadataWithSpreadsheetParserContext()
                    .spreadsheetParserContext(
                        SpreadsheetMetadata.NO_CELL,
                        LOCALE_CONTEXT,
                        NOW
                    )
            ).orElseThrow(() -> new AssertionError("parser failed"));
        this.checkEquals(true, cursor.isEmpty(), () -> cursor + " is not empty");

        final ExpressionEvaluationContext expressionEvaluationContext = new FakeExpressionEvaluationContext() {
            @Override
            public int defaultYear() {
                return 1900;
            }

            @Override
            public ExpressionNumberKind expressionNumberKind() {
                return EXPRESSION_NUMBER_KIND;
            }
        };

        this.checkEquals(expected, valueExtractor.apply(token, expressionEvaluationContext), () -> text + "\n" + token);
    }

    // HasParserContext.................................................................................................

    @Test
    public void testSpreadsheetParserContext() {
        final SpreadsheetMetadata metadata = this.metadataWithSpreadsheetParserContext();

        this.checkNotEquals(
            null,
            metadata.spreadsheetParserContext(
                SpreadsheetMetadata.NO_CELL,
                LOCALE_CONTEXT,
                NOW
            )
        );
    }

    private SpreadsheetMetadata metadataWithSpreadsheetParserContext() {
        return SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS, DECIMAL_NUMBER_SYMBOLS)
            .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, DEFAULT_YEAR)
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .set(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT, DECIMAL_NUMBER_DIGIT_COUNT)
            .set(SpreadsheetMetadataPropertyName.PRECISION, 10)
            .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.DOWN)
            .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 20)
            .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, VALUE_SEPARATOR);
    }

    // SpreadsheetValidatorContext......................................................................................

    @Test
    public void testSpreadsheetValidatorContextWithCell() {
        this.spreadsheetValidatorContextAndCheck(SpreadsheetSelection.A1);
    }

    @Test
    public void testSpreadsheetValidatorContextWithLabel() {
        this.spreadsheetValidatorContextAndCheck(
            SpreadsheetSelection.labelName("Label123")
        );
    }

    private void spreadsheetValidatorContextAndCheck(final SpreadsheetExpressionReference cellOrLabel) {
        final Locale locale = LOCALE;

        final SpreadsheetValidatorContext context = this.createSpreadsheetMetadata()
            .set(
                SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET,
                Converters.EXCEL_1900_DATE_SYSTEM_OFFSET
            ).set(
                SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS,
                DecimalNumberSymbols.fromDecimalFormatSymbols(
                    '+',
                    new DecimalFormatSymbols(locale)
                )
            ).set(
                SpreadsheetMetadataPropertyName.DEFAULT_YEAR,
                1950
            ).set(
                SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND,
                EXPRESSION_NUMBER_KIND
            ).set(
                SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT,
                DECIMAL_NUMBER_DIGIT_COUNT
            ).set(
                SpreadsheetMetadataPropertyName.LOCALE,
                locale
            ).set(
                SpreadsheetMetadataPropertyName.PRECISION,
                10
            ).set(
                SpreadsheetMetadataPropertyName.ROUNDING_MODE,
                RoundingMode.HALF_UP
            ).set(
                SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR,
                50
            ).set(
                SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER,
                ConverterSelector.parse("never")
            ).set(
                SpreadsheetMetadataPropertyName.VALIDATION_VALIDATORS,
                ValidatorAliasSet.EMPTY
            ).set(
                SpreadsheetMetadataPropertyName.VALUE_SEPARATOR,
                ','
            ).spreadsheetValidatorContext(
                cellOrLabel,
                (final ValidatorSelector validatorSelector) -> {
                    throw new UnsupportedOperationException();
                },
                (final Object value,
                 final SpreadsheetExpressionReference c) -> {
                    throw new UnsupportedOperationException();
                },
                LABEL_NAME_RESOLVER,
                LINE_ENDING,
                ConverterProviders.converters(),
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            );

        this.checkEquals(
            cellOrLabel,
            context.validationReference(),
            "validationReference"
        );
    }

    // missingRequiredProperties.........................................................................................

    @Test
    public void testMissingProperties() {
        this.missingRequiredPropertiesAndCheck(
            SpreadsheetMetadata.EMPTY.set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP),
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

    @Test
    public void testMissingPropertiesNonMissing() {
        this.missingRequiredPropertiesAndCheck(
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.with(
                    EmailAddress.parse("created@example.com"),
                    LocalDateTime.MIN,
                    EmailAddress.parse("modified@example.com"),
                    LocalDateTime.MAX
                )
            ).set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
        );
    }

    @Test
    public void testMissingPropertiesSomeMissing() {
        this.missingRequiredPropertiesAndCheck(
            SpreadsheetMetadata.EMPTY
                .set(
                    SpreadsheetMetadataPropertyName.AUDIT_INFO,
                    AuditInfo.with(
                        EmailAddress.parse("created@example.com"),
                        LocalDateTime.MIN,
                        EmailAddress.parse("modified@example.com"),
                        LocalDateTime.MAX
                    )
                ).set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        this.toStringAndCheck(
            SpreadsheetMetadataNonEmpty.with(map, null),
            "{\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"created@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  },\n" +
                "  \"hideZeroValues\": true\n" +
                "}"
        );
    }

    @Test
    public void testToStringStringValue() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.DOWN);

        this.toStringAndCheck(
            SpreadsheetMetadataNonEmpty.with(map, null),
            "{\n" +
                "  \"roundingMode\": \"DOWN\"\n" +
                "}"
        );
    }

    @Test
    public void testToStringWithDefaults() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        this.toStringAndCheck(
            SpreadsheetMetadataNonEmpty.with(
                map,
                null
            ).setDefaults(
                SpreadsheetMetadataNonEmpty.with(
                    Maps.of(
                        SpreadsheetMetadataPropertyName.LOCALE, LOCALE),
                    null
                )
            ),
            "{\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"created@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  },\n" +
                "  \"hideZeroValues\": true,\n" +
                "  \"_defaults\": {\n" +
                "    \"locale\": \"en\"\n" +
                "  }\n" +
                "}"
        );
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Test
    public void testUnmarshallInvalidCharacterValueFails() {
        this.unmarshallFails(
            "{" +
                "  \"decimalSeparator\": \"d\"\n" +
                "}",
            SpreadsheetMetadata.class
        );
    }

    /**
     * This test verifies that all {@link SpreadsheetMetadataPropertyName} value types are also
     * {@link walkingkooka.tree.json.marshall.JsonNodeContext} registered.
     */
    @Test
    public void testUnmarshall() {
        final JsonNode json = JsonNode.parse(
            "{\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"created@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  },\n" +
                "  \"cellCharacterWidth\": 0,\n" +
                "  \"color1\": \"#000001\",\n" +
                "  \"color10\": \"#00000a\",\n" +
                "  \"color11\": \"#00000b\",\n" +
                "  \"color12\": \"#00000c\",\n" +
                "  \"color13\": \"#00000d\",\n" +
                "  \"color14\": \"#00000e\",\n" +
                "  \"color15\": \"#00000f\",\n" +
                "  \"color16\": \"#000010\",\n" +
                "  \"color17\": \"#000011\",\n" +
                "  \"color18\": \"#000012\",\n" +
                "  \"color19\": \"#000013\",\n" +
                "  \"color2\": \"#000002\",\n" +
                "  \"color20\": \"#000014\",\n" +
                "  \"color21\": \"#000015\",\n" +
                "  \"color22\": \"#000016\",\n" +
                "  \"color23\": \"#000017\",\n" +
                "  \"color24\": \"#000018\",\n" +
                "  \"color25\": \"#000019\",\n" +
                "  \"color26\": \"#00001a\",\n" +
                "  \"color27\": \"#00001b\",\n" +
                "  \"color28\": \"#00001c\",\n" +
                "  \"color29\": \"#00001d\",\n" +
                "  \"color3\": \"#000003\",\n" +
                "  \"color30\": \"#00001e\",\n" +
                "  \"color31\": \"#00001f\",\n" +
                "  \"color32\": \"#000020\",\n" +
                "  \"color33\": \"#000021\",\n" +
                "  \"color4\": \"#000004\",\n" +
                "  \"color5\": \"#000005\",\n" +
                "  \"color6\": \"#000006\",\n" +
                "  \"color7\": \"#000007\",\n" +
                "  \"color8\": \"#000008\",\n" +
                "  \"color9\": \"#000009\",\n" +
                "  \"colorbig\": 1,\n" +
                "  \"colormedium\": 2,\n" +
                "  \"colorsmall\": 3,\n" +
                "  \"dateFormatter\": \"date DD/MM/YYYY\",\n" +
                "  \"dateParser\": \"date DD/MM/YYYY;DDMMYYYY\",\n" +
                "  \"dateTimeFormatter\": \"date-time DD/MM/YYYY hh:mm\",\n" +
                "  \"dateTimeOffset\": \"0\",\n" +
                "  \"dateTimeParser\": \"date-time-pattern DD/MM/YYYY hh:mm;DDMMYYYYHHMM;DDMMYYYY HHMM\",\n" +
                "  \"defaultYear\": 1901,\n" +
                "  \"hideZeroValues\": true,\n" +
                "  \"locale\": \"en\",\n" +
                "  \"numberFormatter\": \"number #0.0\",\n" +
                "  \"numberParser\": \"number #0.0;$#0.00\",\n" +
                "  \"precision\": 123,\n" +
                "  \"roundingMode\": \"FLOOR\",\n" +
                "  \"spreadsheetId\": \"7b\",\n" +
                "  \"textFormatter\": \"text @@\",\n" +
                "  \"timeFormatter\": \"time hh:mm\",\n" +
                "  \"timeParser\": \"time hh:mm;hh:mm:ss.000\",\n" +
                "  \"twoDigitYear\": 31\n" +
                "}"
        );
        final SpreadsheetMetadata metadata = this.unmarshall(json);
        this.checkNotEquals(
            metadata,
            SpreadsheetMetadata.EMPTY
        );
    }

    @Test
    public void testMarshallRoundtrip() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> properties = Maps.ordered();

        properties.put(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            AuditInfo.with(
                EmailAddress.parse("created@example.com"),
                LocalDateTime.MIN,
                EmailAddress.parse("modified@example.com"),
                LocalDateTime.MAX
            )
        );
        properties.put(
            SpreadsheetMetadataPropertyName.AUTO_HIDE_SCROLLBARS,
            false
        );
        properties.put(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 0);
        properties.put(
            SpreadsheetMetadataPropertyName.CLIPBOARD_EXPORTER,
            SpreadsheetExporterSelector.parse("json")
        );
        properties.put(
            SpreadsheetMetadataPropertyName.CLIPBOARD_IMPORTER,
            SpreadsheetImporterSelector.parse("json")
        );
        properties.put(
            SpreadsheetMetadataPropertyName.COMPARATORS,
            SpreadsheetComparatorProviders.spreadsheetComparators()
                .spreadsheetComparatorInfos()
                .aliasSet()
        );
        properties.put(
            SpreadsheetMetadataPropertyName.CONVERTERS,
            ConverterAliasSet.parse("text, number, date-time, basic, spreadsheet-value, boolean")
        );
        properties.put(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("DD/MM/YYYY").spreadsheetFormatterSelector());
        properties.put(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("DD/MM/YYYY;DDMMYYYY").spreadsheetParserSelector());
        properties.put(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET);
        properties.put(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("DD/MM/YYYY hh:mm").spreadsheetFormatterSelector());
        properties.put(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("DD/MM/YYYY hh:mm;DDMMYYYYHHMM;DDMMYYYY HHMM").spreadsheetParserSelector());
        properties.put(SpreadsheetMetadataPropertyName.DATE_TIME_SYMBOLS, DATE_TIME_SYMBOLS);
        properties.put(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS, DECIMAL_NUMBER_SYMBOLS);
        properties.put(
            SpreadsheetMetadataPropertyName.DEFAULT_FORM_HANDLER,
            FormHandlerSelector.parse("hello-form-handler")
        );
        properties.put(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 1901);
        properties.put(
            SpreadsheetMetadataPropertyName.ERROR_FORMATTER,
            SpreadsheetPattern.parseTextFormatPattern("\"ERROR\" @").spreadsheetFormatterSelector()
        );
        properties.put(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.BIG_DECIMAL);
        properties.put(
            SpreadsheetMetadataPropertyName.EXPORTERS,
            SpreadsheetExporterAliasSet.EMPTY
        );
        properties.put(
            SpreadsheetMetadataPropertyName.FIND_CONVERTER,
            ConverterSelector.parse("basic")
        );
        properties.put(
            SpreadsheetMetadataPropertyName.FIND_FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("find-something-something")
        );
        properties.put(
            SpreadsheetMetadataPropertyName.FIND_HIGHLIGHTING,
            false
        );
        properties.put(
            SpreadsheetMetadataPropertyName.FIND_QUERY,
            SpreadsheetCellQuery.parse("help()")
        );
        properties.put(
            SpreadsheetMetadataPropertyName.FORMATTING_CONVERTER,
            ConverterSelector.parse("basic")
        );
        properties.put(
            SpreadsheetMetadataPropertyName.FORMATTING_FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("hello")
        );
        properties.put(
            SpreadsheetMetadataPropertyName.FORMATTERS,
            SpreadsheetFormatterProviders.spreadsheetFormatters()
                .spreadsheetFormatterInfos()
                .aliasSet()
        );
        properties.put(
            SpreadsheetMetadataPropertyName.FORM_HANDLERS,
            FormHandlerAliasSet.parse("hello-form-handler")
        );
        properties.put(
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
            ConverterSelector.parse("collection(text, number, date-time, basic, spreadsheet-value, boolean)")
        );
        properties.put(
            SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("hello")
        );
        properties.put(SpreadsheetMetadataPropertyName.FROZEN_COLUMNS, SpreadsheetSelection.parseColumnRange("A:B"));
        properties.put(SpreadsheetMetadataPropertyName.FROZEN_ROWS, SpreadsheetSelection.parseRowRange("1:2"));
        properties.put(
            SpreadsheetMetadataPropertyName.FUNCTIONS,
            SpreadsheetExpressionFunctions.EMPTY_ALIAS_SET
        );
        properties.put(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT, DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT);
        properties.put(SpreadsheetMetadataPropertyName.HIDE_ZERO_VALUES, true);
        properties.put(
            SpreadsheetMetadataPropertyName.IMPORTERS,
            SpreadsheetImporterAliasSet.EMPTY
        );
        properties.put(SpreadsheetMetadataPropertyName.LOCALE, LOCALE);
        properties.put(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("#0.0").spreadsheetFormatterSelector());
        properties.put(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("#0.0;$#0.00").spreadsheetParserSelector());
        properties.put(
            SpreadsheetMetadataPropertyName.PARSERS,
            SPREADSHEET_PARSER_PROVIDER.spreadsheetParserInfos()
                .aliasSet()
        );
        properties.put(
            SpreadsheetMetadataPropertyName.PLUGINS,
            PluginNameSet.parse("test-plugin-111,test-plugin-222")
        );
        properties.put(SpreadsheetMetadataPropertyName.PRECISION, 123);
        properties.put(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.FLOOR);
        properties.put(
            SpreadsheetMetadataPropertyName.SCRIPTING_CONVERTER,
            ConverterSelector.parse("basic")
        );
        properties.put(
            SpreadsheetMetadataPropertyName.SCRIPTING_FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("hello")
        );
        properties.put(
            SpreadsheetMetadataPropertyName.SHOW_FORMULA_EDITOR,
            true
        );
        properties.put(
            SpreadsheetMetadataPropertyName.SHOW_FORMULAS,
            false
        );
        properties.put(
            SpreadsheetMetadataPropertyName.SHOW_GRID_LINES,
            true
        );
        properties.put(
            SpreadsheetMetadataPropertyName.SHOW_HEADINGS,
            true
        );
        properties.put(
            SpreadsheetMetadataPropertyName.SORT_COMPARATORS,
            ConverterSelector.parse("day-of-month")
        );
        properties.put(
            SpreadsheetMetadataPropertyName.SORT_CONVERTER,
            ConverterSelector.parse("basic")
        );
        properties.put(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(123));
        properties.put(SpreadsheetMetadataPropertyName.SPREADSHEET_NAME, SpreadsheetName.with("Spreadsheet-name-123"));
        properties.put(SpreadsheetMetadataPropertyName.STYLE,
            TextStyle.EMPTY
                .set(TextStylePropertyName.FONT_FAMILY, FontFamily.with("MS Sans Serif"))
                .set(TextStylePropertyName.FONT_SIZE, FontSize.with(11))
                .set(TextStylePropertyName.HEIGHT, Length.pixel(60.0))
                .set(TextStylePropertyName.WIDTH, Length.pixel(15.0))
        );
        properties.put(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("@@").spreadsheetFormatterSelector());
        properties.put(SpreadsheetMetadataPropertyName.TIME_FORMATTER, SpreadsheetPattern.parseTimeFormatPattern("hh:mm").spreadsheetFormatterSelector());
        properties.put(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("hh:mm;hh:mm:ss.000").spreadsheetParserSelector());
        properties.put(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 31);
        properties.put(
            SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER,
            ConverterSelector.parse("validator-converter-123")
        );
        properties.put(
            SpreadsheetMetadataPropertyName.VALIDATION_FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("hello")
        );
        properties.put(
            SpreadsheetMetadataPropertyName.VALIDATION_VALIDATORS,
            ValidatorAliasSet.parse("hello")
        );
        properties.put(SpreadsheetMetadataPropertyName.VALIDATORS, ValidatorAliasSet.EMPTY);
        properties.put(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, VALUE_SEPARATOR);
        properties.put(
            SpreadsheetMetadataPropertyName.VIEWPORT_HOME,
            SpreadsheetSelection.A1
        );
        properties.put(
            SpreadsheetMetadataPropertyName.VIEWPORT_SELECTION,
            SpreadsheetSelection.parseColumnRange("B:C")
                .setDefaultAnchor()
        );

        for (int i = SpreadsheetColors.MIN; i < SpreadsheetColors.MAX + 1; i++) {
            properties.put(
                SpreadsheetMetadataPropertyName.numberedColor(i),
                Color.fromRgb(i)
            );
        }

        Stream.of("big", "small", "medium")
            .forEach(
                n -> properties.put(
                    SpreadsheetMetadataPropertyName.namedColor(SpreadsheetColorName.with(n)),
                    n.length()
                )
            );

        final Set<SpreadsheetMetadataPropertyName<?>> missing = Sets.ordered();
        missing.addAll(SpreadsheetMetadataPropertyName.CONSTANTS.values());
        missing.removeAll(properties.keySet());

        this.checkEquals(
            Sets.empty(),
            missing,
            () -> "Several properties are missing values in " + properties);

        this.marshallRoundTripTwiceAndCheck(
            SpreadsheetMetadataNonEmpty.with(
                properties,
                SpreadsheetMetadata.EMPTY
            )
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(1234))
                .set(SpreadsheetMetadataPropertyName.SPREADSHEET_NAME, SpreadsheetName.with("Untitled"))
                .set(SpreadsheetMetadataPropertyName.FROZEN_COLUMNS, SpreadsheetSelection.parseColumnRange("A:C"))
                .set(SpreadsheetMetadataPropertyName.FROZEN_ROWS, SpreadsheetSelection.parseRowRange("1:3"))
                .set(SpreadsheetMetadataPropertyName.VIEWPORT_HOME, SpreadsheetSelection.parseCell("D4")),
            "spreadsheetId: 4d2\n" +
                "frozenColumns: column-range A:C\n" +
                "frozenRows: row-range 1:3\n" +
                "spreadsheetName: Untitled\n" +
                "viewportHome: cell D4\n"
        );
    }

    // helpers...........................................................................................................

    @Override
    public SpreadsheetMetadataNonEmpty createObject() {
        return this.createSpreadsheetMetadata();
    }

    private SpreadsheetMetadataNonEmpty createSpreadsheetMetadata() {
        return this.createSpreadsheetMetadata(this.property1(), this.value1(), this.property2(), this.value2());
    }

    private <X> SpreadsheetMetadataNonEmpty createSpreadsheetMetadata(final SpreadsheetMetadataPropertyName<X> property1,
                                                                      final X value1) {
        return this.createSpreadsheetMetadata(Maps.of(property1, value1));
    }

    private <X, Y> SpreadsheetMetadataNonEmpty createSpreadsheetMetadata(final SpreadsheetMetadataPropertyName<X> property1,
                                                                         final X value1,
                                                                         final SpreadsheetMetadataPropertyName<Y> property2,
                                                                         final Y value2) {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(property1, value1);
        map.put(property2, value2);
        return this.createSpreadsheetMetadata(map);
    }

    private <X, Y, Z> SpreadsheetMetadataNonEmpty createSpreadsheetMetadata(final SpreadsheetMetadataPropertyName<X> property1,
                                                                            final X value1,
                                                                            final SpreadsheetMetadataPropertyName<Y> property2,
                                                                            final Y value2,
                                                                            final SpreadsheetMetadataPropertyName<Z> property3,
                                                                            final Z value3) {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(property1, value1);
        map.put(property2, value2);
        map.put(property3, value3);
        return this.createSpreadsheetMetadata(map);
    }

    private SpreadsheetMetadataNonEmpty createSpreadsheetMetadata(final Map<SpreadsheetMetadataPropertyName<?>, Object> map) {
        return SpreadsheetMetadataNonEmpty.with(map, null);
    }

    @SuppressWarnings("SameReturnValue")
    private SpreadsheetMetadataPropertyName<AuditInfo> property1() {
        return SpreadsheetMetadataPropertyName.AUDIT_INFO;
    }

    private AuditInfo value1() {
        return AuditInfo.with(
            EmailAddress.parse("created@example.com"),
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

    @SuppressWarnings("SameReturnValue")
    private SpreadsheetMetadataPropertyName<Integer> property3() {
        return SpreadsheetMetadataPropertyName.PRECISION;
    }

    private Integer value3() {
        return 10;
    }

    @Override
    Class<SpreadsheetMetadataNonEmpty> metadataType() {
        return SpreadsheetMetadataNonEmpty.class;
    }

}
