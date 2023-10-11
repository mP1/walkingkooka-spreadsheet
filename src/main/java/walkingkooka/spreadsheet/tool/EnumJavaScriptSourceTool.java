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

import walkingkooka.Cast;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelectionNavigation;
import walkingkooka.text.CharSequences;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printers;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.text.TextStylePropertyName;

import java.io.FileWriter;
import java.io.Writer;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class EnumJavaScriptSourceTool {

    public static void main(final String[] args) throws Exception {
        final Path reactSrc = Paths.get("../walkingkooka-spreadsheet-react/src/");

        generateExpressionNumberKind(Paths.get(reactSrc.toString(), "math"));
        generateRoundingMode(Paths.get(reactSrc.toString(), "math"));
        generateSpreadsheetEngineEvaluation(Paths.get(reactSrc.toString(), "spreadsheet", "engine"));
        generateSpreadsheetErrorKind(Paths.get(reactSrc.toString(), "spreadsheet"));
        generateSpreadsheetReferenceKind(Paths.get(reactSrc.toString(), "spreadsheet", "reference"));
        generateSpreadsheetViewportAnchor(Paths.get(reactSrc.toString(), "spreadsheet", "reference"));
        generateTextStylePropertyNames(Paths.get(reactSrc.toString(), "text"));
    }

    private static void generateExpressionNumberKind(final Path dest) throws Exception {
        generateEnums(
                ExpressionNumberKind.class,
                dest
        );
    }

    private static void generateRoundingMode(final Path dest) throws Exception {
        generateEnums(
                RoundingMode.class,
                dest
        );
    }

    private static void generateSpreadsheetEngineEvaluation(final Path dest) throws Exception {
        generateEnums(
                SpreadsheetEngineEvaluation.class,
                dest
        );
    }

    private static void generateSpreadsheetErrorKind(final Path dest) throws Exception {
        generateEnums(
                SpreadsheetErrorKind.class,
                dest
        );
    }

    private static void generateSpreadsheetReferenceKind(final Path dest) throws Exception {
        generateEnums(
                SpreadsheetReferenceKind.class,
                dest
        );
    }

    private static void generateSpreadsheetViewportAnchor(final Path dest) throws Exception {
        generateEnums(
                SpreadsheetViewportAnchor.class,
                "anchor",
                dest
        );
    }

    private static void generateTextStylePropertyNames(final Path dest) throws Exception {
        for (final TextStylePropertyName<?> property : TextStylePropertyName.values()) {
            final Optional<Class<Enum<?>>> maybeEnumType = property.enumType();
            if (maybeEnumType.isPresent()) {
                final Class<Enum<?>> enumType = maybeEnumType.get();

                generateEnums(
                        enumType,
                        dest
                );
            }
        }
    }

    private static void generateEnums(final Class<? extends Enum<?>> enumType,
                                      final Path dest) throws Exception {
        generateEnums(
                enumType,
                enumType.getSimpleName(),
                dest
        );
    }

    private static void generateEnums(final Class<? extends Enum<?>> enumType,
                                      final String label,
                                      final Path dest) throws Exception {
        try (final Writer writer = new FileWriter(sourceFilePath(dest, enumType).toFile())) {
            final IndentingPrinter printer = Printers.writer(writer, LineEnding.SYSTEM)
                    .indenting(Indentation.SPACES2);

            int importUp = 0;
            Path up = dest;

            for (; ; ) {
                if (up.getFileName().toString().equals("src")) {
                    break;
                }
                up = up.getParent();
                importUp++;
            }

            generateSource(
                    Cast.to(enumType),
                    importUp,
                    label,
                    printer
            );
            printer.flush();
        }
    }

    private static Path sourceFilePath(final Path parent,
                                       final Class<?> type) {
        return Paths.get(parent.toString(), type.getSimpleName() + ".js");
    }

    private static void generateSource(final Class<Enum<?>> enumClass,
                                       final int importUp,
                                       final String label,
                                       final IndentingPrinter printer) throws Exception {
        final String enumTypeName = enumClass.getSimpleName();

        printer.println("// generated by " + EnumJavaScriptSourceTool.class.getSimpleName() + " at " + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()));

        imports(importUp, printer);
        typeNameConstant(enumClass, printer);

        printer.println();

        // export default class RoundingMode extends SystemEnum {
        printer.println("export default class " + enumTypeName + " extends SystemEnum {");
        printer.indent();
        {
            constants(enumClass, printer);
            values(enumClass, printer);
            valueOf(enumClass, label, printer);
            fromJson(enumClass, printer);

            if (SpreadsheetViewportAnchor.class.equals(enumClass) || SpreadsheetViewportSelectionNavigation.class.equals(enumClass)) {
                from(enumClass, label, printer);
            }

            if (SpreadsheetReferenceKind.class.equals(enumClass)) {
                prefix(printer);
            }
            typeName(printer);
        }
        printer.outdent();
        printer.println("}");

        register(enumClass, printer);
    }

    /**
     * <pre>
     * import SystemEnum parse "../SystemEnum.js";
     * import SystemObject parse "../SystemObject.js";
     * </pre>
     */
    private static void imports(final int up, final IndentingPrinter printer) {
        printer.println();

        final String upString = CharSequences.repeating('!', up).toString().replace("!", "../");

        printer.println("import SystemEnum parse \"" + upString + "SystemEnum.js\";");
        printer.println("import SystemObject parse \"" + upString + "SystemObject.js\";");
    }

    /**
     * <pre>
     * const TYPE_NAME = "rounding-mode";
     * </pre>
     */
    private static void typeNameConstant(final Class<Enum<?>> enumType, final IndentingPrinter printer) {
        printer.println();
        printer.println("const TYPE_NAME = \"" + toKebabCase(enumType.getSimpleName()) + "\";");
    }

    private static void constants(final Class<Enum<?>> enumClass,
                                  final IndentingPrinter printer) throws Exception {
        printer.println();

        final String enumTypeName = enumClass.getSimpleName();

        for (final Enum<?> value : values(enumClass)) {
            // static UP = new RoundingMode("UP", "Up");
            printer.println("static " + value.name() + " = new " + enumTypeName + "(\"" + value.name() + "\");");
        }
    }

    /**
     * <pre>
     * static values() {
     *     return [
     *     ExpressionNumberKind.BIG_DECIMAL,
     *             ExpressionNumberKind.DOUBLE,
     *     ];
     * }
     * </pre>
     */
    private static void values(final Class<Enum<?>> enumClass,
                               final IndentingPrinter printer) throws Exception {
        printer.println();
        printer.println("static values() {");

        printer.indent();
        {
            printer.println("return [");
            printer.indent();
            {
                final String enumTypeName = enumClass.getSimpleName();

                final List<Enum<?>> enumValues = values(enumClass);
                int comma = enumValues.size() - 1;
                for (final Enum<?> enumValue : enumValues) {
                    printer.println(enumTypeName + "." + enumValue.name() + (--comma >= 0 ? "," : ""));
                }
            }
            printer.outdent();
            printer.println("];");
        }
        printer.outdent();
        printer.println("}");
    }

    /**
     * <pre>
     * static valueOf(name) {
     *     return SystemEnum.valueOf(name, label, ExpressionNumberKind.values());
     * }
     * </pre>
     */
    private static void valueOf(final Class<Enum<?>> enumClass,
                                final String label,
                                final IndentingPrinter printer) {
        printer.println();

        printer.println("static valueOf(name) {");

        printer.indent();
        {
            printer.println("return SystemEnum.valueOf(name, " + CharSequences.quoteAndEscape(label) + ", " + enumClass.getSimpleName() + ".values());");
        }
        printer.outdent();
        printer.println("}");
    }

    /**
     * <pre>
     * static parse(text) {
     *     return RoundingMode.parse(text, "RoundingMode", RoundingMode.values());
     * }
     * </pre>
     */
    private static void from(final Class<Enum<?>> enumClass,
                             final String label,
                             final IndentingPrinter printer) {
        printer.println();

        printer.println("static parse(text) {");
        printer.indent();
        {
            printer.println("return SystemObject.parse(text, " + CharSequences.quoteAndEscape(label) + ", " + enumClass.getSimpleName() + ".value());");
        }
        printer.outdent();
        printer.println("}");
    }

    /**
     * <pre>
     * static fromJson(name) {
     *     return RoundingMode.of(name);
     * }
     * </pre>
     */
    private static void fromJson(final Class<Enum<?>> enumClass,
                                 final IndentingPrinter printer) {
        printer.println();

        printer.println("static fromJson(name) {");
        printer.indent();
        {
            printer.println("return " + enumClass.getSimpleName() + ".valueOf(name);");
        }
        printer.outdent();
        printer.println("}");
    }

    /**
     * <pre>
     * prefix() {
     *   return SpreadsheetReferenceKind.ABSOLUTE === this ?
     *     "$" :
     *     "";
     * }
     * </pre>
     */
    private static void prefix(final IndentingPrinter printer) {
        printer.println();

        printer.println("prefix() {");

        printer.indent();
        {
            printer.println("return " + SpreadsheetReferenceKind.class.getSimpleName() + "." + SpreadsheetReferenceKind.ABSOLUTE + " === this ?");

            printer.indent();
            {
                printer.println(CharSequences.quoteAndEscape("$") + " :");
                printer.println(CharSequences.quoteAndEscape("") + " ;");
            }
            printer.outdent();

        }
        printer.outdent();
        printer.println("}");
    }

    /**
     * <pre>
     *   typeName() {
     *     return TYPE_NAME;
     *   }
     * </pre>
     */
    private static void typeName(final IndentingPrinter printer) {
        printer.println();

        printer.println("typeName() {");
        printer.indent();
        {
            printer.println("return TYPE_NAME;");
        }
        printer.outdent();
        printer.println("}");
    }

    /**
     * <pre>
     * SystemObject.register(TYPE_NAME, ExpressionNumberKind.fromJson);
     * </pre>
     */
    private static void register(final Class<Enum<?>> enumClass,
                                 final IndentingPrinter printer) {
        printer.println();
        printer.println("SystemObject.register(TYPE_NAME, " + enumClass.getSimpleName() + ".fromJson);");
    }

    private static CharSequence toKebabCase(final String name) {
        final StringBuilder b = new StringBuilder();

        for (final char c : name.toCharArray()) {
            if (b.length() > 0 && Character.isUpperCase(c)) {
                b.append('-');
            }
            b.append(Character.toLowerCase(c));
        }

        return b;
    }

    private static List<Enum<?>> values(final Class<Enum<?>> enumClass) throws Exception {
        final Enum<?>[] values = (Enum<?>[]) enumClass.getMethod("values")
                .invoke(null);
        return Arrays.asList(values);
    }
}
