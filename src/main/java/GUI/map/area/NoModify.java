package GUI.map.area;

import GUI.agent.container.AgentContainer;

public class NoModify extends EffectArea<Double> {

    public NoModify() {
        super(null, null);
    }

    @Override
    public Double get(AgentContainer<?> agentContainer) {
        return 1D;
    }

}
