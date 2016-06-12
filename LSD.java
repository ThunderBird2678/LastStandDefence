import java.util.ArrayList;
import java.util.Scanner;

public class LSD
{
    protected static int money = 2000;
    protected static int score = 0;
    static GameBoard aBoard = new GameBoard();
    static ArrayList<Tower> towers = new ArrayList(0);
    static ArrayList<Position> path = new ArrayList(0);
    protected static ArrayList<Enemy> wave = new ArrayList(0);

    public static void main(String args[])
    {
		System.out.println("LSD v.0.01 Codename 'Incompetence'\npress return to add, enter any string then return to remove.");
        aBoard.setVisible(true);
        Scanner little = new Scanner(System.in);
        while(true)
        {
            String inputs = little.nextLine();
            System.out.println("current position: "+aBoard.select);
            if(inputs .isEmpty())
            addTower(aBoard.select);
            else
            removeTower(aBoard.select);
        }
    }

    public static boolean canBuild(Position place)
    {
        for(int a = 0;a<path.size();a++)
        {
            if(path.get(a).distance(place)==0)//if place is on the path
            {
                System.out.println(place+" is part of the path. Can't build.");
                return false; //no building allowed
            }
        }

        for(int a = 0;a<towers.size();a++)
        {
            //System.out.println("Tower "+a+": "+towers.get(a).getPos()+towers.get(a).getPos().distance(place));
            if(towers.get(a).getPos().distance(place)==0)//tower is at that position
            {
                System.out.println("There is a tower at "+place+" Can't build.");
                return false; //no building allowed
            }
        }

        return true;
    }

    public static void addTower(Position place)//a flimsy addTower class, makes a default Tower()
    {
        if(canBuild(place))
        {
            if(money>200)//change this to a proper requirement
            {
                System.out.println("Added tower at "+place);
                towers.add(new Tower(0,new Position(place)));
                money-=200; //subtract funds
                //then update graphics as necessary
            }
            else
            {
                System.out.println("Insufficient funds.");
            }
        }
    }

    public static void removeTower(Position place)
    {
        for(int a = 0;a<=towers.size();a++)
        {
            if(a==towers.size())
            {
                System.out.println("There is no tower there.");
            }

            if(towers.get(a).getPos().distance(place)==0)
            {
			System.out.println("Removed tower "+a+" from "+towers.get(a).getPos());
                towers.remove(a);
                //then update graphics as necessary
			break;
            }
        }
    }
}
