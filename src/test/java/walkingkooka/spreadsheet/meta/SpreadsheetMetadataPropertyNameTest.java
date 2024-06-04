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
import walkingkooka.color.Color;
import walkingkooka.naming.NameTesting;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.FieldAttributes;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.pattern.HasSpreadsheetPatternKindTesting;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.spreadsheet.store.SpreadsheetCellStoreAction;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.lang.reflect.Field;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataPropertyNameTest extends SpreadsheetMetadataTestCase2<SpreadsheetMetadataPropertyName<?>>
        implements NameTesting<SpreadsheetMetadataPropertyName<?>, SpreadsheetMetadataPropertyName<?>>,
        HasSpreadsheetPatternKindTesting {

    @Test
    public void testUnknownConstantFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetMetadataPropertyName.with("unknown1234567"));
    }

    @Test
    public void testDefaultsSpecialInternalConstantFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetMetadataPropertyName.with("_defaults"));
    }

    @Test
    public void testConstants() {
        this.checkEquals(Lists.empty(),
                Arrays.stream(SpreadsheetMetadataPropertyName.class.getDeclaredFields())
                        .filter(FieldAttributes.STATIC::is)
                        .filter(f -> f.getType() == SpreadsheetMetadataPropertyName.class)
                        .filter(SpreadsheetMetadataPropertyNameTest::constantNotCached)
                        .collect(Collectors.toList()),
                "");
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
    public void testWithColorPropertyFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetMetadataPropertyName.with("color-"));
    }

    @Test
    public void testWithColorPropertyFails2() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetMetadataPropertyName.with("color-!"));
    }

    @Test
    public void testWithColorName() {
        final int colorNumber = 12;

        Stream.of("big", "medium", "small")
                .forEach(i -> {
                            final String value = "color-" + i;
                            final SpreadsheetMetadataPropertyName<?> propertyName = SpreadsheetMetadataPropertyName.with(value);
                            this.checkEquals(SpreadsheetMetadataPropertyNameNamedColor.class, propertyName.getClass(), "class name");
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
                            final String value = "color-" + i;
                            final SpreadsheetMetadataPropertyName<?> propertyName = SpreadsheetMetadataPropertyName.with(value);
                            this.checkEquals(SpreadsheetMetadataPropertyNameNumberedColor.class, propertyName.getClass(), "class name");
                            this.checkEquals(value, propertyName.value(), "value");

                            propertyName.checkValue(color);
                        }
                );
    }

    // patch............................................................................................................

    @Test
    public void testPatchNonNullValue() {
        this.patchAndCheck(
                SpreadsheetMetadataPropertyName.CREATOR,
                EmailAddress.parse("user@example.com"),
                JsonNode.object()
                        .set(
                                JsonPropertyName.with("creator"),
                                JsonNode.string("user@example.com")
                        )
        );
    }

    @Test
    public void testPatchNonNullValue2() {
        this.patchAndCheck(
                SpreadsheetMetadataPropertyName.CREATOR,
                EmailAddress.parse("user@example.com"),
                SpreadsheetMetadata.EMPTY.set(
                        SpreadsheetMetadataPropertyName.CREATOR,
                        EmailAddress.parse("user@patched-over.com")
                ),
                SpreadsheetMetadata.EMPTY.set(
                        SpreadsheetMetadataPropertyName.CREATOR,
                        EmailAddress.parse("user@example.com")
                )
        );
    }

    @Test
    public void testPatchNonNullValue3() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);

        this.patchAndCheck(
                SpreadsheetMetadataPropertyName.CREATOR,
                EmailAddress.parse("user@example.com"),
                metadata.set(
                        SpreadsheetMetadataPropertyName.CREATOR,
                        EmailAddress.parse("user@patched-over.com")
                ),
                metadata.set(
                        SpreadsheetMetadataPropertyName.CREATOR,
                        EmailAddress.parse("user@example.com")
                )
        );
    }

    @Test
    public void testPatchNullValue() {
        this.patchAndCheck(
                SpreadsheetMetadataPropertyName.CREATOR,
                null,
                JsonNode.object()
                        .set(
                                JsonPropertyName.with("creator"),
                                JsonNode.nullNode()
                        )
        );
    }

    @Test
    public void testPatchNullValue2() {
        this.patchAndCheck(
                SpreadsheetMetadataPropertyName.CREATOR,
                null,
                SpreadsheetMetadata.EMPTY.set(
                        SpreadsheetMetadataPropertyName.CREATOR,
                        EmailAddress.parse("user@patched-over.com")
                ),
                SpreadsheetMetadata.EMPTY
        );
    }

    @Test
    public void testPatchNullValue3() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);

        this.patchAndCheck(
                SpreadsheetMetadataPropertyName.CREATOR,
                null,
                metadata.set(
                        SpreadsheetMetadataPropertyName.CREATOR,
                        EmailAddress.parse("user@patched-over.com")
                ),
                metadata
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
        final SpreadsheetMetadataPropertyName<?> creator = SpreadsheetMetadataPropertyName.CREATOR;
        final SpreadsheetMetadataPropertyName<?> modifiedBy = SpreadsheetMetadataPropertyName.MODIFIED_BY;
        final SpreadsheetMetadataPropertyName<?> modifiedDateTime = SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME;
        final SpreadsheetMetadataPropertyName<?> spreadsheetId = SpreadsheetMetadataPropertyName.SPREADSHEET_ID;

        //noinspection unchecked
        this.compareToArraySortAndCheck(
                spreadsheetId, modifiedDateTime, creator, modifiedBy,
                spreadsheetId, creator, modifiedBy, modifiedDateTime
        );
    }

    @Test
    public void testSortSpreadsheetIdFirst2() {
        final SpreadsheetMetadataPropertyName<?> creator = SpreadsheetMetadataPropertyName.CREATOR;
        final SpreadsheetMetadataPropertyName<?> modifiedBy = SpreadsheetMetadataPropertyName.MODIFIED_BY;
        final SpreadsheetMetadataPropertyName<?> modifiedDateTime = SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME;
        final SpreadsheetMetadataPropertyName<?> spreadsheetId = SpreadsheetMetadataPropertyName.SPREADSHEET_ID;

        //noinspection unchecked
        this.compareToArraySortAndCheck(
                modifiedDateTime, spreadsheetId, modifiedBy, creator,
                spreadsheetId, creator, modifiedBy, modifiedDateTime
        );
    }

    @Test
    public void testSortNumberedColours() {
        final SpreadsheetMetadataPropertyName<?> creator = SpreadsheetMetadataPropertyName.CREATOR;
        final SpreadsheetMetadataPropertyName<?> color10 = SpreadsheetMetadataPropertyName.numberedColor(10);
        final SpreadsheetMetadataPropertyName<?> color2 = SpreadsheetMetadataPropertyName.numberedColor(2);
        final SpreadsheetMetadataPropertyName<?> color3 = SpreadsheetMetadataPropertyName.numberedColor(3);

        //noinspection unchecked
        this.compareToArraySortAndCheck(
                color3, color2, creator, color10,
                color2, color3, color10, creator
        );
    }

    @Test
    public void testSortSpreadsheetIdNumberedColours() {
        final SpreadsheetMetadataPropertyName<?> spreadsheetId = SpreadsheetMetadataPropertyName.SPREADSHEET_ID;
        final SpreadsheetMetadataPropertyName<?> creator = SpreadsheetMetadataPropertyName.CREATOR;
        final SpreadsheetMetadataPropertyName<?> color10 = SpreadsheetMetadataPropertyName.numberedColor(10);
        final SpreadsheetMetadataPropertyName<?> color2 = SpreadsheetMetadataPropertyName.numberedColor(2);
        final SpreadsheetMetadataPropertyName<?> color3 = SpreadsheetMetadataPropertyName.numberedColor(3);

        //noinspection unchecked
        this.compareToArraySortAndCheck(
                color3, color2, creator, spreadsheetId, color10,
                spreadsheetId, color2, color3, color10, creator
        );
    }

    // parseValue.......................................................................................................

    @Test
    public void testParseValueColor() {
        final Color color = Color.parse("#123456");

        this.checkEquals(
                color,
                SpreadsheetMetadataPropertyName.numberedColor(1)
                        .parseValue(color.toString())
        );
    }

    @Test
    public void testParseValueCreatorFails() {
        this.parseValueFails(
                SpreadsheetMetadataPropertyName.CREATOR,
                EmailAddress.parse("creator@example.com")
        );
    }

    @Test
    public void testParseValueFrozenColumnFails() {
        this.parseValueFails(
                SpreadsheetMetadataPropertyName.FROZEN_COLUMNS,
                "A:B"
        );
    }

    @Test
    public void testParseValueModifiedByFails() {
        this.parseValueFails(
                SpreadsheetMetadataPropertyName.MODIFIED_BY,
                EmailAddress.parse("modified-by@example.com")
        );
    }

    @Test
    public void testParseValueFrozenRowFails() {
        this.parseValueFails(
                SpreadsheetMetadataPropertyName.FROZEN_ROWS,
                "1:2"
        );
    }

    @Test
    public void testParseValueSpreadsheetIdFails() {
        this.parseValueFails(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                SpreadsheetId.parse("123abc")
        );
    }

    @Test
    public void testParseValueStyleFails() {
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
                () -> propertyName.parseValue(propertyValue)
        );
    }

    @Test
    public void testParseValueSpreadsheetName() {
        final String value = "SpreadsheetName123";

        this.checkEquals(
                SpreadsheetName.with(value),
                SpreadsheetMetadataPropertyName.SPREADSHEET_NAME.parseValue(value)
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
    public void testSpreadsheetCellStoreActionCreator() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.CREATOR,
                SpreadsheetCellStoreAction.NONE
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionCreateDateTime() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.CREATE_DATE_TIME,
                SpreadsheetCellStoreAction.NONE
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionModifiedBy() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.MODIFIED_BY,
                SpreadsheetCellStoreAction.NONE
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionModifiedDateTime() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME,
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
    public void testSpreadsheetCellStoreActionViewport() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.VIEWPORT,
                SpreadsheetCellStoreAction.EVALUATE_AND_FORMAT
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionCurrencySymbol() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL,
                SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionDecimalSeparator() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR,
                SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionExponentSymbol() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL,
                SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionGroupSeparator() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.GROUP_SEPARATOR,
                SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionNegativeSign() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.NEGATIVE_SIGN,
                SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionPercentageSymbol() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL,
                SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionPositiveSign() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.POSITIVE_SIGN,
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
    public void testSpreadsheetCellStoreActionDateParsePattern() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERN,
                SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionDateTimeParsePattern() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERN,
                SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionNumberParsePattern() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERN,
                SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testSpreadsheetCellStoreActionTimeParsePattern() {
        this.spreadsheetCellStoreActionAndCheck(
                SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERN,
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

    @Test
    public void testJsonNodeNameCached() {
        final SpreadsheetMetadataPropertyName<?> propertyName = this.createObject();
        assertSame(propertyName.jsonPropertyName, propertyName.jsonPropertyName);
    }

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
        return SpreadsheetMetadataPropertyName.CREATOR.name;
    }

    @Override
    public String differentNameText() {
        return this.nameTextLess();
    }

    @Override
    public String nameTextLess() {
        return SpreadsheetMetadataPropertyName.CREATE_DATE_TIME.name;
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

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyName<?>> type() {
        return Cast.to(SpreadsheetMetadataPropertyName.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // TypeNameTesting...................................................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetMetadata.class.getSimpleName();
    }
}
