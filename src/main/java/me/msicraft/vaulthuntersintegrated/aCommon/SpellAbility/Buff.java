package me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility;

public class Buff {

    private final SpellAbility spellAbility;
    private String buffName;
    private final int seconds;
    private final long endTime;

    public Buff(SpellAbility spellAbility, int seconds, String buffName) {
        this.spellAbility = spellAbility;
        if (buffName != null) {
            this.buffName = buffName;
        } else {
            this.buffName = spellAbility.getDisplayName();
        }
        if (this.buffName == null) {
            this.buffName = "Unknown";
        }
        this.seconds = seconds;
        this.endTime = (long) (System.currentTimeMillis() + (seconds * 1000L));
    }

    public SpellAbility getSpellAbility() {
        return spellAbility;
    }

    public String getBuffName() {
        return buffName;
    }

    public void setBuffName(String buffName) {
        this.buffName = buffName;
    }

    public int getSeconds() {
        return seconds;
    }

    public long getEndTime() {
        return endTime;
    }

}
