package com.put.swolarz.servicediscoveryapi.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class ResultsPage<T> {
    private int page;
    private int perPage;
    private long totalNumber;

    private List<T> results;

    public static <T> Builder<T> builder(Class<T> dtoClass){
        return new Builder<>();
    }

    public static class Builder<T> {
        private int page;
        private int perPage;
        private long totalNumber;
        private List<T> results;

        public ResultsPage<T> build() {
            return new ResultsPage<T>(page, perPage, totalNumber, results);
        }

        public Builder<T> page(int page) {
            this.page = page;
            return this;
        }

        public Builder<T> perPage(int perPage) {
            this.perPage = perPage;
            return this;
        }

        public Builder<T> totalNumber(long totalNumber) {
            this.totalNumber = totalNumber;
            return this;
        }

        public Builder<T> results(List<T> results) {
            this.results = results;
            return this;
        }
    }
}
