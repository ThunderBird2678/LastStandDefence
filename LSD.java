import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class LSD
{
    protected static int money = 2000;
    protected static int score = 0;
    static GameBoard aBoard;
    static ArrayList<Tower> towers = new ArrayList(0);
    static ArrayList<Position> path = new ArrayList(0);
    static ArrayList<Enemy> wave;

    public static void main(String args[])
    {

        System.out.println("LSD v.0.3 Codename 'Bubba'\npress return to add, enter any string then return to remove.");
        
        try
        {
            ArrayList<String> pathtemp = parse(parse(new String(Files.readAllBytes(Paths.get("paths.txt"))),'\n').get(0),'#');
            //ArrayList<String> pricetemp = parse(parse(new String(Files.readAllBytes(Paths.get("properties.txt"))),'\n').get(0),'#');
            for(int a = 0;a<pathtemp.size();a++)
            {
                path.add(new Position(pathtemp.get(a)));
            }

        }
        catch(IOException e)
        {
            System.out.println("Could not read file.");
        }
        wave = new ArrayList(4);
        Scanner little = new Scanner(System.in);
        aBoard = new GameBoard(path);
        aBoard.setVisible(true);

        for(int i = 0; i < 4; i++)
        {
            wave.add(new Enemy(20));
        }

        for(int i = 0; i < wave.size(); i++)
        {
            System.out.println(wave.get(i));
        }

        aBoard.spawnWave(wave);

        while(true)
        {
            String inputs = little.nextLine();
            System.out.println("current position: "+aBoard.select);
            if(inputs .isEmpty())
                addTower(aBoard.select,0);
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

    public static void addTower(Position place, int type)//a flimsy addTower class, makes a default Tower()
    {
        if(canBuild(place))
        {
            if(money>200)//change this to a proper requirement
            {
                towers.add(new Tower(type,new Position(place)));
                money-=200; //subtract funds
                Tower toAdd = new Tower(type,new Position(place));
                aBoard.addTower(toAdd);
                System.out.println("Added tower at "+place);
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
                break;
            }
            if(towers.get(a).getPos().distance(place)==0)
            {                
                Tower toRemove = new Tower(towers.get(a).getType(), new Position(place));
                towers.remove(a);
                aBoard.removeTower(toRemove);
                System.out.println("Removed tower "+a+" from "+towers.get(a).getPos());
                break;
                //then update graphics as necessary
            }
        }
    }

    public static ArrayList<String> parse(String parseThis,char delimiter)
    {
        int width = 0,elements = 0,temp = 0;

        for(int a = 0; a < parseThis.length();a++)//check the first row to see what the dimension should be
        {
            if(parseThis.charAt(a) == ',')
            {
                width++;
            }
        }

        ArrayList<String> toR = new ArrayList(0);

        if(width==0)
        {
            toR.add(parseThis);
            return toR;
        }

        for(int b = 0;elements<width;b++)
        {
            if(elements>=width-1)//if last element copy the rest
            {
                toR.add(parseThis.substring(temp));
                break;
            }

            if(b>=parseThis.length())//if we are at the end of the line before all the elements filled
            {
                while(elements<width)
                {
                    toR.add("");//just fill it with blank then
                    elements++;
                }

                break;
            }

            if(parseThis.charAt(b)==delimiter)//delimiter is the comma
            {
                toR.add(parseThis.substring(temp,b));//set the former stuff to the element
                temp = b+1;//shorten the string we have to work with
                elements++;//go to next element
            }
        }

        return toR;
    }

}
