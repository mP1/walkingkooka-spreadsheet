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
import walkingkooka.color.Color;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetParserSelectorEditContextTest implements SpreadsheetParserSelectorEditContextTesting<BasicSpreadsheetParserSelectorEditContext> {

    @Test
    public void testWithNullSpreadsheetParserProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetParserSelectorEditContext.with(
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
                SpreadsheetParserProviders.spreadsheetParsePattern(
                        spreadsheetFormatterProvider
                ),
                this.spreadsheetFormatterContext(),
                spreadsheetFormatterProvider
        );
    }

    private SpreadsheetFormatterContext spreadsheetFormatterContext() {
        return SpreadsheetFormatterContexts.basic(
                this::numberToColor,
                this::nameToColor,
                1, // cellCharacterWidth
                8, // default general-format-number-digit-count
                SpreadsheetFormatters.fake(), // should never be called
                this.spreadsheetConverterContext()
        );
    }

    private SpreadsheetConverterContext spreadsheetConverterContext() {
        return SpreadsheetConverterContexts.basic(
                Converters.objectToString(),
                SpreadsheetLabelNameResolvers.fake(),
                ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ConverterContexts.basic(
                                Converters.JAVA_EPOCH_OFFSET, // dateOffset
                                Converters.objectToString(),
                                DateTimeContexts.locale(
                                        Locale.forLanguageTag("EN-AU"),
                                        1950, // default year
                                        50, // two-digit-year
                                        LocalDateTime::now
                                ),
                                DecimalNumberContexts.american(
                                        MathContext.DECIMAL32
                                )
                        ),
                        ExpressionNumberKind.BIG_DECIMAL
                )
        );
    }

    private Optional<Color> numberToColor(final Integer value) {
        return SpreadsheetText.WITHOUT_COLOR; // ignore the colour number
    }

    private Optional<Color> nameToColor(final SpreadsheetColorName name) {
        return SpreadsheetText.WITHOUT_COLOR; // ignore the colour name.
    }

    @Override
    public String currencySymbol() {
        return this.spreadsheetConverterContext()
                .currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.spreadsheetConverterContext()
                .decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return this.spreadsheetConverterContext()
                .exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return this.spreadsheetConverterContext()
                .groupSeparator();
    }

    @Override
    public MathContext mathContext() {
        return this.spreadsheetConverterContext()
                .mathContext();
    }

    @Override
    public char negativeSign() {
        return this.spreadsheetConverterContext()
                .negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return this.spreadsheetConverterContext()
                .percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return this.spreadsheetConverterContext()
                .positiveSign();
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
