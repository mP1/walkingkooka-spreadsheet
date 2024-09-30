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
import walkingkooka.convert.provider.ConverterInfoSet;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextTesting;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextTesting;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.export.SpreadsheetExporterInfoSet;
import walkingkooka.spreadsheet.export.SpreadsheetExporterSelector;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterTesting;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterInfoSet;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetDateParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDateTimeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.parser.SpreadsheetTimeParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberConverters;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.expression.FakeExpressionNumberConverterContext;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.FontFamily;
import walkingkooka.tree.text.FontSize;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.tree.text.WordWrap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
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

    private final static Supplier<LocalDateTime> NOW = LocalDateTime::now;

    private final static SpreadsheetFormatterProvider SPREADSHEET_FORMATTER_PROVIDER = SpreadsheetFormatterProviders.spreadsheetFormatPattern();

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
        final SpreadsheetMetadataPropertyName<EmailAddress> propertyName = SpreadsheetMetadataPropertyName.CREATOR;
        final EmailAddress value = EmailAddress.parse("creator111@example.com");

        final SpreadsheetMetadata notEmpty = SpreadsheetMetadataNonEmpty.with(Maps.of(propertyName, value), SpreadsheetMetadata.EMPTY);
        this.getAndCheck(notEmpty,
                propertyName,
                value);
    }

    // getOrFail........................................................................................................

    @Test
    public void testGetOrFailPresent() {
        final SpreadsheetMetadataPropertyName<EmailAddress> propertyName = SpreadsheetMetadataPropertyName.CREATOR;
        final EmailAddress email = EmailAddress.parse("creator123@example.com");

        final SpreadsheetMetadata metadata = SpreadsheetMetadataNonEmpty.with(Maps.of(propertyName, email), SpreadsheetMetadata.EMPTY);
        this.checkEquals(email,
                metadata.getOrFail(propertyName),
                () -> "getOrFail " + propertyName + " in " + metadata);
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
        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';
        final Character comma = ',';

        this.setAndCheck(
                SpreadsheetMetadataNonEmpty.with(
                        Maps.empty(),
                        SpreadsheetMetadataNonEmpty.with(
                                Maps.of(
                                        decimalSeparator, comma
                                ),
                                null)
                ),
                decimalSeparator,
                comma,
                "{\n" +
                        "  \"_defaults\": {\n" +
                        "    \"decimal-separator\": \",\"\n" +
                        "  },\n" +
                        "  \"decimal-separator\": \",\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetPropertyValueSameDefaultValue2() {
        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';
        final Character comma = ',';

        this.setAndCheck(
                SpreadsheetMetadataNonEmpty.with(
                        Maps.of(
                                decimalSeparator, dot
                        ),
                        SpreadsheetMetadataNonEmpty.with(
                                Maps.of(
                                        decimalSeparator, comma
                                ),
                                null)
                ),
                decimalSeparator,
                comma,
                "{\n" +
                        "  \"_defaults\": {\n" +
                        "    \"decimal-separator\": \",\"\n" +
                        "  },\n" +
                        "  \"decimal-separator\": \",\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetReplacePropertyAndValue() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> created = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> creator = this.property2();
        final EmailAddress value2 = this.value2();

        final LocalDateTime different = LocalDateTime.of(1999, 12, 31, 12, 58, 59);
        assertNotSame(different, value1);

        this.setAndCheck(this.createSpreadsheetMetadata(created, value1, creator, value2),
                created,
                different,
                "{\n" +
                        "  \"create-date-time\": \"1999-12-31T12:58:59\",\n" +
                        "  \"creator\": \"user@example.com\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetReplacePropertyAndValue2() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> created = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> creator = this.property2();
        final EmailAddress value2 = this.value2();

        final EmailAddress different = EmailAddress.parse("different@example.com");
        assertNotSame(different, value2);

        this.setAndCheck(this.createSpreadsheetMetadata(created, value1, creator, value2),
                creator,
                different,
                "{\n" +
                        "  \"create-date-time\": \"2000-01-02T12:58:59\",\n" +
                        "  \"creator\": \"different@example.com\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetNewPropertyAndValue() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> created = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> creator = this.property2();
        final EmailAddress value2 = this.value2();

        final SpreadsheetMetadataPropertyName<EmailAddress> modified = this.property3();
        final EmailAddress value3 = this.value3();

        this.setAndCheck(this.createSpreadsheetMetadata(created, value1, creator, value2),
                modified,
                value3,
                "{\n" +
                        "  \"create-date-time\": \"2000-01-02T12:58:59\",\n" +
                        "  \"creator\": \"user@example.com\",\n" +
                        "  \"modified-by\": \"different@example.com\"\n" +
                        "}");
    }

    @Test
    public void testSetNewPropertyAndValue2() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> created = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> creator = this.property2();
        final EmailAddress value2 = this.value2();

        final SpreadsheetMetadataPropertyName<EmailAddress> modifier = this.property3();
        final EmailAddress value3 = this.value3();

        this.setAndCheck(this.createSpreadsheetMetadata(creator, value2, modifier, value3),
                created,
                value1,
                "{\n" +
                        "  \"create-date-time\": \"2000-01-02T12:58:59\",\n" +
                        "  \"creator\": \"user@example.com\",\n" +
                        "  \"modified-by\": \"different@example.com\"\n" +
                        "}");
    }

    @Test
    public void testSetNewPropertyAndWithoutSwapValue() {
        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final Character comma = ',';

        this.setAndCheck(
                this.createSpreadsheetMetadata(decimalSeparator, dot),
                groupSeparator,
                comma,
                "{\n" +
                        "  \"decimal-separator\": \".\",\n" +
                        "  \"group-separator\": \",\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetNewPropertyAndWithoutSwapValue2() {
        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final Character comma = ',';

        final SpreadsheetMetadataPropertyName<Character> positive = SpreadsheetMetadataPropertyName.POSITIVE_SIGN;
        final Character plus = '+';

        this.setAndCheck(
                this.createSpreadsheetMetadata(decimalSeparator, dot, groupSeparator, comma),
                positive,
                plus,
                "{\n" +
                        "  \"decimal-separator\": \".\",\n" +
                        "  \"group-separator\": \",\",\n" +
                        "  \"positive-sign\": \"+\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetNewPropetyGroupseparatorAndValueSeparatorWithSameValue() {
        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> value = SpreadsheetMetadataPropertyName.VALUE_SEPARATOR;

        this.setAndCheck(
                this.createSpreadsheetMetadata(groupSeparator, dot),
                value,
                dot,
                "{\n" +
                        "  \"group-separator\": \".\",\n" +
                        "  \"value-separator\": \".\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetNewPropertyWithDuplicateFails() {
        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createSpreadsheetMetadata(decimalSeparator, dot).set(groupSeparator, dot)
        );

        this.checkEquals("Cannot set group-separator='.' duplicate of decimal-separator", thrown.getMessage(), "thrown message");
    }

    @Test
    public void testSetNewPropertyWithDuplicateFails2() {
        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> valueSeparator = SpreadsheetMetadataPropertyName.VALUE_SEPARATOR;

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createSpreadsheetMetadata(decimalSeparator, dot).set(valueSeparator, dot)
        );

        this.checkEquals("Cannot set value-separator='.' duplicate of decimal-separator", thrown.getMessage(), "thrown message");
    }

    @Test
    public void testSetPropertyCharacterGroupseparatorAndValueSeparator() {
        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> value = SpreadsheetMetadataPropertyName.VALUE_SEPARATOR;
        final Character comma = ',';

        this.setAndCheck(
                this.createSpreadsheetMetadata(groupSeparator, dot, value, comma),
                value,
                dot,
                "{\n" +
                        "  \"group-separator\": \".\",\n" +
                        "  \"value-separator\": \".\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetPropertyCausesSwap() {
        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final Character comma = ',';

        this.setAndCheck(
                this.createSpreadsheetMetadata(decimalSeparator, dot, groupSeparator, comma),
                groupSeparator,
                dot,
                "{\n" +
                        "  \"decimal-separator\": \",\",\n" +
                        "  \"group-separator\": \".\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetPropertyCausesSwap2() {
        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> valueSeparator = SpreadsheetMetadataPropertyName.VALUE_SEPARATOR;
        final Character comma = ',';

        this.setAndCheck(
                this.createSpreadsheetMetadata(decimalSeparator, dot, valueSeparator, comma),
                decimalSeparator,
                comma,
                "{\n" +
                        "  \"decimal-separator\": \",\",\n" +
                        "  \"value-separator\": \".\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetPropertyCausesSwap3() {
        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final Character comma = ',';

        final SpreadsheetMetadataPropertyName<Character> positive = SpreadsheetMetadataPropertyName.POSITIVE_SIGN;
        final Character plus = '+';

        this.setAndCheck(
                this.createSpreadsheetMetadata(
                        decimalSeparator, dot,
                        groupSeparator, comma,
                        positive, plus
                ),
                groupSeparator,
                dot,
                "{\n" +
                        "  \"decimal-separator\": \",\",\n" +
                        "  \"group-separator\": \".\",\n" +
                        "  \"positive-sign\": \"+\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetPropertyCausesSwapGroupseparatorValueSeparator() {
        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final Character comma = ',';

        final SpreadsheetMetadataPropertyName<Character> positive = SpreadsheetMetadataPropertyName.POSITIVE_SIGN;
        final Character plus = '+';

        this.setAndCheck(
                this.createSpreadsheetMetadata(
                        decimalSeparator, dot,
                        groupSeparator, comma,
                        positive, plus
                ),
                groupSeparator,
                dot,
                "{\n" +
                        "  \"decimal-separator\": \",\",\n" +
                        "  \"group-separator\": \".\",\n" +
                        "  \"positive-sign\": \"+\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetPropertyCausesSwapTwice() {
        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final Character comma = ',';

        final SpreadsheetMetadataPropertyName<Character> positive = SpreadsheetMetadataPropertyName.POSITIVE_SIGN;
        final Character plus = '+';

        // start
        //  decimal=dot
        //  groupSeparator=comma
        //  positive=plus

        // set decimal=comma
        //  decimal=comma
        //  groupSeparator=dot (swap with decimal)
        //  positive=plus

        // set groupSeparator=plus
        //  decimal=comma
        //  groupSeparator=plus
        //  positive=dot

        this.checkEquals(
                this.createSpreadsheetMetadata(
                        decimalSeparator, comma,
                        groupSeparator, plus,
                        positive, dot
                ),
                this.createSpreadsheetMetadata(
                                decimalSeparator, dot,
                                groupSeparator, comma,
                                positive,
                                plus
                        ).set(decimalSeparator, comma)
                        .set(groupSeparator, plus)
        );
    }

    @Test
    public void testSetPropertyDefaultsCausesSwap() {
        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final Character comma = ',';

        final SpreadsheetMetadata defaults = this.createSpreadsheetMetadata(groupSeparator, comma);

        this.setAndCheck(
                this.createSpreadsheetMetadata(decimalSeparator, dot).setDefaults(defaults),
                decimalSeparator,
                comma,
                "{\n" +
                        "  \"_defaults\": {\n" +
                        "    \"group-separator\": \",\"\n" +
                        "  },\n" +
                        "  \"decimal-separator\": \",\",\n" +
                        "  \"group-separator\": \".\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetPropertyDefaultsCausesSwap2() {
        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final Character comma = ',';

        final SpreadsheetMetadata defaults = this.createSpreadsheetMetadata(
                decimalSeparator, dot,
                groupSeparator, comma
        );

        this.setAndCheck(
                this.createSpreadsheetMetadata(decimalSeparator, dot).setDefaults(defaults),
                decimalSeparator,
                comma,
                "{\n" +
                        "  \"_defaults\": {\n" +
                        "    \"decimal-separator\": \".\",\n" +
                        "    \"group-separator\": \",\"\n" +
                        "  },\n" +
                        "  \"decimal-separator\": \",\",\n" +
                        "  \"group-separator\": \".\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetPropertyDefaultsCausesSwap3() {
        final SpreadsheetMetadataPropertyName<EmailAddress> creator = SpreadsheetMetadataPropertyName.CREATOR;
        final EmailAddress email = EmailAddress.parse("creator@example.com");

        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final Character comma = ',';

        final SpreadsheetMetadata defaults = this.createSpreadsheetMetadata(decimalSeparator, dot, groupSeparator, comma);

        this.setAndCheck(
                this.createSpreadsheetMetadata(creator, email).setDefaults(defaults),
                decimalSeparator,
                comma,
                "{\n" +
                        "  \"_defaults\": {\n" +
                        "    \"decimal-separator\": \".\",\n" +
                        "    \"group-separator\": \",\"\n" +
                        "  },\n" +
                        "  \"creator\": \"creator@example.com\",\n" +
                        "  \"decimal-separator\": \",\",\n" +
                        "  \"group-separator\": \".\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetPropertyDefaultsCausesSwap4() {
        final SpreadsheetMetadataPropertyName<EmailAddress> creator = SpreadsheetMetadataPropertyName.CREATOR;
        final EmailAddress email = EmailAddress.parse("creator@example.com");

        final SpreadsheetMetadataPropertyName<Character> decimalSeparator = SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR;
        final Character dot = '.';

        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final Character comma = ',';

        final SpreadsheetMetadata defaults = this.createSpreadsheetMetadata(decimalSeparator, dot, groupSeparator, comma);

        this.setAndCheck(
                this.createSpreadsheetMetadata(creator, email).setDefaults(defaults),
                groupSeparator,
                dot,
                "{\n" +
                        "  \"_defaults\": {\n" +
                        "    \"decimal-separator\": \".\",\n" +
                        "    \"group-separator\": \",\"\n" +
                        "  },\n" +
                        "  \"creator\": \"creator@example.com\",\n" +
                        "  \"decimal-separator\": \",\",\n" +
                        "  \"group-separator\": \".\"\n" +
                        "}"
        );
    }

    @Test
    public void testSetDefaultAgain() {
        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final SpreadsheetMetadataPropertyName<Character> percent = SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL;
        final SpreadsheetMetadataPropertyName<Character> value = SpreadsheetMetadataPropertyName.VALUE_SEPARATOR;

        final SpreadsheetMetadata defaults = SpreadsheetMetadata.EMPTY.set(groupSeparator, ',').set(percent, '%').set(value, ',');

        this.setAndCheck(SpreadsheetMetadata.EMPTY.setDefaults(defaults),
                percent,
                '%',
                "{\n" +
                        "  \"_defaults\": {\n" +
                        "    \"group-separator\": \",\",\n" +
                        "    \"percentage-symbol\": \"%\",\n" +
                        "    \"value-separator\": \",\"\n" +
                        "  },\n" +
                        "  \"percentage-symbol\": \"%\"\n" +
                        "}");
    }

    @Test
    public void testSetDefaultGroupseparatorAgain() {
        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final SpreadsheetMetadataPropertyName<Character> percent = SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL;
        final SpreadsheetMetadataPropertyName<Character> value = SpreadsheetMetadataPropertyName.VALUE_SEPARATOR;

        final SpreadsheetMetadata defaults = SpreadsheetMetadata.EMPTY.set(groupSeparator, ',').set(percent, '%').set(value, ',');

        this.setAndCheck(SpreadsheetMetadata.EMPTY.setDefaults(defaults),
                groupSeparator,
                ',',
                "{\n" +
                        "  \"_defaults\": {\n" +
                        "    \"group-separator\": \",\",\n" +
                        "    \"percentage-symbol\": \"%\",\n" +
                        "    \"value-separator\": \",\"\n" +
                        "  },\n" +
                        "  \"group-separator\": \",\"\n" +
                        "}");
    }

    @Test
    public void testSetDefaultValueSeparatorAgain() {
        final SpreadsheetMetadataPropertyName<Character> groupSeparator = SpreadsheetMetadataPropertyName.GROUP_SEPARATOR;
        final SpreadsheetMetadataPropertyName<Character> percent = SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL;
        final SpreadsheetMetadataPropertyName<Character> value = SpreadsheetMetadataPropertyName.VALUE_SEPARATOR;

        final SpreadsheetMetadata defaults = SpreadsheetMetadata.EMPTY.set(groupSeparator, ',').set(percent, '%').set(value, ',');

        this.setAndCheck(SpreadsheetMetadata.EMPTY.setDefaults(defaults),
                value,
                ',',
                "{\n" +
                        "  \"_defaults\": {\n" +
                        "    \"group-separator\": \",\",\n" +
                        "    \"percentage-symbol\": \"%\",\n" +
                        "    \"value-separator\": \",\"\n" +
                        "  },\n" +
                        "  \"value-separator\": \",\"\n" +
                        "}");
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
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();

        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();

        this.removeAndCheck(this.createSpreadsheetMetadata(property1, this.value1(), property2, value2),
                property1,
                this.createSpreadsheetMetadata(property2, value2));
    }

    @Test
    public void testRemove2() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();

        this.removeAndCheck(this.createSpreadsheetMetadata(property1, value1, property2, this.value2()),
                property2,
                this.createSpreadsheetMetadata(property1, value1));
    }

    @Test
    public void testRemoveBecomesEmpty() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();

        this.removeAndCheck(this.createSpreadsheetMetadata(property1, value1),
                property1,
                SpreadsheetMetadata.EMPTY);
    }

    // set & remove ...................................................................................................

    @Test
    public void testSetSetRemoveRemove() {
        //set
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();
        final SpreadsheetMetadata metadata1 = this.setAndCheck(SpreadsheetMetadata.EMPTY,
                property1,
                value1,
                "{\n" +
                        "  \"create-date-time\": \"2000-01-02T12:58:59\"\n" +
                        "}"
        );

        //set
        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();
        final SpreadsheetMetadata metadata2 = this.setAndCheck(metadata1,
                property2,
                value2,
                "{\n" +
                        "  \"create-date-time\": \"2000-01-02T12:58:59\",\n" +
                        "  \"creator\": \"user@example.com\"\n" +
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
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();
        final SpreadsheetMetadata metadata1 = this.setAndCheck(SpreadsheetMetadata.EMPTY,
                property1,
                value1,
                "{\n" +
                        "  \"create-date-time\": \"2000-01-02T12:58:59\"\n" +
                        "}"
        );

        //set
        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();
        final SpreadsheetMetadata metadata2 = this.setAndCheck(metadata1,
                property2,
                value2,
                "{\n" +
                        "  \"create-date-time\": \"2000-01-02T12:58:59\",\n" +
                        "  \"creator\": \"user@example.com\"\n" +
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
                        "  \"create-date-time\": \"2000-01-02T12:58:59\",\n" +
                        "  \"creator\": \"user@example.com\"\n" +
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
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.numberedColor(2), color1)
                .set(SpreadsheetMetadataPropertyName.numberedColor(4), color2)
                .set(SpreadsheetMetadataPropertyName.namedColor(name1), 2)
                .set(SpreadsheetMetadataPropertyName.namedColor(name2), 4)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);

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
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.numberedColor(1), black)
                .set(SpreadsheetMetadataPropertyName.namedColor(blackName), 1)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);

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
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.numberedColor(12), red)
                .set(SpreadsheetMetadataPropertyName.namedColor(redName), 12)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);

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
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
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
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
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
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
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
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.numberedColor(2), color1)
                .set(SpreadsheetMetadataPropertyName.numberedColor(4), color2)
                .set(SpreadsheetMetadataPropertyName.namedColor(name1), 2)
                .set(SpreadsheetMetadataPropertyName.namedColor(name2), 4)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);

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
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.numberedColor(number1), color1)
                .set(SpreadsheetMetadataPropertyName.numberedColor(number7), color7)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
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
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
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
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
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
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.numberedColor(number1), color1)
                .set(SpreadsheetMetadataPropertyName.namedColor(colorName1), number1)
                .set(SpreadsheetMetadataPropertyName.numberedColor(number7), color7)
                .set(SpreadsheetMetadataPropertyName.namedColor(colorName7), number7)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
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
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
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
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
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
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);

        final SpreadsheetMetadata withDefaults = metadata.setDefaults(notEmpty);
        assertThrows(IllegalArgumentException.class, () -> withDefaults.setDefaults(withDefaults));
    }

    @Test
    public void testSetDefaultsCycleFails2() {
        final SpreadsheetMetadata metadata = this.createObject();
        final SpreadsheetMetadata notEmpty = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "$AUD");
        final SpreadsheetMetadata notEmpty2 = notEmpty.set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);

        final SpreadsheetMetadata withDefaults = metadata.setDefaults(notEmpty2);
        assertThrows(IllegalArgumentException.class, () -> withDefaults.setDefaults(withDefaults));
    }

    @Test
    public void testSetDefaultsTree() {
        final SpreadsheetMetadata metadata = this.createObject();
        final SpreadsheetMetadata notEmpty = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "$AUD");
        final SpreadsheetMetadata notEmpty2 = notEmpty.set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);

        final SpreadsheetMetadata withDefaults = metadata.setDefaults(notEmpty2);
        this.checkDefaults(withDefaults, notEmpty2);
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
                "Number 123.500");
    }

    @Test
    public void testConverterBigDecimalToString() {
        this.converterConvertAndCheck(
                BigDecimal.valueOf(123.5),
                "Number 123.500"
        );
    }

    @Test
    public void testConverterBigIntegerToString() {
        this.converterConvertAndCheck(
                BigInteger.valueOf(123),
                "Number 123.000"
        );
    }

    @Test
    public void testConverterByteToString() {
        this.converterConvertAndCheck(
                (byte) 123,
                "Number 123.000"
        );
    }

    @Test
    public void testConverterShortToString() {
        this.converterConvertAndCheck(
                (short) 123,
                "Number 123.000"
        );
    }

    @Test
    public void testConverterIntegerToString() {
        this.converterConvertAndCheck(
                123,
                "Number 123.000"
        );
    }

    @Test
    public void testConverterLongToString() {
        this.converterConvertAndCheck(
                123L,
                "Number 123.000"
        );
    }

    @Test
    public void testConverterFloatToString() {
        this.converterConvertAndCheck(
                123.5f,
                "Number 123.500"
        );
    }

    @Test
    public void testConverterDoubleToString() {
        this.converterConvertAndCheck(
                123.5,
                "Number 123.500"
        );
    }

    @Test
    public void testConverterStringToExpressionNumber() {
        this.converterConvertAndCheck(
                "Number 123.500",
                EXPRESSION_NUMBER_KIND.create(123.5)
        );
    }

    @Test
    public void testConverterStringToBigDecimal() {
        this.converterConvertAndCheck(
                "Number 123.500",
                BigDecimal.valueOf(123.5)
        );
    }

    @Test
    public void testConverterStringToBigInteger() {
        this.converterConvertAndCheck(
                "Number 123.000",
                BigInteger.valueOf(123)
        );
    }

    @Test
    public void testConverterStringToByte() {
        this.converterConvertAndCheck(
                "Number 123.000",
                (byte) 123
        );
    }

    @Test
    public void testConverterStringToShort() {
        this.converterConvertAndCheck(
                "Number 123.000",
                (short) 123
        );
    }

    @Test
    public void testConverterStringToInteger() {
        this.converterConvertAndCheck(
                "Number 123.000",
                123
        );
    }

    @Test
    public void testConverterStringToLong() {
        this.converterConvertAndCheck(
                "Number 123.000",
                123L
        );
    }

    @Test
    public void testConverterStringToFloat() {
        this.converterConvertAndCheck(
                "Number 123.500",
                123.5f
        );
    }

    @Test
    public void testConverterStringToDouble() {
        this.converterConvertAndCheck(
                "Number 123.500",
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
                        metadata,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER
                ),
                PROVIDER_CONTEXT
        );

        this.convertAndCheck3(
                value,
                expected,
                converter,
                SpreadsheetConverterContexts.basic(
                        converter,
                        LABEL_NAME_RESOLVER,
                        ExpressionNumberConverterContexts.basic(
                                Converters.fake(),
                                ConverterContexts.basic(
                                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                                        Converters.fake(),
                                        DateTimeContexts.locale(
                                                Locale.ENGLISH,
                                                DEFAULT_YEAR,
                                                20,
                                                NOW
                                        ),
                                        DecimalNumberContexts.american(MathContext.DECIMAL32)
                                ),
                                metadata.expressionNumberKind()
                        )
                )
        );
    }

    private SpreadsheetMetadata createSpreadsheetMetadataWithConverter(final SpreadsheetMetadataPropertyName<ConverterSelector> converterSelector) {
        return SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                .set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("\"Date\" yyyy mm dd").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("\"Date\" yyyy mm dd").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("\"DateTime\" yyyy hh").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("\"DateTime\" yyyy hh").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, DEFAULT_YEAR)
                .set(converterSelector, ConverterSelector.parse("general"))
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
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

    @Test
    public void testGeneralConvertWithConverterContext() {
        final SpreadsheetMetadata metadata = createSpreadsheetMetadataWithConverterAndConverterContext();

        this.convertAndCheck3(
                LocalTime.of(12, 58, 59),
                "Time 59 12",
                metadata.generalConverter(
                        SPREADSHEET_FORMATTER_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER,
                        PROVIDER_CONTEXT
                ),
                metadata.formulaSpreadsheetConverterContext(
                        NOW,
                        LABEL_NAME_RESOLVER,
                        SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                                metadata,
                                SPREADSHEET_FORMATTER_PROVIDER,
                                SPREADSHEET_PARSER_PROVIDER
                        ),
                        PROVIDER_CONTEXT
                )
        );
    }

    private final static String CURRENCY = "$AUD";
    private final static char DECIMAL_SEPARATOR = ',';
    private final static String EXPONENT_SYMBOL = "XPO";
    private final static char GROUP_SEPARATOR = '.';
    private final static char NEGATIVE_SIGN = '*';
    private final static char PERCENT = '$';
    private final static char POSITIVE_SIGN = '/';
    private final static char VALUE_SEPARATOR = '\'';

    private SpreadsheetMetadata createSpreadsheetMetadataWithConverterAndConverterContext() {
        return this.createSpreadsheetMetadataWithConverter(SpreadsheetMetadataPropertyName.FORMULA_CONVERTER)
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY)
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT_SYMBOL)
                .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, GROUP_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, NEGATIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, PERCENT)
                .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, POSITIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 16)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.DOWN);
    }

    // HasDateTimeContext...............................................................................................

    @Test
    public void testDateTimeContext() {
        Arrays.stream(Locale.getAvailableLocales())
                .forEach(l -> {
                            final int twoDigitYear = 49;
                            final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                                    .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, DEFAULT_YEAR)
                                    .set(SpreadsheetMetadataPropertyName.LOCALE, l)
                                    .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, twoDigitYear);

                    final DateFormatSymbols symbols = DateFormatSymbols.getInstance(l);
                    final DateTimeContext context = metadata.dateTimeContext(NOW);
                            this.amPmAndCheck(context, 13, symbols.getAmPmStrings()[1]);
                            this.monthNameAndCheck(context, 2, symbols.getMonths()[2]);
                            this.monthNameAbbreviationAndCheck(context, 3, symbols.getShortMonths()[3]);
                            this.twoDigitYearAndCheck(context, twoDigitYear);
                            this.weekDayNameAndCheck(context, 1, symbols.getWeekdays()[2]);
                            this.weekDayNameAbbreviationAndCheck(context, 3, symbols.getShortWeekdays()[4]);

                        }
                );
    }

    // HasDecimalNumberContext..........................................................................................

    @Test
    public void testDecimalNumberContextSomeRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY)
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT_SYMBOL)
                .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, GROUP_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, NEGATIVE_SIGN)
                .decimalNumberContext());
        this.checkEquals("Required properties \"locale\", \"percentage-symbol\", \"positive-sign\", \"precision\", \"rounding-mode\" missing.",
                thrown.getMessage(),
                "message");
    }

    @Test
    public void testDecimalNumberContextSomeRequiredPropertiesAbsentFails2() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "CS")
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT_SYMBOL)
                .decimalNumberContext());
        this.checkEquals("Required properties \"group-separator\", \"locale\", \"negative-sign\", \"percentage-symbol\", \"positive-sign\", \"precision\", \"rounding-mode\" missing.",
                thrown.getMessage(),
                "message");
    }

    @Test
    public void testDecimalNumberContextPropertiesPresent() {
        final String currencySymbol = "$AUD";
        final Character decimalSeparator = '.';
        final String exponentSymbol = "E";
        final Character groupSeparator = ',';
        final Character negativeSign = '-';
        final Character percentSymbol = '%';
        final Character positiveSign = '+';
        final Locale locale = Locale.forLanguageTag("EN-AU");

        Lists.of(MathContext.DECIMAL32, MathContext.DECIMAL64, MathContext.DECIMAL128, MathContext.UNLIMITED)
                .forEach(mc -> {
                    final int precision = mc.getPrecision();
                    final RoundingMode roundingMode = mc.getRoundingMode();

                    this.decimalNumberContextAndCheck(SpreadsheetMetadata.EMPTY
                                    .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, currencySymbol)
                                    .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, decimalSeparator)
                                    .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, exponentSymbol)
                                    .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, groupSeparator)
                                    .set(SpreadsheetMetadataPropertyName.LOCALE, locale)
                                    .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, negativeSign)
                                    .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, percentSymbol)
                                    .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, positiveSign)
                                    .set(SpreadsheetMetadataPropertyName.PRECISION, precision)
                                    .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, roundingMode),
                            currencySymbol,
                            decimalSeparator,
                            exponentSymbol,
                            groupSeparator,
                            locale,
                            negativeSign,
                            percentSymbol,
                            positiveSign,
                            precision,
                            roundingMode);
                });
    }

    private void decimalNumberContextAndCheck(final SpreadsheetMetadata metadata,
                                              final String currencySymbol,
                                              final Character decimalSeparator,
                                              final String exponentSymbol,
                                              final Character groupSeparator,
                                              final Locale locale,
                                              final Character negativeSign,
                                              final Character percentSymbol,
                                              final Character positiveSign,
                                              final int precision,
                                              final RoundingMode roundingMode) {
        final DecimalNumberContext context = metadata.decimalNumberContext();
        this.checkCurrencySymbol(context, currencySymbol);
        this.checkDecimalSeparator(context, decimalSeparator);
        this.checkExponentSymbol(context, exponentSymbol);
        this.checkGroupSeparator(context, groupSeparator);
        this.checkNegativeSign(context, negativeSign);
        this.checkPercentageSymbol(context, percentSymbol);
        this.checkPositiveSign(context, positiveSign);

        this.hasLocaleAndCheck(context, locale);
        this.hasMathContextAndCheck(context, new MathContext(precision, roundingMode));
    }

    @Test
    public void testDecimalNumberContextCached() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY)
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT_SYMBOL)
                .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, GROUP_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
                .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, NEGATIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, PERCENT)
                .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, POSITIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 16)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.FLOOR);
        assertSame(metadata.decimalNumberContext(), metadata.decimalNumberContext());
    }

    // EnvironmentContext...............................................................................................

    @Test
    public void testEnvironmentContext() {
        final EnvironmentContext context = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY)
                .environmentContext();
        this.checkEquals(
                Optional.of(CURRENCY),
                context.environmentValue(
                        EnvironmentValueName.with("metadata." + SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL)
                )
        );
    }

    // HasExpressionNumberContext.......................................................................................

    @Test
    public void testExpressionNumberContextSomeRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.DOUBLE)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 5)
                .expressionNumberContext());
        this.checkEquals("Required properties \"rounding-mode\" missing.",
                thrown.getMessage(),
                "message");
    }

    @Test
    public void testExpressionNumberContextSomeRequiredPropertiesAbsentFails2() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.DOUBLE)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.CEILING)
                .expressionNumberContext());
        this.checkEquals("Required properties \"precision\" missing.",
                thrown.getMessage(),
                "message");
    }

    @Test
    public void testExpressionNumberContext() {
        final ExpressionNumberKind kind = ExpressionNumberKind.DOUBLE;

        final ExpressionNumberContext context = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY)
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT_SYMBOL)
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, kind)
                .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, GROUP_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
                .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, NEGATIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, PERCENT)
                .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, POSITIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 16)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.CEILING)
                .expressionNumberContext();
        this.checkEquals(kind, context.expressionNumberKind(), "expressionNumberKind");
        this.checkNotEquals(null, context.mathContext(), "mathContext");
    }

    // HasFormatter.....................................................................................................

    @Test
    public void testFormatterFormatDate() {
        this.formatAndCheck2(LocalDate.of(2000, 12, 31), "Date 31122000");
    }

    @Test
    public void testFormatterFormatDateTime() {
        this.formatAndCheck2(LocalDateTime.of(2000, 12, 31, 12, 58, 59), "DateTime 31122000 125859");
    }

    @Test
    public void testFormatterFormatNumber() {
        this.formatAndCheck2(125.5, "Number 125.500");
    }

    @Test
    public void testFormatterFormatText() {
        this.formatAndCheck2("abc123", "Text abc123");
    }

    @Test
    public void testFormatterFormatTime() {
        this.formatAndCheck2(LocalTime.of(12, 58, 59), "Time 125859");
    }

    private void formatAndCheck2(final Object value,
                                 final String text) {
        this.formatAndCheck(
                this.createSpreadsheetMetadataWithFormatter()
                        .formatter(
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

                    private final DecimalNumberContext decimalNumberContext = DecimalNumberContexts.american(MathContext.UNLIMITED);
                },
                SpreadsheetText.with(text));
    }

    private SpreadsheetMetadata createSpreadsheetMetadataWithFormatter() {
        return SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("\"Date\" ddmmyyyy").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("\"DateTime\" ddmmyyyy hhmmss").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("\"Number\" #.000").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("\"Text\" @").spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.TIME_FORMATTER, SpreadsheetPattern.parseTimeFormatPattern("\"Time\" hhmmss").spreadsheetFormatterSelector());
    }

    // formatterContext.................................................................................................

    @Test
    public void testFormatterContext() {
        final SpreadsheetMetadata metadata = this.createSpreadsheetMetadataWithConverter(SpreadsheetMetadataPropertyName.FORMAT_CONVERTER)
                .set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 10)
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY)
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT_SYMBOL)
                .set(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT, 8)
                .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, GROUP_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
                .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, NEGATIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, PERCENT)
                .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, POSITIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 10)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.DOWN);

        this.checkNotEquals(
                null,
                metadata.formatterContext(
                        NOW,
                        LABEL_NAME_RESOLVER,
                        SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                                metadata,
                                SPREADSHEET_FORMATTER_PROVIDER,
                                SPREADSHEET_PARSER_PROVIDER
                        ),
                        SPREADSHEET_FORMATTER_PROVIDER,
                        PROVIDER_CONTEXT
                )
        );
    }

    // SpreadsheetFormatterProviderSamplesContext.......................................................................

    @Test
    public void testSpreadsheetFormatterProviderSamplesContext() {
        final SpreadsheetMetadata metadata = this.createSpreadsheetMetadataWithConverter(SpreadsheetMetadataPropertyName.FORMAT_CONVERTER)
                .set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 10)
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY)
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT_SYMBOL)
                .set(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT, 8)
                .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, GROUP_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
                .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, NEGATIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, PERCENT)
                .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, POSITIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 10)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.DOWN);

        this.checkNotEquals(
                null,
                metadata.spreadsheetFormatterProviderSamplesContext(
                        NOW,
                        LABEL_NAME_RESOLVER,
                        SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                                metadata,
                                SPREADSHEET_FORMATTER_PROVIDER,
                                SPREADSHEET_PARSER_PROVIDER
                        ),
                        SPREADSHEET_FORMATTER_PROVIDER,
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
        this.checkEquals("Required properties \"rounding-mode\" missing.",
                thrown.getMessage(),
                "message");
    }

    @Test
    public void testUnmarshallContextSomeRequiredPropertiesAbsentFails2() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.DOUBLE)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.CEILING)
                .jsonNodeUnmarshallContext());
        this.checkEquals("Required properties \"precision\" missing.",
                thrown.getMessage(),
                "message");
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
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> SpreadsheetMetadataNonEmpty.with(Maps.of(SpreadsheetMetadataPropertyName.PRECISION, 1), SpreadsheetMetadata.EMPTY)
                .mathContext());
        this.checkMessage(thrown, "Required properties \"rounding-mode\" missing.");
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
    public void testParserMissingProperties() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                .set(
                        SpreadsheetMetadataPropertyName.DATE_PARSER,
                        SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd").spreadsheetParserSelector()
                );
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> metadata.parser(
                        SPREADSHEET_PARSER_PROVIDER,
                        PROVIDER_CONTEXT
                )
        );
        this.checkEquals(
                "Required properties \"date-time-parser\", \"number-parser\", \"time-parser\" missing.",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testParserAndParseDate() {
        this.metadataParserParseAndCheck(
                "2000/12/31",
                (t, c) -> t.cast(SpreadsheetDateParserToken.class).toLocalDate(c),
                LocalDate.of(2000, 12, 31)
        );
    }

    @Test
    public void testParserAndParseDateTime() {
        this.metadataParserParseAndCheck(
                "2000/12/31 15:58",
                (t, c) -> t.cast(SpreadsheetDateTimeParserToken.class).toLocalDateTime(c),
                LocalDateTime.of(
                        LocalDate.of(2000, 12, 31),
                        LocalTime.of(15, 58)
                )
        );
    }

    @Test
    public void testParserAndParseNumber() {
        this.metadataParserParseAndCheck(
                "1" + DECIMAL_SEPARATOR + "5",
                (t, c) -> t.cast(SpreadsheetNumberParserToken.class).toNumber(c),
                EXPRESSION_NUMBER_KIND.create(1.5)
        );
    }

    @Test
    public void testParserAndParseTime() {
        this.metadataParserParseAndCheck(
                "15:58",
                (t, c) -> t.cast(SpreadsheetTimeParserToken.class).toLocalTime(),
                LocalTime.of(15, 58)
        );
    }

    private SpreadsheetMetadata metadataWithParser() {
        return SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("#.#").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("hh:mm").spreadsheetParserSelector());
    }

    private <T> void metadataParserParseAndCheck(final String text,
                                                 final BiFunction<ParserToken, ExpressionEvaluationContext, T> valueExtractor,
                                                 final T expected) {
        final TextCursor cursor = TextCursors.charSequence(text);

        final ParserToken token = this.metadataWithParser()
                .parser(
                        SPREADSHEET_PARSER_PROVIDER,
                        PROVIDER_CONTEXT
                ).parse(
                        cursor,
                        this.metadataWithParserContext()
                                .parserContext(NOW)
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
    public void testParserContext() {
        final SpreadsheetMetadata metadata = this.metadataWithParserContext();

        this.checkNotEquals(
                null,
                metadata.parserContext(NOW)
        );
    }

    private SpreadsheetMetadata metadataWithParserContext() {
        return SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY)
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, DEFAULT_YEAR)
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT_SYMBOL)
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
                .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, GROUP_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
                .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, NEGATIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, PERCENT)
                .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, POSITIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 10)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.DOWN)
                .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 20)
                .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, VALUE_SEPARATOR);
    }

    // missingRequiredProperties.........................................................................................

    @Test
    public void testMissingProperties() {
        this.missingRequiredPropertiesAndCheck(SpreadsheetMetadata.EMPTY.set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "$"),
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

    @Test
    public void testMissingPropertiesNonMissing() {
        this.missingRequiredPropertiesAndCheck(SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("creator@example.com"))
                .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.now())
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
                .set(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("modified@example.com"))
                .set(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.now()));
    }

    @Test
    public void testMissingPropertiesSomeMissing() {
        this.missingRequiredPropertiesAndCheck(SpreadsheetMetadata.EMPTY
                        .set(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("creator@example.com"))
                        .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.now())
                        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH),
                SpreadsheetMetadataPropertyName.MODIFIED_BY,
                SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME);
    }

    @Test
    public void testMissingPropertiesSomeMissing2() {
        this.missingRequiredPropertiesAndCheck(SpreadsheetMetadata.EMPTY
                        .set(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("creator@example.com"))
                        .set(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.now()),
                SpreadsheetMetadataPropertyName.CREATOR,
                SpreadsheetMetadataPropertyName.CREATE_DATE_TIME,
                SpreadsheetMetadataPropertyName.LOCALE);
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
                        "  \"create-date-time\": \"2000-01-02T12:58:59\",\n" +
                        "  \"creator\": \"user@example.com\"\n" +
                        "}"
        );
    }

    @Test
    public void testToStringCharacterValue() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '.');
        map.put(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("modified@example.com"));

        this.toStringAndCheck(
                SpreadsheetMetadataNonEmpty.with(map, null),
                "{\n" +
                        "  \"decimal-separator\": \".\",\n" +
                        "  \"modified-by\": \"modified@example.com\"\n" +
                        "}"
        );
    }

    @Test
    public void testToStringStringValue() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "AUD");
        map.put(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("modified@example.com"));

        this.toStringAndCheck(
                SpreadsheetMetadataNonEmpty.with(map, null),
                "{\n" +
                        "  \"currency-symbol\": \"AUD\",\n" +
                        "  \"modified-by\": \"modified@example.com\"\n" +
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
                                        SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH),
                                null
                        )
                ),
                "{\n" +
                        "  \"_defaults\": {\n" +
                        "    \"locale\": \"en\"\n" +
                        "  },\n" +
                        "  \"create-date-time\": \"2000-01-02T12:58:59\",\n" +
                        "  \"creator\": \"user@example.com\"\n" +
                        "}"
        );
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Test
    public void testUnmarshallInvalidCharacterValueFails() {
        this.unmarshallFails(
                "{" +
                        "  \"decimal-separator\": \"d\"\n" +
                        "}",
                SpreadsheetMetadata.class
        );
    }

    @Test
    public void testUnmarshallWithDefaultSwap() {
        this.unmarshallAndCheck(
                "{\n" +
                        "    \"decimal-separator\": \",\",\n" +
                        "    \"_defaults\": {\n" +
                        "        \"decimal-separator\": \".\",\n" +
                        "        \"group-separator\": \",\"\n" +
                        "    }\n" +
                        "}",
                SpreadsheetMetadata.EMPTY
                        .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, ',')
                        .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, '.')
                        .setDefaults(SpreadsheetMetadata.EMPTY
                                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '.')
                                .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, ',')
                        )
        );
    }

    /**
     * This test verifies that all {@link SpreadsheetMetadataPropertyName} value types are also
     * {@link walkingkooka.tree.json.marshall.JsonNodeContext} registered.
     */
    @Test
    public void testUnmarshall() {
        final JsonNode json = JsonNode.parse("{\n" +
                "  \"cell-character-width\": 0,\n" +
                "  \"color-1\": \"#000001\",\n" +
                "  \"color-10\": \"#00000a\",\n" +
                "  \"color-11\": \"#00000b\",\n" +
                "  \"color-12\": \"#00000c\",\n" +
                "  \"color-13\": \"#00000d\",\n" +
                "  \"color-14\": \"#00000e\",\n" +
                "  \"color-15\": \"#00000f\",\n" +
                "  \"color-16\": \"#000010\",\n" +
                "  \"color-17\": \"#000011\",\n" +
                "  \"color-18\": \"#000012\",\n" +
                "  \"color-19\": \"#000013\",\n" +
                "  \"color-2\": \"#000002\",\n" +
                "  \"color-20\": \"#000014\",\n" +
                "  \"color-21\": \"#000015\",\n" +
                "  \"color-22\": \"#000016\",\n" +
                "  \"color-23\": \"#000017\",\n" +
                "  \"color-24\": \"#000018\",\n" +
                "  \"color-25\": \"#000019\",\n" +
                "  \"color-26\": \"#00001a\",\n" +
                "  \"color-27\": \"#00001b\",\n" +
                "  \"color-28\": \"#00001c\",\n" +
                "  \"color-29\": \"#00001d\",\n" +
                "  \"color-3\": \"#000003\",\n" +
                "  \"color-30\": \"#00001e\",\n" +
                "  \"color-31\": \"#00001f\",\n" +
                "  \"color-32\": \"#000020\",\n" +
                "  \"color-33\": \"#000021\",\n" +
                "  \"color-4\": \"#000004\",\n" +
                "  \"color-5\": \"#000005\",\n" +
                "  \"color-6\": \"#000006\",\n" +
                "  \"color-7\": \"#000007\",\n" +
                "  \"color-8\": \"#000008\",\n" +
                "  \"color-9\": \"#000009\",\n" +
                "  \"color-big\": 1,\n" +
                "  \"color-medium\": 2,\n" +
                "  \"color-small\": 3,\n" +
                "  \"create-date-time\": \"2000-12-31T12:58:59\",\n" +
                "  \"creator\": \"creator@example.com\",\n" +
                "  \"currency-symbol\": \"$AUD\",\n" +
                "  \"date-formatter\": \"date-format-pattern DD/MM/YYYY\",\n" +
                "  \"date-parser\": \"date-parse-pattern DD/MM/YYYY;DDMMYYYY\",\n" +
                "  \"date-time-formatter\": \"date-time-format-pattern DD/MM/YYYY hh:mm\",\n" +
                "  \"date-time-offset\": \"0\",\n" +
                "  \"date-time-parser\": \"date-time-pattern DD/MM/YYYY hh:mm;DDMMYYYYHHMM;DDMMYYYY HHMM\",\n" +
                "  \"decimal-separator\": \".\",\n" +
                "  \"default-year\": 1901,\n" +
                "  \"exponent-symbol\": \"E\",\n" +
                "  \"group-separator\": \",\",\n" +
                "  \"hide-zero-values\": true,\n" +
                "  \"locale\": \"en\",\n" +
                "  \"modified-by\": \"modified@example.com\",\n" +
                "  \"modified-date-time\": \"1999-12-31T12:58:59\",\n" +
                "  \"negative-sign\": \"-\",\n" +
                "  \"number-formatter\": \"number-format-pattern #0.0\",\n" +
                "  \"number-parser\": \"number-parse-pattern #0.0;$#0.00\",\n" +
                "  \"percentage-symbol\": \"%\",\n" +
                "  \"positive-sign\": \"+\",\n" +
                "  \"precision\": 123,\n" +
                "  \"rounding-mode\": \"FLOOR\",\n" +
                "  \"spreadsheet-id\": \"7b\",\n" +
                "  \"text-formatter\": \"text-format-pattern @@\",\n" +
                "  \"time-formatter\": \"time-format-pattern hh:mm\",\n" +
                "  \"time-parser\": \"time-parse-pattern hh:mm;hh:mm:ss.000\",\n" +
                "  \"two-digit-year\": 31\n" +
                "}");
        final SpreadsheetMetadata metadata = this.unmarshall(json);
        this.checkNotEquals(metadata, SpreadsheetMetadata.EMPTY);
    }

    @Test
    public void testMarshallRoundtrip() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> properties = Maps.ordered();

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
                SpreadsheetMetadataPropertyName.CONVERTERS,
                ConverterInfoSet.parse(SpreadsheetConvertersConverterProviders.BASE_URL + "/General general")
        );
        properties.put(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.of(2000, 12, 31, 12, 58, 59));
        properties.put(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("creator@example.com"));
        properties.put(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "$AUD");
        properties.put(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("DD/MM/YYYY").spreadsheetFormatterSelector());
        properties.put(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("DD/MM/YYYY;DDMMYYYY").spreadsheetParserSelector());
        properties.put(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET);
        properties.put(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("DD/MM/YYYY hh:mm").spreadsheetFormatterSelector());
        properties.put(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("DD/MM/YYYY hh:mm;DDMMYYYYHHMM;DDMMYYYY HHMM").spreadsheetParserSelector());
        properties.put(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL_SEPARATOR);
        properties.put(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 1901);
        properties.put(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.BIG_DECIMAL);
        properties.put(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT_SYMBOL);
        properties.put(
                SpreadsheetMetadataPropertyName.FIND_FUNCTIONS,
                ConverterSelector.parse("find-something-something")
        );
        properties.put(
                SpreadsheetMetadataPropertyName.FORMAT_CONVERTER,
                ConverterSelector.parse("general")
        );
        properties.put(
                SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
                ConverterSelector.parse("general")
        );
        properties.put(
                SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS,
                ConverterSelector.parse("hello")
        );
        properties.put(SpreadsheetMetadataPropertyName.FROZEN_COLUMNS, SpreadsheetSelection.parseColumnRange("A:B"));
        properties.put(SpreadsheetMetadataPropertyName.FROZEN_ROWS, SpreadsheetSelection.parseRowRange("1:2"));
        properties.put(
                SpreadsheetMetadataPropertyName.FUNCTIONS,
                ExpressionFunctionInfoSet.with(Sets.empty())
        );
        properties.put(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, GROUP_SEPARATOR);
        properties.put(SpreadsheetMetadataPropertyName.HIDE_ZERO_VALUES, true);
        properties.put(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);
        properties.put(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("modified@example.com"));
        properties.put(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.of(1999, 12, 31, 12, 58, 59));
        properties.put(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, NEGATIVE_SIGN);
        properties.put(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("#0.0").spreadsheetFormatterSelector());
        properties.put(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT, 8);
        properties.put(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("#0.0;$#0.00").spreadsheetParserSelector());
        properties.put(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, PERCENT);
        properties.put(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, POSITIVE_SIGN);
        properties.put(SpreadsheetMetadataPropertyName.PRECISION, 123);
        properties.put(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.FLOOR);
        properties.put(
                SpreadsheetMetadataPropertyName.SORT_COMPARATORS,
                ConverterSelector.parse("day-of-month")
        );
        properties.put(
                SpreadsheetMetadataPropertyName.SORT_CONVERTER,
                ConverterSelector.parse("general")
        );
        properties.put(
                SpreadsheetMetadataPropertyName.COMPARATORS,
                SpreadsheetComparatorProviders.spreadsheetComparators().spreadsheetComparatorInfos()
        );
        properties.put(
                SpreadsheetMetadataPropertyName.EXPORTERS,
                SpreadsheetExporterInfoSet.EMPTY
        );
        properties.put(
                SpreadsheetMetadataPropertyName.FORMATTERS,
                SpreadsheetFormatterProviders.spreadsheetFormatPattern()
                        .spreadsheetFormatterInfos()
        );
        properties.put(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(123));
        properties.put(
                SpreadsheetMetadataPropertyName.IMPORTERS,
                SpreadsheetImporterInfoSet.EMPTY
        );
        properties.put(SpreadsheetMetadataPropertyName.SPREADSHEET_NAME, SpreadsheetName.with("Spreadsheet-name-123"));
        properties.put(
                SpreadsheetMetadataPropertyName.PARSERS,
                SPREADSHEET_PARSER_PROVIDER.spreadsheetParserInfos()
        );
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
        properties.put(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, VALUE_SEPARATOR);
        properties.put(
                SpreadsheetMetadataPropertyName.VIEWPORT,
                SpreadsheetSelection.parseCell("B99")
                        .viewportRectangle(100, 50)
                        .viewport());

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
                        .set(SpreadsheetMetadataPropertyName.VIEWPORT, SpreadsheetSelection.parseCell("D4")
                                .viewportRectangle(100, 50)
                                .viewport()
                                .setAnchoredSelection(
                                        Optional.of(
                                                SpreadsheetSelection.parseCell("E5")
                                                        .setDefaultAnchor()
                                        )
                                )),
                "spreadsheet-id: 4d2\n" +
                        "frozen-columns: column-range A:C\n" +
                        "frozen-rows: row-range 1:3\n" +
                        "spreadsheet-name: Untitled\n" +
                        "viewport: rectangle:\n" +
                        "  home: D4\n" +
                        "  width: 100.0\n" +
                        "  height: 50.0\n" +
                        "anchoredSelection: cell E5\n"
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

    @SuppressWarnings("SameReturnValue")
    private SpreadsheetMetadataPropertyName<EmailAddress> property3() {
        return SpreadsheetMetadataPropertyName.MODIFIED_BY;
    }

    private EmailAddress value3() {
        return EmailAddress.parse("different@example.com");
    }

    @Override
    Class<SpreadsheetMetadataNonEmpty> metadataType() {
        return SpreadsheetMetadataNonEmpty.class;
    }

}
