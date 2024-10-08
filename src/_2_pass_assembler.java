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
    static class ReaderWriterCollection{
        public BufferedWriter pass1Writer;
        public BufferedReader pass1Reader;
        public BufferedWriter pass2Writer;
        public BufferedReader pass2Reader;
        public ReaderWriterCollection() throws IOException {
            pass1Writer = new BufferedWriter(new FileWriter(new File("/Users/parimal/SPOS_Assignments/src/pass_1_output_assembler.txt")));
            pass1Reader = new BufferedReader(new FileReader(new File("/Users/parimal/SPOS_Assignments/src/pass_1_input_assembler.txt")));
            pass2Writer = new BufferedWriter(new FileWriter(new File("/Users/parimal/SPOS_Assignments/src/pass_2_output_assembler.txt")));
            pass2Reader = new BufferedReader(new FileReader(new File("/Users/parimal/SPOS_Assignments/src/pass_1_output_assembler.txt")));
        }
    }

    private BufferedReader reader;
    private BufferedWriter writer;
    private ReaderWriterCollection collection;
    private ArrayList<ST> symbol_table = new ArrayList<>();
    private ArrayList<LT> literal_table = new ArrayList<>();
    private ArrayList<PT> pool_table = new ArrayList<>();
    private int loc_cnt = -1;
    private boolean isDonePass1 = false;

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

    public _2_pass_assembler() throws IOException {
        collection = new ReaderWriterCollection();
    }
    // ************************************************** Pass-1 *******************************************************

    public Mnemonic getREG(String line){
        for(int x = 0; x < REGs.length; x++){
            if(line.contains(REGs[x].name)){
                return new Mnemonic(REGs[x].name, REGs[x].code, REGs[x].mClass);
            }
        }
        return  null;
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
            writer.write("(AD,0" + mnemonic.code + ") (C," + loc_cnt + ")\n");
        }
        else if(mnemonic.name.equals("END")){
            writer.write("(AD,0" + mnemonic.code + ")\n");
        }
        else if(mnemonic.name.equals("LTORG")){
            for(int x= 0; x < literal_table.size(); x++){
                LT temp = literal_table.get(x);
                if(temp.a == 0){
                    temp.a = loc_cnt;
                    writer.write("(AD,0" + mnemonic.code + ") (DL,02) " + "(C," + temp.l + ")\n");
                    loc_cnt++;
                }
            }
            pool_table.add(new PT(literal_table.size()));
        }
    }

    public Mnemonic hasIS(String line){      // assembler directive.
        Mnemonic ad = null;
        for(int x= 0; x < ISs.length; x++){
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
            Mnemonic reg = getREG(line);
            if(line.contains("=")){        // if literal.
                int idx = line.indexOf("=");
                int literal = Integer.parseInt(line.substring(idx + 2, idx + 3));
                LT temp = null;
                for(int x = 0; x < literal_table.size(); x++){
                    LT t = literal_table.get(x);
                    if(t.l == literal){
                        temp = t;
                        break;
                    }
                }
                if(temp == null) {      // if literal is not present then only add in table.
                    LT t = new LT(literal_table.size(), literal, 0);
                    literal_table.add(t);
                    temp = t;
                }
                writer.write("(IS,0" + mnemonic.code + ") (RG,0" + reg.code + ") (L," + temp.i + ")\n");
                loc_cnt++;
            }
        }
        else if(mnemonic.name.equals("MOVEM")){
            Mnemonic reg = getREG(line);
            String symbol = line.substring(line.indexOf("MOVEM") + 6, line.lastIndexOf(","));
            ST temp = null;
            for(int x = 0; x < symbol_table.size(); x++){
                ST s = symbol_table.get(x);
                if(s.s.equals(symbol)){
                    temp = s;
                    break;
                }
            }
            if(temp == null){
                ST s = new ST(symbol_table.size(), symbol, 0);
                symbol_table.add(s);
                temp = s;
            }
            writer.write("(IS,0" + mnemonic.code + ") (S," + temp.i + ") (RG,0" + reg.code + ")\n");
            loc_cnt++;
        }
        else if(mnemonic.name.equals("READ")){
            String symbol = line.substring(line.indexOf("READ") + 5, line.length());
            ST temp = null;
            for(int x = 0; x < symbol_table.size(); x++){
                ST s = symbol_table.get(x);
                if(s.s.equals(symbol)){
                    temp = s;
                    break;
                }
            }
            if(temp == null){
                ST s = new ST(symbol_table.size(), symbol, 0);
                symbol_table.add(s);
                temp = s;
            }
            writer.write("(IS,0" + mnemonic.code + ") (S," + temp.i + ")\n");
            loc_cnt++;
        }
        else if(mnemonic.name.equals("MULT")){
            Mnemonic reg = getREG(line);
            String symbol = line.substring(line.indexOf(",") + 2, line.length());
            ST temp = null;
            for(int x = 0; x < symbol_table.size(); x++){
                ST s = symbol_table.get(x);
                if(s.s.equals(symbol)){
                    temp = s;
                    break;
                }
            }
            if(temp == null){
                ST s = new ST(symbol_table.size(), symbol, 0);
                symbol_table.add(s);
                temp = s;
            }
            writer.write("(IS,0" + mnemonic.code + ") (RG,0" + reg.code + ") (S," + temp.i + ")\n");
            loc_cnt++;
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
    public void processDL(Mnemonic mnemonic, String line) throws IOException{
        if(mnemonic.name.equals("DS")){
            String symbol = "";
            ST temp = null;
            for(int x = 4; line.charAt(x) != ' '; x++){
                symbol += line.charAt(x);
            }
            for(int x = 0; x < symbol_table.size(); x++){
                ST s = symbol_table.get(x);
                if(s.s.equals(symbol)){
                    temp = s;
                    break;
                }
            }
            temp.a = loc_cnt;
            String cons = line.substring(line.indexOf("DS") + 4, line.length());
            writer.write("(S," + temp.i + ") (DS,0" + mnemonic.code + ") (C," + cons + ")\n");
            loc_cnt++;
        }
    }

    public void startPass_1(){
        try {
            reader = collection.pass1Reader;
            writer = collection.pass1Writer;
            String line = "";
            while ((line = reader.readLine()) != null) {
                Mnemonic mnemonic = null;
                if((mnemonic = hasAD(line)) != null){
                    processAD(mnemonic, line);
                }
                else if((mnemonic = hasIS(line)) != null){
                    processIS(mnemonic, line);
                }
                else if((mnemonic = hasDL(line)) != null){
                    processDL(mnemonic, line);
                }
                else{
                    System.out.println("Invalid statement.");
                    System.out.println("Terminating the pass-1 process");
                    System.out.println("Location counter: " + loc_cnt);
                    break;
                }
            }
            System.out.println();

            // printing the symbol table.
            System.out.println("Symbol Table");
            for(int x = 0; x < symbol_table.size(); x++){
                ST temp = symbol_table.get(x);
                System.out.println(temp.i + "   " + "   " + temp.s + "  " + temp.a);
            }
            System.out.println();

            // printing the literal table.
            System.out.println("Literal Table");
            for(int x = 0; x < literal_table.size(); x++){
                LT temp = literal_table.get(x);
                System.out.println(temp.i + "   " + "   " + temp.l + "  " + temp.a);
            }
            System.out.println();

            // printing the pool table.
            System.out.println("Pool Table");
            for(int x = 0; x < pool_table.size(); x++){
                PT temp = pool_table.get(x);
                System.out.println(temp.count);
            }

            reader.close();
            writer.close();
            isDonePass1 = true;
        } catch (Exception e){
            System.out.println("Location counter: " + (loc_cnt + 1));
            System.out.println(e.getLocalizedMessage());
        }
    }

    public boolean isPass1Done(){
        return isDonePass1;
    }
    // *****************************************************************************************************************

    // ************************************************** Pass-2 *******************************************************

    public void startPass_2(){
        int lineNo = 1;
        try{
            reader = collection.pass2Reader;
            writer = collection.pass2Writer;
            String line = "";
            while((line = reader.readLine()) != null){
                if(line.contains("AD")){
                    String code = line.substring(4, 6);
                    if(code.equals("01")){     // not START
                        writer.write("\n");
                    }
                    else if(code.equals("05")){
                        writer.write("(00) (00) (00" + line.substring(line.lastIndexOf(",") + 1, line.lastIndexOf(")")) + ")\n");
                    }
                    else if(code.equals("02")){
                        writer.write("\n");
                    }
                }
                else if(line.contains("IS")){
                    String code = line.substring(4, 6);
                    String temp = "(" + code + ") ";
                    if(line.contains("RG")){
                        temp += "(" + line.substring(line.indexOf("RG") + 3, line.indexOf("RG") + 5) + ") ";
                    }
                    int address = -1;
                    if(line.contains("(L")){
                        String literalIdx = line.substring(line.indexOf("(L,") + 3, line.indexOf("(L,") + 4);
                         address = literal_table.get(Integer.parseInt(literalIdx)).a;
                    }
                    else if(line.contains("(S,")){
                        if(code.equals("09")){
                            temp += "(00) ";
                        }
                        String symbolIdx = line.substring(line.indexOf("(S,") + 3, line.indexOf("(S,") + 4);
                        address = symbol_table.get(Integer.parseInt(symbolIdx)).a;
                    }
                    if(address != -1){
                        temp += "(" + address + ")";
                        writer.write(temp + "\n");
                    }
                    else{
                        System.out.println("Error while processing line: " + line);
                        break;
                    }
                }
                lineNo++;
            }

            reader.close();
            writer.close();
        } catch (Exception e){
            System.out.println("Line number: " + lineNo);
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        _2_pass_assembler assembler = new _2_pass_assembler();
        assembler.startPass_1();
        if(assembler.isPass1Done()){
            assembler.startPass_2();
        }
        else{
            System.out.println("Unable to start pass-2");
        }
    }
}
