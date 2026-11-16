package edu.calpoly.csc3100.fantasy;

/**
 * undefined
 * @author Marcus Hauen-Limkilde
 * @version 1.0
 */
public final class Shield implements Character {
  private final Character wrapped;
  public Shield(Character wrapped) { this.wrapped = java.util.Objects.requireNonNull(wrapped); }
  @Override public void draw(java.awt.Graphics2D g, int x, int y) {
    wrapped.draw(g, x, y);
    g.setColor(java.awt.Color.DARK_GRAY);
    g.fillOval(x + 1, y + 10, 8, 14);
    g.setColor(java.awt.Color.GREEN);
    g.fillOval(x + 2, y + 12, 4, 8);

  }

  public int attack() { return wrapped.attack(); }
  public int defense() { return wrapped.defense() + 5; }
  public String type() { return wrapped.type(); }
}
