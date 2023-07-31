package restaurant.apps.falcons.flaconsrestaurant.printing;

import java.util.ArrayList;
import java.util.List;

public class Comiler {
    private static int[] a;
    private static int b;
    private List c = new ArrayList();
    private String d = "{0x622, 0xfef6, 0xfef5, 0xfef6, 0xfef5};{0x623, 0xfef8, 0xfef7, 0xfef8, 0xfef7};{0x625, 0xfefa, 0xfef9, 0xfefa, 0xfef9};{0x627, 0xfefc, 0xfefb, 0xfefc, 0xfefb}";
    private static String e = "{0x621, 0xfe80, 0xfe80, 0xfe80, 0xfe80};{0x622, 0xfe82, 0xfe81, 0xfe82, 0xfe81};{0x623, 0xfe84, 0xfe83, 0xfe84, 0xfe83};{0x624, 0xfe86, 0xfe85, 0xfe86, 0xfe85};{0x625, 0xfe88, 0xfe87, 0xfe88, 0xfe87};{0x626, 0xfe8a, 0xfe8b, 0xfe8c, 0xfe89};{0x627, 0xfe8e, 0xfe8d, 0xfe8e, 0xfe8d};{0x628, 0xfe90, 0xfe91, 0xfe92, 0xfe8f};{0x629, 0xfe94, 0xfe93, 0xfe93, 0xfe93};{0x62a, 0xfe96, 0xfe97, 0xfe98, 0xfe95};{0x62b, 0xfe9a, 0xfe9b, 0xfe9c, 0xfe99};{0x62c, 0xfe9e, 0xfe9f, 0xfea0, 0xfe9d};{0x62d, 0xfea2, 0xfea3, 0xfea4, 0xfea1};{0x62e, 0xfea6, 0xfea7, 0xfea8, 0xfea5};{0x62f, 0xfeaa, 0xfea9, 0xfeaa, 0xfea9};{0x630, 0xfeac, 0xfeab, 0xfeac, 0xfeab};{0x631, 0xfeae, 0xfead, 0xfeae, 0xfead};{0x632, 0xfeb0, 0xfeaf, 0xfeb0, 0xfeaf};{0x633, 0xfeb2, 0xfeb3, 0xfeb4, 0xfeb1};{0x634, 0xfeb6, 0xfeb7, 0xfeb8, 0xfeb5};{0x635, 0xfeba, 0xfebb, 0xfebc, 0xfeb9};{0x636, 0xfebe, 0xfebf, 0xfec0, 0xfebd}; {0x637, 0xfec2, 0xfec3, 0xfec4, 0xfec1};{0x638, 0xfec6, 0xfec7, 0xfec8, 0xfec5};{0x639, 0xfeca, 0xfecb, 0xfecc, 0xfec9};{0x63a, 0xfece, 0xfecf, 0xfed0, 0xfecd};{0x63b, 0, 0, 0, 0};{0x63c, 0, 0, 0, 0};{0x63d, 0, 0, 0, 0};{0x63e, 0, 0, 0, 0};{0x63f, 0, 0, 0, 0}; {0x640,0x0640, 0x0640, 0x0640, 0x0640}; {0x641, 0xfed2, 0xfed3, 0xfed4, 0xfed1}; {0x642, 0xfed6, 0xfed7, 0xfed8, 0xfed5};{0x643, 0xfeda, 0xfedb, 0xfedc, 0xfed9};{0x644, 0xfede, 0xfedf, 0xfee0, 0xfedd}; {0x645, 0xfee2, 0xfee3, 0xfee4, 0xfee1}; {0x646, 0xfee6, 0xfee7, 0xfee8, 0xfee5};{0x647, 0xfeea, 0xfeeb, 0xfeec, 0xfee9};{0x648, 0xfeee, 0xfeed, 0xfeee, 0xfeed};{0x649, 0xfef0, 0xfeef, 0xfef0, 0xfeef};{0x64a, 0xfef2, 0xfef3, 0xfef4, 0xfef1}; {0x64b, 0, 0, 0, 0}; {0x64c, 0, 0, 0, 0}; {0x64d, 0, 0, 0, 0}; {0x64e, 0, 0, 0, 0}; {0x64f, 0, 0, 0, 0};{0x650, 0, 0, 0, 0};{0x651, 0, 0, 0, 0};{0x652, 0, 0, 0, 0};";
    private List f = new ArrayList();
    private static int g = 1604;
    private static int[] h = new int[]{1574, 1576, 1578, 1579, 1580, 1581, 1582, 1587, 1588, 1589, 1590, 1591, 1592, 1593, 1594, 1600, 1601, 1602, 1603, 1604, 1605, 1606, 1607, 1610};
    private static int[] i = new int[]{1570, 1571, 1572, 1573, 1575, 1577, 1583, 1584, 1585, 1586, 1608, 1609};

    public List get_lookupArray() {
        return this.returnList(e);
    }

    public void Comiler() {
        this.c = this.returnList(this.d);
        this.f = this.get_lookupArray();
        a = new int[this.f.size() * 5];

        for(int var1 = 0; var1 < this.f.size(); ++var1) {
            a[var1 * 5] = ((UnicodeChr)this.f.get(var1)).get_charCode();
            a[var1 * 5 + 1] = ((UnicodeChr)this.f.get(var1)).get_end();
            a[var1 * 5 + 2] = ((UnicodeChr)this.f.get(var1)).get_init();
            a[var1 * 5 + 3] = ((UnicodeChr)this.f.get(var1)).get_mid();
            a[var1 * 5 + 4] = ((UnicodeChr)this.f.get(var1)).get_iso();
        }

        this.a((int[])a, 0, a.length - 1);

        for(b = 0; a[b] == 0; ++b) {
            ;
        }

    }

    public List returnList(String var1) {
        ArrayList var2 = new ArrayList();
        String[] var10 = var1.replace("{", "").replace("}", "").split(";");

        for(int var3 = 0; var3 <= var10.length - 1; ++var3) {
            UnicodeChr var4 = new UnicodeChr();
            String[] var5;
            int var6 = Integer.parseInt((var5 = var10[var3].split(","))[0].replace("0x", "").trim(), 16);
            int var7 = Integer.parseInt(var5[1].replace("0x", "").trim(), 16);
            int var8 = Integer.parseInt(var5[2].replace("0x", "").trim(), 16);
            int var9 = Integer.parseInt(var5[3].replace("0x", "").trim(), 16);
            int var11 = Integer.parseInt(var5[4].replace("0x", "").trim(), 16);
            var4.set_charCode(var6);
            var4.set_end(var7);
            var4.set_init(var8);
            var4.set_mid(var9);
            var4.set_iso(var11);
            var2.add(var4);
        }

        return var2;
    }

    public boolean IsArabic(char var1) {
        return (var1 & '\uff00' ^ 1536) != 0 && (var1 & '\uff00' ^ '︀') != 0?false:a(a, var1) >= 0;
    }

    private boolean a(char var1) {
        return a(h, var1) >= 0;
    }

    private boolean b(char var1) {
        return a(i, var1) >= 0;
    }

    private static void a(StringBuilder var0, int var1, int var2) {
        for(int var3 = 0; var3 < var2; ++var3) {
            int var4;
            if((var4 = var2 - var3 - 1) <= var3) {
                return;
            }

            char var5 = var0.charAt(var1 + var4);
            var4 += var1;
            char var6 = var0.charAt(var3 + var1);
            var0.insert(var4, var6);
            var0.deleteCharAt(var4 + 1);
            var0.insert(var3 + var1, var5);
            var0.deleteCharAt(var3 + var1 + 1);
        }

    }

    public void ArabicReverse(StringBuilder var1) {
        a((StringBuilder)var1, 0, var1.length());

        for(int var2 = 0; var2 < var1.length(); ++var2) {
            if(!this.IsArabic(var1.charAt(var2))) {
                int var3;
                for(var3 = 0; var2 + var3 < var1.length() && !this.IsArabic(var1.charAt(var2 + var3)); ++var3) {
                    ;
                }

                a(var1, var2, var3);
                var2 += var3 - 1;
            }
        }

    }

    public String Compile(String var1) {
        if(var1 == null) {
            return "";
        } else {
            this.Comiler();
            StringBuilder var4 = new StringBuilder(var1);
            int var5 = 0;

            for(int var6 = 0; var5 < var1.length(); ++var6) {
                char var7;
                int var8;
                if((var8 = (var7 = var1.charAt(var5)) - ((UnicodeChr)this.f.get(0)).get_charCode()) >= 0 && var8 < this.f.size()) {
                    label116: {
                        boolean var3 = var5 != var1.length() - 1 && (this.a(var1.charAt(var5 + 1)) || this.b(var1.charAt(var5 + 1)));
                        boolean var2;
                        if(var5 == 0) {
                            var2 = false;
                        } else {
                            var2 = this.a(var1.charAt(var5 - 1));
                        }

                        if(var3 && var7 == g) {
                            var7 = var1.charAt(var5 + 1);

                            int var9;
                            for(var9 = 0; var9 < this.c.size(); ++var9) {
                                if(var7 == ((UnicodeChr)this.c.get(var9)).get_charCode()) {
                                    var7 = var2?(char)((UnicodeChr)this.c.get(var9)).get_mid():(char)((UnicodeChr)this.c.get(var9)).get_init();
                                    var4.insert(var6, var7);
                                    var4.deleteCharAt(var6 + 1);
                                    ++var5;
                                    break;
                                }
                            }

                            if(var9 < this.c.size()) {
                                break label116;
                            }
                        }

                        if(var3 && var2) {
                            var4.deleteCharAt(var6);
                            var4.insert(var6, (char)((UnicodeChr)this.f.get(var8)).get_mid());
                        } else if(var2 && !var3) {
                            var4.insert(var6, (char)((UnicodeChr)this.f.get(var8)).get_end());
                            var4.deleteCharAt(var6 + 1);
                        } else if(!var2 && var3) {
                            var4.deleteCharAt(var6);
                            var4.insert(var6, (char)((UnicodeChr)this.f.get(var8)).get_init());
                        } else if(!var2 && !var3) {
                            var4.deleteCharAt(var6);
                            var4.insert(var6, (char)((UnicodeChr)this.f.get(var8)).get_iso());
                        }

                        if(var4.charAt(var6) == 0) {
                            var4.deleteCharAt(var6);
                        }
                    }
                } else if(this.IsArabic(var7)) {
                    return var1;
                }

                ++var5;
            }

            this.ArabicReverse(var4);
            return var4.toString();
        }
    }

    private void a(int[] var1, int var2, int var3) {
        do {
            int var4 = var2;
            int var5 = var3;
            int var6 = var1[var2 + var3 >> 1];

            do {
                while(var1[var4] < var6) {
                    ++var4;
                }

                while(var6 < var1[var5]) {
                    --var5;
                }

                if(var4 > var5) {
                    break;
                }

                if(var4 < var5) {
                    int var7 = var1[var4];
                    var1[var4] = var1[var5];
                    var1[var5] = var7;
                }

                ++var4;
                --var5;
            } while(var4 <= var5);

            if(var5 - var2 <= var3 - var4) {
                if(var2 < var5) {
                    this.a(var1, var2, var5);
                }

                var2 = var4;
            } else {
                if(var4 < var3) {
                    this.a(var1, var4, var3);
                }

                var3 = var5;
            }
        } while(var2 < var3);

    }

    private static int a(int[] var0, int var1) {
        int var3;
        if(var0.equals(a)) {
            var3 = b;
        } else {
            var3 = 0;
        }

        int var2 = var0.length - 1;

        while(var3 <= var2) {
            int var4 = var3 + var2 >> 1;
            int var5;
            if((var5 = var0[var4]) == var1) {
                return var4;
            }

            if(var5 < var1) {
                var3 = var4 + 1;
            } else {
                var2 = var4 - 1;
            }
        }

        return ~var3;
    }
}
