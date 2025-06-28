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

package walkingkooka.spreadsheet.convert;

import walkingkooka.collect.list.Lists;
import walkingkooka.color.convert.ColorConverters;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.net.convert.NetConverters;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.formula.parser.DateSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DateTimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.NumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.TimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.template.convert.TemplateConverters;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.convert.ExpressionNumberConverters;
import walkingkooka.tree.json.convert.JsonNodeConverters;
import walkingkooka.tree.text.convert.TreeTextConverters;
import walkingkooka.validation.convert.ValidatorConvertConverters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A collection of factory methods for creating {@link Converter} converters.
 */
public final class SpreadsheetConverters implements PublicStaticHelper {

    /**
     * A basic {@link Converter} that supports number -> number, date -> datetime, time -> datetime.
     */
    public static Converter<SpreadsheetConverterContext> basic() {
        return BASIC_CONVERTER;
    }

    private final static Converter<SpreadsheetConverterContext> BASIC_CONVERTER = Converters.collection(
            Lists.of(
                    Converters.simple(),
                    textToText(),
                    numberToNumber(),
                    Converters.localDateToLocalDateTime(),
                    Converters.localTimeToLocalDateTime()
            )
    );

    /**
     * {@see Converters#collection}
     */
    public static <C extends ConverterContext> Converter<C> collection(final List<Converter<C>> converters) {
        return Converters.collection(
                converters
        );
    }

    /**
     * {@see SpreadsheetConverterSpreadsheetErrorThrowing}
     */
    public static Converter<SpreadsheetConverterContext> errorThrowing() {
        return SpreadsheetConverterSpreadsheetErrorThrowing.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterSpreadsheetErrorToNumber}
     */
    public static Converter<SpreadsheetConverterContext> errorToNumber() {
        return SpreadsheetConverterSpreadsheetErrorToNumber.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterFormatPatternToString}
     */
    public static Converter<SpreadsheetConverterContext> formatPatternToString(final String pattern) {
        return SpreadsheetConverterFormatPatternToString.with(pattern);
    }

    /**
     * {@see SpreadsheetConverterGeneral}
     */
    public static Converter<SpreadsheetConverterContext> general(final SpreadsheetFormatter dateFormatter,
                                                                 final Parser<SpreadsheetParserContext> dateParser,
                                                                 final SpreadsheetFormatter dateTimeFormatter,
                                                                 final Parser<SpreadsheetParserContext> dateTimeParser,
                                                                 final SpreadsheetFormatter numberFormatter,
                                                                 final Parser<SpreadsheetParserContext> numberParser,
                                                                 final SpreadsheetFormatter textFormatter,
                                                                 final SpreadsheetFormatter timeFormatter,
                                                                 final Parser<SpreadsheetParserContext> timeParser) {
        return SpreadsheetConverterGeneral.with(
                dateFormatter,
                dateParser,
                dateTimeFormatter,
                dateTimeParser,
                numberFormatter,
                numberParser,
                textFormatter,
                timeFormatter,
                timeParser
        );
    }

    /**
     * {@see TreeTextConverters#hasTextStyleToTextStyle}
     */
    public static Converter<SpreadsheetConverterContext> hasTextStyleToTextStyle() {
        return TreeTextConverters.hasTextStyleToTextStyle();
    }

    /**
     * {@see JsonNodeConverters#jsonNodeTo}
     */
    public static Converter<SpreadsheetConverterContext> jsonTo() {
        return JsonNodeConverters.jsonNodeTo();
    }

    /**
     * {@see SpreadsheetConverterNullToNumber}
     */
    public static Converter<SpreadsheetConverterContext> nullToNumber() {
        return SpreadsheetConverterNullToNumber.INSTANCE;
    }

    /**
     * {@see ExpressionNumberConverters#numberToNumber}
     */
    public static Converter<SpreadsheetConverterContext> numberToNumber() {
        return ExpressionNumberConverters.numberToNumber();
    }

    /**
     * A wrapper around {@link Converters#parser(Class, Parser, Function, BiFunction)} simplifying the abstraction.
     */
    public static <V> Converter<SpreadsheetConverterContext> parser(final Class<V> parserValueType,
                                                                    final Parser<SpreadsheetParserContext> parser,
                                                                    final BiFunction<ParserToken, SpreadsheetConverterContext, V> parserTokenToValue) {
        return Converters.parser(
                parserValueType, // parserValueType
                parser, // parser
                (final SpreadsheetConverterContext scc) -> SpreadsheetParserContexts.basic(
                        InvalidCharacterExceptionFactory.POSITION,
                        scc,
                        scc,
                        '*' // valueSeparator not required because not parsing multiple values.
                ),
                parserTokenToValue
        );
    }

    /**
     * {@see SpreadsheetSelectionToSpreadsheetSelectionConverter}
     */
    public static Converter<SpreadsheetConverterContext> selectionToSelection() {
        return SpreadsheetSelectionToSpreadsheetSelectionConverter.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterSpreadsheetSelectionToText}
     */
    public static Converter<SpreadsheetConverterContext> selectionToText() {
        return SpreadsheetConverterSpreadsheetSelectionToText.INSTANCE;
    }

    /**
     * {@see Converters#simple}
     */
    public static Converter<SpreadsheetConverterContext> simple() {
        return Converters.simple();
    }

    /**
     * {@see SpreadsheetConverterSpreadsheetCell}
     */
    public static Converter<SpreadsheetConverterContext> spreadsheetCellTo() {
        return SpreadsheetConverterSpreadsheetCell.INSTANCE;
    }

    /**
     * {@see ColorConverters#textToConverter}
     */
    public static Converter<SpreadsheetConverterContext> textToColor() {
        return ColorConverters.textToConverter();
    }

    /**
     * A {@link Converter} that uses the given {@link Parser} to parse text into a {@link DateSpreadsheetFormulaParserToken} and converting
     * that into a {@link LocalDate}.
     */
    public static Converter<SpreadsheetConverterContext> textToDate(final Parser<SpreadsheetParserContext> parser) {
        return parser(
                LocalDate.class, // parserValueType
                parser,
                (final ParserToken t,
                 final SpreadsheetConverterContext scc) -> t.cast(DateSpreadsheetFormulaParserToken.class)
                        .toLocalDate(scc)
        );
    }

    /**
     * A {@link Converter} that uses the given {@link Parser} to parse text into a {@link DateTimeSpreadsheetFormulaParserToken} and converting
     * that into a {@link LocalDateTime}.
     */
    public static Converter<SpreadsheetConverterContext> textToDateTime(final Parser<SpreadsheetParserContext> parser) {
        return parser(
                LocalDateTime.class, // parserValueType
                parser,
                (final ParserToken t,
                 final SpreadsheetConverterContext scc) -> t.cast(DateTimeSpreadsheetFormulaParserToken.class)
                        .toLocalDateTime(scc)
        );
    }

    /**
     * {@see SpreadsheetConverterTextToExpression}
     */
    public static Converter<SpreadsheetConverterContext> textToExpression() {
        return SpreadsheetConverterTextToExpression.INSTANCE;
    }

    /**
     * {@see ValidatorConvertConverters.textToFormName}
     */
    public static Converter<SpreadsheetConverterContext> textToFormName() {
        return ValidatorConvertConverters.textToFormName();
    }

    /**
     * {@see JsonNodeConverters#textToJsonNode}
     */
    public static Converter<SpreadsheetConverterContext> textToJson() {
        return JsonNodeConverters.textToJsonNode();
    }

    /**
     * A {@link Converter} that uses the given {@link Parser} to parse text into a {@link NumberSpreadsheetFormulaParserToken} and converting
     * that into a {@link Number}.
     */
    public static Converter<SpreadsheetConverterContext> textToNumber(final Parser<SpreadsheetParserContext> parser) {
        return ExpressionNumberConverters.toExpressionNumberThen(
                parser(
                        ExpressionNumber.class, // parserValueType
                        parser,
                        (final ParserToken t,
                         final SpreadsheetConverterContext scc) -> t.cast(NumberSpreadsheetFormulaParserToken.class)
                                .toNumber(scc)
                ),
                numberToNumber()
        );
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetSelection}
     */
    public static Converter<SpreadsheetConverterContext> textToSelection() {
        return SpreadsheetConverterTextToSpreadsheetSelection.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetColorName}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetColorName() {
        return SpreadsheetConverterTextToSpreadsheetColorName.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetError}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetError() {
        return SpreadsheetConverterTextToSpreadsheetError.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetFormatterSelector}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetFormatterSelector() {
        return SpreadsheetConverterTextToSpreadsheetFormatterSelector.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetId}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetId() {
        return SpreadsheetConverterTextToSpreadsheetId.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetMetadata}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetMetadata() {
        return SpreadsheetConverterTextToSpreadsheetMetadata.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetMetadataPropertyName}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetMetadataPropertyName() {
        return SpreadsheetConverterTextToSpreadsheetMetadataPropertyName.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetMetadataColor}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetMetadataColor() {
        return SpreadsheetConverterTextToSpreadsheetMetadataColor.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetName}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetName() {
        return SpreadsheetConverterTextToSpreadsheetName.INSTANCE;
    }

    /**
     * {@see SpreadsheetConverterTextToSpreadsheetText}
     */
    public static Converter<SpreadsheetConverterContext> textToSpreadsheetText() {
        return SpreadsheetConverterTextToSpreadsheetText.INSTANCE;
    }

    /**
     * {@see TemplateConverters#textToTemplateValueName()}
     */
    public static Converter<SpreadsheetConverterContext> textToTemplateValueName() {
        return TemplateConverters.textToTemplateValueName();
    }

    /**
     * {@see Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString}
     */
    public static Converter<SpreadsheetConverterContext> textToText() {
        return Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString();
    }

    /**
     * {@see TreeTextConverters.textToTextNode()}
     */
    public static Converter<SpreadsheetConverterContext> textToTextNode() {
        return TreeTextConverters.textToTextNode();
    }

    /**
     * {@see TreeTextConverters.textToTextStyle()}
     */
    public static Converter<SpreadsheetConverterContext> textToTextStyle() {
        return TreeTextConverters.textToTextStyle();
    }

    /**
     * {@see TreeTextConverters.textToTextStylePropertyName()}
     */
    public static Converter<SpreadsheetConverterContext> textToTextStylePropertyName() {
        return TreeTextConverters.textToTextStylePropertyName();
    }

    /**
     * A {@link Converter} that uses the given {@link Parser} to parse text into a {@link TimeSpreadsheetFormulaParserToken} and converting
     * that into a {@link LocalTime}.
     */
    public static Converter<SpreadsheetConverterContext> textToTime(final Parser<SpreadsheetParserContext> parser) {
        return parser(
                LocalTime.class, // parserValueType
                parser,
                (final ParserToken t,
                 final SpreadsheetConverterContext scc) -> t.cast(TimeSpreadsheetFormulaParserToken.class)
                        .toLocalTime()
        );
    }

    /**
     * {@see NetConverters#textToUrl()}
     */
    public static Converter<SpreadsheetConverterContext> textToUrl() {
        return NetConverters.textToUrl();
    }

    /**
     * {@see SpreadsheetConverterTextToValidationError}
     */
    public static Converter<SpreadsheetConverterContext> textToValidationError() {
        return SpreadsheetConverterTextToValidationError.INSTANCE;
    }

    /**
     * {@see ValidatorConvertConverters.textToValidatorSelector}
     */
    public static Converter<SpreadsheetConverterContext> textToValidatorSelector() {
        return ValidatorConvertConverters.textToValidatorSelector();
    }

    /**
     * {@see ValidatorConvertConverters.textToValidationValueTypeName}
     */
    public static Converter<SpreadsheetConverterContext> textToValueType() {
        return ValidatorConvertConverters.textToValidationValueTypeName();
    }

    /**
     * {@see JsonNodeConverters#toJsonNode}
     */
    public static Converter<SpreadsheetConverterContext> toJson() {
        return JsonNodeConverters.toJsonNode();
    }

    /**
     * {@see TreeTextConverters#hasTextNodeToTextNode}
     */
    public static Converter<SpreadsheetConverterContext> toTextNode() {
        return TreeTextConverters.hasTextNodeToTextNode();
    }

    /**
     * {@see SpreadsheetConverterUnformattedNumber}
     */
    public static Converter<SpreadsheetConverterContext> unformattedNumber() {
        return SpreadsheetConverterUnformattedNumber.INSTANCE;
    }

    /**
     * {@see TreeTextConverters.urlToHyperlink()}
     */
    public static Converter<SpreadsheetConverterContext> urlToHyperlink() {
        return TreeTextConverters.urlToHyperlink();
    }

    /**
     * {@see TreeTextConverters.urlToImage()}
     */
    public static Converter<SpreadsheetConverterContext> urlToImage() {
        return TreeTextConverters.urlToImage();
    }

    /**
     * Stop creation
     */
    private SpreadsheetConverters() {
        throw new UnsupportedOperationException();
    }
}
