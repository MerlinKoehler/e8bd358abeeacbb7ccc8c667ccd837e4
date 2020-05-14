package GUI.map.area;

import GUI.agent.container.AgentContainer;
import GUI.map.objects.MapObject;
import GUI.tree.Container;
import GUI.tree.PointContainer;

public abstract class EffectArea<T> implements Container<PointContainer> {

    private final MapObject parent;
    private final PointContainer pointContainer;

    public EffectArea(MapObject parent, PointContainer pointContainer)
    {
        this.parent = parent;
        this.pointContainer = pointContainer;
    }

    public MapObject getParent() {
        return parent;
    }

    abstract public T get(AgentContainer<?> agentContainer);

    @Override
    public PointContainer getContainer() {
        return pointContainer;
    }

}
