import java.util.*;

public class MemoryPlacementStrategies {

    private static Scanner sc = new Scanner(System.in);

    private static class Process {
        int id;
        int memory;
        public Process(int id, int memory){
            this.id = id;
            this.memory = memory;
        }
    }

    private static class Block {
        private LinkedList<Process> processes;
        private int memory;
        public Block(int memory){
            processes = new LinkedList<>();
            this.memory = memory;
        }

        public void printProcesses(){
            for(int x = 0; x < processes.size(); x++){
                Process process = processes.get(x);
                System.out.println("Id: " + process.id + ", Memory: " + process.memory);
            }
        }

        public void addProcess(Process process){
            processes.add(process);
        }

    }

    private static class CommonUtils {
        protected LinkedList<Block> blocks;
        protected LinkedList<Process> processes;

        public CommonUtils(){
            blocks = new LinkedList<>();
            processes = new LinkedList<>();
        }

        public void setProcesses(int n){
            for(int x = 0; x < n; x++){
                System.out.print("Enter the memory space for process-" + x + ": ");
                Process process = new Process(x, sc.nextInt());
                processes.add(process);
            }
        }

        public void setBlocks(int n){
            for(int x = 0; x < n; x++){
                System.out.print("Memory capacity of block B" + x + ": ");
                Block block = new Block(sc.nextInt());
                blocks.add(block);
            }
        }

        public void getResults(){
            for(int x = 0; x < blocks.size(); x++){
                Block block = blocks.get(x);
                System.out.println("***************" + "For block-" + x + "***************");
                block.printProcesses();
            }
        }
    }

    private static class FirstFit extends CommonUtils {
        public void applyAlgorithm(){
            for(int x = 0; x < processes.size(); x++){
                Process process = processes.get(x);
                for(int y = 0; y < blocks.size(); y++){
                    Block block = blocks.get(y);
                    if(block.memory >= process.memory){
                        block.addProcess(process);
                        block.memory = block.memory - process.memory;
                        break;
                    }
                }
            }
        }
    }

    private static class NextFit extends CommonUtils {
        public void applyAlgorithm(){
            int idx = 0;
            for(int x = 0; x < processes.size(); x++){
                Process process = processes.get(x);
                for(int y = idx; y < blocks.size(); y++){
                    Block block = blocks.get(y);
                    if(block.memory >= process.memory){
                        block.addProcess(process);
                        block.memory = block.memory - process.memory;
                        idx = y;
                        break;
                    }
                }
                if(idx == blocks.size() - 1){
                    idx = 0;
                }
            }
        }
    }

    private static class BestFit extends CommonUtils {
        public void applyAlgorithm(){
            for(int x = 0; x < processes.size(); x++){
                Process process = processes.get(x);
                int minMemory = Integer.MAX_VALUE;
                int minMemoryIdx = -1;
                for(int y = 0; y < blocks.size(); y++){
                    Block block = blocks.get(y);
                    if(minMemory > block.memory && block.memory >= process.memory){
                        minMemory = block.memory;
                        minMemoryIdx = y;
                    }
                }
                if(minMemoryIdx != -1){
                    Block block = blocks.get(minMemoryIdx);
                    block.addProcess(process);
                    block.memory = block.memory - process.memory;
                }
            }
        }
    }

    private static class WorstFit extends CommonUtils {
        public void applyAlgorithm(){
            for(int x = 0; x < processes.size(); x++){
                Process process = processes.get(x);
                int maxMemory = Integer.MIN_VALUE;
                int maxMemoryIdx = -1;
                for(int y = 0; y < blocks.size(); y++){
                    Block block = blocks.get(y);
                    if(maxMemoryIdx < block.memory && block.memory >= process.memory){
                        maxMemory = block.memory;
                        maxMemoryIdx = y;
                    }
                }
                if(maxMemoryIdx != -1){
                    Block block = blocks.get(maxMemoryIdx);
                    block.addProcess(process);
                    block.memory = block.memory - process.memory;
                }
            }
        }
    }



    public static void main(String[] args) {

        WorstFit ff = new WorstFit();
        ff.setBlocks(5);
        ff.setProcesses(4);
        ff.applyAlgorithm();
        ff.getResults();

    }

}

