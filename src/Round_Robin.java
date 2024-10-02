import java.util.*;


public class Round_Robin {

    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<Process> processes = new ArrayList<>();

    private static class Process implements Comparable<Process> {
        int at, bt, ct, wt, tat, index, rt, tempBT, cpuFirstTime;
        boolean started = false;
        public Process(int at, int bt, int index){
            this.at = at;
            this.bt = bt;
            this.index = index;
            tempBT = bt;
        }

        @Override
        public int compareTo(Process p2) {
            return this.tempBT - p2.tempBT;
        }

        public int compareByAt(Process p2){
            return this.at - p2.at;
        }

        public int compareByBt(Process p2){
            return this.tempBT - p2.tempBT;
        }
    }

    private static void setProcesses(){
        System.out.println("Enter the no.of processes: ");
        int n = sc.nextInt();
        for(int x = 0; x < n; x++){
            System.out.println("******************" + "For process P" + x + "******************");
            System.out.print("Arrival Time: ");
            int at = sc.nextInt();
            System.out.print("Burst Time: ");
            int bt = sc.nextInt();
            processes.add(new Process(at, bt, x));
        }
    }



}
