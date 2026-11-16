package edu.calpoly.csc3100.fantasy;

/**
 * undefined
 * @author Marcus Hauen-Limkilde
 * @version 1.0
 */
public final class Sword implements Character {
  private final Character wrapped;
  public Sword(Character wrapped) { this.wrapped = java.util.Objects.requireNonNull(wrapped); }
  @Override public void draw(java.awt.Graphics2D g, int x, int y) {
    wrapped.draw(g, x, y);
    g.setColor(java.awt.Color.LIGHT_GRAY);
    g.fillRect(x + 33, y + 4,  3, 18);
    g.setColor(new java.awt.Color(120, 70, 20));
    g.fillRect(x + 31, y + 22, 7, 2);
    g.fillRect(x + 33, y + 24, 3, 4);
  }

  public int attack() { return wrapped.attack() + 5; }
  public int defense() { return wrapped.defense(); }
  public String type() { return wrapped.type(); }
}
