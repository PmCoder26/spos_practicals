import java.util.*;

public class FCFS_Scheduling {
    private static Scanner sc = new Scanner(System.in);
    private static ArrayList<Process> processes = new ArrayList<>();

    private static class Process implements Comparable<Process>{
        int at, bt, st, wt, tat, index;
        public Process(int at, int bt, int index){
            this.at = at;
            this.bt = bt;
            this.index = index;
        }

        @Override
        public int compareTo(Process p2) {
            return this.at - p2.at;
        }
    }

    private static void getProcesses(){
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

    private static void sortProcesses() {
        processes.sort(Process::compareTo);
    }

    private static void performScheduling() {
        int serviceTime = 0;
        for(int x = 0; x < processes.size(); x++){
            Process process = processes.get(x);
            process.st = serviceTime;
            process.wt = process.st - process.at;
            process.tat = process.wt + process.bt;
            serviceTime += process.bt;
        }
    }

    private static void printProcesses() {
        for(int x = 0; x < processes.size(); x++){
            System.out.println("Index" + processes.get(x).index + ", " + "Arrival time: " + processes.get(x).at);
        }
    }

    private static void showResults() {
        System.out.println("Process\t\tArrival Time\t\tBurst Time\t\tService Time\t\tWaiting Time\t\tTurnAround Time");
        for (Process process : processes) {
            System.out.println(process.index + "\t\t\t\t" + process.at + "\t\t\t\t" + process.bt + "\t\t\t\t" + process.st + "\t\t\t\t" + process.wt + "\t\t\t\t" + process.tat);
        }
    }

    public static void main(String[] args) {

        getProcesses();

        sortProcesses();

        performScheduling();

        showResults();







    }

}
