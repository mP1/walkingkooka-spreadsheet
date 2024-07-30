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

package walkingkooka.spreadsheet.format.edit;

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
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

public final class BasicSpreadsheetFormatterSelectorEditContextTest implements SpreadsheetFormatterSelectorEditContextTesting<BasicSpreadsheetFormatterSelectorEditContext> {

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

    @Override
    public BasicSpreadsheetFormatterSelectorEditContext createContext() {
        return BasicSpreadsheetFormatterSelectorEditContext.with(
                this.spreadsheetFormatterContext(),
                SpreadsheetFormatterProviders.spreadsheetFormatPattern()
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

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetFormatterSelectorEditContext> type() {
        return BasicSpreadsheetFormatterSelectorEditContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
