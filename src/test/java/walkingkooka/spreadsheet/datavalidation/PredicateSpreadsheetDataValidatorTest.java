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

package walkingkooka.spreadsheet.datavalidation;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.set.Sets;
import walkingkooka.predicate.Predicates;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PredicateSpreadsheetDataValidatorTest extends SpreadsheetDataValidatorTemplateTestCase<PredicateSpreadsheetDataValidator<String>, String> {

    private final static String VALUE = "abc123";
    private final static String TO_STRING = "toString123";

    @Test
    public void testWithNullClassFails() {
        assertThrows(NullPointerException.class, () -> PredicateSpreadsheetDataValidator.with(null,
                this.predicate(),
                TO_STRING));
    }

    @Test
    public void testWithNullPredicateFails() {
        assertThrows(NullPointerException.class, () -> PredicateSpreadsheetDataValidator.with(String.class,
                null,
                TO_STRING));
    }

    @Test
    public void testWithNullToStringFails() {
        assertThrows(NullPointerException.class, () -> PredicateSpreadsheetDataValidator.with(String.class,
                this.predicate(),
                null));
    }

    @Test
    public void testPredicatePass() {
        this.validatePassCheck(VALUE);
    }

    @Test
    public void testPredicateFalse() {
        this.validateFailCheck("Value that fails validation");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createSpreadsheetDataValidator(), TO_STRING);
    }

    @Override
    public PredicateSpreadsheetDataValidator<String> createSpreadsheetDataValidator() {
        return PredicateSpreadsheetDataValidator.with(
                String.class,
                this.predicate(),
                TO_STRING
        );
    }

    private Predicate<String> predicate() {
        return Predicates.setContains(Sets.of(VALUE));
    }

    @Override
    public String value() {
        return VALUE;
    }

    @Override
    public Class<String> valueType() {
        return String.class;
    }

    @Override
    public SpreadsheetDataValidatorContext createContext() {
        return SpreadsheetDataValidatorContexts.fake();
    }

    @Override
    public Class<PredicateSpreadsheetDataValidator<String>> type() {
        return Cast.to(PredicateSpreadsheetDataValidator.class);
    }
}
