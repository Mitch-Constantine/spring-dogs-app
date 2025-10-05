package com.example.springdogs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGptResponse {
    private Choice[] choices;
    private Usage usage;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private Message message;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Message {
            private String content;
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }
}
