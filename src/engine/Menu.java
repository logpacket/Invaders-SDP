package engine;

public enum Menu {
    GAME_SETTING,
    SHOP,
    ACHIEVEMENT,
    RANKING,
    SETTING,
    EXIT,
    MAIN,
    SINGLE_PLAY,
    MULTI_PLAY,
    CREDIT,
    SCORE,
    LOGIN,
    SIGN_UP,
    MATCHMAKING;

    static final Menu[] TITLE_MENU = {GAME_SETTING, SHOP, ACHIEVEMENT, RANKING, SETTING, EXIT};
    private static final int TITLE_MENU_COUNT = 6;

    public Menu getNext() {
        return TITLE_MENU[(this.ordinal() + 1) % TITLE_MENU_COUNT];
    }

    public Menu getPrev() {
        return TITLE_MENU[(this.ordinal() - 1 + TITLE_MENU_COUNT) % TITLE_MENU_COUNT];
    }
}
