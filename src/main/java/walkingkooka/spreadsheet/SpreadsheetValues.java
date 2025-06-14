
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

import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.validation.ValidationValueTypeName;

import java.util.Set;

/**
 * A collection of {@link ValidationValueTypeName} that may appear within a {@link SpreadsheetCell}.
 */
public final class SpreadsheetValues implements PublicStaticHelper {

    public static final ValidationValueTypeName BOOLEAN = ValidationValueTypeName.BOOLEAN;

    public static final ValidationValueTypeName CELL = ValidationValueTypeName.with("cell");

    public static final ValidationValueTypeName COLUMN = ValidationValueTypeName.with("column");

    public static final ValidationValueTypeName CONDITION = ValidationValueTypeName.with("condition");

    public static final ValidationValueTypeName DATE = ValidationValueTypeName.DATE;

    public static final ValidationValueTypeName DATE_TIME = ValidationValueTypeName.DATE_TIME;

    public static final ValidationValueTypeName ERROR = ValidationValueTypeName.with("error");

    public static final ValidationValueTypeName LABEL = ValidationValueTypeName.with("label");

    public static final ValidationValueTypeName NUMBER = ValidationValueTypeName.NUMBER;

    public static final ValidationValueTypeName ROW = ValidationValueTypeName.with("row");

    public static final ValidationValueTypeName TEMPLATE_VALUE_NAME = ValidationValueTypeName.with("template-value-name");

    public static final ValidationValueTypeName TEXT = ValidationValueTypeName.TEXT;

    public static final ValidationValueTypeName TIME = ValidationValueTypeName.TIME;

    public static final ValidationValueTypeName VALUE_OR_EXPRESSION = ValidationValueTypeName.with("valueOrExpression");

    /**
     * Does not include all types, only those that typically appear in a cell
     */
    public static final Set<ValidationValueTypeName> ALL = Sets.of(
            BOOLEAN,
            DATE,
            DATE_TIME,
            NUMBER,
            TEXT,
            TIME
    );

    /**
     * Stop creation
     */
    private SpreadsheetValues() {
        throw new UnsupportedOperationException();
    }
}
