import java.util.*;


public class Round_Robin {

    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<Process> processes = new ArrayList<>();

    private static int TIME_QUANTUM = 2;

    private static class Process implements Comparable<Process> {
        int at, bt, wt, tat, index, rt, tempBT, cpuFirstTime;
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

    private static void getProcessesToExecute(Queue<Process> readyQueue, ArrayList<Process> processes, int currentTime) {
        processes.stream()
                .filter(process -> process.tempBT > 0 && !readyQueue.contains(process) && process.at <= currentTime)
                .sorted(Process::compareByAt)
                .forEach(readyQueue::add);
    }

    private static boolean shouldStop(ArrayList<Process> processes){
        return processes.stream()
                .filter(process -> process.tempBT > 0)
                .toList().isEmpty();
    }

    private static void performScheduling(){
        processes.sort(Process::compareByAt);
        int currentTime = 0;
        List<Process> processesToExecute = processes;
        Queue<Process> readyQueue = new LinkedList<>();
        readyQueue.add(processesToExecute.get(0));
        while(!shouldStop(processes)){
            Process process = readyQueue.peek();
            if(!process.started){
                process.started = true;
                process.cpuFirstTime = currentTime;
            }
            if(process.tempBT > TIME_QUANTUM){
                currentTime += TIME_QUANTUM;
                process.tempBT -= TIME_QUANTUM;
            }
            else{
                currentTime += process.tempBT;
                process.tempBT = 0;
                process.rt = currentTime;
            }
            getProcessesToExecute(readyQueue, processes, currentTime);
            readyQueue.remove();
            if(process.tempBT > 0){
                readyQueue.add(process);
            }
        }
        for(Process process : processes){
            process.tat = process.rt - process.at;
            process.wt = Math.abs(process.tat - process.bt);
        }
    }

    private static void showResults() {
        System.out.println("Process\tArrival Time\tBurst Time\tWaiting Time\tTurnAround Time\tCPU FirstTime\tResponse Time");
        for (Process process : processes) {
            System.out.println(process.index + "\t\t" + process.at + "\t\t\t\t" + process.bt + "\t\t\t\t" + process.wt + "\t\t\t\t" + process.tat + "\t\t\t\t" + process.cpuFirstTime + "\t\t\t\t" + process.rt);
        }
    }


    public static void main(String[] args){
        getProcesses();
        performScheduling();
        showResults();
    }


}
