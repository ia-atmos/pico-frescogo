// The oracle of the score of FrescoGO!
//
// The functions of the score of `placar/placar.pde` (v5.3.0, at 1d2edb8),
// taken out of the sketch into plain Java, with no Processing. It reads a
// script from the standard input and prints the numbers that the original
// computes: the port must print the same lines for the same scripts.
//
// The functions between the marks BEGIN and END are copied from the sketch.
// The only changes are: `static`, and `1.2f` where the sketch has `1.2`
// (Processing reads a literal with a point as a float). `min`, `max`, `nf`,
// and `IntList` are those of Processing, redefined at the end of this file.
//
// See `oracle/README.md` for the format of the scripts.

import java.io.*;
import java.util.*;

public class Oracle {

    static int         MAJOR    = 5;
    static int         MINOR    = 3;
    static int         REVISION = 0;
    static String      VERSAO   = MAJOR + "." + MINOR + "." + REVISION;

    static int         NOW;

    static boolean     RADAR = false;      // stands for (RADAR!=null || RADAR_MOCK)

    static boolean     ESQUENTA = false;
    static int         ESQUENTA_INICIO;

    // the defaults are those of `placar/data/conf.json`
    static int         CONF_TEMPO       = 300;
    static int         CONF_DISTANCIA   = 750;
    static int         CONF_GOLPES      = 60;
    static int         CONF_EQUILIBRIO  = 130;
    static int         CONF_VEL_MIN     = 50;
    static int         CONF_VEL_MAX     = 85;
    static int         CONF_MAXIMAS     = 10;
    static int         CONF_INTENSIDADE = 10;
    static int         CONF_SAQUE       = 45;
    static boolean     CONF_TRINCA      = false;
    static int         CONF_TREGUA      = 1;
    static int         CONF_QUEDAS      = 2400;
    static int         CONF_ESQUENTA    = 60;
    static int         CONF_DESCANSO    = 90;

    static int         LADO_RADAR       = 0;
    static int         LADO_PIVO        = 0;

    static String[]    CONF_NOMES = { "Atleta 1", "Atleta 2", "Árbitro", "MIX_AA" };

    static String      ESTADO = "ocioso";
    static String      ESTADO_JOGANDO = "sacando";

    static int         INSS_DIFF = 1500;
    static int         INSS_SOUND_NEW = 0;
    static int         INSS_SOUND_OLD = 0;

    static int         JOGO_DESCANSO_TOTAL;
    static int         JOGO_TOTAL, JOGO_QUEDAS, JOGO_QUEDAS_MANUAL;
    static int         JOGO_TEMPO_INICIO, JOGO_TEMPO_PASSADO, JOGO_TEMPO_RESTANTE, JOGO_TEMPO_RESTANTE_SHOW;

    static int[][]     JOGO_JOGS = new int[2][12];

    static int IDX_PTS   = 0;
    static int IDX_GLP   = 1;
    static int IDX_MED   = 2;
    static int IDX_MIN   = 3;
    static int IDX_MAX   = 4;
    static int IDX_MMIN  = 5;
    static int IDX_MMAX  = 6;
    static int IDX_NRMS  = 7;
    static int IDX_BAKS  = 8;
    static int IDX_MNRMS = 9;
    static int IDX_MBAKS = 10;
    static int IDX_INSS  = 11;

    static ArrayList<ArrayList> JOGO = new ArrayList<ArrayList>();

    static int IDX_NOW = 0;
    static int IDX_JOG = 1;
    static int IDX_KMH = 2;
    static int IDX_BAK = 3;
    static int IDX_INS = 4;

    ///////////////////////////////////////////////////////////////////////////
    // BEGIN: copied from `placar.pde`
    ///////////////////////////////////////////////////////////////////////////

    static String ns (String str, int n) {
        int len = str.length();
        for (int i=0; i<n-len; i++) {
            str += " ";
        }
        return str;
    }

    static String conf_pars () {
        return "v" + VERSAO + " / " +
               (CONF_TRINCA ? "trinca" : "dupla") + " / " +
               (conf_radar() ? "radar" : CONF_DISTANCIA + "cm") + " / " +
               (CONF_INTENSIDADE==0 ? "-cnts" : "+cnts") + " / " +
               (CONF_MAXIMAS    ==0 ? "-pots" : "+pots") + " / " +
               CONF_TEMPO   + "s";
    }

    static int conf_tregua () {
        return CONF_TREGUA * CONF_TEMPO / 60;    // 1 / 60s
    }

    static int conf_quedas () {
        return CONF_QUEDAS * CONF_TEMPO / 60 / 1000;
    }

    static int conf_tempo () {
        return (ESQUENTA ? CONF_ESQUENTA : CONF_TEMPO+conf_tregua()*conf_quedas());
    }

    static int conf_golpes (int jog) {
        if (CONF_TRINCA) {
            if (jog == LADO_PIVO) {
                return 0;
            } else {
                return max(1, CONF_TEMPO * CONF_GOLPES / 60);
            }
        } else {
            return max(1, CONF_TEMPO * CONF_GOLPES / 60 / 2);
        }
    }

    static int conf_maximas (int jog) {
        if (CONF_TRINCA) {
            if (jog == LADO_PIVO) {
                return 0;
            } else {
                return max(1, CONF_TEMPO * CONF_MAXIMAS / 60);
            }
        } else {
            return max(1, CONF_TEMPO * CONF_MAXIMAS / 60 / 2);
        }
    }

    static boolean conf_radar () {
        return RADAR;
    }

    static void _jogo_tempo () {
        int ret = 0;
        int last = NOW;

        if (ESQUENTA) {
            if (!ESTADO.equals("ocioso")) {
                ret = NOW - ESQUENTA_INICIO;
            }
        } else {
            for (int i=0; i<JOGO.size(); i++) {
                ArrayList<int[]> seq = JOGO.get(i);
                int S = seq.size();
                if (S > 0) {
                    last = seq.get(S-1)[IDX_NOW];
                    if (S >= 2) {
                        ret += (last - seq.get(0)[IDX_NOW]);
                    }
                }
            }
        }

        JOGO_TEMPO_PASSADO = ret / 1000;
        JOGO_TEMPO_RESTANTE = max(0, conf_tempo()-JOGO_TEMPO_PASSADO - jogo_quedas()*conf_quedas());
        JOGO_TEMPO_RESTANTE_SHOW = JOGO_TEMPO_RESTANTE;

        if (ESTADO.equals("jogando") && ESTADO_JOGANDO.equals("jogando")) {
            JOGO_TEMPO_RESTANTE_SHOW -= ((NOW - last) / 1000);
        }
    }

    static void _jogo_lado_ins (ArrayList<int[]> seq, IntList inss) {
        IntList kmhs = new IntList();
        for (int i=0; i<inss.size(); i++) {
            int kmh = jogo_kmh(seq, inss.get(i));
            kmhs.append(kmh);
        }
        kmhs.sortReverse();
        for (int i=0; i<inss.size(); i++) {
            int I = inss.get(i);
            int kmh = jogo_kmh(seq, I);
            if (kmh == kmhs.get(CONF_INTENSIDADE/2)) {
                seq.get(I)[IDX_INS] = 1;
                break;
            }
        }
    }

    static void _jogo_lado (int jog) {
        IntList kmhs = new IntList();
        IntList nrms = new IntList();
        IntList baks = new IntList();

        // INTENSIDADE
        int ninss = 0;

        for (int i=0; i<JOGO.size(); i++) {
            ArrayList<int[]> seq = JOGO.get(i);
            IntList inss = new IntList();
            int inss_now = -1;
            int J = seq.size() - (conf_radar() ? 1 : 2);
            for (int j=0; j<J; j++) {    // -1/2: ignora ultimo golpe
                int[] golpe = seq.get(j);
                if (golpe[IDX_JOG] != jog) {
                    continue;
                }

                int kmh = jogo_kmh(seq,j);

                int[] prev1 = new int[] {};
                int[] prev2 = new int[] {};
                if (j > 0) {
                    prev1 = seq.get(j-1);
                }
                if (j > 1) {
                    prev2 = seq.get(j-2);
                }

                if (kmh >= CONF_VEL_MIN) {
                    kmhs.append(kmh);

                    // ataque nrm/bak valido?
                    if (CONF_MAXIMAS!=0 && j>0) { // precisa de golpe anterior
                        int kmh2 = jogo_kmh(seq,j-1);
                        // se passou 1s || repetiu jog || 20% mais forte
                        if (prev1[IDX_NOW]+1000<golpe[IDX_NOW] || prev1[IDX_JOG]==jog || kmh2*1.2f<=kmh) {
                            if (golpe[IDX_BAK] == 0) {
                                nrms.append(kmh);
                            } else {
                                baks.append(kmh);
                            }
                        }
                    }
                }

                if (CONF_INTENSIDADE != 0) {
                    golpe[IDX_INS] = 0;   // zera ins
                    int n = inss.size();
                    boolean ok0 = (golpe[IDX_NOW] <= inss_now+INSS_DIFF);
                    boolean ok1 = (j<=0 || prev1[IDX_JOG]==jog);
                    boolean ok2 = (j<=1 || prev2[IDX_JOG]==jog);
                    if ((n==0 || (ok0 && (ok1 || ok2))) && kmh>=CONF_VEL_MIN) {
                        if (inss.size() == 2*CONF_INTENSIDADE-1) {
                            IntList tmp = new IntList();
                            int z = inss.size();
                            for (int k=CONF_INTENSIDADE; k<z; k++) {
                                tmp.append(inss.get(CONF_INTENSIDADE));
                                inss.remove(CONF_INTENSIDADE);
                            }
                            ninss++;
                            _jogo_lado_ins(seq, inss);
                            inss = tmp;
                        }
                        inss.append(j);
                        inss_now = golpe[IDX_NOW];
                        if (inss.size() == CONF_INTENSIDADE) {
                            int s = i*100 + j;
                            if (s > INSS_SOUND_NEW) {
                                INSS_SOUND_NEW = s;
                            }
                        }
                    } else {
                        if (inss.size() >= CONF_INTENSIDADE) {
                            ninss++;
                            _jogo_lado_ins(seq, inss);
                        }
                        inss.clear();
                        if (kmh >= CONF_VEL_MIN) {
                            inss.append(j);
                        }
                    }
                }
            }
            if (CONF_INTENSIDADE!=0 && inss.size()>=CONF_INTENSIDADE) {
                ninss++;
                _jogo_lado_ins(seq, inss);
            }
        }
        kmhs.sortReverse();
        nrms.sortReverse();
        baks.sortReverse();

        int glps = conf_golpes(jog);

        // GOLPES
        int N = min(glps,kmhs.size());
        int sum1 = 0;   // simples
        int sum2 = 0;   // quadrado
        for (int i=0; i<N; i++) {
            int cur = kmhs.get(i);
            sum1 += cur;
            sum2 += cur*(50+cur)/100;
        }

        int Nmax = min(glps/2, N);
        int Nmin = min(glps/2, N-Nmax);

        // MAXIMAS
        int sumMin = 0;
        int sumMax = 0;
        for (int i=0; i<Nmax; i++) {
            int cur = kmhs.get(i);
            sumMax += cur;
        }
        for (int i=0; i<Nmin; i++) {
            int cur = kmhs.get(Nmax+i);
            sumMin += cur;
        }

        // INTENSIDADE
        if (CONF_INTENSIDADE != 0) {
            for (int i=0; i<JOGO.size(); i++) {
                ArrayList<int[]> seq = JOGO.get(i);
                for (int j=0; j<seq.size(); j++) {
                    int[] golpe = seq.get(j);
                    if (golpe[IDX_JOG]==jog && golpe[IDX_INS]==1) {
                        int cur = jogo_kmh(seq,j);
                        sum2 += cur*(50+cur)/100;
                    }
                }
            }
        }

        // BAKS+NRMS
        int nrm1 = 0;
        int bak1 = 0;
        int maxs = conf_maximas(jog);
        if (CONF_MAXIMAS != 0) {
            for (int i=0; i<min(maxs,nrms.size()); i++) {
                int nrm = min(100,nrms.get(i)); // >100 probably error
                nrm1 += nrm;
                sum2 += nrm*(50+nrm)/100;
            }
            for (int i=0; i<min(maxs,baks.size()); i++) {
                int bak = min(100,baks.get(i)); // >100 probably error
                bak1 += bak;
                sum2 += bak*(50+bak)/100;
            }
        }

        JOGO_JOGS[jog][IDX_PTS]   = sum2;
        JOGO_JOGS[jog][IDX_GLP]   = kmhs.size();
        JOGO_JOGS[jog][IDX_MED]   = sum1 * 100 / max(1,N);
        JOGO_JOGS[jog][IDX_MIN]   = (N == 0) ? 0 : kmhs.get(N-1);
        JOGO_JOGS[jog][IDX_MAX]   = (N == 0) ? 0 : kmhs.get(0);
        JOGO_JOGS[jog][IDX_MMIN]  = sumMin * 100 / max(1,glps/2);
        JOGO_JOGS[jog][IDX_MMAX]  = sumMax * 100 / max(1,glps/2);
        JOGO_JOGS[jog][IDX_NRMS]  = nrms.size();
        JOGO_JOGS[jog][IDX_BAKS]  = baks.size();
        JOGO_JOGS[jog][IDX_MNRMS] = nrm1 * 100 / maxs;
        JOGO_JOGS[jog][IDX_MBAKS] = bak1 * 100 / maxs;
        JOGO_JOGS[jog][IDX_INSS]  = ninss;
    }

    static int jogo_kmh (ArrayList<int[]> seq, int i) {
        int[] cur = seq.get(i);
        int kmh = cur[IDX_KMH];
        if (kmh != 0) {     // radar ligado
            return min(100, kmh); // >100 probably error
        } else {            // radar desligado
            if (seq.size() < i+2) {
                return 0;
            } else {
                int[] nxt = seq.get(i+1);
                return min(CONF_VEL_MAX, 36 * CONF_DISTANCIA / (nxt[IDX_NOW] - cur[IDX_NOW]));
            }
        }
    }

    static int jogo_quedas () {
        return JOGO_QUEDAS + JOGO_QUEDAS_MANUAL;
    }

    static int[] jogo_equ () {
        int p0 = JOGO_JOGS[0][IDX_PTS];
        int p1 = JOGO_JOGS[1][IDX_PTS];

        int n0 = JOGO_JOGS[0][IDX_GLP];
        int n1 = JOGO_JOGS[1][IDX_GLP];

        if (CONF_EQUILIBRIO != 0) {
            int pct = max(0, 100-JOGO_TEMPO_PASSADO);
            p0 = min(p0, max(p0*pct/100, p1*CONF_EQUILIBRIO/100));
            p1 = min(p1, max(p1*pct/100, p0*CONF_EQUILIBRIO/100));
        }

        return new int[] {p0,p1};
    }

    static void jogo_calc () {
        _jogo_tempo();
        _jogo_lado(0);
        _jogo_lado(1);

        int[] ps = jogo_equ();
        JOGO_TOTAL = ps[0] + ps[1];
    }

    // The text of the report, from `go_termino`: the part that goes to
    // `saveStrings`, with the date given by the script.
    static String report (String ts) {
        String out = ns("Data:",          15) + ts + "\n"
                   + ns("Versão:",        15) + conf_pars() + "\n"
                   + ns("Total:",         15) + nf(JOGO_TOTAL,5) + " pontos\n"
                   + "\n";
        for (int i=0; i<JOGO.size(); i++) {
            ArrayList<int[]> seq = JOGO.get(i);
            out += "SEQUÊNCIA " + nf(i+1,2) + "\n============\n\nTEMPO   DIR   KMH + *\n-----   ---   --- - -\n";
            for (int j=0; j<seq.size(); j++) {
                int[] golpe = seq.get(j);
                int ms = golpe[IDX_NOW] - JOGO_TEMPO_INICIO;
                out += nf(ms,6) + "   " + (golpe[IDX_JOG]==0 ? "->" : "<-") + "   " + nf(jogo_kmh(seq,j),3) + " " + (golpe[IDX_INS]==0 ? " " : "+") + " " + (golpe[IDX_BAK]==0 ? " " : "*") + "\n";
            }
            out += "\n\n";
        }
        return out;
    }

    ///////////////////////////////////////////////////////////////////////////
    // END: copied from `placar.pde`
    ///////////////////////////////////////////////////////////////////////////

    // Processing

    static int min (int a, int b) { return Math.min(a,b); }
    static int max (int a, int b) { return Math.max(a,b); }

    static String nf (int n, int digits) {
        String s = Integer.toString(Math.abs(n));
        while (s.length() < digits) {
            s = "0" + s;
        }
        return (n < 0 ? "-" : "") + s;
    }

    static class IntList {
        ArrayList<Integer> l = new ArrayList<Integer>();
        int  size   ()      { return l.size(); }
        int  get    (int i) { return l.get(i); }
        void append (int v) { l.add(v); }
        int  remove (int i) { return l.remove(i); }
        void clear  ()      { l.clear(); }
        void sortReverse () { Collections.sort(l, Collections.reverseOrder()); }
    }

    // The script

    static void conf (String key, String val) {
        if (key.equals("trinca")) {
            CONF_TRINCA = val.equals("true");
            return;
        }
        int v = Integer.parseInt(val);
        switch (key) {
            case "tempo":      CONF_TEMPO       = v;   break;
            case "distancia":  CONF_DISTANCIA   = v;   break;
            case "golpes":     CONF_GOLPES      = v;   break;
            case "continuos":  CONF_INTENSIDADE = v;   break;
            case "potentes":   CONF_MAXIMAS     = v;   break;
            case "equilibrio": CONF_EQUILIBRIO  = v;   break;
            case "minima":     CONF_VEL_MIN     = v;   break;
            case "maxima":     CONF_VEL_MAX     = v;   break;
            case "tregua":     CONF_TREGUA      = v;   break;
            case "quedas":     CONF_QUEDAS      = v;   break;
            case "saque":      CONF_SAQUE       = v;   break;
            case "esquenta":   CONF_ESQUENTA    = v;   break;
            case "descanso":   CONF_DESCANSO    = v;   break;
            case "lado_radar": LADO_RADAR       = v-1; break;
            case "lado_pivo":  LADO_PIVO        = v-1; break;
            default: throw new RuntimeException("conf : invalid key : " + key);
        }
    }

    static String ints (int[] v) {
        String s = "";
        for (int i=0; i<v.length; i++) {
            s += (i==0 ? "" : ",") + v[i];
        }
        return s;
    }

    static void print_calc () {
        int[] ps = jogo_equ();
        String ins = "";
        for (int i=0; i<JOGO.size(); i++) {
            ArrayList<int[]> seq = JOGO.get(i);
            ins += (i==0 ? "" : "|");
            for (int j=0; j<seq.size(); j++) {
                ins += seq.get(j)[IDX_INS];
            }
        }
        System.out.println("calc"
            + " tempo="  + JOGO_TEMPO_PASSADO + "," + JOGO_TEMPO_RESTANTE + "," + JOGO_TEMPO_RESTANTE_SHOW
            + " total="  + JOGO_TOTAL
            + " equ="    + ps[0] + "," + ps[1]
            + " jog0="   + ints(JOGO_JOGS[0])
            + " jog1="   + ints(JOGO_JOGS[1])
            + " snd="    + INSS_SOUND_NEW
            + " ins="    + ins
        );
    }

    public static void main (String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        System.setOut(out);
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] w = line.split("\\s+");
            switch (w[0]) {
                case "conf":
                    conf(w[1], w[2]);
                    // as `setup` does
                    if (CONF_TRINCA) {
                        CONF_EQUILIBRIO = 0;
                    }
                    break;
                case "radar":
                    RADAR = w[1].equals("1");
                    break;
                case "esquenta":
                    ESQUENTA = w[1].equals("1");
                    if (w.length > 2) {
                        ESQUENTA_INICIO = Integer.parseInt(w[2]);
                    }
                    break;
                case "inicio":
                    JOGO_TEMPO_INICIO = Integer.parseInt(w[1]);
                    break;
                case "estado":
                    ESTADO = w[1];
                    if (w.length > 2) {
                        ESTADO_JOGANDO = w[2];
                    }
                    break;
                case "now":
                    NOW = Integer.parseInt(w[1]);
                    break;
                case "seq":
                    JOGO.add(new ArrayList<int[]>());
                    break;
                case "hit": {
                    int[] golpe = {
                        Integer.parseInt(w[1]), Integer.parseInt(w[2]),
                        Integer.parseInt(w[3]), Integer.parseInt(w[4]), 0
                    };
                    ArrayList<int[]> seq = JOGO.get(JOGO.size()-1);
                    seq.add(golpe);
                    break;
                }
                case "quedas":
                    JOGO_QUEDAS = Integer.parseInt(w[1]);
                    break;
                case "manual":
                    JOGO_QUEDAS_MANUAL = Integer.parseInt(w[1]);
                    break;
                case "calc":
                    jogo_calc();
                    print_calc();
                    break;
                case "report":
                    System.out.print(report(w[1]));
                    break;
                default:
                    throw new RuntimeException("invalid command : " + w[0]);
            }
        }
    }
}
