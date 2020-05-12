package Group9.map.dynamic;

import Group9.agent.container.AgentContainer;
import Group9.math.Vector2;
import Interop.Geometry.Point;
import Interop.Percept.Smell.SmellPerceptType;
import javafx.scene.shape.Circle;

public class Pheromone extends DynamicObject<AgentContainer<?>> {

    private final SmellPerceptType type;
    private final double initialRadius;
    private final int initialLifetime;

    private int turnsLeft;
    private Circle shape;
    private double radius;
    private double scalingFactor;

    public Pheromone(SmellPerceptType type, AgentContainer<?> source, Vector2 center, double radius, int lifetime) {
        super(source, center, radius, lifetime);
        this.initialRadius = radius;
        this.initialLifetime = lifetime;
        this.type = type;
    }

    public double getInitialRadius()
    {
        return this.initialRadius;
    }

    public int getInitialLifetime() {
        return initialLifetime;
    }

    public SmellPerceptType getType() {
        return type;
    }

    @Override
    public Pheromone clone() {
        return new Pheromone(type, getSource(), getCenter(), getRadius(), getLifetime());
    }
}
