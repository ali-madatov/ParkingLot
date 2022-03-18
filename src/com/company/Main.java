package com.company;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        int lotWidth;
        int lotHeight;
        int vehicleNum;
        List<List<Integer>> vehicles = new ArrayList<List<Integer>>();

        Scanner scanner = new Scanner(System.in);

//        String lotParameters = scanner.nextLine();
//        String[] splitedParameters = lotParameters.split("\\t");
        try{
            System.out.println("Enter height of the lot:");
            lotHeight = scanner.nextInt();//Integer.parseInt(splitedParameters[0]);
            System.out.println("Enter width of the lot:");
            lotWidth = scanner.nextInt();//Integer.parseInt(splitedParameters[1]);
        } catch(NumberFormatException ex){
            lotHeight=0;
            lotWidth=0;
            System.out.println("Error in reading input!");
        }

//        String vehiclePar = scanner.nextLine();
        try{
            System.out.println("Enter the number of vehicles:");
            vehicleNum = scanner.nextInt();//Integer.parseInt(vehiclePar);
        } catch(NumberFormatException ex){
            vehicleNum=0;
            System.out.println("Error in reading input!");
        }

        for(int i=0;i<vehicleNum;i++){
            ArrayList<Integer> vehicleInput = new ArrayList<Integer>();
//            String vehicleScanner = scanner.nextLine();
//            String[] vehiclePars = vehicleScanner.split("\\t");
            try{
                System.out.println("Enter the vehicle size:");
                vehicleInput.add(scanner.nextInt());
                vehicleInput.add(scanner.nextInt());
                vehicleInput.add(i+1);
            } catch(NumberFormatException ex){
                System.out.println("Error in reading input!");
            }

            vehicles.add(vehicleInput);
        }

       //sorting vehicles according to longest side
        Collections.sort(vehicles, new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                int o1LongSide = o1.get(0)>=o1.get(1)?o1.get(0):o1.get(1);
                int o2LongSide = o2.get(0)>=o2.get(1)?o2.get(0):o2.get(1);
                if(o1LongSide>o2LongSide )
                    return 1;
                if(o1LongSide<o2LongSide)
                    return -1;
                int volume1_area = o1.get(0)*o1.get(1);
                int volume2_area = o2.get(0)*o2.get(1);
                return Integer.compare(volume1_area,volume2_area);
            }
        });
        Collections.reverse(vehicles);
        int[][] resultArray = new int[lotHeight][lotWidth];


        VehicleParking parkingLot = new VehicleParking();
        parkingLot.Init(lotHeight, lotWidth,true);


        for(int i = 0; i < vehicleNum; i++)
        {
            // Read next vehicle to park.
            int vehicleID=vehicles.get(i).get(2);
            int vehicleWidth = vehicles.get(i).get(1);
            int vehicleHeight = vehicles.get(i).get(0);

            VehicleSlot parkedVehicle;
            if(vehicleWidth<=vehicleHeight)
                parkedVehicle = parkingLot.Insert(vehicleID,vehicleWidth, vehicleHeight);
            else
                parkedVehicle = parkingLot.Insert(vehicleID,vehicleHeight, vehicleWidth);

            // Test success or failure.
            if (parkedVehicle.height > 0) {
                for(int h=parkedVehicle.y; h<parkedVehicle.height+parkedVehicle.y;h++){
                    for(int w=parkedVehicle.x; w<parkedVehicle.width+parkedVehicle.x;w++){
                        resultArray[h][w]=parkedVehicle.objectNum;
                    }
                }
            }
            //else System.out.println(vehicleID+"could not park it");
        }


        StringBuilder output = new StringBuilder();

        for(int h=0; h < lotHeight; h++){
            for(int w = 0; w < lotWidth; w++ ){
                if (w == (lotWidth - 1 )){
                    output.append(resultArray[h][w] + "\n");
                }                else{
                    output.append(resultArray[h][w] + "\t");
                }
            }
        }

        System.out.println(new String(output));
    }


}
