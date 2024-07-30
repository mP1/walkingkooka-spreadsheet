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

package walkingkooka.spreadsheet.parser.edit;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetParserSelectorEditContextTest implements SpreadsheetParserSelectorEditContextTesting<BasicSpreadsheetParserSelectorEditContext>,
        SpreadsheetMetadataTesting {

    @Test
    public void testWithNullSpreadsheetParserProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetParserSelectorEditContext.with(
                        null,
                        SpreadsheetParserContexts.fake(),
                        SpreadsheetFormatterContexts.fake(),
                        SpreadsheetFormatterProviders.fake()
                )
        );
    }

    @Test
    public void testWithNullSpreadsheetParserContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetParserSelectorEditContext.with(
                        SpreadsheetParserProviders.fake(),
                        null,
                        SpreadsheetFormatterContexts.fake(),
                        SpreadsheetFormatterProviders.fake()
                )
        );
    }

    @Test
    public void testWithNullSpreadsheetFormatterContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetParserSelectorEditContext.with(
                        SpreadsheetParserProviders.fake(),
                        SpreadsheetParserContexts.fake(),
                        null,
                        SpreadsheetFormatterProviders.fake()
                )
        );
    }

    @Test
    public void testWithNullSpreadsheetFormatterProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetParserSelectorEditContext.with(
                        SpreadsheetParserProviders.fake(),
                        SpreadsheetParserContexts.fake(),
                        SpreadsheetFormatterContexts.fake(),
                        null
                )
        );
    }

    // SpreadsheetFormatterSelector.....................................................................................

    @Test
    public void testSpreadsheetFormatterSelectorDateTextFormat() {
        final SpreadsheetFormatPattern dateTextFormat = SpreadsheetPattern.parseDateFormatPattern("yyyy/mm/dd");

        this.spreadsheetFormatterAndCheck(
                dateTextFormat.spreadsheetFormatterSelector(),
                dateTextFormat.formatter()
        );
    }

    @Test
    public void testSpreadsheetParserSelectorDateTextParser() {
        final SpreadsheetParsePattern dateTextParser = SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd");

        this.spreadsheetParserAndCheck(
                dateTextParser.spreadsheetParserSelector(),
                dateTextParser.parser()
        );
    }

    @Override
    public BasicSpreadsheetParserSelectorEditContext createContext() {
        final SpreadsheetFormatterProvider spreadsheetFormatterProvider = SpreadsheetFormatterProviders.spreadsheetFormatPattern(
                Locale.forLanguageTag("EN-AU"),
                LocalDateTime::now
        );

        return BasicSpreadsheetParserSelectorEditContext.with(
                SPREADSHEET_PARSER_PROVIDER,
                SPREADSHEET_PARSER_CONTEXT,
                SPREADSHEET_FORMATTER_CONTEXT,
                SPREADSHEET_FORMATTER_PROVIDER
        );
    }

    @Override
    public String currencySymbol() {
        return SPREADSHEET_CONVERTER_CONTEXT.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return SPREADSHEET_CONVERTER_CONTEXT.decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return SPREADSHEET_CONVERTER_CONTEXT.exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return SPREADSHEET_CONVERTER_CONTEXT.groupSeparator();
    }

    @Override
    public MathContext mathContext() {
        return SPREADSHEET_CONVERTER_CONTEXT.mathContext();
    }

    @Override
    public char negativeSign() {
        return SPREADSHEET_CONVERTER_CONTEXT.negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return SPREADSHEET_CONVERTER_CONTEXT.percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return SPREADSHEET_CONVERTER_CONTEXT.positiveSign();
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetParserSelectorEditContext> type() {
        return BasicSpreadsheetParserSelectorEditContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
