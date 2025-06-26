package com.sv.qlbh.models;
public enum CustomerGroup {
    
    NORMAL("Khách hàng thường", 0, 1_000_000, "🥉"),
    SILVER("Khách hàng bạc", 1_000_000, 5_000_000, "🥈"), 
    GOLD("Khách hàng vàng", 5_000_000, 20_000_000, "🥇"),
    DIAMOND("Khách hàng kim cương", 20_000_000, Double.MAX_VALUE, "💎");
    
    private final String displayName;
    private final double minSpent;
    private final double maxSpent;
    private final String icon;
    
    CustomerGroup(String displayName, double minSpent, double maxSpent, String icon) {
        this.displayName = displayName;
        this.minSpent = minSpent;
        this.maxSpent = maxSpent;
        this.icon = icon;
    }
    
    public static CustomerGroup getGroupBySpent(double totalSpent) {
        for (CustomerGroup group : values()) {
            if (totalSpent >= group.minSpent && totalSpent < group.maxSpent) {
                return group;
            }
        }
        return NORMAL;
    }
    
    public CustomerGroup getNextLevel() {
        switch (this) {
            case NORMAL: return SILVER;
            case SILVER: return GOLD;
            case GOLD: return DIAMOND;
            case DIAMOND: return null;
            default: return null;
        }
    }
    
    public double getAmountToNextLevel(double currentSpent) {
        CustomerGroup nextLevel = getNextLevel();
        if (nextLevel == null) {
            return 0;
        }
        return nextLevel.minSpent - currentSpent;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public double getMinSpent() {
        return minSpent;
    }
    
    public double getMaxSpent() {
        return maxSpent;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public String getDisplayNameWithIcon() {
        return icon + " " + displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
