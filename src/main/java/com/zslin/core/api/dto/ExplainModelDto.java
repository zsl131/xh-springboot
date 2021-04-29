package com.zslin.core.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExplainModelDto {

    private ExplainDto explain;

    private List<ExplainOperationDto> operationList;
}
