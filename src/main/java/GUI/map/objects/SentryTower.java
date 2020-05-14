package GUI.map.objects;

import GUI.map.ViewRange;
import GUI.map.area.ModifySpeedEffect;
import GUI.map.area.ModifyViewRangeEffect;
import GUI.tree.PointContainer;
import Interop.Percept.Vision.ObjectPerceptType;

public class SentryTower extends MapObject {

    public SentryTower(PointContainer area, double sentrySlowdownModifier, ViewRange viewRange) {
        super(area, ObjectPerceptType.SentryTower);
        this.addEffects(
                new ModifySpeedEffect(this, area, sentrySlowdownModifier,sentrySlowdownModifier),
                new ModifyViewRangeEffect(this, area, viewRange)
        );
    }

}
