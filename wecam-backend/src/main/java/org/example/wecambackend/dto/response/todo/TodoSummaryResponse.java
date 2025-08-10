package org.example.wecambackend.dto.response.todo;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoSummaryResponse {

    private CountPair todayTodo;       // 오늘의 할 일
    private RateTriple weekTodo;       // 이번 주 완료율
    private CountPair receivedTodo;    // 받은 일 현황
    private CountPair sentTodo;        // 보낸 일 현황

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CountPair {
        private int done;
        private int total;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RateTriple {
        private int done;
        private int total;
        private int rate; // 완료율 (0~100)
    }
}
