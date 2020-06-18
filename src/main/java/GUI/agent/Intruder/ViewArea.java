package GUI.agent.Intruder;

import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPercepts;
import Interop.Percept.Vision.VisionPrecepts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ViewArea {
    private Distance range;
    private Angle alpha;
    private double direction;
    private Coordinate origin;
    private Coordinate left;
    private Coordinate right;
    public ViewArea(Distance range, Angle alpha, double direction, double originX, double originY)
    {
        this.range = range;
        this.alpha = alpha;
        this.direction = direction;
        this.origin = new Coordinate(originX, originY);
        constructArea();
    }
    private void constructArea()
    {
        double leftX = Math.cos(direction - 0.5 * alpha.getRadians()) * range.getValue();
        double leftY = Math.sin(direction - 0.5 * alpha.getRadians()) * range.getValue();
        this.left = new Coordinate(leftX, leftY);
        double rightX = Math.cos(direction + 0.5 * alpha.getRadians()) * range.getValue();
        double rightY = Math.sin(direction + 0.5 * alpha.getRadians()) * range.getValue();
        this.right = new Coordinate(rightX, rightY);

    }
    public double partContained(Cell c, ObjectPercepts objects)
    {
        c.setProcessed(true);
        c.addVisitedCount();
        ArrayList<Coordinate> pointsContained = pointsContained(c);
        setObjectsContained(c, objects);
        if(pointsContained.size() == 4)
        {
            return 1.0;
        }
        else if (pointsContained.size() == 0)
        {


            if(isIn(origin, c))
            {
                Line[] lines = {new Line(origin, left), new Line(left, right), new Line(right, origin)};
                return solveNone(lines, c.getPoints());
            }
            else
            {
                return 0.0;
            }
        }
        else if (pointsContained.size() == 1)
        {
            Line[] lines = {new Line(origin, left), new Line(left, right), new Line(right, origin)};
            double area = solveSingle(pointsContained.get(0), lines, c.getPoints());
            return area;
        }
        else if (pointsContained.size() == 2)
        {
            Line[] lines = {new Line(origin, left), new Line(left, right), new Line(right, origin)};
            double area = solveDouble(pointsContained, lines, c.getPoints());

            return area;
        }
        else if (pointsContained.size() == 3)
        {
            Line[] lines = {new Line(origin, left), new Line(left, right), new Line(right, origin)};
            double area = solveTriple(pointsContained, lines);
            if(area > 1.0)
            {
                //System.out.println("wrong: 3");
            }
            return area;
        }
        else
        {
            return 0.0;
        }
    }
    private boolean isIn(Coordinate p, Cell c)
    {
        Coordinate[] points = c.getPoints();
        return (p.getX() < points[0].getX() && p.getX() > points[1].getX() && p.getY() >  points[2].getY() && p.getY() < points[0].getY());
    }
    private double solveNone(Line[] lines, Coordinate[] allPoints)
    {
        Coordinate p1 = origin;
        Coordinate p2 = Line.getIntersectionPoint(lines[0], allPoints);
        Coordinate p3 = Line.getIntersectionPoint(lines[2], allPoints);
        if (p2 == null || p3 == null)
        {
            return 0.0;
        }
        if (p2.getX() == p3.getX())
        {
            return 0.5 * Math.abs(p2.getY() - p3.getY()) * Math.abs(p1.getX() - p2.getX());
        }
        else
        {
            return 0.5 * Math.abs(p2.getX() - p3.getX()) * Math.abs(p1.getY() - p2.getY());
        }
    }
    private double solveSingle(Coordinate point, Line[] lines, Coordinate[] allPoints)
    {
        ArrayList<Coordinate> remainingPoints = new ArrayList<>();
        Line lineHorizontal = new Line();
        Line lineVertical = new Line();
        for (Coordinate allPoint : allPoints) {
            if (!point.equals(allPoint)) {
                remainingPoints.add(allPoint);
            }
        }
        for (Coordinate remainingPoint : remainingPoints) {
            if (remainingPoint.getX() == point.getX()) {
                lineVertical = new Line(point, remainingPoint);
            }
            if (remainingPoint.getY() == point.getY()) {
                lineHorizontal = new Line(point, remainingPoint);
            }
        }
        Coordinate point1 = Line.getIntersectionPoint(lineHorizontal, lines);
        Coordinate point2 = Line.getIntersectionPoint(lineVertical, lines);
        double area = Math.abs((point1.getX() - point.getX()) * (point2.getY() - point.getY())) * 0.5;
        return area;
    }
    private double solveDouble(ArrayList<Coordinate> points, Line[] lines, Coordinate[] allPoints)
    {
        ArrayList<Coordinate> remainingPoints = new ArrayList<>();
        double area = 0;
        for (Coordinate allPoint : allPoints) {
            if (!points.get(0).equals(allPoint) && !points.get(1).equals(allPoint)) {
                remainingPoints.add(allPoint);
            }
        }
        if(points.get(0).getX() != remainingPoints.get(0).getX() && points.get(1).getX() != remainingPoints.get(0).getX())
        {
            Line line1 = new Line(points.get(0), new Coordinate(remainingPoints.get(0).getX(), points.get(0).getY()));
            Line line2 = new Line(points.get(1), new Coordinate(remainingPoints.get(0).getX(), points.get(1).getY()));
            Coordinate point1 = Line.getIntersectionPoint(line1, lines);
            Coordinate point2 = Line.getIntersectionPoint(line2, lines);
            area = Math.min(Math.abs(point1.getX() - points.get(0).getX()), Math.abs(point2.getX() - points.get(0).getX())) * 1 + 0.5 * Math.abs(point1.getX() - point2.getX());
        }
        else
        {
            Line line1 = new Line(points.get(0), new Coordinate(points.get(0).getX(), remainingPoints.get(0).getY()));
            Line line2 = new Line(points.get(1), new Coordinate(points.get(1).getX(), remainingPoints.get(0).getY()));
            Coordinate point1 = Line.getIntersectionPoint(line1, lines);
            Coordinate point2 = Line.getIntersectionPoint(line2, lines);
            area = Math.min(Math.abs(point1.getY()-points.get(0).getY()), Math.abs(point2.getY() - points.get(0).getY())) * 1 + 0.5 * Math.abs(point1.getY() - point2.getY());
        }
        return area;
    }
    private double solveTriple(ArrayList<Coordinate> points, Line[] lines)
    {
        double area = 0.5;//Three points means there is at least a triangle covered of area .5(The three points in the viewArea)
        if(points.get(0).getX() == points.get(1).getX() || points.get(0).getY() == points.get(1).getY())
        {
            if(points.get(0).getX() == points.get(2).getX())
            {
                Coordinate[] triangle1 = {points.get(1), points.get(2), Line.getIntersectionPoint(points.get(2), "h", (points.get(2).getX() - points.get(1).getX()), lines)};
                Coordinate[] triangle2 = {points.get(1), Line.getIntersectionPoint(points.get(2), "h", (points.get(2).getX() - points.get(1).getX()), lines), Line.getIntersectionPoint(points.get(1), "v", (points.get(1).getY()-points.get(2).getY()), lines)};
                area += getTriangleArea(triangle1);
                area += getTriangleArea(triangle2);
                if(area > 1.0)
                {
                    //System.out.println("1");
                }

                return area;
            }
            else if (points.get(0).getY() == points.get(2).getY())
            {
                Coordinate[] triangle1 = {points.get(1), points.get(2), Line.getIntersectionPoint(points.get(2), "v", (points.get(2).getY() - points.get(1).getY()), lines)};
                Coordinate[] triangle2 = {points.get(1), Line.getIntersectionPoint(points.get(2), "v", (points.get(2).getY() - points.get(1).getY()), lines), Line.getIntersectionPoint(points.get(1), "h", (points.get(1).getX()-points.get(2).getX()), lines)};
                area += getTriangleArea(triangle1);
                area += getTriangleArea(triangle2);
                if(area > 1.0)
                    System.out.println("2");
                return area;
            }
            else if (points.get(1).getX() == points.get(2).getX())
            {
                Coordinate[] triangle1 = {points.get(0), points.get(2), Line.getIntersectionPoint(points.get(2), "v", (points.get(2).getY() - points.get(0).getY()), lines)};
                Coordinate[] triangle2 = {points.get(0), Line.getIntersectionPoint(points.get(2), "v", (points.get(2).getY() - points.get(0).getY()), lines), Line.getIntersectionPoint(points.get(0), "h", (points.get(0).getX()-points.get(2).getX()), lines)};
                area += getTriangleArea(triangle1);
                area += getTriangleArea(triangle2);
                if(area > 1.0)
                {
                    // System.out.println("3");
                }
                return area;
            }
            else
            {
                Coordinate[] triangle1 = {points.get(0), points.get(2), Line.getIntersectionPoint(points.get(2), "h", (points.get(2).getX() - points.get(0).getX()), lines)};
                Coordinate[] triangle2 = {points.get(0), Line.getIntersectionPoint(points.get(2), "h", (points.get(2).getX() - points.get(0).getX()), lines), Line.getIntersectionPoint(points.get(0), "v", (points.get(0).getY()-points.get(2).getY()), lines)};
                area += getTriangleArea(triangle1);
                area += getTriangleArea(triangle2);
                if(area > 1.0)
                {
                    // System.out.println("4");
                }

                return area;
            }
        }
        else
        {
            if (points.get(2).getX() == points.get(0).getX())
            {
                Coordinate[] triangle1 = {points.get(0), points.get(1), Line.getIntersectionPoint(points.get(0), "h", (points.get(0).getX() - points.get(1).getX()), lines)};
                Coordinate[] triangle2 = {points.get(0), Line.getIntersectionPoint(points.get(0), "h", (points.get(0).getX() - points.get(1).getX()), lines), Line.getIntersectionPoint(points.get(1), "v", (points.get(1).getY()-points.get(0).getY()), lines)};
                area += getTriangleArea(triangle1);
                area += getTriangleArea(triangle2);
                return area;
            }
            else
            {
                Coordinate[] triangle1 = {points.get(0), points.get(1), Line.getIntersectionPoint(points.get(0), "v", (points.get(0).getY() - points.get(1).getY()), lines)};
                Coordinate[] triangle2 = {points.get(0), Line.getIntersectionPoint(points.get(0), "v", (points.get(0).getY() - points.get(1).getY()), lines), Line.getIntersectionPoint(points.get(1), "h", (points.get(1).getX()-points.get(0).getX()), lines)};
                area += getTriangleArea(triangle1);
                area += getTriangleArea(triangle2);
                return area;
            }
        }
    }
    private double getTriangleArea(Coordinate[] points)
    {
        Line base = new Line(points[0], points[1]);
        Line height = Line.getOrthogonal(base, points[2]);
        return base.getLength() * height.getLength() * .5;
    }
    public ArrayList<Coordinate> pointsContained(Cell c)
    {
        // The four corner points of the cell
        Coordinate[] points = c.getPoints();
        ArrayList<Coordinate> containedPoints = new ArrayList<>();
        Line[] lines = {new Line(origin, left), new Line(left, right), new Line(right, origin)};
        for(int i=0; i<points.length; i++)
        {
            containedPoints.add(points[i]);
            if(!checkHorizontal(lines, points[i].getX(), points[i].getY()) || !checkVertical(lines, points[i].getX(), points[i].getY()))
            {
                containedPoints.remove(containedPoints.size()-1);
            }
        }
        return containedPoints;
    }
    public void setObjectsContained(Cell c, ObjectPercepts objects)
    {
        ObjectPercept[] objcts = (ObjectPercept[]) objects.getAll().toArray(new ObjectPercept[objects.getAll().size()]);
        for(ObjectPercept object:objcts)
        {
            Point p = object.getPoint();
            Coordinate[] cornerPoints = c.getPoints();
            if (p.getX() < cornerPoints[0].getX() && p.getX() > cornerPoints[1].getX() && p.getY() < cornerPoints[0].getY() && p.getY() > cornerPoints[2].getY())
            {
                c.setObject(object.getType());
            }
        }
    }
    private boolean checkHorizontal(Line[] lines, double x, double y)
    {
        double highest = Double.NEGATIVE_INFINITY;
        double lowest = Double.POSITIVE_INFINITY;
        for(int i=0; i<lines.length; i++)
        {
            if(lines[i].getX(y) != 0.5)
            {
                if(highest < lines[i].getX(y))
                {
                    highest = lines[i].getX(y);
                }
                if(lowest > lines[i].getX(y))
                {
                    lowest = lines[i].getX(y);
                }
            }
        }
        if(x > lowest && x < highest)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    private boolean checkVertical(Line[] lines, double x, double y)
    {
        double highest = Double.NEGATIVE_INFINITY;
        double lowest = Double.POSITIVE_INFINITY;
        for(int i=0; i<lines.length; i++)
        {
            if(lines[i].getY(x) != 0.5)
            {
                if(highest < lines[i].getY(x))
                {
                    highest = lines[i].getY(x);
                }
                if(lowest > lines[i].getY(x))
                {
                    lowest = lines[i].getY(x);
                }
            }
        }
        if(y > lowest && y < highest)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    @Override
    public String toString()
    {
        return "ViewArea: " + origin + " " + left + " " + right;
    }


}
