package com.adab.service.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskParserService {

    private final ObjectMapper objectMapper;

    public List<Map<String, String>> parseJsonFromResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            // JSON 배열 추출 (정규식: \[[\s\S]*\])
            Pattern pattern = Pattern.compile("\\[([\\s\\S]*)\\]");
            Matcher matcher = pattern.matcher(response.trim());

            if (matcher.find()) {
                String jsonArray = "[" + matcher.group(1) + "]";

                // JSON 문자열 값 내부의 이스케이프되지 않은 줄바꿈 처리
                jsonArray = fixUnescapedNewlines(jsonArray);

                return objectMapper.readValue(jsonArray, new TypeReference<List<Map<String, String>>>() {
                });
            }
        } catch (Exception e) {
            log.error("Failed to parse JSON from LLM response: {}", e.getMessage());
            log.debug("Full response that failed: {}", response);
        }
        return Collections.emptyList();
    }

    /**
     * JSON 문자열 값 내부의 이스케이프되지 않은 줄바꿈을 이스케이프 처리
     */
    public String fixUnescapedNewlines(String json) {
        if (json == null)
            return null;

        StringBuilder result = new StringBuilder();
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (escaped) {
                result.append(c);
                escaped = false;
                continue;
            }

            if (c == '\\') {
                result.append(c);
                escaped = true;
                continue;
            }

            if (c == '"') {
                result.append(c);
                inString = !inString;
                continue;
            }

            // 문자열 내부의 줄바꿈 문자를 이스케이프
            if (inString && (c == '\n' || c == '\r')) {
                if (c == '\n') {
                    result.append("\\n");
                } else if (c == '\r') {
                    result.append("\\r");
                }
                continue;
            }

            result.append(c);
        }

        return result.toString();
    }
}
