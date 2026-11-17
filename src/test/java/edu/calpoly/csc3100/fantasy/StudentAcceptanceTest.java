package edu.calpoly.csc3100.fantasy;

import org.junit.jupiter.api.Test;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class StudentAcceptanceTest {
  private final List<PlayerState> sent = new ArrayList<>();          // fake publisher
  private final List<PropertyChangeEvent> events = new ArrayList<>();
  private final Blackboard board = new Blackboard(sent::add);

  StudentAcceptanceTest() {
    board.addPropertyChangeListener(Blackboard.PLAYERS, events::add);
    board.addLocalPlayer(new PlayerState("alice", "warrior", 2, 3, List.of()));
  }

  @Test void equipMoveUnequipPublishesEachChangeOnceAndKeepsOrder() {
    board.equipLocal("alice", "sword");
    board.equipLocal("alice", "shoes");
    board.moveLocal("alice", 1, 0);
    board.unequipLocal("alice", "sword");
    
    PlayerState expected = new PlayerState("alice", "warrior", 3, 3, List.of("shoes"));
    assertAll(
      () -> assertEquals(expected, board.getPlayer("alice")),
      () -> assertEquals(5, sent.size()),
      () -> assertEquals(expected, sent.getLast()),
      () -> assertEquals(5, events.size())
    );
  }

  @Test void unequippingRebuildsCharacterWithoutBonus() {
    board.equipLocal("alice", "sword");
    Character characterWithSword = CharacterFactory.create(board.getPlayer("alice"));
    assertEquals(15, characterWithSword.attack());
    board.unequipLocal("alice", "sword");
    Character characterWithoutSword = CharacterFactory.create(board.getPlayer("alice"));
    assertEquals(10, characterWithoutSword.attack());
  }
  @Test void remoteSnapshotReachesBoardWithoutEchoAndBadMessagesAreIgnored() {
  IncomingPlayerHandler handler =
      new IncomingPlayerHandler("team", "alice", board::applyRemote);
  String bobTopic = Subscriber.topicPrefix("team") + "bob";
  PlayerState bob = new PlayerState("bob", "warrior", 5, 5, List.of("shield"));

  handler.accept(bobTopic, PlayerCodec.encode(bob).getBytes(java.nio.charset.StandardCharsets.UTF_8));
  handler.accept(bobTopic, "not json".getBytes(java.nio.charset.StandardCharsets.UTF_8));
  
  assertAll( 
    () -> assertEquals(bob, board.getPlayer("bob")),
    () -> assertEquals(1, sent.size()),
    () -> assertEquals(2, events.size())
  );
}
}