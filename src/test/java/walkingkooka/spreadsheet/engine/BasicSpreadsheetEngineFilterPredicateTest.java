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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.convert.Converters;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FunctionExpressionName;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class BasicSpreadsheetEngineFilterPredicateTest implements PredicateTesting2<BasicSpreadsheetEngineFilterPredicate, SpreadsheetCell> {

    private final static String CONTEXT_TO_STRING = "FakeSpreadsheetEngineContext123";

    @Test
    public void testFalseEmptyFormulaNoValueWithAny() {
        this.testFalse(
                this.createPredicate(SpreadsheetValueType.ANY),
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
        );
    }

    @Test
    public void testTrueAnyValueTypeWithNotEmptyFormula() {
        this.testTrue(
                this.createPredicate(SpreadsheetValueType.ANY),
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                )
        );
    }

    @Test
    public void testTrueSameValueType() {
        this.testTrue(
                this.createPredicate(SpreadsheetValueType.TEXT),
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=\"hello\"")
                                .setValue(
                                        Optional.of("hello")
                                )
                )
        );
    }

    @Test
    public void testFalseAnyValueType() {
        this.testFalse(
                this.createPredicate(SpreadsheetValueType.ANY),
                SpreadsheetSelection.parseCell("B2")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );
    }

    @Test
    public void testFalseWrongValueType() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=\"Hello\"")
                        .setValue(
                                Optional.of("Hello")
                        )
        );

        this.testTrue(
                this.createPredicate(SpreadsheetValueType.ANY),
                cell
        );
        this.testFalse(
                this.createPredicate(SpreadsheetValueType.NUMBER),
                cell
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createPredicate(),
                "* Test123() " + CONTEXT_TO_STRING
        );
    }

    @Override
    public BasicSpreadsheetEngineFilterPredicate createPredicate() {
        return this.createPredicate(
                SpreadsheetValueType.ANY
        );
    }

    private BasicSpreadsheetEngineFilterPredicate createPredicate(final String valueType) {
        final Expression expression = Expression.call(
                Expression.namedFunction(FunctionExpressionName.with("Test123")),
                Expression.NO_CHILDREN
        );

        return BasicSpreadsheetEngineFilterPredicate.with(
                valueType,
                expression,
                new FakeSpreadsheetEngineContext() {
                    @Override
                    public Object evaluate(final Expression e,
                                           final Optional<SpreadsheetCell> cell) {
                        assertSame(expression, e);

                        final SpreadsheetCellReference cellReference = cell.get()
                                .reference();
                        switch (cellReference.text()) {
                            case "A1":
                                return true;
                            case "B2":
                                return false;
                            default:
                                throw new IllegalArgumentException("Unexpected cell " + cell);
                        }
                    }

                    @Override
                    public SpreadsheetMetadata spreadsheetMetadata() {
                        return SpreadsheetMetadata.EMPTY
                                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-AU"))
                                .loadFromLocale()
                                .set(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("user@example.com"))
                                .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.now())
                                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.EXCEL_1900_DATE_SYSTEM_OFFSET)
                                .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 50)
                                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.DOUBLE)
                                .set(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("user@example.com"))
                                .set(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.now())
                                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP)
                                .set(SpreadsheetMetadataPropertyName.PRECISION, 7)
                                .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 50)
                                .set(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetPattern.DEFAULT_TEXT_FORMAT_PATTERN);
                    }

                    @Override
                    public String toString() {
                        return CONTEXT_TO_STRING;
                    }
                }
        );
    }

    @Override
    public Class<BasicSpreadsheetEngineFilterPredicate> type() {
        return BasicSpreadsheetEngineFilterPredicate.class;
    }
}
