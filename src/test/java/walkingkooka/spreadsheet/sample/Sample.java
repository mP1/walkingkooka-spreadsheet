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

package walkingkooka.spreadsheet.sample;

import walkingkooka.Cast;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.Converters;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetExpressionReferenceStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class Sample {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    private static final Supplier<LocalDateTime> NOW = LocalDateTime::now;

    private final static Function<SpreadsheetSelection, SpreadsheetSelection> RESOLVE_IF_LABEL = (s) -> {
        throw new UnsupportedOperationException();
    };

    public static void main(final String[] args) {
        final SpreadsheetEngine engine = engine();
        final SpreadsheetEngineContext engineContext = engineContext(engine);

        engine.saveCell(
                SpreadsheetSelection.A1
                        .setFormula(
                                SpreadsheetFormula.EMPTY
                                        .setText("12+B2")
                        ),
                engineContext
        );

        final SpreadsheetDelta delta = engine.saveCell(
                SpreadsheetSelection.parseCell("B2")
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
        assertEquals(expected, actual, message);
    }

    private static SpreadsheetMetadata metadata() {
        if (null == metadata) {
            SpreadsheetMetadata m = SpreadsheetMetadata.EMPTY
                    .set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 10)
                    .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.of(2000, 12, 31, 12, 58, 59))
                    .set(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("creator@example.com"))
                    .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "$AUD")
                    .set(SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN, SpreadsheetPattern.parseDateFormatPattern("DD/MM/YYYY"))
                    .set(SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERN, SpreadsheetPattern.parseDateParsePattern("DD/MM/YYYYDDMMYYYY"))
                    .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                    .set(SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN, SpreadsheetPattern.parseDateTimeFormatPattern("DD/MM/YYYY hh:mm"))
                    .set(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERN, SpreadsheetPattern.parseDateTimeParsePattern("DD/MM/YYYY hh:mmDDMMYYYYHHMMDDMMYYYY HHMM"))
                    .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '.')
                    .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 1900)
                    .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
                    .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, "E")
                    .set(SpreadsheetMetadataPropertyName.FROZEN_COLUMNS, SpreadsheetSelection.parseColumnRange("A:B"))
                    .set(SpreadsheetMetadataPropertyName.FROZEN_ROWS, SpreadsheetSelection.parseRowRange("1:2"))
                    .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, ',')
                    .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-AU"))
                    .set(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("modified@example.com"))
                    .set(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.of(1999, 12, 31, 12, 58, 59))
                    .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, '-')
                    .set(SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN, SpreadsheetPattern.parseNumberFormatPattern("#0.0"))
                    .set(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT, 8)
                    .set(SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERN, SpreadsheetPattern.parseNumberParsePattern("#0.0$#0.00"))
                    .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, '%')
                    .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, '+')
                    .set(SpreadsheetMetadataPropertyName.PRECISION, 123)
                    .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.FLOOR)
                    .set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(123))
                    .set(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetPattern.parseTextFormatPattern("@@"))
                    .set(
                            SpreadsheetMetadataPropertyName.STYLE,
                            TextStyle.EMPTY.set(TextStylePropertyName.WIDTH, Length.pixel(50.0))
                                    .set(TextStylePropertyName.HEIGHT, Length.pixel(50.0)))
                    .set(SpreadsheetMetadataPropertyName.TIME_FORMAT_PATTERN, SpreadsheetPattern.parseTimeFormatPattern("hh:mm"))
                    .set(SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERN, SpreadsheetPattern.parseTimeParsePattern("hh:mmhh:mm:ss.000"))
                    .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 31)
                    .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, ',');

            for (int i = SpreadsheetColors.MIN; i < SpreadsheetColors.MAX + 1; i++) {
                m = m.set(SpreadsheetMetadataPropertyName.numberedColor(i), Color.fromRgb(i));
            }

            metadata = m;
        }
        return metadata;
    }

    private static SpreadsheetMetadata metadata;

    private static SpreadsheetEngine engine() {
        return SpreadsheetEngines.basic();
    }

    private static SpreadsheetEngineContext engineContext(final SpreadsheetEngine engine) {
        final SpreadsheetMetadata metadata = metadata();

        return new FakeSpreadsheetEngineContext() {

            @Override
            public SpreadsheetMetadata spreadsheetMetadata() {
                return metadata;
            }

            @Override
            public SpreadsheetParserToken parseFormula(final TextCursor formula) {
                return Cast.to(SpreadsheetParsers.expression()
                        .orFailIfCursorNotEmpty(ParserReporters.basic())
                        .parse(
                                formula,
                                metadata.parserContext(NOW)
                        ) // TODO should fetch parse metadata prop
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
                                        NOW,
                                        RESOLVE_IF_LABEL
                                )
                        )
                );
            }

            private Function<FunctionExpressionName, ExpressionFunction<?, ExpressionEvaluationContext>> functions() {
                return (n) -> {
                    throw new UnsupportedOperationException("unsupported expression " + n);
                };
            }

            private Function<ExpressionReference, Optional<Optional<Object>>> references() {
                return SpreadsheetEngines.expressionReferenceFunction(
                        engine,
                        this
                );
            }

            @Override
            public Optional<SpreadsheetText> format(final Object value,
                                                    final SpreadsheetFormatter formatter) {
                checkEquals(false, value instanceof Optional, "Value must not be optional" + value);

                return formatter.format(
                        value,
                        metadata.formatterContext(
                                NOW,
                                RESOLVE_IF_LABEL
                        )
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
