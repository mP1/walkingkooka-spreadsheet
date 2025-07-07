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

/**
 * TODO create a subclass that extracts the source and destination types parse a {@link ClassCastException#getMessage()}.
 * <br>
 * <a href="https://github.com/mP1/walkingkooka-spreadsheet/issues/2302">issues/2302</a>
 */
final class SpreadsheetErrorKindClassCastExceptionMessage {

    // "walkingkooka.spreadsheet.SpreadsheetErrorKindTest cannot be cast to java.base/java.lang.Void"
    // ->
    // class walkingkooka.spreadsheet.SpreadsheetError cannot be cast to class walkingkooka.tree.expression.ExpressionNumber
    // (walkingkooka.spreadsheet.SpreadsheetError and walkingkooka.tree.expression.ExpressionNumber are in unnamed module of loader 'app')
    //
    // Failed to convert $FROM to $TO
    static String extractClassCastExceptionMessage(final String message) {
        String result = message;

        final String[] tokens = message.split(" ");
        if (tokens.length > 7) {
            // class walkingkooka.spreadsheet.SpreadsheetError cannot be cast to class walkingkooka.tree.expression.ExpressionNumber
            //
            // 0=class
            // 1=walkingkooka.spreadsheet.SpreadsheetError
            // 2=cannot
            // 3=be
            // 4=cast
            // 5=to
            // 6=class
            // 7=walkingkooka.tree.expression.ExpressionNumber
            if (tokens[0].equals("class") &&
                tokens[2].equals("cannot") &&
                tokens[3].equals("be") &&
                tokens[4].equals("cast") &&
                tokens[5].equals("to") &&
                tokens[6].equals("class")) {
                result = classClassNameCannotBeCastToClass(tokens);
            }
        }
        if (tokens.length > 2) {
            // "walkingkooka.spreadsheet.SpreadsheetErrorKindTest cannot be cast to java.base/java.lang.Void"
            //
            // 0="walkingkooka.spreadsheet.SpreadsheetErrorKindTest
            // 1=cannot
            // 2=be
            // 3=cast
            // 4=to
            // 5=java.base/java.lang.Void"
            if (tokens[1].equals("cannot") &&
                tokens[2].equals("be") &&
                tokens[3].equals("cast") &&
                tokens[4].equals("to")) {
                result = classCannotBeCastTo(tokens);
            }
        }
        return result;
    }

    // class walkingkooka.spreadsheet.SpreadsheetError cannot be cast to class walkingkooka.tree.expression.ExpressionNumber
    // (walkingkooka.spreadsheet.SpreadsheetError and walkingkooka.tree.expression.ExpressionNumber are in unnamed module of loader 'app')
    //
    // 0=class
    // 1=walkingkooka.spreadsheet.SpreadsheetError
    // 2=cannot
    // 3=be
    // 4=cast
    // 5=to
    // 6=class
    // 7=walkingkooka.tree.expression.ExpressionNumber
    //
    // parse = 1
    // to = 7
    private static String classClassNameCannotBeCastToClass(final String[] tokens) {
        return failedToConvert(
            tokens[1],
            tokens[7]
        );
    }

    // "walkingkooka.spreadsheet.SpreadsheetErrorKindTest cannot be cast to java.base/java.lang.Void"

    // parse = first
    // to = last
    private static String classCannotBeCastTo(final String[] tokens) {
        return failedToConvert(
            tokens[0],
            tokens[tokens.length - 1]
        );
    }

    private static String failedToConvert(final String from,
                                          final String to) {
        return "Failed to convert " + extractSimpleClassName(from) + " to " + extractSimpleClassName(to);
    }

    private static String extractSimpleClassName(final String typeName) {
        String simple = typeName;

        // there could be a mention in message in parens
        final int leftParens = simple.indexOf('(');
        if (-1 != leftParens) {
            simple = simple.substring(0, leftParens);
        }

        final int slash = simple.indexOf('/');
        if (-1 != slash) {
            simple = simple.substring(slash + 1);
        }

        final int lastDot = simple.lastIndexOf('.');
        if (-1 != lastDot) {
            simple = simple.substring(lastDot + 1);
        }

        return simple;
    }
}
