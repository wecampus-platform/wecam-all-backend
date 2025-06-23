package org.example.wecambackend.dto.projection;

import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.OcrResult;


@Getter
@Setter
public class OcrEvaluationResult {

    private final OcrResult result;
    private final String reason;

    public OcrEvaluationResult(OcrResult result, String reason) {
        this.result = result;
        this.reason = reason;
    }
}
