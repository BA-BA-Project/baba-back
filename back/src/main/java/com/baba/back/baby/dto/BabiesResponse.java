package com.baba.back.baby.dto;

import java.util.List;

public record BabiesResponse(List<IsMyBabyResponse> myBaby, List<IsMyBabyResponse> others) {
}
