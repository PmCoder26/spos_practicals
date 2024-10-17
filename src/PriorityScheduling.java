import java.util.*;
import java.util.stream.Collectors;


public class PriorityScheduling {

    private static Scanner sc = new Scanner(System.in);
    private static LinkedList<Process> processes = new LinkedList<>();

    private static class Process implements Comparable<Process>{
        int at, bt, tat, wt, p, index;
        boolean isProcessed;
        public Process(int at, int bt, int p, int index){
            this.at = at;
            this.bt = bt;
            this.p = p;
            this.index = index;
        }

        @Override
        public int compareTo(Process p2){
            return p2.p - this.p;
        }

        public int compareByAt(Process p2){
            return this.at - p2.at;
        }

        public int compareByIdx(Process p2){
            return this.index - p2.index;
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
            System.out.print("Priority: ");
            int p = sc.nextInt();
            processes.add(new Process(at, bt, p, x));
        }
    }

    private static boolean shouldStop(LinkedList<Process> processes){
        return processes.stream()
                .filter(process -> !process.isProcessed)
                .toList().isEmpty();
    }

    private static List<Process> getProcessedByPtAndAt(List<Process> processes){
        List<Process> temp = processes.stream()
                .filter(process -> !process.isProcessed)
                .sorted(Process::compareTo)
                .toList();
        if(!temp.isEmpty()) {
            int p = temp.get(0).p;
            return temp.stream()
                    .filter(process -> process.p == p)
                    .sorted(Process::compareByAt)
                    .toList();
        }
        return Collections.emptyList();
    }

    private static void performScheduling(){
        processes.sort(Process::compareByAt);
        Process firstProcess = processes.getFirst();
        int currentTime = 0;
        firstProcess.isProcessed = true;
        currentTime = firstProcess.bt;
        firstProcess.tat = currentTime - firstProcess.at;
        firstProcess.wt = firstProcess.tat - firstProcess.bt;

        List<Process> processesToExecute = getProcessedByPtAndAt(processes);
        while(!shouldStop(processes)){
            for(int x = 0; x < processesToExecute.size(); x++){
                Process process = processesToExecute.get(x);
                process.isProcessed = true;
                currentTime += process.bt;
                process.tat = currentTime - process.at;
                process.wt = Math.abs(process.tat - process.bt);
            }
            processesToExecute = getProcessedByPtAndAt(processes);
        }
    }

    private static void showResults(){
        processes.sort(Process::compareByIdx);
        System.out.println("Index\tAT\tBT\tPriority\tTAT\t\tWT");
        for(int x = 0; x < processes.size(); x++){
            Process process = processes.get(x);
            System.out.println(process.index + "\t\t" + process.at + "   " + process.bt + "\t\t" + process.p + "\t\t" + process.tat + "\t\t" + process.wt);
        }
    }

    public static void main(String[] args){

        getProcesses();
        performScheduling();
        showResults();

    }

}
