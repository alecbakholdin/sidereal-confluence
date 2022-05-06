package com.bakholdin.siderealconfluence.model;

import lombok.Data;

import java.util.List;

@Data
public class Confluence {
    private int turn;
    private int sharingBonus;
    private int yengiiSharingBonus;
    private List<Integer> playerCounts;
}
