package GUI.map.area;

import GUI.agent.container.AgentContainer;
import GUI.map.ViewRange;
import GUI.map.objects.MapObject;
import GUI.tree.PointContainer;

public class ModifyViewRangeEffect extends EffectArea<ViewRange> {

    private final ViewRange viewRange;

    public ModifyViewRangeEffect(MapObject parent, PointContainer pointContainer, ViewRange viewRange) {
        super(parent, pointContainer);
        this.viewRange = viewRange;
    }

    @Override
    public ViewRange get(AgentContainer<?> agentContainer) {
        return this.viewRange;
    }
}
