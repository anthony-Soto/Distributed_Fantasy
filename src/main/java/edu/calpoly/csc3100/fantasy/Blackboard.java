package edu.calpoly.csc3100.fantasy;

import java.beans.PropertyChangeSupport;
import java.util.*;

/**
 * Player state and change notifications. The application confines access to the Swing EDT.
 * @author CSC 3100 Instructional Team
 * @author Marcus Hauen-Limkilde (moveLocal, equipLocal, unequipLocal)
 * @version 1.0
 */
public final class Blackboard extends PropertyChangeSupport {
  public static final String PLAYERS = "players";
  public static final int COLUMNS = 16;
  public static final int ROWS = 11;
  private final Map<String,PlayerState> players = new LinkedHashMap<>();
  private final GamePublisher publisher;
  private String localUsername;

  public Blackboard(GamePublisher publisher) {
    super(Blackboard.class);
    this.publisher = Objects.requireNonNull(publisher);
  }

  private void store(PlayerState state) {
    PlayerState previous = players.put(state.player(), state);
    firePropertyChange(new java.beans.PropertyChangeEvent(this, PLAYERS, previous, state));
  }
  private void commitLocal(PlayerState state) {
    store(state);
    publisher.publish(state);
  }
  public PlayerState getPlayer(String username) { return players.get(username); }
  public Collection<PlayerState> players() { return List.copyOf(players.values()); }
  public void addLocalPlayer(PlayerState state) {
    localUsername = state.player();
    store(state);
    publisher.publish(state);
  }
  public void moveLocal(String username, int dx, int dy) {
    if (!username.equals(localUsername)) return;
    if (dx < -1 || dx > 1 || dy < -1 || dy > 1) return;
    if ((dx == 0) == (dy == 0)) return;

    PlayerState current = players.get(username);
    if (current == null) return;
    int newX = current.x() + dx;
    int newY = current.y() + dy;
    if (newX < 0 || newX >= COLUMNS || newY < 0 || newY >= ROWS) return;
    PlayerState updated = new PlayerState(username,current.type(), newX, newY, current.equipment());
    commitLocal(updated);
  }
  public void applyRemote(PlayerState state) {
    if (!state.player().equals(localUsername)) store(state);
  }
  public void equipLocal(String username, String item) {
    if (!username.equals(localUsername)) return;
    if (!item.equals("sword") && !item.equals("shield") && !item.equals("shoes")) return;

    PlayerState current = players.get(username);
    if (current.equipment().contains(item)) return;
    List<String> equipment = new ArrayList<>(current.equipment());
    equipment.add(item);

    PlayerState updated = new PlayerState(username,current.type(), current.x(), current.y(), equipment);
    commitLocal(updated);
  }
  public void unequipLocal(String username, String item) {
    if (!username.equals(localUsername)) return;
    if (!item.equals("sword") && !item.equals("shield") && !item.equals("shoes")) return;

    PlayerState current = players.get(username);
    if (current == null) return;
    if (!current.equipment().contains(item)) return;
    List<String> equipment = new ArrayList<>(current.equipment());
    equipment.remove(item);

    PlayerState updated = new PlayerState(username,current.type(), current.x(), current.y(), equipment);
    commitLocal(updated);
  }
}
