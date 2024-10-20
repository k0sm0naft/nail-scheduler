package fern.nail.art.nailscheduler.telegram.model;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PatternAndRegex {
    DATE("([0-2][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.\\d{2}", "dd.MM.yy"),
    TIME("([0-1][0-9]|2[0-3]):[0-5][0-9]", "HH:mm"),
    SPACE("\\s", " "),
    DAYS_OF_WEEK("([1-7](\\s[1-7])*)", "d"),
    ISO_DATE("\\d{4}-\\d{2}-\\d{2}","yyyy-MM-dd");

    private final String pattern;
    private final String regex;

    public static String getPatternOf(List<PatternAndRegex> patterns) {
        return patterns.stream()
                .map(patternAndRegex -> patternAndRegex.pattern)
                .collect(Collectors.joining());
    }

    public static String getRegexOf(List<PatternAndRegex> patterns) {
        return patterns.stream()
                .map(patternAndRegex -> patternAndRegex.regex)
                .collect(Collectors.joining());
    }
}
