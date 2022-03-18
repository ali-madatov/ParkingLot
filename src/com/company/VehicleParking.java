package com.company;

import java.util.ArrayList;
import java.util.Comparator;

import static java.lang.Math.max;
import static java.lang.Math.min;



public class VehicleParking {

    int parkingWidth;
    int parkingHeight;
    int newFreeSlotsLastSize;

    ArrayList<VehicleSlot> newFreeSlots = new ArrayList<VehicleSlot>();;
    ArrayList<VehicleSlot> freeSlots = new ArrayList<VehicleSlot>();
    ArrayList<VehicleSlot> usedSlots = new ArrayList<VehicleSlot>();

    boolean allowRotation;

    // Instantiates a parking lot of size (0,0)
    VehicleParking(){
        parkingWidth=0;
        parkingHeight=0;
    }

    // Instantiates a parking lot of given size.
    VehicleParking(int width, int height, boolean allowRotation){
        Init(width, height, allowRotation);
    }

    void Init(int width, int height, boolean allowRotation){
        this.allowRotation = allowRotation;
        parkingWidth = width;
        parkingHeight = height;

        VehicleSlot n = new VehicleSlot();
        n.x = 0;
        n.y = 0;
        n.width = width;
        n.height = height;

        usedSlots.clear();
        freeSlots.clear();

        freeSlots.add(n);
    }




    void Insert(ArrayList<VehicleSize> slots, ArrayList<VehicleSlot> parkedVehicles){
        parkedVehicles.clear();

        while(slots.size() > 0)
        {
            int bestScore1 = Integer.MAX_VALUE;
            int bestScore2 = Integer.MAX_VALUE;
            int bestRectIndex = -1;
            VehicleSlot bestNode = new VehicleSlot();

            for(int i = 0; i < slots.size(); ++i)
            {
                Integer score1 =Integer.MAX_VALUE;
                Integer score2 =Integer.MAX_VALUE;
                VehicleSlot newNode = ScoreSlot(slots.get(i).width, slots.get(i).height,  score1, score2);

                if (score1 < bestScore1 || (score1 == bestScore1 && score2 < bestScore2))
                {
                    bestScore1 = score1;
                    bestScore2 = score2;
                    bestNode = newNode;
                    bestRectIndex = i;
                }
            }

            if (bestRectIndex == -1)
                return;

            PlaceVehicle(bestNode);
            parkedVehicles.add(bestNode);
            slots.set(bestRectIndex,slots.get(slots.size()-1));
            slots.remove(slots.size()-1);
        }
    }

    VehicleSlot Insert(int objectNum,int width, int height){
        VehicleSlot newNode;
        Integer score1 = Integer.MAX_VALUE;
        Integer score2 = Integer.MAX_VALUE;

        newNode = FindPositionForNewNodeContactPoint(width, height, score1);

        newNode.objectNum=objectNum;
        if (newNode.height == 0)
            return newNode;

        PlaceVehicle(newNode);

        return newNode;
    }

//    double Occupancy(){
//        int usedSurfaceArea = 0;
//        for(int i = 0; i < usedSlots.size(); ++i)
//            usedSurfaceArea += usedSlots.get(i).width * usedSlots.get(i).height;
//
//        return (double)usedSurfaceArea / (parkingWidth * parkingHeight);
//    }

    private VehicleSlot ScoreSlot(int width, int height,  Integer score1, Integer score2) {
        VehicleSlot newNode;
        score1 = Integer.MAX_VALUE;
        score2 = Integer.MAX_VALUE;
        newNode = FindPositionForNewNodeContactPoint(width, height, score1);
                score1 = -score1; // Reverse since we are minimizing, but for contact point score bigger is better.


        if (newNode.height == 0)
        {
            score1 = Integer.MAX_VALUE;
            score2 = Integer.MAX_VALUE;
        }

        return newNode;
    }

    private void PlaceVehicle(VehicleSlot node){
        for(int i = 0; i < freeSlots.size();)
        {
            if (SplitFreeNode(freeSlots.get(i), node))
            {
                freeSlots.set(i,freeSlots.get(freeSlots.size()-1)) ;
                freeSlots.remove(freeSlots.size()-1);
            }
            else
                ++i;
        }

        PruneFreeList();

        usedSlots.add(node);
    }

    int CommonIntervalLength(int i1start, int i1end, int i2start, int i2end)
    {
        if (i1end < i2start || i2end < i1start)
            return 0;
        return min(i1end, i2end) - max(i1start, i2start);
    }

    int ContactPointScoreNode(int x, int y, int width, int height) {
        int score = 0;

        if (x == 0 || x + width == parkingWidth)
            score += height;
        if (y == 0 || y + height == parkingHeight)
            score += width;

        for(int i = 0; i < usedSlots.size(); ++i)
        {
            if (usedSlots.get(i).x == x + width || usedSlots.get(i).x + usedSlots.get(i).width == x)
                score += CommonIntervalLength(usedSlots.get(i).y, usedSlots.get(i).y + usedSlots.get(i).height, y, y + height);
            if (usedSlots.get(i).y == y + height || usedSlots.get(i).y + usedSlots.get(i).height == y)
                score += CommonIntervalLength(usedSlots.get(i).x, usedSlots.get(i).x + usedSlots.get(i).width, x, x + width);
        }
        return score;
    }

    VehicleSlot FindPositionForNewNodeContactPoint(int width, int height, Integer bestContactScore) {
        VehicleSlot bestNode = new VehicleSlot();

        bestContactScore = -1;

        for(int i = 0; i < freeSlots.size(); ++i)
        {
            if (freeSlots.get(i).width >= width && freeSlots.get(i).height >= height)
            {
                int score = ContactPointScoreNode(freeSlots.get(i).x, freeSlots.get(i).y, width, height);
                if (score > bestContactScore)
                {
                    bestNode.x = freeSlots.get(i).x;
                    bestNode.y = freeSlots.get(i).y;
                    bestNode.width = width;
                    bestNode.height = height;
                    bestContactScore = score;
                }
            }

        }

        if(bestContactScore<0){
            for(int i = 0; i < freeSlots.size(); ++i)
            {
                if (allowRotation && freeSlots.get(i).width >= height && freeSlots.get(i).height >= width)
                {
                    int score = ContactPointScoreNode(freeSlots.get(i).x, freeSlots.get(i).y, height, width);
                    if (score > bestContactScore)
                    {
                        bestNode.x = freeSlots.get(i).x;
                        bestNode.y = freeSlots.get(i).y;
                        bestNode.width = height;
                        bestNode.height = width;
                        bestContactScore = score;
                    }
                }
            }
        }
        return bestNode;
    }

    void InsertNewFreeSlot(VehicleSlot newFreeSlot){
        assert(newFreeSlot.width > 0);
        assert(newFreeSlot.height > 0);

        for(int i = 0; i < newFreeSlotsLastSize;)
        {
            if (Method.IsContainedIn(newFreeSlot, newFreeSlots.get(i)))
                return;

            if (Method.IsContainedIn(newFreeSlots.get(i), newFreeSlot))
            {
                newFreeSlots.set(i,newFreeSlots.get(--newFreeSlotsLastSize));
                newFreeSlots.set(newFreeSlotsLastSize,newFreeSlots.get(newFreeSlots.size()-1));
                newFreeSlots.remove(newFreeSlots.size()-1);
            }
            else
                ++i;
        }
        newFreeSlots.add(newFreeSlot);

    }

    boolean SplitFreeNode(VehicleSlot freeNode, VehicleSlot usedNode){

        if (usedNode.x >= freeNode.x + freeNode.width || usedNode.x + usedNode.width <= freeNode.x ||
                usedNode.y >= freeNode.y + freeNode.height || usedNode.y + usedNode.height <= freeNode.y)
            return false;

        newFreeSlotsLastSize = newFreeSlots.size();

        if (usedNode.x < freeNode.x + freeNode.width && usedNode.x + usedNode.width > freeNode.x)
        {
            if (usedNode.y > freeNode.y && usedNode.y < freeNode.y + freeNode.height)
            {
                VehicleSlot newNode = new VehicleSlot();
                newNode.x = freeNode.x;
                newNode.y = freeNode.y;
                newNode.width = freeNode.width;
                newNode.height = usedNode.y - newNode.y;
                InsertNewFreeSlot(newNode);
            }

            if (usedNode.y + usedNode.height < freeNode.y + freeNode.height)
            {
                VehicleSlot newNode = new VehicleSlot();
                newNode.x = freeNode.x;
                newNode.width = freeNode.width;
                newNode.y = usedNode.y + usedNode.height;
                newNode.height = freeNode.y + freeNode.height - (usedNode.y + usedNode.height);
                InsertNewFreeSlot(newNode);
            }
        }

        if (usedNode.y < freeNode.y + freeNode.height && usedNode.y + usedNode.height > freeNode.y)
        {
            if (usedNode.x > freeNode.x && usedNode.x < freeNode.x + freeNode.width)
            {
                VehicleSlot newNode = new VehicleSlot();
                newNode.x = freeNode.x;
                newNode.y = freeNode.y;
                newNode.height = freeNode.height;
                newNode.width = usedNode.x - newNode.x;
                InsertNewFreeSlot(newNode);
            }

            if (usedNode.x + usedNode.width < freeNode.x + freeNode.width)
            {
                VehicleSlot newNode = new VehicleSlot();
                newNode.y = freeNode.y;
                newNode.height = freeNode.height;
                newNode.x = usedNode.x + usedNode.width;
                newNode.width = freeNode.x + freeNode.width - usedNode.x - usedNode.width;
                InsertNewFreeSlot(newNode);
            }
        }

        return true;
    }

    void PruneFreeList(){
        for(int i = 0; i < freeSlots.size(); ++i)
            for(int j = 0; j < newFreeSlots.size();)
            {
                if (Method.IsContainedIn(newFreeSlots.get(j), freeSlots.get(i)))
                {
                    newFreeSlots.set(j,newFreeSlots.get(newFreeSlots.size()-1));
                    newFreeSlots.remove(newFreeSlots.size()-1);
                }
                else
                {
                    assert(!Method.IsContainedIn(freeSlots.get(i), newFreeSlots.get(j)));

                    ++j;
                }
            }

        freeSlots.addAll(newFreeSlots);
        newFreeSlots.clear();
    }

}
