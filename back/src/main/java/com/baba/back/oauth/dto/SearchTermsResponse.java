package com.baba.back.oauth.dto;

import java.util.List;

public record SearchTermsResponse(List<TermsResponse> terms) {
}
