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

package walkingkooka.spreadsheet.template;

import walkingkooka.InvalidCharacterException;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.expression.FakeSpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.template.TemplateValueName;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.validation.ValidationError;
import walkingkooka.validation.form.FormField;

import java.util.List;
import java.util.Optional;

public class FakeSpreadsheetTemplateContext extends FakeSpreadsheetExpressionEvaluationContext
        implements SpreadsheetTemplateContext {
    
    public FakeSpreadsheetTemplateContext() {
        super();
    }

    @Override
    public Optional<SpreadsheetCell> cell() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FakeSpreadsheetTemplateContext setCell(final Optional<SpreadsheetCell> cell) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String templateValue(final TemplateValueName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char valueSeparator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ValidationError<SpreadsheetExpressionReference>> validateFormFields(final List<FormField<SpreadsheetExpressionReference>> fields) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InvalidCharacterException invalidCharacterException(final Parser<?> parser,
                                                               final TextCursor cursor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetTemplateContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetTemplateContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        throw new UnsupportedOperationException();
    }
}
