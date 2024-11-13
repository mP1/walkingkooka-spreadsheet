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

/*
 * Copyright 2023 Miroslav Pokorny (github.com/mP1)
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

import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.UrlQueryString;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.math.MathContext;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A query {@link Expression}
 */
public final class SpreadsheetCellQuery implements HasUrlFragment,
        HasText {

    /**
     * Reads or extracts a {@link SpreadsheetCellFindQuery} from the parameters probably a {@link UrlQueryString}.
     */
    public static Optional<SpreadsheetCellQuery> extract(final Map<HttpRequestAttribute<?>, ?> parameters) {
        Objects.requireNonNull(parameters, "parameters");

        return QUERY.firstParameterValue(parameters)
                .map(SpreadsheetCellQuery::parse);
    }

    public static final UrlParameterName QUERY = UrlParameterName.with("query");

    /**
     * Parses the given text into a {@link SpreadsheetCellQuery}.
     */
    public static SpreadsheetCellQuery parse(final String text) {
        Objects.requireNonNull(text, "text");

        return with(
                PARSER.parseText(
                                text,
                                PARSER_CONTEXT
                        ).cast(SpreadsheetParserToken.class)
                        .toExpression(EXPRESSION_EVALUATION_CONTEXT)
                        .orElseThrow(() -> new IllegalArgumentException("Missing expression"))
        );
    }

    private final static Parser<SpreadsheetParserContext> PARSER = SpreadsheetParsers.expression()
            .orFailIfCursorNotEmpty(ParserReporters.basic());

    final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    private final static SpreadsheetParserContext PARSER_CONTEXT = SpreadsheetParserContexts.basic(
            DateTimeContexts.fake(),
            ExpressionNumberContexts.basic(
                    EXPRESSION_NUMBER_KIND,
                    DecimalNumberContexts.american(MathContext.UNLIMITED)
            ),
            ','
    );

    private final static ExpressionEvaluationContext EXPRESSION_EVALUATION_CONTEXT = new FakeExpressionEvaluationContext() {

        public ExpressionNumberKind expressionNumberKind() {
            return EXPRESSION_NUMBER_KIND;
        }
    };

    /**
     * Factory that creates a new {@link SpreadsheetCellQuery} with the given {@link Expression}.
     */
    public static SpreadsheetCellQuery with(final Expression expression) {
        return new SpreadsheetCellQuery(
                Objects.requireNonNull(expression, "expression")
        );
    }

    // VisibleForTesting
    SpreadsheetCellQuery(final Expression expression) {
        this.expression = expression;
    }

    public Expression expression() {
        return this.expression;
    }

    public SpreadsheetCellQuery setExpression(final Expression expression) {
        return this.expression.equals(expression) ?
                this :
                with(expression);
    }

    private final Expression expression;

    // HasUrlFragment...................................................................................................

    @Override
    public UrlFragment urlFragment() {
        return UrlFragment.with(this.text());
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        return this.expression().toString();
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.expression.hashCode();
    }

    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetCellQuery && this.equals0((SpreadsheetCellQuery) other);
    }

    private boolean equals0(final SpreadsheetCellQuery other) {
        return this.expression.equals(other.expression);
    }

    @Override
    public String toString() {
        return this.urlFragment()
                .toString();
    }

    // json.............................................................................................................

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetCellQuery.class),
                SpreadsheetCellQuery::unmarshall,
                SpreadsheetCellQuery::marshall,
                SpreadsheetCellQuery.class
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshall(this.text());
    }

    static SpreadsheetCellQuery unmarshall(final JsonNode node,
                                           final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }
}