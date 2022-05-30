/*
 * Copyright © 2020 Miroslav Pokorny
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
 */
package test;


import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Assert;
import org.junit.Test;

import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.format.SpreadsheetFormatException;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExpressionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetExpressionReferenceStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

// copied from Sample
@J2clTestInput(JunitTest.class)
public class JunitTest {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    private static final Supplier<LocalDateTime> NOW = LocalDateTime::now;

    @Test
    public void testMetadataNonLocaleDefaults() {
        Assert.assertNotEquals(null, SpreadsheetMetadata.NON_LOCALE_DEFAULTS);
    }

    @Test
    public void testWithCellReference() {
        final SpreadsheetEngine engine = engine();
        final SpreadsheetEngineContext engineContext = engineContext(engine);

        engine.saveCell(
                SpreadsheetCellReference.parseCell("A1")
                        .setFormula(
                                SpreadsheetFormula.EMPTY
                                        .setText("12+B2")
                        ),
                engineContext
        );

        final SpreadsheetDelta delta = engine.saveCell(
                SpreadsheetCellReference.parseCell("B2")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("34")
                        ),
                engineContext
        );

        final Set<String> saved = delta.cells()
                .stream()
                .map(c -> c.formula().value().get().toString())
                .collect(Collectors.toCollection(Sets::sorted));

        // a1=12+b2
        // a1=12+34
        // b2=34
        checkEquals(Sets.of("46", "34"), saved, "saved formula values");
    }

    private static void checkEquals(final Object expected,
                                    final Object actual,
                                    final String message) {
        Assert.assertEquals(message, expected, actual);
    }

    private static SpreadsheetMetadata metadata() {
        if (null == metadata) {
            SpreadsheetMetadata m = SpreadsheetMetadata.EMPTY
                    .set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 10)
                    .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.of(2000, 12, 31, 12, 58, 59))
                    .set(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("creator@example.com"))
                    .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "$AUD")
                    .set(SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN, SpreadsheetPattern.parseDateFormatPattern("DD/MM/YYYY"))
                    .set(SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERNS, SpreadsheetPattern.parseDateParsePatterns("DD/MM/YYYYDDMMYYYY"))
                    .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                    .set(SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN, SpreadsheetPattern.parseDateTimeFormatPattern("DD/MM/YYYY hh:mm"))
                    .set(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERNS, SpreadsheetPattern.parseDateTimeParsePatterns("DD/MM/YYYY hh:mmDDMMYYYYHHMMDDMMYYYY HHMM"))
                    .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '.')
                    .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 1900)
                    .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
                    .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, "E")
                    .set(SpreadsheetMetadataPropertyName.FROZEN_COLUMNS, SpreadsheetSelection.parseColumnRange("A:B"))
                    .set(SpreadsheetMetadataPropertyName.FROZEN_ROWS, SpreadsheetSelection.parseRowRange("1:2"))
                    .set(SpreadsheetMetadataPropertyName.GROUPING_SEPARATOR, ',')
                    .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-AU"))
                    .set(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("modified@example.com"))
                    .set(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.of(1999, 12, 31, 12, 58, 59))
                    .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, '-')
                    .set(SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN, SpreadsheetPattern.parseNumberFormatPattern("#0.0"))
                    .set(SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERNS, SpreadsheetPattern.parseNumberParsePatterns("#0.0$#0.00"))
                    .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, '%')
                    .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, '+')
                    .set(SpreadsheetMetadataPropertyName.PRECISION, 123)
                    .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.FLOOR)
                    .set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(123))
                    .set(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetPattern.parseTextFormatPattern("@@"))
                    .set(SpreadsheetMetadataPropertyName.TIME_FORMAT_PATTERN, SpreadsheetPattern.parseTimeFormatPattern("hh:mm"))
                    .set(SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERNS, SpreadsheetPattern.parseTimeParsePatterns("hh:mmhh:mm:ss.000"))
                    .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 31)
                    .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, ',');

            for (int i = 0; i < SpreadsheetMetadata.MAX_NUMBER_COLOR + 2; i++) {
                m = m.set(SpreadsheetMetadataPropertyName.numberedColor(i), Color.fromRgb(i));
            }

            metadata = m;
        }
        return metadata;
    }

    private static SpreadsheetMetadata metadata;

    private static SpreadsheetEngine engine() {
        return SpreadsheetEngines.basic(
                metadata(),
                NOW
        );
    }

    private static SpreadsheetEngineContext engineContext(final SpreadsheetEngine engine) {
        final SpreadsheetMetadata metadata = metadata();

        return new FakeSpreadsheetEngineContext() {

            @Override
            public SpreadsheetMetadata metadata() {
                return metadata;
            }

            @Override
            public SpreadsheetParserToken parseFormula(final String formula) {
                return Cast.to(SpreadsheetParsers.expression()
                        .orFailIfCursorNotEmpty(ParserReporters.basic())
                        .parse(TextCursors.charSequence(formula), SpreadsheetParserContexts.basic(DateTimeContexts.fake(),
                                metadata.converterContext(NOW),
                                EXPRESSION_NUMBER_KIND,
                                ',')) // TODO should fetch from metadata prop
                        .get());
            }

            @Override
            public Object evaluate(final Expression node,
                                   final Optional<SpreadsheetCell> cell) {
                return node.toValue(
                        ExpressionEvaluationContexts.basic(
                                EXPRESSION_NUMBER_KIND,
                                this.functions(),
                                (r) -> {
                                    throw new UnsupportedOperationException();
                                },
                                this.references(),
                                SpreadsheetExpressionEvaluationContexts.referenceNotFound(),
                                CaseSensitivity.INSENSITIVE,
                                metadata.converterContext(
                                        NOW
                                )
                        )
                );
            }

            private Function<FunctionExpressionName, ExpressionFunction<?, ExpressionEvaluationContext>> functions() {
                return (n) -> {
                    throw new UnsupportedOperationException("unsupported expression " + n);
                };
            }

            private Function<ExpressionReference, Optional<Object>> references() {
                return SpreadsheetEngines.expressionEvaluationContextExpressionReferenceExpressionFunction(
                        engine,
                        this
                );
            }

            @Override
            public SpreadsheetFormatter parsePattern(final String pattern) {
                final SpreadsheetFormatExpressionParserToken token = SpreadsheetFormatParsers.expression()
                        .orFailIfCursorNotEmpty(ParserReporters.basic())
                        .parse(TextCursors.charSequence(pattern), SpreadsheetFormatParserContexts.basic())
                        .get()
                        .cast(SpreadsheetFormatExpressionParserToken.class);
                return SpreadsheetFormatters.expression(token, (v) -> {
                    throw new UnsupportedOperationException();
                });
            }

            @Override
            public Optional<SpreadsheetText> format(final Object value,
                                                    final SpreadsheetFormatter formatter) {
                checkEquals(false, value instanceof Optional, "Value must not be optional" + value);

                return formatter.format(
                        value,
                        metadata.formatterContext(NOW)
                );
            }

            @Override
            public SpreadsheetStoreRepository storeRepository() {
                return this.storeRepository;
            }

            private final SpreadsheetStoreRepository storeRepository = SpreadsheetStoreRepositories.basic(
                    SpreadsheetCellStores.treeMap(),
                    SpreadsheetExpressionReferenceStores.treeMap(),
                    SpreadsheetColumnStores.treeMap(),
                    SpreadsheetGroupStores.fake(),
                    SpreadsheetLabelStores.treeMap(),
                    SpreadsheetExpressionReferenceStores.treeMap(),
                    SpreadsheetMetadataStores.fake(),
                    SpreadsheetCellRangeStores.treeMap(),
                    SpreadsheetCellRangeStores.treeMap(),
                    SpreadsheetRowStores.treeMap(),
                    SpreadsheetUserStores.fake()
            );
        };
    }
}

