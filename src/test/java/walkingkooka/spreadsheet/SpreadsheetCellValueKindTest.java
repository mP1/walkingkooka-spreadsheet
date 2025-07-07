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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.text.TextAlign;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.validation.ValidationValueTypeName;
import walkingkooka.validation.provider.ValidatorSelector;

import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public final class SpreadsheetCellValueKindTest implements ClassTesting<SpreadsheetCellValueKind> {

    @Test
    public void testCellValue() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setValueType(
                Optional.of(
                    ValidationValueTypeName.with("HelloValueType")
                )
            )
        ).setDateTimeSymbols(
            Optional.of(
                DateTimeSymbols.fromDateFormatSymbols(
                    new DateFormatSymbols(Locale.FRANCE)
                )
            )
        ).setDecimalNumberSymbols(
            Optional.of(
                DecimalNumberSymbols.fromDecimalFormatSymbols(
                    '+',
                    new DecimalFormatSymbols(Locale.FRANCE)
                )
            )
        ).setLocale(
            Optional.of(Locale.ENGLISH)
        ).setFormatter(
            Optional.of(
                SpreadsheetFormatterSelector.parse("hello-formatter")
            )
        ).setFormattedValue(
            Optional.of(
                TextNode.text("formatted-value")
            )
        ).setParser(
            Optional.of(
                SpreadsheetParserSelector.parse("hello-parser")
            )
        ).setStyle(
            TextStyle.EMPTY.set(
                TextStylePropertyName.TEXT_ALIGN,
                TextAlign.CENTER
            )
        ).setValidator(
            Optional.of(
                ValidatorSelector.parse("hello-validator")
            )
        );

        final Set<Object> values = Sets.hash();
        for (final SpreadsheetCellValueKind kind : SpreadsheetCellValueKind.values()) {
            final Object value = values.add(
                kind.cellValue(cell)
            );
            this.checkNotEquals(
                Optional.empty(),
                value,
                () -> kind + " value missing returned Optional#empty"
            );

            this.checkEquals(
                true,
                value,
                () -> kind + " returned duplicate value (must be returning wrong property"
            );
        }
    }

    // fileExtension....................................................................................................

    @Test
    public void testFileExtensionWithCell() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.CELL,
            "cell"
        );
    }

    @Test
    public void testFileExtensionWithDecimalNumberSymbols() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.DECIMAL_NUMBER_SYMBOLS,
            "decimal-number-symbols"
        );
    }

    @Test
    public void testFileExtensionWithValueType() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.VALUE_TYPE,
            "value-type"
        );
    }

    private void fileExtensionAndCheck(final SpreadsheetCellValueKind kind,
                                       final String expected) {
        this.checkEquals(
            expected,
            kind.fileExtension()
                .value()
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetCellValueKind> type() {
        return SpreadsheetCellValueKind.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
