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

package walkingkooka.spreadsheet.convert;

import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.SpreadsheetValueVisitor;
import walkingkooka.tree.expression.convert.ExpressionNumberConverters;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A {@link SpreadsheetValueVisitor} that converts a value into a {@link walkingkooka.tree.expression.ExpressionNumber}.
 */
final class SpreadsheetConverterToNumberSpreadsheetValueVisitor extends SpreadsheetValueVisitor {

    static Converter<SpreadsheetConverterContext> converter(final Object value) {
        final SpreadsheetConverterToNumberSpreadsheetValueVisitor visitor = new SpreadsheetConverterToNumberSpreadsheetValueVisitor();
        visitor.accept(value);
        return visitor.converter;
    }

    // @VisibleForTesting
    SpreadsheetConverterToNumberSpreadsheetValueVisitor() {
        super();
    }

    @Override
    protected void visit(final Boolean value) {
        this.converter = BOOLEAN;
    }

    private final static Converter<SpreadsheetConverterContext> BOOLEAN = ExpressionNumberConverters.toNumberOrExpressionNumber(Converters.booleanToNumber());

    @Override
    protected void visit(final LocalDate value) {
        this.converter = DATE;
    }

    private final static Converter<SpreadsheetConverterContext> DATE = ExpressionNumberConverters.toNumberOrExpressionNumber(Converters.localDateToNumber());

    @Override
    protected void visit(final LocalDateTime value) {
        this.converter = DATE_TIME;
    }

    private final static Converter<SpreadsheetConverterContext> DATE_TIME = ExpressionNumberConverters.toNumberOrExpressionNumber(Converters.localDateTimeToNumber());

    @Override
    protected void visit(final String value) {
        this.converter = SpreadsheetConverterToNumberSpreadsheetValueVisitorStringConverter.INSTANCE;
    }

    private Converter<SpreadsheetConverterContext> converter;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return String.valueOf(this.converter);
    }
}
