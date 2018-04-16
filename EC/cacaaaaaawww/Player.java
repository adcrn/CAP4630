// University of Central Florida
// CAP4630 - Spring 2018
// Authors: Nhi Nguyen and Alexander deCurnou

package cacaaaaaawww;

import java.awt.Point;
import java.util.*;
import pacsim.*;

public class Player extends AbstractPlayer
{
    private List<Point> path;

    public Player()
    {
        System.out.println("Team cacaaaaaawww has joined the game!");
    }

    @Override
    public void init()
    {
        System.out.println(morphTeam + " " + morphID + " CACAAAAAAWWW");
    }

    @Override
    public PacFace action(Object state)
    {
        // Get state of game grid and all facing directions.
        PacCell[][] grid = (PacCell[][]) state;
        PacFace[] faces = PacUtils.randomFaces();
        Point p = morph.getLoc();

        // If player is a ghost...
        if (morph.getMode() == MorphMode.GHOST)
        {
            // Let's check each direction
            for (PacFace face : faces)
            {
                // Get the next adjacent cell in that direction.
                PacCell pc = PacUtils.neighbor(face, p, grid);
                // If that cell is not player's color and is an opposing Pacman...
                if (pc instanceof MorphCell && ((MorphCell) pc).getTeam() != morph.getTeam()
                    && ((MorphCell) pc).getMode() == MorphMode.PACMAN)
                {
                    // let's eat them.
                    return face;
                }
            }

            // If there are no opposing Pacman in each
            // of adjacent cells, let's _try_ to eat them.
            //PacFace face = advance_ghost(grid, p);

            // If there is no Pacman, then there's bound to be one
            // at some point and they need food, so let's go to the food.
            return advance_ghost(grid, p);
        }
        // Otherwise, we're a Pacman, so let's go eat.
        else
        {
            // Let's check each direction
            for (PacFace face : faces)
            {
                // Get the next adjacent cell in that direction.
                PacCell pc = PacUtils.neighbor(face, p, grid);
                // If that cell is not player's color, it's enemy territory...
                if (pc instanceof MorphCell && ((MorphCell) pc).getTeam() != morph.getTeam())
                {
                    // so let's eat their food.
                    return face;
                }
            }

            // Now that we're not in immediate danger, let's go find food.
            return advance_pacman(grid, p, faces);
        }

    }

    private PacFace advance_pacman(PacCell[][] grid, Point p, PacFace[] faces)
    {
        /*
         * Pacmen should value self-preservation over food. It will move when
         * there is a ghost within a certain distance. Other than that, it
         * will go for the closest food pellet.
         */

        PacTeam opp = PacUtils.opposingTeam(morphTeam);
        List<Point> enemies = PacUtils.findMorphs(grid, opp);

        // If there are opposing enemy morphs...
        if (!enemies.isEmpty())
        {
            // for each enemy morph...
            for (Point enemy : enemies)
            {
                // If there is indeed an enemy morph and it is a ghost...
                if ((enemy != null)
                    && ((MorphCell) grid[enemy.x][enemy.y]).getMode() == MorphMode.GHOST)
                {
                    // let's get the hell out of dodge if it's within three units away
                    List<Point> temp = BFSPath.getPath(grid, p, enemy);
                    if (temp.size() <= 3)
                    {
                        // by choosing a direction that isn't in the path of a ghost or wall.
                        PacFace face = PacUtils.avoidTarget(p, enemy, grid);
                        
                        if (face != null)
                            return face;
                    }
                }
            }
        }

        // Determine opposing team color and nearest food item.
        Point goal = PacUtils.nearestFood(grid, p, opp);

        // Get path to the nearest food and
        // return correct direction towards goal.
        List<Point> temp = BFSPath.getPath(grid, p, goal);
        if (!temp.isEmpty())
        {
            Point next_step = temp.remove(0);
            PacFace face = PacUtils.direction(p, next_step);
            if (face != null)
                return face;
        }

        return null;
    }

    private PacFace advance_ghost(PacCell[][] grid, Point p)
    {
        /*
         * Ghosts should look for enemies first and if there are no
         * enemy Pacmen on the board. If so, the primary objective is to
         * protect pellets by eating enemies. If there are no enemies, then
         * the new objective is to move to the nearest morph.
         */

        // Find all enemy morphs.
        PacTeam opp = PacUtils.opposingTeam(morphTeam);
        List<Point> enemies = PacUtils.findMorphs(grid, opp);

        // If there are opposing enemy morphs...
        if (!enemies.isEmpty())
        {
            // for each enemy morph...
            for (Point enemy : enemies)
            {
                // If there is indeed an enemy morph and it is a Pacman...
                if ((enemy != null)
                    && ((MorphCell) grid[enemy.x][enemy.y]).getMode() == MorphMode.PACMAN)
                {
                    // Let's advance the ghost towards that morph.
                    List<Point> temp = BFSPath.getPath(grid, p, enemy);
                    Point next_point = temp.remove(0);
                    if (next_point != null)
                    {
                        PacFace face = PacUtils.direction(p, next_point);
                        if (face != null)
                            return face;
                    }
                }
            }
        }

        // Find nearest enemy morph.
        Point goal = PacUtils.nearestMorph(grid, p, opp);

        // Get path to the nearest enemy morph and
        // return correct direction towards goal.
        List<Point> temp = BFSPath.getPath(grid, p, goal);
        Point next_step = temp.remove(0);
        PacFace face = PacUtils.direction(p, next_step);
        
        if (face != null)
            return face;

        return null;
    }
}