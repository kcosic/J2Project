package main.hr.kcosic.project.utils;


import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;

public class DrawingUtils {
    public static final ArrayList<Color> ladderColors = new ArrayList<>(
            List.of(
                Color.rgb(63, 48, 29),
                Color.rgb(101, 42, 14),
                Color.rgb(46, 21, 3),
                Color.rgb(128, 71, 28),
                Color.rgb(63, 48, 29),
                Color.rgb(75, 55, 28),
                Color.rgb(121, 92, 52),
                Color.rgb(53, 35, 21),
                Color.rgb(227, 192, 153),
                Color.rgb(195, 132, 82)
            )
    );

    public static final ArrayList<Color> snakeColors = new ArrayList<>(
            List.of(
                Color.rgb(2, 81, 33),
                Color.rgb(26, 103, 47),
                Color.rgb(26, 148, 47),
                Color.rgb(117, 195, 109),
                Color.rgb(60, 154, 66),
                Color.rgb(74, 145, 75),
                Color.rgb(177, 224, 170),
                Color.rgb(174, 243, 89),
                Color.rgb(3, 192, 74),
                Color.rgb(61, 237, 152)
            )
    );

    public static void drawLadder(GraphicsContext gc, double startX, double startY, double endX, double endY, Color ladderColor)
    {
        var startPoint = new Point2D(startX, startY);
        var endPoint = new Point2D(endX, endY);
        var distanceBetweenPoles = 30;
        var halfWidth = distanceBetweenPoles / 2;
        var rad90 = Math.PI /2;
        var angle = Math.toRadians(getAngle(startPoint, endPoint));
        var leftPoleTop = getPointInRelation(new Point2D(startX, startY), angle + rad90, halfWidth);
        var topBottomDistance = calculateDistanceBetweenPoints(startPoint, endPoint);
        var distanceIncrement = calculateLadderIncrement(topBottomDistance);
        var lastPointLeft = leftPoleTop;

        Point2D lastPointRight = null;

        gc.setLineWidth(3);
        gc.setStroke(ladderColor);

        for (double i = 0; i < topBottomDistance; i = i+distanceIncrement) {
            var firstPoint = getPointInRelation(lastPointLeft, angle, distanceIncrement);
            gc.strokeLine( lastPointLeft.getX(), lastPointLeft.getY(),firstPoint.getX(), firstPoint.getY());

            var secondPoint = getPointInRelation(firstPoint, angle - rad90, distanceBetweenPoles);
            gc.strokeLine( firstPoint.getX(), firstPoint.getY(),secondPoint.getX(), secondPoint.getY());
            lastPointRight = secondPoint;

            var thirdPoint = getPointInRelation(secondPoint, angle - (2 * rad90), distanceIncrement);
            gc.strokeLine( secondPoint.getX(), secondPoint.getY(),thirdPoint.getX(), thirdPoint.getY());

            lastPointLeft = firstPoint;
        }


        if (lastPointRight != null) {
            gc.strokeLine(
                    lastPointRight.getX(),
                    lastPointRight.getY(),
                    getPointInRelation(lastPointRight, angle, 10).getX(),
                    getPointInRelation(lastPointRight, angle, 10).getY());
        }
        gc.strokeLine(
                lastPointLeft.getX(),
                lastPointLeft.getY(),
                getPointInRelation(lastPointLeft, angle, 10).getX(),
                getPointInRelation(lastPointLeft, angle, 10).getY());


    }

    private static double calculateLadderIncrement(double maxHeight) {
        if(maxHeight >= 1600){
            return maxHeight / 36;
        }else if(maxHeight >= 1400 && maxHeight < 1600){
            return maxHeight / 32;
        }else if(maxHeight >= 1200 && maxHeight < 1400){
            return maxHeight / 28;
        }else if(maxHeight >= 1000 && maxHeight < 1200){
            return maxHeight / 24;
        }else if(maxHeight >= 800 && maxHeight < 1000){
            return maxHeight / 20;
        }else if(maxHeight >= 600 && maxHeight < 800){
            return maxHeight / 16;
        }else if(maxHeight >= 400 && maxHeight < 600){
            return maxHeight / 12;
        }else if(maxHeight >= 200 && maxHeight < 400){
            return maxHeight / 8;
        }else {
            return maxHeight / 4;
        }
    }

    private static double calculateSnakeIncrement(double maxHeight) {
        if(maxHeight >= 1600){
            return maxHeight / 9;
        }else if(maxHeight >= 1400 && maxHeight < 1600){
            return maxHeight / 8;
        }else if(maxHeight >= 1200 && maxHeight < 1400){
            return maxHeight / 7;
        }else if(maxHeight >= 1000 && maxHeight < 1200){
            return maxHeight / 6;
        }else if(maxHeight >= 800 && maxHeight < 1000){
            return maxHeight / 5;
        }else if(maxHeight >= 600 && maxHeight < 800){
            return maxHeight / 4;
        }else if(maxHeight >= 400 && maxHeight < 600){
            return maxHeight / 3;
        }else if(maxHeight >= 200 && maxHeight < 400){
            return maxHeight / 2;
        }else {
            return maxHeight / 1;
        }
    }

    private static double getAngle(Point2D p1, Point2D p2) {
        return  Math.toDegrees(Math.atan2(p2.getY() - p1.getY(),  p2.getX() - p1.getX()));
    }

    private static Point2D getPointInRelation(Point2D startPoint, double angle, double distanceFromStartPoint){

        return new Point2D(
                 startPoint.getX() + distanceFromStartPoint * Math.cos(angle),
                startPoint.getY()+ distanceFromStartPoint * Math.sin(angle)
        );
    }

    private static double calculateDistanceBetweenPoints(
            Point2D p1,
            Point2D p2) {
        return Math.sqrt((p2.getY() - p1.getY()) * (p2.getY() - p1.getY()) + (p2.getX() - p1.getX()) * (p2.getX() - p1.getX()));
    }

    public static void drawSnake(GraphicsContext gc, double startX, double startY, double endX, double endY, Color snakeBodyColor)
    {
        var eyeDistance = 6;
        var eyeRadius = 10;
        var pupilRadius = 5;
        var headFi = 40;
        var headRadius = (headFi - 10) / 2;
        var snakeEyeColor = Color.WHITE;
        var snakePupilColor = Color.BLACK;

        var startPoint = new Point2D(startX, startY);
        var endPoint = new Point2D(endX, endY);
        var distanceBetweenPoles = 30;
        var halfWidth = distanceBetweenPoles / 2;
        var rad90 = Math.PI /2;
        var rad120 = Math.toRadians(120);
        var angle = Math.toRadians(getAngle(startPoint, endPoint));

        //body

        var topBottomDistance = calculateDistanceBetweenPoints(startPoint, endPoint);
        var distanceIncrement = calculateSnakeIncrement(topBottomDistance);

        var startPointPath = getPointInRelation(startPoint, angle, headRadius);
        var startPointLeftSide = getPointInRelation(
                getPointInRelation(startPoint, angle + rad90, distanceIncrement  / 3),
                angle,
                headRadius);
        var startPointRightSide = getPointInRelation(
                getPointInRelation(startPoint, angle - rad90, distanceIncrement  / 3),
                angle,
                headRadius);

        gc.beginPath();
        gc.setLineWidth(10);
        gc.setStroke(snakeBodyColor);
        gc.moveTo(startPoint.getX(), startPoint.getY());
        gc.lineTo(startPointPath.getX(), startPointPath.getY());

        var leftCurvePoint = getPointInRelation(startPointLeftSide, angle, distanceIncrement * 0.66667);
        var rightCurvePoint = getPointInRelation(startPointRightSide, angle, distanceIncrement * 0.33334);
        var endCurvePoint = getPointInRelation(startPointPath, angle, distanceIncrement);

        for (double i = 0; i < topBottomDistance; i = i+distanceIncrement) {
            gc.bezierCurveTo(
                    rightCurvePoint.getX(), /*first curve point x*/
                    rightCurvePoint.getY(), /*first curvve point y*/
                    leftCurvePoint.getX(), /*second curve point x*/
                    leftCurvePoint.getY(), /*second curve point y*/

                    endCurvePoint.getX(), /*end curvve point y*/
                    endCurvePoint.getY() /*end curvve point y*/
            );

            leftCurvePoint = getPointInRelation(leftCurvePoint, angle, distanceIncrement);
            rightCurvePoint = getPointInRelation(rightCurvePoint, angle, distanceIncrement);
            endCurvePoint = getPointInRelation(endCurvePoint, angle, distanceIncrement);
        }

        gc.stroke();
        gc.closePath();


        //head
        drawSnakeHead(gc, headFi, headRadius, eyeDistance, eyeRadius, pupilRadius, snakeBodyColor, snakeEyeColor, snakePupilColor, startPoint,angle);

    }

    private static void drawSnakeHead(GraphicsContext gc, int headFi, int headRadius, int eyeDistance, int eyeRadius, int pupilRadius, Color snakeBodyColor, Color snakeEyeColor, Color snakePupilColor, Point2D startPoint, double angle) {
        var rad90 = Math.toRadians(90);
        var rad120 = Math.toRadians(120);
        var rad180 = Math.toRadians(180);

        var headPoint1 = getPointInRelation(startPoint, angle + rad90, headRadius);
        var centeredHeadPoint = getPointInRelation(headPoint1, angle + rad180, headRadius);

        gc.setFill(snakeBodyColor);
        gc.save();
        var rotation = new Rotate(Math.toDegrees(angle) - 90, centeredHeadPoint.getX(), centeredHeadPoint.getY());
        gc.setTransform(rotation.getMxx(), rotation.getMyx(), rotation.getMxy(), rotation.getMyy(),
                rotation.getTx(), rotation.getTy());
        gc.fillOval(
                centeredHeadPoint.getX(),
                centeredHeadPoint.getY(),
                headFi,
                headFi);

        gc.restore();

        //eye left
        var leftEyeCenterPoint = getPointInRelation(
                getPointInRelation(startPoint, angle + rad180, headRadius),
                angle + rad90,
                eyeDistance / 2 + eyeRadius);

        gc.setFill(snakeEyeColor);
        gc.fillOval(
                leftEyeCenterPoint.getX(),
                leftEyeCenterPoint.getY(),
                eyeRadius,
                eyeRadius);

        gc.setFill(snakePupilColor);
        gc.fillOval(
                leftEyeCenterPoint.getX(),
                leftEyeCenterPoint.getY(),
                pupilRadius,
                pupilRadius);

        //eye right
        var rightEyeCenterPoint = getPointInRelation(
                getPointInRelation(startPoint, angle + rad180, headRadius),
                angle - rad90,
                eyeDistance / 2);
        gc.setFill(snakeEyeColor);
        gc.fillOval(
                rightEyeCenterPoint.getX(),
                rightEyeCenterPoint.getY(),
                eyeRadius,
                eyeRadius);
        gc.setFill(snakePupilColor);
        gc.fillOval(
                rightEyeCenterPoint.getX(),
                rightEyeCenterPoint.getY(),
                pupilRadius,
                pupilRadius);

    }

}
