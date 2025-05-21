package src.GUI;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.ADT.Board;
import src.ADT.Car;

/**
 * Animation class for the Rush Hour Puzzle Solver GUI.
 */
public class Animation {
    
    // Cell size constants
    private static final int CELL_PADDING = 2;
    private static final int BORDER_WIDTH = 2;
    
    // Car rectangles mapped by their IDs
    private Map<Character, Rectangle> carRectangles = new HashMap<>();
    
    // Car labels mapped by their IDs
    private Map<Character, Text> carLabels = new HashMap<>();
    
    // Animation timelines
    private SequentialTransition movesAnimation;
    
    // State tracking
    private int cellSize = 60; 
    private int currentStep = 0;
    private int totalSteps = 0;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private Board currentBoard;
    private Text stepCountText;

    /**
     * Initialize the animation
     * 
     * @param board The initial board state
     * @param moves The list of moves to animate
     * @param stepCountText The text object to update with step count
     */
    public void initialize(Board board, List<int[]> moves, Text stepCountText) {
        this.currentBoard = board.copy();
        this.totalSteps = moves.size();
        this.currentStep = 0;
        this.stepCountText = stepCountText;
        updateStepCount();
    }
    
    /**
     * Draw the board on the provided pane
     * 
     * @param board The board to draw
     * @param boardPane The pane to draw on
     */
    public void drawBoard(Board board, Pane boardPane) {
        boardPane.getChildren().clear();
        carRectangles.clear();
        carLabels.clear();
        
        int rows = board.getRows();
        int cols = board.getCols();
        
        // Create the grid background
        Rectangle background = new Rectangle(
            cols * cellSize + 2 * BORDER_WIDTH,
            rows * cellSize + 2 * BORDER_WIDTH
        );
        background.setFill(Color.rgb(15, 15, 15)); // Very dark background
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(BORDER_WIDTH);
        boardPane.getChildren().add(background);
        
        // Draw grid cells with black borders and dark gray fill
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Rectangle cell = new Rectangle(
                    cellSize - CELL_PADDING * 2,
                    cellSize - CELL_PADDING * 2
                );
                // Make empty cells dark gray with full opacity
                cell.setFill(Color.rgb(40, 40, 40, 1.0)); // Lighter than the border
                cell.setStroke(Color.rgb(10, 10, 10)); // Very dark border, almost black
                cell.setStrokeWidth(2.0);
                cell.setTranslateX(col * cellSize + BORDER_WIDTH + CELL_PADDING);
                cell.setTranslateY(row * cellSize + BORDER_WIDTH + CELL_PADDING);
                cell.setArcHeight(8);
                cell.setArcWidth(8);
                boardPane.getChildren().add(cell);
            }
        }
        
        // Draw the exit
        drawExit(board, boardPane);
        
        // Draw the cars
        for (Car car : board.getCars()) {
            drawCar(car, board, boardPane);
        }
    }

    /**
     * Draw the exit on the board
     * 
     * @param board The board
     * @param boardPane The pane to draw on
     */
    private void drawExit(Board board, Pane boardPane) {
        String exitSide = board.getExitSide();
        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();
        
        // Exit arrow colors
        Color exitFill = Color.LIGHTGREEN;
        Color exitStroke = Color.GREEN;
        
        // Create exit rectangle and arrow
        Rectangle exitRect = new Rectangle(cellSize, cellSize);
        exitRect.setFill(exitFill);
        exitRect.setStroke(exitStroke);
        exitRect.setStrokeWidth(2);
        exitRect.setArcHeight(8);
        exitRect.setArcWidth(8);
        
        Polygon arrow = new Polygon();
        
        if (exitSide != null) {
            if (exitSide.equalsIgnoreCase("RIGHT")) {
                // Position exit to the right of the board
                exitRect.setTranslateX(board.getCols() * cellSize + BORDER_WIDTH);
                exitRect.setTranslateY(exitRow * cellSize + BORDER_WIDTH);
                
                // Draw arrow pointing left
                arrow.getPoints().addAll(
                    exitRect.getTranslateX() + 10.0, exitRect.getTranslateY() + cellSize/2 - 10.0,
                    exitRect.getTranslateX() + 30.0, exitRect.getTranslateY() + cellSize/2,
                    exitRect.getTranslateX() + 10.0, exitRect.getTranslateY() + cellSize/2 + 10.0
                );
            } else if (exitSide.equalsIgnoreCase("LEFT")) {
                // Position exit to the left of the board
                exitRect.setTranslateX(-cellSize);
                exitRect.setTranslateY(exitRow * cellSize + BORDER_WIDTH);
                
                // Draw arrow pointing right
                arrow.getPoints().addAll(
                    exitRect.getTranslateX() + cellSize - 10.0, exitRect.getTranslateY() + cellSize/2 - 10.0,
                    exitRect.getTranslateX() + cellSize - 30.0, exitRect.getTranslateY() + cellSize/2,
                    exitRect.getTranslateX() + cellSize - 10.0, exitRect.getTranslateY() + cellSize/2 + 10.0
                );
            } else if (exitSide.equalsIgnoreCase("BOTTOM")) {
                // Position exit below the board
                exitRect.setTranslateX(exitCol * cellSize + BORDER_WIDTH);
                exitRect.setTranslateY(board.getRows() * cellSize + BORDER_WIDTH);
                
                // Draw arrow pointing up
                arrow.getPoints().addAll(
                    exitRect.getTranslateX() + cellSize/2 - 10.0, exitRect.getTranslateY() + 10.0,
                    exitRect.getTranslateX() + cellSize/2, exitRect.getTranslateY() + 30.0,
                    exitRect.getTranslateX() + cellSize/2 + 10.0, exitRect.getTranslateY() + 10.0
                );
            } else if (exitSide.equalsIgnoreCase("TOP")) {
                // Position exit above the board
                exitRect.setTranslateX(exitCol * cellSize + BORDER_WIDTH);
                exitRect.setTranslateY(-cellSize);
                
                // Draw arrow pointing down
                arrow.getPoints().addAll(
                    exitRect.getTranslateX() + cellSize/2 - 10.0, exitRect.getTranslateY() + cellSize - 10.0,
                    exitRect.getTranslateX() + cellSize/2, exitRect.getTranslateY() + cellSize - 30.0,
                    exitRect.getTranslateX() + cellSize/2 + 10.0, exitRect.getTranslateY() + cellSize - 10.0
                );
            }
            
            arrow.setFill(Color.DARKGREEN);
            boardPane.getChildren().addAll(exitRect, arrow);
        }
    }

    /**
     * Draw a car on the board
     */
    private void drawCar(Car car, Board board, Pane boardPane) {
        char id         = car.getId();
        int startRow    = car.getStartRow();
        int startCol    = car.getStartCol();
        int length      = car.getLength();
        int orientation = car.getOrientation();

        // Dimensions
        int w = (orientation == Board.HORIZONTAL)
                ? length * cellSize - 2*CELL_PADDING
                : cellSize - 2*CELL_PADDING;
        int h = (orientation == Board.VERTICAL)
                ? length * cellSize - 2*CELL_PADDING
                : cellSize - 2*CELL_PADDING;

        // Rectangle
        Rectangle rect = new Rectangle(w, h);
        rect.setTranslateX(startCol * cellSize + BORDER_WIDTH + CELL_PADDING);
        rect.setTranslateY(startRow * cellSize + BORDER_WIDTH + CELL_PADDING);
        rect.setArcWidth(12);
        rect.setArcHeight(12);

        // Gradient fill
        double hue, sat, bri;
        if (car.isPrimary()) {
            hue = 0; sat = 0.85; bri = 0.90;
        } else {
            hue = ((id - 'A') * 37) % 360;
            if (hue < 30 || hue > 330) hue = (hue + 45) % 360;
            sat = 0.7 + ((id - 'A') % 3)*0.1;
            bri = 0.75 + ((id - 'A') % 5)*0.05;
        }
        Color base  = Color.hsb(hue, sat, bri);
        Color light = Color.hsb(hue, Math.max(0.5,sat-0.2), Math.min(1,bri+0.2));
        LinearGradient grad = (orientation == Board.HORIZONTAL)
            ? new LinearGradient(0,0,0,1,true, CycleMethod.NO_CYCLE,
                new Stop(0, light), new Stop(1, base))
            : new LinearGradient(0,0,1,0,true, CycleMethod.NO_CYCLE,
                new Stop(0, light), new Stop(1, base));
        rect.setFill(grad);

        // Label
        Text lbl = new Text(String.valueOf(id));
        lbl.setFill(Color.BLACK);
        lbl.setFont(Font.font("System", 18));
        lbl.setStroke(Color.BLACK);
        lbl.setStrokeWidth(1.5);
        lbl.setTranslateX(rect.getTranslateX() + w/2 - 6);
        lbl.setTranslateY(rect.getTranslateY() + h/2 + 6);

        // Store
        carRectangles.put(id, rect);
        carLabels.put(id,    lbl);

        // Add to pane
        boardPane.getChildren().addAll(rect, lbl);

        // _Only_ the current moved car gets an effect
        Integer hi = board.getCurrentMovedCarIndex();
        int     idx = board.getCars().indexOf(car);
        if (hi != null && hi == idx) {
            highlightCar(rect);
        } else {
            rect.setEffect(null);
        }
    }


    /**
     * Highlight a car to show it's currently being moved
     * 
     * @param carRect The car rectangle to highlight
     */
    private void highlightCar(Rectangle carRect) {
        // Get base color from the car rectangle
        Color highlightColor = Color.YELLOW;
        
        // Create strong highlight effect
        Glow glow = new Glow(0.8);
        DropShadow shadow = new DropShadow(12, highlightColor);
        shadow.setInput(glow);
        
        // Apply the highlight effect directly without any animation that would reset it
        carRect.setEffect(shadow);
        
        // Create the pulse animation for visual feedback, but don't reset the effect when it's done
        ScaleTransition pulse = new ScaleTransition(Duration.millis(500), carRect);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setCycleCount(2);
        pulse.setAutoReverse(true);
        
        // Play the pulse animation without affecting the highlight
        pulse.play();
    }

    /**
     * Animate a single car moving (translate + optional pop)
     */
    private ParallelTransition createMoveAnimation(
            Board board,
            int   carIndex,
            int   moveAmount,
            double durationMillis
    ) {
        Car car = board.getCars().get(carIndex);
        char id = car.getId();

        Rectangle rect = carRectangles.get(id);
        Text      lbl  = carLabels.get(id);
        if (rect == null) return new ParallelTransition();

        // Translate
        TranslateTransition t1 = new TranslateTransition(
            Duration.millis(durationMillis), rect);
        TranslateTransition t2 = new TranslateTransition(
            Duration.millis(durationMillis), lbl);

        if (car.getOrientation() == Board.HORIZONTAL) {
            t1.setByX(moveAmount * cellSize);
            t2.setByX(moveAmount * cellSize);
        } else {
            t1.setByY(moveAmount * cellSize);
            t2.setByY(moveAmount * cellSize);
        }

        // Optional little pop
        ScaleTransition pop = new ScaleTransition(
            Duration.millis(durationMillis * 0.2), rect);
        pop.setFromX(1.0); pop.setFromY(1.0);
        pop.setToX(1.1);   pop.setToY(1.1);
        pop.setAutoReverse(true);
        pop.setCycleCount(2);

        return new ParallelTransition(t1, t2, pop);
    }


    public SequentialTransition createMovesAnimation(
            Board board,
            List<int[]> moves,
            Pane boardPane,
            double speedMultiplier
    ) {
        if (moves == null || moves.isEmpty()) {
            return new SequentialTransition();
        }

        // 1) initialize
        currentBoard = board.copy();
        currentBoard.setCurrentMovedCarIndex(null);
        totalSteps = moves.size();
        currentStep = 0;
        updateStepCount();

        Board animBoard = board.copy();
        animBoard.setCurrentMovedCarIndex(null);

        SequentialTransition seq = new SequentialTransition();
        seq.setRate(speedMultiplier);

        for (int i = 0; i < moves.size(); i++) {
            int[]     mv    = moves.get(i);
            int       cIdx  = mv[0];
            int       amt   = mv[1];
            final int step  = i + 1;

            // make “before” state with that one car highlighted
            Board before = animBoard.copy();
            before.setCurrentMovedCarIndex(cIdx);

            // apply move on our animBoard for next loop
            animBoard = animBoard.applyMove(cIdx, amt);
            Board after  = animBoard.copy();

            // a) pause so we can show the highlight
            PauseTransition highlight = new PauseTransition(Duration.millis(200));
            highlight.setOnFinished(e -> {
                currentStep = step;
                updateStepCount();
                drawBoard(before, boardPane);
            });

            // b) do the actual move
            ParallelTransition move = createMoveAnimation(before, cIdx, amt, 500);
            move.setInterpolator(Interpolator.LINEAR);
            move.setOnFinished(e -> {
                drawBoard(after, boardPane);
                if (after.isSolved() && step == totalSteps) {
                    createCelebrationAnimation().play();
                }
            });

            seq.getChildren().addAll(highlight, move);
        }

        this.movesAnimation = seq;
        return seq;
    }

    
    /**
     * Create a celebration animation for the primary car
     */
    private ParallelTransition createCelebrationAnimation() {
        Car primaryCar = currentBoard.getPrimaryCar();
        if (primaryCar == null) {
            return new ParallelTransition();
        }
        
        Rectangle carRect = carRectangles.get(primaryCar.getId());
        if (carRect == null) {
            return new ParallelTransition();
        }
        
        // Store original properties for restoration
        javafx.scene.paint.Paint originalFill = carRect.getFill();
        
        // Apply strong highlighting effect explicitly that will remain throughout the celebration
        Glow glow = new Glow(0.9);
        DropShadow highlight = new DropShadow(15, Color.GOLD);
        highlight.setInput(glow);
        carRect.setEffect(highlight);
        
        // Create a parallel transition for multiple animations
        ParallelTransition celebration = new ParallelTransition();
        
        // Create pulsing effect with scale
        ScaleTransition pulse = new ScaleTransition(Duration.millis(250), carRect);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.2);
        pulse.setToY(1.2);
        pulse.setCycleCount(12);
        pulse.setAutoReverse(true);
        
        // Add rotation
        RotateTransition rotate = new RotateTransition(Duration.millis(1000), carRect);
        rotate.setByAngle(360);
        rotate.setCycleCount(3);
        
        // Add animations to the celebration
        celebration.getChildren().addAll(pulse, rotate);
        
        // Reset car appearance when animation completes, but keep the highlight effect
        celebration.setOnFinished(e -> {
            carRect.setFill(originalFill);
            carRect.setScaleX(1.0);
            carRect.setScaleY(1.0);
            carRect.setRotate(0);
            // The highlight effect remains! Not resetting it
        });
        
        return celebration;
    }

    /**
     * Update the step count display
     */
    private void updateStepCount() {
        if (stepCountText != null) {
            stepCountText.setText("Move: " + currentStep + " / " + totalSteps);
        }
    }
    
    /**
     * Adjust the playback speed without restarting.
     */
    public void setSpeed(double speedMultiplier) {
        if (movesAnimation != null) {
            movesAnimation.setRate(speedMultiplier);
        }
    }

    /**
     * Start or resume at the current spot, using linear interpolation.
     */
    public void play(double speedMultiplier) {
        if (movesAnimation == null) return;

        // 1) always update rate up front
        movesAnimation.setRate(speedMultiplier);

        // 2) resume or start-from-current-time
        if (isPaused) {
            movesAnimation.play();
            isPaused = false;
        } else {
            movesAnimation.playFrom(movesAnimation.getCurrentTime());
        }
        isPlaying = true;
    }
    
    /**
     * Pause the animation
     */
    public void pause() {
        if (movesAnimation != null && isPlaying) {
            movesAnimation.pause();
            isPlaying = false;
            isPaused = true;
        }
    }
    
    /**
     * Stop the animation
     */
    public void stop() {
        if (movesAnimation != null) {
            movesAnimation.stop();
            isPlaying = false;
            isPaused = false;
            currentStep = 0;
            updateStepCount();
        }
    }
    
    /**
     * Get moves animation
     * @return The SequentialTransition object
     */
    public SequentialTransition getMovesAnimation() {
        return movesAnimation;
    }

    /**
     * Get the current step
     * 
     * @return The current step
     */
    public int getCurrentStep() {
        return currentStep;
    }
    
    /**
     * Get the total steps
     * 
     * @return The total steps
     */
    public int getTotalSteps() {
        return totalSteps;
    }
    
    /**
     * Set the cell size for the board
     *
     * @param newCellSize The new cell size to use
     */
    public void setCellSize(int newCellSize) {
        this.cellSize = newCellSize;
    }

    /**
     * Check if the animation is playing
     * 
     * @return True if playing, false otherwise
     */
    public boolean isPlaying() {
        return isPlaying;
    }
    
    /**
     * Check if the animation is paused
     * 
     * @return True if paused, false otherwise
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Update the step count display
     * 
     * @param step The step to display
     */
    public void updateStepCount(int step) {
        if (stepCountText != null) {
            stepCountText.setText("Move: " + step + " / " + totalSteps);
        }
    }

    /**
     * Set the current step
     * 
     * @param step The step to set
     */
    public void setCurrentStep(int step) {
        this.currentStep = step;
        updateStepCount();
    }
}