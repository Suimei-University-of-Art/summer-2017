import java.io.*;
import java.util.*;

class parse_as_wa {
    FastScanner in;
    PrintWriter out;

    final static long P = 239;
    final static long[] Ppow = new long[501];
    
    static {
        Ppow[0] = 1;
        for (int i = 1; i < Ppow.length; i++) {
            Ppow[i] = Ppow[i - 1] * P;
        }
    }
    
    long[] compHashes(String s) {
        long[] res = new long[s.length() + 1];
        for (int i = 0; i < s.length(); i++) {
            res[i + 1] = res[i] * P + s.charAt(i);  
        }
        return res;
    }
    
    boolean compStrings(long[] hashes1, int from1, long[] hashes2, int from2, int len) {
        long h1 = hashes1[from1 + len] - hashes1[from1] * Ppow[len];
        long h2 = hashes2[from2 + len] - hashes2[from2] * Ppow[len];
        return h1 == h2;
    }
    
    class Word {
        String s;
        long[] h;

        Word(String s) {
            this.s = s;
            h = compHashes(s);
        }
    }
    
    boolean[] calcCan(Word w, Word[] d) {
        int L = w.s.length();
        int n = d.length;
        boolean[] can = new boolean[L + 1];
        can[0] = true;
        for (int j = 0; j < L; j++) {
            if (can[j]) {
                for (int k = 0; k < n; k++) {
                    int kl = d[k].s.length();
                    if (j + kl <= L && compStrings(w.h, j, d[k].h, 0, kl)) {
                        can[j + kl] = true;
                    } 
                }
            }
        }
        return can;
    }

    public void solve() throws IOException {
        int n = in.nextInt();
        assert 1 <= n && n <= 250;
        Word[] d = new Word[n];
        Set<String> words = new HashSet<String>();
        for (int i = 0; i < n; i++) {
            String dd = in.next();
            assert 1 <= dd.length() && dd.length() <= 500;
            assert dd.matches("[a-z]*");
            d[i] = new Word(dd);
            words.add(dd);
        }
        assert words.size() == n;
        
        Arrays.sort(d, new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                return o2.s.length() - o1.s.length();
            }
        });
        
        boolean[][] canPrefix = new boolean[n][];
        for (int i = 0; i < n; i++) {
            canPrefix[i] = calcCan(d[i], d);
//            System.out.println(Arrays.toString(canPrefix[i]));
        }
        
        boolean[][] greedySuffix = new boolean[n][];
        for (int i = 0; i < n; i++) {
            int L = d[i].s.length();
            greedySuffix[i] = new boolean[L + 1];
            greedySuffix[i][L] = true;
            for (int j = L - 1; j >= 0; j--) {
                for (Word z : d) {
                    if (j + z.s.length() <= L && compStrings(d[i].h, j, z.h, 0, z.s.length())) {
                        greedySuffix[i][j] = greedySuffix[i][j + z.s.length()];
                        break;
                    }
                }
            }
//            System.out.println(Arrays.toString(greedySuffix[i]));
        }
        
        
        String ans = null;
        
        boolean[][] foundLen = new boolean[n][1001];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int len = Math.min(d[i].s.length(), d[j].s.length());
                for (int k = 1; k < len; k++) {
                    int thisLen = d[i].s.length() + d[j].s.length() - k;
                    if (!foundLen[j][thisLen] && compStrings(d[i].h, d[i].s.length() - k, d[j].h, 0, k)) {
                        foundLen[j][thisLen] = true;
//                        System.out.println(i + " " + j + " " + k + " " + thisLen);
                        if (canPrefix[i][d[i].s.length() - k] && !greedySuffix[j][k] && (ans == null || ans.length() > thisLen)) {
//                            System.out.println(i + " " + j + " " + k + " " + thisLen);
                            ans = d[i].s + d[j].s.substring(k);
                        }
                    }
                }
            }
        }
         
        if (ans == null) {
            out.println("Good vocabulary!");
        } else {
            out.println(ans);
        }
    }

    public void run() {
        try {
            in = new FastScanner(System.in);
            out = new PrintWriter(System.out);

            solve();

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        FastScanner(InputStream f) {
            br = new BufferedReader(new InputStreamReader(f));
        }

        String next() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
    }

    public static void main(String[] arg) {
        new parse_as_wa().run();
    }
}
