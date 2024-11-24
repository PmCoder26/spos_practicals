import java.util.*;

public class RoundRobin {

    private static double TIME_QUANTUM = 0.5;
    private static final Scanner sc = new Scanner(System.in);
    private static LinkedList<Process> processes = new LinkedList<>();
    private static Queue<Process> readyQueue = new LinkedList<>();

    private static class Process implements Comparable<Process>{
        int at;
        int bt;
        double ct;
        double tat;
        double rt;
        double wt;
        double tempBt;
        int index;
        double cpuFirstTime;
        boolean isStarted;

        public Process(int index, int at, int bt){
            this.index = index;
            this.at = at;
            this.bt = bt;
            isStarted = false;
            this.tempBt = bt;
        }

        public void printProcess(){
            System.out.println("Index: " + index + ", AT: " + at + ", BT: " + bt + ", CT: " + ct + ", TAT: " + tat + ", WT: " + wt + ", RT: " + rt);
        }

        @Override
        public int compareTo(Process p2){
            return this.bt - p2.bt;
        }

        public int compareByAt(Process p2){
            return this.at - p2.at;
        }
    }

    private static void setProcesses(){
        System.out.print("Enter the no.of processes: ");
        int n = sc.nextInt();
        for(int x = 0; x < n; x++){
            System.out.println("For process P" + x);
            System.out.print("Arrival Time: ");
            int a = sc.nextInt();
            System.out.print("Burst Time: ");
            int b = sc.nextInt();
            Process process = new Process(x, a, b);
            processes.add(process);
        }
    }

    private static void getProcessesToExecute(LinkedList<Process> processes, double currentTime){
        processes.stream()
                .filter(process -> process.tempBt > 0 && !readyQueue.contains(process) && process.at <= currentTime)
                .sorted(Process::compareByAt)
                .forEach(readyQueue::add);
    }

    private static boolean shouldStop(LinkedList<Process> processes){
        return processes.stream()
                .filter(process -> process.tempBt > 0)
                .toList().isEmpty();
    }

    private static void applyAlgorithm(){
        processes.sort(Process::compareByAt);
        readyQueue.add(processes.get(0));
        double currTime = readyQueue.peek().at;
        while(!shouldStop(processes)){
            Process process = readyQueue.peek();
            System.out.println(readyQueue.size());
            if(!process.isStarted){
                process.isStarted = true;
                process.cpuFirstTime = currTime;
            }
            if(process.tempBt > TIME_QUANTUM){
                currTime += TIME_QUANTUM;
                process.tempBt -= TIME_QUANTUM;
            }
            else{
                currTime += process.tempBt;
                process.tempBt = 0;
                process.ct = currTime;
            }
            getProcessesToExecute(processes, currTime);
            readyQueue.remove();
            if(process.tempBt > 0){
                readyQueue.add(process);
            }
        }
        for(Process process : processes){
            process.tat = process.ct - process.at;
            process.wt = Math.abs(process.tat - process.bt);
            process.rt = Math.abs(process.cpuFirstTime - process.at);
        }
    }

    private static void showResults(){
        System.out.println("**********************************************************");
        for(Process process : processes){
            process.printProcess();
        }
        System.out.println("**********************************************************");
    }


    public static void main(String[] args) {
        setProcesses();
        applyAlgorithm();
        showResults();
    }

}
