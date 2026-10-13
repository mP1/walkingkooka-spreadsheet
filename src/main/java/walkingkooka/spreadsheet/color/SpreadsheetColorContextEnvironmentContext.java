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

package walkingkooka.spreadsheet.color;

import walkingkooka.Cast;
import walkingkooka.color.Color;
import walkingkooka.color.IndexedColor;
import walkingkooka.color.NamedColor;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetColorContext} that sources {@link IndexedColor} and {@link NamedColor} from the given {@link EnvironmentContext}.
 */
final class SpreadsheetColorContextEnvironmentContext implements SpreadsheetColorContext,
    TreePrintable {

    static SpreadsheetColorContextEnvironmentContext with(final EnvironmentContext environmentContext) {
        return new SpreadsheetColorContextEnvironmentContext(
            Objects.requireNonNull(environmentContext, "environmentContext")
        );
    }

    private SpreadsheetColorContextEnvironmentContext(final EnvironmentContext environmentContext) {
        super();

        this.environmentContext = environmentContext;
    }

    @Override
    public Optional<Color> lookupIndexedColor(final IndexedColor color) {
        SpreadsheetColors.checkIndexedColor(color);

        return this.environmentColorValue(
            "color" + color.index()
        );
    }

    @Override
    public Optional<Color> lookupNamedColor(final NamedColor color) {
        SpreadsheetColors.checkNamedColor(color);

        return this.environmentColorValue(
            "color" + color.value()
        );
    }

    private Optional<Color> environmentColorValue(final String environmentValueName) {
        return this.environmentContext.environmentValue(
            EnvironmentValueName.with(
                environmentValueName,
                Color.class
            )
        );
    }

    private final EnvironmentContext environmentContext;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.environmentContext.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetColorContextEnvironmentContext &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetColorContextEnvironmentContext other) {
        return this.environmentContext.equals(other.environmentContext);
    }

    @Override
    public String toString() {
        return this.environmentContext.toString();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());

        printer.indent();
        {
            TreePrintable.printTreeOrToString(
                this.environmentContext,
                printer
            );
        }
        printer.outdent();
    }
}
