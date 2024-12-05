package engine;

/**
 * Represents a game event that can update the GameState.
 */
public class GameEvent {

    public enum EventType {
        PLAYER_MOVE,       // 플레이어 이동
        PLAYER_SHOOT,      // 플레이어 총알 발사
        ENEMY_SPAWN,       // 적 생성
        ITEM_PICKUP,       // 아이템 획득
        GAME_OVER          // 게임 종료
    }

    private EventType eventType; // 이벤트 타입
    private Object eventData;    // 이벤트 데이터

    public GameEvent(EventType eventType, Object eventData) {
        this.eventType = eventType;
        this.eventData = eventData;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Object getEventData() {
        return eventData;
    }
}
