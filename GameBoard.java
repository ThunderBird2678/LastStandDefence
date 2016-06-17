import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GameBoard extends JFrame
{
  
  protected MiscImage wavesImage =           new MiscImage(40 , 160, 500, 70, "resources/backings/waves.png");
  protected MiscImage killsImage =           new MiscImage(240, 160, 500, 70, "resources/backings/kills.png");
  protected MiscImage nextWaveImage =        new MiscImage(680, 160, 500, 70, "resources/backings/nextWave.png");
  protected MiscImage pauseImage =           new MiscImage(880, 160, 500, 70, "resources/backings/pause.png");
  protected MiscImage backgroundImage =      new MiscImage(0 , 1080, 0 , 600, "resources/overall/background.png");
  protected MiscImage boardBackgroundImage = new MiscImage(40, 1000, 40, 440, "resources/overall/boardBackground.png"); // This is actually not useless, it's used for a bit of checking, will clean up soon(tm)
  
  protected MiscImage baseImage =            new MiscImage(840, 160, 265, 70, "resources/entities/base.png"); // These coordinates can be changed depending on how we want to set up the map
  
  protected MiscImage currentTurretImage =   new MiscImage(524, 40, 518, 40, "resources/turrets/blue.png"); // I'll deal with this one later
  protected int currentTurretID = 1; // Turret ID Number, between 1 and 4 to represent the four different types of turrets
  
  protected MiscImage buttonsImage =         new MiscImage(478, 125, 520, 30, "resources/controls/arrows.png"); // I'll deal with this one later
  
  // protected MiscImage path =                 new MiscImage(40, 880, 80, 360, "resources/entities/path.png"); Time to remove this and legitimatelly generate a path
  
  protected ArrayList<MiscImage> path = new ArrayList<MiscImage>(); // Path will be an arrayList of position objects, I'd presume..?
  protected ArrayList<MiscImage> turrets = new ArrayList<MiscImage>();
  protected ArrayList<MiscImage> enemies = new ArrayList<MiscImage>();
  
  protected DrawArea main; // This is the main area where all of our components will be drawn onto.
  
  protected Position select = new Position (0,0); //if you want to access this in a static method, use Gameboard.aboard.select
  
  // Okay, for now, I'm defining each square to be 40px by 40px
  
  public GameBoard(ArrayList<Position> aPath)
  {
    
    msListen mouse = new msListen();
    
    main = new DrawArea(1080, 630);
    
    main.setLayout(null);
    main.addMouseListener(mouse);
    main.addMouseMotionListener(mouse);
    
    for(int i = 0; i < aPath.size(); i++)
    {
      int squareX = (aPath.get(i).getX() * 40) + 40;
      int squareY = (aPath.get(i).getY() * 40) + 40;
      path.add(new MiscImage(squareX, 40, squareY, 40, "resources/entities/path.png"));
    }
    
    //spawnEnemies spawner = new spawnEnemies();
    //spawner.start();
    
    main.add(backgroundImage);
    main.add(wavesImage);
    main.add(killsImage);
    main.add(nextWaveImage);
    main.add(pauseImage);
    main.add(baseImage);
    main.add(buttonsImage);
    main.add(currentTurretImage);
    
    main.add(turrets);
    main.add(path);
    main.add(enemies);
    
    setContentPane(main);                                // set the content pane to be whatever content pane contains all the others
    pack ();                                             // this is apparently required
    setTitle ("GUI Testing");                            // set the title of the window
    setSize (1080, 630);                                 // set the size of the window (in pixels)
    setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);     // set the close operation (just use EXIT_ON_CLOSE, we're not one of those dicks who minimizes windows when the user hits close)
    setLocationRelativeTo (null);                        // Center window.
    
  }
  
  public void revert()
  {
    main.revert();
    
    main.add(backgroundImage);
    main.add(wavesImage);
    main.add(killsImage);
    main.add(nextWaveImage);
    main.add(pauseImage);
    main.add(baseImage);
    main.add(buttonsImage);
    main.add(currentTurretImage);
    
    main.add(turrets);
    main.add(path);
    main.add(enemies);    
  }
  
  public void addTower(Tower toAdd)
  {
    int towerX = toAdd.getPos().getX();
    int towerY = toAdd.getPos().getY();
    System.out.println("Tower is located at: (" + towerX + ", " + towerY + ")");
    
    int towerDrawX = (towerX * 40) + 40;
    int towerDrawY = (towerY * 40) + 40;
    
    turrets.add(new MiscImage(towerDrawX, 40, towerDrawY, 40, currentTurretImage.getImg()));
    
    revert();
    main.add(turrets);
    repaint();
    
  }
  
  public void removeTower(Tower toRemove)
  {
    int towerX = toRemove.getPos().getX();
    int towerY = toRemove.getPos().getY();
    System.out.println("Removing tower at: (" + towerX + ", " + towerY + ")");
    int towerDrawX = (towerX * 40) + 40;
    int towerDrawY = (towerY * 40) + 40;
    
    for(int i = 0; i < turrets.size(); i++)
    {
      if(turrets.get(i).getX() == towerDrawX && turrets.get(i).getY() == towerDrawY)
      {
        turrets.remove(i);
      }
    }
    
    revert();
    repaint();
    
  }
  
  public void spawnWave(ArrayList<Enemy> enems)
  {
    spawnEnemies spawner = new spawnEnemies(enems); // Creates a new spawnEnemies thread that will spawn one wave
    spawner.start();                                // Starts the spawner thread; spawner will run until it's finished spawning and moving that wave
  }
  
  class spawnEnemies extends Thread
  {
    
    ArrayList<Enemy> enems = new ArrayList<Enemy>(); // The list of enemies
    
    public spawnEnemies(ArrayList<Enemy> enems)      // Constructor, takes in a list of enemies
    {
      this.enems = enems;
    }
    
    public void run() // The thread that will be run
    {
      
      int pathCounter = 0;    // The counter that tracks what square of the path we're in (is this still necessary)
      int hereIsACounter = 0; // The counter that will continously increment; sets up how many enemies are currently on the board
      int enemCounter = 0;    // The counter that allows for iteration through the list of enemies
      
      boolean plusOne = true; // This is based off if an enemy has been generated this turn; if they have not, while the enems.size() won't increase, we still need to move the current enemies one square ahead
      
      int divisBy = 0; // This is so that the generator only generates a new enemy every n number of runs of the thread (n specified below)
      int rate = 2;   // So that the enemy is generated every 2 runs (i.e. every 2 squares)
      
      while(enems.size() > 0)
      {
        
        // Spawning a new enemy
        
        if(enemCounter < enems.size()) // As long as the enemCounter doesn't get to the point where I'm out of bounds, I can execute this section
        {
          
          int enemDrawX = path.get(0).getX();
          int enemDrawY = path.get(0).getY();
          String enemPath = "resources/enemies/default.png";
          
          // enemPath = "resources/enemies/" + enems.get(enemCounter).getID() + ".png"; // Ideally, this is the kind of setup I'd like to have
          
          if(divisBy % rate == 0)
          {
            //System.out.println("Adding enemy number " + (enemCounter+1) + " at: (" + enemDrawX + ", " + enemDrawY + ")");
            enemies.add(new MiscImage(enemDrawX, 40, enemDrawY, 40, enemPath));
            enemCounter++;
            plusOne = false;
          }
          else
          {
            //System.out.println("Not Adding enemy this run.");
            plusOne = true;
          }
          
        }
        
        if(enemies.size() > 0)
        {
          
          if(divisBy % rate == 0)
          {
            hereIsACounter++;
          }
          
          divisBy++;
          
          int pathIndex = 0;
          int drawIndex = pathIndex;
          
          if(hereIsACounter <= enemies.size())
          {
            hereIsACounter = enemies.size();
          }
          
          if(plusOne = true)
          {
            pathIndex = (hereIsACounter-1) * rate + 1;
            //System.out.println("The elements will begin by being placed at: (" + path.get(pathIndex).getX() + ", " + path.get(pathIndex).getY() + ")");
          }
          else
          {
            //System.out.println("The elements will begin by being placed at: (" + path.get(pathIndex).getX() + ", " + path.get(pathIndex).getY() + ")");
            pathIndex = (hereIsACounter-1) * rate;
          }
          
          if(pathIndex > path.size() - 1)
          {
            System.out.println("pathIndex is: " + pathIndex + " and is equal to path.size() - 1: " + (path.size()-1));
            enemies.remove(0);
            enems.remove(0);
            System.out.println("Removed the furthest enemy");
            pathIndex = path.size() - 2;
            System.out.println("pathIndex is: " + pathIndex);
            
          }
          
          for(int i = 0; i < enemies.size(); i++)
          {
            drawIndex = pathIndex - (i*2);
            
            enemies.get(i).setX(path.get(drawIndex).getX());
            enemies.get(i).setY(path.get(drawIndex).getY());
            System.out.println("Drew enemy " + (i+1) + " at: (" + enemies.get(i).getX() + ", " + enemies.get(i).getY() + ")");
          }
          
          revert();
          repaint();
          
        }
        
        
        try
        {
          System.out.println("Done one run.");
          Thread.sleep(250);
        }
        catch(InterruptedException e)
        {
        }
        
      }
      
    }
    
  }
  
  class msListen implements MouseListener, MouseMotionListener
  {
    
    public void mouseEntered  (MouseEvent e){}
    
    public void mouseExited   (MouseEvent e){}
    public void mousePressed  (MouseEvent e){}
    public void mouseReleased (MouseEvent e){}
    public void mouseDragged  (MouseEvent e){}
    
    public void mouseClicked(MouseEvent e)
    {
      if(boardBackgroundImage.checkBounds(e.getX(), e.getY()))
      {
        
        int calcX = e.getX() - 40;
        int calcY = e.getY() - 40;
        
        select.setX(calcX / 40);
        select.setY(calcY / 40);
        System.out.println("Selected coordinates: "+select);
        
      }
      
      System.out.println("(MouseClick at" + e.getX() + ", " + e.getY() + ")");
      
      // For the control panel
      
      if(e.getX() < 508 && e.getX() > 478 && e.getY() < 550 && e.getY() > 520)
      {
        buttonsImage.setImg("resources/controls/left click.png");
        
        currentTurretID--;
        if(currentTurretID == 0)
        {
          currentTurretID = 4;
        }
        
      }
      else if(e.getX() > 575 && e.getX() < 603 && e.getY() < 550 && e.getY() > 520)
      {
        buttonsImage.setImg("resources/controls/right click.png");
        
        currentTurretID++;
        if(currentTurretID == 5)
        {
          currentTurretID = 1;
        }
        
      }
      
      else
      {
        buttonsImage.setImg("resources/controls/arrows.png");
      }
      
      if(currentTurretID == 1)
      {
        currentTurretImage.setImg("resources/turrets/blue.png");
      }
      else if(currentTurretID == 2)
      {
        currentTurretImage.setImg("resources/turrets/green.png");
      }
      else if(currentTurretID == 3)
      {
        currentTurretImage.setImg("resources/turrets/red.png");
      }
      else if(currentTurretID == 4)
      {
        currentTurretImage.setImg("resources/turrets/yellow.png");
      }
      
      repaint();
      
    }
    
    public void mouseMoved(MouseEvent e)
    {
      
      if(nextWaveImage.checkBounds(e.getX(), e.getY()))
      {
        nextWaveImage.setImg("resources/backings/nextWaveSelected.png");
      }
      else
      {
        nextWaveImage.setImg("resources/backings/nextWave.png");
      }
      
      repaint();
      
      if(e.getX() < 508 && e.getX() > 478 && e.getY() < 550 && e.getY() > 520)
      {
        buttonsImage.setImg("resources/controls/left hover.png");
      }
      else if(e.getX() > 575 && e.getX() < 603 && e.getY() < 550 && e.getY() > 520)
      {
        buttonsImage.setImg("resources/controls/right hover.png");
      }
      else
      {
        buttonsImage.setImg("resources/controls/arrows.png");
      }
      
      repaint();
      
    }
    
  }
  
}
