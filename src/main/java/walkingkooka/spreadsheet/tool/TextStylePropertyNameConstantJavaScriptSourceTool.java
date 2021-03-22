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

package walkingkooka.spreadsheet.tool;

import walkingkooka.text.Indentation;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printers;
import walkingkooka.tree.text.TextStylePropertyName;

/**
 * When run prints out javascript constants for each of the {@link TextStylePropertyName} properties.
 */
public final class TextStylePropertyNameConstantJavaScriptSourceTool {

    public static void main(final String[] args) {
        try (final IndentingPrinter printer = Printers.sysOut().indenting(Indentation.with("  "))) {
            for (final TextStylePropertyName name : TextStylePropertyName.values()) {
                printer.println("static " + name.constantName() + " = \"" + name.value() + "\";");
            }
            printer.flush();
        }
    }
}
