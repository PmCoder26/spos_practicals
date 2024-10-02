import java.util.*;
import java.util.stream.Collectors;

public class SJF_Scheduling {

    private static final int TIME_QUANTUM = 1;

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

    private static ArrayList<Process> getProcessesForExecution(int currentTime){
        return processes.stream()
                .filter( process ->
                        process.tempBT > 0 && process.at <= currentTime
                )
                .sorted(Process::compareByBt)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static ArrayList<Process> getProcessesByAt(ArrayList<Process> processesToExecute, int tempBT){
        return processesToExecute.stream()
                .filter( process ->
                        process.tempBT == tempBT
                )
                .sorted(Process::compareByAt)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static boolean shouldStop(){
        return processes.stream()
                .filter(process ->
                        process.tempBT != 0
                )
                .toList().isEmpty();
    }

    private static void performScheduling(){
        int currentTime = 0;
        while(!shouldStop()) {
            // get those processes whose arrival time <= current time.
            ArrayList<Process> processesToExecute = getProcessesForExecution(currentTime);
            // get those processes whose burst time is least, and if multiple processes exists of same burst time then
            // sort by arrival time
            if(!processesToExecute.isEmpty()) {
                processesToExecute = getProcessesByAt(processesToExecute, processesToExecute.get(0).tempBT);
                Process process = processesToExecute.get(0);
                process.tempBT -= TIME_QUANTUM;
                if (!process.started) {
                    process.started = true;
                    process.cpuFirstTime = currentTime;
                }
                currentTime += TIME_QUANTUM;
                if (process.tempBT == 0) {
                    process.ct = currentTime;
                }
            } else{
                currentTime++;
            }
        }
        for(Process process : processes){
            process.tat = process.ct - process.at;
            process.wt = process.tat - process.bt;
            process.rt = process.cpuFirstTime - process.at;
        }
    }


    private static void showResults(ArrayList<Process> processes) {
        System.out.println("Process\tArrival Time\tBurst Time\tCompletion Time\tWaiting Time\tTurnAround Time\tCPU FirstTime\tResponse Time");
        for (Process process : processes) {
            System.out.println(process.index + "\t\t" + process.at + "\t\t\t\t" + process.bt + "\t\t\t\t" + process.ct + "\t\t\t\t" + process.wt + "\t\t\t\t" + process.tat + "\t\t\t\t" + process.cpuFirstTime + "\t\t\t\t" + process.rt);
        }
    }

    public static void main(String[] args) {

        setProcesses();
        performScheduling();
        showResults(processes);



    }
}
