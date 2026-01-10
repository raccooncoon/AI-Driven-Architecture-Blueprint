package com.adab.service.analysis;

import com.adab.dto.RequirementAnalysisResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequirementAnalysisServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock(answer = org.mockito.Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @Test
    void analyze_shouldReturnResponse() {
        // Arrange
        String input = "The system shall support user login via Google OAuth";
        RequirementAnalysisResponse expectedResponse = new RequirementAnalysisResponse();
        expectedResponse.setCategory("Authentication");
        expectedResponse.setDetail("Implement OAuth 2.0 flow with Google Provider.");
        expectedResponse.setSummary("Google Login");

        when(chatClientBuilder.build()).thenReturn(chatClient);

        // Mock Fluent API behavior
        when(chatClient.prompt()
                .system(anyString())
                .user(anyString())
                .call()
                .entity(RequirementAnalysisResponse.class))
                .thenReturn(expectedResponse);

        RequirementAnalysisService service = new RequirementAnalysisService(chatClientBuilder);

        // Act
        RequirementAnalysisResponse actual = service.analyze(input);

        // Assert
        assertEquals("Authentication", actual.getCategory());
        assertEquals("Google Login", actual.getSummary());
    }
}
