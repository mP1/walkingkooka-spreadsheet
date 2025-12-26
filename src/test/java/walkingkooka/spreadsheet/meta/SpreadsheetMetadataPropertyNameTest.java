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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.naming.NameTesting;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.ConstantsTesting;
import walkingkooka.reflect.FieldAttributes;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.color.SpreadsheetColors;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.pattern.HasSpreadsheetPatternKindTesting;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.spreadsheet.store.SpreadsheetCellStoreAction;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.lang.reflect.Field;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataPropertyNameTest extends SpreadsheetMetadataTestCase2<SpreadsheetMetadataPropertyName<?>>
    implements NameTesting<SpreadsheetMetadataPropertyName<?>, SpreadsheetMetadataPropertyName<?>>,
    ConstantsTesting<SpreadsheetMetadataPropertyName<?>>,
    HasSpreadsheetPatternKindTesting {

    @Test
    public void testWithUnknownConstantFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetMetadataPropertyName.with("unknown1234567")
        );
    }

    @Test
    public void testWithDefaultsSpecialInternalConstantFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetMetadataPropertyName.with("_defaults")
        );
    }

    @Test
    public void testWithConstants() {
        this.checkEquals(
            Lists.empty(),
            Arrays.stream(SpreadsheetMetadataPropertyName.class.getDeclaredFields())
                .filter(FieldAttributes.STATIC::is)
                .filter(f -> f.getType() == SpreadsheetMetadataPropertyName.class)
                .filter(SpreadsheetMetadataPropertyNameTest::constantNotCached)
                .collect(Collectors.toList()),
            ""
        );
    }

    private static boolean constantNotCached(final Field field) {
        try {
            final SpreadsheetMetadataPropertyName<?> name = Cast.to(field.get(null));
            return name != SpreadsheetMetadataPropertyName.with(name.value());
        } catch (final Exception cause) {
            throw new AssertionError(cause.getMessage(), cause);
        }
    }

    @Test
    public void testWithColorPropertyMissingNumberOrNameFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetMetadataPropertyName.with("color-")
        );
    }

    @Test
    public void testWithColorPropertyInvalidFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetMetadataPropertyName.with("color-!")
        );
    }

    @Test
    public void testWithColorName() {
        final int colorNumber = 12;

        Stream.of("big", "medium", "small")
            .forEach(i -> {
                    final String value = "color" + i;
                    final SpreadsheetMetadataPropertyName<?> propertyName = SpreadsheetMetadataPropertyName.with(value);
                    this.checkEquals(SpreadsheetMetadataPropertyNameIntegerNamedColor.class, propertyName.getClass(), "class name");
                    this.checkEquals(value, propertyName.value(), "value");

                    propertyName.checkValue(colorNumber);
                }
            );
    }

    @Test
    public void testWithColorNumber() {
        final Color color = Color.fromRgb(0);

        IntStream.range(SpreadsheetColors.MAX, SpreadsheetColors.MAX)
            .forEach(i -> {
                    final String value = "color" + i;
                    final SpreadsheetMetadataPropertyName<?> propertyName = SpreadsheetMetadataPropertyName.with(value);
                    this.checkEquals(SpreadsheetMetadataPropertyNameNumberedColor.class, propertyName.getClass(), "class name");
                    this.checkEquals(value, propertyName.value(), "value");

                    propertyName.checkValue(color);
                }
            );
    }

    // tryWith..........................................................................................................

    @Test
    public void testTryWithUnknown() {
        this.checkEquals(
            Optional.empty(),
            SpreadsheetMetadataPropertyName.tryWith("???")
        );
    }

    // fromEnvironmentValueName.........................................................................................

    @Test
    public void testFromEnvironmentValueNameWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataPropertyName.fromEnvironmentValueName(null)
        );
    }

    @Test
    public void testFromEnvironmentValueNameWithUnknownFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetMetadataPropertyName.fromEnvironmentValueName(
                EnvironmentValueName.with(
                    "Unknown123",
                    Void.class
                )
            )
        );

        this.checkEquals(
            "Unknown metadata property name \"Unknown123\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testFromEnvironmentValueNameWithExpressionNumberKindAllLowerCase() {
        this.fromEnvironmentValueNameAndCheck(
            EnvironmentValueName.with(
                "expressionnumberkind",
                ExpressionNumberKind.class
            ),
            SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND
        );
    }

    @Test
    public void testFromEnvironmentValueNameWithExpressionNumberKindAllUpperCase() {
        this.fromEnvironmentValueNameAndCheck(
            EnvironmentValueName.with(
                "EXPRESSIONNUMBERKIND",
                ExpressionNumberKind.class
            ),
            SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND
        );
    }

    @Test
    public void testFromEnvironmentValueNameWithDecimalNumberSymbols() {
        this.fromEnvironmentValueNameAndCheck(
            SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS
        );
    }

    @Test
    public void testFromEnvironmentValueNameWithLocale() {
        this.fromEnvironmentValueNameAndCheck(
            EnvironmentValueName.LOCALE,
            SpreadsheetMetadataPropertyName.LOCALE
        );
    }

    @Test
    public void testFromEnvironmentValueNameWithRoundingMode() {
        System.out.println(SpreadsheetMetadataPropertyName.ROUNDING_MODE);
        this.fromEnvironmentValueNameAndCheck(
            SpreadsheetMetadataPropertyName.ROUNDING_MODE
        );
    }

    private <T> void fromEnvironmentValueNameAndCheck(final SpreadsheetMetadataPropertyName<T> name) {
        this.fromEnvironmentValueNameAndCheck(
            EnvironmentValueName.with(
                name.value(),
                name.type()
            ),
            name
        );
    }

    private <T> void fromEnvironmentValueNameAndCheck(final EnvironmentValueName<T> name,
                                                      final SpreadsheetMetadataPropertyName<T> expected) {
        this.checkEquals(
            expected,
            SpreadsheetMetadataPropertyName.fromEnvironmentValueName(name)
        );
    }

    // toConverterSelector..............................................................................................

    @Test
    public void testToConverterSelector() {
        final SpreadsheetMetadataPropertyName<ConverterSelector> propertyName = SpreadsheetMetadataPropertyName.FORMULA_CONVERTER;

        assertSame(
            propertyName,
            propertyName.toConverterSelector()
        );
    }

    @Test
    public void testToConverterSelectorWithNotExpressionFunctionAliasSet() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetMetadataPropertyName.LOCALE.toConverterSelector()
        );

        this.checkEquals(
            "Property locale: invalid type Locale expected ExpressionFunctionAliasSet",
            thrown.getMessage()
        );
    }

    @Test
    public void testToConverterSelectorWithFindFunctions() {
        this.checkEquals(
            SpreadsheetMetadataPropertyName.FIND_CONVERTER,
            SpreadsheetMetadataPropertyName.FIND_FUNCTIONS.toConverterSelector()
        );
    }

    @Test
    public void testToConverterSelectorWithFormattingFunctions() {
        this.checkEquals(
            SpreadsheetMetadataPropertyName.FORMATTING_CONVERTER,
            SpreadsheetMetadataPropertyName.FORMATTING_FUNCTIONS.toConverterSelector()
        );
    }

    @Test
    public void testToConverterSelectorWithAllExpressionFunctionAliasSet() {
        for (final SpreadsheetMetadataPropertyName<?> propertyName : SpreadsheetMetadataPropertyName.ALL) {
            if (propertyName != SpreadsheetMetadataPropertyName.FUNCTIONS && propertyName.type() == ExpressionFunctionAliasSet.class) {
                this.checkNotEquals(
                    null,
                    propertyName.toConverterSelector()
                );
            }
        }
    }

    // patch............................................................................................................

    @Test
    public void testPatchNonNullValue() {
        this.patchAndCheck(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            AuditInfo.with(
                EmailAddress.parse("created@example.com"),
                LocalDateTime.MIN,
                EmailAddress.parse("modified@example.com"),
                LocalDateTime.MAX
            ),
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

    @Test
    public void testPatchNonNullValue2() {
        this.patchAndCheck(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            AuditInfo.with(
                EmailAddress.parse("after@example.com"),
                LocalDateTime.MIN,
                EmailAddress.parse("after@example.com"),
                LocalDateTime.MAX
            ),
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.with(
                    EmailAddress.parse("before@example.com"),
                    LocalDateTime.MIN,
                    EmailAddress.parse("before@example.com"),
                    LocalDateTime.MAX
                )
            ),
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.with(
                    EmailAddress.parse("after@example.com"),
                    LocalDateTime.MIN,
                    EmailAddress.parse("after@example.com"),
                    LocalDateTime.MAX
                )
            )
        );
    }

    @Test
    public void testPatchNullValue() {
        this.patchAndCheck(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            null,
            JsonNode.object()
                .setNull(
                    JsonPropertyName.with("auditInfo")
                )
        );
    }

    @Test
    public void testPatchNullValue2() {
        this.patchAndCheck(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            null,
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.with(
                    EmailAddress.parse("user@example.com"),
                    LocalDateTime.MIN,
                    EmailAddress.parse("user@example.com"),
                    LocalDateTime.MAX
                )
            ),
            SpreadsheetMetadata.EMPTY
        );
    }

    @Test
    public void testPatchNullValue3() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);

        this.patchAndCheck(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            null,
            metadata.set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.with(
                    EmailAddress.parse("created@example.com"),
                    LocalDateTime.MIN,
                    EmailAddress.parse("modified@example.com"),
                    LocalDateTime.MAX
                )
            ),
            metadata
        );
    }

    private <T> void patchAndCheck(final SpreadsheetMetadataPropertyName<T> propertyName,
                                   final T value,
                                   final String expected) {
        this.patchAndCheck(
            propertyName,
            value,
            JsonNode.parse(expected)
        );
    }

    private <T> void patchAndCheck(final SpreadsheetMetadataPropertyName<T> propertyName,
                                   final T value,
                                   final JsonNode expected) {
        this.checkEquals(
            expected,
            propertyName.patch(value),
            () -> propertyName + " patch " + value
        );
    }

    private <T> void patchAndCheck(final SpreadsheetMetadataPropertyName<T> propertyName,
                                   final T value,
                                   final SpreadsheetMetadata initial,
                                   final SpreadsheetMetadata expected) {
        this.checkEquals(
            expected,
            initial.patch(
                propertyName.patch(value),
                JsonNodeUnmarshallContexts.basic(
                    ExpressionNumberKind.BIG_DECIMAL,
                    MathContext.DECIMAL32
                )
            ),
            () -> initial + " patch " + propertyName + " patch " + value
        );
    }

    // Comparator ......................................................................................................

    @Override
    public void testCompareDifferentCase() {
    }

    @Test
    public void testSortSpreadsheetIdFirst() {
        final SpreadsheetMetadataPropertyName<?> audit = SpreadsheetMetadataPropertyName.AUDIT_INFO;
        final SpreadsheetMetadataPropertyName<?> hideZeroValues = SpreadsheetMetadataPropertyName.HIDE_ZERO_VALUES;
        final SpreadsheetMetadataPropertyName<?> spreadsheetId = SpreadsheetMetadataPropertyName.SPREADSHEET_ID;

        //noinspection unchecked
        this.compareToArraySortAndCheck(
            spreadsheetId, hideZeroValues, audit,
            spreadsheetId, audit, hideZeroValues
        );
    }

    @Test
    public void testSortNumberedColours() {
        final SpreadsheetMetadataPropertyName<?> audit = SpreadsheetMetadataPropertyName.AUDIT_INFO;
        final SpreadsheetMetadataPropertyName<?> color10 = SpreadsheetMetadataPropertyName.numberedColor(10);
        final SpreadsheetMetadataPropertyName<?> color2 = SpreadsheetMetadataPropertyName.numberedColor(2);
        final SpreadsheetMetadataPropertyName<?> color3 = SpreadsheetMetadataPropertyName.numberedColor(3);

        //noinspection unchecked
        this.compareToArraySortAndCheck(
            color3, color2, audit, color10,
            audit, color2, color3, color10
        );
    }

    @Test
    public void testSortSpreadsheetIdNumberedColours() {
        final SpreadsheetMetadataPropertyName<?> spreadsheetId = SpreadsheetMetadataPropertyName.SPREADSHEET_ID;
        final SpreadsheetMetadataPropertyName<?> audit = SpreadsheetMetadataPropertyName.AUDIT_INFO;
        final SpreadsheetMetadataPropertyName<?> color10 = SpreadsheetMetadataPropertyName.numberedColor(10);
        final SpreadsheetMetadataPropertyName<?> color2 = SpreadsheetMetadataPropertyName.numberedColor(2);
        final SpreadsheetMetadataPropertyName<?> color3 = SpreadsheetMetadataPropertyName.numberedColor(3);

        //noinspection unchecked
        this.compareToArraySortAndCheck(
            color3, color2, audit, spreadsheetId, color10,
            spreadsheetId, audit, color2, color3, color10
        );
    }

    // parseUrlFragmentSaveValue.......................................................................................................

    @Test
    public void testParseUrlFragmentSaveValueColor() {
        final Color color = Color.parse("#123456");

        this.checkEquals(
            color,
            SpreadsheetMetadataPropertyName.numberedColor(1)
                .parseUrlFragmentSaveValue(color.toString())
        );
    }

    @Test
    public void testParseUrlFragmentSaveValueAuditInfoFails() {
        this.parseValueFails(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            AuditInfo.with(
                EmailAddress.parse("created@example.com"),
                LocalDateTime.MIN,
                EmailAddress.parse("modified@example.com"),
                LocalDateTime.MAX
            )
        );
    }

    @Test
    public void testParseUrlFragmentSaveValueSpreadsheetIdFails() {
        this.parseValueFails(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            SpreadsheetId.parse("123abc")
        );
    }

    @Test
    public void testParseUrlFragmentSaveValueStyleFails() {
        this.parseValueFails(
            SpreadsheetMetadataPropertyName.STYLE,
            "style"
        );
    }

    private <T> void parseValueFails(final SpreadsheetMetadataPropertyName<T> propertyName,
                                     final T propertyValue) {
        this.parseValueFails(
            propertyName,
            propertyValue.toString()
        );
    }

    private void parseValueFails(final SpreadsheetMetadataPropertyName<?> propertyName,
                                 final String propertyValue) {
        assertThrows(
            UnsupportedOperationException.class,
            () -> propertyName.parseUrlFragmentSaveValue(propertyValue)
        );
    }

    @Test
    public void testParseUrlFragmentSaveValueSpreadsheetName() {
        final String value = "SpreadsheetName123";

        this.checkEquals(
            SpreadsheetName.with(value),
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME.parseUrlFragmentSaveValue(value)
        );
    }

    // spreadsheetCellStoreAction.......................................................................................

    @Test
    public void testSpreadsheetCellStoreActionSpreadsheetId() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            SpreadsheetCellStoreAction.NONE
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionSpreadsheetName() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
            SpreadsheetCellStoreAction.NONE
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionAuditInfo() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            SpreadsheetCellStoreAction.NONE
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionFrozenColumns() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.FROZEN_COLUMNS,
            SpreadsheetCellStoreAction.NONE
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionFrozenRows() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.FROZEN_ROWS,
            SpreadsheetCellStoreAction.NONE
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionViewportHome() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.VIEWPORT_HOME,
            SpreadsheetCellStoreAction.EVALUATE_AND_FORMAT
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionDecimalNumberSymbols() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS,
            SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionValueSeparator() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.VALUE_SEPARATOR,
            SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionDateParser() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.DATE_PARSER,
            SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionDateTimeParser() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.DATE_TIME_PARSER,
            SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionNumberParser() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.NUMBER_PARSER,
            SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionTimeParser() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.TIME_PARSER,
            SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionNamedColor() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.namedColor(SpreadsheetColorName.BLACK),
            SpreadsheetCellStoreAction.EVALUATE_AND_FORMAT
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionNumberedColor() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.numberedColor(1),
            SpreadsheetCellStoreAction.EVALUATE_AND_FORMAT
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionTextFormatter() {
        this.spreadsheetCellStoreActionAndCheck(
            SpreadsheetMetadataPropertyName.TEXT_FORMATTER,
            SpreadsheetCellStoreAction.EVALUATE_AND_FORMAT
        );
    }

    private void spreadsheetCellStoreActionAndCheck(final SpreadsheetMetadataPropertyName<?> name,
                                                    final SpreadsheetCellStoreAction action) {
        this.checkEquals(
            action,
            name.spreadsheetCellStoreAction(),
            () -> name + " spreadsheetCellStoreAction"
        );
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Override
    public SpreadsheetMetadataPropertyName<?> createName(final String name) {
        return SpreadsheetMetadataPropertyName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    public String nameText() {
        return SpreadsheetMetadataPropertyName.COMPARATORS.name;
    }

    @Override
    public String differentNameText() {
        return this.nameTextLess();
    }

    @Override
    public String nameTextLess() {
        return SpreadsheetMetadataPropertyName.AUDIT_INFO.name;
    }

    // HasSpreadsheetPatternKind........................................................................................

    @Test
    public void testHasSpreadsheetPatternKindSpreadsheetId() {
        this.hasSpreadsheetPatternKindAndCheck(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID
        );
    }

    @Test
    public void testHasSpreadsheetPatternKindColor1() {
        this.hasSpreadsheetPatternKindAndCheck(
            SpreadsheetMetadataPropertyName.numberedColor(1)
        );
    }

    @Test
    public void testHasSpreadsheetPatternKindDateFormatter() {
        this.hasSpreadsheetPatternKindAndCheck(
            SpreadsheetMetadataPropertyName.DATE_FORMATTER,
            SpreadsheetPatternKind.DATE_FORMAT_PATTERN
        );
    }

    @Test
    public void testHasSpreadsheetPatternKindDateTimeFormatter() {
        this.hasSpreadsheetPatternKindAndCheck(
            SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER,
            SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN
        );
    }

    @Test
    public void testHasSpreadsheetPatternKindTextFormatter() {
        this.hasSpreadsheetPatternKindAndCheck(
            SpreadsheetMetadataPropertyName.TEXT_FORMATTER,
            SpreadsheetPatternKind.TEXT_FORMAT_PATTERN
        );
    }

    // constants........................................................................................................

    @Test
    public void testConstantsAll() {
        this.checkNotEquals(
            Sets.empty(),
            SpreadsheetMetadataPropertyName.ALL
        );
    }

    @Override
    public Set<SpreadsheetMetadataPropertyName<?>> intentionalDuplicateConstants() {
        return Set.of();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyName<?>> type() {
        return Cast.to(SpreadsheetMetadataPropertyName.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
