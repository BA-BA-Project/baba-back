package com.baba.back.baby.dto;

import java.util.List;

public record BabiesResponse(List<BabyResponse> myBaby, List<BabyResponse> others) {
}
