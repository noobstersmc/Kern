package net.noobsters.kern.paper.punishments;

public enum PunishmentType {
    MUTE, BAN, WARNING;

    public String getDBName() {
        switch (this) {
            case MUTE:
                return "mutes";
            case BAN:
                return "bans";
            case WARNING:
                return "warns";
            default: {
                return this.toString().toLowerCase();
            }
        }
    }
}
