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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Base class for any class that creates a {@link Parser} from a {@link DateTimeFormatter}.
 */
abstract class SpreadsheetParsePatterns2<F extends SpreadsheetFormatParserToken,
        P extends SpreadsheetParserToken,
        V> extends SpreadsheetParsePatterns<F> {

    // ctor.............................................................................................................

    /**
     * Package private ctor use factory
     */
    SpreadsheetParsePatterns2(final List<F> tokens) {
        super(tokens);
    }

    // HasConverter.....................................................................................................

    /**
     * Creates a {@link Converter} that tries each of the individual patterns, after converting each pattern to a
     * {@link DateTimeFormatter}.
     */
    @Override
    final Converter<ExpressionNumberConverterContext> createConverter() {
        return Converters.parser(
                this.targetType(),
                this.createParser(),
                SpreadsheetParsePatterns2::spreadsheetParserContext,
                this::converterTransformer
        );
    }

    private V converterTransformer(final ParserToken token,
                                   final ExpressionNumberConverterContext context) {
        return this.converterTransformer0(
                token,
                SpreadsheetParserPattern2ExpressionEvaluationContext.with(context)
        );
    }

    /**
     * Transforms the {@link SpreadsheetParserToken} into a {@link LocalDate} etc.
     */
    abstract V converterTransformer0(final ParserToken token,
                                     final ExpressionEvaluationContext context);

    /**
     * The target {@link Class type} of the {@link Converter}.
     */
    abstract Class<V> targetType();

    /**
     * Creates an adapter that will be used by the parser from a {@link ConverterContext}.
     */
    private static SpreadsheetParserContext spreadsheetParserContext(final ConverterContext context) {
        return SpreadsheetParserContexts.basic(
                context,
                context,
                ExpressionNumberKind.BIG_DECIMAL // ignored
        );
    }

    // HasParser........................................................................................................

    /**
     * Creates a {@link Parsers#alternatives(List)} that tries each of the individual patterns until success.
     */
    @Override
    final Parser<SpreadsheetParserContext> createParser() {
        return Parsers.alternatives(
                IntStream.range(0, this.value().size())
                        .mapToObj(this::createParser0)
                        .collect(Collectors.toList())
        ).transform(this::parserTransform)
                .cast();
    }

    private Parser<SpreadsheetParserContext> createParser0(final int i) {
        return SpreadsheetParsePatterns2SpreadsheetFormatParserTokenVisitor.toParser(
                this.value().get(i)
        );
    }

    /**
     * This transformer which transform the {@link SequenceParserToken} into a {@link SpreadsheetParserToken} etc..
     */
    private P parserTransform(final ParserToken token,
                              final SpreadsheetParserContext context) {
        return this.parserTransform0(
                token.cast(SequenceParserToken.class).value(),
                token.text()
        );
    }

    abstract P parserTransform0(final List<ParserToken> token,
                                final String text);
}
