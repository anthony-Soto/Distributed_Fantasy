package edu.calpoly.csc3100.fantasy;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.Set;

/**
 * undefined
 * @author CSC 3100 Instructional Team
 * @version 1.0
 */
public final class PlayerCodec {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private PlayerCodec() { }
  public static String encode(PlayerState state) {
    try { return MAPPER.writeValueAsString(state); }
    catch (Exception e) { throw new IllegalArgumentException("Cannot encode player", e); }
  }
  public static PlayerState decode(String json) {
    // TODO: decode JSON and validate the required fields, type, board coordinates,
    // and equipment before returning a snapshot. See the README and acceptance tests.
    

    PlayerState state;
    try {
      JsonNode root = MAPPER.readTree(json);
      if (root == null || !root.isObject()
          || !root.hasNonNull("player") || !root.hasNonNull("type")
          || !root.hasNonNull("x") || !root.hasNonNull("y")
          || !root.hasNonNull("equipment")) {
        throw new IllegalArgumentException("Incomplete player snapshot");
      }
      if (!root.get("player").isTextual() || root.get("player").textValue().isBlank()
          || !root.get("type").isTextual() || !root.get("x").canConvertToInt()
          || !root.get("y").canConvertToInt() || !root.get("equipment").isArray()) {
        throw new IllegalArgumentException("Invalid player snapshot");
      }
      for (JsonNode item : root.get("equipment")) {
        if (!item.isTextual()) throw new IllegalArgumentException("Invalid player equipment");
      }
      state = MAPPER.treeToValue(root, PlayerState.class);
    } catch (JacksonException e) {
      throw new IllegalArgumentException("Cannot decode player", e);
    }

    if (!"warrior".equals(state.type())
        || state.x() < 0 || state.x() >= Blackboard.COLUMNS
        || state.y() < 0 || state.y() >= Blackboard.ROWS) {
      throw new IllegalArgumentException("Invalid player snapshot");
    }

    Set<String> supportedEquipment = Set.of("sword", "shield", "shoes");
    Set<String> equipment = new HashSet<>();
    for (String item : state.equipment()) {
      if (!supportedEquipment.contains(item) || !equipment.add(item)) {
        throw new IllegalArgumentException("Invalid player equipment");
      }
    }
    return state;
  }
}
