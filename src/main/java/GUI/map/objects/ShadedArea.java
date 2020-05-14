package GUI.map.objects;

import GUI.map.area.ModifyViewEffect;
import GUI.tree.PointContainer;
import Interop.Percept.Vision.ObjectPerceptType;

public class ShadedArea extends MapObject {
    public ShadedArea(PointContainer area, double guardModifier, double intruderModifier) {
        super(area, ObjectPerceptType.ShadedArea);
        this.addEffects(new ModifyViewEffect(this, area, guardModifier, intruderModifier));
    }
}
