package GUI.map.area;

import GUI.agent.container.AgentContainer;
import GUI.map.objects.MapObject;
import GUI.math.Vector2;
import GUI.tree.PointContainer;

public abstract class ModifyLocationEffect extends EffectArea<Vector2> {

    public ModifyLocationEffect(MapObject parent, PointContainer pointContainer) {
        super(parent, pointContainer);
    }

    @Override
    public abstract Vector2 get(AgentContainer<?> agentContainer);

}
