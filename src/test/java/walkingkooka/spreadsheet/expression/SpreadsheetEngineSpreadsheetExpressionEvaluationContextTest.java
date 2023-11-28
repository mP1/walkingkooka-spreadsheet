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

package walkingkooka.spreadsheet.expression;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEngineSpreadsheetExpressionEvaluationContextTest implements ClassTesting<SpreadsheetEngineSpreadsheetExpressionEvaluationContext>,
        ToStringTesting<SpreadsheetEngineSpreadsheetExpressionEvaluationContext> {

    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("http://example.com");

    @Test
    public void testWithNullCellFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetEngineSpreadsheetExpressionEvaluationContext.with(
                        null, // cell
                        SERVER_URL, // serverUrl
                        null
                )
        );
    }

    @Test
    public void testWithNullServerUrlFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetEngineSpreadsheetExpressionEvaluationContext.with(
                        Optional.empty(), // cell
                        null, // serverUrl
                        SpreadsheetEngineContexts.fake()// context
                )
        );
    }

    @Test
    public void testWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetEngineSpreadsheetExpressionEvaluationContext.with(
                        Optional.empty(), // cell
                        SERVER_URL, // serverUrl
                        null // context
                )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetEngineContext context = SpreadsheetEngineContexts.fake();
        this.toStringAndCheck(
                SpreadsheetEngineSpreadsheetExpressionEvaluationContext.with(
                        Optional.empty(), // cell
                        SERVER_URL, // serverUrl
                        context // context
                ),
                SERVER_URL + " " + context
        );
    }

    @Test
    public void testToStringWithCell() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1+2")
        );
        final SpreadsheetEngineContext context = SpreadsheetEngineContexts.fake();

        this.toStringAndCheck(
                SpreadsheetEngineSpreadsheetExpressionEvaluationContext.with(
                        Optional.of(cell), // cell
                        SERVER_URL, // serverUrl
                        context // context
                ),
                cell + " " + SERVER_URL + " " + context
        );
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<SpreadsheetEngineSpreadsheetExpressionEvaluationContext> type() {
        return SpreadsheetEngineSpreadsheetExpressionEvaluationContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
