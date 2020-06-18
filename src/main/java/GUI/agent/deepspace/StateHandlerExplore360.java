package GUI.agent.deepspace;

import GUI.math.graph.Vertex;
import Interop.Action.GuardAction;
import Interop.Action.NoAction;
import Interop.Percept.GuardPercepts;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class StateHandlerExplore360 implements StateHandler {

    private final Queue<ActionContainer<GuardAction>> actionsQueue = new LinkedList<>();
    private DeepSpace ds;
    private StateType nextState = StateType.EXPLORE_360;

    // 'false -> true' after first use of this state
    private boolean active = false;

    public StateHandlerExplore360() {
    }

    @Override
    public ActionContainer<GuardAction> execute(GuardPercepts percepts, DeepSpace ds) {
        ActionContainer<GuardAction> retAction = ActionContainer.of(this, new Inaction());
        this.ds = ds;

        if (!active) {
            init(percepts);
            active = true;
        }

        if (!actionsQueue.isEmpty()) {
            retAction = actionsQueue.poll();
        }

        // add objects seen to the current vertex
        ds.getCurrentVertex().getContent().add(percepts);

        postExecute();
        return retAction;
    }

    void postExecute() {
        if (actionsQueue.isEmpty()) {
            nextState = StateType.FIND_NEW_TARGET;
            active = false;
        } else {
            nextState = StateType.EXPLORE_360;
        }
    }

    // inits the graph (or adds a new vertex)  &  schedules rotations
    private void init(GuardPercepts percepts) {

        Optional<Vertex<DataContainer>> closeVertex = ds.currentGraph.getVertices().stream()
                .filter(e -> e.getContent().getCenter().distance(ds.getPosition()) < 0.01)
                .findAny();
        if(closeVertex.isPresent())
        {
            ds.setCurrentVertex(closeVertex.get());
            return;
        }

        Vertex<DataContainer> newVertex = new Vertex<>(new DataContainer(this.ds, this.ds.getPosition().clone(),
                percepts.getVision().getFieldOfView().getRange().getValue()));


        if(ds.getCurrentVertex() != null)
        {
            ds.currentGraph.add(ds.getCurrentVertex(), newVertex, ds.calculateCost(ds.getCurrentVertex(), newVertex), true);
        }
        ds.currentGraph.add(ds.setCurrentVertex(newVertex));

        actionsQueue.addAll(ds.planRotation(percepts, Math.PI * 2));
    }

    @Override
    public StateType getNextState() {
        return this.nextState;
    }

    @Override
    public void resetState() {
        actionsQueue.clear();
        active = false;
    }
}
