

public class Trame {
    private static final String FLAG = "01111110";
    private String type;
    private int num;
    private String donne;
    private int crc;

    public Trame(String type ,String donne, int num, int crc) {
        this.type = type;
        this.num = num;
        this.donne = donne;
        this.crc = crc;
    }

    public Trame() {
    }

    public String fulltrame() {
        String trameString = FLAG + type + num + donne + crc + FLAG;
        return trameString;
    }


    // Getter methods
    public String getType() {
        return type;
    }

    public int getNum() {
        return num;
    }

    public String getDonne() {
        return donne;
    }

    public int getCRC() {
        return crc;
    }
}



