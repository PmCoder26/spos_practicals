import java.io.*;
import java.util.*;


public class _2_pass_assembler {
    static class ST{    // Symbol table.
        int i;      // index
        String s;   // symbol
        int a;   // address
        public ST(int i, String s, int a){
            this.i = i;
            this.s = s;
            this.a = a;
        }
    }
    static class LT{    // Literal table.
        int i;      // index
        int l;   // literal
        int a;   // address
        public LT(int i, int l, int a){
            this.i = i;
            this.l = l;
            this.a = a;
        }
    }
    static class PT{    // Pool table.
        int count;      // count of total no.of literals processed currently.
        public PT(int c){
            count = c;
        }
    }
    static class Mnemonic{
        String name;
        int code;
        String mClass;      // mnemonic class.
        public Mnemonic(String n, int code, String mClass){
            name = n;
            this.code = code;
            this.mClass = mClass;
        }
    }

    public static BufferedReader reader;
    public static BufferedWriter writer;
    private ArrayList<ST> symbol_table = new ArrayList<>();
    private ArrayList<LT> literal_table = new ArrayList<>();
    private ArrayList<PT> pool_table = new ArrayList<>();
    private int loc_cnt = -1;

    private static Mnemonic[] ADs = {
            new Mnemonic("START", 1, "AD"), new Mnemonic("END", 2, "AD"),
            new Mnemonic("ORIGIN", 3, "AD"), new Mnemonic( "EQU", 4, "AD"),
            new Mnemonic("LTORG", 5, "AD")
    };
    private static Mnemonic[] ISs = {
            new Mnemonic("STOP", 0, "IS"), new Mnemonic("ADD", 1, "IS"),
            new Mnemonic("SUB", 2, "IS"), new Mnemonic("MULT", 3, "IS"),
            new Mnemonic("MOVER", 4, "IS"), new Mnemonic("MOVEM", 5, "IS"),
            new Mnemonic("COMP", 6, "IS"), new Mnemonic("BC", 7, "IS"),
            new Mnemonic("DIV", 8, "IS"), new Mnemonic("READ", 9, "IS"),
    };
    private static Mnemonic[] DLs = {
            new Mnemonic("DS", 1, "DL"), new Mnemonic("DC", 2, "DL")
    };
    private static Mnemonic[] REGs = {
            new Mnemonic("AREG", 1, "REG"), new Mnemonic("BREG", 2, "REG"),
            new Mnemonic("CREG", 3, "REG")
    };
//    private Mnemonic[] mnemonics = {
//
//    };

    public _2_pass_assembler(){
        try{
            reader = new BufferedReader(new FileReader(new File("/Users/parimal/SPOS_Assignments/src/pass_1_input_assembler.txt")));
            writer = new BufferedWriter(new FileWriter(new File("/Users/parimal/SPOS_Assignments/src/pass_1_output_assembler.txt")));
            pool_table.add(new PT(0));      // initially the processed literals count is zero.
        } catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
    }

    public Mnemonic hasAD(String line){      // assembler directive.
        Mnemonic ad = null;
        for(int x= 0; x < ADs.length; x++){
            if(line.contains(ADs[x].name)){
                ad = new Mnemonic(ADs[x].name, ADs[x].code, ADs[x].mClass);
                break;
            }
        }
        return ad;
    }
    public void processAD(Mnemonic mnemonic, String line) throws IOException{
        if(mnemonic.name.equals("START")){
            loc_cnt = Integer.parseInt(line.substring(14, line.length()));
            writer.write("(" + mnemonic.mClass + "," + mnemonic.code + ") (C" + loc_cnt + ")\n");
            loc_cnt++;
        }
        else if(mnemonic.name.equals("END")){
            return;
        }
        else if(mnemonic.name.equals("ORIGIN")){
//            loc_cnt = Integer.parseInt()
        }
        else if(mnemonic.name.equals("LTORG")){
            for(int x= 0; x < literal_table.size(); x++){
                LT temp = literal_table.get(x);
                if(temp.a == 0){
                    temp.a = loc_cnt;
                    writer.write("(" + mnemonic.mClass + ",0" + mnemonic.code + ") (DL,02) " + "(C," + temp.l + ")\n");
                    loc_cnt++;
                }
            }
            pool_table.add(new PT(literal_table.size()));
        }
    }
    public Mnemonic hasIS(String line){      // assembler directive.
        Mnemonic ad = null;
        for(int x= 0; x < ADs.length; x++){
            if(line.contains(ISs[x].name)){
                ad = new Mnemonic(ISs[x].name, ISs[x].code, ISs[x].mClass);
                break;
            }
        }
        return ad;
    }
    public void processIS(Mnemonic mnemonic, String line) throws IOException{
        String label = "";
        if(line.charAt(4) != ' '){
            int x = 4;
            while(line.charAt(x) != ' '){
                label += line.charAt(x++);
            }
            symbol_table.add(new ST(symbol_table.size(), label, loc_cnt));
        }
        if(mnemonic.name.equals("MOVER")){

        }
    }
    public Mnemonic hasDL(String line){      // assembler directive.
        Mnemonic ad = null;
        for(int x= 0; x < DLs.length; x++){
            if(line.contains(DLs[x].name)){
                ad = new Mnemonic(DLs[x].name, DLs[x].code, DLs[x].mClass);
                break;
            }
        }
        return ad;
    }
    public void processDL(Mnemonic temp, String line) throws IOException{

    }

//    public void processMnemonic(Mnemonic mnemonic){
//
//    }
//
//    public Mnemonic findMnemonic(String line){
//        Mnemonic result = null;
//        for(int x = 0; x < ADs.length; x++){
//            if(line.contains(ADs[x].name)){
//                result = new Mnemonic(ADs[x].name, ADs[x].code, ADs[x].mClass);
//                break;
//            }
//        }
//        return result;
//    }

//    public Mnemonic hasIS(String line){
//
//    }

    public static void main(String[] args) {
        _2_pass_assembler assembler = new _2_pass_assembler();

        String line = "";

        try {
            while ((line = assembler.reader.readLine()) != null) {
                Mnemonic mnemonic = null;
                if((mnemonic = assembler.hasAD(line)) != null){
                    assembler.processAD(mnemonic, line);
                }
                else if((mnemonic = assembler.hasIS(line)) != null){
                    assembler.processIS(mnemonic, line);
                }
                else if((mnemonic = assembler.hasDL(line)) != null){
                    assembler.processDL(mnemonic, line);
                }
                else{
                    System.out.println("Invalid statement.");
                    System.out.println("Terminating the pass-1 process");
                    break;
                }
                assembler.reader.close();
                assembler.writer.close();
            }
        } catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }

    }
}
