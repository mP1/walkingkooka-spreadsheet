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

package walkingkooka.spreadsheet.importer;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetImporterContextTest implements SpreadsheetImporterContextTesting<BasicSpreadsheetImporterContext>,
    ToStringTesting<BasicSpreadsheetImporterContext> {

    private final static JsonNodeUnmarshallContext UNMARSHALL_CONTEXT = JsonNodeUnmarshallContexts.basic(
        (String cc) -> Optional.ofNullable(
            Currency.getInstance(cc)
        ),
        (String lt) -> Optional.of(
            Locale.forLanguageTag(lt)
        ),
        ExpressionNumberKind.BIG_DECIMAL,
        MathContext.DECIMAL64
    );

    @Test
    public void testWithNullJsonNodeUnmarshallContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetImporterContext.with(null)
        );
    }

    @Override
    public BasicSpreadsheetImporterContext createContext() {
        return BasicSpreadsheetImporterContext.with(UNMARSHALL_CONTEXT);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            UNMARSHALL_CONTEXT.toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetImporterContext> type() {
        return BasicSpreadsheetImporterContext.class;
    }
}
