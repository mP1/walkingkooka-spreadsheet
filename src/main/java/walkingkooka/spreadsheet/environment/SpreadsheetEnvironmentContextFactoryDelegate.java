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

package walkingkooka.spreadsheet.environment;

import walkingkooka.convert.Converter;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.math.MathContext;

/**
 * A delegate that delegates most of the {@link SpreadsheetEnvironmentContextFactory} methods.
 */
public interface SpreadsheetEnvironmentContextFactoryDelegate extends SpreadsheetEnvironmentContextDelegator {

    default Converter<SpreadsheetConverterContext> converter() {
        return this.spreadsheetEnvironmentContextFactory()
            .converter();
    }

    default SpreadsheetConverterContext spreadsheetConverterContext() {
        return this.spreadsheetEnvironmentContextFactory()
            .spreadsheetConverterContext();
    }

    default DateTimeContext dateTimeContext() {
        return this.spreadsheetEnvironmentContextFactory()
            .dateTimeContext();
    }

    default DecimalNumberContext decimalNumberContext() {
        return this.spreadsheetEnvironmentContextFactory()
            .decimalNumberContext();
    }

    default ExpressionNumberContext expressionNumberContext() {
        return this.spreadsheetEnvironmentContextFactory()
            .expressionNumberContext();
    }

    default ExpressionNumberKind expressionNumberKind() {
        return this.spreadsheetEnvironmentContextFactory()
            .expressionNumberKind();
    }

    default JsonNodeMarshallContext jsonNodeMarshallContext() {
        return this.spreadsheetEnvironmentContextFactory()
            .jsonNodeMarshallContext();
    }

    default JsonNodeUnmarshallContext jsonNodeUnmarshallContext() {
        return this.spreadsheetEnvironmentContextFactory()
            .jsonNodeUnmarshallContext();
    }

    default MathContext mathContext() {
        return this.spreadsheetEnvironmentContextFactory()
            .mathContext();
    }

    default SpreadsheetParser spreadsheetParser() {
        return this.spreadsheetEnvironmentContextFactory()
            .spreadsheetParser();
    }

    default SpreadsheetParserContext spreadsheetParserContext() {
        return this.spreadsheetEnvironmentContextFactory()
            .spreadsheetParserContext();
    }

    // EnvironmentContextDelegator......................................................................................

    @Override
    default SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
        return this.spreadsheetEnvironmentContextFactory();
    }

    SpreadsheetEnvironmentContextFactory spreadsheetEnvironmentContextFactory();
}
